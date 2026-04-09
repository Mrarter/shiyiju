const { request } = require('../../utils/api')
const { getDefaultAddress } = require('../../utils/address')

// 模拟作品数据
const MOCK_ARTWORK = {
  id: 1,
  title: '山水之间',
  artistName: '李明',
  coverUrl: 'https://picsum.photos/400/400?random=1',
  currentPrice: 6888,
  category: '油画',
  widthCm: 60,
  heightCm: 80,
  creationYear: 2024
}

Page({
  data: {
    artwork: null,
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
    
    // 支持从购物车传来的商品信息
    if (options.items) {
      try {
        const items = JSON.parse(decodeURIComponent(options.items))
        if (items && items.length > 0) {
          const item = items[0]
          this.setData({
            artwork: {
              id: item.id,
              title: item.title,
              artistName: item.artistName,
              coverUrl: item.coverUrl,
              currentPrice: item.price,
              category: this.getCategoryName(item.category),
              widthCm: item.widthCm,
              heightCm: item.heightCm,
              creationYear: item.creationYear
            }
          })
        }
      } catch (e) {
        console.log('解析商品信息失败', e)
      }
    }
    
    // 如果没有商品数据，加载默认数据
    if (!this.data.artwork) {
      this.setData({ artwork: MOCK_ARTWORK })
    }
    
    this.loadAddress()
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

  async handlePay() {
    if (this.data.loading || this.data.disabled) return

    const { artwork, address, remark, paymentMethod, isAnonymous } = this.data

    if (!address) {
      wx.showToast({ title: '请先添加收货地址', icon: 'none' })
      return
    }

    this.setData({ loading: true, disabled: true })

    try {
      // 1. 创建订单
      const createRes = await request({
        url: '/orders',
        method: 'POST',
        data: {
          artworkId: artwork.id,
          addressId: address.id,
          remark: remark,
          anonymous: isAnonymous
        }
      })

      const order = createRes.data
      console.log('订单创建成功', order)

      // 2. 发起支付
      const payRes = await request({
        url: '/payments/prepay',
        method: 'POST',
        data: {
          orderId: order.orderId,
          paymentMethod: paymentMethod
        }
      })

      const payData = payRes.data
      console.log('预支付成功', payData)

      // 3. 调起支付
      if (payData.prepayId && payData.prepayId.startsWith('simulate_')) {
        await this.simulatePay(order.orderId)
      } else if (paymentMethod === 'alipay') {
        // 支付宝支付
        wx.navigateTo({
          url: `/pages/order/success?orderId=${order.orderId}`
        })
      } else {
        // 微信支付
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
            url: `/pages/order/success?orderId=${order.orderId}`
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
      await request({
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
