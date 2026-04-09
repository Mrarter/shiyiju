const api = require("../../utils/api")

Page({
  data: {
    loading: true,
    error: "",
    currentTab: 0,
    tabs: ["全部", "持有中", "拼团", "转售中", "已完成"],
    user: null,
    cards: []
  },

  onShow() {
    this.loadCollectionState()
  },

  async loadCollectionState() {
    this.setData({ loading: true, error: "" })
    try {
      let user = null
      try {
        user = await api.request({ url: "/users/me", method: "GET" })
      } catch (_) {
        user = null
      }
      this.setData({
        user,
        cards: [],
        loading: false,
        error: ""
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "藏品页加载失败"
      })
    }
  },

  handleTabTap(event) {
    this.setData({ currentTab: Number(event.currentTarget.dataset.index || 0) })
  },

  handleRetry() {
    this.loadCollectionState()
  },

  goBack() {
    wx.navigateBack({ fail: () => wx.switchTab({ url: '/pages/home/index' }) })
  }
})
