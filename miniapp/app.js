const api = require("./utils/api")
const theme = require("./utils/theme")

App({
  globalData: {
    // 开发环境 - Cloudflare Tunnel (公共 HTTPS)
    apiBaseUrl: "https://euro-spoken-vocal-montgomery.trycloudflare.com",
    // 开发者工具用 localhost（仅开发者工具测试）
    // apiBaseUrl: "http://localhost:8080",
    // 生产环境（正式部署后替换）
    // apiBaseUrl: "https://your-server.com",
    token: wx.getStorageSync("token") || "",
    theme: 'dark' // 当前主题
  },

  updateApiBaseUrl(apiBaseUrl) {
    this.globalData.apiBaseUrl = apiBaseUrl
  },

  // 更新全局主题
  updateTheme(newTheme) {
    this.globalData.theme = newTheme
    theme.setTheme(newTheme)
    
    // 更新所有页面
    const pages = getCurrentPages()
    pages.forEach(page => {
      if (page.updateTheme) {
        page.updateTheme(newTheme)
      }
    })
    
    // 通知所有 listeners
    this.triggerEvent && this.triggerEvent('themeChange', { theme: newTheme })
  },

  onLaunch() {
    api.setApp(this)
    
    // 初始化主题
    this.initTheme()
  },

  // 初始化主题系统
  initTheme() {
    // 监听系统主题变化
    this.watchSystemTheme()
    
    // 设置初始主题
    const currentTheme = theme.getTheme()
    this.globalData.theme = currentTheme
    
    // 更新页面主题
    this.applyThemeToPages(currentTheme)
  },

  // 监听系统主题变化
  watchSystemTheme() {
    try {
      // 使用新的 API 获取窗口信息
      const windowInfo = wx.getWindowInfo()
      this.globalData.systemInfo = windowInfo
      
      // 检测系统是否为深色模式
      const isDark = windowInfo.theme === 'dark'
      
      // 如果用户没有手动设置主题，则跟随系统
      const savedTheme = wx.getStorageSync('app_theme')
      if (!savedTheme) {
        const systemTheme = isDark ? 'dark' : 'light'
        this.globalData.theme = systemTheme
        this.applyThemeToPages(systemTheme)
        
        // 监听主题变化
        theme.addListener((newTheme) => {
          this.globalData.theme = newTheme
          this.applyThemeToPages(newTheme)
        })
      } else {
        // 使用用户设置的主题
        this.applyThemeToPages(savedTheme)
        
        theme.addListener((newTheme) => {
          this.globalData.theme = newTheme
          this.applyThemeToPages(newTheme)
        })
      }
    } catch (e) {
      console.error('[App] 初始化主题失败:', e)
      // 默认使用暗色主题
      this.globalData.theme = 'dark'
      this.applyThemeToPages('dark')
    }
  },

  // 应用主题到所有页面
  applyThemeToPages(themeName) {
    // 更新 TabBar 样式
    this.updateTabBarTheme(themeName)
    
    // 更新导航栏样式
    this.updateNavigationBarTheme(themeName)
    
    // 更新所有已加载的页面
    const pages = getCurrentPages()
    pages.forEach(page => {
      if (page && page.setData) {
        page.setData({
          theme: themeName
        })
      }
    })
  },

  // 更新 TabBar 主题
  // 注意：使用自定义 TabBar 时 wx.setTabBarStyle 不起作用
  // TabBar 主题由 custom-tab-bar 组件自行管理
  updateTabBarTheme(themeName) {
    // 此方法保留为空，因为自定义 TabBar 由组件自身处理主题
  },

  // 更新导航栏主题
  updateNavigationBarTheme(themeName) {
    // frontColor 只能是 #ffffff 或 #000000
    // 根据背景色选择：深色背景配白色文字，浅色背景配黑色文字
    const isDark = themeName === 'dark'
    
    wx.setNavigationBarColor({
      frontColor: isDark ? '#ffffff' : '#000000',
      backgroundColor: isDark ? '#0b0a08' : '#f7f6f3',
      fail: (err) => {
        console.log('[App] 设置导航栏样式失败:', err)
      }
    })
  }
})
