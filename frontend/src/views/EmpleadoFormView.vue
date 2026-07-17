<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import empleadoService from '../services/empleadoService'

const router = useRouter()
const form = ref({
  nombre: '', apellido: '', email: '', cargo: '', ciudad: '', fechaIngreso: ''
})

async function guardar() {
  await empleadoService.crear(form.value)
  router.push('/')
}
</script>

<template>
  <router-link to="/">&larr; Volver</router-link>
  <h1>Nuevo empleado</h1>
  <form @submit.prevent="guardar">
    <input v-model="form.nombre" placeholder="Nombre" required />
    <input v-model="form.apellido" placeholder="Apellido" required />
    <input v-model="form.email" type="email" placeholder="Email" required />
    <input v-model="form.cargo" placeholder="Cargo" />
    <input v-model="form.ciudad" placeholder="Ciudad" required />
    <input v-model="form.fechaIngreso" type="date" />
    <button type="submit">Guardar</button>
  </form>
</template>

<style scoped>
form { display: flex; flex-direction: column; gap: 0.5rem; max-width: 400px; }
</style>