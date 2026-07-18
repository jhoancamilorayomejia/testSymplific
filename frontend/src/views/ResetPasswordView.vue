<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import authService from '../services/authService'

const route = useRoute()
const router = useRouter()

const token = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref(null)
const exito = ref(false)
const cargando = ref(false)

onMounted(() => {
  token.value = route.query.token || ''
  if (!token.value) {
    error.value = 'El enlace no es válido'
  }
})

async function restablecer() {
  error.value = null

  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Las contraseñas no coinciden'
    return
  }

  cargando.value = true
  try {
    await authService.resetPassword(token.value, newPassword.value)
    exito.value = true
    setTimeout(() => router.push('/login'), 2500)
  } catch (e) {
    error.value = e.response?.data?.error || 'El enlace no es válido o ha expirado'
  } finally {
    cargando.value = false
  }
}
</script>

<template>
  <div class="reset-page">
    <div class="reset-card">
      <div class="brand">
        <span class="brand-mark"></span>
        <span class="brand-name">symplifica</span>
      </div>

      <template v-if="!exito">
        <h1>Restablecer contraseña</h1>
        <p class="subtitle">Ingresa tu nueva contraseña</p>

        <form @submit.prevent="restablecer">
          <div class="form-row">
            <label>Nueva contraseña</label>
            <input v-model="newPassword" type="password" placeholder="Mínimo 6 caracteres" required minlength="6" />
          </div>
          <div class="form-row">
            <label>Confirmar contraseña</label>
            <input v-model="confirmPassword" type="password" placeholder="••••••••" required />
          </div>

          <p v-if="error" class="error">{{ error }}</p>

          <button type="submit" class="btn-primary" :disabled="cargando || !token">
            {{ cargando ? 'Guardando…' : 'Restablecer contraseña' }}
          </button>
        </form>
      </template>

      <template v-else>
        <p class="success-banner">
          ✓ Contraseña actualizada. Redirigiendo al inicio de sesión…
        </p>
      </template>
    </div>
  </div>
</template>

<style scoped>
.reset-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F7F8F6;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  color: #1C2B2A;
  padding: 1.5rem;
}

.reset-card {
  background: white;
  border: 1px solid #E4E7E4;
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 380px;
}

.brand { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 1.75rem; }
.brand-mark { width: 10px; height: 10px; background: #35C4B0; border-radius: 2px; }
.brand-name { font-weight: 700; font-size: 1.05rem; }

h1 { font-size: 1.4rem; font-weight: 700; margin: 0 0 0.25rem; }
.subtitle { color: #5B6E6A; font-size: 0.88rem; margin: 0 0 1.5rem; }

.form-row { display: flex; flex-direction: column; gap: 0.35rem; margin-bottom: 1rem; }
.form-row label {
  font-size: 0.78rem; font-weight: 600; color: #5B6E6A;
  text-transform: uppercase; letter-spacing: 0.03em;
}
.form-row input {
  padding: 0.65rem 0.85rem; border: 1px solid #E4E7E4;
  border-radius: 8px; font-size: 0.95rem; font-family: inherit;
}
.form-row input:focus { outline: none; border-color: #1C8F7A; }

.btn-primary {
  width: 100%; padding: 0.75rem; background: #12302B; color: #F7F8F6;
  border: none; border-radius: 8px; cursor: pointer;
  font-size: 0.95rem; font-weight: 600;
}
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.error {
  color: #B3402A; background: #FBF0EC; border: 1px solid #EFCDC4;
  border-radius: 8px; padding: 0.6rem 0.8rem; margin: 0 0 1rem; font-size: 0.85rem;
}
.success-banner {
  color: #1C8F7A; background: #E6F5F2; border: 1px solid #BEE5DD;
  border-radius: 8px; padding: 0.8rem; margin: 0; font-size: 0.9rem; font-weight: 600;
  text-align: center;
}
</style>