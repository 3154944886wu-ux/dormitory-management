import api from '../utils/api'

// 获取房间的可用床位
export const getAvailableBeds = (roomId) => {
  return api.get(`/beds/available/${roomId}`)
}

export default {
  getAvailableBeds
}
