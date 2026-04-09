const api = require("../../utils/api")

const PAGE_SIZE = 10

function formatPrice(price) {
  if (price === null || price === undefined || price === "") return "价格待定"
  return `¥${price.toLocaleString()}`
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
    { background: "linear-gradient(135deg, #6a5248 0%, #40322c 45%, #241b18 100%)", glow: "#c59b6c" },
    { background: "linear-gradient(135deg, #9a8878 0%, #6b5c50 48%, #3d322c 100%)", glow: "#c4a88a" },
    { background: "linear-gradient(135deg, #8a7060 0%, #5c4d42 45%, #2d2522 100%)", glow: "#d4b090" }
  ]
  return presets[index % presets.length]
}

function normalizeWork(item, index) {
  const style = buildCoverStyle(index)
  const heights = [320, 400, 280, 360, 440, 300, 380, 260, 420, 340]
  return {
    id: item.artworkId,
    title: item.title,
    artistName: item.artistName,
    spec: `${item.artistName} · ${formatCategory(item.category)}`,
    priceText: formatPrice(item.currentPrice),
    originalPrice: item.originalPrice ? `¥${item.originalPrice.toLocaleString()}` : "",
    coverUrl: item.coverUrl || "",
    tag: item.tag || "",
    countdown: item.countdown || "",
    height: heights[index % heights.length],
    coverStyle: style
  }
}

