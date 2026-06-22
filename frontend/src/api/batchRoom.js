import api from '../utils/api'

export const batchRoomAPI = {
  list: (batchId) => api.get('/batch-rooms', { params: { batchId } }),

  available: (params) => api.get('/batch-rooms/available', { params }),

  addRooms: (batchId, roomIds) => api.post('/batch-rooms/add', { batchId, roomIds }),

  removeRooms: (batchId, roomIds) => api.post('/batch-rooms/remove', { batchId, roomIds })
}

export default batchRoomAPI
