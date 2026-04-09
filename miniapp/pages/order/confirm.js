const { request } = require('../../utils/api')
const { getAddressList, getDefaultAddress } = require('../../utils/address')

// 模拟作品数据
const MOCK_ARTWORK = {
  id: 1,
  title: '山水之间',
  artistName: '李明',
  coverUrl: 'https://picsum.photos/400/400?random=1',
  currentPrice: 6888,
  spec: '油画（布面油画）/60cm×80cm/2024年'
}

Page({
  data: {
    artwork: null,
    address: null,
    remark: '',
    loading: false,
    disabled: false
  },

  onLoad(options) {
    wx.hideTabBar({ animation: false })
    const { artworkId } = options
    this.loadArtwork(artworkId || 1)
    this.loadAddress()
  },

  async loadArtwork(artworkId) {
    try {
      const res = await request({
        url: `/works/${artworkId}`,
        method: 'GET'
      })
      this.setData({
        artwork: {
          ...res.data,
          spec: this.formatSpec(res.data)
        }
      })
    } catch (err) {
      // 使用模拟数据
      this.setData({
        artwork: {
          ...MOCK_ARTWORK,
          id: parseInt(artworkId) || MOCK_ARTWORK.id
        }
      })
    }
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

  formatSpec(item) {
    const categoryMap = {
      PAINTING: '布面油画',
      PRINT: '丝网版画',
      INK: '宣纸水墨',
      SCULPTURE: '青铜雕塑',
      OTHER: '综合材料'
    }
    const category = categoryMap[item.category] || '综合材料'
    const width = item.widthCm || '-'
    const height = item.heightCm || '-'
    const year = item.creationYear || ''
    return `${category}/${width}cm×${height}cm/${year}`
  },

  selectAddress() {
    wx.navigateTo({
      url: '/pages/address/list?mode=select'
    })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  async handlePay() {
    if (this.data.loading || this.data.disabled) return

    const { artwork, address, remark } = this.data

    if (!address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' })
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
          remark: remark
        }
      })

      const order = createRes.data
      console.log('订单创建成功', order)

      // 2. 发起支付
      const payRes = await request({
        url: '/payments/prepay',
        method: 'POST',
        data: {
          orderId: order.orderId
        }
      })

      const payData = payRes.data
      console.log('预支付成功', payData)

      // 3. 调起微信支付
      if (payData.prepayId && payData.prepayId.startsWith('simulate_')) {
        // 开发环境模拟支付
        await this.simulatePay(order.orderId)
      } else {
        // 正式环境
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
          console.log('支付成功')
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
    // 开发环境：调用模拟支付接口
    try {
      await request({
        url: `/payments/simulate`,
        method: 'POST',
        data: { orderId }
      })
      wx.redirectTo({
        url: `/pages/order/success?orderId=${orderId}`
      })
    } catch (err) {
      wx.redirectTo({
        url: `/pages/order/success?orderId=${orderId}`
      })
    }
  }
})