// 50个作品数据
const ALL_WORKS = [
  { artworkId: 1, title: "静谧的山谷", artistName: "李明", category: "PAINTING", currentPrice: 12800, originalPrice: 15800, coverUrl: "https://picsum.photos/seed/art1/400/500", tag: "特价", countdown: "还剩 2 天" },
  { artworkId: 2, title: "城市光影系列", artistName: "王芳", category: "PRINT", currentPrice: 6800, coverUrl: "https://picsum.photos/seed/art2/400/500" },
  { artworkId: 3, title: "水墨山水", artistName: "张伟", category: "INK", currentPrice: 28000, coverUrl: "https://picsum.photos/seed/art3/400/500", tag: "新品" },
  { artworkId: 4, title: "抽象艺术 No.7", artistName: "刘涛", category: "PAINTING", currentPrice: 15800, originalPrice: 19800, coverUrl: "https://picsum.photos/seed/art4/400/500", tag: "特价" },
  { artworkId: 5, title: "海边日落", artistName: "陈静", category: "PAINTING", currentPrice: 8800, coverUrl: "https://picsum.photos/seed/art5/400/500" },
  { artworkId: 6, title: "雕塑作品 #3", artistName: "赵磊", category: "SCULPTURE", currentPrice: 45000, coverUrl: "https://picsum.photos/seed/art6/400/500", tag: "独家" },
  { artworkId: 7, title: "花卉系列", artistName: "孙丽", category: "PRINT", currentPrice: 5200, originalPrice: 6800, coverUrl: "https://picsum.photos/seed/art7/400/500" },
  { artworkId: 8, title: "竹林深处", artistName: "周杰", category: "INK", currentPrice: 19800, coverUrl: "https://picsum.photos/seed/art8/400/500", tag: "热卖" },
  { artworkId: 9, title: "星空之下", artistName: "吴敏", category: "PAINTING", currentPrice: 22800, originalPrice: 26800, coverUrl: "https://picsum.photos/seed/art9/400/500" },
  { artworkId: 10, title: "现代都市", artistName: "郑强", category: "PRINT", currentPrice: 7800, coverUrl: "https://picsum.photos/seed/art10/400/500", tag: "新品" },
  { artworkId: 11, title: "秋日私语", artistName: "林立", category: "PAINTING", currentPrice: 16800, coverUrl: "https://picsum.photos/seed/art11/400/500" },
  { artworkId: 12, title: "铜版画 #12", artistName: "黄丽", category: "PRINT", currentPrice: 9800, coverUrl: "https://picsum.photos/seed/art12/400/500", tag: "限量" },
  { artworkId: 13, title: "烟雨江南", artistName: "杨帆", category: "INK", currentPrice: 32000, originalPrice: 38000, coverUrl: "https://picsum.photos/seed/art13/400/500" },
  { artworkId: 14, title: "粉色幻想", artistName: "马超", category: "PAINTING", currentPrice: 12800, coverUrl: "https://picsum.photos/seed/art14/400/500" },
  { artworkId: 15, title: "青铜器系列", artistName: "徐磊", category: "SCULPTURE", currentPrice: 58000, coverUrl: "https://picsum.photos/seed/art15/400/500", tag: "新品" },
  { artworkId: 16, title: "黑白摄影", artistName: "钟华", category: "PRINT", currentPrice: 4200, coverUrl: "https://picsum.photos/seed/art16/400/500" },
  { artworkId: 17, title: "黄山云海", artistName: "何静", category: "INK", currentPrice: 45000, coverUrl: "https://picsum.photos/seed/art17/400/500", tag: "热卖" },
  { artworkId: 18, title: "晨曦微露", artistName: "曾伟", category: "PAINTING", currentPrice: 15800, originalPrice: 18800, coverUrl: "https://picsum.photos/seed/art18/400/500" },
  { artworkId: 19, title: "丝网版画", artistName: "丁丽", category: "PRINT", currentPrice: 6800, coverUrl: "https://picsum.photos/seed/art19/400/500" },
  { artworkId: 20, title: "木雕艺术", artistName: "梁勇", category: "SCULPTURE", currentPrice: 36000, coverUrl: "https://picsum.photos/seed/art20/400/500" },
  { artworkId: 21, title: "落日晚霞", artistName: "宋涛", category: "PAINTING", currentPrice: 18800, coverUrl: "https://picsum.photos/seed/art21/400/500", tag: "特价" },
  { artworkId: 22, title: "古韵新风", artistName: "卢敏", category: "INK", currentPrice: 22000, coverUrl: "https://picsum.photos/seed/art22/400/500" },
  { artworkId: 23, title: "石版画 #5", artistName: "许刚", category: "PRINT", currentPrice: 12000, coverUrl: "https://picsum.photos/seed/art23/400/500", tag: "新品" },
  { artworkId: 24, title: "夜的旋律", artistName: "钱丽", category: "PAINTING", currentPrice: 9800, originalPrice: 12800, coverUrl: "https://picsum.photos/seed/art24/400/500" },
  { artworkId: 25, title: "陶瓷雕塑", artistName: "蒋伟", category: "SCULPTURE", currentPrice: 28000, coverUrl: "https://picsum.photos/seed/art25/400/500" },
  { artworkId: 26, title: "春意盎然", artistName: "沈明", category: "PAINTING", currentPrice: 14800, coverUrl: "https://picsum.photos/seed/art26/400/500", tag: "热卖" },
  { artworkId: 27, title: "书法小品", artistName: "韩静", category: "INK", currentPrice: 8500, coverUrl: "https://picsum.photos/seed/art27/400/500" },
  { artworkId: 28, title: "综合版画", artistName: "冯强", category: "PRINT", currentPrice: 5600, originalPrice: 7200, coverUrl: "https://picsum.photos/seed/art28/400/500" },
  { artworkId: 29, title: "雪山之巅", artistName: "曹磊", category: "PAINTING", currentPrice: 32000, coverUrl: "https://picsum.photos/seed/art29/400/500" },
  { artworkId: 30, title: "装置艺术", artistName: "张莉", category: "SCULPTURE", currentPrice: 68000, coverUrl: "https://picsum.photos/seed/art30/400/500", tag: "独家" },
  { artworkId: 31, title: "金色年华", artistName: "程伟", category: "PAINTING", currentPrice: 18800, coverUrl: "https://picsum.photos/seed/art31/400/500" },
  { artworkId: 32, title: "水乡印象", artistName: "傅丽", category: "INK", currentPrice: 25800, coverUrl: "https://picsum.photos/seed/art32/400/500", tag: "新品" },
  { artworkId: 33, title: "限量复刻", artistName: "段勇", category: "PRINT", currentPrice: 18000, originalPrice: 22000, coverUrl: "https://picsum.photos/seed/art33/400/500" },
  { artworkId: 34, title: "梦幻泡影", artistName: "夏敏", category: "PAINTING", currentPrice: 12800, coverUrl: "https://picsum.photos/seed/art34/400/500" },
  { artworkId: 35, title: "玉雕摆件", artistName: "钟刚", category: "SCULPTURE", currentPrice: 88000, coverUrl: "https://picsum.photos/seed/art35/400/500", tag: "热卖" },
  { artworkId: 36, title: "暮色苍茫", artistName: "乔磊", category: "PAINTING", currentPrice: 15800, originalPrice: 19800, coverUrl: "https://picsum.photos/seed/art36/400/500" },
  { artworkId: 37, title: "行书书法", artistName: "翟丽", category: "INK", currentPrice: 12800, coverUrl: "https://picsum.photos/seed/art37/400/500" },
  { artworkId: 38, title: "数字版画", artistName: "方伟", category: "PRINT", currentPrice: 3800, coverUrl: "https://picsum.photos/seed/art38/400/500", tag: "特价" },
  { artworkId: 39, title: "荷塘月色", artistName: "康静", category: "PAINTING", currentPrice: 16800, coverUrl: "https://picsum.photos/seed/art39/400/500" },
  { artworkId: 40, title: "铁艺雕塑", artistName: "史强", category: "SCULPTURE", currentPrice: 42000, coverUrl: "https://picsum.photos/seed/art40/400/500" },
  { artworkId: 41, title: "都市喧嚣", artistName: "薛磊", category: "PAINTING", currentPrice: 22800, coverUrl: "https://picsum.photos/seed/art41/400/500", tag: "新品" },
  { artworkId: 42, title: "草书长卷", artistName: "叶丽", category: "INK", currentPrice: 38000, originalPrice: 45000, coverUrl: "https://picsum.photos/seed/art42/400/500" },
  { artworkId: 43, title: "石版艺术", artistName: "蒋伟", category: "PRINT", currentPrice: 15800, coverUrl: "https://picsum.photos/seed/art43/400/500" },
  { artworkId: 44, title: "童趣天真", artistName: "许静", category: "PAINTING", currentPrice: 8800, coverUrl: "https://picsum.photos/seed/art44/400/500" },
  { artworkId: 45, title: "玻璃艺术", artistName: "陆强", category: "SCULPTURE", currentPrice: 35000, coverUrl: "https://picsum.photos/seed/art45/400/500", tag: "热卖" },
  { artworkId: 46, title: "四季如歌", artistName: "杜磊", category: "PAINTING", currentPrice: 19800, originalPrice: 23800, coverUrl: "https://picsum.photos/seed/art46/400/500" },
  { artworkId: 47, title: "写意山水", artistName: "苏丽", category: "INK", currentPrice: 42000, coverUrl: "https://picsum.photos/seed/art47/400/500", tag: "精品" },
  { artworkId: 48, title: "木刻版画", artistName: "韩伟", category: "PRINT", currentPrice: 9800, coverUrl: "https://picsum.photos/seed/art48/400/500" },
  { artworkId: 49, title: "生命之树", artistName: "杨静", category: "PAINTING", currentPrice: 25800, coverUrl: "https://picsum.photos/seed/art49/400/500" },
  { artworkId: 50, title: "玉雕挂件", artistName: "朱强", category: "SCULPTURE", currentPrice: 58000, coverUrl: "https://picsum.photos/seed/art50/400/500", tag: "限量" }
]

