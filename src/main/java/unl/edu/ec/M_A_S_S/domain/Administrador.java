package unl.edu.ec.M_A_S_S.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Administrador implements Serializable {

    @Id
    private Long id;
    private static final long serialVersionUID = 1L;
    // Atributos
    private int idAdmin;
    private String contrasena;

    // Relaciones UML
    private List<Especialidad> especialidades;
    private List<Medico> medicos;

    // Archivo de persistencia
    private static final String DEFAULT_FILE = "admin_store.dat";

    // Constructor vacío
    public Administrador() {
        this.especialidades = new ArrayList<>();
        this.medicos = new ArrayList<>();
    }

    // Constructor con parámetros
    public Administrador(int idAdmin, String contrasena) {
        this.idAdmin = idAdmin;
        this.contrasena = contrasena;
        this.especialidades = new ArrayList<>();
        this.medicos = new ArrayList<>();
    }

    // Getters y Setters
    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    // Agregar horario a un médico (gestión centralizada por el administrador)
    public void agregarHorarioAMedico(Medico medico, HorarioMedico horario) {
        if (medico != null && horario != null) {
            medico.getHorarios().add(horario);
            System.out.println("Horario agregado al médico correctamente.");
        } else {
            System.out.println("No se pudo agregar el horario al médico.");
        }
    }

    // Eliminar médico
    public boolean eliminarMedico(Medico medico) {
        if (medico != null && medicos.remove(medico)) {
            // eliminar de especialidades si estaba ligado
            for (Especialidad esp : especialidades) {
                esp.getMedicos().remove(medico);
            }
            System.out.println("Médico eliminado correctamente.");
            return true;
        }
        System.out.println("No se pudo eliminar el médico.");
        return false;
    }

    // Eliminar especialidad
    public boolean eliminarEspecialidad(Especialidad especialidad) {
        if (especialidad != null && especialidades.remove(especialidad)) {
            System.out.println("Especialidad eliminada correctamente.");
            return true;
        }
        System.out.println("No se pudo eliminar la especialidad.");
        return false;
    }

    // Eliminar horario de un médico
    public boolean eliminarHorarioDeMedico(Medico medico, HorarioMedico horario) {
        if (medico != null && horario != null && medico.getHorarios().remove(horario)) {
            System.out.println("Horario eliminado del médico correctamente.");
            return true;
        }
        System.out.println("No se pudo eliminar el horario.");
        return false;
    }

    // Método para gestionar especialidades
    public void gestionarEspecialidad(Especialidad especialidad) {

        if (especialidad != null) {
            especialidades.add(especialidad);
            System.out.println("Especialidad agregada correctamente.");
        } else {
            System.out.println("No se pudo agregar la especialidad.");
        }
    }

    // Método para gestionar médicos
    public void gestionarMedico(Medico medico) {

        if (medico != null) {
            medicos.add(medico);
            System.out.println("Médico agregado correctamente.");
        } else {
            System.out.println("No se pudo agregar el médico.");
        }
    }

    // Mostrar información
    @Override
    public String toString() {
        return "Administrador: " +
                "\nID=" + idAdmin;
    }

    // ===== MÉTODOS DE PERSISTENCIA (INTEGRADOS) =====

    public void guardarEnArchivo() {
        guardarEnArchivo(DEFAULT_FILE);
    }

    public void guardarEnArchivo(String ruta) {
        try (FileOutputStream fos = new FileOutputStream(ruta, true);
             ObjectOutputStream oos = new File(ruta).length() == 0
                     ? new ObjectOutputStream(fos)
                     : new ObjectOutputStream(fos) {
                 @Override
                 protected void writeStreamHeader() throws IOException {
                     reset();
                 }
             }) {
            oos.writeObject(this);
            oos.flush();
            System.out.println("Datos guardados en " + ruta);
        } catch (IOException e) {
            System.out.println("Error de escritura: " + e.getMessage());
        }
    }

    public static List<Administrador> cargarDesdeArchivo() {
        return cargarDesdeArchivo(DEFAULT_FILE);
    }

    public static List<Administrador> cargarDesdeArchivo(String ruta) {
        List<Administrador> lista = new ArrayList<>();
        File file = new File(ruta);
        if (!file.exists() || file.length() == 0) {
            return lista;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                Object obj = ois.readObject();
                if (obj instanceof Administrador) {
                    lista.add((Administrador) obj);
                }
            }
        } catch (EOFException ignored) {
            // fin de archivo
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error de lectura: " + e.getMessage());
        }
        return lista;
    }

    public static Administrador cargarUltimoAdministrador() {
        return cargarUltimoAdministrador(DEFAULT_FILE);
    }

    public static Administrador cargarUltimoAdministrador(String ruta) {
        List<Administrador> lista = cargarDesdeArchivo(ruta);
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(lista.size() - 1);
    }

    public boolean sobrescribir(String ruta) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta, false))) {
            oos.writeObject(this);
            oos.flush();
            System.out.println("Archivo sobrescrito con el administrador actual.");
            return true;
        } catch (IOException e) {
            System.out.println("Error al sobrescribir el archivo: " + e.getMessage());
            return false;
        }
    }

    public static List<Administrador> leerArchivosHistoricos() {
        return cargarDesdeArchivo(DEFAULT_FILE);
    }
}

