<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">拾</div>
        <div>
          <div class="brand-title">拾艺局运营后台</div>
          <div class="brand-subtitle">Shiyiju Admin</div>
        </div>
      </div>
      <el-menu :default-active="$route.path" router class="menu">
        <el-menu-item index="/dashboard">控制台</el-menu-item>
        <el-menu-item index="/operations">首页运营</el-menu-item>
        <el-menu-item index="/artists">艺术家管理</el-menu-item>
        <el-menu-item index="/artworks">作品管理</el-menu-item>
        <el-menu-item index="/users">用户管理</el-menu-item>
        <el-menu-item index="/orders">订单管理</el-menu-item>
        <el-menu-item index="/settings">系统设置</el-menu-item>
      </el-menu>
    </aside>
    <div class="main-shell">
      <header class="topbar section-card">
        <div>
          <div class="topbar-title">{{ pageTitle }}</div>
          <div class="topbar-subtitle">支撑小程序内容运营、商品运营与订单处理</div>
        </div>
        <div class="topbar-actions">
          <el-button>消息</el-button>
          <el-dropdown>
            <el-button type="primary">管理员</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人设置</el-dropdown-item>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageTitle = computed(() => {
  const map = {
    '/dashboard': '控制台',
    '/operations': '首页运营',
    '/artists': '艺术家管理',
    '/artworks': '作品管理',
    '/users': '用户管理',
    '/orders': '订单管理',
    '/settings': '系统设置'
  }
  return map[route.path] || '拾艺局运营后台'
})
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 240px 1fr;
  background: #f6f7f9;
}

.sidebar {
  background: linear-gradient(180deg, #111111, #1d1d1d);
  color: #fff;
  padding: 24px 16px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 8px;
}

.brand-mark {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #c9a45c;
  color: #111;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.brand-title {
  font-weight: 700;
}

.brand-subtitle {
  color: rgba(255, 255, 255, 0.65);
  font-size: 12px;
  margin-top: 4px;
}

.menu {
  border-right: none;
  background: transparent;
}

:deep(.el-menu-item) {
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.78);
  margin-bottom: 8px;
}

:deep(.el-menu-item.is-active) {
  background: rgba(201, 164, 92, 0.2);
  color: #f4d79c;
}

.main-shell {
  padding: 20px;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
}

.topbar-title {
  font-size: 20px;
  font-weight: 700;
}

.topbar-subtitle {
  color: #646a73;
  font-size: 13px;
  margin-top: 4px;
}

.topbar-actions {
  display: flex;
  gap: 12px;
}

.content-area {
  margin-top: 20px;
}
</style>
