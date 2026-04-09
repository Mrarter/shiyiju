import { API_BASE_URL } from '../config/env'

async function request(path, options = {}) {
  const token = localStorage.getItem('shiyiju_admin_token')
  const isFormData = options.body instanceof FormData
  const response = await fetch(`${API_BASE_URL}${path}`, {
    mode: 'cors',
    credentials: 'omit',
    headers: {
      ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
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
    return request(path, { method: 'POST', body: body instanceof FormData ? body : JSON.stringify(body) })
  },
  put(path, body) {
    return request(path, { method: 'PUT', body: body instanceof FormData ? body : JSON.stringify(body) })
  },
  delete(path) {
    return request(path, { method: 'DELETE' })
  },
  upload(path, file) {
    const formData = new FormData()
    formData.append('file', file)
    return request(path, { method: 'POST', body: formData })
  },
  uploadWithParams(path, file, params = {}) {
    const formData = new FormData()
    formData.append('file', file)
    // 添加裁剪参数
    for (const [key, value] of Object.entries(params)) {
      if (value !== undefined && value !== null && value !== '') {
        formData.append(key, String(value))
      }
    }
    return request(path, { method: 'POST', body: formData })
  }
}
