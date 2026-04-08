import { API_BASE_URL } from '../config/env'

async function request(path, options = {}) {
  const token = localStorage.getItem('shiyiju_admin_token')
  const response = await fetch(`${API_BASE_URL}${path}`, {
    mode: 'cors',
    credentials: 'omit',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {})
    },
    ...options
  })

  let json = null
  try {
    json = await response.json()
  } catch (error) {
    throw new Error('服务返回异常，未读取到有效响应')
  }

  if (!response.ok) {
    throw new Error(json?.message || `请求失败（HTTP ${response.status}）`)
  }

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
