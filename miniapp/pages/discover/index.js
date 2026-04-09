const api = require("../../utils/api")

function formatPrice(value) {
  if (value === null || value === undefined || value === "") {
    return "价格待定"
  }
  const numeric = Number(value)
  if (Number.isNaN(numeric)) return `¥ ${value}`
  return `¥ ${numeric.toLocaleString("zh-CN")}`
}

function formatCategory(category) {
  const map = {
    PAINTING: "油画（布面油画）",
    PRINT: "版画",
    INK: "国画",
    SCULPTURE: "雕塑",
    OTHER: "综合材料"
  }
  return map[category] || "综合材料"
}

function buildCardVisual(index) {
  const presets = [
    {
      ratio: "landscape",
      background: "linear-gradient(135deg, #77706a 0%, #3a3531 45%, #181514 100%)",
      glow: "#f2e6d4",
      mountain: "#4b443f"
    },
    {
      ratio: "portrait",
      background: "linear-gradient(160deg, #cc9a7b 0%, #8e5f4f 38%, #2b221d 100%)",
      glow: "#ffd9be",
      mountain: "#a97862"
    },
    {
      ratio: "square",
      background: "linear-gradient(140deg, #d7d1bf 0%, #9f997d 42%, #49453d 100%)",
      glow: "#f6efde",
      mountain: "#7c725e"
    },
    {
      ratio: "portrait",
      background: "linear-gradient(145deg, #9b90a2 0%, #5b4d68 42%, #2a232f 100%)",
      glow: "#f7d8ca",
      mountain: "#7d688b"
    }
  ]
  return presets[index % presets.length]
}

function buildMeta(index, item, artistLookup) {
  const sizePresets = [
    "80×40cm / 2026",
    "100×100cm / 2019",
    "80×66cm / 2026",
    "20.5×5×5cm / 2026",
    "88×68.5cm / 2026",
    "30×60cm / 2025",
    "50×50cm / 2026"
  ]
  const slotPresets = [
    { primary: "专场", secondary: "大作" },
    { primary: "专场", secondary: "童话、神话与史诗" },
    { primary: "专场", secondary: "杏月·锐藏" }
  ]
  const deadlinePresets = [
    "4月12日 23:04 截拍",
    "本周六 23:30 截拍",
    "3天22小时后 结束",
    "本周六 22:44 截拍",
    "4月12日 22:36 截拍",
    "4月12日 23:12 截拍"
  ]
  const discountPresets = ["", "", "已优惠¥3,276", "", "", ""]
  const artist = artistLookup[item.artistId] || {}
  return {
    slot: slotPresets[index % slotPresets.length],
    sizeText: sizePresets[index % sizePresets.length],
    deadlineText: deadlinePresets[index % deadlinePresets.length],
    discountText: discountPresets[index % discountPresets.length],
    artistSlogan: artist.slogan || ""
  }
}

function normalizeArtwork(item, index, artistLookup) {
  const visual = buildCardVisual(index)
  const meta = buildMeta(index, item, artistLookup)
  return {
    id: item.artworkId,
    artworkId: item.artworkId,
    title: `${item.artistName}｜${item.title}`,
    artistName: item.artistName,
    slotPrimary: meta.slot.primary,
    slotSecondary: meta.slot.secondary,
    categoryText: formatCategory(item.category),
    metaText: `${formatCategory(item.category)} / ${meta.sizeText}`,
    priceText: formatPrice(item.currentPrice),
    deadlineText: meta.deadlineText,
    discountText: meta.discountText,
    coverUrl: item.coverUrl || "",
    followed: false,
    visual,
    artistSlogan: meta.artistSlogan
  }
}

function splitColumns(items) {
  const left = []
  const right = []
  items.forEach((item, index) => {
    if (index % 2 === 0) {
      left.push(item)
    } else {
      right.push(item)
    }
  })
  return { left, right }
}

Page({
  data: {
    filters: ["全部", "专场", "大作", "新锐"],
    activeFilter: "全部",
    loading: true,
    error: "",
    allFeedItems: [],
    feedItems: [],
    leftColumn: [],
    rightColumn: [],
    visibleCount: 0,
    pageSize: 6,
    loadingMore: false,
    hasMore: false
  },

  onShow() {
    this.loadDiscoverData()
  },

  async loadDiscoverData() {
    this.setData({ loading: true, error: "" })
    try {
      const [artists, artworks] = await Promise.all([
        api.request({ url: "/artists/recommend?limit=6", method: "GET" }),
        api.request({ url: "/works", method: "GET" })
      ])
      const artistLookup = {}
      ;(artists || []).forEach((item) => {
        artistLookup[item.artistId] = item
      })
      const feedItems = (artworks || []).map((item, index) => normalizeArtwork(item, index, artistLookup))
      this.setData({
        allFeedItems: feedItems,
        visibleCount: this.data.pageSize
      })
      this.setFeed(undefined, this.data.activeFilter)
      this.setData({
        loading: false,
        error: ""
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "发现页加载失败"
      })
    }
  },

  setFeed(feedItems, filter) {
    const sourceItems = typeof feedItems === "undefined" ? this.data.allFeedItems : feedItems
    const activeFilter = filter || this.data.activeFilter
    const filtered = sourceItems.filter((item, index) => {
      if (activeFilter === "全部") return true
      if (activeFilter === "专场") return item.slotPrimary === "专场"
      if (activeFilter === "大作") return index % 2 === 0
      if (activeFilter === "新锐") return item.artistSlogan || item.slotSecondary === "杏月·锐藏"
      return true
    })
    const visibleItems = filtered.slice(0, this.data.visibleCount)
    const columns = splitColumns(visibleItems)
    this.setData({
      allFeedItems: sourceItems,
      feedItems: visibleItems,
      activeFilter,
      leftColumn: columns.left,
      rightColumn: columns.right,
      hasMore: filtered.length > visibleItems.length
    })
  },

  handleTabTap(event) {
    this.setData({
      visibleCount: this.data.pageSize,
      loadingMore: false
    })
    this.setFeed(undefined, event.currentTarget.dataset.filter)
  },

  goArtworkDetail(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    if (!artworkId) return
    wx.navigateTo({ url: `/pages/artwork/detail?id=${artworkId}` })
  },

  handleFollowTap(event) {
    const artworkId = event.currentTarget.dataset.artworkId
    const nextItems = this.data.allFeedItems.map((item) => {
      if (item.artworkId !== artworkId) return item
      return {
        ...item,
        followed: !item.followed
      }
    })
    this.setFeed(nextItems, this.data.activeFilter)
  },

  handleRetry() {
    this.loadDiscoverData()
  },

  handleSearchTap() {
    wx.showToast({ title: "搜索入口整理中", icon: "none" })
  },

  handleCreateTap() {
    wx.switchTab({ url: "/pages/publish/index" })
  },

  onReachBottom() {
    if (this.data.loading || this.data.loadingMore || !this.data.hasMore) return
    this.setData({
      loadingMore: true
    })
    setTimeout(() => {
      this.setData({
        visibleCount: this.data.visibleCount + this.data.pageSize,
        loadingMore: false
      })
      this.setFeed()
    }, 220)
  },

  onPullDownRefresh() {
    this.loadDiscoverData().finally(() => {
      wx.stopPullDownRefresh()
    })
  }
})
