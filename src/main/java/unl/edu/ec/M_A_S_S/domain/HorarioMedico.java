package unl.edu.ec.M_A_S_S.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioMedico implements Serializable {

    // Atributos
    private LocalDate fechaDisponible;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean disponible;

    // Constructor
    public HorarioMedico(LocalDate fechaDisponible, LocalTime horaInicio,
                         LocalTime horaFin, boolean disponible) {

        this.fechaDisponible = fechaDisponible;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = disponible;
    }

    // Getters y Setters
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

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    // Método para mostrar información
    public void mostrarHorario() {

        System.out.println("===== HORARIO MÉDICO =====");
        System.out.println("Fecha disponible: " + fechaDisponible);
        System.out.println("Hora inicio: " + horaInicio);
        System.out.println("Hora fin: " + horaFin);

        if (disponible) {
            System.out.println("Estado: Disponible");
        } else {
            System.out.println("Estado: No disponible");
        }
    }

    // Método para reservar horario
    public void reservarHorario() {

        if (disponible) {
            disponible = false;
            System.out.println("Horario reservado correctamente.");
        } else {
            System.out.println("El horario ya no está disponible.");
        }
    }

    // Método principal
    public static void main(String[] args) {

        HorarioMedico horario1 = new HorarioMedico(
                LocalDate.of(2026, 5, 30),
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                true
        );

        horario1.mostrarHorario();

        System.out.println("\nReservando horario...\n");

        horario1.reservarHorario();

        System.out.println();

        horario1.mostrarHorario();
    }
}
