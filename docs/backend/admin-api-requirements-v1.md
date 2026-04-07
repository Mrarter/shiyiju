# 拾艺局运营后台 Admin API 需求清单 v1

## 1. 现状结论

当前后端已经具备的是小程序前台接口，主要包括：
- `/api/v1/auth/wx/login`
- `/api/v1/users/me`
- `/api/v1/users/me/authorization`
- `/api/v1/artists/{id}`
- `/api/v1/artists/recommend`
- `/api/v1/works`
- `/api/v1/works/{id}`
- `/api/v1/orders/{id}`
- `/api/v1/orders`

这些接口面向小程序使用，**不足以直接支撑运营后台**。当前缺口主要是：
- 没有后台登录接口
- 没有后台列表查询接口
- 没有后台创建、编辑、上下架接口
- 没有首页运营位配置接口
- 没有后台订单处理接口

## 2. 建议的 Admin API 命名空间

统一使用：`/admin/v1/*`

## 3. 必需接口清单

## 3.1 后台认证

### POST /admin/v1/auth/login
用途：后台账号密码登录

请求示例：
```json
{
  "username": "admin",
  "password": "123456"
}
```

返回建议：
```json
{
  "token": "xxx",
  "user": {
    "id": 1,
    "username": "admin",
    "role": "SUPER_ADMIN",
    "displayName": "超级管理员"
  }
}
```

### GET /admin/v1/auth/me
用途：获取当前后台登录用户信息

## 3.2 控制台

### GET /admin/v1/dashboard
用途：获取后台首页数据概览

返回建议包含：
- 总用户数
- 今日新增用户
- 艺术家数
- 已上架作品数
- 待支付订单数
- 待发货订单数
- 今日成交金额
- 待处理事项列表

## 3.3 首页运营

### GET /admin/v1/operations
用途：分页查询首页运营配置

### POST /admin/v1/operations
用途：新增推荐位配置

### PUT /admin/v1/operations/{id}
用途：编辑推荐位配置

### PUT /admin/v1/operations/{id}/status
用途：启用/停用推荐位

### DELETE /admin/v1/operations/{id}
用途：删除推荐位

推荐位字段建议：
- id
- type，banner/hot/growth/artist/notice
- title
- subtitle
- targetType
- targetId
- coverUrl
- recommendText
- sort
- status
- startAt
- endAt
- updatedAt

## 3.4 艺术家管理

### GET /admin/v1/artists
用途：分页查询艺术家列表

### GET /admin/v1/artists/{id}
用途：查询艺术家详情

### POST /admin/v1/artists
用途：新增艺术家

### PUT /admin/v1/artists/{id}
用途：编辑艺术家

### PUT /admin/v1/artists/{id}/status
用途：上线/下线艺术家

艺术家字段建议：
- artistId
- artistName
- avatar
- backgroundImageUrl
- slogan
- bio
- styleTags
- city
- sort
- status
- workCount

## 3.5 作品管理

### GET /admin/v1/artworks
用途：分页查询作品列表

### GET /admin/v1/artworks/{id}
用途：作品详情

### POST /admin/v1/artworks
用途：新增作品

### PUT /admin/v1/artworks/{id}
用途：编辑作品

### PUT /admin/v1/artworks/{id}/status
用途：上架/下架

作品字段建议：
- artworkId
- artworkNo
- title
- artistId
- artistName
- category
- saleStatus
- saleMode
- coverUrl
- gallery
- description
- currentPrice
- stock
- tags
- favoriteCount
- viewCount

## 3.6 用户管理

### GET /admin/v1/users
用途：分页查询用户列表

### GET /admin/v1/users/{id}
用途：查看用户详情

### PUT /admin/v1/users/{id}/status
用途：禁用/启用用户

用户字段建议：
- userId
- userNo
- nickname
- avatarUrl
- gender
- mobile
- status
- registerSource
- lastLoginAt
- createdAt

## 3.7 订单管理

### GET /admin/v1/orders
用途：分页查询订单列表

### GET /admin/v1/orders/{id}
用途：查询订单详情

### PUT /admin/v1/orders/{id}/shipment
用途：更新发货信息

请求示例：
```json
{
  "company": "顺丰",
  "trackingNo": "SF1234567890"
}
```

### PUT /admin/v1/orders/{id}/remark
用途：更新运营备注

订单字段建议：
- orderId
- orderNo
- userId
- userNickname
- artworkTitle
- orderStatus
- paymentStatus
- deliveryType
- payAmount
- createdAt
- shipment
- remark

## 3.8 系统设置

### GET /admin/v1/accounts
用途：查询后台账号列表

### POST /admin/v1/accounts
用途：新增后台账号

### PUT /admin/v1/accounts/{id}
用途：编辑后台账号

### PUT /admin/v1/accounts/{id}/password/reset
用途：重置密码

## 4. 与现有小程序接口的复用建议

可以直接复用或参考的现有模型：
- ArtistDetailVO
- ArtistCardVO
- ArtworkListItemVO
- OrderDetailVO
- CurrentUserVO

建议做法：
- 小程序接口继续保留，以前台展示为主
- 新增 admin DTO/VO，避免把后台字段硬塞进小程序接口
- 共享底层 service 和 mapper，减少重复开发

## 5. 后台前端当前适配状态

当前 admin 前端项目已按以下资源结构预留：
- 控制台
- 首页运营
- 艺术家管理
- 作品管理
- 用户管理
- 订单管理
- 系统设置

并已预留：
- `VITE_API_BASE_URL`
- `VITE_API_PREFIX`
- `VITE_ADMIN_API_PREFIX`
- service 层与 store 层

因此，后端只要按本清单逐步补齐 `/admin/v1/*` 接口，前端可以很快切换到真实接口。

## 6. 开发优先级建议

### P0
- `/admin/v1/auth/login`
- `/admin/v1/dashboard`
- `/admin/v1/operations`
- `/admin/v1/artists`
- `/admin/v1/artworks`

### P1
- `/admin/v1/users`
- `/admin/v1/orders`
- 发货与备注更新接口

### P2
- `/admin/v1/accounts`
- 角色权限
- 操作日志

## 7. 结论

当前最合理的路径不是继续让运营靠 SQL 改数据，而是尽快补齐一套 `/admin/v1/*` 接口，把已经搭好的 admin 前端接成真后台。这样既能解决拾艺局首页内容空白问题，也能为后续多项目复用后台方案打基础。