<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">用户管理</div>
        <div class="page-subtitle">查看微信登录用户资料与最近活跃情况</div>
      </div>
      <div class="header-actions">
        <el-button @click="goToOperations">
          <el-icon><Promotion /></el-icon> 运营管理
        </el-button>
        <el-button @click="exportUsers">导出用户</el-button>
      </div>
    </div>

    <div class="section-card" style="padding: 20px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索昵称/用户编号" style="max-width: 260px;" />
        <el-select v-model="statusFilter" placeholder="状态" style="width: 140px;">
          <el-option label="全部" value="all" />
          <el-option label="正常" value="正常" />
          <el-option label="禁用" value="禁用" />
        </el-select>
      </div>
      <el-table :data="filteredUsers">
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar v-if="row.avatarUrl" :src="getImageUrl(row.avatarUrl)" :size="40" style="cursor: pointer;" />
            <el-avatar v-else :size="40">{{ (row.nickname || 'U').slice(0, 1) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="140" />
        <el-table-column prop="userNo" label="用户编号" min-width="160" />
        <el-table-column prop="gender" label="性别" width="100" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="lastLogin" label="最近登录时间" width="180" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewUser(row)">查看详情</el-button>
            <el-button link :type="row.status === '禁用' ? 'success' : 'danger'" @click="toggleUserStatus(row)">
              {{ row.status === '禁用' ? '启用' : '禁用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer v-model="detailVisible" title="用户详情" size="420px">
      <div v-if="activeUser" style="display: grid; gap: 14px;">
        <div style="text-align: center;">
          <el-avatar v-if="activeUser.avatarUrl" :src="getImageUrl(activeUser.avatarUrl)" :size="80" />
          <el-avatar v-else :size="80">{{ (activeUser.nickname || 'U').slice(0, 1) }}</el-avatar>
        </div>
        <div><strong>昵称：</strong>{{ activeUser.nickname }}</div>
        <div><strong>用户编号：</strong>{{ activeUser.userNo }}</div>
        <div><strong>性别：</strong>{{ activeUser.gender }}</div>
        <div><strong>状态：</strong>{{ activeUser.status }}</div>
        <div><strong>最近登录：</strong>{{ activeUser.lastLogin }}</div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage } from 'element-plus'
import { Promotion } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '../../stores/admin'
import { downloadCsv } from '../../utils/download'
import { getImageUrl } from '../../utils/imageUrl'

const adminStore = useAdminStore()
const router = useRouter()
const { users: usersState } = storeToRefs(adminStore)
const keyword = ref('')
const statusFilter = ref('all')
const detailVisible = ref(false)
const activeUser = ref(null)
const users = computed(() => usersState.value)
const filteredUsers = computed(() => {
  const q = keyword.value.trim()
  return users.value.filter((item) => {
    const matchKeyword = !q || (item.nickname || '').includes(q) || (item.userNo || '').includes(q)
    const matchStatus = statusFilter.value === 'all' || item.status === statusFilter.value
    return matchKeyword && matchStatus
  })
})

function goToOperations() {
  router.push('/operations')
}

function exportUsers() {
  downloadCsv(
    'shiyiju-users.csv',
    ['昵称', '用户编号', '性别', '状态', '最近登录时间'],
    filteredUsers.value.map((item) => [item.nickname, item.userNo, item.gender, item.status, item.lastLogin])
  )
  ElMessage.success('用户列表已导出')
}

function viewUser(row) {
  activeUser.value = row
  detailVisible.value = true
}

function toggleUserStatus(row) {
  adminStore.changeUserStatus(row.id, row.status === '禁用' ? 'ENABLED' : 'DISABLED')
    .then(() => {
      ElMessage.success(`用户已${row.status === '禁用' ? '启用' : '禁用'}`)
    })
    .catch((error) => {
      ElMessage.error(error.message || '状态更新失败')
    })
}

onMounted(async () => {
  if (!usersState.value.length) await adminStore.loadUsers()
})
</script>
