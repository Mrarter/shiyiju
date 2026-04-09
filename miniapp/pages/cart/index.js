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
          coverUrl: "",
          selected: false
        },
        {
          id: 2,
          title: "城市光影系列",
          artistName: "王芳",
          price: 6800,
          quantity: 2,
          stock: 3,
          coverUrl: "",
          selected: false
        },
        {
          id: 3,
          title: "水墨山水",
          artistName: "张伟",
          price: 28000,
          quantity: 1,
          stock: 1,
          coverUrl: "",
          selected: false
        }
      ]

      // 模拟推荐作品数据
      const recommendProducts = [
        { id: 101, title: "抽象艺术 No.7", artistName: "李明", price: "15,800", tag: "特价" },
        { id: 102, title: "海边日落", artistName: "陈静", price: "8,800", tag: "" },
        { id: 103, title: "雕塑作品 #3", artistName: "赵磊", price: "45,000", tag: "独家" },
        { id: 104, title: "花卉系列", artistName: "王芳", price: "5,200", tag: "" },
        { id: 105, title: "竹林深处", artistName: "张伟", price: "19,800", tag: "热卖" },
        { id: 106, title: "星空之下", artistName: "吴敏", price: "22,800", tag: "新品" }
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
