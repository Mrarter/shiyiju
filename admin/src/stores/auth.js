import { defineStore } from 'pinia'
import { http } from '../api/http'
import { ADMIN_API_PREFIX } from '../config/env'

const TOKEN_KEY = 'shiyiju_admin_token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: null
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem(TOKEN_KEY, token)
    },
    clearAuth() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
    },
    async login(payload) {
      const result = await http.post(`${ADMIN_API_PREFIX}/auth/login`, payload)
      this.setToken(result.token)
      this.user = result.user
      return result
    },
    async fetchMe() {
      if (!this.token) return null
      const result = await http.get(`${ADMIN_API_PREFIX}/auth/me`)
      this.user = result
      return result
    }
  }
})
