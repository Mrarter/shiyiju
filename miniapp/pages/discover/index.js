const api = require("../../utils/api")

Page({
  data: {
    filters: ["签约艺术家", "独家发行", "拼团专区", "流通市场"],
    activeIndex: 0,
    loading: true,
    error: "",
    artists: [],
    artworks: []
  },

  onShow() {
    this.loadDiscoverData()
  },

  async loadDiscoverData() {
    this.setData({ loading: true, error: "" })
    try {
      const [artists, artworks] = await Promise.all([
        api.request({ url: "/artists/recommend?limit=6", method: "GET" }),
        api.request({ url: "/works", method: "GET" })
      ])
      this.setData({
        loading: false,
        artists: artists || [],
        artworks: (artworks || []).slice(0, 8)
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "发现页加载失败"
      })
    }
  },

  handleTabTap(event) {
    this.setData({ activeIndex: event.currentTarget.dataset.index })
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
    this.loadDiscoverData()
  }
})
