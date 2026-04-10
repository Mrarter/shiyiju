const api = require("../../utils/api")
const { normalizeImageUrl, getArtistAvatarPlaceholder, getArtworkCoverPlaceholder } = require("../../utils/imageUrl")

// Mock 数据兜底
const MOCK_WORKS = {
  1: { artworkId: 1, title: "静谧的山谷", artistName: "李明", artistInitial: "李", category: "PAINTING", saleStatus: "ON_SALE", saleMode: "NORMAL", currentPrice: 12800, basePrice: 12800, coverUrl: "https://picsum.photos/seed/art1/400/500", material: "布面油画", widthCm: 80, heightCm: 100, depthCm: 3, creationYear: "2024", artworkNo: "SYJ-2024-0001", favoriteCount: 328, viewCount: 1256, onlineDays: 45, certificateType: "ELECTRONIC", groupEnabled: false, resaleEnabled: false, priceStatus: "STABLE" },
  2: { artworkId: 2, title: "城市光影系列", artistName: "王芳", artistInitial: "王", category: "PRINT", saleStatus: "PUBLISHED", saleMode: "LIMITED", currentPrice: 6800, basePrice: 6800, coverUrl: "https://picsum.photos/seed/art2/400/500", material: "丝网版画", widthCm: 50, heightCm: 70, creationYear: "2023", artworkNo: "SYJ-2023-0042", favoriteCount: 156, viewCount: 892, onlineDays: 30, certificateType: "PAPER", groupEnabled: true, resaleEnabled: false, priceStatus: "STABLE" },
  3: { artworkId: 3, title: "水墨山水", artistName: "张伟", artistInitial: "张", category: "INK", saleStatus: "ON_SALE", saleMode: "NORMAL", currentPrice: 28000, basePrice: 28000, coverUrl: "https://picsum.photos/seed/art3/400/500", material: "宣纸水墨", widthCm: 138, heightCm: 69, creationYear: "2025", artworkNo: "SYJ-2025-0008", favoriteCount: 567, viewCount: 2340, onlineDays: 15, certificateType: "ELECTRONIC", groupEnabled: false, resaleEnabled: true, priceStatus: "STABLE" },
  4: { artworkId: 4, title: "抽象艺术 No.7", artistName: "刘涛", artistInitial: "刘", category: "PAINTING", saleStatus: "ON_SALE", saleMode: "LIMITED", currentPrice: 15800, basePrice: 19800, coverUrl: "https://picsum.photos/seed/art4/400/500", material: "布面丙烯", widthCm: 60, heightCm: 80, creationYear: "2024", artworkNo: "SYJ-2024-0023", favoriteCount: 234, viewCount: 1108, onlineDays: 38, certificateType: "ELECTRONIC", groupEnabled: true, resaleEnabled: false, priceStatus: "DYNAMIC" },
  5: { artworkId: 5, title: "海边日落", artistName: "陈静", artistInitial: "陈", category: "PAINTING", saleStatus: "PUBLISHED", saleMode: "NORMAL", currentPrice: 8800, basePrice: 8800, coverUrl: "https://picsum.photos/seed/art5/400/500", material: "布面油画", widthCm: 40, heightCm: 50, creationYear: "2025", artworkNo: "SYJ-2025-0015", favoriteCount: 189, viewCount: 756, onlineDays: 22, certificateType: "PAPER", groupEnabled: false, resaleEnabled: false, priceStatus: "STABLE" },
  6: { artworkId: 6, title: "雕塑作品 #3", artistName: "赵磊", artistInitial: "赵", category: "SCULPTURE", saleStatus: "ON_SALE", saleMode: "AUCTION", currentPrice: 45000, basePrice: 38000, coverUrl: "https://picsum.photos/seed/art6/400/500", material: "青铜", widthCm: 25, heightCm: 45, depthCm: 20, creationYear: "2023", artworkNo: "SYJ-2023-0018", favoriteCount: 445, viewCount: 1876, onlineDays: 60, certificateType: "ELECTRONIC", groupEnabled: false, resaleEnabled: true, priceStatus: "DYNAMIC" },
  7: { artworkId: 7, title: "花卉系列", artistName: "孙丽", artistInitial: "孙", category: "PRINT", saleStatus: "PUBLISHED", saleMode: "LIMITED", currentPrice: 5200, basePrice: 6800, coverUrl: "https://picsum.photos/seed/art7/400/500", material: "木版版画", widthCm: 45, heightCm: 60, creationYear: "2024", artworkNo: "SYJ-2024-0031", favoriteCount: 98, viewCount: 445, onlineDays: 28, certificateType: "PAPER", groupEnabled: true, resaleEnabled: false, priceStatus: "STABLE" },
  8: { artworkId: 8, title: "竹林深处", artistName: "周杰", artistInitial: "周", category: "INK", saleStatus: "ON_SALE", saleMode: "NORMAL", currentPrice: 19800, basePrice: 19800, coverUrl: "https://picsum.photos/seed/art8/400/500", material: "绢本水墨", widthCm: 68, heightCm: 136, creationYear: "2025", artworkNo: "SYJ-2025-0003", favoriteCount: 312, viewCount: 1432, onlineDays: 35, certificateType: "ELECTRONIC", groupEnabled: false, resaleEnabled: false, priceStatus: "STABLE" },
  9: { artworkId: 9, title: "星空之下", artistName: "吴敏", artistInitial: "吴", category: "PAINTING", saleStatus: "PUBLISHED", saleMode: "LIMITED", currentPrice: 22800, basePrice: 26800, coverUrl: "https://picsum.photos/seed/art9/400/500", material: "布面油画", widthCm: 90, heightCm: 120, creationYear: "2024", artworkNo: "SYJ-2024-0056", favoriteCount: 421, viewCount: 1890, onlineDays: 42, certificateType: "ELECTRONIC", groupEnabled: true, resaleEnabled: true, priceStatus: "DYNAMIC" },
  10: { artworkId: 10, title: "现代都市", artistName: "郑强", artistInitial: "郑", category: "PRINT", saleStatus: "ON_SALE", saleMode: "NORMAL", currentPrice: 7800, basePrice: 7800, coverUrl: "https://picsum.photos/seed/art10/400/500", material: "石版画", widthCm: 55, heightCm: 75, creationYear: "2025", artworkNo: "SYJ-2025-0021", favoriteCount: 167, viewCount: 678, onlineDays: 18, certificateType: "PAPER", groupEnabled: false, resaleEnabled: false, priceStatus: "STABLE" }
}

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
    favorited: false,
    tags: [],
    stats: [],
    infoRows: [],
    optionRows: [],
    showWantModal: false
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
        coverUrl: normalizeImageUrl(detail.coverUrl, getArtworkCoverPlaceholder(artworkId)),
        artistAvatar: normalizeImageUrl(detail.artistAvatar, getArtistAvatarPlaceholder(detail.artistName)),
        artistInitial: (detail.artistName || "艺").slice(0, 1),
        saleStatusText: mapSaleStatus(detail.saleStatus),
        saleModeText: mapSaleMode(detail.saleMode),
        categoryText: mapCategory(detail.category),
        currentPriceText: formatPrice(detail.currentPrice),
        changeRateText: formatChangeRate(detail),
        dynamicHint: detail.priceStatus === "DYNAMIC" ? "动态价格，每日10点更新" : "价格稳定，可直接入藏"
      }

      // 检查收藏状态
      const favorites = wx.getStorageSync('favorites') || []
      const isFavorited = favorites.includes(artworkId)

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
        favorited: isFavorited,
        loading: false,
        error: ""
      })
    } catch (error) {
      // API 失败时尝试使用 mock 数据
      const mockData = MOCK_WORKS[artworkId]
      if (mockData) {
        const detail = mockData
        const enriched = {
          ...detail,
          coverUrl: normalizeImageUrl(detail.coverUrl, getArtworkCoverPlaceholder(artworkId)),
          artistAvatar: normalizeImageUrl(detail.artistAvatar, getArtistAvatarPlaceholder(detail.artistName)),
          artistInitial: (detail.artistName || "艺").slice(0, 1),
          saleStatusText: mapSaleStatus(detail.saleStatus),
          saleModeText: mapSaleMode(detail.saleMode),
          categoryText: mapCategory(detail.category),
          currentPriceText: formatPrice(detail.currentPrice),
          changeRateText: formatChangeRate(detail),
          dynamicHint: detail.priceStatus === "DYNAMIC" ? "动态价格，每日10点更新" : "价格稳定，可直接入藏"
        }

        const favorites = wx.getStorageSync('favorites') || []
        const isFavorited = favorites.includes(artworkId)

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
          favorited: isFavorited,
          loading: false,
          error: ""
        })
      } else {
        this.setData({
          loading: false,
          error: "作品详情加载失败"
        })
      }
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

  handleFavoriteTap() {
    const artworkId = this.data.artworkId
    const favorites = wx.getStorageSync('favorites') || []
    const index = favorites.indexOf(artworkId)
    
    if (index > -1) {
      favorites.splice(index, 1)
      wx.showToast({ title: '已取消收藏', icon: 'success' })
    } else {
      favorites.push(artworkId)
      wx.showToast({ title: '收藏成功', icon: 'success' })
    }
    
    wx.setStorageSync('favorites', favorites)
    this.setData({ favorited: !this.data.favorited })
    
    // 同步到服务器
    api.request({
      url: `/works/${artworkId}/favorite`,
      method: "POST",
      data: { favorite: index === -1 }
    }).catch(() => {})
  },

  handleFollowTap() {
    this.setData({ followed: !this.data.followed })
  },

  // 显示我想要弹窗
  showWantOptions() {
    this.setData({ showWantModal: true })
  },

  // 隐藏我想要弹窗
  hideWantOptions() {
    this.setData({ showWantModal: false })
  },

  // 阻止事件冒泡
  stopPropagation() {
    return
  },

  // 加入购物车
  async handleAddCart() {
    const { artworkId } = this.data
    this.setData({ showWantModal: false })
    
    try {
      await api.request({
        url: `/cart/add`,
        method: "POST",
        data: { artworkId, quantity: 1 }
      })
      wx.showToast({ title: "已加入购物车", icon: "success" })
    } catch (e) {
      console.error("[作品详情] 加入购物车失败:", e)
      // 如果 API 失败，保存到本地存储
      this.addToLocalCart(artworkId)
      wx.showToast({ title: "已加入购物车", icon: "success" })
    }
  },

  // 添加到本地购物车（API 失败时使用）
  addToLocalCart(artworkId) {
    try {
      const localCart = wx.getStorageSync('localCart') || []
      const existing = localCart.find(item => item.artworkId === artworkId)
      
      if (existing) {
        existing.quantity += 1
      } else {
        // 使用作品详情数据
        const { detail } = this.data
        localCart.push({
          id: 'local_' + Date.now(),
          artworkId: artworkId,
          title: detail.title,
          artistName: detail.artistName,
          price: detail.currentPrice,
          quantity: 1,
          stock: detail.stock || 1,
          coverUrl: detail.coverUrl,
          widthCm: detail.widthCm,
          heightCm: detail.heightCm,
          creationYear: detail.creationYear,
          category: detail.category,
          selected: true
        })
      }
      
      wx.setStorageSync('localCart', localCart)
    } catch (e) {
      console.error("[作品详情] 保存本地购物车失败:", e)
    }
  },

  // 直接购买
  handleDirectBuy() {
    const { artworkId } = this.data
    this.setData({ showWantModal: false })
    wx.navigateTo({
      url: `/pages/order/confirm?artworkId=${artworkId}`
    })
  },

  // 联系作者
  handleContactAuthor() {
    const { detail } = this.data
    this.setData({ showWantModal: false })
    wx.showToast({ title: "正在打开会话...", icon: "none" })
    // TODO: 调用联系作者 API
    api.request({
      url: `/messages/artist`,
      method: "POST",
      data: { artistId: detail.artistId }
    }).catch(() => {})
  },

  // 评论点击
  handleCommentTap() {
    const { artworkId } = this.data
    wx.showToast({ title: "评论功能开发中", icon: "none" })
    // TODO: 跳转到评论页
    // wx.navigateTo({
    //   url: `/pages/comment/list?artworkId=${artworkId}`
    // })
  },

  handleCartTap() {
    wx.showToast({ title: "正在跳转购物车...", icon: "none" })
    // TODO: 跳转到购物车页
    // wx.switchTab({ url: "/pages/cart/index" })
  },

  handleRetry() {
    if (!this.data.artworkId) return
    this.loadDetail(this.data.artworkId)
  }
})
