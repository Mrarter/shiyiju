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
      const hotSource = list.slice(0, 3)
      const risingSource = list.slice(1, 3).length ? list.slice(1, 3) : list.slice(0, 2)

      const hotWorks = hotSource.map((item) => ({
        id: item.artworkId,
        title: item.title,
        artistName: item.artistName,
        coverUrl: item.coverUrl,
        priceText: formatPrice(item.currentPrice),
        subtitle: `${item.artistName} · ${formatSaleStatus(item.saleStatus)}`
      }))

      const risingWorks = risingSource.map((item) => ({
        id: item.artworkId,
        title: item.title,
        artistName: item.artistName,
        priceText: formatPrice(item.currentPrice),
        trendText: formatSaleStatus(item.saleStatus)
      }))

      const recommendedArtists = (artists || []).slice(0, 3).map((item) => ({
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
