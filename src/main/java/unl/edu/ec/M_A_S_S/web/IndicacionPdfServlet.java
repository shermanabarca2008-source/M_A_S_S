package unl.edu.ec.M_A_S_S.web;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import unl.edu.ec.M_A_S_S.domain.IndicacionesMedicas;
import unl.edu.ec.M_A_S_S.view.IndicacionesMedicasBean;

import java.io.IOException;

@WebServlet("/indicacionPdf")
public class IndicacionPdfServlet extends HttpServlet {

    @Inject
    private IndicacionesMedicasBean indicacionesMedicasBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro id");
            return;
        }

        Long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
            return;
        }

        IndicacionesMedicas indicacion = indicacionesMedicasBean.buscarIndicacionPorId(id);
        if (indicacion == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Indicación no encontrada");
            return;
        }

        byte[] pdfBytes = indicacionesMedicasBean.generarPdfIndicacion(id);
        if (pdfBytes == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo generar el PDF");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"indicacion_" + id + ".pdf\"");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
    }
}
