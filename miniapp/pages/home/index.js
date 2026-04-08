const api = require("../../utils/api")

function formatPrice(price) {
  if (price === null || price === undefined || price === "") return "价格待定"
  return `¥${price}`
}

function formatSaleStatus(status) {
  const map = {
    ON_SALE: "在售中",
    PUBLISHED: "在售中",
    COLLECTED: "已收藏",
    SOLD_OUT: "已售罄"
  }
  return map[status] || "持续更新"
}

function buildCoverStyle(index) {
  const presets = [
    {
      background: "linear-gradient(135deg, #8f6552 0%, #6f4c3f 45%, #3a2925 100%)",
      glow: "#d9b287",
      mountain: "#8c5e49"
    },
    {
      background: "linear-gradient(135deg, #7c665c 0%, #53433c 48%, #2d231f 100%)",
      glow: "#d9cec0",
      mountain: "#6d554b"
    },
    {
      background: "linear-gradient(135deg, #a0785c 0%, #755842 46%, #3f2e27 100%)",
      glow: "#e2c39e",
      mountain: "#8a694f"
    },
    {
      background: "linear-gradient(135deg, #6a5248 0%, #40322c 45%, #241b18 100%)",
      glow: "#c59b6c",
      mountain: "#5b473d"
    }
  ]
  return presets[index % presets.length]
}

function normalizeWork(item, index) {
  return {
    id: item.artworkId,
    title: item.title,
    artistName: item.artistName,
    artistLevelName: item.artistLevelName || "签约艺术家",
    coverUrl: item.coverUrl || "",
    priceText: formatPrice(item.currentPrice),
    statusText: formatSaleStatus(item.saleStatus),
    coverFallback: (item.title || "作品").slice(0, 2),
    coverStyle: buildCoverStyle(index)
  }
}

function buildEmptyState() {
  return {
    loading: false,
    error: "",
    allWorks: [],
    featuredWork: null,
    hotWorks: [],
    risingWorks: [],
    hasAnyContent: false
  }
}

Page({
  data: {
    loading: true,
    error: "",
    allWorks: [],
    featuredWork: null,
    hotWorks: [],
    risingWorks: [],
    hasAnyContent: false
  },

  onShow() {
    this.loadHome()
  },

  async loadHome() {
    this.setData({ loading: true, error: "" })
    try {
      const works = await api.request({ url: "/works", method: "GET" })
      const workPool = (works || []).map(normalizeWork)
      const hasAnyContent = workPool.length > 0

      if (!hasAnyContent) {
        this.setData(buildEmptyState())
        return
      }

      this.setData({
        loading: false,
        error: "",
        allWorks: workPool,
        featuredWork: workPool[0] || null,
        hotWorks: workPool.slice(0, 4),
        risingWorks: workPool.slice(0, 3),
        hasAnyContent
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "首页加载失败",
        hasAnyContent: false
      })
    }
  },

  goArtworkDetail(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    if (!artworkId) return
    wx.navigateTo({ url: `/pages/artwork/detail?id=${artworkId}` })
  },

  goDiscover() {
    wx.switchTab({ url: "/pages/discover/index" })
  },

  goPublish() {
    wx.switchTab({ url: "/pages/publish/index" })
  },

  handleRetry() {
    this.loadHome()
  }
})
