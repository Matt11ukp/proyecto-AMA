package unl.edu.ec.ama.web.rest;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import unl.edu.ec.ama.data.service.GameService;

import java.io.IOException;
import java.time.LocalDate; // <-- 1. NUEVO: Importamos LocalDate para procesar la fecha

// servicio web accesible a travez de esa url
// El propio motor del juego (clase domain.Test, en la JVM del Swing) hace la
// petición HTTP directamente y este servlet la atiende. Es un adaptador
// delgado: solo traduce parámetros HTTP -> tipos primitivos y delega toda la
// lógica de negocio en GameService.
@WebServlet("/finalizarPrueba")
public class RecepcionDatosServlet extends HttpServlet {

    @Inject
    private GameService gameService;

    // do get y dopost redirigen ambos tipos de peticiones hacia un metodo precesarYRedirigir
    // get y post para que no falle sin importar como reciba los datos
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        procesarYRedirigir(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        procesarYRedirigir(request, response);
    }

    // extrae los valores enviados por la URL
    private void procesarYRedirigir(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(">>> PETICION RECIBIDA: " + request.getMethod() + " | QUERY STRING: " + request.getQueryString());

        String nombre = request.getParameter("name");
        String birthDateStr = request.getParameter("birthDate"); // <-- Leemos la fecha de nacimiento
        String gradoStr = request.getParameter("schoolGrade");
        String prueba = request.getParameter("testName");
        String succStr = request.getParameter("successes");
        String mistStr = request.getParameter("mistakes");
        String timeStr = request.getParameter("time");

        // <-- 2. MODIFICADO: Reemplazamos edadStr por birthDateStr en la validación
        if (nombre == null || birthDateStr == null || gradoStr == null || succStr == null || mistStr == null || timeStr == null) {
            System.err.println(" [ERROR] La petición llegó incompleta (algún parámetro es NULL).");
            System.err.println(" -> Revisa la línea 'QUERY STRING' arriba para ver qué parámetro faltó enviar desde el juego.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetros incompletos en el POST");
            return;
        }

        // <-- 3. NUEVO: Convertimos el texto a una fecha LocalDate real en lugar de un entero
        LocalDate fechaNacimiento = LocalDate.parse(birthDateStr);
        int aciertos = Integer.parseInt(succStr);
        int errores  = Integer.parseInt(mistStr);
        double tiempo = Double.parseDouble(timeStr.replace(",", "."));

        // <-- 4. MODIFICADO: Pasamos fechaNacimiento a tu servicio actualizado
        gameService.registrarResultado(nombre, fechaNacimiento, gradoStr, prueba, aciertos, errores, tiempo);
        response.sendRedirect(request.getContextPath() + "/resultados.xhtml");
    }
}