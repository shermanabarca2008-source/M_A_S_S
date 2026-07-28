package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "configuracion_sistema")
public class ConfiguracionSistema implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String nombreInstitucion;
    private String registroSanitario;
    private String direccionInstitucion;
    private String correoSoporte;
    private String telefonoEmergencia;

    public ConfiguracionSistema() {
    }

    public ConfiguracionSistema(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
