package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Administrador;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.Medico;

import java.io.Serializable;
import java.util.List;

@Named
@ApplicationScoped

public class AdministradorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Administrador administrador;
    private String nombreEspecialidad;
    private String descripcionEspecialidad;
    private String nombreMedico;
    private String especialidadNuevoMedico;
    private String usuarioAdmin;
    private String passwordAdmin;
    private String mensajeAdmin;
    private boolean errorAdmin;
    private Medico medicoSeleccionado;
    private String nombreMedicoEdicion;
    private String especialidadSeleccionadaEdicion;
    private boolean modoEdicion;

    private boolean formularioVisible = false;

    private Medico medico = new Medico();

    public void mostrarFormulario() {
        formularioVisible = true;
    }

    public boolean isFormularioVisible() {
        return formularioVisible;
    }

    public void setFormularioVisible(boolean formularioVisible) {
        this.formularioVisible = formularioVisible;
    }

    public Medico getMedico() {
        return medico;
    }

    @PostConstruct
    private void init() {
        administrador = new Administrador();
    }

    public void agregarEspecialidad() {
        if (nombreEspecialidad != null && !nombreEspecialidad.trim().isEmpty()) {
            Especialidad especialidad = new Especialidad(
                    nombreEspecialidad.trim(),
                    descripcionEspecialidad != null ? descripcionEspecialidad.trim() : ""
            );
            administrador.gestionarEspecialidad(especialidad);
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

    public void eliminarEspecialidad(Especialidad especialidad) {
        if (administrador != null && administrador.eliminarEspecialidad(especialidad)) {
            mensajeAdmin = "Especialidad eliminada correctamente.";
            errorAdmin = false;
        } else {
            mensajeAdmin = "No se pudo eliminar la especialidad.";
            errorAdmin = true;
        }
    }

    public String agregarMedico() {
        if (nombreMedico == null || nombreMedico.trim().isEmpty()) {
            mensajeAdmin = "Ingrese un nombre para el médico.";
            errorAdmin = true;
            return null;
        }

        Especialidad especialidad = encontrarEspecialidadPorNombre(especialidadNuevoMedico);
        if (especialidad == null) {
            mensajeAdmin = "Seleccione una especialidad válida.";
            errorAdmin = true;
            return null;
        }

        Medico medico = new Medico(nombreMedico.trim(), especialidad);
        administrador.gestionarMedico(medico);
        especialidad.agregarMedico(medico);
        nombreMedico = "";
        especialidadNuevoMedico = "";
        mensajeAdmin = "Médico agregado correctamente.";
        errorAdmin = false;
        return "gestionMedicos?faces-redirect=true";
    }

    public void seleccionarMedicoParaEditar(Medico medico) {
        medicoSeleccionado = medico;
        nombreMedicoEdicion = medico.getNombreCompleto();
        especialidadSeleccionadaEdicion = medico.getEspecialidad() != null ? medico.getEspecialidad().getNombre() : "";
        modoEdicion = true;
    }

    public void guardarEdicionMedico() {
        if (medicoSeleccionado == null || nombreMedicoEdicion == null || nombreMedicoEdicion.trim().isEmpty()) {
            mensajeAdmin = "Ingrese un nombre válido para el médico.";
            errorAdmin = true;
            return;
        }

        Especialidad nuevaEspecialidad = encontrarEspecialidadPorNombre(especialidadSeleccionadaEdicion);
        if (nuevaEspecialidad == null) {
            mensajeAdmin = "Seleccione una especialidad válida.";
            errorAdmin = true;
            return;
        }

        medicoSeleccionado.setNombreCompleto(nombreMedicoEdicion.trim());

        Especialidad especialidadAnterior = medicoSeleccionado.getEspecialidad();
        if (especialidadAnterior != nuevaEspecialidad) {
            if (especialidadAnterior != null) {
                especialidadAnterior.getMedicos().remove(medicoSeleccionado);
            }
            nuevaEspecialidad.agregarMedico(medicoSeleccionado);
            medicoSeleccionado.setEspecialidad(nuevaEspecialidad);
        }

        mensajeAdmin = "Médico actualizado correctamente.";
        errorAdmin = false;
        cancelarEdicion();
    }

    public void cancelarEdicion() {
        modoEdicion = false;
        medicoSeleccionado = null;
        nombreMedicoEdicion = "";
        especialidadSeleccionadaEdicion = "";
        mensajeAdmin = "Edición cancelada.";
        errorAdmin = false;
    }

    public void eliminarMedico(Medico medico) {
        if (administrador != null && administrador.eliminarMedico(medico)) {
            mensajeAdmin = "Médico eliminado correctamente.";
            errorAdmin = false;
        } else {
            mensajeAdmin = "No se pudo eliminar el médico.";
            errorAdmin = true;
        }
    }

    private Especialidad encontrarEspecialidadPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        for (Especialidad especialidad : administrador.getEspecialidades()) {
            if (nombre.equals(especialidad.getNombre())) {
                return especialidad;
            }
        }
        return null;
    }

    public String validarAccesoAdministrador() {
        if (administrador != null && "admin123".equals(passwordAdmin) && "admin".equalsIgnoreCase(usuarioAdmin)) {
            mensajeAdmin = "Acceso correcto. Bienvenido administrador.";
            errorAdmin = false;
            return "admin";
        }
        mensajeAdmin = "Credenciales incorrectas. Intente nuevamente.";
        errorAdmin = true;
        return null;
    }

    public List<Especialidad> getEspecialidades() {
        return administrador != null ? administrador.getEspecialidades() : List.of();
    }

    public List<Medico> getMedicos() {
        return administrador != null ? administrador.getMedicos() : List.of();
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

    public String getEspecialidadNuevoMedico() {
        return especialidadNuevoMedico;
    }

    public void setEspecialidadNuevoMedico(String especialidadNuevoMedico) {
        this.especialidadNuevoMedico = especialidadNuevoMedico;
    }

    public Administrador getAdministrador() {
        return administrador;
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

    public String getEspecialidadSeleccionadaEdicion() {
        return especialidadSeleccionadaEdicion;
    }

    public void setEspecialidadSeleccionadaEdicion(String especialidadSeleccionadaEdicion) {
        this.especialidadSeleccionadaEdicion = especialidadSeleccionadaEdicion;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

}
