package unl.edu.ec.M_A_S_S.view;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Especialidad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class EspecialidadBean implements Serializable {

    private Especialidad especialidadActual;
    private List<Especialidad> especialidades;

    private String nombreBusqueda;

    private String mensaje;
    private boolean error;

    @PostConstruct
    public void init() {
        especialidades = new ArrayList<>();
        especialidadActual = new Especialidad();

        // Datos de ejemplo
        especialidades.add(new Especialidad(
                "Cardiología",
                "Especialidad encargada del corazón"));

        especialidades.add(new Especialidad(
                "Pediatría",
                "Atención médica infantil"));
    }

    // Registrar especialidad
    public void registrarEspecialidad() {

        if (especialidadActual.getNombre() == null ||
                especialidadActual.getNombre().trim().isEmpty()) {

            mensaje = "Debe ingresar el nombre de la especialidad.";
            error = true;
            return;
        }

        for (Especialidad e : especialidades) {

            if (e.getNombre().equalsIgnoreCase(especialidadActual.getNombre())) {

                mensaje = "La especialidad ya existe.";
                error = true;
                return;
            }
        }

        especialidades.add(especialidadActual);

        mensaje = "Especialidad registrada correctamente.";
        error = false;

        especialidadActual = new Especialidad();
    }

    // Buscar especialidad
    public void buscarEspecialidad() {

        for (Especialidad e : especialidades) {

            if (e.getNombre().equalsIgnoreCase(nombreBusqueda)) {

                especialidadActual = e;

                mensaje = "Especialidad encontrada.";
                error = false;
                return;
            }
        }

        mensaje = "No existe la especialidad.";
        error = true;
    }
    
    public void actualizarEspecialidad() {

        mensaje = "Especialidad actualizada correctamente.";
        error = false;
    }

    public void eliminarEspecialidad(Especialidad especialidad) {

        especialidades.remove(especialidad);

        mensaje = "Especialidad eliminada.";
        error = false;
    }

    public void limpiarFormulario() {

        especialidadActual = new Especialidad();
    }

    public Especialidad getEspecialidadActual() {
        return especialidadActual;
    }

    public void setEspecialidadActual(Especialidad especialidadActual) {
        this.especialidadActual = especialidadActual;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    public String getNombreBusqueda() {
        return nombreBusqueda;
    }

    public void setNombreBusqueda(String nombreBusqueda) {
        this.nombreBusqueda = nombreBusqueda;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isError() {
        return error;
    }
}