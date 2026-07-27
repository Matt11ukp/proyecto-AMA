package unl.edu.ec.ama.web.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import unl.edu.ec.ama.data.entity.EspecialistaEntity;

import java.io.Serializable;

/**
 * Bean de sesión (CDI, @SessionScoped) que representa al especialista/psicopedagogo
 * autenticado en el portal. Vive durante toda la sesión HTTP del usuario.
 *
 * Se referencia desde el layout base (template.xhtml) para pintar su nombre en el
 * sidebar/topbar y para el enlace de "Cerrar sesión". La lógica real de autenticación
 * contra PostgreSQL se implementará en el paso del login (ama_acceso_especialistas);
 * por ahora expone valores por defecto para poder maquetar y probar el template.
 */
@Named("especialistaSessionBean")
@SessionScoped
public class EspecialistaSessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre = "Dr. Especialista";
    private String email;
    private boolean autenticado = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    /**
     * Vuelca los datos del especialista autenticado en la sesión.
     * Se invoca desde {@code LoginBean.login()} justo después de validar la contraseña.
     */
    public void setEspecialista(EspecialistaEntity especialista) {
        this.id = especialista.getId();
        this.nombre = especialista.getNombreCompleto();
        this.email = especialista.getEmail();
        this.autenticado = true;
    }

    /**
     * Cierra la sesión del especialista e invalida la sesión HTTP,
     * luego redirige al login.
     */
    public String logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.getExternalContext().invalidateSession();
        return "/ama_acceso_especialistas.xhtml?faces-redirect=true";
    }
}
