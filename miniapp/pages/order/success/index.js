const { request } = require('../../../utils/api')

Page({
  data: {
    order: null,
    orderId: null
  },

  onLoad(options) {
    const { orderId } = options
    this.setData({ orderId })
    if (orderId) {
      this.loadOrder(orderId)
    }
  },

  async loadOrder(orderId) {
    try {
      const res = await request({
        url: `/orders/${orderId}`,
        method: 'GET'
      })
      this.setData({ order: res.data })
    } catch (err) {
      console.error('加载订单失败', err)
    }
  },

  goHome() {
    wx.switchTab({ url: '/pages/home/index' })
  },

  goOrders() {
    wx.navigateTo({ url: '/pages/order/list' })
  }
})
