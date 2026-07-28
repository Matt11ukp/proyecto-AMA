package unl.edu.ec.ama.engine.domain.entity;

import unl.edu.ec.ama.data.dto.GameResult;
import unl.edu.ec.ama.data.entity.User;
import unl.edu.ec.ama.engine.view.render.GamePanel;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public abstract class Test {

    // El motor del juego (esta clase) corre en la JVM del Swing y el
    // servidor Jakarta EE corre por separado en Open Liberty: son dos
    // procesos independientes. La única forma de que el resultado llegue a
    // la base de datos es notificarlo por HTTP al servlet que lo recibe
    // (RecepcionDatosServlet -> GameService). Antes esta responsabilidad
    // vivía en una clase intermedia aparte (ClienteIntegracionWeb) que solo
    // envolvía, sin aportar nada, una única llamada HTTP; se eliminó esa
    // capa y el propio Test arma y envía la notificación con el HttpClient
    // estándar de Java.
    private static final String ENDPOINT_RESULTADO = "http://127.0.0.1:9080/jbrew/finalizarPrueba";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .executor(java.util.concurrent.Executors.newFixedThreadPool(2, r -> {
                Thread t = new Thread(r, "ama-http-client");
                t.setDaemon(false);
                return t;
            }))
            .build();

    protected int successes;
    protected int mistakes;
    protected boolean testCompleted;

    private long startTime;
    private long endTime;

    protected GamePanel gp;

    public Test(GamePanel gp) {
        this.gp = gp;
        successes = 0;
        mistakes = 0;
        testCompleted = false;
    }

    public Test(Instruction actualInstruction, boolean b) {
    }

    public void startTest() {
        successes = 0;
        mistakes = 0;
        testCompleted = false;
        startTime = System.currentTimeMillis();
        onStart();
    }

    public GameResult endTest() {
        endTime = System.currentTimeMillis();
        testCompleted = true;
        double tiempoFinal = getElapsedTime();

        // Calculamos el resultado normal para que el juego no pierda su lógica
        GameResult resultado = new GameResult(successes, mistakes, tiempoFinal);

        // Si hay un usuario activo, notificamos el resultado al servidor Jakarta EE
        if (gp != null && gp.getCurrentUser() != null) {
            notificarResultadoAlServidor(gp.getCurrentUser(), tiempoFinal);
        } else {
            // Protección por si hacen pruebas en consola sin interfaz o sin usuario
            System.err.println(" [WARN] No hay usuario activo en GamePanel. No se envió a la BD.");
        }

        // Retornamos el resultado intacto para la ventana gráfica
        return resultado;
    }

    public static void warmUpConexionServidor() {
        HttpRequest warmUp = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT_RESULTADO))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        HTTP_CLIENT.sendAsync(warmUp, HttpResponse.BodyHandlers.discarding())
                .exceptionally(e -> null); // ignoramos el resultado, solo precalienta
    }

    // Envía el resultado a /finalizarPrueba en segundo plano, para no congelar el juego.

    // Envía el resultado de forma SÍNCRONA para garantizar que la BD guarde antes de continuar
    private void notificarResultadoAlServidor(User usuario, double tiempoFinal) {
        String nombrePrueba = this.getClass().getSimpleName();

        String birthDateParam = usuario.getBirthDate() != null
                ? usuario.getBirthDate().toString()
                : "";

        String cuerpo =
                "name=" + encode(usuario.getName()) +
                        "&birthDate=" + encode(birthDateParam) +
                        "&schoolGrade=" + usuario.getSchoolGrade() +
                        "&testName=" + encode(nombrePrueba) +
                        "&successes=" + successes +
                        "&mistakes=" + mistakes +
                        "&time=" + tiempoFinal;

        // CORRECCIÓN AQUÍ: ya no se dispara en segundo plano. endTest() se queda
        // esperando aquí (bloqueante) hasta que el POST responda o se agoten los
        // reintentos. Así, cuando endTest() retorna, el registro YA está confirmado
        // en Postgres y recién entonces el llamador (Visual/Concentration/Verbal)
        // hace Desktop.browse(...) hacia resultados.xhtml. Se elimina la carrera
        // entre el INSERT asíncrono y la apertura del navegador.
        enviarConReintentoBloqueante(cuerpo, 1);
    }

    private void enviarConReintentoBloqueante(String cuerpo, int intento) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT_RESULTADO))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8))
                .build();

        try {
            // CORRECCIÓN AQUÍ: send() en vez de sendAsync(). Al ser localhost, el
            // round-trip son unos pocos milisegundos; ese pequeño costo es preferible
            // a mostrar resultados incorrectos/vacíos en el primer intento.
            HttpResponse<String> response =
                    HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 400) {
                System.out.println(" [OK] Partida guardada en BD exitosamente (intento " + intento + ").");
            } else if (intento < 3) {
                System.err.println(" [WARN] Respuesta " + response.statusCode() + ", reintentando...");
                enviarConReintentoBloqueante(cuerpo, intento + 1);
            } else {
                System.err.println(" [ERROR] El servidor respondió con: " + response.statusCode() + " tras " + intento + " intentos.");
            }
        } catch (Exception e) {
            if (intento < 3) {
                System.err.println(" [WARN] Fallo en intento " + intento + " (" + e.getMessage() + "), reintentando...");
                enviarConReintentoBloqueante(cuerpo, intento + 1);
            } else {
                System.err.println(" [ERROR] No se pudo conectar tras " + intento + " intentos.");
                System.err.println(" ---> Motivo técnico: " + e.getMessage());
            }
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    protected void addSuccess() {
        successes++;
    }

    protected void addMistake() {
        mistakes++;
    }

    private double getElapsedTime() {
        return (endTime - startTime) / 1000.0;
    }

    public boolean isTestCompleted() {
        return testCompleted;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getMistakes() {
        return mistakes;
    }

    protected abstract void onStart();

    public abstract void update();


}
