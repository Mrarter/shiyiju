<template>
  <div>
    <div class="page-header">
      <div>
        <div class="page-title">系统设置</div>
        <div class="page-subtitle">管理后台账号、角色权限和基础配置项</div>
      </div>
      <div class="header-actions">
        <el-button @click="goToOperations">
          <el-icon><Promotion /></el-icon> 运营管理
        </el-button>
        <el-button type="primary" @click="openCreateAccount">新增账号</el-button>
      </div>
    </div>

    <div class="section-card" style="padding: 24px;">
      <el-tabs>
        <el-tab-pane label="账号管理">
          <el-table :data="accounts">
            <el-table-column prop="name" label="账号" min-width="160" />
            <el-table-column prop="role" label="角色" width="140" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="lastLogin" label="最近登录" width="180" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEditAccount(row)">编辑</el-button>
                <el-button link @click="resetPassword(row)">重置密码</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="角色权限">
          <div class="toolbar" style="margin-bottom: 16px;">
            <el-input v-model="roleKeyword" placeholder="搜索角色名" style="max-width: 260px;" />
            <el-button type="primary" @click="openCreateRole">新增角色</el-button>
          </div>
          <el-table :data="filteredRoles">
            <el-table-column prop="name" label="角色名" min-width="160" />
            <el-table-column prop="scopeText" label="权限范围" min-width="320" />
            <el-table-column prop="memberCount" label="账号数" width="100" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEditRole(row)">编辑权限</el-button>
                <el-button link @click="toggleRoleStatus(row)">{{ row.status === '启用' ? '停用' : '启用' }}</el-button>
                <el-button link type="danger" @click="removeRole(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="配置项">
          <div class="toolbar" style="margin-bottom: 16px;">
            <el-input v-model="configKeyword" placeholder="搜索配置键/说明" style="max-width: 260px;" />
            <el-button type="primary" @click="openCreateConfig">新增配置</el-button>
          </div>
          <el-table :data="filteredConfigs">
            <el-table-column prop="group" label="分组" width="120" />
            <el-table-column prop="key" label="配置键" min-width="220" />
            <el-table-column prop="value" label="配置值" min-width="220" show-overflow-tooltip />
            <el-table-column prop="remark" label="说明" min-width="220" show-overflow-tooltip />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEditConfig(row)">编辑</el-button>
                <el-button link @click="duplicateConfig(row)">复制</el-button>
                <el-button link type="danger" @click="removeConfig(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="accountDialogVisible" :title="editingAccountIndex === null ? '新增账号' : '编辑账号'" width="520px">
      <div style="display: grid; gap: 16px;">
        <el-input v-model="accountForm.name" placeholder="账号名" />
        <el-select v-model="accountForm.role" placeholder="角色">
          <el-option v-for="role in roles" :key="role.name" :label="role.name" :value="role.name" />
        </el-select>
        <el-select v-model="accountForm.status" placeholder="状态">
          <el-option label="启用" value="启用" />
          <el-option label="停用" value="停用" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="accountDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAccount">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" :title="editingRoleIndex === null ? '新增角色' : '编辑角色'" width="680px">
      <div style="display: grid; gap: 16px;">
        <el-input v-model="roleForm.name" placeholder="角色名" />
        <el-select v-model="roleForm.status" placeholder="状态">
          <el-option label="启用" value="启用" />
          <el-option label="停用" value="停用" />
        </el-select>
        <div>
          <div class="page-subtitle" style="margin-bottom: 8px;">页面权限</div>
          <el-checkbox-group v-model="roleForm.scopes">
            <el-checkbox v-for="option in permissionOptions" :key="option" :label="option">{{ option }}</el-checkbox>
          </el-checkbox-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="configDialogVisible" :title="editingConfigIndex === null ? '新增配置' : '编辑配置'" width="640px">
      <div style="display: grid; gap: 16px;">
        <div class="form-grid">
          <el-input v-model="configForm.group" placeholder="分组" />
          <el-input v-model="configForm.key" placeholder="配置键" />
        </div>
        <el-input v-model="configForm.value" type="textarea" :rows="4" placeholder="配置值" />
        <el-input v-model="configForm.remark" placeholder="说明" />
      </div>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Promotion } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '../../stores/admin'

const permissionOptions = ['控制台', '首页运营', '艺术家管理', '作品管理', '用户管理', '订单管理', '系统设置']

const adminStore = useAdminStore()
const router = useRouter()
const {
  adminAccounts: accountsState,
  adminRoles: rolesState,
  systemConfigs: configsState
} = storeToRefs(adminStore)

const roleKeyword = ref('')
const configKeyword = ref('')

const accounts = computed(() => accountsState.value)
const roles = computed(() => rolesState.value)
const configs = computed(() => configsState.value)

const accountDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const configDialogVisible = ref(false)

const editingAccountIndex = ref(null)
const editingRoleIndex = ref(null)
const editingConfigIndex = ref(null)

const accountForm = reactive({
  name: '',
  role: '运营人员',
  status: '启用'
})

const roleForm = reactive({
  name: '',
  scopes: [],
  status: '启用'
})

const configForm = reactive({
  group: 'default',
  key: '',
  value: '',
  remark: ''
})

