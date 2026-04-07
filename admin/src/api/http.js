import { API_BASE_URL } from '../config/env'

async function request(path, options = {}) {
  const token = localStorage.getItem('shiyiju_admin_token')
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    },
    ...options
  })

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }

  const json = await response.json()
  if (json?.code && json.code !== 0) {
    throw new Error(json.message || '请求失败')
  }
  return json?.data ?? json
}

export const http = {
  get(path) {
    return request(path)
  },
  post(path, body) {
    return request(path, { method: 'POST', body: JSON.stringify(body) })
  },
  put(path, body) {
    return request(path, { method: 'PUT', body: JSON.stringify(body) })
  }
}
