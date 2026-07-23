package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

    @PersistenceContext(unitName = "massPU")
    private EntityManager em;

    private Medico medicoSeleccionado;
    private LocalDate fechaDisponible;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String mensaje;
    private boolean error;

    public void seleccionarMedico(Medico medico) {
        medicoSeleccionado = medico != null ? em.find(Medico.class, medico.getId()) : null;
        limpiarCampos();
        mensaje = null;
        error = false;
    }

    public String irAHorarios(Medico medico) {
        seleccionarMedico(medico);
        return "gestionHorarios?faces-redirect=true";
    }

    @Transactional
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

        Medico administrado = em.find(Medico.class, medicoSeleccionado.getId());
        HorarioMedico nuevo = new HorarioMedico(fechaDisponible, horaInicio, horaFin, true);
        administrado.gestionarDisponibilidad(nuevo);
        em.persist(nuevo);
        medicoSeleccionado = administrado;
        mostrarExito("Horario registrado correctamente.");
        limpiarCampos();
    }

    @Transactional
    public void liberarHorario(HorarioMedico horario) {
        if (horario == null) {
            mostrarError("Seleccione un horario.");
            return;
        }
        HorarioMedico administrado = em.find(HorarioMedico.class, horario.getId());
        administrado.setDisponible(true);
        mostrarExito("Horario habilitado nuevamente.");
    }

    @Transactional
    public void eliminarHorario(HorarioMedico horario) {
        HorarioMedico administrado = horario != null ? em.find(HorarioMedico.class, horario.getId()) : null;
        if (administrado == null) {
            mostrarError("No se pudo eliminar el horario.");
            return;
        }
        if (administrado.getMedico() != null) {
            administrado.getMedico().getHorarios().remove(administrado);
        }
        em.remove(administrado);
        mostrarExito("Horario eliminado correctamente.");
    }

    public List<HorarioMedico> getHorariosMedicoSeleccionado() {
        if (medicoSeleccionado == null) {
            return new ArrayList<>();
        }
        return em.createQuery(
                        "SELECT h FROM HorarioMedico h WHERE h.medico = :medico ORDER BY h.fechaDisponible, h.horaInicio",
                        HorarioMedico.class)
                .setParameter("medico", medicoSeleccionado)
                .getResultList();
    }

    private boolean existeCruce() {
        for (HorarioMedico horario : getHorariosMedicoSeleccionado()) {
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
