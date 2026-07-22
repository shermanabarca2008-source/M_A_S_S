package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Paciente;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class PacienteRepositorioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Paciente> pacientes = new ArrayList<>();

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void registrar(Paciente paciente) {
        pacientes.add(paciente);
    }

    public Paciente buscarPorCredenciales(String cedula, String contrasena) {
        for (Paciente paciente : pacientes) {
            if (cedula != null && cedula.equals(paciente.getCedula())
                    && contrasena != null && contrasena.equals(paciente.getContrasena())) {
                return paciente;
            }
        }
        return null;
    }

    public boolean existeCedula(String cedula) {
        for (Paciente paciente : pacientes) {
            if (cedula.equals(paciente.getCedula())) {
                return true;
            }
        }
        return false;
    }

    public boolean existeCorreo(String correo) {
        for (Paciente paciente : pacientes) {
            if (correo.equalsIgnoreCase(paciente.getCorreo())) {
                return true;
            }
        }
        return false;
    }
}
