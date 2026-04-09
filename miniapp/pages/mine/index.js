const api = require("../../utils/api")

function mapRole(role) {
  const map = {
    COLLECTOR: "收藏家",
    ARTIST: "艺术家",
    DISTRIBUTOR: "经纪人"
  }
  return map[role] || "收藏家"
}

function buildAvatarPlaceholder(name) {
  const title = (name || "拾艺").slice(0, 2)
  return {
    title,
    background: "linear-gradient(135deg, #4a3f39 0%, #241d19 48%, #171210 100%)",
    glow: "#d9b287",
    mountain: "#8c5e49"
  }
}

Page({
  data: {
    loading: true,
    error: "",
    user: null,
    profileSummary: null,
    assets: [],
    tradeMenus: [],
    featureCards: [],
    quickLinks: [],
    serviceLinks: []
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
        profileSummary: this.buildProfileSummary(user),
        assets: this.buildAssets(user),
        tradeMenus: this.buildTradeMenus(),
        featureCards: this.buildFeatureCards(),
        quickLinks: this.buildQuickLinks(),
        serviceLinks: this.buildServiceLinks(user),
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

  buildProfileSummary(user) {
    return {
      name: user.nickname || "微信用户",
      roleText: mapRole(user.defaultRole),
      profileText: "个人主页",
      follows: 3,
      fans: 3,
      medals: 0,
      creditScore: 100,
      authTitle: "在拾艺局出售作品",
      authDesc: "完成卖家认证后即可发布与流通作品",
      avatarUrl: user.avatarUrl || "",
      avatarPlaceholder: buildAvatarPlaceholder(user.nickname)
    }
  },

  buildAssets(user) {
    return [
      { label: "炭粒", value: 0 },
      { label: "优惠券", value: 0 },
      { label: "标记", value: (user.permissions || []).length },
      { label: "购物车", value: 0 }
    ]
  },

  buildTradeMenus() {
    return [
      { key: "bought", icon: "¥", label: "我买到的" },
      { key: "sold", icon: "袋", label: "我卖出的" },
      { key: "afterSale", icon: "售", label: "退货售后" },
      { key: "review", icon: "赞", label: "我的评价" }
    ]
  },

  buildFeatureCards() {
    return [
      {
        key: "collection",
        title: "我的藏品",
        desc: "查看你已收藏与持有的作品",
        icon: "藏",
        badge: false
      },
      {
        key: "certificate",
        title: "收藏证书",
        desc: "查看你已持有作品的收藏认证",
        imageTitle: "艺术品收藏证书"
      }
    ]
  },

  buildQuickLinks() {
    return [
      { key: "history", icon: "迹", label: "浏览记录" },
      { key: "likes", icon: "赞", label: "我赞" },
      { key: "medal", icon: "勋", label: "勋章馆" }
    ]
  },

  buildServiceLinks(user) {
    return [
      { key: "wallet", icon: "¥", label: "钱包" },
      { key: "settings", icon: "设", label: "设置" },
      { key: "invoice", icon: "票", label: "发票中心" },
      { key: "level", icon: "阶", label: "藏家等级" },
      { key: "feedback", icon: "服", label: "意见反馈" },
      { key: "follow", icon: "关", label: "关注公众号" },
      { key: "role", icon: mapRole(user.defaultRole).slice(0, 1), label: mapRole(user.defaultRole) }
    ]
  },

  handleTradeTap(event) {
    const key = event.currentTarget.dataset.key
    if (key === "bought") {
      wx.showToast({ title: "订单中心整理中", icon: "none" })
      return
    }
    if (key === "sold") {
      wx.switchTab({ url: "/pages/publish/index" })
      return
    }
    wx.showToast({ title: "功能正在补齐", icon: "none" })
  },

  handleFeatureTap(event) {
    const key = event.currentTarget.dataset.key
    if (key === "collection") {
      wx.navigateTo({ url: "/pages/collection/index" })
      return
    }
    if (key === "certificate") {
      wx.navigateTo({ url: "/pages/collection/index" })
      return
    }
    wx.showToast({ title: "功能开发中", icon: "none" })
  },

  handleQuickLinkTap(event) {
    const key = event.currentTarget.dataset.key
    if (key === "history") {
      wx.showToast({ title: "浏览记录整理中", icon: "none" })
      return
    }
    if (key === "likes") {
      wx.navigateTo({ url: "/pages/collection/index" })
      return
    }
    wx.showToast({ title: "敬请期待", icon: "none" })
  },

  handleServiceTap(event) {
    const key = event.currentTarget.dataset.key
    if (key === "settings") {
      wx.showToast({ title: "设置页开发中", icon: "none" })
      return
    }
    if (key === "feedback") {
      wx.showToast({ title: "意见反馈已记录", icon: "none" })
      return
    }
    wx.showToast({ title: "该入口稍后开放", icon: "none" })
  },

  handleAuthTap() {
    wx.showToast({ title: "卖家认证流程整理中", icon: "none" })
  },

  handleRetry() {
    this.loadProfile()
  }
})
