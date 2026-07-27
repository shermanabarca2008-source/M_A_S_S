package unl.edu.ec.M_A_S_S.service;

import unl.edu.ec.M_A_S_S.domain.*;

public class CitaService {

    /**
     * Reserva un horario.
     */
    public boolean reservar(HorarioMedico horario, Paciente paciente){

        if(horario==null){

            return false;

        }

        if(horario.getEstado()!=EstadoHorario.DISPONIBLE){

            return false;

        }

        horario.setEstado(EstadoHorario.OCUPADO);
        horario.setPaciente(paciente);

        return true;

    }

    /**
     * Cancelar una cita.
     */
    public boolean cancelar(HorarioMedico horario){

        if(horario==null){

            return false;

        }

        horario.setEstado(EstadoHorario.DISPONIBLE);

        return true;

    }

    /**
     * Reagendar.
     */
    public boolean reagendar(HorarioMedico origen,
                             HorarioMedico destino,
                             Paciente paciente){

        if(destino.getEstado()!=EstadoHorario.DISPONIBLE){

            return false;

        }

        cancelar(origen);

        reservar(destino,paciente);

        return true;

    }

}