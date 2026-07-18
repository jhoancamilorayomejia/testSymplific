<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import authService from '../services/authService'

const router = useRouter()
const authStore = useAuthStore()

// --- Registro ---
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref(null)
const cargando = ref(false)
const registroExitoso = ref(false)

async function registrarse() {
  error.value = null

  if (password.value !== confirmPassword.value) {
    error.value = 'Las contraseñas no coinciden'
    return
  }

  cargando.value = true
  try {
    await authService.register({ email: email.value, password: password.value })

    registroExitoso.value = true
    loginEmail.value = email.value

    email.value = ''
    password.value = ''
    confirmPassword.value = ''

    abrirLogin()
  } catch (e) {
    error.value = e.response?.data?.message || 'No se pudo completar el registro'
  } finally {
    cargando.value = false
  }
}

// --- Modal de login ---
const mostrarModalLogin = ref(false)
const loginEmail = ref('')
const loginPassword = ref('')
const loginError = ref(null)
const loginCargando = ref(false)

function abrirLogin() {
  loginPassword.value = ''
  loginError.value = null
  mostrarModalLogin.value = true
}

function cerrarLogin() {
  mostrarModalLogin.value = false
  registroExitoso.value = false
}

async function iniciarSesion() {
  loginError.value = null
  loginCargando.value = true
  try {
    const { data } = await authService.login({ email: loginEmail.value, password: loginPassword.value })
    authStore.setSesion(data)
    router.push('/')
  } catch {
    loginError.value = 'Credenciales incorrectas'
  } finally {
    loginCargando.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- Panel informativo -->
    <section class="info-panel">
      <div class="info-content">
        <div class="brand">
          <span class="brand-mark"></span>
          <span class="brand-name">symplifica</span>
        </div>

        <h1>Dashboard de Empleados</h1>
        <p class="info-lead">
          Prueba técnica Fullstack MID — gestión unificada de empleados, beneficios
          y ubicación, integrando una API interna y una externa en un solo backend.
        </p>

        <ul class="feature-list">
          <li>
            <span class="feature-icon">01</span>
            <div>
              <strong>Gestión de empleados</strong>
              <p>Alta, edición y consulta de personas registradas en el equipo.</p>
            </div>
          </li>
          <li>
            <span class="feature-icon">02</span>
            <div>
              <strong>Beneficios asociados</strong>
              <p>CRUD de beneficios por empleado, con monto y detalle.</p>
            </div>
          </li>
          <li>
            <span class="feature-icon">03</span>
            <div>
              <strong>Ubicación en tiempo real</strong>
              <p>Enriquecimiento de datos vía la API de OpenStreetMap (Nominatim).</p>
            </div>
          </li>
        </ul>

        <div class="stack-tags">
          <span>Spring Boot</span>
          <span>VueJS</span>
          <span>PostgreSQL</span>
          <span>JWT</span>
        </div>
      </div>
    </section>

    <!-- Panel de registro -->
    <section class="form-panel">
      <form @submit.prevent="registrarse" class="login-form">
        <h2>Crea tu cuenta</h2>
        <p class="subtitle">Tu usuario quedará con rol Administrador</p>

        <div class="form-row">
          <label>Correo electrónico</label>
          <input v-model="email" type="email" placeholder="nombre@symplifica.com" required />
        </div>

        <div class="form-row">
          <label>Contraseña</label>
          <input v-model="password" type="password" placeholder="Mínimo 6 caracteres" required minlength="6" />
        </div>

        <div class="form-row">
          <label>Confirmar contraseña</label>
          <input v-model="confirmPassword" type="password" placeholder="••••••••" required />
        </div>

        <p v-if="error" class="error">{{ error }}</p>

        <button type="submit" class="btn-primary" :disabled="cargando">
          {{ cargando ? 'Creando cuenta…' : 'Crear cuenta' }}
        </button>

        <button type="button" class="link-secondary" @click="abrirLogin">
          ¿Ya tienes cuenta? Inicia sesión
        </button>
      </form>
    </section>

    <!-- Modal de login -->
    <div v-if="mostrarModalLogin" class="modal-overlay" @click.self="cerrarLogin">
      <div class="modal-card">
        <button class="modal-close" @click="cerrarLogin" aria-label="Cerrar">✕</button>

        <form @submit.prevent="iniciarSesion" class="login-form modal-form">
          <p v-if="registroExitoso" class="success-banner">
            ✓ Usuario registrado correctamente. Inicia sesión para continuar.
          </p>

          <h2>Bienvenido de vuelta</h2>
          <p class="subtitle">Ingresa tus credenciales para continuar</p>

          <div class="form-row">
            <label>Correo electrónico</label>
            <input v-model="loginEmail" type="email" placeholder="nombre@symplifica.com" required />
          </div>

          <div class="form-row">
            <label>Contraseña</label>
            <input v-model="loginPassword" type="password" placeholder="••••••••" required />
          </div>

          <p v-if="loginError" class="error">{{ loginError }}</p>

          <button type="submit" class="btn-primary" :disabled="loginCargando">
            {{ loginCargando ? 'Ingresando…' : 'Ingresar' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
* { box-sizing: border-box; }

.login-page {
  min-height: 100vh;
  display: flex;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  color: #1C2B2A;
}

/* Panel informativo */
.info-panel {
  flex: 1.1;
  background: #12302B;
  color: #F7F8F6;
  display: flex;
  align-items: center;
  padding: 3rem;
}

.info-content { max-width: 480px; margin: 0 auto; }

.brand { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 2.5rem; }
.brand-mark {
  width: 10px;
  height: 10px;
  background: #35C4B0;
  border-radius: 2px;
  display: inline-block;
}
.brand-name { font-weight: 700; font-size: 1.05rem; letter-spacing: -0.01em; }

.info-panel h1 {
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin: 0 0 0.75rem;
}

.info-lead {
  color: #A9C4BF;
  font-size: 0.95rem;
  line-height: 1.6;
  margin: 0 0 2.25rem;
}

.feature-list {
  list-style: none;
  margin: 0 0 2.25rem;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.feature-list li { display: flex; gap: 1rem; align-items: flex-start; }

.feature-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(53, 196, 176, 0.15);
  color: #35C4B0;
  font-size: 0.75rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-list strong { display: block; font-size: 0.92rem; margin-bottom: 0.15rem; }
.feature-list p { margin: 0; color: #A9C4BF; font-size: 0.85rem; line-height: 1.5; }

.stack-tags { display: flex; flex-wrap: wrap; gap: 0.5rem; }
.stack-tags span {
  border: 1px solid rgba(247, 248, 246, 0.2);
  color: #F7F8F6;
  font-size: 0.78rem;
  font-weight: 600;
  padding: 0.3rem 0.75rem;
  border-radius: 999px;
}

/* Panel de formulario */
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F7F8F6;
  padding: 2rem;
}

.login-form {
  background: white;
  padding: 2.5rem;
  border-radius: 12px;
  border: 1px solid #E4E7E4;
  width: 100%;
  max-width: 360px;
  display: flex;
  flex-direction: column;
  gap: 1.1rem;
}

.login-form h2 {
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin: 0;
}
.subtitle { color: #5B6E6A; font-size: 0.88rem; margin: -0.6rem 0 0; }

.form-row { display: flex; flex-direction: column; gap: 0.35rem; }
.form-row label {
  font-size: 0.78rem;
  font-weight: 600;
  color: #5B6E6A;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}
.form-row input {
  padding: 0.65rem 0.85rem;
  border: 1px solid #E4E7E4;
  border-radius: 8px;
  font-size: 0.95rem;
  font-family: inherit;
}
.form-row input:focus { outline: none; border-color: #1C8F7A; }

.btn-primary {
  padding: 0.75rem;
  background: #12302B;
  color: #F7F8F6;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 600;
  transition: background 0.15s ease;
}
.btn-primary:hover { background: #0C211D; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.link-secondary {
  background: none;
  border: none;
  text-align: center;
  font-size: 0.85rem;
  color: #1C8F7A;
  cursor: pointer;
  font-family: inherit;
  padding: 0;
}
.link-secondary:hover { text-decoration: underline; }

.error {
  color: #B3402A;
  background: #FBF0EC;
  border: 1px solid #EFCDC4;
  border-radius: 8px;
  padding: 0.6rem 0.8rem;
  margin: 0;
  font-size: 0.85rem;
}

.success-banner {
  color: #1C8F7A;
  background: #E6F5F2;
  border: 1px solid #BEE5DD;
  border-radius: 8px;
  padding: 0.6rem 0.8rem;
  margin: 0;
  font-size: 0.85rem;
  font-weight: 600;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(18, 48, 43, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  z-index: 50;
  animation: fadeIn 0.15s ease;
}

.modal-card {
  position: relative;
  width: 100%;
  max-width: 380px;
  animation: slideUp 0.2s ease;
}

.modal-form { max-width: none; }

.modal-close {
  position: absolute;
  top: -0.75rem;
  right: -0.75rem;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #12302B;
  color: #F7F8F6;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
@keyframes slideUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 900px) {
  .login-page { flex-direction: column; }
  .info-panel { padding: 2.5rem 1.5rem; }
  .feature-list { display: none; }
  .form-panel { padding: 2rem 1.25rem 3rem; }
}
</style>