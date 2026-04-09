import { defineStore } from 'pinia'
import {
  createArtist,
  createArtwork,
  createAdminAccount as createAdminAccountApi,
  createAdminRole as createAdminRoleApi,
  createOperationConfig,
  createSystemConfig as createSystemConfigApi,
  deleteAdminRole as deleteAdminRoleApi,
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
      this.artists = await getArtists()
    },
    async loadArtworks() {
      this.artworks = await getArtworks()
    },
    async loadUsers() {
      this.users = await getUsers()
    },
    async changeUserStatus(id, status) {
      await updateUserStatus(id, status)
      await this.loadUsers()
    },
    async loadOrders() {
      this.orders = await getOrders()
    },
    async loadAdminAccounts() {
      this.adminAccounts = await getAdminAccountsApi()
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
      this.adminRoles = await getAdminRolesApi()
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
      this.systemConfigs = await getSystemConfigsApi()
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
      await this.loadOperations()
    },
    async updateOperation(id, payload) {
      await updateOperationConfig(id, payload)
      await this.loadOperations()
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
      if (id) {
        await updateArtist(id, payload)
      } else {
        await createArtist(payload)
      }
      await this.loadArtists()
    },
    async saveArtwork(id, payload) {
      if (id) {
        await updateArtwork(id, payload)
      } else {
        await createArtwork(payload)
      }
      await this.loadArtworks()
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
