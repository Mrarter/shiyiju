const api = require("../../utils/api")

function formatPrice(value) {
  if (value === null || value === undefined || value === "") {
    return "价格待定"
  }
  return `¥${value}`
}

Page({
  data: {
    artworkId: null,
    loading: true,
    error: "",
    detail: null,
    images: [],
    tags: [],
    stats: []
  },

  onLoad(options) {
    const artworkId = Number(options.id || options.artworkId)
    if (!artworkId) {
      this.setData({
        loading: false,
        error: "缺少作品ID"
      })
      return
    }
    this.setData({ artworkId })
    this.loadDetail(artworkId)
  },

  async loadDetail(artworkId) {
    this.setData({ loading: true, error: "" })
    try {
      const detail = await api.request({
        url: `/works/${artworkId}`,
        method: "GET"
      })

      this.setData({
        detail,
        images: detail.mediaList || [],
        tags: this.buildTags(detail),
        stats: this.buildStats(detail),
        loading: false,
        error: ""
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "作品详情加载失败"
      })
    }
  },

  buildTags(detail) {
    const tags = []
    if (detail.artistLevelName) tags.push(detail.artistLevelName)
    if (detail.supportResale) tags.push("可转售")
    if (detail.saleMode) tags.push(detail.saleMode)
    return tags
  },

  buildStats(detail) {
    return [
      { label: "收藏", value: detail.favoriteCount || 0 },
      { label: "浏览", value: detail.viewCount || 0 },
      { label: "在线天数", value: detail.onlineDays || 0 }
    ]
  },

  previewImage(event) {
    const url = event.currentTarget.dataset.url
    const urls = this.data.images.map((item) => item.mediaUrl)
    if (!url || !urls.length) return
    wx.previewImage({ current: url, urls })
  },

  handleRetry() {
    if (!this.data.artworkId) return
    this.loadDetail(this.data.artworkId)
  },

  formatPrice
})
