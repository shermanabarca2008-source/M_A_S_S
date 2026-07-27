package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "horario_medico")
public class HorarioMedico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoHorario estado;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private AgendaMedico agendaMedico;

    public HorarioMedico(LocalDate fecha,
                         LocalTime inicio,
                         LocalTime fin,
                         Medico medico) {

        this.fecha = fecha;
        this.horaInicio = inicio;
        this.horaFin = fin;
        this.medico = medico;
        this.estado = EstadoHorario.DISPONIBLE;
    }
    
    public HorarioMedico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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

    public EstadoHorario getEstado() {
        return estado;
    }

    public void setEstado(EstadoHorario estado) {
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

    public boolean disponible() {
        return estado == EstadoHorario.DISPONIBLE;
    }

    public void setDisponible(boolean disponible) {
        this.estado = disponible ? EstadoHorario.DISPONIBLE : EstadoHorario.OCUPADO;
    }

    public AgendaMedico getAgendaMedico() {
        return agendaMedico;
    }

    public void setAgendaMedico(AgendaMedico agendaMedico) {
        this.agendaMedico = agendaMedico;
    }
}