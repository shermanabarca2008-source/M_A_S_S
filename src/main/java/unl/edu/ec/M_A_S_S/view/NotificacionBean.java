package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Notificacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class NotificacionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Notificacion notificacionActual;
    private List<Notificacion> notificaciones;
    private Cita citaSeleccionada;
    private String mensaje;
    private boolean error;

    @PostConstruct
    public void init() {
        notificaciones = new ArrayList<>();
        notificacionActual = new Notificacion();
    }

    public void enviarNotificacion() {
        if (notificacionActual.getMensaje() == null
                || notificacionActual.getMensaje().isBlank()) {
            mostrarError("Debe ingresar el mensaje de la notificación.");
            return;
        }
        if (citaSeleccionada == null) {
            mostrarError("Debe seleccionar la cita asociada.");
            return;
        }

        notificacionActual.setFechaEnvio(new Date());
        notificacionActual.setCita(citaSeleccionada);
        citaSeleccionada.setNotificacion(notificacionActual);
        notificacionActual.enviarNotificacion();
        notificaciones.add(notificacionActual);

        mostrarExito("Notificación enviada correctamente.");
        notificacionActual = new Notificacion();
    }

    public void reenviarNotificacion(Notificacion notificacion) {
        if (notificacion == null) {
            mostrarError("Seleccione una notificación.");
            return;
        }
        notificacion.setFechaEnvio(new Date());
        notificacion.enviarNotificacion();
        mostrarExito("Notificación reenviada correctamente.");
    }

    public void eliminarNotificacion(Notificacion notificacion) {
        if (notificacion == null || !notificaciones.remove(notificacion)) {
            mostrarError("No se pudo eliminar la notificación.");
            return;
        }
        if (notificacion.getCita() != null
                && notificacion.getCita().getNotificacion() == notificacion) {
            notificacion.getCita().setNotificacion(null);
        }
        mostrarExito("Notificación eliminada correctamente.");
    }

    public void limpiarFormulario() {
        notificacionActual = new Notificacion();
        citaSeleccionada = null;
        mensaje = null;
        error = false;
    }

    private void mostrarExito(String texto) {
        mensaje = texto;
        error = false;
    }

    private void mostrarError(String texto) {
        mensaje = texto;
        error = true;
    }

    public Notificacion getNotificacionActual() {
        return notificacionActual;
    }

    public void setNotificacionActual(Notificacion notificacionActual) {
        this.notificacionActual = notificacionActual;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(List<Notificacion> notificaciones) {
        this.notificaciones = notificaciones;
    }

    public Cita getCitaSeleccionada() {
        return citaSeleccionada;
    }

    public void setCitaSeleccionada(Cita citaSeleccionada) {
        this.citaSeleccionada = citaSeleccionada;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }
}
