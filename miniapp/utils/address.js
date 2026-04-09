const { request } = require('./api')

/**
 * 获取地址列表
 */
async function getAddressList() {
  try {
    const res = await request({
      url: '/users/me/addresses',
      method: 'GET'
    })
    return res.data || []
  } catch (err) {
    console.error('获取地址列表失败', err)
    return []
  }
}

/**
 * 获取默认地址
 */
async function getDefaultAddress() {
  try {
    const res = await request({
      url: '/users/me/addresses/default',
      method: 'GET'
    })
    return res.data
  } catch (err) {
    return null
  }
}

/**
 * 获取单个地址
 */
async function getAddress(id) {
  try {
    const res = await request({
      url: `/users/me/addresses/${id}`,
      method: 'GET'
    })
    return res.data
  } catch (err) {
    return null
  }
}

/**
 * 创建地址
 */
async function createAddress(data) {
  const res = await request({
    url: '/users/me/addresses',
    method: 'POST',
    data
  })
  return res.data
}

/**
 * 更新地址
 */
async function updateAddress(id, data) {
  const res = await request({
    url: `/users/me/addresses/${id}`,
    method: 'PUT',
    data
  })
  return res.data
}

/**
 * 删除地址
 */
async function deleteAddress(id) {
  const res = await request({
    url: `/users/me/addresses/${id}`,
    method: 'DELETE'
  })
  return res.data
}

/**
 * 设置默认地址
 */
async function setDefaultAddress(id) {
  const res = await request({
    url: `/users/me/addresses/${id}/default`,
    method: 'PUT'
  })
  return res.data
}

module.exports = {
  getAddressList,
  getDefaultAddress,
  getAddress,
  createAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
}
