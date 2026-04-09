const api = require("../../utils/api")

Page({
  data: {
    loading: true,
    error: "",
    cartItems: [],
    allSelected: false,
    totalPrice: "¥0.00",
    selectedCount: 0
  },

  onShow() {
    this.loadCart()
  },

  async loadCart() {
    this.setData({ loading: true, error: "" })
    try {
      const mockItems = []
      this.setData({
        cartItems: mockItems,
        allSelected: false,
        totalPrice: "¥0.00",
        selectedCount: 0,
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

  toggleItem(event) {
    const id = event.currentTarget.dataset.id
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

  increaseCount(event) {
    const id = event.currentTarget.dataset.id
    const items = this.data.cartItems.map(item => {
      if (item.id === id) {
        return { ...item, count: item.count + 1 }
      }
      return item
    })
    this.updateSummary(items)
  },

  decreaseCount(event) {
    const id = event.currentTarget.dataset.id
    const items = this.data.cartItems.map(item => {
      if (item.id === id && item.count > 1) {
        return { ...item, count: item.count - 1 }
      }
      return item
    })
    this.updateSummary(items)
  },

  removeItem(event) {
    const id = event.currentTarget.dataset.id
    const items = this.data.cartItems.filter(item => item.id !== id)
    this.updateSummary(items)
  },

  updateSummary(items) {
    const selectedItems = items.filter(item => item.selected)
    const allSelected = items.length > 0 && selectedItems.length === items.length
    let total = 0
    selectedItems.forEach(item => {
      const price = parseFloat(item.priceText.replace(/[^0-9.]/g, '')) || 0
      total += price * item.count
    })
    this.setData({
      cartItems: items,
      allSelected,
      totalPrice: `¥${total.toFixed(2)}`,
      selectedCount: selectedItems.length
    })
  },

  handleCheckout() {
    if (this.data.selectedCount === 0) {
      wx.showToast({ title: "请先选择作品", icon: "none" })
      return
    }
    wx.showToast({ title: "结算功能开发中", icon: "none" })
  },

  goHome() {
    wx.switchTab({ url: "/pages/home/index" })
  }
})
