package unl.edu.ec.ama.web.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.ama.data.dto.GameResult;
import unl.edu.ec.ama.data.service.GameService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean del Dashboard de Reportes.
 * <p>
 * Recibe el nombre del estudiante como parámetro de vista (?usuario=...,
 * mapeado con &lt;f:viewParam&gt; en el XHTML) y arma, para ese estudiante,
 * un módulo por cada tipo de prueba distinto que exista en la tabla
 * "gameresult" (columna nombre_prueba). Así el dashboard no depende de que
 * existan exactamente 4 pruebas fijas: si el equipo agrega una nueva
 * subclase de Test, aparecerá aquí automáticamente sin tocar el bean.
 */
@Named("dashboardBean")
@ViewScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Traduce el nombre técnico de la prueba (GameResultEntity.testName,
     * que hoy viene del getSimpleName() de la subclase de Test, ej. "Visual",
     * "Memory") al título vistoso que ya usa la vista. Si no hay traducción
     * registrada, se muestra el nombre técnico tal cual.
     */
    private static final Map<String, String> ETIQUETAS_PRUEBA = Map.of(
            "Visual", "Test Visual: El Intruso del Bosque",
            "Memory", "Test de Memoria: El Tesoro Inverso"
            // TODO: agregar aquí "Auditivo" -> "Test Verbal: El Pergamino de las Órdenes"
            // y "Concentracion" -> "Test de Concentración: La Escolta Pixelada"
            // en cuanto existan esas subclases de Test en el motor del juego.
    );

    @Inject
    private GameService gameService;

    // Se completa automáticamente por <f:viewParam name="usuario" .../>
    private String usuario;
    private boolean datosCargados = false;

    private String pacienteNombre;
    private int pacienteEdad;
    private int pacienteGrado;
    private String pacienteUltimaEval = "Sin evaluaciones registradas";
    private boolean tieneEvaluaciones;

    private List<ModuloResultado> modulos = new ArrayList<>();

    private int atencionPorcentaje;
    private int precisionPorcentaje;
    private int rapidezPorcentaje;

    /**
     * Invocado por &lt;f:viewAction&gt; en el XHTML, NO por @PostConstruct.
     * <p>
     * Motivo: &lt;f:viewParam&gt; asigna "usuario" en la fase Update Model
     * Values del ciclo de vida de JSF, y esa asignación ocurre resolviendo
     * la propia expresión #{dashboardBean.usuario}. Si el bean todavía no
     * existe, CDI lo crea en ese momento — lo que dispara @PostConstruct
     * ANTES de que el setter reciba el valor real. Resultado: dentro de un
     * método @PostConstruct, "usuario" siempre llega null, aunque el
     * parámetro sí esté presente en la URL. f:viewAction se ejecuta en la
     * fase Invoke Application, que es posterior, así que aquí "usuario" ya
     * tiene el valor correcto.
     */
    public void cargarDatos() {
        if (usuario == null || usuario.isBlank()) {
            return; // se navegó sin ?usuario=...; la vista puede mostrar un aviso
        }
        if (datosCargados) {
            return; // evita recargar en cada postback de la misma vista
        }
        datosCargados = true;

        gameService.buscarUsuarioPorNombre(usuario).ifPresent(u -> {
            pacienteNombre = u.getName();
            pacienteEdad = u.getAge();
            pacienteGrado = u.getSchoolGrade();
        });

        cargarModulos();
    }

    private void cargarModulos() {
        List<GameResult> resultados = gameService.obtenerResultadosPorUsuario(usuario);
        tieneEvaluaciones = !resultados.isEmpty();
        if (!tieneEvaluaciones) {
            return;
        }

        // La lista ya viene ordenada por id DESC (ver GameService), así que
        // el primer resultado que encontremos por cada testName es el más reciente.
        Map<String, GameResult> ultimoPorPrueba = new LinkedHashMap<>();
        for (GameResult r : resultados) {
            ultimoPorPrueba.putIfAbsent(r.getTestName(), r);
        }

        int sumaAciertos = 0;
        int sumaIntentos = 0;

        for (GameResult r : ultimoPorPrueba.values()) {
            String etiqueta = ETIQUETAS_PRUEBA.getOrDefault(r.getTestName(), r.getTestName());
            int total = r.getSuccesses() + r.getMistakes();

            modulos.add(new ModuloResultado(etiqueta, r.getFormattedTime(), r.getSuccesses(), total, r.getMistakes()));

            sumaAciertos += r.getSuccesses();
            sumaIntentos += total;
        }

        // resultados.get(0) es la prueba más reciente de todas (cualquier tipo)
        pacienteUltimaEval = resultados.get(0).getTestName() + " · " + resultados.get(0).getFormattedTime();

        // Métricas de resumen: cálculo simple a partir de aciertos/intentos.
        // El criterio clínico definitivo (ponderaciones por módulo, tiempos, etc.)
        // lo debe afinar el equipo; se deja este cálculo como punto de partida real.
        precisionPorcentaje = sumaIntentos > 0 ? Math.round(100f * sumaAciertos / sumaIntentos) : 0;
        atencionPorcentaje = precisionPorcentaje;
        rapidezPorcentaje = precisionPorcentaje;
    }

    // --- Getter/Setter del parámetro de vista ---
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    // --- Getters usados por el XHTML ---
    public String getPacienteNombre() { return pacienteNombre; }
    public int getPacienteEdad() { return pacienteEdad; }
    public int getPacienteGrado() { return pacienteGrado; }
    public String getPacienteUltimaEval() { return pacienteUltimaEval; }
    public boolean isTieneEvaluaciones() { return tieneEvaluaciones; }
    public List<ModuloResultado> getModulos() { return modulos; }
    public int getAtencionPorcentaje() { return atencionPorcentaje; }
    public int getPrecisionPorcentaje() { return precisionPorcentaje; }
    public int getRapidezPorcentaje() { return rapidezPorcentaje; }

    public static class ModuloResultado implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String etiqueta;
        private final String tiempo;
        private final int aciertos;
        private final int total;
        private final int errores;

        public ModuloResultado(String etiqueta, String tiempo, int aciertos, int total, int errores) {
            this.etiqueta = etiqueta;
            this.tiempo = tiempo;
            this.aciertos = aciertos;
            this.total = total;
            this.errores = errores;
        }

        public String getEtiqueta() { return etiqueta; }
        public String getTiempo() { return tiempo; }
        public int getAciertos() { return aciertos; }
        public int getTotal() { return total; }
        public int getErrores() { return errores; }
    }
}
