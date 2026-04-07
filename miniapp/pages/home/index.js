const api = require("../../utils/api")

Page({
  data: {
    loading: true,
    error: "",
    emptyState: false,
    hotWorks: [],
    risingWorks: [],
    recommendedArtists: []
  },

  onShow() {
    this.loadHomeData()
  },

  async loadHomeData() {
    this.setData({ loading: true, error: "", emptyState: false })
    try {
      const [works, artists] = await Promise.all([
        api.request({ url: "/works", method: "GET" }),
        api.request({ url: "/artists/recommend?limit=6", method: "GET" })
      ])

      const hotWorks = (works || []).slice(0, 4).map((item) => ({
        id: item.artworkId,
        title: item.title,
        artistName: item.artistName,
        coverUrl: item.coverUrl,
        priceText: this.formatPrice(item.currentPrice)
      }))

      const risingWorks = (works || []).slice(0, 4).map((item) => ({
        id: item.artworkId,
        title: item.title,
        artistName: item.artistName,
        priceText: this.formatPrice(item.currentPrice),
        trendText: item.saleStatus || "持续更新"
      }))

      const recommendedArtists = (artists || []).map((item) => ({
        id: item.artistId,
        artistName: item.artistName,
        avatar: item.avatar,
        levelName: item.levelName,
        slogan: item.slogan
      }))

      this.setData({
        loading: false,
        error: "",
        hotWorks,
        risingWorks,
        recommendedArtists,
        emptyState: !hotWorks.length && !recommendedArtists.length
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "首页加载失败"
      })
    }
  },

  formatPrice(price) {
    if (price === null || price === undefined || price === "") {
      return "价格待定"
    }
    return `¥${price}`
  },

  goArtistProfile(event) {
    const artistId = event.currentTarget.dataset.artistId
    if (!artistId) return
    wx.navigateTo({ url: `/pages/artist/profile?id=${artistId}` })
  },

  goArtworkDetail(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    if (!artworkId) return
    wx.navigateTo({ url: `/pages/artwork/detail?id=${artworkId}` })
  },

  handleRetry() {
    this.loadHomeData()
  }
})
