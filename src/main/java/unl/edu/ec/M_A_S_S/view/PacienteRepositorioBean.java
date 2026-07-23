package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import unl.edu.ec.M_A_S_S.domain.Paciente;

import java.io.Serializable;
import java.util.List;

@Named
@ApplicationScoped
public class PacienteRepositorioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "massPU")
    private EntityManager em;

    public List<Paciente> getPacientes() {
        return em.createQuery("SELECT p FROM Paciente p ORDER BY p.nombreCompleto", Paciente.class).getResultList();
    }

    @Transactional
    public void registrar(Paciente paciente) {
        em.persist(paciente);
    }

    public Paciente buscarPorCredenciales(String cedula, String contrasena) {
        List<Paciente> resultado = em.createQuery(
                        "SELECT p FROM Paciente p WHERE p.cedula = :cedula AND p.contrasena = :contrasena",
                        Paciente.class)
                .setParameter("cedula", cedula)
                .setParameter("contrasena", contrasena)
                .getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public boolean existeCedula(String cedula) {
        Long total = em.createQuery("SELECT COUNT(p) FROM Paciente p WHERE p.cedula = :cedula", Long.class)
                .setParameter("cedula", cedula)
                .getSingleResult();
        return total > 0;
    }

    public boolean existeCorreo(String correo) {
        Long total = em.createQuery(
                        "SELECT COUNT(p) FROM Paciente p WHERE LOWER(p.correoElectronico) = LOWER(:correo)", Long.class)
                .setParameter("correo", correo)
                .getSingleResult();
        return total > 0;
    }
}
