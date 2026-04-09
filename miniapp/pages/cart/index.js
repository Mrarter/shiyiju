const api = require("../../utils/api")

Page({
  data: {
    loading: true,
    error: "",
    isEditMode: false,
    cartItems: [],
    allSelected: false,
    totalPrice: "0.00",
    selectedCount: 0,
    recommendProducts: []
  },

  onShow() {
    this.loadCart()
  },

  async loadCart() {
    this.setData({ loading: true, error: "" })
    try {
      // 模拟购物车数据
      const mockItems = [
        {
          id: 1,
          title: "静谧的山谷",
          artistName: "李明",
          price: 12800,
          quantity: 1,
          stock: 5,
          coverUrl: "https://picsum.photos/seed/art1/400/500",
          widthCm: 80,
          heightCm: 100,
          creationYear: 2025,
          category: "PAINTING",
          selected: false
        },
        {
          id: 2,
          title: "城市光影系列",
          artistName: "王芳",
          price: 6800,
          quantity: 2,
          stock: 3,
          coverUrl: "https://picsum.photos/seed/art2/400/500",
          widthCm: 50,
          heightCm: 70,
          creationYear: 2024,
          category: "PRINT",
          selected: false
        },
        {
          id: 3,
          title: "水墨山水",
          artistName: "张伟",
          price: 28000,
          quantity: 1,
          stock: 1,
          coverUrl: "https://picsum.photos/seed/art3/400/500",
          widthCm: 138,
          heightCm: 69,
          creationYear: 2025,
          category: "INK",
          selected: false
        }
      ]

      // 模拟推荐作品数据 - 使用首页存在的作品ID
      const recommendProducts = [
        { id: 4, title: "抽象艺术 No.7", artistName: "刘涛", price: "15,800", coverUrl: "https://picsum.photos/seed/art4/400/500", widthCm: 60, heightCm: 80, creationYear: 2024, category: "PAINTING", tag: "特价" },
        { id: 5, title: "海边日落", artistName: "陈静", price: "8,800", coverUrl: "https://picsum.photos/seed/art5/400/500", widthCm: 40, heightCm: 50, creationYear: 2025, category: "PAINTING", tag: "" },
        { id: 6, title: "雕塑作品 #3", artistName: "赵磊", price: "45,000", coverUrl: "https://picsum.photos/seed/art6/400/500", widthCm: 25, heightCm: 45, creationYear: 2023, category: "SCULPTURE", tag: "独家" },
        { id: 7, title: "花卉系列", artistName: "孙丽", price: "5,200", coverUrl: "https://picsum.photos/seed/art7/400/500", widthCm: 45, heightCm: 60, creationYear: 2024, category: "PRINT", tag: "" },
        { id: 8, title: "竹林深处", artistName: "周杰", price: "19,800", coverUrl: "https://picsum.photos/seed/art8/400/500", widthCm: 68, heightCm: 136, creationYear: 2024, category: "INK", tag: "热卖" },
        { id: 9, title: "星空之下", artistName: "吴敏", price: "22,800", coverUrl: "https://picsum.photos/seed/art9/400/500", widthCm: 90, heightCm: 120, creationYear: 2025, category: "PAINTING", tag: "新品" }
      ]

      this.setData({
        cartItems: mockItems,
        recommendProducts,
        loading: false,
        error: ""
      })
    } catch (error) {
      this.setData({
        loading: false,
        error: error.message || "购物车加载失败"
      })
    }
  },

  toggleEditMode() {
    this.setData({
      isEditMode: !this.data.isEditMode
    })
  },

  toggleItem(e) {
    const id = e.currentTarget.dataset.id
    const items = this.data.cartItems.map(item => {
      if (item.id === id) {
        return { ...item, selected: !item.selected }
      }
      return item
    })
    this.updateSummary(items)
  },

  toggleSelectAll() {
    const allSelected = !this.data.allSelected
    const items = this.data.cartItems.map(item => ({
      ...item,
      selected: allSelected
    }))
    this.updateSummary(items)
  },

  increaseCount(e) {
    const id = e.currentTarget.dataset.id
    const items = this.data.cartItems.map(item => {
      if (item.id === id && item.quantity < item.stock) {
        return { ...item, quantity: item.quantity + 1 }
      }
      return item
    })
    this.updateSummary(items)
  },

  decreaseCount(e) {
    const id = e.currentTarget.dataset.id
    const items = this.data.cartItems.map(item => {
      if (item.id === id && item.quantity > 1) {
        return { ...item, quantity: item.quantity - 1 }
      }
      return item
    })
    this.updateSummary(items)
  },

  removeItem(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认删除',
      content: '确定要从购物车中移除这件商品吗？',
      confirmColor: '#ff4d4f',
      success: (res) => {
        if (res.confirm) {
          const items = this.data.cartItems.filter(item => item.id !== id)
          this.updateSummary(items)
          wx.showToast({ title: '已删除', icon: 'success' })
        }
      }
    })
  },

  updateSummary(items) {
    const selectedItems = items.filter(item => item.selected)
    const allSelected = items.length > 0 && selectedItems.length === items.length
    let total = 0
    selectedItems.forEach(item => {
      total += item.price * item.quantity
    })
    this.setData({
      cartItems: items,
      allSelected,
      totalPrice: total.toLocaleString('zh-CN', { minimumFractionDigits: 2 }),
      selectedCount: selectedItems.length
    })
  },

  handleCheckout() {
    if (this.data.selectedCount === 0) {
      wx.showToast({ title: '请先选择商品', icon: 'none' })
      return
    }
    wx.showToast({ title: '结算功能开发中', icon: 'none' })
  },

  goHome() {
    wx.switchTab({ url: '/pages/home/index' })
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/artwork/detail?id=${id}` })
  }
})
