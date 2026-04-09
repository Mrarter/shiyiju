const api = require("../../utils/api")

function formatPrice(price) {
  if (price === null || price === undefined || price === "") return "价格待定"
  return `¥${price}`
}

function formatCategory(category) {
  const map = {
    PAINTING: "绘画",
    PRINT: "版画",
    INK: "水墨",
    SCULPTURE: "雕塑",
    OTHER: "综合"
  }
  return map[category] || "综合"
}

function buildCoverStyle(index) {
  const presets = [
    { background: "linear-gradient(135deg, #8f6552 0%, #6f4c3f 45%, #3a2925 100%)", glow: "#d9b287" },
    { background: "linear-gradient(135deg, #7c665c 0%, #53433c 48%, #2d231f 100%)", glow: "#d9cec0" },
    { background: "linear-gradient(135deg, #a0785c 0%, #755842 46%, #3f2e27 100%)", glow: "#e2c39e" },
    { background: "linear-gradient(135deg, #6a5248 0%, #40322c 45%, #241b18 100%)", glow: "#c59b6c" }
  ]
  return presets[index % presets.length]
}

function normalizeWork(item, index) {
  const style = buildCoverStyle(index)
  const heights = [300, 400, 350, 450, 320, 380]
  return {
    id: item.artworkId,
    title: item.title,
    artistName: item.artistName,
    spec: `${item.artistName} · ${formatCategory(item.category)}`,
    priceText: formatPrice(item.currentPrice),
    originalPrice: item.originalPrice ? `¥${item.originalPrice}` : "",
    coverUrl: item.coverUrl || "",
    tag: item.tag || "",
    countdown: item.countdown || "",
    height: heights[index % heights.length],
    coverStyle: style
  }
}

Page({
  data: {
    loading: true,
    error: "",
    bannerItems: [],
    leftColumn: [],
    rightColumn: [],
    categories: [
      { id: 0, name: "推荐", icon: "🔥" },
      { id: 1, name: "绘画", icon: "🖼" },
      { id: 2, name: "版画", icon: "📄" },
      { id: 3, name: "雕塑", icon: "🗿" },
      { id: 4, name: "水墨", icon: "🖌" },
      { id: 5, name: "更多", icon: "⋯" }
    ],
    currentCategory: 0,
    hasAnyContent: false
  },

  onShow() {
    this.loadHome()
  },

  async loadHome() {
    this.setData({ loading: true, error: "" })
    try {
      const works = await api.request({ url: "/works", method: "GET" })
      const workPool = (works || []).map(normalizeWork)
      const hasAnyContent = workPool.length > 0

      if (!hasAnyContent) {
        // Mock data for demo
        const mockWorks = this.getMockWorks()
        const { left, right } = this.splitToColumns(mockWorks)
        this.setData({
          loading: false,
          error: "",
          bannerItems: this.getMockBanners(),
          leftColumn: left,
          rightColumn: right,
          hasAnyContent: true
        })
        return
      }

      const { left, right } = this.splitToColumns(workPool)
      this.setData({
        loading: false,
        error: "",
        bannerItems: workPool.slice(0, 3).map((item, index) => ({
          ...item,
          tag: index === 0 ? "精选" : "",
          date: this.getRandomDate()
        })),
        leftColumn: left,
        rightColumn: right,
        hasAnyContent: true
      })
    } catch (error) {
      const mockWorks = this.getMockWorks()
      const { left, right } = this.splitToColumns(mockWorks)
      this.setData({
        loading: false,
        error: "",
        bannerItems: this.getMockBanners(),
        leftColumn: left,
        rightColumn: right,
        hasAnyContent: true
      })
    }
  },

  splitToColumns(works) {
    const left = []
    const right = []
    works.forEach((item, index) => {
      if (index % 2 === 0) {
        left.push(item)
      } else {
        right.push(item)
      }
    })
    return { left, right }
  },

  getMockWorks() {
    const works = [
      { artworkId: 1, title: "静谧的山谷", artistName: "李明", category: "PAINTING", currentPrice: 12800, originalPrice: 15800, tag: "特价", countdown: "还剩 2 天" },
      { artworkId: 2, title: "城市光影系列", artistName: "王芳", category: "PRINT", currentPrice: 6800, tag: "" },
      { artworkId: 3, title: "水墨山水", artistName: "张伟", category: "INK", currentPrice: 28000, tag: "新品" },
      { artworkId: 4, title: "抽象艺术 No.7", artistName: "刘涛", category: "PAINTING", currentPrice: 15800, originalPrice: 19800, tag: "特价" },
      { artworkId: 5, title: "海边日落", artistName: "陈静", category: "PAINTING", currentPrice: 8800 },
      { artworkId: 6, title: "雕塑作品 #3", artistName: "赵磊", category: "SCULPTURE", currentPrice: 45000, tag: "独家" },
      { artworkId: 7, title: "花卉系列", artistName: "孙丽", category: "PRINT", currentPrice: 5200, originalPrice: 6800 },
      { artworkId: 8, title: "竹林深处", artistName: "周杰", category: "INK", currentPrice: 19800, tag: "热卖" },
      { artworkId: 9, title: "星空之下", artistName: "吴敏", category: "PAINTING", currentPrice: 22800, originalPrice: 26800 },
      { artworkId: 10, title: "现代都市", artistName: "郑强", category: "PRINT", currentPrice: 7800, tag: "新品" }
    ]
    return works.map((item, index) => normalizeWork(item, index))
  },

  getMockBanners() {
    return [
      { id: 1, title: "当代艺术展", subtitle: "2026春季大促 全场8折起", date: "2026.04.01 - 04.30", coverStyle: { background: "linear-gradient(135deg, #8f6552, #3a2925)", glow: "#d9b287" } },
      { id: 2, title: "新锐艺术家专区", subtitle: "发现下一个艺术大师", date: "持续更新", coverStyle: { background: "linear-gradient(135deg, #7c665c, #2d231f)", glow: "#d9cec0" } },
      { id: 3, title: "版画专场", subtitle: "收藏级限量版画", date: "本周精选", coverStyle: { background: "linear-gradient(135deg, #a0785c, #3f2e27)", glow: "#e2c39e" } }
    ]
  },

  getRandomDate() {
    const dates = ["今天", "昨天", "3天前", "1周前", "刚刚"]
    return dates[Math.floor(Math.random() * dates.length)]
  },

  switchCategory(event) {
    const index = event.currentTarget.dataset.index
    this.setData({ currentCategory: index })
  },

  goArtworkDetail(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    if (!artworkId) return
    wx.navigateTo({ url: `/pages/artwork/detail?id=${artworkId}` })
  },

  handleRetry() {
    this.loadHome()
  }
})
