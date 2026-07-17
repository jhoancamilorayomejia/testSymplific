import api from './api'

export default {
  listar() {
    return api.get('/empleados')
  },
  obtener(id) {
    return api.get(`/empleados/${id}`)
  },
  crear(empleado) {
    return api.post('/empleados', empleado)
  },
  listarBeneficios(idEmpleado) {
    return api.get(`/empleados/${idEmpleado}/beneficios`)
  },
  crearBeneficio(idEmpleado, beneficio) {
    return api.post(`/empleados/${idEmpleado}/beneficios`, beneficio)
  }
}