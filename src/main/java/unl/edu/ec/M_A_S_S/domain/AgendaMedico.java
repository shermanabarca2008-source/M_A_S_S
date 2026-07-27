package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agenda_medico")
public class AgendaMedico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "agendaMedico")
    private Medico medico;

    @OneToMany(
            mappedBy = "agendaMedico",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DiaAgenda> agenda = new ArrayList<>();

    public AgendaMedico() {
    }

    public Long getId() {
        return id;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public List<DiaAgenda> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<DiaAgenda> agenda) {
        this.agenda = agenda;
    }
}