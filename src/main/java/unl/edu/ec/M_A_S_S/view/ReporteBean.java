package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Cita.EstadoCita;
import unl.edu.ec.M_A_S_S.domain.Medico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

@Named
@RequestScoped
public class ReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AdministradorBean administradorBean;

    @Inject
    private PacienteRepositorioBean pacienteRepositorioBean;

    public int getTotalPacientes() {
        return pacienteRepositorioBean.getPacientes().size();
    }

    public int getTotalMedicos() {
        return administradorBean.getMedicos().size();
    }

    public int getTotalEspecialidades() {
        return administradorBean.getEspecialidades().size();
    }

    public int getTotalCitas() {
        return getTodasLasCitas().size();
    }

    public int getCitasPorEstado(EstadoCita estado) {
        int total = 0;
        for (Cita cita : getTodasLasCitas()) {
            if (cita.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }

    public int getCitasAgendadas() {
        return getCitasPorEstado(EstadoCita.AGENDADA);
    }

    public int getCitasCanceladas() {
        return getCitasPorEstado(EstadoCita.CANCELADA);
    }

    public int getCitasReagendadas() {
        return getCitasPorEstado(EstadoCita.REAGENDADA);
    }

    public int getCitasFinalizadas() {
        return getCitasPorEstado(EstadoCita.FINALIZADA);
    }

    public List<Medico> getMedicosPorCantidadDeCitas() {
        List<Medico> medicos = new ArrayList<>(administradorBean.getMedicos());
        medicos.sort(Comparator.comparingInt(this::getCantidadCitasMesActual).reversed());
        return medicos;
    }

    public int getCantidadCitasMesActual(Medico medico) {
        if (medico == null || medico.getCitas() == null) {
            return 0;
        }
        int total = 0;
        for (Cita cita : medico.getCitas()) {
            if (esDelMesActual(cita)) {
                total++;
            }
        }
        return total;
    }

    private List<Cita> getTodasLasCitas() {
        List<Cita> citas = new ArrayList<>();
        for (Medico medico : administradorBean.getMedicos()) {
            for (Cita cita : medico.getCitas()) {
                if (esDelMesActual(cita)) {
                    citas.add(cita);
                }
            }
        }
        return citas;
    }

    private boolean esDelMesActual(Cita cita) {
        if (cita == null || cita.getFecha() == null) {
            return false;
        }

        Calendar fechaCita = Calendar.getInstance();
        fechaCita.setTime(cita.getFecha());
        Calendar hoy = Calendar.getInstance();
        return fechaCita.get(Calendar.YEAR) == hoy.get(Calendar.YEAR)
                && fechaCita.get(Calendar.MONTH) == hoy.get(Calendar.MONTH);
    }
}
