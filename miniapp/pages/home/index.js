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

function formatCategory(category) {
  const map = {
    PAINTING: "绘画",
    PRINT: "版画",
    INK: "水墨",
    SCULPTURE: "雕塑",
    OTHER: "精选"
  }
  return map[category] || "精选"
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
    categoryText: formatCategory(item.category),
    coverUrl: item.coverUrl || "",
    priceText: formatPrice(item.currentPrice),
    statusText: formatSaleStatus(item.saleStatus),
    coverFallback: (item.title || "作品").slice(0, 2),
    coverStyle: buildCoverStyle(index),
    viewCountText: `${item.viewCount || 0} 浏览`,
    favoriteCountText: `${item.favoriteCount || 0} 收藏`
  }
}

function normalizeArtist(item, index) {
  const styles = buildCoverStyle(index)
  return {
    id: item.artistId,
    name: item.artistName,
    levelName: item.levelName || "签约艺术家",
    slogan: item.slogan || "持续创作中",
    avatarUrl: item.avatar || item.avatarUrl || "",
    badgeStyle: styles
  }
}

function buildEmptyState() {
  return {
    loading: false,
    error: "",
    allWorks: [],
    onlineWorks: [],
    featuredWork: null,
    hotWorks: [],
    risingWorks: [],
    recommendedArtists: [],
    bannerItems: [],
    headlineMetrics: [],
    hasAnyContent: false
  }
}

Page({
  data: {
    loading: true,
    error: "",
    allWorks: [],
    onlineWorks: [],
    featuredWork: null,
    hotWorks: [],
    risingWorks: [],
    recommendedArtists: [],
    bannerItems: [],
    headlineMetrics: [],
    hasAnyContent: false
  },

  onShow() {
    this.loadHome()
  },

  async loadHome() {
    this.setData({ loading: true, error: "" })
    try {
      const [works, artists] = await Promise.all([
        api.request({ url: "/works", method: "GET" }),
        api.request({ url: "/artists/recommend?limit=3", method: "GET" })
      ])
      const workPool = (works || []).map(normalizeWork)
      const artistPool = (artists || []).map(normalizeArtist)
      const hasAnyContent = workPool.length > 0

      if (!hasAnyContent) {
        this.setData(buildEmptyState())
        return
      }

      this.setData({
        loading: false,
        error: "",
        allWorks: workPool,
        onlineWorks: workPool,
        featuredWork: workPool[0] || null,
        hotWorks: workPool.slice(0, 4),
        risingWorks: workPool.slice(1, 4),
        recommendedArtists: artistPool,
        bannerItems: workPool.slice(0, 3).map((item, index) => ({
          id: item.id,
          eyebrow: index === 0 ? "主推" : index === 1 ? "热卖" : "上新",
          title: item.title,
          subtitle: `${item.artistName} · ${item.categoryText}`,
          priceText: item.priceText
        })),
        headlineMetrics: [
          { label: "在线作品", value: `${workPool.length}` },
          { label: "签约艺术家", value: `${new Set(workPool.map((item) => item.artistName)).size}` },
          { label: "本周推荐", value: `${Math.min(workPool.length, 3)}` }
        ],
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

  goArtistProfile(event) {
    const artistId = event.currentTarget.dataset.artistId
    if (!artistId) return
    wx.navigateTo({ url: `/pages/artist/profile?id=${artistId}` })
  },

  goPublish() {
    wx.switchTab({ url: "/pages/publish/index" })
  },

  handleRetry() {
    this.loadHome()
  }
})
