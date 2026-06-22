import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  
  // 管理员路由
  {
    path: '/admin',
    component: () => import('../views/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['admin'] },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '工作台', roles: ['admin'] }
      },
      {
        path: 'buildings',
        name: 'AdminBuildings',
        component: () => import('../views/BuildingManagement.vue'),
        meta: { title: '楼栋管理', roles: ['admin'] }
      },
      {
        path: 'rooms',
        name: 'AdminRooms',
        component: () => import('../views/RoomManagement.vue'),
        meta: { title: '房间管理', roles: ['admin'] }
      },
      {
        path: 'students',
        name: 'AdminStudents',
        component: () => import('../views/StudentManagement.vue'),
        meta: { title: '学生管理', roles: ['admin'] }
      },
      {
        path: 'visitors',
        name: 'AdminVisitors',
        component: () => import('../views/VisitorManagement.vue'),
        meta: { title: '访客登记', roles: ['admin'] }
      },
      {
        path: 'repairs',
        name: 'AdminRepairs',
        component: () => import('../views/RepairManagement.vue'),
        meta: { title: '报修管理', roles: ['admin'] }
      },
      {
        path: 'utility-fees',
        name: 'AdminUtilityFees',
        component: () => import('../views/UtilityFeeManagement.vue'),
        meta: { title: '水电费管理', roles: ['admin'] }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('../views/AnnouncementManagement.vue'),
        meta: { title: '公告管理', roles: ['admin'] }
      },
      {
        path: 'check-rules',
        name: 'AdminCheckRules',
        component: () => import('../views/CheckRule.vue'),
        meta: { title: '打卡规则', roles: ['admin'] }
      },
      {
        path: 'check-records',
        name: 'AdminCheckRecords',
        component: () => import('../views/CheckInRecords.vue'),
        meta: { title: '打卡记录', roles: ['admin'] }
      },
      {
        path: 'check-exceptions',
        name: 'AdminCheckExceptions',
        component: () => import('../views/ManagerExceptions.vue'),
        meta: { title: '异常记录', roles: ['admin'] }
      },
      {
        path: 'check-statistics',
        name: 'AdminCheckStatistics',
        component: () => import('../views/ManagerStatistics.vue'),
        meta: { title: '归寝统计', roles: ['admin'] }
      },
      {
        path: 'leave-approval',
        name: 'AdminLeaveApproval',
        component: () => import('../views/LeaveApproval.vue'),
        meta: { title: '请假审批', roles: ['admin'] }
      },
      {
        path: 'inspection',
        redirect: '/admin/inspection/plans',
        meta: { title: '安全卫生检查' },
        children: [
          {
            path: 'plans',
            name: 'AdminInspectionPlans',
            component: () => import('../views/InspectionPlanList.vue'),
            meta: { title: '检查计划', roles: ['admin'] }
          },
          {
            path: 'records',
            name: 'AdminInspectionRecords',
            component: () => import('../views/InspectionRecordList.vue'),
            meta: { title: '检查记录', roles: ['admin'] }
          },
          {
            path: 'execute',
            name: 'AdminInspectionExecute',
            component: () => import('../views/InspectionExecute.vue'),
            meta: { title: '执行检查', roles: ['admin'] }
          },
          {
            path: 'items',
            name: 'AdminInspectionItems',
            component: () => import('../views/InspectionItemList.vue'),
            meta: { title: '检查项目', roles: ['admin'] }
          },
          {
            path: 'statistics',
            name: 'AdminInspectionStatistics',
            component: () => import('../views/InspectionStatistics.vue'),
            meta: { title: '统计分析', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'college-major',
        name: 'AdminCollegeMajor',
        component: () => import('../views/CollegeMajorManagement.vue'),
        meta: { title: '学院专业管理', roles: ['admin'] }
      },
      {
        path: 'questionnaires',
        name: 'AdminQuestionnaires',
        component: () => import('../views/QuestionnaireManagement.vue'),
        meta: { title: '问卷管理', roles: ['admin'] }
      },
      {
        path: 'dorm-batches',
        name: 'AdminDormBatches',
        component: () => import('../views/DormBatchManagement.vue'),
        meta: { title: '批次管理', roles: ['admin'] }
      },
      {
        path: 'batch-rooms',
        name: 'AdminBatchRooms',
        component: () => import('../views/BatchRoomAllocation.vue'),
        meta: { title: '房源划拨', roles: ['admin'] }
      },
      {
        path: 'allocation-result',
        name: 'AdminAllocationResult',
        component: () => import('../views/AllocationResultView.vue'),
        meta: { title: '分配结果', roles: ['admin'] }
      },
      {
        path: 'allocation-report',
        name: 'AdminAllocationReport',
        component: () => import('../views/AllocationReport.vue'),
        meta: { title: '分配报表', roles: ['admin'] }
      },
      {
        path: 'allocation-statistics',
        name: 'AdminAllocationStatistics',
        component: () => import('../views/AllocationStatistics.vue'),
        meta: { title: '统计分析', roles: ['admin'] }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人信息', roles: ['admin'] }
      },
      {
        path: 'teachers',
        name: 'AdminTeachers',
        component: () => import('../views/TeacherManagement.vue'),
        meta: { title: '教师管理', roles: ['admin'] }
      },
      {
        path: 'audit-logs',
        name: 'AdminAuditLogs',
        component: () => import('../views/AuditLog.vue'),
        meta: { title: '审计日志', roles: ['admin'] }
      }
    ]
  },
  
  // manager 路由
  {
    path: '/manager',
    component: () => import('../views/ManagerLayout.vue'),
    meta: { requiresAuth: true, roles: ['manager'] },
    children: [
      {
        path: '',
        redirect: '/manager/exceptions'
      },
      {
        path: 'exceptions',
        name: 'ManagerExceptions',
        component: () => import('../views/ManagerExceptions.vue'),
        meta: { title: '异常处理', roles: ['manager'] }
      },
      {
        path: 'check-records',
        name: 'ManagerCheckRecords',
        component: () => import('../views/ManagerCheckRecords.vue'),
        meta: { title: '归寝记录', roles: ['manager'] }
      },
      {
        path: 'statistics',
        name: 'ManagerStatistics',
        component: () => import('../views/ManagerStatistics.vue'),
        meta: { title: '统计分析', roles: ['manager'] }
      },
      {
        path: 'profile',
        name: 'ManagerProfile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人信息', roles: ['manager'] }
      }
    ]
  },

  // 学生路由
  {
    path: '/student',
    component: () => import('../views/StudentLayout.vue'),
    meta: { requiresAuth: true, roles: ['student'] },
    children: [
      {
        path: '',
        redirect: '/student/home'
      },
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('../views/StudentHome.vue'),
        meta: { title: '我的首页', roles: ['student'] }
      },
      {
        path: 'check-in',
        name: 'StudentCheckIn',
        component: () => import('../views/CheckIn.vue'),
        meta: { title: '归寝打卡', roles: ['student'] }
      },
      {
        path: 'my-records',
        name: 'StudentMyRecords',
        component: () => import('../views/StudentCheckRecords.vue'),
        meta: { title: '我的打卡记录', roles: ['student'] }
      },
      {
        path: 'leave-request',
        name: 'StudentLeaveRequest',
        component: () => import('../views/LeaveRequest.vue'),
        meta: { title: '请假申请', roles: ['student'] }
      },
      {
        path: 'my-leaves',
        name: 'StudentMyLeaves',
        component: () => import('../views/StudentLeaveRecords.vue'),
        meta: { title: '我的请假记录', roles: ['student'] }
      },
      {
        path: 'my-room',
        name: 'StudentMyRoom',
        component: () => import('../views/StudentRoom.vue'),
        meta: { title: '我的宿舍', roles: ['student'] }
      },
      {
        path: 'repairs',
        name: 'StudentRepairs',
        component: () => import('../views/StudentRepairs.vue'),
        meta: { title: '报修申请', roles: ['student'] }
      },
      {
        path: 'announcements',
        name: 'StudentAnnouncements',
        component: () => import('../views/StudentAnnouncements.vue'),
        meta: { title: '公告通知', roles: ['student'] }
      },
      {
        path: 'fees',
        name: 'StudentFees',
        component: () => import('../views/StudentFees.vue'),
        meta: { title: '水电费用', roles: ['student'] }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人信息', roles: ['student'] }
      },
    ]
  },
  
  // 根路径重定向
  {
    path: '/',
    redirect: '/login'
  },
  
  // 兼容旧路由（重定向到新路由）
  {
    path: '/dashboard',
    redirect: '/admin/dashboard'
  },
  {
    path: '/buildings',
    redirect: '/admin/buildings'
  },
  {
    path: '/rooms',
    redirect: '/admin/rooms'
  },
  {
    path: '/students',
    redirect: '/admin/students'
  },
  {
    path: '/check-in',
    redirect: '/student/check-in'
  },
  {
    path: '/leave-request',
    redirect: '/student/leave-request'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 宿舍管理系统` : '宿舍管理系统'
  
  const token = localStorage.getItem('token')
  let userRole = 'student'
  
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    userRole = (user.role || 'student').toLowerCase()
  } catch (e) {
    console.error('解析用户信息失败:', e)
  }
  
  // 需要认证的路由 - 未登录跳转登录页
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }
  
  // 已登录访问登录页/注册页 - 重定向到首页
  if ((to.path === '/login' || to.path === '/register') && token) {
    if (userRole === 'admin') {
      return next('/admin/dashboard')
    } else if (userRole === 'manager') {
      return next('/manager/exceptions')
    } else {
      return next('/student/home')
    }
  }
  
  // 角色权限检查 - 只检查需要特定角色的路由
  if (to.meta.roles && to.meta.roles.length > 0) {
    if (!to.meta.roles.includes(userRole)) {
      // 访问了不属于自己角色的页面
      if (userRole === 'admin') {
        return next('/admin/dashboard')
      } else if (userRole === 'manager') {
        return next('/manager/exceptions')
      } else {
        return next('/student/home')
      }
    }
  }
  
  // 放行
  next()
})

export default router