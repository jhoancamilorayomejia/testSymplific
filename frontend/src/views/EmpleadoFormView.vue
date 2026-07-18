<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import empleadoService from '../services/empleadoService'

const router = useRouter()
const form = ref({
  nombre: '', apellido: '', email: '', cargo: '', ciudad: '', direccion: '', fechaIngreso: ''
})

const error = ref(null)
const guardando = ref(false)

// --- confirmación previa a guardar ---
const modalConfirmAbierto = ref(false)

const nombreCompleto = computed(() => `${form.value.nombre} ${form.value.apellido}`.trim())

function abrirConfirmacion() {
  modalConfirmAbierto.value = true
}

function cerrarConfirmacion() {
  modalConfirmAbierto.value = false
}

async function confirmarGuardado() {
  guardando.value = true
  error.value = null
  try {
    await empleadoService.crear(form.value)
    router.push('/')
  } catch {
    error.value = 'No se pudo guardar el empleado. Revisa los datos e intenta de nuevo.'
    modalConfirmAbierto.value = false
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="page">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark"></span>
        <span class="brand-name">symplifica</span>
      </div>
      <router-link to="/" class="btn-ghost">← Volver a empleados</router-link>
    </header>

    <main class="content">
      <div class="page-header">
        <h1>Nuevo empleado</h1>
        <p class="page-subtitle">Completa los datos para registrar a la persona en el equipo.</p>
      </div>

      <section class="panel">
        <p v-if="error" class="state-message state-error">{{ error }}</p>

        <form class="employee-form" @submit.prevent="abrirConfirmacion">
          <div class="form-grid">
            <div class="form-row">
              <label>Nombre</label>
              <input v-model="form.nombre" placeholder="Ej. Camilo" required />
            </div>
            <div class="form-row">
              <label>Apellidos</label>
              <input v-model="form.apellido" placeholder="Ej. Rayo Mejia" required />
            </div>
            <div class="form-row form-row-wide">
              <label>Email</label>
              <input v-model="form.email" type="email" placeholder="nombre@gmail.com" required />
            </div>
            <div class="form-row">
              <label>Cargo</label>
              <input v-model="form.cargo" placeholder="Ej. Desarrollador Full Stack" />
            </div>
            <div class="form-row">
              <label>Ciudad</label>
              <input v-model="form.ciudad" placeholder="Ej. Cali" required />
            </div>
            <div class="form-row form-row-wide">
              <label>Dirección</label>
              <input v-model="form.direccion" placeholder="Ej. Cl. 5 #50-103" />
            </div>
            <div class="form-row">
              <label>Fecha de ingreso</label>
              <input v-model="form.fechaIngreso" type="date" />
            </div>
          </div>

          <div class="form-actions">
            <router-link to="/" class="btn-ghost-dark">Cancelar</router-link>
            <button type="submit" class="btn-primary">Guardar empleado</button>
          </div>
        </form>
      </section>
    </main>

    <!-- Modal de confirmación -->
    <Teleport to="body">
      <div v-if="modalConfirmAbierto" class="modal-overlay" @click.self="cerrarConfirmacion">
        <div class="modal-card">
          <div class="modal-header">
            <h2>Confirmar nuevo empleado</h2>
            <button class="modal-close" @click="cerrarConfirmacion" aria-label="Cerrar">×</button>
          </div>

          <div class="modal-body">
            <p class="confirm-text">Estás a punto de registrar a:</p>

            <div class="confirm-summary">
              <div class="confirm-row">
                <span class="confirm-label">Nombre</span>
                <span class="confirm-value">{{ nombreCompleto || '—' }}</span>
              </div>
              <div class="confirm-row">
                <span class="confirm-label">Email</span>
                <span class="confirm-value">{{ form.email || '—' }}</span>
              </div>
              <div class="confirm-row">
                <span class="confirm-label">Cargo</span>
                <span class="confirm-value">{{ form.cargo || '—' }}</span>
              </div>
              <div class="confirm-row">
                <span class="confirm-label">Ciudad</span>
                <span class="confirm-value">{{ form.ciudad || '—' }}</span>
              </div>
              <div class="confirm-row">
  <span class="confirm-label">Dirección</span>
  <span class="confirm-value">{{ form.direccion || '—' }}</span>
</div>
<div class="confirm-row">
  <span class="confirm-label">Fecha de ingreso</span>
  <span class="confirm-value">{{ form.fechaIngreso || '—' }}</span>
</div>
            </div>

            <p class="confirm-hint">Podrás editar estos datos después desde la lista de empleados.</p>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-ghost-dark" @click="cerrarConfirmacion">Revisar de nuevo</button>
            <button type="button" class="btn-primary" :disabled="guardando" @click="confirmarGuardado">
              {{ guardando ? 'Guardando…' : 'Confirmar y guardar' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #F7F8F6;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  color: #1C2B2A;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #12302B;
  color: #F7F8F6;
}

.brand { display: flex; align-items: center; gap: 0.5rem; }
.brand-mark {
  width: 10px;
  height: 10px;
  background: #35C4B0;
  border-radius: 2px;
  display: inline-block;
}
.brand-name { font-weight: 700; font-size: 1.05rem; letter-spacing: -0.01em; }

.btn-ghost {
  background: transparent;
  border: 1px solid rgba(247, 248, 246, 0.25);
  color: #F7F8F6;
  padding: 0.4rem 0.9rem;
  border-radius: 6px;
  font-size: 0.85rem;
  text-decoration: none;
  transition: background 0.15s ease;
}
.btn-ghost:hover { background: rgba(247, 248, 246, 0.1); }

.content {
  max-width: 680px;
  margin: 0 auto;
  padding: 2.5rem 2rem 4rem;
}

.page-header { margin-bottom: 1.5rem; }
.page-header h1 {
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin: 0;
}
.page-subtitle { color: #5B6E6A; font-size: 0.9rem; margin: 0.35rem 0 0; }

.panel {
  background: white;
  border: 1px solid #E4E7E4;
  border-radius: 12px;
  padding: 1.75rem;
}

.state-message {
  border-radius: 8px;
  padding: 0.85rem 1rem;
  font-size: 0.88rem;
  margin: 0 0 1.25rem;
}
.state-error { color: #B3402A; border: 1px solid #EFCDC4; background: #FBF0EC; }

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem 1.25rem;
}
.form-row-wide { grid-column: 1 / -1; }

.form-row { display: flex; flex-direction: column; gap: 0.35rem; }
.form-row label {
  font-size: 0.78rem;
  font-weight: 600;
  color: #5B6E6A;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}
.form-row input {
  padding: 0.6rem 0.8rem;
  border: 1px solid #E4E7E4;
  border-radius: 8px;
  font-size: 0.92rem;
  font-family: inherit;
}
.form-row input:focus { outline: none; border-color: #1C8F7A; }

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.75rem;
  padding-top: 1.25rem;
  border-top: 1px solid #EEF1EF;
}

.btn-primary {
  background: #12302B;
  color: #F7F8F6;
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: background 0.15s ease;
}
.btn-primary:hover { background: #0C211D; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-ghost-dark {
  background: transparent;
  border: 1px solid #E4E7E4;
  color: #1C2B2A;
  padding: 0.6rem 1.1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}
.btn-ghost-dark:hover { background: #F4FAF9; }

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(18, 48, 43, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  z-index: 50;
}

.modal-card {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #E4E7E4;
}
.modal-header h2 { margin: 0; font-size: 1.1rem; font-weight: 700; }

.modal-close {
  background: none;
  border: none;
  font-size: 1.4rem;
  line-height: 1;
  color: #5B6E6A;
  cursor: pointer;
  padding: 0.25rem;
}
.modal-close:hover { color: #1C2B2A; }

.modal-body { padding: 1.5rem; }
.confirm-text { margin: 0 0 1rem; font-size: 0.9rem; color: #5B6E6A; }

.confirm-summary {
  border: 1px solid #EEF1EF;
  border-radius: 10px;
  overflow: hidden;
}
.confirm-row {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.65rem 1rem;
  border-bottom: 1px solid #EEF1EF;
  font-size: 0.88rem;
}
.confirm-row:last-child { border-bottom: none; }
.confirm-label { color: #8A9895; font-weight: 600; }
.confirm-value { font-weight: 600; text-align: right; }

.confirm-hint {
  margin: 1rem 0 0;
  font-size: 0.8rem;
  color: #8A9895;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 0 1.5rem 1.5rem;
}

@media (max-width: 640px) {
  .content { padding: 1.5rem 1rem 3rem; }
  .form-grid { grid-template-columns: 1fr; }
  .form-row-wide { grid-column: 1; }
}
</style>