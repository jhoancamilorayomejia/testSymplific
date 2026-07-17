import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const email = ref(localStorage.getItem('email') || null)
  const rol = ref(localStorage.getItem('rol') || null)

  function setSesion(data) {
    token.value = data.token
    email.value = data.email
    rol.value = data.rol
    localStorage.setItem('token', data.token)
    localStorage.setItem('email', data.email)
    localStorage.setItem('rol', data.rol)
  }

  function logout() {
    token.value = null
    email.value = null
    rol.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('email')
    localStorage.removeItem('rol')
  }

  function estaAutenticado() {
    return !!token.value
  }

  return { token, email, rol, setSesion, logout, estaAutenticado }
})