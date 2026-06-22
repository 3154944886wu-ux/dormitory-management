import api from '../utils/api'

// 获取房间列表
export const getRooms = (params = {}) => {
  return api.get('/rooms', { params })
}

// 获取房间详情
export const getRoomById = (id) => {
  return api.get(`/rooms/${id}`)
}

// 根据楼栋获取房间
export const getRoomsByBuilding = (buildingId) => {
  return api.get(`/rooms/building/${buildingId}`)
}

// 创建房间
export const createRoom = (room) => {
  return api.post('/rooms', room)
}

// 更新房间
export const updateRoom = (id, room) => {
  return api.put(`/rooms/${id}`, room)
}

// 删除房间
export const deleteRoom = (id) => {
  return api.delete(`/rooms/${id}`)
}

// 更新房间状态
export const updateRoomStatus = (id, status) => {
  return api.put(`/rooms/${id}/status`, { status })
}

export default {
  getRooms,
  getRoomById,
  getRoomsByBuilding,
  createRoom,
  updateRoom,
  deleteRoom,
  updateRoomStatus
}