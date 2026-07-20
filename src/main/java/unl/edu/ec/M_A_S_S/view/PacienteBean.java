package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.HorarioMedico;
import unl.edu.ec.M_A_S_S.domain.IndicacionesMedicas;
import unl.edu.ec.M_A_S_S.domain.Medico;
import unl.edu.ec.M_A_S_S.domain.Notificacion;
import unl.edu.ec.M_A_S_S.domain.Paciente;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class PacienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Paciente> pacientes = new ArrayList<>();
    private String cedula;
    private String contrasena;
    private String mensaje;
    private boolean error;
    private Paciente pacienteActual;
    private String especialidadSeleccionada;
    private String medicoSeleccionado;
    private String horarioSeleccionado;
    private String citaSeleccionada;
    private String tabActiva = "agendar";
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private String direccion;
    private String fechaNacimiento;

    public String iniciarSesion() {
        if ("admin".equals(cedula) && "admin123".equals(contrasena)) {
            mensaje = "Acceso administrador correcto.";
            error = false;
            return "/gestionAdmin.xhtml?faces-redirect=true";
        }

        pacienteActual = null;
        for (Paciente paciente : pacientes) {
            if (cedula != null && cedula.equals(paciente.getCedula())
                    && contrasena != null && contrasena.equals(paciente.getContrasena())) {
                pacienteActual = paciente;
                break;
            }
        }

        if (pacienteActual != null) {
            mensaje = "Bienvenido " + pacienteActual.getNombreCompleto() + ".";
            error = false;
            return "paciente";
        }

        mensaje = "Cédula o contraseña incorrecta.";
        error = true;
        return null;
    }

    public String registrarPaciente() {

        if (!telefono.matches("\\d{10}")) {
            mensaje = "El teléfono debe contener 10 dígitos.";
            error = true;
            return null;
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            mensaje = "Correo electrónico inválido.";
            error = true;
            return null;
        }

        if (!cedula.matches("\\d{10}")) {
            mensaje = "La cédula debe contener 10 dígitos.";
            error = true;
            return null;
        }

        if (cedula.trim().isEmpty() || contrasena == null || contrasena.trim().isEmpty() || nombres == null
                || nombres.trim().isEmpty() || apellidos == null
                || apellidos.trim().isEmpty() || telefono.trim().isEmpty()
                || correo.trim().isEmpty() || direccion == null || direccion.trim().isEmpty()) {

            mensaje = "Debe completar todos los campos.";
            error = true;
            return null;
        }

        // Verificar que la cédula no exista
        for (Paciente paciente : pacientes) {
            if (cedula.equals(paciente.getCedula())) {
                mensaje = "Ya existe un paciente registrado con esa cédula.";
                error = true;
                return null;
            }
        }

        // Verificar que el correo no exista
        for (Paciente paciente : pacientes) {
            if (correo.equalsIgnoreCase(paciente.getCorreo())) {
                mensaje = "El correo electrónico ya está registrado.";
                error = true;
                return null;
            }
        }

        Paciente nuevo = new Paciente();

        nuevo.setCedula(cedula);
        nuevo.setContrasena(contrasena);
        nuevo.setNombres(nombres);
        nuevo.setApellidos(apellidos);
        nuevo.setTelefono(telefono);
        nuevo.setCorreo(correo);
        nuevo.setDireccion(direccion);

        if (fechaNacimiento != null && !fechaNacimiento.isBlank()) {
            nuevo.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        }

        pacientes.add(nuevo);

        pacienteActual = nuevo;

        mensaje = "Registro exitoso.";
        error = false;

        limpiarFormulario();

        return "/index.xhtml?faces-redirect=true";
    }

    private void limpiarFormulario() {

        cedula = "";
        contrasena = "";
        nombres = "";
        apellidos = "";
        telefono = "";
        correo = "";
        direccion = "";
        fechaNacimiento = null;

    }

    public String agendarCita() {
        if (pacienteActual == null) {
            mensaje = "Debe iniciar sesión primero.";
            error = true;
            return null;
        }

        if (especialidadSeleccionada == null || medicoSeleccionado == null || horarioSeleccionado == null) {
            mensaje = "Seleccione especialidad, médico y horario.";
            error = true;
            return null;
        }

        Especialidad especialidad = encontrarEspecialidadPorNombre(especialidadSeleccionada);
        Medico medico = encontrarMedicoPorNombre(medicoSeleccionado, especialidad);
        HorarioMedico horario = encontrarHorarioPorTexto(horarioSeleccionado, medico);

        if (especialidad == null || medico == null || horario == null) {
            mensaje = "No fue posible crear la cita.";
            error = true;
            return null;
        }

        Cita cita = new Cita(Date.from(horario.getFechaDisponible().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()),
                Time.valueOf(horario.getHoraInicio()), medico, pacienteActual);
        cita.agendar();
        pacienteActual.agendarCita(cita);
        horario.reservarHorario();
        medico.agregarCita(cita);
        cita.setNotificacion(new Notificacion("Cita agendada correctamente", new Date(), cita));
        cita.getNotificacion().enviarNotificacion();

        mensaje = "Cita agendada correctamente.";
        error = false;
        return null;
    }

    public String cancelarCita() {
        if (pacienteActual == null || citaSeleccionada == null) {
            mensaje = "Seleccione una cita para cancelar.";
            error = true;
            return null;
        }

        for (Cita cita : pacienteActual.getCitas()) {
            if (citaSeleccionada.equals(cita.getMedico().getNombreCompleto() + " | " + cita.getFecha() + " | " + cita.getHora())) {
                cita.cancelar();
                if (cita.getNotificacion() != null) {
                    cita.getNotificacion().enviarNotificacion();
                }
                mensaje = "Cita cancelada correctamente.";
                error = false;
                return null;
            }
        }

        mensaje = "No se encontró la cita seleccionada.";
        error = true;
        return null;
    }

    public String seleccionarTab(String tab) {
        this.tabActiva = tab;
        return null;
    }

    public boolean isTabActiva(String tab) {
        return tab != null && tab.equals(tabActiva);
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Especialidad> getEspecialidades() {
        return obtenerEspecialidadesBase();
    }

    public List<Medico> getMedicosDisponibles() {
        List<Medico> resultado = new ArrayList<>();
        Especialidad especialidad = encontrarEspecialidadPorNombre(especialidadSeleccionada);
        if (especialidad != null) {
            for (Medico medico : especialidad.getMedicos()) {
                resultado.add(medico);
            }
        }
        return resultado;
    }

    public List<HorarioMedico> getHorariosDisponibles() {
        List<HorarioMedico> resultado = new ArrayList<>();
        Medico medico = encontrarMedicoPorNombre(medicoSeleccionado, encontrarEspecialidadPorNombre(especialidadSeleccionada));
        if (medico != null) {
            for (HorarioMedico horario : medico.getHorarios()) {
                if (horario.isDisponible()) {
                    resultado.add(horario);
                }
            }
        }
        return resultado;
    }

    public List<String> getCitasPaciente() {
        List<String> result = new ArrayList<>();
        if (pacienteActual != null) {
            for (Cita cita : pacienteActual.getCitas()) {
                result.add(cita.getMedico().getNombreCompleto() + " | " + cita.getFecha() + " | " + cita.getHora() + " | " + cita.getEstado());
            }
        }
        return result;
    }

    public List<String> getHistorialClinico() {
        List<String> result = new ArrayList<>();
        if (pacienteActual != null) {
            for (Cita cita : pacienteActual.getCitas()) {
                if (cita.getIndicaciones() != null && !cita.getIndicaciones().isEmpty()) {
                    for (IndicacionesMedicas indicacion : cita.getIndicaciones()) {
                        result.add("Cita: " + cita.getMedico().getNombreCompleto() + " | " + cita.getFecha() + " | " + indicacion.getDiagnostico() + " | " + indicacion.getTratamiento());
                    }
                } else {
                    result.add("Cita: " + cita.getMedico().getNombreCompleto() + " | " + cita.getFecha() + " | Sin indicaciones registradas");
                }
            }
        }
        return result;
    }

    private List<Especialidad> obtenerEspecialidadesBase() {
        List<Especialidad> resultado = new ArrayList<>();
        Especialidad medicinaGeneral = new Especialidad("Medicina General", "Atención primaria");
        Especialidad pediatria = new Especialidad("Pediatría", "Atención infantil");
        Medico medico1 = new Medico("Dra. Ana Torres", medicinaGeneral);
        Medico medico2 = new Medico("Dr. Luis Pérez", pediatria);
        HorarioMedico horario1 = new HorarioMedico(LocalDate.now().plusDays(1), LocalTime.of(8, 0), LocalTime.of(9, 0), true);
        HorarioMedico horario2 = new HorarioMedico(LocalDate.now().plusDays(2), LocalTime.of(10, 0), LocalTime.of(11, 0), true);
        medicinaGeneral.agregarMedico(medico1);
        pediatria.agregarMedico(medico2);
        medico1.getHorarios().add(horario1);
        medico2.getHorarios().add(horario2);
        resultado.add(medicinaGeneral);
        resultado.add(pediatria);
        return resultado;
    }

    private Especialidad encontrarEspecialidadPorNombre(String nombre) {
        for (Especialidad especialidad : getEspecialidades()) {
            if (especialidad.getNombre().equals(nombre)) {
                return especialidad;
            }
        }
        return null;
    }

    private Medico encontrarMedicoPorNombre(String nombre, Especialidad especialidad) {
        if (especialidad == null) {
            return null;
        }
        for (Medico medico : especialidad.getMedicos()) {
            if (medico.getNombreCompleto().equals(nombre)) {
                return medico;
            }
        }
        return null;
    }

    private HorarioMedico encontrarHorarioPorTexto(String texto, Medico medico) {
        if (medico == null) {
            return null;
        }
        for (HorarioMedico horario : medico.getHorarios()) {
            String valor = horario.getFechaDisponible().toString() + " " + horario.getHoraInicio().toString();
            if (valor.equals(texto)) {
                return horario;
            }
        }
        return null;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getEspecialidadSeleccionada() {
        return especialidadSeleccionada;
    }

    public void setEspecialidadSeleccionada(String especialidadSeleccionada) {
        this.especialidadSeleccionada = especialidadSeleccionada;
    }

    public String getMedicoSeleccionado() {
        return medicoSeleccionado;
    }

    public void setMedicoSeleccionado(String medicoSeleccionado) {
        this.medicoSeleccionado = medicoSeleccionado;
    }

    public String getHorarioSeleccionado() {
        return horarioSeleccionado;
    }

    public void setHorarioSeleccionado(String horarioSeleccionado) {
        this.horarioSeleccionado = horarioSeleccionado;
    }

    public String getCitaSeleccionada() {
        return citaSeleccionada;
    }

    public void setCitaSeleccionada(String citaSeleccionada) {
        this.citaSeleccionada = citaSeleccionada;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }

    public Paciente getPacienteActual() {
        return pacienteActual;
    }

    public String getTabActiva() {
        return tabActiva;
    }

    public void setTabActiva(String tabActiva) {
        this.tabActiva = tabActiva;
    }

    public String getNombres() {return nombres;}

    public void setNombres(String nombres) {this.nombres = nombres;}

    public String getApellidos() {return apellidos;}

    public void setApellidos(String apellidos) {this.apellidos = apellidos;}

    public String getTelefono() {return telefono;}

    public void setTelefono(String telefono) {this.telefono = telefono;}

    public String getCorreo() {return correo;}

    public void setCorreo(String correo) {this.correo = correo;}

    public String getDireccion() {return direccion;}

    public void setDireccion(String direccion) {this.direccion = direccion;}

    public String getFechaNacimiento() {return fechaNacimiento;}

    public void setFechaNacimiento(String fechaNacimiento) {this.fechaNacimiento = fechaNacimiento;}

    public String getTelfono() {return telefono;}
}
