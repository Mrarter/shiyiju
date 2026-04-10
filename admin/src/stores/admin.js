import { defineStore } from 'pinia'
import {
  createArtist,
  createArtwork,
  createAdminAccount as createAdminAccountApi,
  createAdminRole as createAdminRoleApi,
  createOperationConfig,
  createSystemConfig as createSystemConfigApi,
  deleteAdminRole as deleteAdminRoleApi,
  deleteOperationConfig as deleteOperationConfigApi,
  deleteSystemConfig as deleteSystemConfigApi,
  getArtists,
  getAdminAccounts as getAdminAccountsApi,
  getAdminRoles as getAdminRolesApi,
  getArtworks,
  getDashboard,
  getOperationConfigs,
  getOrders,
  getSystemConfigs as getSystemConfigsApi,
  getUsers,
  getCurrentMiniappCapabilities,
  updateArtistStatus,
  updateArtist,
  updateArtworkStatus,
  updateArtwork,
  updateAdminAccount as updateAdminAccountApi,
  updateAdminRole as updateAdminRoleApi,
  updateOperationConfig,
  updateOrderRemark,
  updateOrderShipment,
  updateSystemConfig as updateSystemConfigApi,
  updateUserStatus,
  duplicateSystemConfig as duplicateSystemConfigApi
} from '../api/admin'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    dashboard: null,
    operations: [],
    artists: [],
    artworks: [],
    users: [],
    orders: [],
    capabilities: null,
    adminAccounts: [],
    adminRoles: [],
    systemConfigs: []
  }),
  actions: {
    async loadDashboard() {
      this.dashboard = await getDashboard()
    },
    async loadOperations() {
      this.operations = await getOperationConfigs()
    },
    async loadArtists() {
      const newArtists = await getArtists()
      this.artists.splice(0, this.artists.length, ...newArtists)
    },
    async loadArtworks() {
      const newArtworks = await getArtworks()
      this.artworks.splice(0, this.artworks.length, ...newArtworks)
    },
    async loadUsers() {
      const newUsers = await getUsers()
      this.users.splice(0, this.users.length, ...newUsers)
    },
    async changeUserStatus(id, status) {
      await updateUserStatus(id, status)
      await this.loadUsers()
    },
    async loadOrders() {
      const newOrders = await getOrders()
      this.orders.splice(0, this.orders.length, ...newOrders)
    },
    async loadAdminAccounts() {
      const newAccounts = await getAdminAccountsApi()
      this.adminAccounts.splice(0, this.adminAccounts.length, ...newAccounts)
    },
    async saveAdminAccount(id, payload) {
      if (id) {
        await updateAdminAccountApi(id, payload)
      } else {
        await createAdminAccountApi(payload)
      }
      await this.loadAdminAccounts()
      await this.loadAdminRoles()
    },
    async loadAdminRoles() {
      const newRoles = await getAdminRolesApi()
      this.adminRoles.splice(0, this.adminRoles.length, ...newRoles)
    },
    async saveAdminRole(id, payload) {
      if (id) {
        await updateAdminRoleApi(id, payload)
      } else {
        await createAdminRoleApi(payload)
      }
      await this.loadAdminRoles()
      await this.loadAdminAccounts()
    },
    async deleteAdminRole(id) {
      await deleteAdminRoleApi(id)
      await this.loadAdminRoles()
      await this.loadAdminAccounts()
    },
    async loadSystemConfigs() {
      const newConfigs = await getSystemConfigsApi()
      this.systemConfigs.splice(0, this.systemConfigs.length, ...newConfigs)
    },
    async saveSystemConfig(id, payload) {
      if (id) {
        await updateSystemConfigApi(id, payload)
      } else {
        await createSystemConfigApi(payload)
      }
      await this.loadSystemConfigs()
    },
    async duplicateSystemConfig(id) {
      await duplicateSystemConfigApi(id)
      await this.loadSystemConfigs()
    },
    async deleteSystemConfig(id) {
      await deleteSystemConfigApi(id)
      await this.loadSystemConfigs()
    },
    async loadCapabilities() {
      this.capabilities = await getCurrentMiniappCapabilities()
    },
    async createOperation(payload) {
      await createOperationConfig(payload)
      const newOps = await getOperationConfigs()
      this.operations.splice(0, this.operations.length, ...newOps)
    },
    async updateOperation(id, payload) {
      await updateOperationConfig(id, payload)
      const newOps = await getOperationConfigs()
      this.operations.splice(0, this.operations.length, ...newOps)
    },
    async deleteOperation(id) {
      await deleteOperationConfigApi(id)
      const newOps = await getOperationConfigs()
      this.operations.splice(0, this.operations.length, ...newOps)
    },
    async changeArtworkStatus(id, status) {
      await updateArtworkStatus(id, status)
      await this.loadArtworks()
    },
    async changeArtistStatus(id, status) {
      await updateArtistStatus(id, status)
      await this.loadArtists()
    },
    async saveArtist(id, payload) {
      let result
      if (id) {
        result = await updateArtist(id, payload)
        // 直接更新本地数据，避免依赖后端重新查询
        if (result) {
          const index = this.artists.findIndex(a => a.id === id)
          if (index !== -1) {
            this.artists.splice(index, 1, result)
          }
        }
      } else {
        result = await createArtist(payload)
        await this.loadArtists()
      }
      return result
    },
    async saveArtwork(id, payload) {
      let result
      if (id) {
        result = await updateArtwork(id, payload)
      } else {
        result = await createArtwork(payload)
      }
      await this.loadArtworks()
      return result
    },
    async saveOrderShipment(id, payload) {
      await updateOrderShipment(id, payload)
      await this.loadOrders()
    },
    async saveOrderRemark(id, payload) {
      await updateOrderRemark(id, payload)
      await this.loadOrders()
    }
  }
})
