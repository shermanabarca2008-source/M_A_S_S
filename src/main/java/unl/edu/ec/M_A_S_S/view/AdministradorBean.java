package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Administrador;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.Medico;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Named
@SessionScoped

public class AdministradorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Administrador administrador;
    private String nombreEspecialidad;
    private String descripcionEspecialidad;
    private String nombreMedico;
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
    private void cargarDatosIniciales() {
        Especialidad medicinaGeneral = new Especialidad("Medicina General", "Atención primaria");
        Especialidad pediatria = new Especialidad("Pediatría", "Atención infantil");

        administrador.gestionarEspecialidad(medicinaGeneral);
        administrador.gestionarEspecialidad(pediatria);

        Medico medico1 = new Medico("Dra. Ana Torres", medicinaGeneral);
        Medico medico2 = new Medico("Dr. Luis Pérez", pediatria);

        medicinaGeneral.agregarMedico(medico1);
        pediatria.agregarMedico(medico2);

        administrador.gestionarMedico(medico1);
        administrador.gestionarMedico(medico2);
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
        }
    }

    public void agregarMedico() {
        if (nombreMedico != null && !nombreMedico.trim().isEmpty()) {
            Especialidad especialidad = administrador.getEspecialidades().isEmpty()
                    ? null
                    : administrador.getEspecialidades().get(0);

            Medico medico = new Medico(nombreMedico.trim(), especialidad);
            administrador.gestionarMedico(medico);
            if (especialidad != null) {
                especialidad.agregarMedico(medico);
            }
            nombreMedico = "";
            mensajeAdmin = "Médico agregado correctamente.";
            errorAdmin = false;
        } else {
            mensajeAdmin = "Ingrese un nombre para el médico.";
            errorAdmin = true;
        }
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
