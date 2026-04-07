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

function request(options) {
  const app = getAppInstance()
  const token = getToken()
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${app.globalData.apiBaseUrl}${options.url}`,
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
        reject(new Error(body.message || "请求失败"))
      },
      fail(error) {
        reject(error)
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
