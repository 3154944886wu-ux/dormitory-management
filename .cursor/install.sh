#!/usr/bin/env bash
# 幂等的环境初始化脚本：安装系统依赖、初始化数据库、准备后端本地配置与前后端依赖缓存。
# 该脚本在环境构建阶段执行一次（生成基线快照），因此必须可重复运行。
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

echo "==> 安装系统依赖（maven、mysql-server）"
sudo apt-get update
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends maven mysql-server

echo "==> 启动 MySQL 以便初始化"
sudo service mysql start
for _ in $(seq 1 30); do
  if sudo mysqladmin ping >/dev/null 2>&1; then break; fi
  sleep 1
done

echo "==> 配置 root 账号（本地开发：mysql_native_password + 空密码，与 application.yml 默认一致）"
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY ''; FLUSH PRIVILEGES;"

echo "==> 创建数据库并按 MIGRATIONS.md 黄金路径导入（仅当 students 表不存在时）"
mysql -uroot -h 127.0.0.1 -e "CREATE DATABASE IF NOT EXISTS dormitory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
HAS_TABLE="$(mysql -uroot -h 127.0.0.1 -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='dormitory' AND table_name='students';")"
if [ "$HAS_TABLE" = "0" ]; then
  # schema.sql 已是包含全部迁移的超集，故只需导入 schema + 测试数据 + 访客补充脚本。
  mysql -uroot -h 127.0.0.1 dormitory < database/schema.sql
  mysql -uroot -h 127.0.0.1 dormitory < database/test_data.sql
  mysql -uroot -h 127.0.0.1 dormitory < database/visitors.sql
  echo "    数据库初始化完成"
else
  echo "    students 表已存在，跳过数据导入"
fi

echo "==> 生成后端本地配置 application-local.yml（已被 .gitignore 忽略，不会提交）"
LOCAL_YML="backend/src/main/resources/application-local.yml"
if [ ! -f "$LOCAL_YML" ]; then
  JWT_SECRET="$(head -c 48 /dev/urandom | base64 | tr -dc 'A-Za-z0-9' | head -c 48)"
  cat > "$LOCAL_YML" <<EOF
# 由 .cursor/install.sh 自动生成，仅用于本地/云端开发环境。
spring:
  datasource:
    username: root
    password: ""
jwt:
  secret: ${JWT_SECRET}
  expiration: 604800000
app:
  init-admin:
    enabled: true
EOF
  echo "    已生成 $LOCAL_YML"
else
  echo "    $LOCAL_YML 已存在，保留现有配置"
fi

echo "==> 预热后端 Maven 依赖"
(cd backend && mvn -q -B -DskipTests dependency:go-offline) || echo "    依赖预热出现告警（可忽略）"

echo "==> 安装前端依赖"
(cd frontend && npm ci)

echo "==> 环境初始化完成"