const filteredRoles = computed(() => {
  const q = roleKeyword.value.trim()
  return roles.value.filter((item) => !q || item.name.includes(q))
})

const filteredConfigs = computed(() => {
  const q = configKeyword.value.trim()
  return configs.value.filter((item) => !q || item.key.includes(q) || (item.remark || '').includes(q))
})

function resetAccountForm() {
  accountForm.name = ''
  accountForm.role = roles.value[0]?.name || '运营人员'
  accountForm.status = '启用'
}

function resetRoleForm() {
  roleForm.name = ''
  roleForm.scopes = ['控制台']
  roleForm.status = '启用'
}

function resetConfigForm() {
  configForm.group = 'default'
  configForm.key = ''
  configForm.value = ''
  configForm.remark = ''
}

function goToOperations() {
  router.push('/operations')
}

function openCreateAccount() {
  editingAccountIndex.value = null
  resetAccountForm()
  accountDialogVisible.value = true
}

function openEditAccount(row) {
  editingAccountIndex.value = row.id
  accountForm.name = row.name
  accountForm.role = row.role
  accountForm.status = row.status
  accountDialogVisible.value = true
}

function saveAccount() {
  if (!accountForm.name.trim()) {
    ElMessage.warning('请输入账号名')
    return
  }
  adminStore.saveAdminAccount(editingAccountIndex.value, {
    name: accountForm.name.trim(),
    role: accountForm.role,
    status: accountForm.status
  }).then(() => {
    ElMessage.success(editingAccountIndex.value === null ? '账号已新增' : '账号已更新')
    accountDialogVisible.value = false
  }).catch((error) => {
    ElMessage.error(error.message || '账号保存失败')
  })
}

function resetPassword(row) {
  ElMessage.success(`已为 ${row.name} 生成临时密码：Shiyiju@2026`)
}

function openCreateRole() {
  editingRoleIndex.value = null
  resetRoleForm()
  roleDialogVisible.value = true
}

function openEditRole(row) {
  editingRoleIndex.value = row.id
  roleForm.name = row.name
  roleForm.scopes = [...row.scopes]
  roleForm.status = row.status
  roleDialogVisible.value = true
}

function saveRole() {
  if (!roleForm.name.trim()) {
    ElMessage.warning('请输入角色名')
    return
  }
  adminStore.saveAdminRole(editingRoleIndex.value, {
    name: roleForm.name.trim(),
    scopes: [...roleForm.scopes],
    status: roleForm.status
  }).then(() => {
    ElMessage.success(editingRoleIndex.value === null ? '角色已新增' : '角色权限已更新')
    roleDialogVisible.value = false
  }).catch((error) => {
    ElMessage.error(error.message || '角色保存失败')
  })
}

async function removeRole(row) {
  if (row.memberCount > 0) {
    ElMessage.warning('该角色仍有账号绑定，不能删除')
    return
  }
  await ElMessageBox.confirm(`确定删除角色“${row.name}”吗？`, '删除角色', { type: 'warning' })
  await adminStore.deleteAdminRole(row.id)
  ElMessage.success('角色已删除')
}

function toggleRoleStatus(row) {
  adminStore.saveAdminRole(row.id, {
    name: row.name,
    scopes: row.scopes,
    status: row.status === '启用' ? '停用' : '启用'
  }).then(() => {
    ElMessage.success(`角色已${row.status === '启用' ? '停用' : '启用'}`)
  }).catch((error) => {
    ElMessage.error(error.message || '角色状态更新失败')
  })
}

function openCreateConfig() {
  editingConfigIndex.value = null
  resetConfigForm()
  configDialogVisible.value = true
}

function openEditConfig(row) {
  editingConfigIndex.value = row.id
  configForm.group = row.group
  configForm.key = row.key
  configForm.value = row.value
  configForm.remark = row.remark
  configDialogVisible.value = true
}

function saveConfig() {
  if (!configForm.key.trim()) {
    ElMessage.warning('请输入配置键')
    return
  }
  adminStore.saveSystemConfig(editingConfigIndex.value, {
    group: configForm.group.trim() || 'default',
    key: configForm.key.trim(),
    value: configForm.value,
    remark: configForm.remark.trim()
  }).then(() => {
    ElMessage.success(editingConfigIndex.value === null ? '配置项已新增' : '配置项已更新')
    configDialogVisible.value = false
  }).catch((error) => {
    ElMessage.error(error.message || '配置项保存失败')
  })
}

function duplicateConfig(row) {
  adminStore.duplicateSystemConfig(row.id).then(() => {
    ElMessage.success('配置项已复制')
  }).catch((error) => {
    ElMessage.error(error.message || '配置项复制失败')
  })
}

async function removeConfig(row) {
  await ElMessageBox.confirm(`确定删除配置“${row.key}”吗？`, '删除配置', { type: 'warning' })
  await adminStore.deleteSystemConfig(row.id)
  ElMessage.success('配置项已删除')
}

onMounted(async () => {
  if (!accountsState.value.length) await adminStore.loadAdminAccounts()
  if (!rolesState.value.length) await adminStore.loadAdminRoles()
  if (!configsState.value.length) await adminStore.loadSystemConfigs()
})
</script>
