const app = getApp()
const theme = require('../utils/theme')

Component({
  data: {
    selected: 0,
    theme: 'dark',
    color: "#8d8579",
    selectedColor: "#d6b074",
    list: [
      {
        pagePath: "/pages/home/index",
        text: "首页",
        icon: "🏠",
        activeIcon: "🏠"
      },
      {
        pagePath: "/pages/agent/index",
        text: "经纪人",
        icon: "🤝",
        activeIcon: "🤝"
      },
      {
        pagePath: "/pages/cart/index",
        text: "购物车",
        icon: "🛒",
        activeIcon: "🛒"
      },
      {
        pagePath: "/pages/mine/index",
        text: "我的",
        icon: "👤",
        activeIcon: "👤"
      }
    ]
  },
  
  lifetimes: {
    attached() {
      // 获取当前主题
      const currentTheme = app.globalData.theme || theme.getTheme()
      this.setData({ theme: currentTheme })
      
      // 初始化选中状态
      this.initSelected()
      
      // 监听主题变化
      theme.addListener(this.handleThemeChange.bind(this))
    },
    detached() {
      // 移除监听
      theme.removeListener(this.handleThemeChange.bind(this))
    }
  },
  
  pageLifetimes: {
    show() {
      this.initSelected()
    }
  },
  
  methods: {
    // 初始化选中状态
    initSelected() {
      const pages = getCurrentPages()
      if (!pages || pages.length === 0) {
        return
      }
      const page = pages.pop()
      const route = `/${page.route}`
      const selected = this.data.list.findIndex((item) => item.pagePath === route)
      this.setData({ selected: selected < 0 ? 0 : selected })
    },
    
    // 主题变化处理
    handleThemeChange(newTheme) {
      this.setData({ theme: newTheme })
    },
    
    // 切换 Tab
    switchTab(event) {
      const { path, index } = event.currentTarget.dataset
      this.setData({ selected: index })
      wx.switchTab({ url: path })
    }
  }
})
