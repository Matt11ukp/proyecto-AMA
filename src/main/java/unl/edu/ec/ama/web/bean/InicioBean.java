package unl.edu.ec.ama.web.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("inicioBean")
@RequestScoped

public class InicioBean {
    // REGLA APLICADA: Inicialización obligatoria como tipo primitivo (int), NO Integer.
    private int institucionesRegistradas = 500;
    private int evaluacionesRealizadas = 15000;

    public InicioBean() {
        // Constructor por defecto
    }

    public int getInstitucionesRegistradas() {
        return institucionesRegistradas;
    }

    public void setInstitucionesRegistradas(int institucionesRegistradas) {
        this.institucionesRegistradas = institucionesRegistradas;
    }

    public int getEvaluacionesRealizadas() {
        return evaluacionesRealizadas;
    }

    public void setEvaluacionesRealizadas(int evaluacionesRealizadas) {
        this.evaluacionesRealizadas = evaluacionesRealizadas;
    }
}
