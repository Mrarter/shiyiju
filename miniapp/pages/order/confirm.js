const api = require('../../utils/api')
const { getDefaultAddress } = require('../../utils/address')
const { normalizeImageUrl } = require('../../utils/imageUrl')

Page({
  data: {
    artwork: null,
    artworkList: [], // 支持多商品下单
    totalPrice: '0.00', // 总价
    address: null,
    remark: '',
    paymentMethod: 'alipay',
    isAnonymous: false,
    loading: false,
    disabled: false
  },

  onLoad(options) {
    wx.hideTabBar({ animation: false })
    this.goBack = () => wx.navigateBack()
    
    // 从 URL 参数获取商品信息
    if (options.artworkId) {
      // 单个商品直接购买
      this.loadArtwork(options.artworkId)
    } else if (options.items) {
      // 从购物车传来的商品信息
      this.loadFromCartItems(options.items)
    } else {
      // 尝试从本地存储获取
      this.loadFromLocalCart()
    }
    
    this.loadAddress()
  },

  // 更新总价
  updateTotalPrice() {
    const { artworkList } = this.data
    let total = 0
    artworkList.forEach(item => {
      total += (item.currentPrice || 0) * (item.quantity || 1)
    })
    this.setData({
      totalPrice: total.toLocaleString('zh-CN', { minimumFractionDigits: 2 })
    })
  },

  // 加载单个作品详情
  async loadArtwork(artworkId) {
    try {
      const res = await api.request({ url: `/artworks/${artworkId}`, method: 'GET' })
      if (res) {
        const formatted = this.formatArtwork(res)
        this.setData({
          artwork: formatted,
          artworkList: [formatted]
        })
        this.updateTotalPrice()
      }
    } catch (e) {
      console.error('[订单确认] 加载作品失败:', e)
      // 尝试从本地存储获取
      this.loadFromLocalCart()
    }
  },

  // 从购物车传来的商品解析
  loadFromCartItems(itemsStr) {
    try {
      const items = JSON.parse(decodeURIComponent(itemsStr))
      if (items && items.length > 0) {
        const formattedList = items.map(item => this.formatArtwork(item))
        this.setData({
          artworkList: formattedList,
          artwork: formattedList[0] // 主商品显示第一个
        })
        this.updateTotalPrice()
      } else {
        this.loadFromLocalCart()
      }
    } catch (e) {
      console.error('[订单确认] 解析商品信息失败', e)
      this.loadFromLocalCart()
    }
  },

  // 从本地存储获取购物车数据
  loadFromLocalCart() {
    try {
      const localCart = wx.getStorageSync('localCart') || []
      const selectedItems = localCart.filter(item => item.selected !== false)
      if (selectedItems.length > 0) {
        const formattedList = selectedItems.map(item => this.formatArtwork(item))
        this.setData({
          artworkList: formattedList,
          artwork: formattedList[0]
        })
        this.updateTotalPrice()
      }
    } catch (e) {
      console.error('[订单确认] 本地存储读取失败', e)
    }
  },

  // 格式化作品数据
  formatArtwork(item) {
    return {
      id: item.artworkId || item.id,
      title: item.title || '未知作品',
      artistName: item.artistName || '未知艺术家',
      coverUrl: normalizeImageUrl(item.coverUrl, null, `art${item.artworkId || item.id}`),
      currentPrice: item.price || item.currentPrice || 0,
      category: this.getCategoryName(item.category),
      widthCm: item.widthCm || '-',
      heightCm: item.heightCm || '-',
      creationYear: item.creationYear || '',
      quantity: item.quantity || 1
    }
  },

  getCategoryName(category) {
    const map = {
      PAINTING: '油画',
      PRINT: '版画',
      INK: '水墨',
      SCULPTURE: '雕塑',
      OTHER: '综合'
    }
    return map[category] || '综合'
  },

  async loadAddress() {
    try {
      const res = await getDefaultAddress()
      if (res) {
        this.setData({ address: res })
      }
    } catch (err) {
      console.log('获取地址失败', err)
    }
  },

  selectAddress() {
    wx.navigateTo({
      url: '/pages/address/list?mode=select'
    })
  },

  selectPayment(e) {
    const method = e.currentTarget.dataset.method
    this.setData({ paymentMethod: method })
  },

  toggleAnonymous(e) {
    this.setData({ isAnonymous: e.detail.value })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  // 计算总价
  getTotalPrice() {
    const { artworkList } = this.data
    let total = 0
    artworkList.forEach(item => {
      total += (item.currentPrice || 0) * (item.quantity || 1)
    })
    return total
  },

  async handlePay() {
    if (this.data.loading || this.data.disabled) return

    const { address, remark, paymentMethod, isAnonymous, artworkList } = this.data

    if (!address) {
      wx.showToast({ title: '请先添加收货地址', icon: 'none' })
      return
    }

    if (!artworkList || artworkList.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' })
      return
    }

    this.setData({ loading: true, disabled: true })

    try {
      // 1. 创建订单
      const createRes = await api.request({
        url: '/orders',
        method: 'POST',
        data: {
          artworkIds: artworkList.map(a => a.id),
          addressId: address.id,
          remark: remark,
          anonymous: isAnonymous
        }
      })

      const order = createRes.data || createRes
      console.log('订单创建成功', order)

      // 2. 清理本地购物车中已下单的商品
      this.clearOrderedItems()

      // 3. 发起支付
      const payRes = await api.request({
        url: '/payments/prepay',
        method: 'POST',
        data: {
          orderId: order.orderId || order.id,
          paymentMethod: paymentMethod
        }
      })

      const payData = payRes.data || payRes
      console.log('预支付成功', payData)

      // 4. 调起支付
      if (payData.prepayId && payData.prepayId.startsWith('simulate_')) {
        await this.simulatePay(order.orderId || order.id)
      } else if (paymentMethod === 'alipay') {
        wx.navigateTo({
          url: `/pages/order/success?orderId=${order.orderId || order.id}`
        })
      } else {
        await this.doWxPay(payData, order)
      }

    } catch (err) {
      console.error('支付失败', err)
      wx.showModal({
        title: '支付失败',
        content: err.message || '请稍后重试',
        showCancel: false
      })
      this.setData({ loading: false, disabled: false })
    }
  },

  // 清理已下单的商品
  clearOrderedItems() {
    try {
      const { artworkList } = this.data
      const localCart = wx.getStorageSync('localCart') || []
      const artworkIds = artworkList.map(a => a.id)
      const remainingCart = localCart.filter(item => 
        !artworkIds.includes(item.id) && !artworkIds.includes(item.artworkId)
      )
      wx.setStorageSync('localCart', remainingCart)
    } catch (e) {
      console.error('[订单确认] 清理本地购物车失败', e)
    }
  },

  doWxPay(payData, order) {
    return new Promise((resolve, reject) => {
      wx.requestPayment({
        timeStamp: payData.timeStamp,
        nonceStr: payData.nonceStr,
        package: `prepay_id=${payData.prepayId}`,
        signType: 'MD5',
        paySign: payData.paySign || 'mock_sign',
        success: () => {
          wx.redirectTo({
            url: `/pages/order/success?orderId=${order.orderId || order.id}`
          })
          resolve()
        },
        fail: (err) => {
          console.log('支付取消', err)
          wx.showToast({ title: '支付已取消', icon: 'none' })
          reject(err)
        }
      })
    })
  },

  async simulatePay(orderId) {
    try {
      await api.request({
        url: `/payments/simulate`,
        method: 'POST',
        data: { orderId }
      })
    } catch (err) {
      console.log('模拟支付接口调用失败', err)
    }
    wx.redirectTo({
      url: `/pages/order/success?orderId=${orderId}`
    })
  }
})
