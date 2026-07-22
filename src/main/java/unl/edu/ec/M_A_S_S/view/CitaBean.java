package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Notificacion;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class CitaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Cita citaActual;
    private List<Cita> citas;
    private Date nuevaFecha;
    private Time nuevaHora;
    private String mensaje;
    private boolean error;

    @PostConstruct
    public void init() {
        citas = new ArrayList<>();
        citaActual = new Cita();
    }

    public void registrarCita() {
        if (!datosCompletos(citaActual)) {
            mostrarError("Debe seleccionar la fecha, hora, médico y paciente.");
            return;
        }

        if (existeCruceDeHorario(citaActual)) {
            mostrarError("El médico ya tiene una cita en esa fecha y hora.");
            return;
        }

        citaActual.agendar();
        citas.add(citaActual);

        if (!citaActual.getMedico().getCitas().contains(citaActual)) {
            citaActual.getMedico().agregarCita(citaActual);
        }
        if (!citaActual.getPaciente().getCitas().contains(citaActual)) {
            citaActual.getPaciente().agendarCita(citaActual);
        }

        Notificacion notificacion = new Notificacion(
                "Su cita médica fue agendada correctamente.", new Date(), citaActual);
        citaActual.setNotificacion(notificacion);
        notificacion.enviarNotificacion();

        mostrarExito("Cita registrada correctamente.");
        citaActual = new Cita();
    }

    public void seleccionarCita(Cita cita) {
        citaActual = cita;
        nuevaFecha = cita != null ? cita.getFecha() : null;
        nuevaHora = cita != null ? cita.getHora() : null;
    }

    public void cancelarCita(Cita cita) {
        if (cita == null) {
            mostrarError("Seleccione una cita.");
            return;
        }

        cita.cancelar();
        crearNotificacion(cita, "Su cita médica fue cancelada.");
        mostrarExito("Cita cancelada correctamente.");
    }

    public void reagendarCita() {
        if (citaActual == null || nuevaFecha == null || nuevaHora == null) {
            mostrarError("Seleccione la cita, la nueva fecha y la nueva hora.");
            return;
        }

        Date fechaAnterior = citaActual.getFecha();
        Time horaAnterior = citaActual.getHora();
        citaActual.setFecha(nuevaFecha);
        citaActual.setHora(nuevaHora);

        if (existeCruceDeHorario(citaActual)) {
            citaActual.setFecha(fechaAnterior);
            citaActual.setHora(horaAnterior);
            mostrarError("El médico ya tiene una cita en la nueva fecha y hora.");
            return;
        }

        citaActual.reagendar(nuevaFecha, nuevaHora);
        crearNotificacion(citaActual, "Su cita médica fue reagendada.");
        mostrarExito("Cita reagendada correctamente.");
    }

    public void finalizarCita(Cita cita) {
        if (cita == null) {
            mostrarError("Seleccione una cita.");
            return;
        }
        cita.setEstado(Cita.EstadoCita.FINALIZADA);
        mostrarExito("Cita finalizada correctamente.");
    }

    public void eliminarCita(Cita cita) {
        if (cita == null || !citas.remove(cita)) {
            mostrarError("No se pudo eliminar la cita.");
            return;
        }
        if (cita.getMedico() != null) {
            cita.getMedico().getCitas().remove(cita);
        }
        if (cita.getPaciente() != null) {
            cita.getPaciente().getCitas().remove(cita);
        }
        if (cita == citaActual) {
            citaActual = new Cita();
        }
        mostrarExito("Cita eliminada correctamente.");
    }

    public void limpiarFormulario() {
        citaActual = new Cita();
        nuevaFecha = null;
        nuevaHora = null;
        mensaje = null;
        error = false;
    }

    private boolean datosCompletos(Cita cita) {
        return cita != null && cita.getFecha() != null && cita.getHora() != null
                && cita.getMedico() != null && cita.getPaciente() != null;
    }

    private boolean existeCruceDeHorario(Cita citaEvaluada) {
        for (Cita cita : citas) {
            if (cita != citaEvaluada
                    && cita.getEstado() != Cita.EstadoCita.CANCELADA
                    && cita.getMedico() == citaEvaluada.getMedico()
                    && cita.getFecha().equals(citaEvaluada.getFecha())
                    && cita.getHora().equals(citaEvaluada.getHora())) {
                return true;
            }
        }
        return false;
    }

    private void crearNotificacion(Cita cita, String texto) {
        Notificacion notificacion = new Notificacion(texto, new Date(), cita);
        cita.setNotificacion(notificacion);
        notificacion.enviarNotificacion();
    }

    private void mostrarExito(String texto) {
        mensaje = texto;
        error = false;
    }

    private void mostrarError(String texto) {
        mensaje = texto;
        error = true;
    }

    public Cita getCitaActual() {
        return citaActual;
    }

    public void setCitaActual(Cita citaActual) {
        this.citaActual = citaActual;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }

    public Date getNuevaFecha() {
        return nuevaFecha;
    }

    public void setNuevaFecha(Date nuevaFecha) {
        this.nuevaFecha = nuevaFecha;
    }

    public Time getNuevaHora() {
        return nuevaHora;
    }

    public void setNuevaHora(Time nuevaHora) {
        this.nuevaHora = nuevaHora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }
}
