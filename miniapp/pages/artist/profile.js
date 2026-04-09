const api = require("../../utils/api")

function formatMoney(value) {
  if (value === null || value === undefined || value === "") {
    return "¥0"
  }
  const num = Number(value)
  if (Number.isNaN(num)) {
    return `¥${value}`
  }
  return `¥${num.toLocaleString("zh-CN")}`
}

function mapSaleStatus(status) {
  const map = {
    PUBLISHED: "在售中",
    ON_SALE: "在售中",
    COLLECTED: "已收藏",
    SOLD_OUT: "已售罄",
    DRAFT: "待上架"
  }
  return map[status] || "持续更新"
}

Page({
  data: {
    artistId: null,
    loading: true,
    error: "",
    artist: null,
    stats: [],
    works: []
  },

  onLoad(options) {
    const artistId = Number(options.id)
    if (!artistId) {
      this.setData({
        loading: false,
        error: "缺少艺术家ID"
      })
      return
    }
    this.setData({ artistId })
    this.loadArtistProfile(artistId)
  },

  async loadArtistProfile(artistId) {
    this.setData({ loading: true, error: "" })
    try {
      const detail = await api.request({
        url: `/artists/${artistId}`,
        method: "GET"
      })
      this.setData({
        loading: false,
        artist: {
          ...detail,
          totalSaleAmountText: formatMoney(detail.totalSaleAmount)
        },
        stats: this.buildStats(detail),
        works: (detail.works || []).map((item) => ({
          ...item,
          saleStatusText: mapSaleStatus(item.saleStatus),
          currentPriceText: formatMoney(item.currentPrice)
        }))
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "艺术家页面加载失败"
      })
    }
  },

  buildStats(detail) {
    return [
      { label: "粉丝", value: detail.fanCount || 0 },
      { label: "作品", value: detail.workCount || 0 },
      { label: "成交", value: detail.totalSaleAmountText || formatMoney(detail.totalSaleAmount) },
      { label: "售出", value: detail.soldCount || 0 }
    ]
  },

  goArtworkDetail(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    if (!artworkId) return
    wx.navigateTo({
      url: `/pages/artwork/detail?id=${artworkId}`
    })
  },

  handleRetry() {
    if (!this.data.artistId) return
    this.loadArtistProfile(this.data.artistId)
  },

  goBack() {
    wx.navigateBack({ fail: () => wx.switchTab({ url: '/pages/home/index' }) })
  }
})
