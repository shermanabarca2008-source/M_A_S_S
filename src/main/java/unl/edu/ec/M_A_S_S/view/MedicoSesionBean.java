package unl.edu.ec.M_A_S_S.view;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Named;
import unl.edu.ec.M_A_S_S.domain.Cita;
import unl.edu.ec.M_A_S_S.domain.Medico;
import unl.edu.ec.M_A_S_S.domain.Notificacion;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Named
@SessionScoped
public class MedicoSesionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Medico medicoActual;
    private LocalDate mesCalendario = LocalDate.now().withDayOfMonth(1);
    private LocalDate fechaSeleccionada = LocalDate.now();
    private String terminoBusqueda = "";

    public Medico getMedicoActual() {
        return medicoActual;
    }

    public void setMedicoActual(Medico medicoActual) {
        this.medicoActual = medicoActual;
        this.mesCalendario = LocalDate.now().withDayOfMonth(1);
        this.fechaSeleccionada = LocalDate.now();
        this.terminoBusqueda = "";
    }

    public List<Cita> getCitasOrdenadas() {
        if (medicoActual == null || medicoActual.getCitas() == null) {
            return new ArrayList<>();
        }
        List<Cita> citas = new ArrayList<>(medicoActual.getCitas());
        citas.sort(Comparator.comparing(Cita::getFecha).thenComparing(Cita::getHora));
        return citas;
    }

    public List<Cita> getCitasFiltradas() {
        List<Cita> resultado = new ArrayList<>();
        for (Cita cita : getCitasOrdenadas()) {
            if (coincideBusqueda(cita)) {
                resultado.add(cita);
            }
        }
        return resultado;
    }

    public int getTotalCitas() {
        return medicoActual == null || medicoActual.getCitas() == null
                ? 0 : medicoActual.getCitas().size();
    }

    public int getCitasAgendadas() {
        return contarPorEstado(Cita.EstadoCita.AGENDADA);
    }

    public int getCitasFinalizadas() {
        return contarPorEstado(Cita.EstadoCita.FINALIZADA);
    }

    public int getCitasCanceladas() {
        return contarPorEstado(Cita.EstadoCita.CANCELADA);
    }

    public List<Notificacion> getNotificaciones() {
        List<Notificacion> resultado = new ArrayList<>();
        if (medicoActual == null || medicoActual.getCitas() == null) {
            return resultado;
        }
        for (Cita cita : medicoActual.getCitas()) {
            if (cita.getNotificacion() != null) {
                resultado.add(cita.getNotificacion());
            }
        }
        resultado.sort(Comparator.comparing(Notificacion::getFechaEnvio).reversed());
        return resultado;
    }

    private int contarPorEstado(Cita.EstadoCita estado) {
        if (medicoActual == null || medicoActual.getCitas() == null) {
            return 0;
        }
        int total = 0;
        for (Cita cita : medicoActual.getCitas()) {
            if (cita.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }

    public int getTotalPacientes() {
        Set<String> pacientes = new HashSet<>();
        for (Cita cita : getCitasOrdenadas()) {
            if (cita.getPaciente() != null) {
                String identificador = cita.getPaciente().getId() == null
                        ? cita.getPaciente().getCedula()
                        : cita.getPaciente().getId().toString();
                pacientes.add(identificador);
            }
        }
        return pacientes.size();
    }

    public int getCitasHoy() {
        return getCitasPorFecha(LocalDate.now()).size();
    }

    public Cita getProximaCita() {
        LocalDate hoy = LocalDate.now();
        for (Cita cita : getCitasOrdenadas()) {
            LocalDate fecha = convertirFecha(cita);
            if (fecha != null
                    && !fecha.isBefore(hoy)
                    && cita.getEstado() != Cita.EstadoCita.CANCELADA
                    && cita.getEstado() != Cita.EstadoCita.FINALIZADA) {
                return cita;
            }
        }
        return null;
    }

    public String getFechaActualCompleta() {
        return formatearFecha(LocalDate.now(), "EEEE, dd 'de' MMMM 'de' yyyy");
    }

    public String getTituloMes() {
        return capitalizar(formatearFecha(mesCalendario, "MMMM yyyy"));
    }

    public String getFechaSeleccionadaFormateada() {
        return capitalizar(formatearFecha(fechaSeleccionada, "EEEE, dd 'de' MMMM"));
    }

    public List<DiaCalendario> getDiasCalendario() {
        List<DiaCalendario> dias = new ArrayList<>();
        LocalDate inicio = mesCalendario.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate hoy = LocalDate.now();

        for (int i = 0; i < 42; i++) {
            LocalDate fecha = inicio.plusDays(i);
            List<Cita> citas = getCitasPorFecha(fecha);
            dias.add(new DiaCalendario(
                    fecha,
                    fecha.getMonth() == mesCalendario.getMonth()
                            && fecha.getYear() == mesCalendario.getYear(),
                    fecha.equals(hoy),
                    fecha.equals(fechaSeleccionada),
                    citas));
        }
        return dias;
    }

    public List<Cita> getCitasFechaSeleccionada() {
        return getCitasPorFecha(fechaSeleccionada);
    }

    public int getTotalCitasFechaSeleccionada() {
        return getCitasFechaSeleccionada().size();
    }

    public int getCitasRestantesFechaSeleccionada() {
        int total = 0;
        for (Cita cita : getCitasFechaSeleccionada()) {
            if (cita.getEstado() != Cita.EstadoCita.FINALIZADA
                    && cita.getEstado() != Cita.EstadoCita.CANCELADA) {
                total++;
            }
        }
        return total;
    }

    public String seleccionarDia(LocalDate fecha) {
        if (fecha != null) {
            fechaSeleccionada = fecha;
            mesCalendario = fecha.withDayOfMonth(1);
        }
        return null;
    }

    public String mesAnterior() {
        mesCalendario = mesCalendario.minusMonths(1);
        fechaSeleccionada = mesCalendario.withDayOfMonth(1);
        return null;
    }

    public String mesSiguiente() {
        mesCalendario = mesCalendario.plusMonths(1);
        fechaSeleccionada = mesCalendario.withDayOfMonth(1);
        return null;
    }

    public String irAHoy() {
        fechaSeleccionada = LocalDate.now();
        mesCalendario = fechaSeleccionada.withDayOfMonth(1);
        return null;
    }

    public void buscarCitas(AjaxBehaviorEvent event) {
        // El valor se actualiza mediante JSF; el método permite refrescar el calendario por Ajax.
    }

    public String cerrarSesion() {
        medicoActual = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index.xhtml?faces-redirect=true";
    }

    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }

    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    private List<Cita> getCitasPorFecha(LocalDate fecha) {
        List<Cita> resultado = new ArrayList<>();
        if (fecha == null) {
            return resultado;
        }
        for (Cita cita : getCitasOrdenadas()) {
            if (fecha.equals(convertirFecha(cita)) && coincideBusqueda(cita)) {
                resultado.add(cita);
            }
        }
        resultado.sort(Comparator.comparing(Cita::getHora));
        return resultado;
    }

    private boolean coincideBusqueda(Cita cita) {
        if (terminoBusqueda == null || terminoBusqueda.trim().isEmpty()) {
            return true;
        }
        String termino = terminoBusqueda.trim().toLowerCase();
        String paciente = cita.getPaciente() == null ? "" : cita.getPaciente().getNombreCompleto();
        String cedula = cita.getPaciente() == null ? "" : cita.getPaciente().getCedula();
        String estado = cita.getEstado() == null ? "" : cita.getEstado().name();
        return paciente.toLowerCase().contains(termino)
                || cedula.toLowerCase().contains(termino)
                || estado.toLowerCase().contains(termino);
    }

    private LocalDate convertirFecha(Cita cita) {
        if (cita == null || cita.getFecha() == null) {
            return null;
        }
        return Instant.ofEpochMilli(cita.getFecha().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String formatearFecha(LocalDate fecha, String patron) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern(
                patron, Locale.forLanguageTag("es-EC"));
        return fecha.format(formato);
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    public static class DiaCalendario implements Serializable {

        private static final long serialVersionUID = 1L;

        private final LocalDate fecha;
        private final boolean mesActual;
        private final boolean hoy;
        private final boolean seleccionado;
        private final List<Cita> citas;

        public DiaCalendario(LocalDate fecha, boolean mesActual, boolean hoy,
                             boolean seleccionado, List<Cita> citas) {
            this.fecha = fecha;
            this.mesActual = mesActual;
            this.hoy = hoy;
            this.seleccionado = seleccionado;
            this.citas = citas;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public int getNumero() {
            return fecha.getDayOfMonth();
        }

        public boolean isMesActual() {
            return mesActual;
        }

        public boolean isHoy() {
            return hoy;
        }

        public boolean isSeleccionado() {
            return seleccionado;
        }

        public List<Cita> getCitas() {
            return citas;
        }

        public List<Cita> getCitasVisibles() {
            return citas.size() <= 2 ? citas : citas.subList(0, 2);
        }

        public int getCantidadAdicional() {
            return Math.max(0, citas.size() - 2);
        }
    }
}
