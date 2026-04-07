import { defineStore } from 'pinia'
import {
  getArtists,
  getArtworks,
  getDashboard,
  getOperationConfigs,
  getOrders,
  getUsers,
  getCurrentMiniappCapabilities
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
    }
  }
})
