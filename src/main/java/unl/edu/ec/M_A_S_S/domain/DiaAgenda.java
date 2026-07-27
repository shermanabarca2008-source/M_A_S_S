package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dia_agenda")
public class DiaAgenda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private AgendaMedico agendaMedico;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "dia_agenda_id")
    private List<HorarioMedico> horarios = new ArrayList<>();

    public DiaAgenda() {
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public AgendaMedico getAgendaMedico() {
        return agendaMedico;
    }

    public void setAgendaMedico(AgendaMedico agendaMedico) {
        this.agendaMedico = agendaMedico;
    }

    public List<HorarioMedico> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioMedico> horarios) {
        this.horarios = horarios;
    }
}