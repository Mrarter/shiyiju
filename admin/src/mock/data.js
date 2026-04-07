export const dashboardMetrics = [
  { label: '总用户数', value: '1,286', delta: '较昨日 +28' },
  { label: '艺术家数量', value: '36', delta: '待完善资料 3' },
  { label: '已上架作品', value: '218', delta: '待上架 12' },
  { label: '待处理订单', value: '15', delta: '待发货 6' }
]

export const operationItems = [
  { title: '春季主视觉 Banner', type: 'Banner', target: '首页主视觉', status: '启用', updatedAt: '2026-04-07 14:20' },
  { title: '热门藏品推荐', type: '热门藏品', target: '作品 8 个', status: '启用', updatedAt: '2026-04-07 13:45' },
  { title: '推荐艺术家', type: '推荐艺术家', target: '艺术家 6 位', status: '草稿', updatedAt: '2026-04-07 12:15' }
]

export const artists = [
  { name: '林观山', city: '杭州', tags: '油画, 当代', works: 12, status: '上线', sort: 1 },
  { name: '周岚', city: '上海', tags: '版画, 新锐', works: 8, status: '上线', sort: 2 },
  { name: '陈河', city: '苏州', tags: '水墨, 山水', works: 15, status: '下线', sort: 3 }
]

export const artworks = [
  { name: '春山可望', artist: '林观山', price: '¥12,800', stock: 1, status: '上架', tag: '热门藏品' },
  { name: '潮汐笔记', artist: '周岚', price: '¥8,600', stock: 2, status: '上架', tag: '正在升值' },
  { name: '园林记忆', artist: '陈河', price: '¥16,500', stock: 1, status: '下架', tag: '无' }
]

export const users = [
  { nickname: '木木', userNo: 'SYJ10021', gender: '女', status: '正常', lastLogin: '2026-04-07 15:08' },
  { nickname: '阿泽', userNo: 'SYJ10022', gender: '男', status: '正常', lastLogin: '2026-04-07 14:41' },
  { nickname: 'Suki', userNo: 'SYJ10023', gender: '未知', status: '禁用', lastLogin: '2026-04-06 20:15' }
]

export const orders = [
  { orderNo: 'SYJ202604070001', user: '木木', artwork: '春山可望', amount: '¥12,800', status: '待发货', payStatus: '已支付', shipStatus: '未发货' },
  { orderNo: 'SYJ202604070002', user: '阿泽', artwork: '潮汐笔记', amount: '¥8,600', status: '待支付', payStatus: '待支付', shipStatus: '未发货' },
  { orderNo: 'SYJ202604060018', user: 'Suki', artwork: '园林记忆', amount: '¥16,500', status: '已完成', payStatus: '已支付', shipStatus: '已发货' }
]
