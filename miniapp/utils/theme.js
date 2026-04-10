/**
 * 主题管理器
 * 支持跟随系统主题和手动切换
 */

// 主题配置
const THEMES = {
  LIGHT: 'light',
  DARK: 'dark'
}

// 主题存储键名
const STORAGE_KEY = 'app_theme'

// 监听器列表
let listeners = []

/**
 * 获取当前主题
 */
function getTheme() {
  try {
    const saved = wx.getStorageSync(STORAGE_KEY)
    if (saved === THEMES.LIGHT || saved === THEMES.DARK) {
      return saved
    }
    // 返回系统主题
    return getSystemTheme()
  } catch (e) {
    return THEMES.DARK
  }
}

/**
 * 获取系统主题
 */
function getSystemTheme() {
  try {
    const systemInfo = wx.getStorageSync('systemInfo')
    if (systemInfo && systemInfo.theme) {
      return systemInfo.theme
    }
  } catch (e) {}
  
  // 默认返回深色
  return THEMES.DARK
}

/**
 * 设置主题
 * @param {string} theme - 'light' 或 'dark'
 */
function setTheme(theme) {
  if (theme !== THEMES.LIGHT && theme !== THEMES.DARK) {
    return
  }
  
  try {
    wx.setStorageSync(STORAGE_KEY, theme)
  } catch (e) {}
  
  // 更新全局状态
  globalTheme = theme
  
  // 通知所有监听器
  notifyListeners(theme)
}

/**
 * 切换主题
 */
function toggleTheme() {
  const current = getTheme()
  const next = current === THEMES.LIGHT ? THEMES.DARK : THEMES.LIGHT
  setTheme(next)
  return next
}

/**
 * 是否跟随系统
 */
function isFollowSystem() {
  try {
    return !wx.getStorageSync(STORAGE_KEY)
  } catch (e) {
    return true
  }
}

/**
 * 是否为暗色主题
 */
function isDarkTheme() {
  return getTheme() === THEMES.DARK
}

/**
 * 是否为亮色主题
 */
function isLightTheme() {
  return getTheme() === THEMES.LIGHT
}

/**
 * 添加主题变化监听器
 */
function addListener(callback) {
  if (typeof callback === 'function' && !listeners.includes(callback)) {
    listeners.push(callback)
  }
}

/**
 * 移除主题变化监听器
 */
function removeListener(callback) {
  const index = listeners.indexOf(callback)
  if (index > -1) {
    listeners.splice(index, 1)
  }
}

/**
 * 通知所有监听器
 */
function notifyListeners(theme) {
  listeners.forEach(callback => {
    try {
      callback(theme)
    } catch (e) {
      console.error('[主题] 监听器执行失败:', e)
    }
  })
}

// 初始化系统主题监听
function initSystemThemeListener() {
  try {
    const systemInfo = wx.getSystemInfoSync()
    wx.setStorageSync('systemInfo', { theme: systemInfo.theme })
    
    // 如果用户没有手动设置主题，则跟随系统
    if (!wx.getStorageSync(STORAGE_KEY)) {
      globalTheme = systemInfo.theme || THEMES.DARK
      notifyListeners(globalTheme)
    }
  } catch (e) {
    console.error('[主题] 初始化系统主题失败:', e)
  }
}

// 全局主题状态
let globalTheme = THEMES.DARK

// 导出模块
module.exports = {
  THEMES,
  getTheme,
  setTheme,
  toggleTheme,
  isFollowSystem,
  isDarkTheme,
  isLightTheme,
  addListener,
  removeListener,
  initSystemThemeListener
}
