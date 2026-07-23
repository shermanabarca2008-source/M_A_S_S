package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Medico;
import unl.edu.ec.M_A_S_S.domain.Notificacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Named
@SessionScoped
public class MedicoSesionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Medico medicoActual;

    public Medico getMedicoActual() {
        return medicoActual;
    }

    public void setMedicoActual(Medico medicoActual) {
        this.medicoActual = medicoActual;
    }

    public List<Cita> getCitasOrdenadas() {
        List<Cita> citas = new ArrayList<>(medicoActual.getCitas());
        citas.sort(Comparator.comparing(Cita::getFecha).thenComparing(Cita::getHora));
        return citas;
    }

    public int getTotalCitas() {
        return medicoActual.getCitas().size();
    }

    public int getCitasAgendadas() {
        return contarPorEstado(Cita.EstadoCita.AGENDADA);
    }

    public int getCitasFinalizadas() {
        return contarPorEstado(Cita.EstadoCita.FINALIZADA);
    }

    public int getCitasCanceladas() {
        return contarPorEstado(Cita.EstadoCita.CANCELADA);
    }

    public List<Notificacion> getNotificaciones() {
        List<Notificacion> resultado = new ArrayList<>();
        for (Cita cita : medicoActual.getCitas()) {
            if (cita.getNotificacion() != null) {
                resultado.add(cita.getNotificacion());
            }
        }
        resultado.sort(Comparator.comparing(Notificacion::getFechaEnvio).reversed());
        return resultado;
    }

    private int contarPorEstado(Cita.EstadoCita estado) {
        int total = 0;
        for (Cita cita : medicoActual.getCitas()) {
            if (cita.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }
}
