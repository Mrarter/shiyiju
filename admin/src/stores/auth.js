import { defineStore } from 'pinia'
import { http } from '../api/http'
import { ADMIN_API_PREFIX } from '../config/env'

const TOKEN_KEY = 'shiyiju_admin_token'
const PROFILE_KEY = 'shiyiju_admin_profile'

function readProfile() {
  try {
    return JSON.parse(localStorage.getItem(PROFILE_KEY) || '{}')
  } catch {
    return {}
  }
}

function applyProfile(user, profile) {
  if (!user) return null
  return {
    ...user,
    displayName: profile.displayName || user.displayName
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: null,
    profile: readProfile()
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
    setUser(user) {
      this.user = applyProfile(user, this.profile)
    },
    saveProfile(payload) {
      this.profile = {
        ...this.profile,
        ...payload
      }
      localStorage.setItem(PROFILE_KEY, JSON.stringify(this.profile))
      if (this.user) {
        this.user = applyProfile(this.user, this.profile)
      }
    },
    async login(payload) {
      const result = await http.post(`${ADMIN_API_PREFIX}/auth/login`, payload)
      this.setToken(result.token)
      this.setUser(result.user)
      return result
    },
    async fetchMe() {
      if (!this.token) return null
      const result = await http.get(`${ADMIN_API_PREFIX}/auth/me`)
      this.setUser(result)
      return result
    }
  }
})
