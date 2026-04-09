const api = require("../../utils/api")

function formatPrice(value) {
  if (value === null || value === undefined || value === "") {
    return "价格待定"
  }
  const numeric = Number(value)
  if (Number.isNaN(numeric)) return `¥${value}`
  return `¥${numeric.toLocaleString("zh-CN")}`
}

function formatChangeRate(detail) {
  if (!detail.basePrice || !detail.currentPrice) return "+0.00%（近30日）"
  const rate = ((Number(detail.currentPrice) - Number(detail.basePrice)) / Number(detail.basePrice)) * 100
  const sign = rate >= 0 ? "+" : ""
  return `${sign}${rate.toFixed(2)}%（较首发）`
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

function mapCategory(category) {
  const map = {
    PAINTING: "绘画",
    PRINT: "版画",
    INK: "水墨",
    SCULPTURE: "雕塑",
    OTHER: "综合材料"
  }
  return map[category] || "综合材料"
}

function mapCertificate(type) {
  const map = {
    ELECTRONIC: "寄证书",
    PAPER: "寄作品"
  }
  return map[type] || "寄证书"
}

function buildPlaceholderStyle(seed) {
  const presets = [
    {
      background: "linear-gradient(180deg, #c9c3b7 0%, #737166 36%, #35372f 62%, #24251f 100%)",
      glow: "rgba(242, 221, 171, 0.9)",
      mountain: "#434338"
    },
    {
      background: "linear-gradient(180deg, #d3c7ae 0%, #8f8a7c 38%, #514d42 65%, #27241f 100%)",
      glow: "rgba(243, 224, 189, 0.88)",
      mountain: "#4f4a40"
    },
    {
      background: "linear-gradient(180deg, #d9d3c9 0%, #807b70 34%, #44433d 66%, #20201d 100%)",
      glow: "rgba(255, 231, 196, 0.86)",
      mountain: "#555046"
    }
  ]
  return presets[seed % presets.length]
}

Page({
  data: {
    artworkId: null,
    loading: true,
    error: "",
    detail: null,
    heroVisual: null,
    followed: true,
    liked: false,
    tags: [],
    stats: [],
    infoRows: [],
    optionRows: [],
    primaryActionText: "立即入藏"
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

      const enriched = {
        ...detail,
        artistInitial: (detail.artistName || "艺").slice(0, 1),
        saleStatusText: mapSaleStatus(detail.saleStatus),
        saleModeText: mapSaleMode(detail.saleMode),
        categoryText: mapCategory(detail.category),
        currentPriceText: formatPrice(detail.currentPrice),
        changeRateText: formatChangeRate(detail),
        dynamicHint: detail.priceStatus === "DYNAMIC" ? "动态价格，每日10点更新" : "价格稳定，可直接入藏"
      }

      this.setData({
        detail: enriched,
        heroVisual: {
          title: (detail.title || "作品").slice(0, 4),
          style: buildPlaceholderStyle(detail.artworkId || artworkId)
        },
        tags: this.buildTags(detail),
        stats: this.buildStats(detail),
        infoRows: this.buildInfoRows(detail),
        optionRows: this.buildOptionRows(detail),
        primaryActionText: detail.groupEnabled ? "发起拼团" : (detail.resaleEnabled ? "查看转售" : "立即入藏"),
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
    tags.push(mapSaleMode(detail.saleMode))
    return tags
  },

  buildStats(detail) {
    return [
      { label: "在链时长", value: `${detail.onlineDays || 0}天` },
      { label: "收藏人数", value: `${detail.favoriteCount || 0}` },
      { label: "浏览量", value: detail.viewCount >= 1000 ? `${(detail.viewCount / 1000).toFixed(1)}k` : `${detail.viewCount || 0}` }
    ]
  },

  buildInfoRows(detail) {
    return [
      { label: "材质", value: detail.material || "待补充" },
      { label: "尺寸", value: `${detail.widthCm || "-"}cm × ${detail.heightCm || "-"}cm${detail.depthCm ? ` × ${detail.depthCm}cm` : ""}` },
      { label: "创作时间", value: detail.creationYear || "待补充" },
      { label: "作品编号", value: detail.artworkNo || "待生成" }
    ]
  },

  buildOptionRows(detail) {
    return [
      { label: "配送方式", value: mapCertificate(detail.certificateType) },
      { label: "托管方案", value: detail.saleStatus === "ON_SALE" ? "平台托管中" : "线下托管" },
      { label: "拼团入口", value: detail.groupEnabled ? "可发起拼团" : "暂未开放" }
    ]
  },

  goBack() {
    if (getCurrentPages().length > 1) {
      wx.navigateBack()
      return
    }
    wx.switchTab({ url: "/pages/discover/index" })
  },

  handleShareTap() {
    wx.showToast({ title: "分享能力整理中", icon: "none" })
  },

  handleLikeTap() {
    this.setData({ liked: !this.data.liked })
  },

  handleFollowTap() {
    this.setData({ followed: !this.data.followed })
  },

  handleCartTap() {
    wx.showToast({ title: "已加入意向清单", icon: "none" })
  },

  handlePrimaryTap() {
    wx.showToast({ title: this.data.primaryActionText, icon: "none" })
  },

  handleRetry() {
    if (!this.data.artworkId) return
    this.loadDetail(this.data.artworkId)
  }
})
