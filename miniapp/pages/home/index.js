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

function buildArtworkCard(item) {
  return {
    id: item.artworkId,
    title: item.title,
    artistName: item.artistName,
    coverUrl: item.coverUrl || "",
    priceText: formatPrice(item.currentPrice),
    statusText: formatSaleStatus(item.saleStatus),
    coverFallback: (item.title || "作品").slice(0, 2)
  }
}

Page({
  data: {
    loading: true,
    error: "",
    emptyState: false,
    featuredWorks: [],
    hotWorks: [],
    risingWorks: [],
    recommendedArtists: [],
    artistPage: 1,
    artistPageSize: 4,
    allArtistsLoaded: false
  },

  onShow() {
    this.loadHomeData()
  },

  onReachBottom() {
    if (this.data.allArtistsLoaded || this.data.loading) return
    this.loadMoreArtists()
  },

  async loadHomeData() {
    this.setData({ loading: true, error: "", emptyState: false, artistPage: 1, allArtistsLoaded: false })
    try {
      const [works, artists] = await Promise.all([
        api.request({ url: "/works", method: "GET" }),
        api.request({ url: "/artists/recommend?limit=4", method: "GET" })
      ])

      const list = (works || []).map(buildArtworkCard)
      const featuredWorks = list.slice(0, 6)
      const hotWorks = list.slice(0, 4)
      const risingWorks = (list.slice(1, 4).length ? list.slice(1, 4) : list.slice(0, 3))
      const recommendedArtists = (artists || []).slice(0, 4).map((item) => ({
        id: item.artistId,
        artistName: item.artistName,
        levelName: item.levelName,
        slogan: item.slogan,
        avatar: item.avatar || ""
      }))

      this.setData({
        loading: false,
        error: "",
        featuredWorks,
        hotWorks,
        risingWorks,
        recommendedArtists,
        emptyState: !featuredWorks.length && !recommendedArtists.length,
        allArtistsLoaded: recommendedArtists.length < 4
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "首页加载失败"
      })
    }
  },

  async loadMoreArtists() {
    try {
      const nextPage = this.data.artistPage + 1
      const limit = nextPage * this.data.artistPageSize
      const artists = await api.request({ url: `/artists/recommend?limit=${limit}`, method: "GET" })
      const normalized = (artists || []).slice(0, limit).map((item) => ({
        id: item.artistId,
        artistName: item.artistName,
        levelName: item.levelName,
        slogan: item.slogan,
        avatar: item.avatar || ""
      }))
      this.setData({
        recommendedArtists: normalized,
        artistPage: nextPage,
        allArtistsLoaded: normalized.length < limit
      })
    } catch (_) {}
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
