const api = require("../../utils/api")

Page({
  data: {
    loading: true,
    error: "",
    user: null,
    stats: [],
    menus: [
      { key: "collection", label: "我的藏品", desc: "查看持有作品" },
      { key: "orders", label: "我的订单", desc: "查看订单与物流" },
      { key: "service", label: "联系客服", desc: "获取平台帮助" }
    ]
  },

  onShow() {
    this.loadProfile()
  },

  async loadProfile() {
    this.setData({ loading: true, error: "" })
    try {
      await api.ensureLogin()
      const user = await api.request({
        url: "/users/me",
        method: "GET"
      })
      this.setData({
        user,
        stats: this.buildStats(user),
        loading: false,
        error: ""
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "我的页面加载失败"
      })
    }
  },

  buildStats(user) {
    return [
      { label: "用户编号", value: user.userNo || "待生成" },
      { label: "默认角色", value: user.defaultRole || "COLLECTOR" },
      { label: "权限数", value: (user.permissions || []).length }
    ]
  },

  handleMenuTap(event) {
    const key = event.currentTarget.dataset.key
    if (key === 'collection') {
      wx.navigateTo({ url: '/pages/collection/index' })
      return
    }
    if (key === 'orders') {
      wx.showToast({ title: '订单页正在补齐中', icon: 'none' })
      return
    }
    wx.showToast({
      title: '客服入口仍在开发中',
      icon: 'none'
    })
  },

  handleRetry() {
    this.loadProfile()
  }
})
