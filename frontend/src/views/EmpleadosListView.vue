<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import empleadoService from '../services/empleadoService'
import { useAuthStore } from '../stores/auth'

const empleados = ref([])
const cargando = ref(true)
const error = ref(null)
const router = useRouter()
const authStore = useAuthStore()

// --- estado del modal de edición ---
const modalAbierto = ref(false)
const guardando = ref(false)
const errorModal = ref(null)
const formEdit = reactive({
  id: null,
  nombre: '',
  apellido: '',
  email: '',
  cargo: '',
  ciudad: '',
  direccion: '',
  fechaIngreso: ''
})

async function cargarEmpleados() {
  cargando.value = true
  try {
    const { data } = await empleadoService.listar()
    empleados.value = data
  } catch {
    error.value = 'No se pudo cargar la lista de empleados'
  } finally {
    cargando.value = false
  }
}

function verDetalle(id) {
  router.push(`/empleados/${id}`)
}

function cerrarSesion() {
  authStore.logout()
  router.push('/login')
}

function iniciales(emp) {
  return `${emp.nombre?.[0] ?? ''}${emp.apellido?.[0] ?? ''}`.toUpperCase()
}

// --- lógica del modal ---
function abrirModalEditar(emp) {
  formEdit.id = emp.id
  formEdit.nombre = emp.nombre ?? ''
  formEdit.apellido = emp.apellido ?? ''
  formEdit.email = emp.email ?? ''
  formEdit.cargo = emp.cargo ?? ''
  formEdit.ciudad = emp.ciudad ?? ''
  formEdit.direccion = emp.direccion ?? ''
  formEdit.fechaIngreso = emp.fechaIngreso ?? ''
  errorModal.value = null
  modalAbierto.value = true
}

function cerrarModal() {
  modalAbierto.value = false
}

async function guardarEdicion() {
  guardando.value = true
  errorModal.value = null
  try {
    const { data } = await empleadoService.actualizar(formEdit.id, { ...formEdit })
    const idx = empleados.value.findIndex(e => e.id === formEdit.id)
    if (idx !== -1) empleados.value[idx] = data
    modalAbierto.value = false
  } catch {
    errorModal.value = 'No se pudo guardar. Revisa los datos e intenta de nuevo.'
  } finally {
    guardando.value = false
  }
}

async function eliminar(id) {
  if (!confirm('¿Eliminar este empleado? Esta acción no se puede deshacer.')) return
  try {
    await empleadoService.eliminar(id)
    empleados.value = empleados.value.filter(e => e.id !== id)
  } catch {
    error.value = 'No se pudo eliminar el empleado'
  }
}

onMounted(cargarEmpleados)
</script>

<template>
  <div class="page">
    <header class="topbar">
      <div class="brand">
        <span class="brand-mark"></span>
        <span class="brand-name">symplifica</span>
      </div>
      <div class="account">
        <span class="account-email">{{ authStore.email }}</span>
        <button class="btn-ghost" @click="cerrarSesion">Cerrar sesión</button>
      </div>
    </header>

    <main class="content">
      <div class="page-header">
        <div>
          <h1>Empleados</h1>
          <p class="page-subtitle">
            {{ empleados.length }} {{ empleados.length === 1 ? 'persona registrada' : 'personas registradas' }}
          </p>
        </div>
        <router-link to="/empleados/nuevo" class="btn-primary">+ Nuevo empleado</router-link>
      </div>

      <p v-if="cargando" class="state-message">Cargando empleados…</p>
      <p v-else-if="error" class="state-message state-error">{{ error }}</p>
      <p v-else-if="empleados.length === 0" class="state-message">
        Todavía no hay empleados. Crea el primero con "Nuevo empleado".
      </p>

      <div v-else class="table-card">
        <table>
          <thead>
            <tr>
              <th></th>
              <th>Nombre</th>
              <th>Email</th>
              <th>Cargo</th>
              <th>Ciudad</th>
              <th>Fecha de registro</th>
              <th>Registrado por</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="emp in empleados" :key="emp.id" @click="verDetalle(emp.id)">
              <td><span class="avatar">{{ iniciales(emp) }}</span></td>
              <td class="cell-name">{{ emp.nombre }} {{ emp.apellido }}</td>
              <td class="cell-muted">{{ emp.email }}</td>
              <td>{{ emp.cargo || '—' }}</td>
              <td>{{ emp.ciudad }}</td>
              <td>{{ new Date(emp.createdAt).toLocaleDateString('es-CO') }}</td>
              <td>{{ emp.usuarioRegistro?.email || '—' }}</td>
              <td class="cell-action">
                <button class="btn-link" @click.stop="verDetalle(emp.id)">Ver detalle →</button>
                <button class="btn-link" @click.stop="abrirModalEditar(emp)">Editar</button>
                <button class="btn-link btn-danger" @click.stop="eliminar(emp.id)">Eliminar</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </main>

    <Teleport to="body">
      <div v-if="modalAbierto" class="modal-overlay" @click.self="cerrarModal">
        <div class="modal-card">
          <div class="modal-header">
            <h2>Editar empleado</h2>
            <button class="modal-close" @click="cerrarModal" aria-label="Cerrar">×</button>
          </div>

          <form class="modal-form" @submit.prevent="guardarEdicion">
            <div class="form-row">
              <label>Nombre completo</label>
              <input v-model="formEdit.nombre" required />
            </div>
            <div class="form-row">
              <label>Apellido</label>
              <input v-model="formEdit.apellido" required />
            </div>
            <div class="form-row">
              <label>Email</label>
              <input v-model="formEdit.email" type="email" required />
            </div>
            <div class="form-row">
              <label>Cargo</label>
              <input v-model="formEdit.cargo" />
            </div>
            <div class="form-row">
              <label>Ciudad</label>
              <input v-model="formEdit.ciudad" required />
            </div>
            <div class="form-row">
              <label>Dirección</label>
              <input v-model="formEdit.direccion" />
            </div>
            <div class="form-row">
              <label>Fecha de ingreso</label>
              <input v-model="formEdit.fechaIngreso" type="date" />
            </div>

            <p v-if="errorModal" class="state-message state-error modal-error">{{ errorModal }}</p>

            <div class="modal-actions">
              <button type="button" class="btn-ghost-dark" @click="cerrarModal">Cancelar</button>
              <button type="submit" class="btn-primary" :disabled="guardando">
                {{ guardando ? 'Guardando…' : 'Guardar cambios' }}
              </button>
            </div>
          </form>
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
  max-width: 440px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #E4E7E4;
}
.modal-header h2 { margin: 0; font-size: 1.15rem; font-weight: 700; }

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

