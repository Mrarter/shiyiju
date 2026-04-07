import { ADMIN_API_PREFIX, API_PREFIX } from '../config/env'
import { http } from './http'
import { artists, artworks, operationItems, orders, users, dashboardMetrics } from '../mock/data'

const useMock = import.meta.env.VITE_USE_MOCK !== 'false'

export async function getDashboard() {
  if (useMock) {
    return {
      metrics: dashboardMetrics,
      todos: [
        '补齐首页推荐与艺术家展示数据',
        '修正首页空数据误提示',
        '补齐订单后台处理动作'
      ]
    }
  }
  return http.get(`${ADMIN_API_PREFIX}/dashboard`)
}

export async function getOperationConfigs() {
  if (useMock) return operationItems
  return http.get(`${ADMIN_API_PREFIX}/operations`)
}

export async function getArtists() {
  if (useMock) return artists
  return http.get(`${ADMIN_API_PREFIX}/artists`)
}

export async function getArtworks() {
  if (useMock) return artworks
  return http.get(`${ADMIN_API_PREFIX}/artworks`)
}

export async function getUsers() {
  if (useMock) return users
  return http.get(`${ADMIN_API_PREFIX}/users`)
}

export async function getOrders() {
  if (useMock) return orders
  return http.get(`${ADMIN_API_PREFIX}/orders`)
}

export async function getCurrentMiniappCapabilities() {
  if (useMock) {
    return {
      artistsDetailApi: `${API_PREFIX}/artists/{id}`,
      artistRecommendApi: `${API_PREFIX}/artists/recommend`,
      artworkListApi: `${API_PREFIX}/works`,
      artworkDetailApi: `${API_PREFIX}/works/{id}`,
      userMeApi: `${API_PREFIX}/users/me`,
      orderDetailApi: `${API_PREFIX}/orders/{id}`
    }
  }
  return {}
}
