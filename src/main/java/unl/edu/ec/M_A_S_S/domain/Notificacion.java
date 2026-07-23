package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "notificacion")
public class Notificacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensaje;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEnvio;

    // Relación UML
    @OneToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;

    // Constructor vacío
    public Notificacion() {
        this.fechaEnvio = new Date();
    }

    // Constructor con parámetros
    public Notificacion(String mensaje,
                        Date fechaEnvio,
                        Cita cita) {

        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.cita = cita;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    // Método para enviar notificación
    public void enviarNotificacion() {
        System.out.println("Notificación enviada correctamente.");
        System.out.println("Mensaje: " + mensaje);
        System.out.println("Fecha de envío: " + fechaEnvio);
        if (cita != null && cita.getPaciente() != null) {
            System.out.println("Correo destino: " + cita.getPaciente().getCorreoElectronico());
        }
    }

    // Mostrar información
    @Override
    public String toString() {
        return "Notificacion{" +
                "mensaje='" + mensaje + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                ", cita=" +
                (cita != null
                        ? cita.getEstado()
                        : "Sin cita asociada") +
                '}';
    }
}
