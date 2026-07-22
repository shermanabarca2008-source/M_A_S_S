package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.HorarioMedico;
import unl.edu.ec.M_A_S_S.domain.IndicacionesMedicas;
import unl.edu.ec.M_A_S_S.domain.Medico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
public class MedicoBean implements Serializable {


    private Medico medicoActual;
    private List<Medico> medicos;

    private String nombreBusqueda;

    private String mensaje;
    private boolean error;

    @PostConstruct
    public void init() {

        medicos = new ArrayList<>();
        medicoActual = new Medico();
    }

    public void registrarMedico() {

        if (medicoActual.getNombreCompleto() == null || medicoActual.getNombreCompleto().trim().isEmpty()) {
            mensaje = "Ingrese el nombre del médico.";
            error = true;
            return;
        }

        if (medicoActual.getEspecialidad() == null) {
            mensaje = "Seleccione una especialidad.";
            error = true;
            return;
        }
        for (Medico medico : medicos) {

            if (medico.getNombreCompleto().equalsIgnoreCase(medicoActual.getNombreCompleto())) {
                mensaje = "El médico ya existe.";
                error = true;
                return;
            }
        }
        medicos.add(medicoActual);
        mensaje = "Médico registrado correctamente.";
        error = false;
        medicoActual = new Medico();
    }

    public void buscarMedico() {
        for (Medico medico : medicos) {
            if (medico.getNombreCompleto().equalsIgnoreCase(nombreBusqueda)) {
                medicoActual = medico;
                mensaje = "Médico encontrado.";
                error = false;
                return;
            }
        }
        mensaje = "No existe el médico.";
        error = true;
    }

    public void actualizarMedico() {
        mensaje = "Información actualizada correctamente.";
        error = false;
    }

    public void eliminarMedico(Medico medico) {
        medicos.remove(medico);
        mensaje = "Médico eliminado correctamente.";
        error = false;

    }

    public void agregarHorario(HorarioMedico horario) {
        medicoActual.gestionarDisponibilidad(horario);
        mensaje = "Horario agregado.";
        error = false;
    }

    public void registrarIndicacion(IndicacionesMedicas indicacion) {
        medicoActual.registrarIndicacion(indicacion);
        mensaje = "Indicación registrada.";
        error = false;
    }

    public void agregarCita(Cita cita) {
        medicoActual.agregarCita(cita);
        mensaje = "Cita asignada.";
        error = false;
    }

    public void limpiarFormulario() {medicoActual = new Medico();}

    public Medico getMedicoActual() {
        return medicoActual;
    }

    public void setMedicoActual(Medico medicoActual) {
        this.medicoActual = medicoActual;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }

    public String getNombreBusqueda() {
        return nombreBusqueda;
    }

    public void setNombreBusqueda(String nombreBusqueda) {
        this.nombreBusqueda = nombreBusqueda;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }
}