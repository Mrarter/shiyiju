const api = require("../../utils/api")
const { normalizeImageUrl, normalizeImageFields, getArtworkCoverPlaceholder, getBannerPlaceholder } = require("../../utils/imageUrl")

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

function formatMaterial(category) {
  const map = {
    PAINTING: "布面油画",
    PRINT: "丝网版画",
    INK: "宣纸水墨",
    SCULPTURE: "青铜雕塑",
    OTHER: "综合材料"
  }
  return map[category] || "综合材料"
}

function formatSpec(item) {
  const categoryText = formatCategory(item.category)
  const materialText = formatMaterial(item.category)
  const width = item.widthCm || "-"
  const height = item.heightCm || "-"
  const year = item.creationYear ? `${item.creationYear}年` : ""
  return `${categoryText}（${materialText}）/${width}cm×${height}cm/${year}`
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

function normalizeWork(item, index, favorites = []) {
  const style = buildCoverStyle(index)
  const heights = [320, 400, 280, 360, 440, 300, 380, 260, 420, 340]
  return {
    id: item.artworkId,
    title: item.title,
    artistName: item.artistName,
    spec: formatSpec(item),
    priceText: formatPrice(item.currentPrice),
    originalPrice: item.originalPrice ? `¥${item.originalPrice.toLocaleString()}` : "",
    coverUrl: normalizeImageUrl(item.coverUrl, getArtworkCoverPlaceholder(item.artworkId)),
    tag: item.tag || "",
    countdown: item.countdown || "",
    height: heights[index % heights.length],
    coverStyle: style,
    // 权重信息
    weight: item.weight || 0,
    viewCount: item.viewCount || 0,
    favoriteCount: item.favoriteCount || 0,
    cartCount: item.cartCount || 0,
    // 收藏状态
    isFavorited: favorites.includes(item.artworkId)
  }
}

// 计算作品权重得分
function calculateWeight(work) {
  // 权重公式：后台配置权重 * 10 + 点击次数 * 1 + 收藏次数 * 3 + 加入购物车次数 * 5
  const configWeight = (work.adminWeight || 1) * 10
  const viewScore = (work.viewCount || 0) * 1
  const favoriteScore = (work.favoriteCount || 0) * 3
  const cartScore = (work.cartCount || 0) * 5
  return configWeight + viewScore + favoriteScore + cartScore
}

// 按权重排序
function sortByWeight(works) {
  return works.map((work, index) => ({
    ...work,
    _weight: calculateWeight(work),
    _originalIndex: index
  })).sort((a, b) => {
    // 权重高的在前，权重相同按原始顺序
    if (b._weight !== a._weight) {
      return b._weight - a._weight
    }
    return a._originalIndex - b._originalIndex
  }).map(work => {
    const { _weight, _originalIndex, ...rest } = work
    return rest
  })
}

// 随机打乱数组
function shuffleArray(array) {
  const shuffled = [...array]
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]
  }
  return shuffled
}

