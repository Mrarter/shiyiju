import { ADMIN_API_PREFIX, API_PREFIX } from '../config/env'
import { http } from './http'
import { artists, artworks, operationItems, orders, users, dashboardMetrics } from '../mock/data'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'

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

export async function createOperationConfig(payload) {
  if (useMock) return { id: Date.now(), ...payload }
  return http.post(`${ADMIN_API_PREFIX}/operations`, payload)
}

export async function updateOperationConfig(id, payload) {
  if (useMock) return { id, ...payload }
  return http.put(`${ADMIN_API_PREFIX}/operations/${id}`, payload)
}

export async function deleteOperationConfig(id) {
  if (useMock) return { success: true }
  return http.delete(`${ADMIN_API_PREFIX}/operations/${id}`)
}

export async function uploadAdminImage(file) {
  if (useMock) return { url: URL.createObjectURL(file), name: file.name }
  return http.upload(`${ADMIN_API_PREFIX}/uploads/images`, file)
}

/**
 * 上传图片（支持裁剪）
 * @param {File} file - 图片文件
 * @param {Object} cropParams - 裁剪参数 { cropX, cropY, cropW, cropH, scale }
 */
export async function uploadAdminImageWithCrop(file, cropParams = {}) {
  if (useMock) return { url: URL.createObjectURL(file), name: file.name }
  return http.uploadWithParams(`${ADMIN_API_PREFIX}/uploads/images/crop`, file, cropParams)
}

export async function updateArtworkStatus(id, status) {
  if (useMock) return { success: true }
  return http.put(`${ADMIN_API_PREFIX}/artworks/${id}/status`, { status })
}

export async function updateArtistStatus(id, status) {
  if (useMock) return { success: true }
  return http.put(`${ADMIN_API_PREFIX}/artists/${id}/status`, { status })
}

export async function createArtist(payload) {
  if (useMock) return { id: Date.now(), ...payload }
  return http.post(`${ADMIN_API_PREFIX}/artists`, payload)
}

export async function updateArtist(id, payload) {
  if (useMock) return { id, ...payload }
  return http.put(`${ADMIN_API_PREFIX}/artists/${id}`, payload)
}

export async function updateOrderShipment(id, payload) {
  if (useMock) return { success: true }
  return http.put(`${ADMIN_API_PREFIX}/orders/${id}/shipment`, payload)
}

export async function updateOrderRemark(id, payload) {
  if (useMock) return { success: true }
  return http.put(`${ADMIN_API_PREFIX}/orders/${id}/remark`, payload)
}

export async function getArtists() {
  if (useMock) return artists
  return http.get(`${ADMIN_API_PREFIX}/artists`)
}

export async function getArtworks() {
  if (useMock) return artworks
  return http.get(`${ADMIN_API_PREFIX}/artworks`)
}

export async function createArtwork(payload) {
  if (useMock) {
    const newArtwork = { id: Date.now(), ...payload }
    artworks.push(newArtwork)
    return newArtwork
  }
  return http.post(`${ADMIN_API_PREFIX}/artworks`, payload)
}

export async function updateArtwork(id, payload) {
  if (useMock) {
    const index = artworks.findIndex(a => a.id === id)
    if (index !== -1) {
      artworks[index] = { ...artworks[index], ...payload }
      return artworks[index]
    }
    return { id, ...payload }
  }
  return http.put(`${ADMIN_API_PREFIX}/artworks/${id}`, payload)
}

export async function getUsers() {
  if (useMock) return users
  return http.get(`${ADMIN_API_PREFIX}/users`)
}

export async function updateUserStatus(id, status) {
  if (useMock) return { success: true }
  return http.put(`${ADMIN_API_PREFIX}/users/${id}/status`, { status })
}

export async function getOrders() {
  if (useMock) return orders
  return http.get(`${ADMIN_API_PREFIX}/orders`)
}

export async function getCurrentMiniappCapabilities() {
  return {
    artistsDetailApi: `${API_PREFIX}/artists/{id}`,
    artistRecommendApi: `${API_PREFIX}/artists/recommend`,
    artworkListApi: `${API_PREFIX}/works`,
    artworkDetailApi: `${API_PREFIX}/works/{id}`,
    userMeApi: `${API_PREFIX}/users/me`,
    orderDetailApi: `${API_PREFIX}/orders/{id}`
  }
}

export async function getAdminAccounts() {
  if (useMock) return []
  return http.get(`${ADMIN_API_PREFIX}/settings/accounts`)
}

export async function createAdminAccount(payload) {
  if (useMock) return { id: Date.now(), ...payload }
  return http.post(`${ADMIN_API_PREFIX}/settings/accounts`, payload)
}

export async function updateAdminAccount(id, payload) {
  if (useMock) return { id, ...payload }
  return http.put(`${ADMIN_API_PREFIX}/settings/accounts/${id}`, payload)
}

export async function getAdminRoles() {
  if (useMock) return []
  return http.get(`${ADMIN_API_PREFIX}/settings/roles`)
}

export async function createAdminRole(payload) {
  if (useMock) return { id: Date.now(), ...payload }
  return http.post(`${ADMIN_API_PREFIX}/settings/roles`, payload)
}

export async function updateAdminRole(id, payload) {
  if (useMock) return { id, ...payload }
  return http.put(`${ADMIN_API_PREFIX}/settings/roles/${id}`, payload)
}

export async function deleteAdminRole(id) {
  if (useMock) return { success: true }
  return http.delete(`${ADMIN_API_PREFIX}/settings/roles/${id}`)
}

export async function getSystemConfigs() {
  if (useMock) return []
  return http.get(`${ADMIN_API_PREFIX}/settings/configs`)
}

export async function createSystemConfig(payload) {
  if (useMock) return { id: Date.now(), ...payload }
  return http.post(`${ADMIN_API_PREFIX}/settings/configs`, payload)
}

export async function updateSystemConfig(id, payload) {
  if (useMock) return { id, ...payload }
  return http.put(`${ADMIN_API_PREFIX}/settings/configs/${id}`, payload)
}

export async function duplicateSystemConfig(id) {
  if (useMock) return { success: true }
  return http.post(`${ADMIN_API_PREFIX}/settings/configs/${id}/duplicate`, {})
}

export async function deleteSystemConfig(id) {
  if (useMock) return { success: true }
  return http.delete(`${ADMIN_API_PREFIX}/settings/configs/${id}`)
}
