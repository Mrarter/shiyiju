const api = require("./utils/api")

App({
  globalData: {
    apiBaseUrl: "http://192.168.1.163:8082/api/v1",
    token: wx.getStorageSync("token") || ""
  },

  updateApiBaseUrl(apiBaseUrl) {
    this.globalData.apiBaseUrl = apiBaseUrl
  },

  onLaunch() {
    api.setApp(this)
  }
})
