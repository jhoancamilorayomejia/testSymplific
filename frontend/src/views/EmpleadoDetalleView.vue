<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import empleadoService from '../services/empleadoService'

const route = useRoute()
const empleado = ref(null)
const beneficios = ref([])
const ubicacion = ref(null)
const cargando = ref(true)
const mostrarMapa = ref(false)

const nuevoBeneficio = ref({ nombreBeneficio: '', monto: null })

const mapaEmbedUrl = computed(() => {
  if (!ubicacion.value) return null
  return `https://www.google.com/maps?q=${ubicacion.value.latitud},${ubicacion.value.longitud}&output=embed`
})

const mapaEnlaceExterno = computed(() => {
  if (!ubicacion.value) return null
  return `https://www.google.com/maps?q=${ubicacion.value.latitud},${ubicacion.value.longitud}`
})

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
    <p v-if="empleado.direccion" class="direccion">{{ empleado.direccion }}</p>

    <div v-if="ubicacion" class="ubicacion">
      <strong>Ubicación (Nominatim):</strong>
      <p>{{ ubicacion.nombreCompleto }}</p>
      <p>Lat: {{ ubicacion.latitud }} | Lon: {{ ubicacion.longitud }}</p>

      <div class="mapa-acciones">
        <button @click="mostrarMapa = !mostrarMapa">
          {{ mostrarMapa ? 'Ocultar mapa' : 'Ver en Google Maps' }}
        </button>
        <a :href="mapaEnlaceExterno" target="_blank" rel="noopener">Abrir en Google Maps ↗</a>
      </div>

      <iframe
        v-if="mostrarMapa"
        :src="mapaEmbedUrl"
        class="mapa-embed"
        loading="lazy"
        referrerpolicy="no-referrer-when-downgrade"
      ></iframe>
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
.direccion { color: #555; margin-top: -0.5rem; }
.mapa-acciones { display: flex; gap: 1rem; align-items: center; margin-top: 0.75rem; }
.mapa-embed {
  width: 100%;
  height: 300px;
  border: 0;
  border-radius: 8px;
  margin-top: 1rem;
}
form { display: flex; gap: 0.5rem; margin-top: 0.5rem; }
</style>