import { defineStore } from 'pinia'
import {
  createOperationConfig,
  getArtists,
  getArtworks,
  getDashboard,
  getOperationConfigs,
  getOrders,
  getUsers,
  getCurrentMiniappCapabilities,
  updateArtworkStatus,
  updateOperationConfig
} from '../api/admin'

export const useAdminStore = defineStore('admin', {
  state: () => ({
    dashboard: null,
    operations: [],
    artists: [],
    artworks: [],
    users: [],
    orders: [],
    capabilities: null
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
    async loadOrders() {
      this.orders = await getOrders()
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
    }
  }
})
