const api = require("../../utils/api")

Page({
  data: {
    loading: true,
    error: "",
    hotWorks: [],
    risingWorks: [],
    artists: []
  },

  onShow() {
    this.loadPage()
  },

  async loadPage() {
    this.setData({ loading: true, error: "" })
    try {
      const [works, artists] = await Promise.all([
        api.request({ url: "/works", data: { status: "PUBLISHED" } }),
        api.request({ url: "/artists/recommend", data: { limit: 4 } })
      ])
      this.setData({
        hotWorks: works.slice(0, 4).map(item => ({
          id: item.artworkId,
          title: item.title,
          artist: item.artistName,
          price: this.formatPrice(item.currentPrice),
          rise: `热度 ${item.favoriteCount || 0}`,
          badge: item.artistLevelName || "艺术家作品"
        })),
        risingWorks: works.slice(0, 4).map(item => ({
          id: item.artworkId,
          title: item.title,
          price: this.formatPrice(item.currentPrice),
          stats: `关注 ${item.favoriteCount || 0} · 浏览 ${item.viewCount || 0}`
        })),
        artists: artists.map(item => ({
          id: item.artistId,
          name: item.artistName,
          level: item.levelName,
          desc: item.slogan || "艺术家主页"
        })),
        loading: false
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "首页加载失败"
      })
    }
  },

  goArtistProfile(event) {
    const artistId = event.currentTarget.dataset.artistId
    if (!artistId) {
      return
    }
    wx.navigateTo({
      url: `/pages/artist/profile?id=${artistId}`
    })
  },

  goArtworkDetail(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    if (!artworkId) {
      return
    }
    wx.navigateTo({
      url: `/pages/artwork/detail?id=${artworkId}`
    })
  },

  formatPrice(price) {
    if (price === undefined || price === null || price === "") {
      return "¥0"
    }
    return `¥${price}`
  }
})
