import api from '../utils/api'

export const allocationResultAPI = {
  viewRooms: (batchId, params) => api.get(`/batches/${batchId}/view-rooms`, { params }),
  viewRoomBeds: (batchId, roomId) => api.get(`/batches/${batchId}/view-rooms/${roomId}/beds`)
}

export default allocationResultAPI
