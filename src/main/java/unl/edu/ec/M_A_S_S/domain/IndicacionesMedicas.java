package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Entity
@Table(name = "indicaciones_medicas")
public class IndicacionesMedicas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "El tratamiento es obligatorio")
    private String tratamiento;

    private String observaciones;

    // Relaciones UML
    @ManyToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    // Constructor vacío
    public IndicacionesMedicas() {
    }

    // Constructor con parámetros
    public IndicacionesMedicas(String diagnostico,
                               String tratamiento,
                               String observaciones,
                               Medico medico,
                               Cita cita) {

        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.medico = medico;
        this.cita = cita;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    // Método para registrar indicación médica
    public void registrarIndicacion() {

        System.out.println(
                "Indicación médica registrada correctamente."
        );
    }

    // Método para editar indicación médica
    public void editarIndicacion(String nuevoDiagnostico,
                                 String nuevoTratamiento,
                                 String nuevasObservaciones) {

        this.diagnostico = nuevoDiagnostico;
        this.tratamiento = nuevoTratamiento;
        this.observaciones = nuevasObservaciones;

        System.out.println(
                "Indicación médica editada correctamente."
        );
    }

    // Mostrar información
    @Override
    public String toString() {
        return "IndicacionesMedicas{" +
                "diagnostico='" + diagnostico + '\'' +
                ", tratamiento='" + tratamiento + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
