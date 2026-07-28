package unl.edu.ec.M_A_S_S.service;

import unl.edu.ec.M_A_S_S.domain.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioService {

    /**
     * Genera la agenda completa de un médico para los próximos 90 días.
     */
    public AgendaMedico generarAgenda(Medico medico){

        AgendaMedico agenda = new AgendaMedico();

        agenda.setMedico(medico);

        LocalDate fecha = LocalDate.now();

        for(int i=0;i<90;i++){

            DiaAgenda dia = generarDia(medico,fecha);

            agenda.getAgenda().add(dia);

            fecha = fecha.plusDays(1);

        }

        return agenda;

    }

    /**
     * Genera un solo día.
     */
    private DiaAgenda generarDia(Medico medico,
                                 LocalDate fecha){

        DiaAgenda dia = new DiaAgenda();

        dia.setFecha(fecha);

        LocalTime hora = LocalTime.of(8,30);

        while(!hora.isAfter(LocalTime.of(17,30))){

            // Hora de almuerzo

            if(hora.equals(LocalTime.of(12,0))){

                HorarioMedico descanso = new HorarioMedico();

                descanso.setFecha(fecha);

                descanso.setHoraInicio(LocalTime.of(12,0));

                descanso.setHoraFin(LocalTime.of(13,0));

                descanso.setEstado(EstadoHorario.RECESO);

                descanso.setMedico(medico);

                dia.getHorarios().add(descanso);

                hora = LocalTime.of(13,0);

                continue;

            }

            HorarioMedico horario = new HorarioMedico();

            horario.setFecha(fecha);

            horario.setHoraInicio(hora);

            horario.setHoraFin(hora.plusMinutes(30));

            horario.setEstado(EstadoHorario.DISPONIBLE);

            horario.setMedico(medico);

            dia.getHorarios().add(horario);

            hora = hora.plusMinutes(30);

        }

        return dia;

    }

}