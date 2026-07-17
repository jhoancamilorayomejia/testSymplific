import { createRouter, createWebHistory } from 'vue-router'
import EmpleadosListView from '../views/EmpleadosListView.vue'
import EmpleadoDetalleView from '../views/EmpleadoDetalleView.vue'
import EmpleadoFormView from '../views/EmpleadoFormView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'empleados', component: EmpleadosListView },
    { path: '/empleados/nuevo', name: 'empleado-nuevo', component: EmpleadoFormView },
    { path: '/empleados/:id', name: 'empleado-detalle', component: EmpleadoDetalleView }
  ]
})

export default router