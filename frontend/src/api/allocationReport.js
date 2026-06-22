import api from '../utils/api'

export const allocationReportAPI = {
  getReport: (batchId) => api.get(`/batches/${batchId}/report`),
  downloadExcel: (batchId) => api.get(`/batches/${batchId}/report/excel`, {
    responseType: 'blob'
  }),
  getStatistics: (batchId) => api.get(`/batches/${batchId}/statistics`)
}

export default allocationReportAPI
