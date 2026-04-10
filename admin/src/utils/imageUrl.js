/**
 * 图片 URL 处理工具
 */
import { API_BASE_URL } from '../config/env'

/**
 * 获取完整的图片访问 URL
 * @param {string} path - 图片路径，如 /uploads/images/xxx.jpg
 * @returns {string} 完整的图片 URL
 */
export function getImageUrl(path) {
  if (!path) return ''
  
  // 如果已经是完整 URL（以 http:// 或 https:// 开头），直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  
  // 如果是 blob URL（裁剪临时图片），直接返回
  if (path.startsWith('blob:')) {
    return path
  }
  
  // 拼接完整 URL
  // 后端 context-path 是 /
  return `${API_BASE_URL}${path}`
}

export default getImageUrl
