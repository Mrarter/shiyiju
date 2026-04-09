const api = require("./utils/api")

App({
  globalData: {
    // 开发环境使用 HTTPS
    apiBaseUrl: "https://localhost:8443/api/v1",
    // 生产环境示例（根据实际服务器配置）
    // apiBaseUrl: "https://your-server.com/api/v1",
    token: wx.getStorageSync("token") || ""
  },

  updateApiBaseUrl(apiBaseUrl) {
    this.globalData.apiBaseUrl = apiBaseUrl
  },

  onLaunch() {
    api.setApp(this)
  }
})
