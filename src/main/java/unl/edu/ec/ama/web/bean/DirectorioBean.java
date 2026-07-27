package unl.edu.ec.ama.web.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.ama.data.dto.GameResult;
import unl.edu.ec.ama.data.entity.User;
import unl.edu.ec.ama.data.service.GameService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("directorioBean")
@ViewScoped
public class DirectorioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Avatar por defecto mientras no exista un campo de foto persistido en la BD.
    private static final String AVATAR_POR_DEFECTO =
            "https://lh3.googleusercontent.com/aida-public/AB6AXuC_pyiqMdd25au-k_YRZVQ2cK5TLAuZl_CSq9Pf6mrHcIPzFOAhavRLMydskQ-v8XcO8zxDevckpzurkWD6VQist5ZY_SAvuNHaNvjag7gHjWIq3Rno1bO7BVcEn9i3BjvgNqomioJDYa1Pm3iyjn5ihDdN1s_rOwwB6JMsNZHgOuKRTvmx_7UoBHJFlvgEa5zsDKOuQ2fA2gJlhQY3VoC6zTrJYdoVW0cUmi1uBdwC6CbxiOHKzOnt";

    @Inject
    private GameService gameService;

    private List<Estudiante> listaEstudiantes;
    private int totalEstudiantes;
    private int estudiantesMostrados;

    @PostConstruct
    public void init() {
        cargarEstudiantes();
    }

    private void cargarEstudiantes() {
        listaEstudiantes = new ArrayList<>();

        List<User> usuarios = gameService.listarEstudiantes();

        for (User usuario : usuarios) {
            // El "estado" se deriva de si el estudiante ya registró resultados,
            // no es un dato quemado.
            List<GameResult> resultados = gameService.obtenerResultadosPorUsuario(usuario.getName());
            boolean tieneEvaluaciones = !resultados.isEmpty();

            String estado = tieneEvaluaciones ? "Completado" : "Pendiente";
            String estadoCss = tieneEvaluaciones
                    ? "bg-tertiary-fixed text-on-tertiary-fixed-variant"
                    : "bg-secondary-container text-on-secondary-container";
            String headerCss = tieneEvaluaciones ? "bg-secondary" : "bg-on-surface-variant";

            listaEstudiantes.add(new Estudiante(
                    usuario.getName(),   // "expediente": usamos el nombre porque es la clave natural (@Id) en User
                    usuario.getName(),
                    AVATAR_POR_DEFECTO,
                    estado,
                    estadoCss,
                    usuario.getAge(),
                    usuario.getSchoolGrade(),
                    headerCss
            ));
        }

        totalEstudiantes = listaEstudiantes.size();
        estudiantesMostrados = listaEstudiantes.size();
    }

    /**
     * Acción invocada desde la tarjeta del estudiante en el Directorio.
     * Elimina al estudiante (y sus resultados) de la BD, refresca la lista
     * mostrada en pantalla y confirma el resultado con un FacesMessage.
     */
    public void eliminarEstudiante(String nombreEstudiante) {
        boolean eliminado = gameService.eliminarEstudiante(nombreEstudiante);
        cargarEstudiantes();

        if (eliminado) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                            "El estudiante \"" + nombreEstudiante + "\" fue eliminado correctamente."));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso",
                            "El estudiante \"" + nombreEstudiante + "\" ya no existía en la base de datos."));
        }
    }

    public List<Estudiante> getListaEstudiantes() { return listaEstudiantes; }
    public int getTotalEstudiantes() { return totalEstudiantes; }
    public int getEstudiantesMostrados() { return estudiantesMostrados; }

    public static class Estudiante implements Serializable {
        private static final long serialVersionUID = 1L;
        private String expediente, nombre, fotoUrl, estado, estadoCss, headerCss;
        private int edad, grado;

        public Estudiante(String expediente, String nombre, String fotoUrl, String estado,
                           String estadoCss, int edad, int grado, String headerCss) {
            this.expediente = expediente;
            this.nombre = nombre;
            this.fotoUrl = fotoUrl;
            this.estado = estado;
            this.estadoCss = estadoCss;
            this.edad = edad;
            this.grado = grado;
            this.headerCss = headerCss;
        }

        public String getExpediente() { return expediente; }
        public String getNombre() { return nombre; }
        public String getFotoUrl() { return fotoUrl; }
        public String getEstado() { return estado; }
        public String getEstadoCss() { return estadoCss; }
        public int getEdad() { return edad; }
        public int getGrado() { return grado; }
        public String getHeaderCss() { return headerCss; }
    }
}
