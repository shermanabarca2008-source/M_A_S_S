package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.IndicacionesMedicas;
import unl.edu.ec.M_A_S_S.domain.Medico;
import unl.edu.ec.M_A_S_S.domain.Notificacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class IndicacionesMedicasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "massPU")
    private EntityManager em;

    @Inject
    private MedicoSesionBean medicoSesionBean;

    private Long citaSeleccionadaId;
    private String terminoBusqueda = "";
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private String mensaje;
    private boolean error;

    public List<Cita> getCitasDisponibles() {
        Medico medico = medicoSesionBean.getMedicoActual();
        if (medico == null || medico.getId() == null) {
            return new ArrayList<>();
        }

        String busqueda = terminoBusqueda == null ? "" : terminoBusqueda.trim().toLowerCase();
        if (busqueda.isEmpty()) {
            return em.createQuery(
                            "SELECT c FROM Cita c WHERE c.medico.id = :medicoId "
                                    + "AND c.estado <> :cancelada ORDER BY c.fecha DESC, c.hora DESC",
                            Cita.class)
                    .setParameter("medicoId", medico.getId())
                    .setParameter("cancelada", Cita.EstadoCita.CANCELADA)
                    .getResultList();
        }

        return em.createQuery(
                        "SELECT c FROM Cita c WHERE c.medico.id = :medicoId "
                                + "AND c.estado <> :cancelada "
                                + "AND (LOWER(c.paciente.nombreCompleto) LIKE :busqueda "
                                + "OR LOWER(c.paciente.cedula) LIKE :busqueda) "
                                + "ORDER BY c.fecha DESC, c.hora DESC",
                        Cita.class)
                .setParameter("medicoId", medico.getId())
                .setParameter("cancelada", Cita.EstadoCita.CANCELADA)
                .setParameter("busqueda", "%" + busqueda + "%")
                .getResultList();
    }

    public Cita getCitaSeleccionada() {
        if (citaSeleccionadaId == null) {
            return null;
        }
        return em.find(Cita.class, citaSeleccionadaId);
    }

    public void buscar(AjaxBehaviorEvent event) {
        // JSF actualiza el término y vuelve a renderizar la lista de citas.
    }

    public String prepararIndicacion(Cita cita) {
        if (cita == null) {
            return null;
        }
        citaSeleccionadaId = cita.getId();
        mensaje = null;
        error = false;
        return "indicacionesMedicas?faces-redirect=true";
    }

    public String guardarBorrador() {
        if (citaSeleccionadaId == null) {
            mensaje = "Seleccione primero la cita del paciente.";
            error = true;
            return null;
        }
        mensaje = "Borrador guardado mientras permanezca en su sesión.";
        error = false;
        return null;
    }

    @Transactional
    public String enviarAlPaciente() {
        if (!validarFormulario()) {
            return null;
        }

        Cita cita = em.find(Cita.class, citaSeleccionadaId);
        Medico medicoSesion = medicoSesionBean.getMedicoActual();
        Medico medico = medicoSesion == null ? null : em.find(Medico.class, medicoSesion.getId());

        if (cita == null || medico == null || cita.getMedico() == null
                || !medico.getId().equals(cita.getMedico().getId())) {
            mensaje = "No fue posible encontrar la cita seleccionada para este médico.";
            error = true;
            return null;
        }

        IndicacionesMedicas indicacion = new IndicacionesMedicas(
                diagnostico.trim(),
                tratamiento.trim(),
                observaciones == null ? "" : observaciones.trim(),
                medico,
                cita);

        cita.agregarIndicacion(indicacion);
        medico.registrarIndicacion(indicacion);
        cita.setEstado(Cita.EstadoCita.FINALIZADA);
        em.persist(indicacion);

        Notificacion notificacion = cita.getNotificacion();
        String textoNotificacion = "El médico " + medico.getNombreCompleto()
                + " registró nuevas indicaciones médicas para su cita.";
        if (notificacion == null) {
            notificacion = new Notificacion(textoNotificacion, new Date(), cita);
            cita.setNotificacion(notificacion);
            em.persist(notificacion);
        } else {
            notificacion.setMensaje(textoNotificacion);
            notificacion.setFechaEnvio(new Date());
        }
        notificacion.enviarNotificacion();

        em.flush();
        medicoSesionBean.setMedicoActual(medico);

        mensaje = "Indicaciones registradas y enviadas al paciente correctamente.";
        error = false;
        diagnostico = "";
        tratamiento = "";
        observaciones = "";
        return null;
    }

    private boolean validarFormulario() {
        if (citaSeleccionadaId == null) {
            mensaje = "Seleccione la cita del paciente.";
            error = true;
            return false;
        }
        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            mensaje = "Ingrese el diagnóstico o código CIE-10.";
            error = true;
            return false;
        }
        if (tratamiento == null || tratamiento.trim().isEmpty()) {
            mensaje = "Ingrese el tratamiento o las indicaciones médicas.";
            error = true;
            return false;
        }
        return true;
    }

    public Long getCitaSeleccionadaId() {
        return citaSeleccionadaId;
    }

    public void setCitaSeleccionadaId(Long citaSeleccionadaId) {
        this.citaSeleccionadaId = citaSeleccionadaId;
    }

    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
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

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }
}
