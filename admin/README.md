# 拾艺局运营后台

## 本地启动

### 1. 启动后端
在 `server` 目录执行：

```bash
SERVER_PORT=8081 mvn spring-boot:run
```

### 2. 启动前端
在 `admin` 目录执行：

```bash
npm install
npm run dev -- --host 0.0.0.0
```

## 默认访问地址

如果 5174 未被占用：
- <http://localhost:5174>

如果 5174 被占用，Vite 会自动顺延到下一个端口，例如：
- <http://localhost:5175>

## 默认演示账号

- 账号：`admin`
- 密码：`123456`

## 当前已接通的后台接口

- `POST /admin/v1/auth/login`
- `GET /admin/v1/auth/me`
- `GET /admin/v1/dashboard`
- `GET /admin/v1/operations`
- `GET /admin/v1/artists`
- `GET /admin/v1/artworks`
- `GET /admin/v1/users`
- `GET /admin/v1/orders`

## 当前说明

- 当前后端 admin 接口仍为第一版骨架，返回的是 mock / 内存数据
- 前端已经切到真实 admin API 调用链路，不再依赖页面内写死数据
- 下一步应继续把 operations / artists / artworks / users / orders 接到真实数据库查询
