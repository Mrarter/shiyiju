# 拾艺局 Codex 开发启动包

“拾艺局”是一个面向微信生态的艺术品流通小程序，当前仓库已扩展出小程序前端、业务后端与运营后台三部分。

## 仓库结构

```text
shiyiju/
  admin/          # 运营后台前端（Vue 3 + Vite + Element Plus）
  docs/           # PRD、UI、后端接口等文档
  miniapp/        # 微信小程序前端
  server/         # Spring Boot 后端
  sql/            # 数据库结构与种子数据
  prompts/        # 历史提示词/辅助文件
```

## 当前进展
- 小程序主线已进入 0.7.0 收尾
- 运营后台已补建并可浏览器打开、登录、读写部分真实数据
- GitHub 仓库已创建：<https://github.com/Mrarter/shiyiju>

## 本地启动

### 1. 启动后端
```bash
cd server
DB_HOST=localhost DB_PORT=3306 DB_NAME=shiyiju DB_USER=root DB_PASSWORD='' SERVER_PORT=8081 mvn spring-boot:run
```

### 2. 启动后台前端
```bash
cd admin
npm install
npm run dev -- --host 0.0.0.0
```

默认后台地址：
- <http://localhost:5174>

### 3. 默认后台演示账号
- 账号：`admin`
- 密码：`123456`

## 数据脚本
- 建表：`sql/schema.sql`
- 后台演示数据：`sql/admin_seed.sql`
- 首页运营配置表：`sql/admin_operation_config.sql`

## 当前已接通的后台能力
- 登录
- 控制台数据
- 首页运营配置查询/新增/编辑
- 作品状态更新
- 艺术家状态更新
- 订单发货更新
- 订单备注更新

## 下一步主线
- 艺术家编辑
- 作品编辑
- 首页运营发布流完善
- 订单处理流程继续补齐
