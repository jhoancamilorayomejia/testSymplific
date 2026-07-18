import api from './api'

export default {
  login(credenciales) {
    return api.post('/auth/login', credenciales)
  },
  register(datos) {
    return api.post('/auth/register', datos)
  },
  forgotPassword(email) {
    return api.post('/auth/forgot-password', { email })
  },
  resetPassword(token, newPassword) {
    return api.post('/auth/reset-password', { token, newPassword })
  }
}