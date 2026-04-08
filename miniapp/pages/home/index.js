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
    badge: index < 3 ? ["TOP 1", "TOP 2", "TOP 3"][index] : "推荐"
  }
}

function normalizeArtist(item, index) {
  return {
    id: item.artistId,
    artistName: item.artistName,
    levelName: item.levelName || "艺术家",
    slogan: item.slogan || "持续上新中",
    avatar: item.avatar || "",
    avatarFallback: (item.artistName || "艺术家").slice(0, 1),
    rankText: `0${index + 1}`.slice(-2)
  }
}

function buildEmptyState() {
  return {
    loading: false,
    error: "",
    allWorks: [],
    featuredWorks: [],
    hotWorks: [],
    risingWorks: [],
    recommendedArtists: [],
    artistPool: [],
    artistVisibleCount: 4,
    allArtistsLoaded: true,
    hasAnyContent: false
  }
}

Page({
  data: {
    loading: true,
    error: "",
    allWorks: [],
    featuredWorks: [],
    hotWorks: [],
    risingWorks: [],
    recommendedArtists: [],
    artistPool: [],
    artistVisibleCount: 4,
    artistStep: 4,
    allArtistsLoaded: false,
    hasAnyContent: false
  },

  onShow() {
    this.loadHome()
  },

  onReachBottom() {
    this.loadMoreArtists()
  },

  async loadHome() {
    this.setData({ loading: true, error: "" })
    try {
      const [works, artists] = await Promise.all([
        api.request({ url: "/works", method: "GET" }),
        api.request({ url: "/artists/recommend?limit=20", method: "GET" })
      ])

      const workPool = (works || []).map(normalizeWork)
      const artistPool = (artists || []).map(normalizeArtist)
      const hasAnyContent = workPool.length > 0 || artistPool.length > 0

      if (!hasAnyContent) {
        this.setData(buildEmptyState())
        return
      }

      this.setData({
        loading: false,
        error: "",
        allWorks: workPool,
        artistPool,
        featuredWorks: workPool.slice(0, 4),
        hotWorks: workPool.slice(0, 6),
        risingWorks: workPool.slice(0, 3),
        recommendedArtists: artistPool.slice(0, 4),
        artistVisibleCount: 4,
        allArtistsLoaded: artistPool.length <= 4,
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

  loadMoreArtists() {
    if (this.data.allArtistsLoaded) return
    const nextCount = this.data.artistVisibleCount + this.data.artistStep
    const recommendedArtists = this.data.artistPool.slice(0, nextCount)
    this.setData({
      recommendedArtists,
      artistVisibleCount: nextCount,
      allArtistsLoaded: recommendedArtists.length >= this.data.artistPool.length
    })
  },

  goArtworkDetail(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    if (!artworkId) return
    wx.navigateTo({ url: `/pages/artwork/detail?id=${artworkId}` })
  },

  goArtistProfile(event) {
    const artistId = event.currentTarget.dataset.artistId
    if (!artistId) return
    wx.navigateTo({ url: `/pages/artist/profile?id=${artistId}` })
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
