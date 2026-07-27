package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import unl.edu.ec.M_A_S_S.domain.*;
import unl.edu.ec.M_A_S_S.service.*;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class CitaBean implements Serializable {

    private CitaService citaService;

    private HorarioMedico horarioSeleccionado;

    private Paciente pacienteActual;

    private Medico medicoSeleccionado;

    private List<Cita> citas;

    private String mensaje;

    @PostConstruct
    public void init(){

        citaService = new CitaService();

        citas = new ArrayList<>();

    }

    public String agendar(){

        if(horarioSeleccionado==null){

            mensaje="Seleccione un horario.";

            return null;

        }

        Cita cita = new Cita();

        cita.setPaciente(pacienteActual);

        cita.setMedico(medicoSeleccionado);

        cita.setHorario(horarioSeleccionado);

        LocalDate fechaLocal = horarioSeleccionado.getFecha();
        cita.setFecha(fechaLocal != null ? Date.from(fechaLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null);

        LocalTime horaLocal = horarioSeleccionado.getHoraInicio();
        cita.setHora(horaLocal != null ? Time.valueOf(horaLocal) : null);

        boolean resultado =
                citaService.reservar(horarioSeleccionado, pacienteActual);

        if(resultado){

            citas.add(cita);

            mensaje="Cita registrada correctamente.";

            return "misCitas?faces-redirect=true";

        }

        mensaje="El horario ya no está disponible.";

        return null;

    }

    public String cancelar(Cita cita){

        citaService.cancelar(cita.getHorario());

        citas.remove(cita);

        return null;

    }

    public String reagendar(Cita cita,
                            HorarioMedico nuevoHorario){

        boolean ok = citaService.reagendar(
                cita.getHorario(),
                nuevoHorario,
                cita.getPaciente()
        );

        if(ok){

            cita.setHorario(nuevoHorario);

            LocalDate fechaLocal = nuevoHorario.getFecha();
            cita.setFecha(fechaLocal != null ? Date.from(fechaLocal.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null);

            LocalTime horaLocal = nuevoHorario.getHoraInicio();
            cita.setHora(horaLocal != null ? Time.valueOf(horaLocal) : null);

        }

        return null;

    }

    public List<Cita> getCitas() {
        return citas;
    }

    public HorarioMedico getHorarioSeleccionado() {
        return horarioSeleccionado;
    }

    public void setHorarioSeleccionado(HorarioMedico horarioSeleccionado) {
        this.horarioSeleccionado = horarioSeleccionado;
    }

    public Paciente getPacienteActual() {
        return pacienteActual;
    }

    public void setPacienteActual(Paciente pacienteActual) {
        this.pacienteActual = pacienteActual;
    }

    public Medico getMedicoSeleccionado() {
        return medicoSeleccionado;
    }

    public void setMedicoSeleccionado(Medico medicoSeleccionado) {
        this.medicoSeleccionado = medicoSeleccionado;
    }

    public String getMensaje() {
        return mensaje;
    }

}