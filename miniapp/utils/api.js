let appInstance = null

function setApp(app) {
  appInstance = app
}

function getAppInstance() {
  return appInstance || getApp()
}

function getToken() {
  const app = getAppInstance()
  const token = app.globalData.token || wx.getStorageSync("token") || ""
  if (token && token !== app.globalData.token) {
    app.globalData.token = token
  }
  return token
}

function saveToken(token) {
  const app = getAppInstance()
  app.globalData.token = token
  wx.setStorageSync("token", token)
}

function clearToken() {
  const app = getAppInstance()
  app.globalData.token = ""
  wx.removeStorageSync("token")
}

function normalizeRequestError(error, requestUrl) {
  const rawMessage = (error && (error.errMsg || error.message)) || "请求失败"
  if (rawMessage.includes("url not in domain list")) {
    return new Error(`请求被微信合法域名校验拦截，请在开发者工具关闭“不校验合法域名”，或把 ${requestUrl} 配到 request 合法域名`)
  }
  if (rawMessage.includes("timeout")) {
    return new Error(`请求超时，请确认后端服务可访问: ${requestUrl}`)
  }
  if (rawMessage.includes("fail")) {
    return new Error(`网络请求失败，请确认接口地址可访问: ${requestUrl}`)
  }
  return new Error(rawMessage)
}

function request(options) {
  const app = getAppInstance()
  const token = getToken()
  const requestUrl = `${app.globalData.apiBaseUrl}${options.url}`
  return new Promise((resolve, reject) => {
    wx.request({
      url: requestUrl,
      method: options.method || "GET",
      data: options.data,
      header: Object.assign({}, options.header, token ? { Authorization: `Bearer ${token}` } : {}),
      success(res) {
        const body = res.data || {}
        if (body.code === 0) {
          resolve(body.data)
          return
        }
        if (body.code === 40100) {
          clearToken()
        }
        reject(new Error(body.message || `请求失败: ${requestUrl}`))
      },
      fail(error) {
        reject(normalizeRequestError(error, requestUrl))
      }
    })
  })
}

function wxLogin() {
  return new Promise((resolve, reject) => {
    wx.login({
      success(res) {
        if (res.code) {
          resolve(res.code)
          return
        }
        reject(new Error("未获取到微信登录 code"))
      },
      fail(error) {
        reject(error)
      }
    })
  })
}

async function ensureLogin(profile) {
  const existingToken = getToken()
  if (existingToken) {
    return existingToken
  }

  try {
    const code = await wxLogin()
    const payload = Object.assign({ code }, profile || {})
    const loginData = await request({
      url: "/auth/wx/login",
      method: "POST",
      data: payload
    })
    saveToken(loginData.token)
    return loginData.token
  } catch (error) {
    const message = (error && error.message) || "微信登录服务调用失败"
    if (message.includes("微信登录服务调用失败") || message.includes("request:fail") || message.includes("timeout")) {
      const loginData = await request({
        url: "/auth/wx/login",
        method: "POST",
        data: Object.assign({ code: `mock-${Date.now()}` }, profile || {})
      })
      saveToken(loginData.token)
      return loginData.token
    }
    throw error
  }
}

module.exports = {
  setApp,
  request,
  ensureLogin,
  clearToken
}
