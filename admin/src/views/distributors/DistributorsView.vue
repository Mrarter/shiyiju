<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">艺荐官管理</div>
        <div class="page-subtitle">管理分销员、艺荐官及其团队关系</div>
      </div>
      <div class="header-actions">
        <el-button @click="goToOperations">
          <el-icon><Promotion /></el-icon> 运营管理
        </el-button>
        <el-button type="primary" @click="startCreate">新建艺荐官</el-button>
      </div>
    </div>

    <div class="section-card" style="padding: 20px; margin-bottom: 16px;">
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索姓名/艺荐官名称" style="max-width: 260px;" />
        <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width: 140px;">
          <el-option label="全部" value="" />
          <el-option label="正常" value="ACTIVE" />
          <el-option label="禁用" value="INACTIVE" />
        </el-select>
      </div>
      <el-table :data="filteredDistributors" style="margin-top: 16px;">
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar v-if="row.avatarUrl" :src="getImageUrl(row.avatarUrl)" :size="40" class="clickable-avatar" @click="showDetail(row)" />
            <span v-else class="text-muted">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户昵称" min-width="120" />
        <el-table-column prop="displayName" label="艺荐官名称" min-width="140" />
        <el-table-column prop="teamLevelName" label="等级" width="120" />
        <el-table-column prop="directCount" label="直属下级" width="100" />
        <el-table-column prop="teamCount" label="团队人数" width="100" />
        <el-table-column prop="totalCommission" label="累计佣金" width="120" />
        <el-table-column prop="availableCommission" label="可提现" width="120" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'" size="small">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="startEdit(row)">编辑</el-button>
            <el-button link :type="row.status === 'ACTIVE' ? 'danger' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新建/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑艺荐官' : '新建艺荐官'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="选择用户" v-if="!editingId">
          <el-select v-model="form.userId" placeholder="请选择用户" filterable style="width: 100%;">
            <el-option v-for="user in availableUsers" :key="user.id" :label="user.nickname" :value="user.id">
              <span>{{ user.nickname }}</span>
              <span class="text-muted" style="margin-left: 8px;">{{ user.mobile }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="艺荐官名称" required>
          <el-input v-model="form.displayName" placeholder="对外展示的艺荐官名称" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.bio" type="textarea" :rows="3" placeholder="艺荐官个人简介" />
        </el-form-item>
        <el-form-item label="团队等级">
          <el-select v-model="form.teamLevel" placeholder="请选择等级" style="width: 100%;">
            <el-option :value="1" label="艺荐官" />
            <el-option :value="2" label="高级艺荐官" />
            <el-option :value="3" label="资深艺荐官" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级艺荐官">
          <el-select v-model="form.parentDistributorId" placeholder="可选填" clearable style="width: 100%;">
            <el-option v-for="d in distributors" :key="d.id" :label="d.displayName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="ACTIVE">正常</el-radio>
            <el-radio value="INACTIVE">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="艺荐官详情" width="700px" :close-on-click-modal="true">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="用户昵称">{{ currentRow.nickname }}</el-descriptions-item>
        <el-descriptions-item label="艺荐官名称">{{ currentRow.displayName }}</el-descriptions-item>
        <el-descriptions-item label="团队等级">{{ currentRow.teamLevelName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRow.status === 'ACTIVE' ? 'success' : 'danger'" size="small">
            {{ currentRow.statusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="直属上级">
          <span v-if="currentRow.parentDistributorId">
            {{ currentRow.parentDistributorId }} - {{ currentRow.parentDistributorName || '未知' }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="累计佣金">{{ currentRow.totalCommission }}</el-descriptions-item>
        <el-descriptions-item label="邀请码" :span="2">{{ currentRow.invitationCode }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ currentRow.bio || '-' }}</el-descriptions-item>
        <el-descriptions-item label="直属下级" :span="2">
          <div class="member-list" v-if="currentRow.directCount > 0">
            <div class="member-count clickable-text" @click="toggleDirectMembers">
              {{ currentRow.directCount }} 人
              <el-icon :class="{ 'is-expand': showDirectMembers }"><ArrowRight /></el-icon>
            </div>
            <div v-if="showDirectMembers" class="member-detail">
              <div v-for="member in directMembers" :key="member.id" class="member-item">
                {{ member.id }} - {{ member.displayName }}
              </div>
            </div>
          </div>
          <span v-else>0 人</span>
        </el-descriptions-item>
        <el-descriptions-item label="团队人数" :span="2">
          <div class="member-list" v-if="currentRow.teamCount > 0">
            <div class="member-count clickable-text" @click="toggleTeamMembers">
              {{ currentRow.teamCount }} 人
              <el-icon :class="{ 'is-expand': showTeamMembers }"><ArrowRight /></el-icon>
            </div>
            <div v-if="showTeamMembers" class="member-detail">
              <div v-for="member in teamMembers" :key="member.id" class="member-item">
                {{ member.id }} - {{ member.displayName }}
              </div>
            </div>
          </div>
          <span v-else>0 人</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRow.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="可提现">{{ currentRow.availableCommission }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight, Promotion } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { http } from '../../api/http'
import { ADMIN_API_PREFIX } from '../../config/env'
import { getImageUrl } from '../../utils/imageUrl'

const router = useRouter()
const distributors = ref([])
const keyword = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const detailVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const currentRow = ref(null)
const availableUsers = ref([])
const showDirectMembers = ref(false)
const showTeamMembers = ref(false)
const directMembers = ref([])
const teamMembers = ref([])

const form = reactive({
  userId: null,
  displayName: '',
  bio: '',
  teamLevel: 1,
  parentDistributorId: null,
  status: 'ACTIVE'
})

const filteredDistributors = computed(() => {
  let list = distributors.value
  if (keyword.value.trim()) {
    const q = keyword.value.trim().toLowerCase()
    list = list.filter(item =>
      (item.nickname || '').toLowerCase().includes(q) ||
      (item.displayName || '').toLowerCase().includes(q)
    )
  }
  if (statusFilter.value) {
    list = list.filter(item => item.status === statusFilter.value)
  }
  return list
})

function resetForm() {
  editingId.value = null
  form.userId = null
  form.displayName = ''
  form.bio = ''
  form.teamLevel = 1
  form.parentDistributorId = null
  form.status = 'ACTIVE'
}

function goToOperations() {
  router.push('/operations')
}

function startCreate() {
  resetForm()
  dialogVisible.value = true
}

function startEdit(row) {
  editingId.value = row.id
  form.displayName = row.displayName || ''
  form.bio = row.bio || ''
  form.teamLevel = row.teamLevel || 1
  form.parentDistributorId = row.parentDistributorId
  form.status = row.status || 'ACTIVE'
  dialogVisible.value = true
}

function showDetail(row) {
  currentRow.value = row
  showDirectMembers.value = false
  showTeamMembers.value = false
  directMembers.value = []
  teamMembers.value = []
  detailVisible.value = true
}

function toggleDirectMembers() {
  if (showDirectMembers.value) {
    showDirectMembers.value = false
    return
  }
  // 获取直属下级（同一 parentDistributorId 的艺荐官）
  const parentId = currentRow.value?.id
  directMembers.value = distributors.value.filter(d => d.parentDistributorId === parentId)
  showDirectMembers.value = true
  showTeamMembers.value = false
}

function toggleTeamMembers() {
  if (showTeamMembers.value) {
    showTeamMembers.value = false
    return
  }
  // 获取团队成员（所有层级）
  const parentId = currentRow.value?.id
  teamMembers.value = getAllTeamMembers(parentId)
  showTeamMembers.value = true
  showDirectMembers.value = false
}

function getAllTeamMembers(parentId) {
  const result = []
  const children = distributors.value.filter(d => d.parentDistributorId === parentId)
  for (const child of children) {
    result.push(child)
    result.push(...getAllTeamMembers(child.id))
  }
  return result
}

async function submitForm() {
  if (!form.displayName.trim()) {
    ElMessage.warning('请输入艺荐官名称')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await http.put(`${ADMIN_API_PREFIX}/distributors/${editingId.value}`, form)
      ElMessage.success('艺荐官已更新')
    } else {
      if (!form.userId) {
        ElMessage.warning('请选择用户')
        return
      }
      await http.post(`${ADMIN_API_PREFIX}/distributors`, form)
      ElMessage.success('艺荐官已创建')
    }
    dialogVisible.value = false
    await loadDistributors()
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  try {
    await http.put(`${ADMIN_API_PREFIX}/distributors/${row.id}/status`, { status: newStatus })
    ElMessage.success(newStatus === 'ACTIVE' ? '艺荐官已启用' : '艺荐官已禁用')
    await loadDistributors()
  } catch (error) {
    ElMessage.error(error.message || '状态更新失败')
  }
}

async function loadDistributors() {
  try {
    distributors.value = await http.get(`${ADMIN_API_PREFIX}/distributors`)
  } catch (error) {
    console.error('加载艺荐官列表失败', error)
  }
}

async function loadUsers() {
  try {
    availableUsers.value = await http.get(`${ADMIN_API_PREFIX}/users`)
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

onMounted(async () => {
  await Promise.all([loadDistributors(), loadUsers()])
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
}
.text-muted {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
.clickable-avatar {
  cursor: pointer;
  transition: opacity 0.2s;
}
.clickable-avatar:hover {
  opacity: 0.8;
}
.clickable-text {
  cursor: pointer;
  color: var(--el-color-primary);
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.clickable-text:hover {
  text-decoration: underline;
}
.member-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.member-count {
  font-weight: 500;
}
.member-count .el-icon {
  transition: transform 0.2s;
}
.member-count .el-icon.is-expand {
  transform: rotate(90deg);
}
.member-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 12px;
  background: var(--el-fill-color-light);
  border-radius: 6px;
  margin-top: 4px;
}
.member-item {
  font-size: 13px;
  color: var(--el-text-color-regular);
}
</style>