const BANNERS = [
  { id: 1, title: "当代艺术展", subtitle: "2026春季大促 全场8折起", date: "2026.04.01 - 04.30", coverUrl: "https://picsum.photos/seed/banner1/750/400", coverStyle: { background: "linear-gradient(135deg, #8f6552, #3a2925)", glow: "#d9b287" } },
  { id: 2, title: "新锐艺术家专区", subtitle: "发现下一个艺术大师", date: "持续更新", coverUrl: "https://picsum.photos/seed/banner2/750/400", coverStyle: { background: "linear-gradient(135deg, #7c665c, #2d231f)", glow: "#d9cec0" } },
  { id: 3, title: "版画专场", subtitle: "收藏级限量版画", date: "本周精选", coverUrl: "https://picsum.photos/seed/banner3/750/400", coverStyle: { background: "linear-gradient(135deg, #a0785c, #3f2e27)", glow: "#e2c39e" } }
]

function normalizeBanner(item) {
  const presets = [
    { background: "linear-gradient(135deg, #8f6552, #3a2925)", glow: "#d9b287" },
    { background: "linear-gradient(135deg, #7c665c, #2d231f)", glow: "#d9cec0" },
    { background: "linear-gradient(135deg, #a0785c, #3f2e27)", glow: "#e2c39e" }
  ]
  return {
    id: item.id,
    title: item.title,
    subtitle: item.subtitle || item.target || "查看详情",
    date: item.date || item.updatedAt || "",
    coverUrl: item.coverUrl || item.imageUrl || "",
    coverStyle: presets[item.id % presets.length]
  }
}

