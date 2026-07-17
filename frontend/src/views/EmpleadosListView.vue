<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import empleadoService from '../services/empleadoService'

const empleados = ref([])
const cargando = ref(true)
const error = ref(null)
const router = useRouter()

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

onMounted(cargarEmpleados)
</script>

<template>
  <div>
    <div class="header">
      <h1>Empleados</h1>
      <router-link to="/empleados/nuevo">+ Nuevo empleado</router-link>
    </div>

    <p v-if="cargando">Cargando...</p>
    <p v-else-if="error">{{ error }}</p>

    <table v-else>
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Email</th>
          <th>Cargo</th>
          <th>Ciudad</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="emp in empleados" :key="emp.id">
          <td>{{ emp.nombre }} {{ emp.apellido }}</td>
          <td>{{ emp.email }}</td>
          <td>{{ emp.cargo }}</td>
          <td>{{ emp.ciudad }}</td>
          <td><button @click="verDetalle(emp.id)">Ver detalle</button></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
th, td { text-align: left; padding: 0.5rem; border-bottom: 1px solid #ddd; }
</style>