const api = require("../../utils/api")
const { normalizeImageUrl } = require("../../utils/imageUrl")

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
      // 从 API 获取购物车数据
      let cartItems = []
      try {
        cartItems = await api.request({ url: "/cart/list", method: "GET" })
        console.log("[购物车] API 返回:", cartItems ? cartItems.length : 0, "条数据")
      } catch (e) {
        console.error("[购物车] API 失败:", e)
        cartItems = []
      }

      // 如果 API 返回空，使用本地存储的购物车数据作为临时方案
      if (!cartItems || cartItems.length === 0) {
        cartItems = this.getLocalCartItems()
      }

      // 格式化购物车数据
      const formattedItems = cartItems.map(item => ({
        id: item.cartId || item.id,
        artworkId: item.artworkId,
        title: item.title || "未知作品",
        artistName: item.artistName || "未知艺术家",
        price: item.price || 0,
        quantity: item.quantity || 1,
        stock: item.stock || 0,
        coverUrl: normalizeImageUrl(item.coverUrl, null, `art${item.artworkId}`),
        widthCm: item.widthCm || "-",
        heightCm: item.heightCm || "-",
        creationYear: item.creationYear || "",
        category: item.category || "OTHER",
        selected: item.selected !== false // 默认选中
      }))

      // 计算总价和选中状态
      const selectedItems = formattedItems.filter(item => item.selected)
      const allSelected = formattedItems.length > 0 && selectedItems.length === formattedItems.length
      let total = 0
      selectedItems.forEach(item => {
        total += (item.price || 0) * item.quantity
      })

      this.setData({
        cartItems: formattedItems,
        allSelected,
        totalPrice: total.toLocaleString('zh-CN', { minimumFractionDigits: 2 }),
        selectedCount: selectedItems.length,
        loading: false,
        error: ""
      })

      // 保存到本地存储
      this.saveLocalCartItems(formattedItems)

    } catch (error) {
      console.error("[购物车] 加载失败:", error)
      // 使用本地缓存
      const localItems = this.getLocalCartItems()
      this.setData({
        cartItems: localItems,
        loading: false,
        error: localItems.length === 0 ? (error.message || "购物车加载失败") : ""
      })
    }
  },

  // 从本地存储获取购物车数据
  getLocalCartItems() {
    try {
      const localCart = wx.getStorageSync('localCart') || []
      return localCart
    } catch (e) {
      return []
    }
  },

  // 保存购物车数据到本地存储
  saveLocalCartItems(items) {
    try {
      wx.setStorageSync('localCart', items)
    } catch (e) {
      console.error("[购物车] 保存本地失败:", e)
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

  async increaseCount(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.cartItems.find(i => i.id === id)
    
    if (!item || item.quantity >= item.stock) {
      wx.showToast({ title: '库存不足', icon: 'none' })
      return
    }

    const newQuantity = item.quantity + 1
    
    // 调用 API 更新
    if (item.artworkId && !id.toString().startsWith('local_')) {
      try {
        await api.request({
          url: "/cart/update",
          method: "PUT",
          data: { cartId: id, quantity: newQuantity }
        })
      } catch (e) {
        console.error("[购物车] 更新数量 API 失败:", e)
      }
    }
    
    const items = this.data.cartItems.map(i => {
      if (i.id === id) {
        return { ...i, quantity: newQuantity }
      }
      return i
    })
    this.updateSummary(items)
  },

  async decreaseCount(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.cartItems.find(i => i.id === id)
    
    if (!item || item.quantity <= 1) {
      return
    }

    const newQuantity = item.quantity - 1
    
    // 调用 API 更新
    if (item.artworkId && !id.toString().startsWith('local_')) {
      try {
        await api.request({
          url: "/cart/update",
          method: "PUT",
          data: { cartId: id, quantity: newQuantity }
        })
      } catch (e) {
        console.error("[购物车] 更新数量 API 失败:", e)
      }
    }
    
    const items = this.data.cartItems.map(i => {
      if (i.id === id) {
        return { ...i, quantity: newQuantity }
      }
      return i
    })
    this.updateSummary(items)
  },

  removeItem(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.cartItems.find(i => i.id === id)
    
    wx.showModal({
      title: '确认删除',
      content: '确定要从购物车中移除这件商品吗？',
      confirmColor: '#ff4d4f',
      success: async (res) => {
        if (res.confirm) {
          // 调用 API 删除
          try {
            if (item && item.artworkId && !item.id.toString().startsWith('local_')) {
              await api.request({
                url: `/cart/remove/${item.id}`,
                method: "DELETE"
              })
            }
          } catch (e) {
            console.error("[购物车] 删除 API 失败:", e)
          }
          
          const items = this.data.cartItems.filter(i => i.id !== id)
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
    // 获取选中的商品信息
    const selectedItems = this.data.cartItems.filter(item => item.selected)
    // 跳转到订单确认页面，传递选中商品信息
    wx.navigateTo({
      url: `/pages/order/confirm?items=${encodeURIComponent(JSON.stringify(selectedItems))}`
    })
  },

  goHome() {
    wx.switchTab({ url: '/pages/home/index' })
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/artwork/detail?id=${id}` })
  }
})