Page({
  data: {
    loading: true,
    loadingMore: false,
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
    hasMore: true,
    page: 1,
    allWorks: []
  },

  onShow() {
    this.loadHome()
  },

  onReachBottom() {
    this.loadMore()
  },

  async loadHome() {
    this.setData({ loading: true, error: "", page: 1, allWorks: [] })
    try {
      // 从 API 获取 Banner 数据
      let banners = []
      try {
        banners = await api.request({ url: "/home/banners", method: "GET" })
      } catch (e) {
        banners = []
      }
      if (!banners || banners.length === 0) {
        banners = BANNERS
      }
      const normalizedBanners = banners.map((item, index) => normalizeBanner({ ...item, id: item.id || (index + 1) }))

      // 从 API 获取作品数据
      let works = []
      try {
        works = await api.request({ url: "/works", method: "GET" })
      } catch (e) {
        works = []
      }

      if (!works || works.length === 0) {
        works = ALL_WORKS
      }

      const normalizedWorks = works.map((item, index) => normalizeWork(item, index))
      const { left, right } = this.splitToColumns(normalizedWorks, PAGE_SIZE)
      
      this.setData({
        loading: false,
        error: "",
        bannerItems: normalizedBanners,
        leftColumn: left,
        rightColumn: right,
        allWorks: normalizedWorks,
        hasMore: normalizedWorks.length >= PAGE_SIZE,
        page: 1
      })
    } catch (error) {
      // 使用本地数据
      const normalizedWorks = ALL_WORKS.map((item, index) => normalizeWork(item, index))
      const { left, right } = this.splitToColumns(normalizedWorks, PAGE_SIZE)
      
      this.setData({
        loading: false,
        error: "",
        bannerItems: BANNERS,
        leftColumn: left,
        rightColumn: right,
        allWorks: normalizedWorks,
        hasMore: normalizedWorks.length >= PAGE_SIZE,
        page: 1
      })
    }
  },

  loadMore() {
    if (this.data.loadingMore || !this.data.hasMore) return
    
    this.setData({ loadingMore: true })
    
    const currentPage = this.data.page
    const nextPage = currentPage + 1
    const start = currentPage * PAGE_SIZE
    const end = nextPage * PAGE_SIZE
    
    const moreWorks = this.data.allWorks.slice(start, end)
    
    if (moreWorks.length === 0) {
      this.setData({ loadingMore: false, hasMore: false })
      return
    }

    const newLeft = []
    const newRight = []
    
    // 交错分配到两列
    moreWorks.forEach((item, index) => {
      if (index % 2 === 0) {
        newLeft.push(item)
      } else {
        newRight.push(item)
      }
    })

    this.setData({
      loadingMore: false,
      page: nextPage,
      leftColumn: [...this.data.leftColumn, ...newLeft],
      rightColumn: [...this.data.rightColumn, ...newRight],
      hasMore: end < this.data.allWorks.length
    })
  },

  splitToColumns(works, limit) {
    const left = []
    const right = []
    const limitedWorks = limit ? works.slice(0, limit) : works
    
    limitedWorks.forEach((item, index) => {
      if (index % 2 === 0) {
        left.push(item)
      } else {
        right.push(item)
      }
    })
    return { left, right }
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
