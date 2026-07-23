package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cita")
public class Cita implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de la cita es obligatoria")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @NotNull(message = "La hora de la cita es obligatoria")
    private Time hora;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;

    // Relaciones UML
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<IndicacionesMedicas> indicaciones;

    @OneToOne(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private Notificacion notificacion;

    // Constructor vacío
    public Cita() {
        this.estado = EstadoCita.AGENDADA;
        this.indicaciones = new ArrayList<>();
    }

    // Constructor con parámetros
    public Cita(Date fecha, Time hora,
                Medico medico, Paciente paciente) {

        this.fecha = fecha;
        this.hora = hora;
        this.medico = medico;
        this.paciente = paciente;
        this.estado = EstadoCita.AGENDADA;
        this.indicaciones = new ArrayList<>();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<IndicacionesMedicas> getIndicaciones() {
        return indicaciones;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    // Método para agendar cita
    public void agendar() {

        this.estado = EstadoCita.AGENDADA;

        System.out.println("Cita agendada correctamente.");
    }

    // Método para cancelar cita
    public void cancelar() {

        this.estado = EstadoCita.CANCELADA;

        System.out.println("Cita cancelada correctamente.");
    }

    // Método para reagendar cita
    public void reagendar(Date nuevaFecha, Time nuevaHora) {

        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
        this.estado = EstadoCita.REAGENDADA;

        System.out.println("Cita reagendada correctamente.");
    }

    // Agregar indicación médica
    public void agregarIndicacion(
            IndicacionesMedicas indicacion) {

        if (indicacion != null) {
            indicaciones.add(indicacion);

            System.out.println(
                    "Indicación médica agregada.");
        }
    }

    // Mostrar información de la cita
    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
        String fechaStr = (fecha != null) ? df.format(fecha) : "Sin fecha";
        String horaStr = (hora != null) ? tf.format(hora) : "Sin hora";

        StringBuilder sb = new StringBuilder();
        sb.append("Doctor: ");
        sb.append(medico != null ? medico.getNombreCompleto() : "Sin médico");
        sb.append("\nFecha: ").append(fechaStr);
        sb.append("\nHora: ").append(horaStr);
        sb.append("\nEstado de la Cita: ").append(estado);
        return sb.toString();
    }
    public enum EstadoCita {

        AGENDADA,
        CANCELADA,
        REAGENDADA,
        FINALIZADA
    }
}
