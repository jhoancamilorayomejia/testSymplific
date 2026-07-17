<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import authService from '../services/authService'

const email = ref('')
const password = ref('')
const error = ref(null)
const cargando = ref(false)

const router = useRouter()
const authStore = useAuthStore()

async function iniciarSesion() {
  error.value = null
  cargando.value = true
  try {
    const { data } = await authService.login({ email: email.value, password: password.value })
    authStore.setSesion(data)
    router.push('/')
  } catch  {
    error.value = 'Credenciales incorrectas'
  } finally {
    cargando.value = false
  }
}
</script>

<template>
  <div class="login-container">
    <form @submit.prevent="iniciarSesion" class="login-form">
      <h1>Symplifica Dashboard</h1>
      <p class="subtitle">Ingresa tus credenciales</p>

      <input v-model="email" type="email" placeholder="Correo electrónico" required />
      <input v-model="password" type="password" placeholder="Contraseña" required />

      <p v-if="error" class="error">{{ error }}</p>

      <button type="submit" :disabled="cargando">
        {{ cargando ? 'Ingresando...' : 'Ingresar' }}
      </button>
    </form>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f5f7fa;
}
.login-form {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 360px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.subtitle { color: #666; margin-top: -0.5rem; }
input {
  padding: 0.7rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
}
button {
  padding: 0.7rem;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}
button:disabled { opacity: 0.6; cursor: not-allowed; }
.error { color: #dc2626; margin: 0; font-size: 0.9rem; }
</style>