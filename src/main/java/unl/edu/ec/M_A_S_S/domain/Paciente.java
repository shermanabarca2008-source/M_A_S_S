package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente")
public class Paciente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "\\d{10}", message = "La cédula debe contener 10 dígitos")
    private String cedula;

    private String nombreCompleto;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Correo electrónico inválido")
    private String correoElectronico;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    private LocalDate fechaNacimiento;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{10}", message = "El teléfono debe contener 10 dígitos")
    private String telefono;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    // Relación con Cita (un paciente puede tener varias citas)
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Cita> citas;

    // Constructor vacío
    public Paciente() {
        this.citas = new ArrayList<>();
    }

    // Constructor con parámetros
    public Paciente(String cedula, String nombreCompleto,
                    String correoElectronico, String contrasena,
                    LocalDate fechaNacimiento, String telefono) {

        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.correoElectronico = correoElectronico;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.citas = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters y Setters
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    // Método para agendar cita
    public boolean agendarCita(Cita cita) {

        if (cita == null) {
            System.out.println("No se pudo agendar la cita.");
            return false;
        }

        if (cita.getFecha() == null) {
            System.out.println("La cita no tiene fecha definida.");
            return false;
        }

        LocalDate fechaCita = Instant.ofEpochMilli(cita.getFecha().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate limite = LocalDate.now().plusMonths(3);
        if (fechaCita.isAfter(limite)) {
            System.out.println("No se puede agendar una cita a más de 3 meses de la fecha actual.");
            return false;
        }

        citas.add(cita);
        System.out.println("Cita agendada correctamente.");
        return true;
    }

    // Si no se agrega la cita por validaciones
    // devuelve false


    // Método para cancelar cita
    public void cancelarCita(Cita cita) {

        if (citas.remove(cita)) {
            System.out.println("Cita cancelada correctamente.");
        } else {
            System.out.println("La cita no existe.");
        }
    }

    // Método para reagendar cita
    public void reagendarCita(Cita citaAntigua, Cita nuevaCita) {

        if (citas.contains(citaAntigua)) {
            citas.remove(citaAntigua);
            citas.add(nuevaCita);
            System.out.println("Cita reagendada correctamente.");
        } else {
            System.out.println("No se encontró la cita a reagendar.");
        }
    }

    // Método toString
    @Override
    public String toString() {
        return "Paciente{" +
                "cedula='" + cedula + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", telefono='" + telefono + '\'' +
                '}';
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
        actualizarNombreCompleto();
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
        actualizarNombreCompleto();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    private void actualizarNombreCompleto() {
        this.nombreCompleto = ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim();
    }
}