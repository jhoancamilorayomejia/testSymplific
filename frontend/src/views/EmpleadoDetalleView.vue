<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import empleadoService from '../services/empleadoService'

const route = useRoute()
const empleado = ref(null)
const beneficios = ref([])
const ubicacion = ref(null)
const cargando = ref(true)
const mostrarMapa = ref(false)
const error = ref(null)

const nuevoBeneficio = ref({ nombreBeneficio: '', monto: null })
const agregando = ref(false)

// --- confirmación de nuevo beneficio ---
const modalConfirmAgregarAbierto = ref(false)

function abrirConfirmAgregar() {
  modalConfirmAgregarAbierto.value = true
}

function cerrarConfirmAgregar() {
  modalConfirmAgregarAbierto.value = false
}

// --- modal de edición de beneficio ---
const modalAbierto = ref(false)
const modoConfirmacionEdicion = ref(false)
const guardandoModal = ref(false)
const errorModal = ref(null)
const idBeneficioEnEdicion = ref(null)
const formEditBeneficio = reactive({ nombreBeneficio: '', monto: null })

// --- mensaje de confirmación (post-guardado) ---
const successMessage = ref(null)
let successTimeout = null

function mostrarExito(mensaje) {
  successMessage.value = mensaje
  clearTimeout(successTimeout)
  successTimeout = setTimeout(() => {
    successMessage.value = null
  }, 3000)
}

const mapaEmbedUrl = computed(() => {
  if (!ubicacion.value) return null
  return `https://www.google.com/maps?q=${ubicacion.value.latitud},${ubicacion.value.longitud}&output=embed`
})

const mapaEnlaceExterno = computed(() => {
  if (!ubicacion.value) return null
  return `https://www.google.com/maps?q=${ubicacion.value.latitud},${ubicacion.value.longitud}`
})

const iniciales = computed(() => {
  if (!empleado.value) return ''
  return `${empleado.value.nombre?.[0] ?? ''}${empleado.value.apellido?.[0] ?? ''}`.toUpperCase()
})

const totalBeneficios = computed(() =>
  beneficios.value.reduce((sum, b) => sum + Number(b.monto || 0), 0)
)

function formatMonto(valor) {
  return Number(valor || 0).toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })
}

async function cargarDetalle() {
  cargando.value = true
  error.value = null
  try {
    const id = route.params.id
    const [empResp, benResp] = await Promise.all([
      empleadoService.obtener(id),
      empleadoService.listarBeneficios(id)
    ])
    empleado.value = empResp.data
    beneficios.value = benResp.data.beneficios
    ubicacion.value = benResp.data.ubicacion
  } catch {
    error.value = 'No se pudo cargar la información del empleado'
  } finally {
    cargando.value = false
  }
}

async function confirmarAgregarBeneficio() {
  agregando.value = true
  try {
    const id = route.params.id
    await empleadoService.crearBeneficio(id, nuevoBeneficio.value)
    nuevoBeneficio.value = { nombreBeneficio: '', monto: null }
    modalConfirmAgregarAbierto.value = false
    await cargarDetalle()
    mostrarExito('Beneficio agregado correctamente')
  } finally {
    agregando.value = false
  }
}

function abrirModalEdicion(b) {
  idBeneficioEnEdicion.value = b.id
  formEditBeneficio.nombreBeneficio = b.nombreBeneficio
  formEditBeneficio.monto = b.monto
  errorModal.value = null
  modoConfirmacionEdicion.value = false
  modalAbierto.value = true
}

function cerrarModal() {
  modalAbierto.value = false
  modoConfirmacionEdicion.value = false
}

function pedirConfirmacionEdicion() {
  modoConfirmacionEdicion.value = true
}

function volverAEditar() {
  modoConfirmacionEdicion.value = false
}

async function guardarEdicionBeneficio() {
  guardandoModal.value = true
  errorModal.value = null
  try {
    const idEmp = route.params.id
    await empleadoService.actualizarBeneficio(idEmp, idBeneficioEnEdicion.value, { ...formEditBeneficio })
    modalAbierto.value = false
    modoConfirmacionEdicion.value = false
    await cargarDetalle()
    mostrarExito('Beneficio actualizado correctamente')
  } catch {
    errorModal.value = 'No se pudo guardar el beneficio. Intenta de nuevo.'
    modoConfirmacionEdicion.value = false
  } finally {
    guardandoModal.value = false
  }
}

