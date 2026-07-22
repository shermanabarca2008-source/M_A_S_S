package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.HorarioMedico;
import unl.edu.ec.M_A_S_S.domain.Medico;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class HorarioMedicoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<HorarioMedico> horarios;
    private HorarioMedico horarioSeleccionado;
    private Medico medicoSeleccionado;
    private LocalDate fechaDisponible;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String mensaje;
    private boolean error;

    @PostConstruct
    public void init() {
        horarios = new ArrayList<>();
    }

    public void registrarHorario() {
        if (medicoSeleccionado == null || fechaDisponible == null
                || horaInicio == null || horaFin == null) {
            mostrarError("Debe seleccionar el médico, la fecha y las horas.");
            return;
        }
        if (fechaDisponible.isBefore(LocalDate.now())) {
            mostrarError("La fecha del horario no puede ser anterior a la fecha actual.");
            return;
        }
        if (!horaFin.isAfter(horaInicio)) {
            mostrarError("La hora de finalización debe ser posterior a la hora de inicio.");
            return;
        }
        if (existeCruce()) {
            mostrarError("El médico ya tiene un horario que coincide con ese intervalo.");
            return;
        }

        HorarioMedico nuevo = new HorarioMedico(fechaDisponible, horaInicio, horaFin, true);
        horarios.add(nuevo);
        medicoSeleccionado.gestionarDisponibilidad(nuevo);
        horarioSeleccionado = nuevo;
        mostrarExito("Horario registrado correctamente.");
        limpiarCampos();
    }

    public void reservarHorario(HorarioMedico horario) {
        if (horario == null || !horario.isDisponible()) {
            mostrarError("El horario seleccionado no está disponible.");
            return;
        }
        horario.reservarHorario();
        mostrarExito("Horario reservado correctamente.");
    }

    public void liberarHorario(HorarioMedico horario) {
        if (horario == null) {
            mostrarError("Seleccione un horario.");
            return;
        }
        horario.setDisponible(true);
        mostrarExito("Horario habilitado nuevamente.");
    }

    public void eliminarHorario(HorarioMedico horario) {
        if (horario == null || !horarios.remove(horario)) {
            mostrarError("No se pudo eliminar el horario.");
            return;
        }
        if (medicoSeleccionado != null) {
            medicoSeleccionado.getHorarios().remove(horario);
        }
        if (horario == horarioSeleccionado) {
            horarioSeleccionado = null;
        }
        mostrarExito("Horario eliminado correctamente.");
    }

    public List<HorarioMedico> getHorariosDisponibles() {
        List<HorarioMedico> disponibles = new ArrayList<>();
        for (HorarioMedico horario : horarios) {
            if (horario.isDisponible()) {
                disponibles.add(horario);
            }
        }
        return disponibles;
    }

    public void limpiarFormulario() {
        horarioSeleccionado = null;
        limpiarCampos();
        mensaje = null;
        error = false;
    }

    private boolean existeCruce() {
        for (HorarioMedico horario : medicoSeleccionado.getHorarios()) {
            if (fechaDisponible.equals(horario.getFechaDisponible())
                    && horaInicio.isBefore(horario.getHoraFin())
                    && horaFin.isAfter(horario.getHoraInicio())) {
                return true;
            }
        }
        return false;
    }

    private void limpiarCampos() {
        fechaDisponible = null;
        horaInicio = null;
        horaFin = null;
    }

    private void mostrarExito(String texto) {
        mensaje = texto;
        error = false;
    }

    private void mostrarError(String texto) {
        mensaje = texto;
        error = true;
    }

    public List<HorarioMedico> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioMedico> horarios) {
        this.horarios = horarios;
    }

    public HorarioMedico getHorarioSeleccionado() {
        return horarioSeleccionado;
    }

    public void setHorarioSeleccionado(HorarioMedico horarioSeleccionado) {
        this.horarioSeleccionado = horarioSeleccionado;
    }

    public Medico getMedicoSeleccionado() {
        return medicoSeleccionado;
    }

    public void setMedicoSeleccionado(Medico medicoSeleccionado) {
        this.medicoSeleccionado = medicoSeleccionado;
    }

    public LocalDate getFechaDisponible() {
        return fechaDisponible;
    }

    public void setFechaDisponible(LocalDate fechaDisponible) {
        this.fechaDisponible = fechaDisponible;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }
}
