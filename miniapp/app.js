const api = require("./utils/api")

App({
  globalData: {
    // 开发环境使用本地地址
    apiBaseUrl: "http://localhost:8080",
    // 生产环境示例（根据实际服务器配置）
    // apiBaseUrl: "https://your-server.com",
    token: wx.getStorageSync("token") || ""
  },

  updateApiBaseUrl(apiBaseUrl) {
    this.globalData.apiBaseUrl = apiBaseUrl
  },

  onLaunch() {
    api.setApp(this)
  }
})
