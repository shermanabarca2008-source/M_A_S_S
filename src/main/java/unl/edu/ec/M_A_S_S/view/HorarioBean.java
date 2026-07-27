package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import unl.edu.ec.M_A_S_S.domain.*;
import unl.edu.ec.M_A_S_S.service.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class HorarioBean implements Serializable {

    private AgendaService agendaService;

    private CitaService citaService;

    private Medico medicoSeleccionado;

    private LocalDate fechaSeleccionada;

    private List<HorarioMedico> horarios;

    public HorarioBean(){

    }

    @PostConstruct
    public void init(){

        agendaService = new AgendaService();

        citaService = new CitaService();

        horarios = new ArrayList<>();

    }

    public void cargarHorarios(){

        if(medicoSeleccionado == null){

            return;

        }

        if(fechaSeleccionada == null){

            return;

        }

        horarios = agendaService.horariosDisponibles(
                medicoSeleccionado,
                fechaSeleccionada
        );

    }

    public List<HorarioMedico> getHorarios(){

        return horarios;

    }

    public Medico getMedicoSeleccionado(){

        return medicoSeleccionado;

    }

    public void setMedicoSeleccionado(Medico medicoSeleccionado){

        this.medicoSeleccionado = medicoSeleccionado;

    }

    public LocalDate getFechaSeleccionada(){

        return fechaSeleccionada;

    }

    public void setFechaSeleccionada(LocalDate fechaSeleccionada){

        this.fechaSeleccionada = fechaSeleccionada;

    }

}