package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import unl.edu.ec.M_A_S_S.domain.Administrador;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.ConfiguracionSistema;
import unl.edu.ec.M_A_S_S.domain.Especialidad;
import unl.edu.ec.M_A_S_S.domain.Medico;
import unl.edu.ec.M_A_S_S.domain.Paciente;
import unl.edu.ec.M_A_S_S.service.AgendaService;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class AdministradorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "massPU")
    private EntityManager em;

    private String nombreEspecialidad;
    private String descripcionEspecialidad;
    private String nombreMedico;
    private String usuarioNuevoMedico;
    private String contrasenaNuevoMedico;
    private List<String> especialidadesNuevoMedico = new ArrayList<>();
    private String usuarioAdmin;
    private String passwordAdmin;
    private String mensajeAdmin;
    private boolean errorAdmin;
    private Medico medicoSeleccionado;
    private String nombreMedicoEdicion;
    private List<String> especialidadesSeleccionadasEdicion = new ArrayList<>();
    private boolean modoEdicion;

    private String nombreInstitucion;
    private String registroSanitario;
    private String direccionInstitucion;
    private String correoSoporte;
    private String telefonoEmergencia;

    private boolean formularioVisible = false;

    private Long medicoSeleccionadoHorariosId;
    private LocalDate fechaSeleccionadaHorarios;

    private String terminoBusqueda;
    private boolean busquedaRealizada;

    @PostConstruct
    public void inicializar() {
        ConfiguracionSistema configuracion = em.find(ConfiguracionSistema.class, 1L);
        if (configuracion == null) {
            cargarConfiguracionPredeterminada();
        } else {
            cargarConfiguracion(configuracion);
        }
    }

    public Date getFechaActual() {
        return new Date();
    }

    public void buscar(AjaxBehaviorEvent event) {
        busquedaRealizada = terminoBusqueda != null && !terminoBusqueda.trim().isEmpty();
    }

    public List<Medico> getResultadosMedicosBusqueda() {
        if (!busquedaRealizada) {
            return List.of();
        }
        String termino = "%" + terminoBusqueda.trim().toLowerCase() + "%";
        return em.createQuery(
                        "SELECT DISTINCT m FROM Medico m LEFT JOIN m.especialidades e "
                                + "WHERE LOWER(m.nombreCompleto) LIKE :termino OR LOWER(e.nombre) LIKE :termino "
                                + "ORDER BY m.nombreCompleto",
                        Medico.class)
                .setParameter("termino", termino)
                .getResultList();
    }

    public List<Paciente> getResultadosPacientesBusqueda() {
        if (!busquedaRealizada) {
            return List.of();
        }
        String termino = "%" + terminoBusqueda.trim().toLowerCase() + "%";
        return em.createQuery(
                        "SELECT p FROM Paciente p WHERE LOWER(p.nombreCompleto) LIKE :termino "
                                + "OR LOWER(p.cedula) LIKE :termino ORDER BY p.nombreCompleto",
                        Paciente.class)
                .setParameter("termino", termino)
                .getResultList();
    }

    public List<Especialidad> getResultadosEspecialidadesBusqueda() {
        if (!busquedaRealizada) {
            return List.of();
        }
        String termino = "%" + terminoBusqueda.trim().toLowerCase() + "%";
        return em.createQuery(
                        "SELECT e FROM Especialidad e WHERE LOWER(e.nombre) LIKE :termino ORDER BY e.nombre",
                        Especialidad.class)
                .setParameter("termino", termino)
                .getResultList();
    }

    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public boolean isBusquedaRealizada() {
        return busquedaRealizada;
    }

    public void mostrarFormulario() {
        formularioVisible = true;
    }

    public boolean isFormularioVisible() {
        return formularioVisible;
    }

    public void setFormularioVisible(boolean formularioVisible) {
        this.formularioVisible = formularioVisible;
    }

    @Transactional
    public String agregarEspecialidad() {
        if (nombreEspecialidad != null && !nombreEspecialidad.trim().isEmpty()) {
            Especialidad especialidad = new Especialidad(
                    nombreEspecialidad.trim(),
                    descripcionEspecialidad != null ? descripcionEspecialidad.trim() : ""
            );
            em.persist(especialidad);
            nombreEspecialidad = "";
            descripcionEspecialidad = "";
            formularioVisible = false;
            mensajeAdmin = "Especialidad agregada correctamente.";
            errorAdmin = false;
            return "gestionEspecialidades?faces-redirect=true";
        } else {
            mensajeAdmin = "Ingrese el nombre de la especialidad.";
            errorAdmin = true;
            return null;
        }
    }

    public void ocultarFormulario() {
        formularioVisible = false;
    }

    public String cancelarNuevaEspecialidad() {
        nombreEspecialidad = "";
        descripcionEspecialidad = "";
        mensajeAdmin = null;
        errorAdmin = false;
        return "gestionEspecialidades?faces-redirect=true";
    }

    @Transactional
    public void eliminarEspecialidad(Especialidad especialidad) {
        Especialidad administrada = especialidad != null ? em.find(Especialidad.class, especialidad.getNombre()) : null;
        if (administrada != null) {
            em.remove(administrada);
            mensajeAdmin = "Especialidad eliminada correctamente.";
            errorAdmin = false;
        } else {
            mensajeAdmin = "No se pudo eliminar la especialidad.";
            errorAdmin = true;
        }
    }

    @Transactional
    public String agregarMedico() {
        if (nombreMedico == null || nombreMedico.trim().isEmpty()) {
            mensajeAdmin = "Ingrese un nombre para el médico.";
            errorAdmin = true;
            return null;
        }

        if (usuarioNuevoMedico == null || usuarioNuevoMedico.trim().isEmpty()
                || contrasenaNuevoMedico == null || contrasenaNuevoMedico.trim().isEmpty()) {
            mensajeAdmin = "Ingrese el usuario y la contraseña de acceso del médico.";
            errorAdmin = true;
            return null;
        }

        Long usuariosExistentes = em.createQuery(
                        "SELECT COUNT(m) FROM Medico m WHERE m.usuario = :usuario", Long.class)
                .setParameter("usuario", usuarioNuevoMedico.trim())
                .getSingleResult();
        if (usuariosExistentes > 0) {
            mensajeAdmin = "Ya existe un médico registrado con ese usuario.";
            errorAdmin = true;
            return null;
        }

        List<Especialidad> especialidades = resolverEspecialidades(especialidadesNuevoMedico);
        if (especialidades.isEmpty()) {
            mensajeAdmin = "Seleccione al menos una especialidad.";
            errorAdmin = true;
            return null;
        }

        Medico medico = new Medico(nombreMedico.trim(), especialidades);
        medico.setUsuario(usuarioNuevoMedico.trim());
        medico.setContrasena(contrasenaNuevoMedico.trim());
        em.persist(medico);
        nombreMedico = "";
        usuarioNuevoMedico = "";
        contrasenaNuevoMedico = "";
        especialidadesNuevoMedico = new ArrayList<>();
        mensajeAdmin = "Médico agregado correctamente.";
        errorAdmin = false;
        return "gestionMedicos?faces-redirect=true";
    }

    public void seleccionarMedicoParaEditar(Medico medico) {
        medicoSeleccionado = medico;
        nombreMedicoEdicion = medico.getNombreCompleto();
        especialidadesSeleccionadasEdicion = new ArrayList<>();
        for (Especialidad especialidad : medico.getEspecialidades()) {
            especialidadesSeleccionadasEdicion.add(especialidad.getNombre());
        }
        modoEdicion = true;
    }

    @Transactional
    public void guardarEdicionMedico() {
        if (medicoSeleccionado == null || nombreMedicoEdicion == null || nombreMedicoEdicion.trim().isEmpty()) {
            mensajeAdmin = "Ingrese un nombre válido para el médico.";
            errorAdmin = true;
            return;
        }

        List<Especialidad> nuevasEspecialidades = resolverEspecialidades(especialidadesSeleccionadasEdicion);
        if (nuevasEspecialidades.isEmpty()) {
            mensajeAdmin = "Seleccione al menos una especialidad.";
            errorAdmin = true;
            return;
        }

        Medico administrado = em.find(Medico.class, medicoSeleccionado.getId());
        administrado.setNombreCompleto(nombreMedicoEdicion.trim());
        administrado.setEspecialidades(nuevasEspecialidades);

        limpiarEdicion();
        mensajeAdmin = "Médico actualizado correctamente.";
        errorAdmin = false;
    }

    public void cancelarEdicion() {
        limpiarEdicion();
        mensajeAdmin = "Edición cancelada.";
        errorAdmin = false;
    }

    private void limpiarEdicion() {
        modoEdicion = false;
        medicoSeleccionado = null;
        nombreMedicoEdicion = "";
        especialidadesSeleccionadasEdicion = new ArrayList<>();
    }

    @Transactional
    public void eliminarMedico(Medico medico) {
        Medico administrado = medico != null ? em.find(Medico.class, medico.getId()) : null;
        if (administrado == null) {
            mensajeAdmin = "No se pudo eliminar el médico.";
            errorAdmin = true;
            return;
        }

        Long citasRegistradas = em.createQuery(
                        "SELECT COUNT(c) FROM Cita c WHERE c.medico = :medico", Long.class)
                .setParameter("medico", administrado)
                .getSingleResult();
        Long indicacionesRegistradas = em.createQuery(
                        "SELECT COUNT(i) FROM IndicacionesMedicas i WHERE i.medico = :medico", Long.class)
                .setParameter("medico", administrado)
                .getSingleResult();

        if (citasRegistradas > 0 || indicacionesRegistradas > 0) {
            mensajeAdmin = "No se puede eliminar al médico porque tiene citas o indicaciones registradas.";
            errorAdmin = true;
            return;
        }

        if (medicoSeleccionado != null && medicoSeleccionado.getId().equals(administrado.getId())) {
            limpiarEdicion();
        }
        em.remove(administrado);
        mensajeAdmin = "Médico eliminado correctamente.";
        errorAdmin = false;
    }

    @Transactional
    public void generarHorarioAutomatico(Medico medico) {
        Medico administrado = medico != null ? em.find(Medico.class, medico.getId()) : null;
        if (administrado != null) {
            AgendaService agendaService = new AgendaService();
            agendaService.registrarAgenda(administrado);
            em.merge(administrado);
            this.medicoSeleccionadoHorariosId = administrado.getId();
            this.fechaSeleccionadaHorarios = LocalDate.now();
            mensajeAdmin = "Horarios generados automáticamente para " + administrado.getNombreCompleto();
            errorAdmin = false;
        } else {
            mensajeAdmin = "No se pudo generar los horarios.";
            errorAdmin = true;
        }
    }

    @Transactional
    public void generarHorarioAutomaticoPorId(Long medicoId) {
        if (medicoId != null) {
            Medico administrado = em.find(Medico.class, medicoId);
            if (administrado != null) {
                AgendaService agendaService = new AgendaService();
                agendaService.registrarAgenda(administrado);
                em.merge(administrado);
                this.medicoSeleccionadoHorariosId = administrado.getId();
                this.fechaSeleccionadaHorarios = LocalDate.now();
                mensajeAdmin = "Horarios generados automáticamente para " + administrado.getNombreCompleto();
                errorAdmin = false;
            } else {
                mensajeAdmin = "Médico no encontrado.";
                errorAdmin = true;
            }
        } else {
            mensajeAdmin = "Seleccione un médico para generar horarios.";
            errorAdmin = true;
        }
    }

    @Transactional
    public void generarHorariosTodosMedicos() {
        List<Medico> medicos = getMedicos();
        AgendaService agendaService = new AgendaService();
        int count = 0;
        for (Medico medico : medicos) {
            if (medico.getAgendaMedico() == null) {
                agendaService.registrarAgenda(medico);
                em.merge(medico);
                count++;
            }
        }
        mensajeAdmin = "Horarios generados para " + count + " médicos.";
        errorAdmin = false;
    }

    private List<Especialidad> resolverEspecialidades(List<String> nombres) {
        List<Especialidad> resultado = new ArrayList<>();
        if (nombres == null) {
            return resultado;
        }
        for (String nombre : nombres) {
            Especialidad especialidad = em.find(Especialidad.class, nombre);
            if (especialidad != null) {
                resultado.add(especialidad);
            }
        }
        return resultado;
    }

    @Transactional
    public void guardarConfiguracion() {
        if (nombreInstitucion == null || nombreInstitucion.trim().isEmpty()
                || direccionInstitucion == null || direccionInstitucion.trim().isEmpty()) {
            mensajeAdmin = "El nombre y la dirección de la institución son obligatorios.";
            errorAdmin = true;
            return;
        }

        ConfiguracionSistema configuracion = em.find(ConfiguracionSistema.class, 1L);
        boolean esNueva = configuracion == null;
        if (esNueva) {
            configuracion = new ConfiguracionSistema(1L);
        }

        configuracion.setNombreInstitucion(nombreInstitucion.trim());
        configuracion.setRegistroSanitario(limpiarTexto(registroSanitario));
        configuracion.setDireccionInstitucion(direccionInstitucion.trim());
        configuracion.setCorreoSoporte(limpiarTexto(correoSoporte));
        configuracion.setTelefonoEmergencia(limpiarTexto(telefonoEmergencia));

        if (esNueva) {
            em.persist(configuracion);
        }

        cargarConfiguracion(configuracion);
        mensajeAdmin = "Configuración guardada correctamente.";
        errorAdmin = false;
    }

    private void cargarConfiguracion(ConfiguracionSistema configuracion) {
        nombreInstitucion = configuracion.getNombreInstitucion();
        registroSanitario = configuracion.getRegistroSanitario();
        direccionInstitucion = configuracion.getDireccionInstitucion();
        correoSoporte = configuracion.getCorreoSoporte();
        telefonoEmergencia = configuracion.getTelefonoEmergencia();
    }

    private void cargarConfiguracionPredeterminada() {
        nombreInstitucion = "Centro Médico de Alta Especialidad M.A.S.S.";
        registroSanitario = "HE-2023-MEX-0941";
        direccionInstitucion = "Av. Insurgentes Sur 1450, Col. Actipan, CDMX";
        correoSoporte = "contacto@mass-salud.mx";
        telefonoEmergencia = "+52 (55) 5555-0199";
    }

    private String limpiarTexto(String texto) {
        return texto == null ? "" : texto.trim();
    }

    public String validarAccesoAdministrador() {
        List<Administrador> resultado = em.createQuery(
                        "SELECT a FROM Administrador a WHERE a.usuario = :usuario AND a.contrasena = :contrasena",
                        Administrador.class)
                .setParameter("usuario", usuarioAdmin)
                .setParameter("contrasena", passwordAdmin)
                .getResultList();

        if (!resultado.isEmpty()) {
            mensajeAdmin = "Acceso correcto. Bienvenido administrador.";
            errorAdmin = false;
            return "admin";
        }
        mensajeAdmin = "Credenciales incorrectas. Intente nuevamente.";
        errorAdmin = true;
        return null;
    }

    public List<Especialidad> getEspecialidades() {
        return em.createQuery("SELECT e FROM Especialidad e ORDER BY e.nombre", Especialidad.class).getResultList();
    }

    public List<Medico> getMedicos() {
        return em.createQuery("SELECT m FROM Medico m ORDER BY m.nombreCompleto", Medico.class).getResultList();
    }

    public int getTotalPacientesRegistrados() {
        return em.createQuery("SELECT COUNT(p) FROM Paciente p", Long.class).getSingleResult().intValue();
    }

    public int getCitasHoy() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.fecha = CURRENT_DATE", Long.class)
                .getSingleResult().intValue();
    }

    public int getCitasCanceladasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.CANCELADA)
                .getSingleResult().intValue();
    }

    public int getCitasAgendadasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.AGENDADA)
                .getSingleResult().intValue();
    }

    public int getCitasFinalizadasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.FINALIZADA)
                .getSingleResult().intValue();
    }

    public int getCitasReagendadasTotal() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class)
                .setParameter("estado", Cita.EstadoCita.REAGENDADA)
                .getSingleResult().intValue();
    }

    public int getTotalCitasRegistradas() {
        return em.createQuery("SELECT COUNT(c) FROM Cita c", Long.class).getSingleResult().intValue();
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public String getDescripcionEspecialidad() {
        return descripcionEspecialidad;
    }

    public void setDescripcionEspecialidad(String descripcionEspecialidad) {
        this.descripcionEspecialidad = descripcionEspecialidad;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getUsuarioNuevoMedico() {
        return usuarioNuevoMedico;
    }

    public void setUsuarioNuevoMedico(String usuarioNuevoMedico) {
        this.usuarioNuevoMedico = usuarioNuevoMedico;
    }

    public String getContrasenaNuevoMedico() {
        return contrasenaNuevoMedico;
    }

    public void setContrasenaNuevoMedico(String contrasenaNuevoMedico) {
        this.contrasenaNuevoMedico = contrasenaNuevoMedico;
    }

    public List<String> getEspecialidadesNuevoMedico() {
        return especialidadesNuevoMedico;
    }

    public void setEspecialidadesNuevoMedico(List<String> especialidadesNuevoMedico) {
        this.especialidadesNuevoMedico = especialidadesNuevoMedico;
    }

    public String getUsuarioAdmin() {
        return usuarioAdmin;
    }

    public void setUsuarioAdmin(String usuarioAdmin) {
        this.usuarioAdmin = usuarioAdmin;
    }

    public String getPasswordAdmin() {
        return passwordAdmin;
    }

    public void setPasswordAdmin(String passwordAdmin) {
        this.passwordAdmin = passwordAdmin;
    }

    public String getMensajeAdmin() {
        return mensajeAdmin;
    }

    public boolean isErrorAdmin() {
        return errorAdmin;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getRegistroSanitario() {
        return registroSanitario;
    }

    public void setRegistroSanitario(String registroSanitario) {
        this.registroSanitario = registroSanitario;
    }

    public String getDireccionInstitucion() {
        return direccionInstitucion;
    }

    public void setDireccionInstitucion(String direccionInstitucion) {
        this.direccionInstitucion = direccionInstitucion;
    }

    public String getCorreoSoporte() {
        return correoSoporte;
    }

    public void setCorreoSoporte(String correoSoporte) {
        this.correoSoporte = correoSoporte;
    }

    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public void setTelefonoEmergencia(String telefonoEmergencia) {
        this.telefonoEmergencia = telefonoEmergencia;
    }

    public Medico getMedicoSeleccionado() {
        return medicoSeleccionado;
    }

    public void setMedicoSeleccionado(Medico medicoSeleccionado) {
        this.medicoSeleccionado = medicoSeleccionado;
    }

    public String getNombreMedicoEdicion() {
        return nombreMedicoEdicion;
    }

    public void setNombreMedicoEdicion(String nombreMedicoEdicion) {
        this.nombreMedicoEdicion = nombreMedicoEdicion;
    }

    public List<String> getEspecialidadesSeleccionadasEdicion() {
        return especialidadesSeleccionadasEdicion;
    }

    public void setEspecialidadesSeleccionadasEdicion(List<String> especialidadesSeleccionadasEdicion) {
        this.especialidadesSeleccionadasEdicion = especialidadesSeleccionadasEdicion;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public String getFechaActualFormateada() {
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy", new java.util.Locale("es"));
        return hoy.format(formatter);
    }

    public List<unl.edu.ec.M_A_S_S.domain.HorarioMedico> getHorariosDelDia() {
        if (medicoSeleccionadoHorariosId == null || fechaSeleccionadaHorarios == null) {
            return new ArrayList<>();
        }
        
        Medico medico = em.find(Medico.class, medicoSeleccionadoHorariosId);
        if (medico == null) {
            return new ArrayList<>();
        }
        
        List<unl.edu.ec.M_A_S_S.domain.HorarioMedico> horarios = new ArrayList<>();
        
        if (medico.getAgendaMedico() != null) {
            unl.edu.ec.M_A_S_S.domain.DiaAgenda dia = medico.getAgendaMedico().getDiaPorFecha(fechaSeleccionadaHorarios);
            if (dia != null) {
                horarios.addAll(dia.getHorarios());
            }
        }
        
        return horarios;
    }

    public void seleccionarMedicoHorarios(Medico medico) {
        if (medico != null) {
            this.medicoSeleccionadoHorariosId = medico.getId();
        }
        if (this.fechaSeleccionadaHorarios == null) {
            this.fechaSeleccionadaHorarios = LocalDate.now();
        }
    }

    public void navegarDia(int dias) {
        if (fechaSeleccionadaHorarios != null) {
            fechaSeleccionadaHorarios = fechaSeleccionadaHorarios.plusDays(dias);
        }
    }

    @Transactional
    public void guardarHorarios() {
        if (medicoSeleccionadoHorariosId != null) {
            Medico medico = em.find(Medico.class, medicoSeleccionadoHorariosId);
            if (medico != null) {
                em.merge(medico);
                mensajeAdmin = "Horarios guardados correctamente para " + medico.getNombreCompleto();
                errorAdmin = false;
            } else {
                mensajeAdmin = "Médico no encontrado.";
                errorAdmin = true;
            }
        } else {
            mensajeAdmin = "Seleccione un médico para guardar los horarios.";
            errorAdmin = true;
        }
    }

    @Transactional
    public void limpiarAgenda() {
        if (medicoSeleccionadoHorariosId != null) {
            Medico medico = em.find(Medico.class, medicoSeleccionadoHorariosId);
            if (medico != null && medico.getAgendaMedico() != null) {
                medico.getAgendaMedico().getAgenda().clear();
                em.merge(medico);
                mensajeAdmin = "Agenda limpiada correctamente para " + medico.getNombreCompleto();
                errorAdmin = false;
            } else {
                mensajeAdmin = "Seleccione un médico con agenda para limpiar.";
                errorAdmin = true;
            }
        } else {
            mensajeAdmin = "Seleccione un médico para limpiar la agenda.";
            errorAdmin = true;
        }
    }

    public String getFechaSeleccionadaFormateada() {
        if (fechaSeleccionadaHorarios == null) {
            return getFechaActualFormateada();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy", new java.util.Locale("es"));
        return fechaSeleccionadaHorarios.format(formatter);
    }

    public Long getMedicoSeleccionadoHorariosId() {
        return medicoSeleccionadoHorariosId;
    }

    public void setMedicoSeleccionadoHorariosId(Long medicoSeleccionadoHorariosId) {
        this.medicoSeleccionadoHorariosId = medicoSeleccionadoHorariosId;
    }

    public Medico getMedicoSeleccionadoHorarios() {
        if (medicoSeleccionadoHorariosId == null) {
            return null;
        }
        return em.find(Medico.class, medicoSeleccionadoHorariosId);
    }

    public LocalDate getFechaSeleccionadaHorarios() {
        return fechaSeleccionadaHorarios;
    }

    public void setFechaSeleccionadaHorarios(LocalDate fechaSeleccionadaHorarios) {
        this.fechaSeleccionadaHorarios = fechaSeleccionadaHorarios;
    }

}
