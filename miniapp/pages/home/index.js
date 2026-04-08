const api = require("../../utils/api")

function formatPrice(price) {
  if (price === null || price === undefined || price === "") {
    return "价格待定"
  }
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

Page({
  data: {
    loading: true,
    error: "",
    emptyState: false,
    featuredWork: null,
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

      const list = works || []
      const featured = list[0]
      const featuredWork = featured ? {
        id: featured.artworkId,
        title: featured.title,
        artistName: featured.artistName,
        coverUrl: featured.coverUrl,
        priceText: formatPrice(featured.currentPrice),
        statusText: formatSaleStatus(featured.saleStatus)
      } : null

      const hotWorks = list.slice(0, 4).map((item) => ({
        id: item.artworkId,
        title: item.title,
        artistName: item.artistName,
        priceText: formatPrice(item.currentPrice),
        statusText: formatSaleStatus(item.saleStatus)
      }))

      const risingWorks = list.slice(0, 2).map((item) => ({
        id: item.artworkId,
        title: item.title,
        artistName: item.artistName,
        priceText: formatPrice(item.currentPrice),
        statusText: formatSaleStatus(item.saleStatus)
      }))

      const recommendedArtists = (artists || []).slice(0, 3).map((item) => ({
        id: item.artistId,
        artistName: item.artistName,
        levelName: item.levelName,
        slogan: item.slogan
      }))

      this.setData({
        loading: false,
        error: "",
        featuredWork,
        hotWorks,
        risingWorks,
        recommendedArtists,
        emptyState: !featuredWork && !recommendedArtists.length
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
