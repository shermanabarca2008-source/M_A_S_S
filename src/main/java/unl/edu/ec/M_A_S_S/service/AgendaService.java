package unl.edu.ec.M_A_S_S.service;

import unl.edu.ec.M_A_S_S.domain.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaService {

    private AgendaMedico agendaMedico;
    private List<AgendaMedico> agendas = new ArrayList<>();

    public AgendaService() {

    }

    /**
     * Registra la agenda de un médico.
     */
    public void registrarAgenda(Medico medico){

        HorarioService horarioService = new HorarioService();

        AgendaMedico agenda = horarioService.generarAgenda(medico);

        medico.setAgendaMedico(agenda);
        agendas.add(agenda);

    }

    /**
     * Devuelve la agenda completa del médico.
     */
    public AgendaMedico obtenerAgenda(Medico medico){

        for(AgendaMedico agenda : agendas){

            if(agenda.getMedico().equals(medico)){

                return agenda;

            }

        }

        return null;

    }

    /**
     * Devuelve un día específico.
     */
    public DiaAgenda obtenerDia(Medico medico,
                                LocalDate fecha){

        AgendaMedico agenda = medico.getAgendaMedico();

        if(agenda == null){

            return null;

        }

        for(DiaAgenda dia : agenda.getAgenda()){

            if(dia.getFecha().equals(fecha)){

                return dia;

            }

        }

        return null;

    }

    /**
     * Horarios disponibles.
     */
    public List<HorarioMedico> horariosDisponibles(Medico medico,
                                                   LocalDate fecha){

        List<HorarioMedico> disponibles = new ArrayList<>();

        DiaAgenda dia = obtenerDia(medico,fecha);

        if(dia==null){

            return disponibles;

        }

        for(HorarioMedico horario : dia.getHorarios()){

            if(horario.getEstado()==EstadoHorario.DISPONIBLE){

                disponibles.add(horario);

            }

        }

        return disponibles;

    }

    /**
     * Buscar un horario específico.
     */
    public HorarioMedico buscarHorario(Medico medico,
                                       LocalDate fecha,
                                       LocalTime hora){

        DiaAgenda dia = obtenerDia(medico,fecha);

        if(dia==null){

            return null;

        }

        for(HorarioMedico horario : dia.getHorarios()){

            if(horario.getHoraInicio().equals(hora)){

                return horario;

            }

        }

        return null;

    }

}