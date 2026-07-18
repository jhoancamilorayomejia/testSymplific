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
  actualizar(id, empleado) {
    return api.put(`/empleados/${id}`, empleado)
  },
  eliminar(id) {
    return api.delete(`/empleados/${id}`)
  },
  listarBeneficios(idEmpleado) {
    return api.get(`/empleados/${idEmpleado}/beneficios`)
  },
  crearBeneficio(idEmpleado, beneficio) {
    return api.post(`/empleados/${idEmpleado}/beneficios`, beneficio)
  },
  actualizarBeneficio(idEmpleado, idBeneficio, beneficio) {
    return api.put(`/empleados/${idEmpleado}/beneficios/${idBeneficio}`, beneficio)
  },
  eliminarBeneficio(idEmpleado, idBeneficio) {
    return api.delete(`/empleados/${idEmpleado}/beneficios/${idBeneficio}`)
  }
}