package unl.edu.ec.M_A_S_S.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Medico implements Serializable {

    // Atributos
    private String nombreCompleto;

    // Relaciones UML
    private Especialidad especialidad;
    private List<HorarioMedico> horarios;
    private List<Cita> citas;
    private List<IndicacionesMedicas> indicaciones;

    // Constructor vacío
    public Medico() {
        this.horarios = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.indicaciones = new ArrayList<>();
    }

    // Constructor con parámetros
    public Medico(String nombreCompleto, Especialidad especialidad) {
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
        this.horarios = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.indicaciones = new ArrayList<>();
    }

    // Getters y Setters
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public List<HorarioMedico> getHorarios() {
        return horarios;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public List<IndicacionesMedicas> getIndicaciones() {
        return indicaciones;
    }

    // Registrar indicación médica
    public void registrarIndicacion(IndicacionesMedicas indicacion) {

        if (indicacion != null) {
            indicaciones.add(indicacion);
            System.out.println("Indicación médica registrada correctamente.");
        } else {
            System.out.println("No se pudo registrar la indicación.");
        }
    }

    // Editar indicación médica
    public void editarIndicacion(int indice,
                                 String nuevasObservaciones) {

        if (indice >= 0 &&
                indice < indicaciones.size()) {

            indicaciones.get(indice)
                    .setObservaciones(
                            nuevasObservaciones
                    );

            System.out.println(
                    "Indicación médica editada correctamente."
            );
        } else {

            System.out.println(
                    "No se encontró la indicación."
            );
        }
    }

    // Gestionar disponibilidad médica
    public void gestionarDisponibilidad(HorarioMedico horario) {

        if (horario != null) {
            horarios.add(horario);
            System.out.println("Horario agregado correctamente.");
        } else {
            System.out.println("No se pudo agregar el horario.");
        }
    }

    // Asignar cita al médico
    public void agregarCita(Cita cita) {

        if (cita != null) {
            citas.add(cita);
            System.out.println("Cita asignada correctamente.");
        } else {
            System.out.println("No se pudo asignar la cita.");
        }
    }

    // Mostrar información del médico
    @Override
    public String toString() {
        return "Medico{" +
                "nombreCompleto='" + nombreCompleto + '\'' +
                ", especialidad=" +
                (especialidad != null ?
                        especialidad.getNombre() :
                        "Sin especialidad") +
                '}';
    }
}