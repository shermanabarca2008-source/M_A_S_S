package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "horario_medico")
public class HorarioMedico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha disponible es obligatoria")
    private LocalDate fechaDisponible;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private boolean disponible;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    // Constructor
    public HorarioMedico() {
    }

    public HorarioMedico(LocalDate fechaDisponible, LocalTime horaInicio,
                         LocalTime horaFin, boolean disponible) {

        this.fechaDisponible = fechaDisponible;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = disponible;
    }

    // Getters y Setters
    public Long getId() {
        return id;
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

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
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
}