// 50个作品数据
const ALL_WORKS = [
  { artworkId: 1, title: "静谧的山谷", artistName: "李明", category: "PAINTING", currentPrice: 12800, originalPrice: 15800, coverUrl: "https://picsum.photos/seed/art1/400/500", widthCm: 80, heightCm: 100, creationYear: 2025, tag: "特价", countdown: "还剩 2 天" },
  { artworkId: 2, title: "城市光影系列", artistName: "王芳", category: "PRINT", currentPrice: 6800, coverUrl: "https://picsum.photos/seed/art2/400/500", widthCm: 50, heightCm: 70, creationYear: 2024 },
  { artworkId: 3, title: "水墨山水", artistName: "张伟", category: "INK", currentPrice: 28000, coverUrl: "https://picsum.photos/seed/art3/400/500", widthCm: 138, heightCm: 69, creationYear: 2025, tag: "新品" },
  { artworkId: 4, title: "抽象艺术 No.7", artistName: "刘涛", category: "PAINTING", currentPrice: 15800, originalPrice: 19800, coverUrl: "https://picsum.photos/seed/art4/400/500", widthCm: 60, heightCm: 80, creationYear: 2024, tag: "特价" },
  { artworkId: 5, title: "海边日落", artistName: "陈静", category: "PAINTING", currentPrice: 8800, coverUrl: "https://picsum.photos/seed/art5/400/500", widthCm: 40, heightCm: 50, creationYear: 2025 },
  { artworkId: 6, title: "雕塑作品 #3", artistName: "赵磊", category: "SCULPTURE", currentPrice: 45000, coverUrl: "https://picsum.photos/seed/art6/400/500", widthCm: 25, heightCm: 45, creationYear: 2023, tag: "独家" },
  { artworkId: 7, title: "花卉系列", artistName: "孙丽", category: "PRINT", currentPrice: 5200, originalPrice: 6800, coverUrl: "https://picsum.photos/seed/art7/400/500", widthCm: 45, heightCm: 60, creationYear: 2024 },
  { artworkId: 8, title: "竹林深处", artistName: "周杰", category: "INK", currentPrice: 19800, coverUrl: "https://picsum.photos/seed/art8/400/500", widthCm: 68, heightCm: 136, creationYear: 2024, tag: "热卖" },
  { artworkId: 9, title: "星空之下", artistName: "吴敏", category: "PAINTING", currentPrice: 22800, originalPrice: 26800, coverUrl: "https://picsum.photos/seed/art9/400/500", widthCm: 90, heightCm: 120, creationYear: 2025 },
  { artworkId: 10, title: "现代都市", artistName: "郑强", category: "PRINT", currentPrice: 7800, coverUrl: "https://picsum.photos/seed/art10/400/500", widthCm: 55, heightCm: 75, creationYear: 2025, tag: "新品" },
  { artworkId: 11, title: "秋日私语", artistName: "林立", category: "PAINTING", currentPrice: 16800, coverUrl: "https://picsum.photos/seed/art11/400/500", widthCm: 50, heightCm: 60, creationYear: 2024 },
  { artworkId: 12, title: "铜版画 #12", artistName: "黄丽", category: "PRINT", currentPrice: 9800, coverUrl: "https://picsum.photos/seed/art12/400/500", widthCm: 40, heightCm: 55, creationYear: 2023, tag: "限量" },
  { artworkId: 13, title: "烟雨江南", artistName: "杨帆", category: "INK", currentPrice: 32000, originalPrice: 38000, coverUrl: "https://picsum.photos/seed/art13/400/500", widthCm: 180, heightCm: 97, creationYear: 2024, tag: "热卖" },
  { artworkId: 14, title: "粉色幻想", artistName: "马超", category: "PAINTING", currentPrice: 12800, coverUrl: "https://picsum.photos/seed/art14/400/500", widthCm: 70, heightCm: 90, creationYear: 2025 },
  { artworkId: 15, title: "青铜器系列", artistName: "徐磊", category: "SCULPTURE", currentPrice: 58000, coverUrl: "https://picsum.photos/seed/art15/400/500", widthCm: 30, heightCm: 50, creationYear: 2023, tag: "新品" },
  { artworkId: 16, title: "黑白摄影", artistName: "钟华", category: "PRINT", currentPrice: 4200, coverUrl: "https://picsum.photos/seed/art16/400/500", widthCm: 30, heightCm: 40, creationYear: 2024 },
  { artworkId: 17, title: "黄山云海", artistName: "何静", category: "INK", currentPrice: 45000, coverUrl: "https://picsum.photos/seed/art17/400/500", widthCm: 245, heightCm: 125, creationYear: 2025, tag: "热卖" },
  { artworkId: 18, title: "晨曦微露", artistName: "曾伟", category: "PAINTING", currentPrice: 15800, originalPrice: 18800, coverUrl: "https://picsum.photos/seed/art18/400/500", widthCm: 80, heightCm: 100, creationYear: 2024 },
  { artworkId: 19, title: "丝网版画", artistName: "丁丽", category: "PRINT", currentPrice: 6800, coverUrl: "https://picsum.photos/seed/art19/400/500", widthCm: 50, heightCm: 65, creationYear: 2025 },
  { artworkId: 20, title: "木雕艺术", artistName: "梁勇", category: "SCULPTURE", currentPrice: 36000, coverUrl: "https://picsum.photos/seed/art20/400/500", widthCm: 20, heightCm: 55, creationYear: 2023 },
  { artworkId: 21, title: "落日晚霞", artistName: "宋涛", category: "PAINTING", currentPrice: 18800, coverUrl: "https://picsum.photos/seed/art21/400/500", widthCm: 60, heightCm: 80, creationYear: 2025, tag: "特价" },
  { artworkId: 22, title: "古韵新风", artistName: "卢敏", category: "INK", currentPrice: 22000, coverUrl: "https://picsum.photos/seed/art22/400/500", widthCm: 68, heightCm: 136, creationYear: 2024 },
  { artworkId: 23, title: "石版画 #5", artistName: "许刚", category: "PRINT", currentPrice: 12000, coverUrl: "https://picsum.photos/seed/art23/400/500", widthCm: 45, heightCm: 60, creationYear: 2025, tag: "新品" },
  { artworkId: 24, title: "夜的旋律", artistName: "钱丽", category: "PAINTING", currentPrice: 9800, originalPrice: 12800, coverUrl: "https://picsum.photos/seed/art24/400/500", widthCm: 50, heightCm: 65, creationYear: 2024 },
  { artworkId: 25, title: "陶瓷雕塑", artistName: "蒋伟", category: "SCULPTURE", currentPrice: 28000, coverUrl: "https://picsum.photos/seed/art25/400/500", widthCm: 35, heightCm: 40, creationYear: 2023 },
  { artworkId: 26, title: "春意盎然", artistName: "沈明", category: "PAINTING", currentPrice: 14800, coverUrl: "https://picsum.photos/seed/art26/400/500", widthCm: 70, heightCm: 90, creationYear: 2025, tag: "热卖" },
  { artworkId: 27, title: "书法小品", artistName: "韩静", category: "INK", currentPrice: 8500, coverUrl: "https://picsum.photos/seed/art27/400/500", widthCm: 34, heightCm: 136, creationYear: 2024 },
  { artworkId: 28, title: "综合版画", artistName: "冯强", category: "PRINT", currentPrice: 5600, originalPrice: 7200, coverUrl: "https://picsum.photos/seed/art28/400/500", widthCm: 40, heightCm: 50, creationYear: 2025 },
  { artworkId: 29, title: "雪山之巅", artistName: "曹磊", category: "PAINTING", currentPrice: 32000, coverUrl: "https://picsum.photos/seed/art29/400/500", widthCm: 100, heightCm: 80, creationYear: 2024 },
  { artworkId: 30, title: "装置艺术", artistName: "张莉", category: "SCULPTURE", currentPrice: 68000, coverUrl: "https://picsum.photos/seed/art30/400/500", widthCm: 80, heightCm: 120, creationYear: 2023, tag: "独家" },
  { artworkId: 31, title: "金色年华", artistName: "程伟", category: "PAINTING", currentPrice: 18800, coverUrl: "https://picsum.photos/seed/art31/400/500", widthCm: 60, heightCm: 75, creationYear: 2025 },
  { artworkId: 32, title: "水乡印象", artistName: "傅丽", category: "INK", currentPrice: 25800, coverUrl: "https://picsum.photos/seed/art32/400/500", widthCm: 97, heightCm: 180, creationYear: 2024, tag: "新品" },
  { artworkId: 33, title: "限量复刻", artistName: "段勇", category: "PRINT", currentPrice: 18000, originalPrice: 22000, coverUrl: "https://picsum.photos/seed/art33/400/500", widthCm: 50, heightCm: 70, creationYear: 2023 },
  { artworkId: 34, title: "梦幻泡影", artistName: "夏敏", category: "PAINTING", currentPrice: 12800, coverUrl: "https://picsum.photos/seed/art34/400/500", widthCm: 80, heightCm: 100, creationYear: 2025 },
  { artworkId: 35, title: "玉雕摆件", artistName: "钟刚", category: "SCULPTURE", currentPrice: 88000, coverUrl: "https://picsum.photos/seed/art35/400/500", widthCm: 25, heightCm: 35, creationYear: 2023, tag: "热卖" },
  { artworkId: 36, title: "暮色苍茫", artistName: "乔磊", category: "PAINTING", currentPrice: 15800, originalPrice: 19800, coverUrl: "https://picsum.photos/seed/art36/400/500", widthCm: 70, heightCm: 85, creationYear: 2024 },
  { artworkId: 37, title: "行书书法", artistName: "翟丽", category: "INK", currentPrice: 12800, coverUrl: "https://picsum.photos/seed/art37/400/500", widthCm: 34, heightCm: 138, creationYear: 2025 },
  { artworkId: 38, title: "数字版画", artistName: "方伟", category: "PRINT", currentPrice: 3800, coverUrl: "https://picsum.photos/seed/art38/400/500", widthCm: 40, heightCm: 50, creationYear: 2025, tag: "特价" },
  { artworkId: 39, title: "荷塘月色", artistName: "康静", category: "PAINTING", currentPrice: 16800, coverUrl: "https://picsum.photos/seed/art39/400/500", widthCm: 60, heightCm: 80, creationYear: 2024 },
  { artworkId: 40, title: "铁艺雕塑", artistName: "史强", category: "SCULPTURE", currentPrice: 42000, coverUrl: "https://picsum.photos/seed/art40/400/500", widthCm: 40, heightCm: 60, creationYear: 2023 },
  { artworkId: 41, title: "都市喧嚣", artistName: "薛磊", category: "PAINTING", currentPrice: 22800, coverUrl: "https://picsum.photos/seed/art41/400/500", widthCm: 80, heightCm: 100, creationYear: 2025, tag: "新品" },
  { artworkId: 42, title: "草书长卷", artistName: "叶丽", category: "INK", currentPrice: 38000, originalPrice: 45000, coverUrl: "https://picsum.photos/seed/art42/400/500", widthCm: 50, heightCm: 800, creationYear: 2024 },
  { artworkId: 43, title: "石版艺术", artistName: "蒋伟", category: "PRINT", currentPrice: 15800, coverUrl: "https://picsum.photos/seed/art43/400/500", widthCm: 45, heightCm: 55, creationYear: 2023 },
  { artworkId: 44, title: "童趣天真", artistName: "许静", category: "PAINTING", currentPrice: 8800, coverUrl: "https://picsum.photos/seed/art44/400/500", widthCm: 50, heightCm: 60, creationYear: 2025 },
  { artworkId: 45, title: "玻璃艺术", artistName: "陆强", category: "SCULPTURE", currentPrice: 35000, coverUrl: "https://picsum.photos/seed/art45/400/500", widthCm: 30, heightCm: 45, creationYear: 2023, tag: "热卖" },
  { artworkId: 46, title: "四季如歌", artistName: "杜磊", category: "PAINTING", currentPrice: 19800, originalPrice: 23800, coverUrl: "https://picsum.photos/seed/art46/400/500", widthCm: 70, heightCm: 90, creationYear: 2024 },
  { artworkId: 47, title: "写意山水", artistName: "苏丽", category: "INK", currentPrice: 42000, coverUrl: "https://picsum.photos/seed/art47/400/500", widthCm: 138, heightCm: 69, creationYear: 2025, tag: "精品" },
  { artworkId: 48, title: "木刻版画", artistName: "韩伟", category: "PRINT", currentPrice: 9800, coverUrl: "https://picsum.photos/seed/art48/400/500", widthCm: 40, heightCm: 55, creationYear: 2024 },
  { artworkId: 49, title: "生命之树", artistName: "杨静", category: "PAINTING", currentPrice: 25800, coverUrl: "https://picsum.photos/seed/art49/400/500", widthCm: 60, heightCm: 90, creationYear: 2025 },
  { artworkId: 50, title: "玉雕挂件", artistName: "朱强", category: "SCULPTURE", currentPrice: 58000, coverUrl: "https://picsum.photos/seed/art50/400/500", widthCm: 15, heightCm: 25, creationYear: 2023, tag: "限量" }
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
  // 确保 coverUrl 有值，否则使用 imageUrl
  let coverUrl = item.coverUrl || item.imageUrl || ""
  // 使用统一的图片处理函数
  const normalizedUrl = normalizeImageUrl(coverUrl, getBannerPlaceholder(item.id))
  const hasValidImage = normalizedUrl && normalizedUrl.startsWith("http")
  const presetIdx = (item.id || 1) % presets.length
  return {
    id: item.id,
    title: item.title || "",
    subtitle: item.subtitle || item.target || item.title || "查看详情",
    date: item.date || item.updatedAt || "",
    // 如果有有效图片URL就用它，否则用默认图片
    coverUrl: hasValidImage ? normalizedUrl : getBannerPlaceholder(item.id),
    coverStyle: presets[presetIdx],
    fallbackBg: presets[presetIdx].background,
    fallbackGlow: presets[presetIdx].glow
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
    allWorks: [],
    favorites: [] // 收藏的作品ID列表
  },

  onLoad() {
    this.loadFavorites()
    this.loadHome()
  },

  onShow() {
    // 只刷新收藏状态，保留当前位置和列表
    this.loadFavorites()
    // 不再刷新整个首页，避免丢失滚动位置
  },

  onPullDownRefresh() {
    this.loadHome(true).then(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    this.loadMore()
  },

  async loadHome(isRefresh = false) {
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

      // 下拉刷新时随机显示，首次加载按权重排序
      let sortedWorks = isRefresh ? shuffleArray(works) : sortByWeight(works)
      const favorites = this.data.favorites || []
      const normalizedWorks = sortedWorks.map((item, index) => normalizeWork(item, index, favorites))
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
      let sortedWorks = isRefresh ? shuffleArray(ALL_WORKS) : sortByWeight(ALL_WORKS)
      const favorites = this.data.favorites || []
      const normalizedWorks = sortedWorks.map((item, index) => normalizeWork(item, index, favorites))
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

  async loadMore() {
    if (this.data.loadingMore) return
    
    this.setData({ loadingMore: true })
    
    try {
      // 获取所有作品
      const works = await api.request({ url: "/works", method: "GET" })
      const allWorks = works || ALL_WORKS
      
      // 获取已显示作品的ID
      const displayedIds = new Set(this.data.allWorks.map(w => w.id))
      
      // 分离未显示和已显示的作品
      const unshownWorks = allWorks.filter(w => !displayedIds.has(w.id))
      const shownWorks = allWorks.filter(w => displayedIds.has(w.id))
      
      // 优先从未显示的作品中随机获取，不够则从已显示中随机补充
      const shuffledUnshown = shuffleArray(unshownWorks)
      let moreWorks = shuffledUnshown.slice(0, PAGE_SIZE)
      
      // 如果未显示的不够 PAGE_SIZE，从已显示的补充
      if (moreWorks.length < PAGE_SIZE && shownWorks.length > 0) {
        const needed = PAGE_SIZE - moreWorks.length
        const shuffledShown = shuffleArray(shownWorks).slice(0, needed)
        moreWorks = [...moreWorks, ...shuffledShown]
      }
      
      // 如果还是没有作品（全是已显示），也随机补充
      if (moreWorks.length === 0 && allWorks.length > 0) {
        moreWorks = shuffleArray(allWorks).slice(0, PAGE_SIZE)
      }
      
      if (moreWorks.length === 0) {
        this.setData({ loadingMore: false })
        return
      }
      
      const favorites = this.data.favorites || []
      const nextStartIndex = this.data.page * PAGE_SIZE
      const normalizedMoreWorks = moreWorks.map((item, index) => 
        normalizeWork(item, nextStartIndex + index, favorites)
      )

      const newLeft = []
      const newRight = []
      
      // 交错分配到两列
      normalizedMoreWorks.forEach((item, index) => {
        if (index % 2 === 0) {
          newLeft.push(item)
        } else {
          newRight.push(item)
        }
      })

      this.setData({
        loadingMore: false,
        page: this.data.page + 1,
        leftColumn: [...this.data.leftColumn, ...newLeft],
        rightColumn: [...this.data.rightColumn, ...newRight],
        allWorks: [...this.data.allWorks, ...normalizedMoreWorks],
        hasMore: true  // 始终允许加载更多
      })
    } catch (error) {
      this.setData({ loadingMore: false })
    }
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
    // 记录点击次数
    this.recordClick(artworkId)
    wx.navigateTo({ url: `/pages/artwork/detail?id=${artworkId}` })
  },

  handleRetry() {
    this.loadHome(false)
  },

  // 加载收藏列表
  loadFavorites() {
    const favorites = wx.getStorageSync('favorites') || []
    this.setData({ favorites })
  },

  // 切换收藏状态
  toggleFavorite(e) {
    const artworkId = e.currentTarget.dataset.id
    const favorites = [...this.data.favorites]
    const index = favorites.indexOf(artworkId)
    
    if (index > -1) {
      favorites.splice(index, 1)
      wx.showToast({ title: '已取消收藏', icon: 'success' })
    } else {
      favorites.push(artworkId)
      wx.showToast({ title: '收藏成功', icon: 'success' })
    }
    
    wx.setStorageSync('favorites', favorites)
    this.setData({ favorites })
    
    // 更新作品列表的收藏状态
    const allWorks = this.data.allWorks.map(work => {
      if (work.id === artworkId) {
        return { ...work, isFavorited: !work.isFavorited }
      }
      return work
    })
    
    const { left, right } = this.splitToColumns(allWorks, PAGE_SIZE)
    this.setData({
      leftColumn: left,
      rightColumn: right,
      allWorks: allWorks
    })
    
    // 调用API记录收藏
    this.syncFavoriteToServer(artworkId, index === -1)
  },

  // 记录点击
  recordClick(artworkId) {
    // 本地记录
    const clicks = wx.getStorageSync('artworkClicks') || {}
    clicks[artworkId] = (clicks[artworkId] || 0) + 1
    wx.setStorageSync('artworkClicks', clicks)
    
    // 调用API
    api.request({
      url: `/works/${artworkId}/click`,
      method: "POST"
    }).catch(() => {})
  },

  // 同步收藏到服务器
  syncFavoriteToServer(artworkId, isFavorite) {
    api.request({
      url: `/works/${artworkId}/favorite`,
      method: "POST",
      data: { favorite: isFavorite }
    }).catch(() => {})
  }
})
