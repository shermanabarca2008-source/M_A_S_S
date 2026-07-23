package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import unl.edu.ec.M_A_S_S.domain.Administrador;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.Medico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class AdministradorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "massPU")
    private EntityManager em;

    private String nombreEspecialidad;
    private String descripcionEspecialidad;
    private String nombreMedico;
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
    public void agregarEspecialidad() {
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
        } else {
            mensajeAdmin = "Ingrese el nombre de la especialidad.";
            errorAdmin = true;
        }
    }

    public void ocultarFormulario() {
        formularioVisible = false;
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

        List<Especialidad> especialidades = resolverEspecialidades(especialidadesNuevoMedico);
        if (especialidades.isEmpty()) {
            mensajeAdmin = "Seleccione al menos una especialidad.";
            errorAdmin = true;
            return null;
        }

        Medico medico = new Medico(nombreMedico.trim(), especialidades);
        em.persist(medico);
        nombreMedico = "";
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
