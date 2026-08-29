#!/usr/bin/env bash
# 每次环境启动时执行：确保 MySQL 已运行并就绪，供后端 terminals 连接。
# 该脚本需可重复执行，且在 MySQL 已运行时不报错。
set -euo pipefail

echo "==> 启动 MySQL"
sudo service mysql start || true

echo "==> 等待 MySQL 就绪"
for _ in $(seq 1 30); do
  if sudo mysqladmin ping >/dev/null 2>&1; then
    echo "    MySQL 已就绪"
    exit 0
  fi
  sleep 1
done

echo "MySQL 未在预期时间内就绪" >&2
exit 1
