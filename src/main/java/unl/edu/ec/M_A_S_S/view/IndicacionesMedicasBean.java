package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.IndicacionesMedicas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class IndicacionesMedicasBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private IndicacionesMedicas indicacionActual;
    private List<IndicacionesMedicas> indicaciones;
    private Cita citaSeleccionada;
    private boolean modoEdicion;
    private String mensaje;
    private boolean error;

    @PostConstruct
    public void init() {
        indicaciones = new ArrayList<>();
        indicacionActual = new IndicacionesMedicas();
    }

    public void registrarIndicacion() {
        if (!datosCompletos()) {
            mostrarError("Debe seleccionar una cita e ingresar el diagnóstico y tratamiento.");
            return;
        }

        indicacionActual.setCita(citaSeleccionada);
        indicacionActual.registrarIndicacion();
        indicaciones.add(indicacionActual);
        citaSeleccionada.agregarIndicacion(indicacionActual);

        if (citaSeleccionada.getMedico() != null
                && !citaSeleccionada.getMedico().getIndicaciones().contains(indicacionActual)) {
            citaSeleccionada.getMedico().registrarIndicacion(indicacionActual);
        }

        mostrarExito("Indicación médica registrada correctamente.");
        indicacionActual = new IndicacionesMedicas();
        modoEdicion = false;
    }

    public void seleccionarIndicacion(IndicacionesMedicas indicacion) {
        indicacionActual = indicacion;
        citaSeleccionada = indicacion != null ? indicacion.getCita() : null;
        modoEdicion = indicacion != null;
    }

    public void actualizarIndicacion() {
        if (!modoEdicion || indicacionActual == null || !datosCompletos()) {
            mostrarError("Seleccione una indicación y complete sus datos.");
            return;
        }
        indicacionActual.setCita(citaSeleccionada);
        mostrarExito("Indicación médica actualizada correctamente.");
        modoEdicion = false;
    }

    public void eliminarIndicacion(IndicacionesMedicas indicacion) {
        if (indicacion == null || !indicaciones.remove(indicacion)) {
            mostrarError("No se pudo eliminar la indicación médica.");
            return;
        }
        Cita cita = indicacion.getCita();
        if (cita != null) {
            cita.getIndicaciones().remove(indicacion);
            if (cita.getMedico() != null) {
                cita.getMedico().getIndicaciones().remove(indicacion);
            }
        }
        if (indicacion == indicacionActual) {
            indicacionActual = new IndicacionesMedicas();
            modoEdicion = false;
        }
        mostrarExito("Indicación médica eliminada correctamente.");
    }

    public void limpiarFormulario() {
        indicacionActual = new IndicacionesMedicas();
        citaSeleccionada = null;
        modoEdicion = false;
        mensaje = null;
        error = false;
    }

    private boolean datosCompletos() {
        return citaSeleccionada != null
                && indicacionActual != null
                && indicacionActual.getDiagnostico() != null
                && !indicacionActual.getDiagnostico().isBlank()
                && indicacionActual.getTratamiento() != null
                && !indicacionActual.getTratamiento().isBlank();
    }

    private void mostrarExito(String texto) {
        mensaje = texto;
        error = false;
    }

    private void mostrarError(String texto) {
        mensaje = texto;
        error = true;
    }

    public IndicacionesMedicas getIndicacionActual() {
        return indicacionActual;
    }

    public void setIndicacionActual(IndicacionesMedicas indicacionActual) {
        this.indicacionActual = indicacionActual;
    }

    public List<IndicacionesMedicas> getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(List<IndicacionesMedicas> indicaciones) {
        this.indicaciones = indicaciones;
    }

    public Cita getCitaSeleccionada() {
        return citaSeleccionada;
    }

    public void setCitaSeleccionada(Cita citaSeleccionada) {
        this.citaSeleccionada = citaSeleccionada;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }
}
