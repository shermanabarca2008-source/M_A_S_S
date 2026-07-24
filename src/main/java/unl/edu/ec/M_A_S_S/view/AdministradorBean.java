package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import unl.edu.ec.M_A_S_S.domain.Administrador;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.Medico;
import unl.edu.ec.M_A_S_S.domain.Paciente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class AdministradorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "massPU")
    private EntityManager em;

    private String nombreEspecialidad;
    private String descripcionEspecialidad;
    private String nombreMedico;
    private String usuarioNuevoMedico;
    private String contrasenaNuevoMedico;
    private List<String> especialidadesNuevoMedico = new ArrayList<>();
    private String usuarioAdmin;
    private String passwordAdmin;
    private String mensajeAdmin;
    private boolean errorAdmin;
    private Medico medicoSeleccionado;
    private String nombreMedicoEdicion;
    private List<String> especialidadesSeleccionadasEdicion = new ArrayList<>();
    private boolean modoEdicion;

    private boolean formularioVisible = false;

    private String terminoBusqueda;
    private boolean busquedaRealizada;

    public Date getFechaActual() {
        return new Date();
    }

    public void buscar(AjaxBehaviorEvent event) {
        busquedaRealizada = terminoBusqueda != null && !terminoBusqueda.trim().isEmpty();
    }

    public List<Medico> getResultadosMedicosBusqueda() {
        if (!busquedaRealizada) {
            return List.of();
        }
        String termino = "%" + terminoBusqueda.trim().toLowerCase() + "%";
        return em.createQuery(
                        "SELECT DISTINCT m FROM Medico m LEFT JOIN m.especialidades e "
                                + "WHERE LOWER(m.nombreCompleto) LIKE :termino OR LOWER(e.nombre) LIKE :termino "
                                + "ORDER BY m.nombreCompleto",
                        Medico.class)
                .setParameter("termino", termino)
                .getResultList();
    }

    public List<Paciente> getResultadosPacientesBusqueda() {
        if (!busquedaRealizada) {
            return List.of();
        }
        String termino = "%" + terminoBusqueda.trim().toLowerCase() + "%";
        return em.createQuery(
                        "SELECT p FROM Paciente p WHERE LOWER(p.nombreCompleto) LIKE :termino "
                                + "OR LOWER(p.cedula) LIKE :termino ORDER BY p.nombreCompleto",
                        Paciente.class)
                .setParameter("termino", termino)
                .getResultList();
    }

    public List<Especialidad> getResultadosEspecialidadesBusqueda() {
        if (!busquedaRealizada) {
            return List.of();
        }
        String termino = "%" + terminoBusqueda.trim().toLowerCase() + "%";
        return em.createQuery(
                        "SELECT e FROM Especialidad e WHERE LOWER(e.nombre) LIKE :termino ORDER BY e.nombre",
                        Especialidad.class)
                .setParameter("termino", termino)
                .getResultList();
    }

    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public boolean isBusquedaRealizada() {
        return busquedaRealizada;
    }

    public void mostrarFormulario() {
        formularioVisible = true;
    }

    public boolean isFormularioVisible() {
        return formularioVisible;
    }

    public void setFormularioVisible(boolean formularioVisible) {
        this.formularioVisible = formularioVisible;
    }

    @Transactional
    public String agregarEspecialidad() {
        if (nombreEspecialidad != null && !nombreEspecialidad.trim().isEmpty()) {
            Especialidad especialidad = new Especialidad(
                    nombreEspecialidad.trim(),
                    descripcionEspecialidad != null ? descripcionEspecialidad.trim() : ""
            );
            em.persist(especialidad);
            nombreEspecialidad = "";
            descripcionEspecialidad = "";
            formularioVisible = false;
            mensajeAdmin = "Especialidad agregada correctamente.";
            errorAdmin = false;
            return "gestionEspecialidades?faces-redirect=true";
        } else {
            mensajeAdmin = "Ingrese el nombre de la especialidad.";
            errorAdmin = true;
            return null;
        }
    }

    public void ocultarFormulario() {
        formularioVisible = false;
    }

    public String cancelarNuevaEspecialidad() {
        nombreEspecialidad = "";
        descripcionEspecialidad = "";
        mensajeAdmin = null;
        errorAdmin = false;
        return "gestionEspecialidades?faces-redirect=true";
    }

    @Transactional
    public void eliminarEspecialidad(Especialidad especialidad) {
        Especialidad administrada = especialidad != null ? em.find(Especialidad.class, especialidad.getNombre()) : null;
        if (administrada != null) {
            em.remove(administrada);
            mensajeAdmin = "Especialidad eliminada correctamente.";
            errorAdmin = false;
        } else {
            mensajeAdmin = "No se pudo eliminar la especialidad.";
            errorAdmin = true;
        }
    }

    @Transactional
    public String agregarMedico() {
        if (nombreMedico == null || nombreMedico.trim().isEmpty()) {
            mensajeAdmin = "Ingrese un nombre para el médico.";
            errorAdmin = true;
            return null;
        }

        if (usuarioNuevoMedico == null || usuarioNuevoMedico.trim().isEmpty()
                || contrasenaNuevoMedico == null || contrasenaNuevoMedico.trim().isEmpty()) {
            mensajeAdmin = "Ingrese el usuario y la contraseña de acceso del médico.";
            errorAdmin = true;
            return null;
        }

        Long usuariosExistentes = em.createQuery(
                        "SELECT COUNT(m) FROM Medico m WHERE m.usuario = :usuario", Long.class)
                .setParameter("usuario", usuarioNuevoMedico.trim())
                .getSingleResult();
        if (usuariosExistentes > 0) {
            mensajeAdmin = "Ya existe un médico registrado con ese usuario.";
            errorAdmin = true;
            return null;
        }

        List<Especialidad> especialidades = resolverEspecialidades(especialidadesNuevoMedico);
        if (especialidades.isEmpty()) {
            mensajeAdmin = "Seleccione al menos una especialidad.";
            errorAdmin = true;
            return null;
        }

        Medico medico = new Medico(nombreMedico.trim(), especialidades);
        medico.setUsuario(usuarioNuevoMedico.trim());
        medico.setContrasena(contrasenaNuevoMedico.trim());
        em.persist(medico);
        nombreMedico = "";
        usuarioNuevoMedico = "";
        contrasenaNuevoMedico = "";
        especialidadesNuevoMedico = new ArrayList<>();
        mensajeAdmin = "Médico agregado correctamente.";
        errorAdmin = false;
        return "gestionMedicos?faces-redirect=true";
    }

    public void seleccionarMedicoParaEditar(Medico medico) {
        medicoSeleccionado = medico;
        nombreMedicoEdicion = medico.getNombreCompleto();
        especialidadesSeleccionadasEdicion = new ArrayList<>();
        for (Especialidad especialidad : medico.getEspecialidades()) {
            especialidadesSeleccionadasEdicion.add(especialidad.getNombre());
        }
        modoEdicion = true;
    }

    @Transactional
    public void guardarEdicionMedico() {
        if (medicoSeleccionado == null || nombreMedicoEdicion == null || nombreMedicoEdicion.trim().isEmpty()) {
            mensajeAdmin = "Ingrese un nombre válido para el médico.";
            errorAdmin = true;
            return;
        }

        List<Especialidad> nuevasEspecialidades = resolverEspecialidades(especialidadesSeleccionadasEdicion);
        if (nuevasEspecialidades.isEmpty()) {
            mensajeAdmin = "Seleccione al menos una especialidad.";
            errorAdmin = true;
            return;
        }

        Medico administrado = em.find(Medico.class, medicoSeleccionado.getId());
        administrado.setNombreCompleto(nombreMedicoEdicion.trim());
        administrado.setEspecialidades(nuevasEspecialidades);

        mensajeAdmin = "Médico actualizado correctamente.";
        errorAdmin = false;
        cancelarEdicion();
    }

    public void cancelarEdicion() {
        modoEdicion = false;
        medicoSeleccionado = null;
        nombreMedicoEdicion = "";
        especialidadesSeleccionadasEdicion = new ArrayList<>();
        mensajeAdmin = "Edición cancelada.";
        errorAdmin = false;
    }

    @Transactional
    public void eliminarMedico(Medico medico) {
        Medico administrado = medico != null ? em.find(Medico.class, medico.getId()) : null;
        if (administrado != null) {
            em.remove(administrado);
            mensajeAdmin = "Médico eliminado correctamente.";
            errorAdmin = false;
        } else {
            mensajeAdmin = "No se pudo eliminar el médico.";
            errorAdmin = true;
        }
    }

    private List<Especialidad> resolverEspecialidades(List<String> nombres) {
        List<Especialidad> resultado = new ArrayList<>();
        if (nombres == null) {
            return resultado;
        }
        for (String nombre : nombres) {
            Especialidad especialidad = em.find(Especialidad.class, nombre);
            if (especialidad != null) {
                resultado.add(especialidad);
            }
        }
        return resultado;
    }

    public String validarAccesoAdministrador() {
        List<Administrador> resultado = em.createQuery(
                        "SELECT a FROM Administrador a WHERE a.usuario = :usuario AND a.contrasena = :contrasena",
                        Administrador.class)
                .setParameter("usuario", usuarioAdmin)
                .setParameter("contrasena", passwordAdmin)
                .getResultList();

        if (!resultado.isEmpty()) {
            mensajeAdmin = "Acceso correcto. Bienvenido administrador.";
            errorAdmin = false;
            return "admin";
        }
        mensajeAdmin = "Credenciales incorrectas. Intente nuevamente.";
        errorAdmin = true;
        return null;
    }

    public List<Especialidad> getEspecialidades() {
        return em.createQuery("SELECT e FROM Especialidad e ORDER BY e.nombre", Especialidad.class).getResultList();
    }

    public List<Medico> getMedicos() {
        return em.createQuery("SELECT m FROM Medico m ORDER BY m.nombreCompleto", Medico.class).getResultList();
    }

    public int getTotalPacientesRegistrados() {
        return em.createQuery("SELECT COUNT(p) FROM Paciente p", Long.class).getSingleResult().intValue();
    }

    public int getCitasHoy() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.fecha = CURRENT_DATE", Long.class)
                .getSingleResult().intValue();
    }

    public int getCitasCanceladasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.CANCELADA)
                .getSingleResult().intValue();
    }

    public int getCitasAgendadasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.AGENDADA)
                .getSingleResult().intValue();
    }

    public int getCitasFinalizadasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.FINALIZADA)
                .getSingleResult().intValue();
    }

    public int getCitasReagendadasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.REAGENDADA)
                .getSingleResult().intValue();
    }

    public int getTotalCitasRegistradas() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c", Long.class).getSingleResult().intValue();
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public String getDescripcionEspecialidad() {
        return descripcionEspecialidad;
    }

    public void setDescripcionEspecialidad(String descripcionEspecialidad) {
        this.descripcionEspecialidad = descripcionEspecialidad;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getUsuarioNuevoMedico() {
        return usuarioNuevoMedico;
    }

    public void setUsuarioNuevoMedico(String usuarioNuevoMedico) {
        this.usuarioNuevoMedico = usuarioNuevoMedico;
    }

    public String getContrasenaNuevoMedico() {
        return contrasenaNuevoMedico;
    }

    public void setContrasenaNuevoMedico(String contrasenaNuevoMedico) {
        this.contrasenaNuevoMedico = contrasenaNuevoMedico;
    }

    public List<String> getEspecialidadesNuevoMedico() {
        return especialidadesNuevoMedico;
    }

    public void setEspecialidadesNuevoMedico(List<String> especialidadesNuevoMedico) {
        this.especialidadesNuevoMedico = especialidadesNuevoMedico;
    }

    public String getUsuarioAdmin() {
        return usuarioAdmin;
    }

    public void setUsuarioAdmin(String usuarioAdmin) {
        this.usuarioAdmin = usuarioAdmin;
    }

    public String getPasswordAdmin() {
        return passwordAdmin;
    }

    public void setPasswordAdmin(String passwordAdmin) {
        this.passwordAdmin = passwordAdmin;
    }

    public String getMensajeAdmin() {
        return mensajeAdmin;
    }

    public boolean isErrorAdmin() {
        return errorAdmin;
    }

    public Medico getMedicoSeleccionado() {
        return medicoSeleccionado;
    }

    public void setMedicoSeleccionado(Medico medicoSeleccionado) {
        this.medicoSeleccionado = medicoSeleccionado;
    }

    public String getNombreMedicoEdicion() {
        return nombreMedicoEdicion;
    }

    public void setNombreMedicoEdicion(String nombreMedicoEdicion) {
        this.nombreMedicoEdicion = nombreMedicoEdicion;
    }

    public List<String> getEspecialidadesSeleccionadasEdicion() {
        return especialidadesSeleccionadasEdicion;
    }

    public void setEspecialidadesSeleccionadasEdicion(List<String> especialidadesSeleccionadasEdicion) {
        this.especialidadesSeleccionadasEdicion = especialidadesSeleccionadasEdicion;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

}
