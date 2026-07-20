package unl.edu.ec.M_A_S_S.domain;

import java.io.Serializable;

public class IndicacionesMedicas implements Serializable {

    // Atributos
    private String diagnostico;
    private String tratamiento;
    private String observaciones;

    // Relaciones UML
    private Cita cita;

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
        this.cita = cita;
    }

    // Getters y Setters
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