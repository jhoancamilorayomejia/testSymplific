<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import empleadoService from '../services/empleadoService'

const route = useRoute()
const empleado = ref(null)
const beneficios = ref([])
const ubicacion = ref(null)
const cargando = ref(true)

const nuevoBeneficio = ref({ nombreBeneficio: '', monto: null })

async function cargarDetalle() {
  cargando.value = true
  const id = route.params.id
  const [empResp, benResp] = await Promise.all([
    empleadoService.obtener(id),
    empleadoService.listarBeneficios(id)
  ])
  empleado.value = empResp.data
  beneficios.value = benResp.data.beneficios
  ubicacion.value = benResp.data.ubicacion
  cargando.value = false
}

async function agregarBeneficio() {
  const id = route.params.id
  await empleadoService.crearBeneficio(id, nuevoBeneficio.value)
  nuevoBeneficio.value = { nombreBeneficio: '', monto: null }
  await cargarDetalle()
}

onMounted(cargarDetalle)
</script>

<template>
  <div v-if="cargando">Cargando...</div>
  <div v-else>
    <router-link to="/">&larr; Volver</router-link>
    <h1>{{ empleado.nombre }} {{ empleado.apellido }}</h1>
    <p>{{ empleado.cargo }} — {{ empleado.ciudad }}</p>

    <div v-if="ubicacion" class="ubicacion">
      <strong>Ubicación (Nominatim):</strong>
      <p>{{ ubicacion.nombreCompleto }}</p>
      <p>Lat: {{ ubicacion.latitud }} | Lon: {{ ubicacion.longitud }}</p>
    </div>
    <p v-else>No se pudo obtener la ubicación para esta ciudad.</p>

    <h2>Beneficios</h2>
    <ul>
      <li v-for="b in beneficios" :key="b.id">
        {{ b.nombreBeneficio }} — ${{ b.monto }}
      </li>
    </ul>

    <h3>Agregar beneficio</h3>
    <form @submit.prevent="agregarBeneficio">
      <input v-model="nuevoBeneficio.nombreBeneficio" placeholder="Nombre del beneficio" required />
      <input v-model.number="nuevoBeneficio.monto" type="number" step="0.01" placeholder="Monto" required />
      <button type="submit">Agregar</button>
    </form>
  </div>
</template>

<style scoped>
.ubicacion { background: #f0f4f8; padding: 1rem; border-radius: 8px; margin: 1rem 0; }
form { display: flex; gap: 0.5rem; margin-top: 0.5rem; }
</style>