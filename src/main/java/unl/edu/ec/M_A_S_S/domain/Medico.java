package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import unl.edu.ec.M_A_S_S.service.HorarioService;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "medico")
public class Medico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del médico es obligatorio")
    private String nombreCompleto;

    @NotBlank(message = "El usuario de acceso es obligatorio")
    @Column(unique = true)
    private String usuario;

    @NotBlank(message = "La contraseña de acceso es obligatoria")
    private String contrasena;

    @NotEmpty(message = "Debe seleccionar al menos una especialidad")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "medico_especialidad",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidad_nombre"))
    private List<Especialidad> especialidades;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<HorarioMedico> horarios;

    @OneToMany(mappedBy = "medico", fetch = FetchType.EAGER)
    private List<Cita> citas;

    @OneToMany(mappedBy = "medico", fetch = FetchType.EAGER)
    private List<IndicacionesMedicas> indicaciones;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "agenda_id")
    private AgendaMedico agendaMedico;

    public Medico() {
        this.especialidades = new ArrayList<>();
        this.horarios = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.indicaciones = new ArrayList<>();
    }

    // Constructor con parámetros
    public Medico(String nombreCompleto, Especialidad especialidad) {
        this.nombreCompleto = nombreCompleto;
        this.especialidades = new ArrayList<>();
        if (especialidad != null) {
            this.especialidades.add(especialidad);
        }
        this.horarios = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.indicaciones = new ArrayList<>();
    }

    public void generarAgenda() {

        HorarioService horarioService = new HorarioService();

        this.agendaMedico = horarioService.generarAgenda(this);

    }

    public Medico(String nombreCompleto, List<Especialidad> especialidades) {
        this.nombreCompleto = nombreCompleto;
        this.especialidades = especialidades != null ? especialidades : new ArrayList<>();
        this.horarios = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.indicaciones = new ArrayList<>();
    }

    public AgendaMedico getAgendaMedico() {
        return agendaMedico;
    }

    public void setAgendaMedico(AgendaMedico agendaMedico) {
        this.agendaMedico = agendaMedico;
    }

    public void generarHorarioAutomatico() {

        horarios.clear();

        LocalTime hora = LocalTime.of(8,30);

        while (!hora.isAfter(LocalTime.of(16,30))) {

            // Receso de 12:00 a 13:00

            if(hora.equals(LocalTime.of(12,0))){

                hora = LocalTime.of(13,0);

                continue;
            }

            HorarioMedico horario = new HorarioMedico();

            horario.setHoraInicio(hora);

            horario.setHoraFin(hora.plusMinutes(30));

            horario.setDisponible(true);

            horarios.add(horario);

            hora = hora.plusMinutes(30);
        }

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
            horario.setMedico(this);
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
                ", especialidades=" + getEspecialidadesTexto() +
                '}';
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    public String getEspecialidadesTexto() {
        return especialidades.stream()
                .map(Especialidad::getNombre)
                .collect(Collectors.joining(", "));
    }
}
