const { request } = require('../../utils/api')

Page({
  data: {
    tabs: [
      { label: '全部', value: 'ALL' },
      { label: '待支付', value: 'PENDING_PAYMENT' },
      { label: '待发货', value: 'PENDING_SHIPMENT' },
      { label: '已发货', value: 'SHIPPED' },
      { label: '已完成', value: 'COMPLETED' }
    ],
    currentTab: 'ALL',
    orders: [],
    page: 1,
    pageSize: 10,
    loading: false,
    hasMore: true
  },

  onLoad() {
    wx.hideTabBar({ animation: false })
    this.goBack = () => wx.navigateBack()
    this.loadOrders()
  },

  onShow() {
    // 返回时刷新
    this.setData({ page: 1, orders: [] })
    this.loadOrders()
  },

  switchTab(e) {
    const status = e.currentTarget.dataset.value
    this.setData({ 
      currentTab: status,
      page: 1,
      orders: []
    })
    this.loadOrders()
  },

  async loadOrders() {
    if (this.data.loading || !this.data.hasMore) return

    this.setData({ loading: true })

    try {
      const params = {
        page: this.data.page,
        pageSize: this.data.pageSize
      }

      if (this.data.currentTab !== 'ALL') {
        params.status = this.data.currentTab
      }

      const res = await request({
        url: '/orders',
        method: 'GET',
        data: params
      })

      const list = res.data?.list || res.data || []

      this.setData({
        orders: this.data.page === 1 ? list : [...this.data.orders, ...list],
        hasMore: list.length >= this.data.pageSize,
        page: this.data.page + 1
      })

    } catch (err) {
      console.error('加载订单失败', err)
      // 使用模拟数据
      this.setData({ orders: this.getMockOrders() })
    } finally {
      this.setData({ loading: false })
    }
  },

  getMockOrders() {
    return [
      {
        orderId: 1,
        orderNo: 'O202604090001',
        itemTitle: '山水之间',
        coverUrl: 'https://picsum.photos/200/200?random=1',
        payAmount: 6888,
        orderStatus: 'PENDING_PAYMENT',
        createdAt: '2026-04-09 21:00'
      },
      {
        orderId: 2,
        orderNo: 'O202604080001',
        itemTitle: '都市夜景',
        coverUrl: 'https://picsum.photos/200/200?random=2',
        payAmount: 12800,
        orderStatus: 'PENDING_SHIPMENT',
        createdAt: '2026-04-08 15:30'
      }
    ]
  },

  formatStatus(status) {
    const map = {
      PENDING_PAYMENT: '待支付',
      PENDING_SHIPMENT: '待发货',
      SHIPPED: '已发货',
      COMPLETED: '已完成',
      CANCELLED: '已取消'
    }
    return map[status] || status
  },

  goOrderDetail(e) {
    wx.navigateTo({
      url: `/pages/order/detail?id=${e.currentTarget.dataset.id}`
    })
  },

  goPay(e) {
    wx.navigateTo({
      url: `/pages/order/pay?orderId=${e.currentTarget.dataset.id}`
    })
  },

  viewShipment(e) {
    wx.navigateTo({
      url: `/pages/order/detail?id=${e.currentTarget.dataset.id}&tab=shipment`
    })
  },

  onReachBottom() {
    this.loadOrders()
  }
})
