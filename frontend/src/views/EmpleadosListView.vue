<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import empleadoService from '../services/empleadoService'
import { useAuthStore } from '../stores/auth'

const empleados = ref([])
const cargando = ref(true)
const error = ref(null)
const router = useRouter()
const authStore = useAuthStore()

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
              <td class="cell-action">
                <button class="btn-link" @click.stop="verDetalle(emp.id)">Ver detalle →</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </main>
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
  overflow: hidden;
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

.cell-name { font-weight: 600; }
.cell-muted { color: #6B7C78; }

.cell-action { text-align: right; }
.btn-link {
  background: none;
  border: none;
  color: #1C8F7A;
  font-weight: 600;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 0;
}
.btn-link:hover { text-decoration: underline; }

@media (max-width: 640px) {
  .content { padding: 1.5rem 1rem 3rem; }
  .page-header { flex-direction: column; align-items: flex-start; gap: 1rem; }
  th:nth-child(3), td:nth-child(3) { display: none; }
}
</style>