const api = require("../../utils/api")

function formatPrice(value) {
  if (value === null || value === undefined || value === "") {
    return "价格待定"
  }
  return `¥${value}`
}

function mapSaleStatus(status) {
  const map = {
    ON_SALE: "在售中",
    PUBLISHED: "在售中",
    COLLECTED: "已收藏",
    SOLD_OUT: "已售罄",
    DRAFT: "待上架"
  }
  return map[status] || "持续更新"
}

function mapSaleMode(mode) {
  const map = {
    NORMAL: "常规发售",
    AUCTION: "竞价收藏",
    LIMITED: "限量发售"
  }
  return map[mode] || "官方推荐"
}

Page({
  data: {
    artworkId: null,
    loading: true,
    error: "",
    detail: null,
    images: [],
    coverImage: "",
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

      const imageUrls = (detail.mediaUrls || []).filter(Boolean)
      const coverImage = imageUrls[0] || detail.coverUrl || ""

      this.setData({
        detail: {
          ...detail,
          saleStatusText: mapSaleStatus(detail.saleStatus),
          saleModeText: mapSaleMode(detail.saleMode)
        },
        images: imageUrls,
        coverImage,
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
    if (detail.resaleEnabled) tags.push("可转售")
    tags.push(mapSaleMode(detail.saleMode))
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
    const url = event.currentTarget.dataset.url || this.data.coverImage
    const urls = (this.data.images || []).filter(Boolean)
    if (!url) return
    wx.previewImage({ current: url, urls: urls.length ? urls : [url] })
  },

  handleRetry() {
    if (!this.data.artworkId) return
    this.loadDetail(this.data.artworkId)
  },

  formatPrice
})
