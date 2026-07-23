package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.HorarioMedico;
import unl.edu.ec.M_A_S_S.domain.IndicacionesMedicas;
import unl.edu.ec.M_A_S_S.domain.Medico;
import unl.edu.ec.M_A_S_S.domain.Notificacion;
import unl.edu.ec.M_A_S_S.domain.Paciente;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class PacienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "massPU")
    private EntityManager em;

    @Inject
    private AdministradorBean administradorBean;

    @Inject
    private PacienteRepositorioBean pacienteRepositorioBean;

    private String cedula;
    private String contrasena;
    private String mensaje;
    private boolean error;
    private Paciente pacienteActual;
    private Paciente nuevoPaciente = new Paciente();
    private String especialidadSeleccionada;
    private String medicoSeleccionado;
    private String horarioSeleccionado;
    private String citaSeleccionada;
    private String tabActiva = "agendar";
    private Cita ultimaCitaAgendada;

    public String iniciarSesion() {
        if ("admin".equals(cedula) && "admin123".equals(contrasena)) {
            mensaje = "Acceso administrador correcto.";
            error = false;
            return "/gestionAdmin.xhtml?faces-redirect=true";
        }

        pacienteActual = pacienteRepositorioBean.buscarPorCredenciales(cedula, contrasena);

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

        // Verificar que la cédula no exista
        if (pacienteRepositorioBean.existeCedula(nuevoPaciente.getCedula())) {
            mensaje = "Ya existe un paciente registrado con esa cédula.";
            error = true;
            return null;
        }

        // Verificar que el correo no exista
        if (pacienteRepositorioBean.existeCorreo(nuevoPaciente.getCorreoElectronico())) {
            mensaje = "El correo electrónico ya está registrado.";
            error = true;
            return null;
        }

        pacienteRepositorioBean.registrar(nuevoPaciente);

        pacienteActual = nuevoPaciente;

        mensaje = "Registro exitoso.";
        error = false;

        nuevoPaciente = new Paciente();

        return "/index.xhtml?faces-redirect=true";
    }

    @Transactional
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

        Medico medico = encontrarMedicoPorNombre(medicoSeleccionado);
        HorarioMedico horario = encontrarHorarioPorTexto(horarioSeleccionado, medico);
        Paciente paciente = em.find(Paciente.class, pacienteActual.getId());

        if (medico == null || horario == null || paciente == null) {
            mensaje = "No fue posible crear la cita.";
            error = true;
            return null;
        }

        Cita cita = new Cita(Date.from(horario.getFechaDisponible().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()),
                Time.valueOf(horario.getHoraInicio()), medico, paciente);
        cita.agendar();
        paciente.agendarCita(cita);
        horario.reservarHorario();
        medico.agregarCita(cita);
        cita.setNotificacion(new Notificacion("Cita agendada correctamente", new Date(), cita));

        em.persist(cita);

        cita.getNotificacion().enviarNotificacion();

        pacienteActual = paciente;
        ultimaCitaAgendada = cita;
        mensaje = "Cita agendada correctamente.";
        error = false;
        return "confirmacion?faces-redirect=true";
    }

    public Cita getUltimaCitaAgendada() {
        return ultimaCitaAgendada;
    }

    @Transactional
    public String cancelarCita() {
        if (pacienteActual == null || citaSeleccionada == null) {
            mensaje = "Seleccione una cita para cancelar.";
            error = true;
            return null;
        }

        for (Cita cita : pacienteActual.getCitas()) {
            if (citaSeleccionada.equals(cita.getMedico().getNombreCompleto() + " | " + cita.getFecha() + " | " + cita.getHora())) {
                cancelarCita(cita);
                return null;
            }
        }

        mensaje = "No se encontró la cita seleccionada.";
        error = true;
        return null;
    }

    @Transactional
    public void cancelarCita(Cita cita) {
        if (cita == null) {
            mensaje = "Seleccione una cita para cancelar.";
            error = true;
            return;
        }
        Cita administrada = em.find(Cita.class, cita.getId());
        administrada.cancelar();
        if (administrada.getNotificacion() != null) {
            administrada.getNotificacion().enviarNotificacion();
        }
        mensaje = "Cita cancelada correctamente.";
        error = false;
    }

    public String seleccionarTab(String tab) {
        this.tabActiva = tab;
        return null;
    }

    public boolean isTabActiva(String tab) {
        return tab != null && tab.equals(tabActiva);
    }

    public List<Paciente> getPacientes() {
        return pacienteRepositorioBean.getPacientes();
    }

    public List<Especialidad> getEspecialidades() {
        return administradorBean.getEspecialidades();
    }

    public List<Medico> getMedicosDisponibles() {
        if (especialidadSeleccionada == null) {
            return new ArrayList<>();
        }
        return em.createQuery(
                        "SELECT m FROM Medico m JOIN m.especialidades e WHERE e.nombre = :nombre ORDER BY m.nombreCompleto",
                        Medico.class)
                .setParameter("nombre", especialidadSeleccionada)
                .getResultList();
    }

    public List<HorarioMedico> getHorariosDisponibles() {
        Medico medico = encontrarMedicoPorNombre(medicoSeleccionado);
        if (medico == null) {
            return new ArrayList<>();
        }
        return em.createQuery(
                        "SELECT h FROM HorarioMedico h WHERE h.medico = :medico AND h.disponible = true "
                                + "ORDER BY h.fechaDisponible, h.horaInicio",
                        HorarioMedico.class)
                .setParameter("medico", medico)
                .getResultList();
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

    private Medico encontrarMedicoPorNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        List<Medico> resultado = em.createQuery(
                        "SELECT m FROM Medico m WHERE m.nombreCompleto = :nombre", Medico.class)
                .setParameter("nombre", nombre)
                .getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
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

    public Paciente getNuevoPaciente() {
        return nuevoPaciente;
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
}
