package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "especialidad")
public class Especialidad implements Serializable {

    @Id
    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    private String nombre;

    private String descripcion;

    @ManyToMany(mappedBy = "especialidades", fetch = FetchType.EAGER)
    private List<Medico> medicos;

    public Especialidad() {
        this.medicos = new ArrayList<>();
    }

    // Constructor con parámetros
    public Especialidad(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.medicos = new ArrayList<>();
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    // Método para agregar médico a la especialidad
    public void agregarMedico(Medico medico) {

        if (medico != null) {
            medicos.add(medico);
            System.out.println("Médico agregado a la especialidad correctamente.");
        } else {
            System.out.println("No se pudo agregar el médico.");
        }
    }

    @Override
    public String toString() {
        return "Especialidad: " +
                "\nNombre=" + nombre +
                "\nDescripción='" + descripcion +
                "\nTotal de médicos=" + medicos.size() +
                '}';
    }
}
