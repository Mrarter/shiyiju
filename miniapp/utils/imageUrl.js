/**
 * 图片URL处理工具
 * 统一处理数据库返回的图片URL，确保兼容各种格式
 */

// 获取实际的服务器基础URL（从 app.globalData.apiBaseUrl 提取）
function getActualServerBaseUrl() {
  let apiBaseUrl = 'https://euro-spoken-vocal-montgomery.trycloudflare.com';
  
  try {
    const app = getApp();
    if (app && app.globalData && app.globalData.apiBaseUrl) {
      apiBaseUrl = app.globalData.apiBaseUrl;
    }
  } catch (e) {
    // 忽略错误，使用默认值
  }
  
  // 从 API 地址提取基础 URL，保留原始协议
  const match = apiBaseUrl.match(/^(https?:\/\/[^\/]+)/);
  return match ? match[1] : 'https://euro-spoken-vocal-montgomery.trycloudflare.com';
}

/**
 * 设置服务器基础URL（保留此方法以兼容旧代码）
 * @param {string} url - 服务器基础地址
 */
function setServerBaseUrl(url) {
  // 现在改用动态获取，此方法仅作保留
}

/**
 * 获取服务器基础URL
 * @returns {string}
 */
function getServerBaseUrl() {
  return getActualServerBaseUrl();
}

/**
 * 从API地址提取基础URL
 * 例如: http://192.168.1.1:8080/api/works -> http://192.168.1.1:8080
 * @param {string} apiBaseUrl - API基础地址
 * @returns {string}
 */
function extractBaseUrlFromApi(apiBaseUrl) {
  const defaultUrl = getActualServerBaseUrl();
  if (!apiBaseUrl) return defaultUrl;
  const match = apiBaseUrl.match(/^(https?:\/\/[^\/]+)/);
  return match ? match[1] : defaultUrl;
}

/**
 * 标准化图片URL
 * - null/undefined/空字符串 -> 返回占位图
 * - 本地图片(localhost/192.168) -> 返回占位图（开发环境）
 * - 绝对路径 -> 直接返回
 *
 * @param {string} url - 数据库中的图片URL
 * @param {string} placeholder - 占位图URL（可选）
 * @param {string} seed - 用于占位图的种子（可选，默认用 timestamp）
 * @returns {string} 标准化后的URL
 */
function normalizeImageUrl(url, placeholder, seed) {
  // 默认占位图
  const defaultPlaceholder = 'https://picsum.photos/seed/default/400/500';
  const fallback = placeholder || defaultPlaceholder;

  // 空值处理
  if (!url || typeof url !== 'string' || url.trim() === '') {
    return fallback;
  }

  const trimmed = url.trim();

  // 已经是完整的HTTP(S) URL
  if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
    // 如果是 localhost/127.0.0.1 地址，替换为实际的服务器地址
    if (trimmed.includes('localhost') || trimmed.includes('127.0.0.1')) {
      // 提取图片路径部分，替换为实际的服务器地址
      const pathMatch = trimmed.match(/\/uploads\/.*$/);
      if (pathMatch) {
        return getActualServerBaseUrl() + pathMatch[0];
      }
      // 如果没有路径，使用占位图
      const placeholderSeed = seed || Date.now().toString();
      return `https://picsum.photos/seed/${placeholderSeed}/400/500`;
    }
    // 其他 HTTP 链接转为 HTTPS（通过 nginx 代理）
    if (trimmed.startsWith('http://')) {
      return trimmed.replace('http://', 'https://');
    }
    return trimmed;
  }

  // 协议相对路径 //cdn.xxx.com/xxx
  if (trimmed.startsWith('//')) {
    return 'https:' + trimmed;
  }

  // 相对路径，需要拼接服务器地址
  if (trimmed.startsWith('/')) {
    return getActualServerBaseUrl() + trimmed;
  }

  // 其他情况，当作相对路径处理
  return getActualServerBaseUrl() + '/' + trimmed;
}

/**
 * 批量标准化图片URL数组
 * @param {string[]} urls - 图片URL数组
 * @param {string} placeholder - 占位图
 * @returns {string[]} 标准化后的URL数组
 */
function normalizeImageUrls(urls, placeholder) {
  if (!urls || !Array.isArray(urls)) {
    return [];
  }
  return urls.map(url => normalizeImageUrl(url, placeholder));
}

/**
 * 检查URL是否是有效的图片URL
 * @param {string} url - 图片URL
 * @returns {boolean}
 */
function isValidImageUrl(url) {
  if (!url || typeof url !== 'string') {
    return false;
  }
  const trimmed = url.trim().toLowerCase();
  return trimmed.startsWith('http://')
    || trimmed.startsWith('https://')
    || trimmed.startsWith('//')
    || trimmed.startsWith('/uploads/')
    || trimmed.startsWith('/images/');
}

/**
 * 获取占位图URL
 * @param {string} seed - 种子，用于生成不同的占位图
 * @returns {string}
 */
function getPlaceholderUrl(seed) {
  if (!seed) {
    return 'https://picsum.photos/seed/default/400/500';
  }
  return `https://picsum.photos/seed/${seed}/400/500`;
}

/**
 * 获取艺术家头像占位图
 * @param {string} name - 艺术家名称
 * @returns {string}
 */
function getArtistAvatarPlaceholder(name) {
  const encodedName = encodeURIComponent(name || 'Artist');
  return `https://ui-avatars.com/api/?name=${encodedName}&background=c9a96d&color=fff&size=128&font-size=0.4&bold=true&rounded=true`;
}

/**
 * 获取作品封面占位图
 * @param {number|string} artworkId - 作品ID
 * @returns {string}
 */
function getArtworkCoverPlaceholder(artworkId) {
  return `https://picsum.photos/seed/art${artworkId || 'default'}/400/500`;
}

/**
 * 获取Banner占位图
 * @param {number|string} index - Banner索引
 * @returns {string}
 */
function getBannerPlaceholder(index) {
  const idx = (parseInt(index) || 1) % 3 + 1;
  return `https://picsum.photos/seed/banner${idx}/750/400`;
}

/**
 * 从API响应中提取并标准化图片字段
 * @param {Object} data - API响应数据对象
 * @param {string[]} imageFields - 需要处理的图片字段名数组
 * @returns {Object} 处理后的数据副本
 */
function normalizeImageFields(data, imageFields = ['coverUrl', 'avatarUrl', 'imageUrl', 'backgroundImageUrl', 'artistAvatar']) {
  if (!data || typeof data !== 'object') {
    return data;
  }

  const result = Array.isArray(data) ? [...data] : { ...data };

  const fields = Array.isArray(imageFields) ? imageFields : [imageFields];

  if (Array.isArray(result)) {
    return result.map(item => normalizeImageFields(item, fields));
  }

  for (const field of fields) {
    if (field in result && result[field] != null) {
      if (field.includes('avatar') || field.includes('Avatar')) {
        result[field] = normalizeImageUrl(result[field], getArtistAvatarPlaceholder());
      } else if (field.includes('cover') || field.includes('Cover')) {
        result[field] = normalizeImageUrl(result[field], getArtworkCoverPlaceholder(result.artworkId));
      } else {
        result[field] = normalizeImageUrl(result[field]);
      }
    }
  }

  return result;
}

module.exports = {
  setServerBaseUrl,
  getServerBaseUrl,
  extractBaseUrlFromApi,
  normalizeImageUrl,
  normalizeImageUrls,
  isValidImageUrl,
  getPlaceholderUrl,
  getArtistAvatarPlaceholder,
  getArtworkCoverPlaceholder,
  getBannerPlaceholder,
  normalizeImageFields
};
