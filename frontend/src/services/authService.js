import api from './api'

export default {
  login(credentials) {
    return api.post('/auth/login', credentials)
  },
  register(credentials) {
    return api.post('/auth/register', credentials)
  }
}