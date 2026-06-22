import api from '../utils/api'

export function getOperationLogs(params) {
  return api.get('/operation-logs', { params })
}
