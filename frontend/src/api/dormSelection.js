import api from '../utils/api'

export const dormSelectionAPI = {
  mySurvey: () => api.get('/dorm-selection/my-survey'),

  submitAnswers: (answers) => api.post('/dorm-selection/submit-answers', { answers }),

  confirmAllocation: () => api.post('/dorm-selection/confirm-allocation'),

  requestReallocation: () => api.post('/dorm-selection/request-reallocation'),

  myNotifications: () => api.get('/dorm-selection/my-notifications')
}

export default dormSelectionAPI
