# 主题系统使用指南

## 概述

小程序支持 **亮色主题 (Light)** 和 **暗色主题 (Dark)**，默认跟随手机系统主题设置，用户也可手动切换。

## 文件结构

```
miniapp/
├── theme/
│   ├── light.wxss        # 亮色主题变量
│   ├── dark.wxss         # 暗色主题变量
│   ├── theme-classes.wxss # 通用样式类
│   └── README.md         # 本文件
├── utils/
│   └── theme.js          # 主题管理工具
├── app.wxss              # 全局样式入口
├── app.js                # 全局 App
└── pages/
    └── ...
```

## 使用方式

### 1. 页面自动支持主题

在页面的 `WXML` 根元素添加 `theme-{{theme}}` 类名：

```xml
<view class="page theme-{{theme || 'dark'}}">
  <!-- 内容 -->
</view>
```

在页面的 `JS` 文件中添加 `updateTheme` 方法：

```javascript
// 主题更新方法
updateTheme(themeName) {
  this.setData({ theme: themeName })
}
```

### 2. 组件支持主题

在组件的 `JS` 中引入主题工具：

```javascript
const theme = require('../../utils/theme')

Component({
  lifetimes: {
    attached() {
      theme.addListener(this.handleThemeChange.bind(this))
    },
    detached() {
      theme.removeListener(this.handleThemeChange.bind(this))
    }
  },
  
  methods: {
    handleThemeChange(newTheme) {
      this.setData({ theme: newTheme })
    }
  }
})
```

### 3. 使用 CSS 变量

所有颜色、阴影等样式应使用 CSS 变量：

```css
/* 错误：硬编码颜色 */
.page {
  background: #ffffff;
  color: #000000;
}

/* 正确：使用 CSS 变量 */
.page {
  background: var(--bg-primary);
  color: var(--text-primary);
}
```

## CSS 变量列表

### 背景色
| 变量名 | 说明 | 暗色值 | 亮色值 |
|--------|------|--------|--------|
| `--bg-primary` | 主背景 | #0b0a08 | #ffffff |
| `--bg-secondary` | 次级背景 | #11100d | #f7f6f3 |
| `--bg-card` | 卡片背景 | rgba(20,17,14,0.94) | #ffffff |

### 文字色
| 变量名 | 说明 | 暗色值 | 亮色值 |
|--------|------|--------|--------|
| `--text-primary` | 主文字 | #f6efe6 | #1a1918 |
| `--text-secondary` | 次级文字 | #b8b1a7 | #6b6860 |
| `--text-tertiary` | 三级文字 | #8d8579 | #9c9890 |

### 主题色
| 变量名 | 说明 | 暗色值 | 亮色值 |
|--------|------|--------|--------|
| `--color-primary` | 主题色（金色） | #d6b074 | #c3944f |
| `--color-primary-bg` | 主题色背景 | rgba(...) | rgba(...) |

### 功能色
| 变量名 | 说明 |
|--------|------|
| `--color-success` | 成功色 |
| `--color-warning` | 警告色 |
| `--color-error` | 错误色 |
| `--color-info` | 信息色 |

### 其他
| 变量名 | 说明 |
|--------|------|
| `--border-primary` | 边框色 |
| `--divider` | 分隔线 |
| `--shadow-card` | 卡片阴影 |
| `--tabbar-bg` | TabBar 背景 |
| `--navbar-bg` | 导航栏背景 |

## 主题 API

```javascript
const theme = require('../../utils/theme')

// 获取当前主题
theme.getTheme()  // 'light' | 'dark'

// 设置主题
theme.setTheme('dark')

// 切换主题
theme.toggleTheme()

// 是否跟随系统
theme.isFollowSystem()

// 添加主题变化监听
theme.addListener((newTheme) => {
  console.log('主题变化:', newTheme)
})

// 移除监听
theme.removeListener(callback)
```

## 注意事项

1. **渐进式适配**：现有页面可逐步改造，先从最常用的页面开始
2. **变量覆盖**：亮色主题变量会覆盖暗色主题变量，需要同时定义
3. **兼容旧样式**：旧样式中的硬编码颜色会继续生效，但建议逐步迁移
4. **系统主题**：小程序可以通过 `wx.getSystemInfoSync().theme` 获取系统主题
