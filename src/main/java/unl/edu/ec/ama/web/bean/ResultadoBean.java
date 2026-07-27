package unl.edu.ec.ama.web.bean;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.ama.data.dto.GameResult;
import unl.edu.ec.ama.data.service.GameService;
// CDI (context and dependency injection), con nombre resultadoBean, sirve para los xhtml:
// {resultadoBean.gameResult.time}
@Named("resultadoBean")
// Define el alcance (scope del bean), esta en especifico indica que vive solo durante una peticion HTTP
// osea que cada que se abre o recarga la pagina, el servidor crea una nueva instancia de este bean
// y al terminar la destruye
// esta es ideal para mostrar paginas de resultados puntuales como en este caso
@RequestScoped
public class ResultadoBean {
    // injectamos GameService en lugar de hacer un new
    @Inject
    private GameService gameService;

    private GameResult gameResult;
    //ejecuta el metodo luego de que la clase ha sido creada y todas sus dependencias han sido inyectadas
    // pero antes de entregarle la pagina web al usuario
    @PostConstruct
    public void init() {
        // Le pedimos al servicio el último resultado que envió el juego Java
        this.gameResult = gameService.obtenerUltimoResultado();
    }

    // Devuelve la fecha actual formateada en estilo retro (ej. 26/07/26) sin tocar BD
    public String getFechaActual() {
        return java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy"));
    }

    public GameResult getGameResult() { return gameResult; }
}