.modal-form {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.form-row { display: flex; flex-direction: column; gap: 0.3rem; }
.form-row label {
  font-size: 0.78rem;
  font-weight: 600;
  color: #5B6E6A;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}
.form-row input {
  padding: 0.55rem 0.75rem;
  border: 1px solid #E4E7E4;
  border-radius: 6px;
  font-size: 0.92rem;
  font-family: inherit;
}
.form-row input:focus {
  outline: none;
  border-color: #1C8F7A;
}

.modal-error { padding: 0.75rem 1rem; margin: 0; font-size: 0.85rem; }

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.btn-ghost-dark {
  background: transparent;
  border: 1px solid #E4E7E4;
  color: #1C2B2A;
  padding: 0.55rem 1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
}
.btn-ghost-dark:hover { background: #F4FAF9; }

.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.brand { display: flex; align-items: center; gap: 0.5rem; }
.brand-mark {
  width: 10px;
  height: 10px;
  background: #35C4B0;
  border-radius: 2px;
  display: inline-block;
}
.brand-name {
  font-weight: 700;
  font-size: 1.05rem;
  letter-spacing: -0.01em;
}

.account { display: flex; align-items: center; gap: 1rem; }
.account-email { font-size: 0.85rem; color: #A9C4BF; }

.btn-ghost {
  background: transparent;
  border: 1px solid rgba(247, 248, 246, 0.25);
  color: #F7F8F6;
  padding: 0.4rem 0.9rem;
  border-radius: 6px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: background 0.15s ease;
}
.btn-ghost:hover { background: rgba(247, 248, 246, 0.1); }

.content {
  max-width: 960px;
  margin: 0 auto;
  padding: 2.5rem 2rem 4rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 1.75rem;
}

h1 {
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin: 0;
}

.page-subtitle {
  color: #5B6E6A;
  font-size: 0.9rem;
  margin: 0.25rem 0 0;
}

.btn-primary {
  background: #12302B;
  color: #F7F8F6;
  padding: 0.6rem 1.1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  text-decoration: none;
  transition: background 0.15s ease;
  white-space: nowrap;
}
.btn-primary:hover { background: #0C211D; }

.state-message {
  background: white;
  border: 1px solid #E4E7E4;
  border-radius: 10px;
  padding: 2.5rem;
  text-align: center;
  color: #5B6E6A;
}
.state-error { color: #B3402A; border-color: #EFCDC4; background: #FBF0EC; }

.table-card {
  background: white;
  border: 1px solid #E4E7E4;
  border-radius: 12px;
  overflow-x: auto;
}

table {
  min-width: 900px;
}

table { width: 100%; border-collapse: collapse; }

th {
  text-align: left;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #8A9895;
  font-weight: 600;
  padding: 0.9rem 1.25rem;
  border-bottom: 1px solid #E4E7E4;
}

td {
  padding: 0.85rem 1.25rem;
  border-bottom: 1px solid #EEF1EF;
  font-size: 0.92rem;
}

tbody tr { cursor: pointer; transition: background 0.12s ease; }
tbody tr:hover { background: #F4FAF9; }
tbody tr:last-child td { border-bottom: none; }

.avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #DFF3EF;
  color: #12302B;
  font-size: 0.75rem;
  font-weight: 700;
}

.cell-name { font-weight: 600; white-space: nowrap; }
.cell-muted { color: #6B7C78; white-space: nowrap; }

.cell-action {
  text-align: right;
  white-space: nowrap;
}
.btn-link {
  background: none;
  border: none;
  color: #1C8F7A;
  font-weight: 600;
  font-size: 0.82rem;
  cursor: pointer;
  padding: 0.2rem 0.4rem;
  white-space: nowrap;
}
.btn-link:hover { text-decoration: underline; }
.btn-danger { color: #B3402A; }

@media (max-width: 640px) {
  .content { padding: 1.5rem 1rem 3rem; }
  .page-header { flex-direction: column; align-items: flex-start; gap: 1rem; }
  th:nth-child(3), td:nth-child(3) { display: none; }
}
</style>