async function eliminarBeneficio(idBeneficio) {
  if (!confirm('¿Eliminar este beneficio? Esta acción no se puede deshacer.')) return
  const idEmp = route.params.id
  await empleadoService.eliminarBeneficio(idEmp, idBeneficio)
  await cargarDetalle()
}

onMounted(cargarDetalle)
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
      <p v-if="cargando" class="state-message">Cargando información…</p>
      <p v-else-if="error" class="state-message state-error">{{ error }}</p>

      <template v-else>
        <!-- Encabezado de empleado -->
        <section class="employee-card">
          <span class="avatar-lg">{{ iniciales }}</span>
          <div class="employee-info">
            <h1>{{ empleado.nombre }} {{ empleado.apellido }}</h1>
            <p class="employee-role">{{ empleado.cargo || 'Sin cargo asignado' }} · {{ empleado.ciudad }}</p>
            <p v-if="empleado.direccion" class="employee-address">{{ empleado.direccion }}</p>
            <p class="employee-email">{{ empleado.email }}</p>
          </div>
        </section>

        <!-- Ubicación -->
        <section class="panel">
          <h2 class="panel-title">Ubicación</h2>
          <div v-if="ubicacion" class="location-body">
            <p class="location-name">{{ ubicacion.nombreCompleto }}</p>
            <p class="location-coords">Lat {{ ubicacion.latitud }} · Lon {{ ubicacion.longitud }}</p>

            <div class="location-actions">
              <button class="btn-secondary" @click="mostrarMapa = !mostrarMapa">
                {{ mostrarMapa ? 'Ocultar mapa' : 'Ver mapa' }}
              </button>
              <a :href="mapaEnlaceExterno" target="_blank" rel="noopener" class="btn-link-inline">
                Abrir en Google Maps ↗
              </a>
            </div>

            <iframe
              v-if="mostrarMapa"
              :src="mapaEmbedUrl"
              class="mapa-embed"
              loading="lazy"
              referrerpolicy="no-referrer-when-downgrade"
            ></iframe>
          </div>
          <p v-else class="state-message state-muted">
            No se pudo obtener la ubicación para esta ciudad.
          </p>
        </section>

        <!-- Beneficios -->
        <section class="panel">
          <div class="panel-header">
            <h2 class="panel-title">Beneficios</h2>
            <span class="panel-badge" v-if="beneficios.length">
              Total: {{ formatMonto(totalBeneficios) }}
            </span>
          </div>

          <p v-if="successMessage" class="state-message state-success">{{ successMessage }}</p>

          <p v-if="beneficios.length === 0" class="state-message state-muted">
            Este empleado todavía no tiene beneficios asignados.
          </p>

          <ul v-else class="benefit-list">
            <li v-for="b in beneficios" :key="b.id" class="benefit-row">
              <div class="benefit-info">
                <span class="benefit-name">{{ b.nombreBeneficio }}</span>
                <span class="benefit-amount">{{ formatMonto(b.monto) }}</span>
              </div>
              <div class="benefit-actions">
                <button class="btn-link" @click="abrirModalEdicion(b)">Editar</button>
                <button class="btn-link btn-danger" @click="eliminarBeneficio(b.id)">Eliminar</button>
              </div>
            </li>
          </ul>

          <form class="add-benefit-form" @submit.prevent="abrirConfirmAgregar">
            <input
              v-model="nuevoBeneficio.nombreBeneficio"
              placeholder="Nombre del beneficio"
              required
            />
            <input
              v-model.number="nuevoBeneficio.monto"
              type="number"
              step="0.01"
              placeholder="Monto"
              required
            />
            <button type="submit" class="btn-primary" :disabled="agregando">
              {{ agregando ? 'Agregando…' : '+ Agregar beneficio' }}
            </button>
          </form>
        </section>
      </template>
    </main>

    <!-- Modal de confirmación: agregar beneficio -->
    <Teleport to="body">
      <div v-if="modalConfirmAgregarAbierto" class="modal-overlay" @click.self="cerrarConfirmAgregar">
        <div class="modal-card">
          <div class="modal-header">
            <h2>Confirmar nuevo beneficio</h2>
            <button class="modal-close" @click="cerrarConfirmAgregar" aria-label="Cerrar">×</button>
          </div>

          <div class="modal-body">
            <p class="confirm-text">Estás a punto de agregar este beneficio:</p>

            <div class="confirm-summary">
              <div class="confirm-row">
                <span class="confirm-label">Nombre</span>
                <span class="confirm-value">{{ nuevoBeneficio.nombreBeneficio || '—' }}</span>
              </div>
              <div class="confirm-row">
                <span class="confirm-label">Monto</span>
                <span class="confirm-value">{{ formatMonto(nuevoBeneficio.monto) }}</span>
              </div>
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn-ghost-dark" @click="cerrarConfirmAgregar">Revisar de nuevo</button>
            <button type="button" class="btn-primary" :disabled="agregando" @click="confirmarAgregarBeneficio">
              {{ agregando ? 'Guardando…' : 'Confirmar y agregar' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Modal de edición de beneficio -->
    <Teleport to="body">
      <div v-if="modalAbierto" class="modal-overlay" @click.self="cerrarModal">
        <div class="modal-card">
          <div class="modal-header">
            <h2>{{ modoConfirmacionEdicion ? 'Confirmar cambios' : 'Editar beneficio' }}</h2>
            <button class="modal-close" @click="cerrarModal" aria-label="Cerrar">×</button>
          </div>

          <!-- Paso 1: editar valores -->
          <form v-if="!modoConfirmacionEdicion" class="modal-form" @submit.prevent="pedirConfirmacionEdicion">
            <div class="form-row">
              <label>Nombre del beneficio</label>
              <input v-model="formEditBeneficio.nombreBeneficio" required />
            </div>
            <div class="form-row">
              <label>Monto</label>
              <input v-model.number="formEditBeneficio.monto" type="number" step="0.01" required />
            </div>

            <p v-if="errorModal" class="state-message state-error modal-error">{{ errorModal }}</p>

            <div class="modal-actions">
              <button type="button" class="btn-ghost-dark" @click="cerrarModal">Cancelar</button>
              <button type="submit" class="btn-primary">Continuar</button>
            </div>
          </form>

          <!-- Paso 2: confirmar cambios -->
          <div v-else class="modal-body">
            <p class="confirm-text">Vas a guardar estos cambios:</p>

            <div class="confirm-summary">
              <div class="confirm-row">
                <span class="confirm-label">Nombre</span>
                <span class="confirm-value">{{ formEditBeneficio.nombreBeneficio || '—' }}</span>
              </div>
              <div class="confirm-row">
                <span class="confirm-label">Monto</span>
                <span class="confirm-value">{{ formatMonto(formEditBeneficio.monto) }}</span>
              </div>
            </div>

            <p v-if="errorModal" class="state-message state-error modal-error">{{ errorModal }}</p>

            <div class="modal-actions">
              <button type="button" class="btn-ghost-dark" @click="volverAEditar">Volver a editar</button>
              <button type="button" class="btn-primary" :disabled="guardandoModal" @click="guardarEdicionBeneficio">
                {{ guardandoModal ? 'Guardando…' : 'Confirmar cambios' }}
              </button>
            </div>
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
  max-width: 760px;
  margin: 0 auto;
  padding: 2rem 2rem 4rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.state-message {
  background: white;
  border: 1px solid #E4E7E4;
  border-radius: 10px;
  padding: 2rem;
  text-align: center;
  color: #5B6E6A;
}
.state-error { color: #B3402A; border-color: #EFCDC4; background: #FBF0EC; }
.state-muted { padding: 1.25rem; text-align: left; }
.state-success {
  color: #1C8F7A;
  border-color: #BEE5DD;
  background: #E6F5F2;
  padding: 0.9rem 1.25rem;
  text-align: left;
}

/* Encabezado empleado */
.employee-card {
  background: white;
  border: 1px solid #E4E7E4;
  border-radius: 12px;
  padding: 1.75rem;
  display: flex;
  gap: 1.25rem;
  align-items: center;
}

.avatar-lg {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #DFF3EF;
  color: #12302B;
  font-size: 1.3rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

.employee-info h1 {
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  margin: 0 0 0.25rem;
}
.employee-role { color: #5B6E6A; font-size: 0.92rem; margin: 0 0 0.15rem; }
.employee-address { color: #8A9895; font-size: 0.85rem; margin: 0 0 0.15rem; }
.employee-email { color: #1C8F7A; font-size: 0.85rem; margin: 0; }

/* Paneles genéricos */
.panel {
  background: white;
  border: 1px solid #E4E7E4;
  border-radius: 12px;
  padding: 1.5rem 1.75rem;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.panel-title {
  font-size: 1.05rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  margin: 0 0 1rem;
}
.panel-header .panel-title { margin: 0; }

.panel-badge {
  font-size: 0.8rem;
  font-weight: 600;
  color: #1C8F7A;
  background: #DFF3EF;
  padding: 0.3rem 0.7rem;
  border-radius: 999px;
}

/* Ubicación */
.location-name { font-weight: 600; margin: 0 0 0.25rem; }
.location-coords { color: #5B6E6A; font-size: 0.85rem; margin: 0 0 1rem; }
.location-actions { display: flex; align-items: center; gap: 1rem; }

.btn-secondary {
  background: #F4FAF9;
  border: 1px solid #C9E6E0;
  color: #12302B;
  padding: 0.5rem 0.9rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
}
.btn-secondary:hover { background: #E9F5F2; }

.btn-link-inline {
  color: #1C8F7A;
  font-size: 0.85rem;
  font-weight: 600;
  text-decoration: none;
}
.btn-link-inline:hover { text-decoration: underline; }

.mapa-embed {
  width: 100%;
  height: 280px;
  border: 0;
  border-radius: 10px;
  margin-top: 1.1rem;
}

/* Beneficios */
.benefit-list {
  list-style: none;
  margin: 0 0 1.25rem;
  padding: 0;
  border: 1px solid #EEF1EF;
  border-radius: 10px;
  overflow: hidden;
}

.benefit-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.85rem 1.1rem;
  border-bottom: 1px solid #EEF1EF;
}
.benefit-row:last-child { border-bottom: none; }

.benefit-info { display: flex; flex-direction: column; gap: 0.15rem; }
.benefit-name { font-weight: 600; font-size: 0.92rem; }
.benefit-amount { color: #5B6E6A; font-size: 0.85rem; }

.benefit-actions { display: flex; gap: 0.9rem; flex-shrink: 0; }

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
.btn-danger { color: #B3402A; }

.add-benefit-form {
  display: flex;
  gap: 0.6rem;
  flex-wrap: wrap;
}
.add-benefit-form input {
  flex: 1;
  min-width: 140px;
  padding: 0.6rem 0.8rem;
  border: 1px solid #E4E7E4;
  border-radius: 8px;
  font-size: 0.9rem;
  font-family: inherit;
}
.add-benefit-form input:focus { outline: none; border-color: #1C8F7A; }

.btn-primary {
  background: #12302B;
  color: #F7F8F6;
  padding: 0.6rem 1.1rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  border: none;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s ease;
}
.btn-primary:hover { background: #0C211D; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

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
  max-width: 400px;
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
.form-row input:focus { outline: none; border-color: #1C8F7A; }

.modal-error { padding: 0.75rem 1rem; margin: 0; font-size: 0.85rem; }

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 0.5rem;
  padding: 0 1.5rem 1.5rem;
}
.modal-form .modal-actions { padding: 0; margin-top: 0.5rem; }

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

@media (max-width: 640px) {
  .content { padding: 1.25rem 1rem 3rem; }
  .employee-card { flex-direction: column; align-items: flex-start; }
  .add-benefit-form { flex-direction: column; }
  .add-benefit-form button { width: 100%; }
}
</style>