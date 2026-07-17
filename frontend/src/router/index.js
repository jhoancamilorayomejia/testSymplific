import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import EmpleadosListView from '../views/EmpleadosListView.vue'
import EmpleadoDetalleView from '../views/EmpleadoDetalleView.vue'
import EmpleadoFormView from '../views/EmpleadoFormView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    { path: '/', name: 'empleados', component: EmpleadosListView, meta: { requiresAuth: true } },
    { path: '/empleados/nuevo', name: 'empleado-nuevo', component: EmpleadoFormView, meta: { requiresAuth: true } },
    { path: '/empleados/:id', name: 'empleado-detalle', component: EmpleadoDetalleView, meta: { requiresAuth: true } }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router