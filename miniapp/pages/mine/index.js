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
    const actionMap = {
      collection: "藏品页仍在开发中，下一步会优先补齐。",
      orders: "订单页仍在开发中，当前可通过后台查看订单状态。",
      service: "客服入口仍在开发中，可先通过后台备注协同处理。"
    }
    wx.showToast({
      title: actionMap[key] || "功能开发中",
      icon: "none"
    })
  },

  handleRetry() {
    this.loadProfile()
  }
})
