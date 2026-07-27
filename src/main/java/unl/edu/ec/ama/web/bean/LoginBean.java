package unl.edu.ec.ama.web.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.ama.data.service.EspecialistaService;
import unl.edu.ec.ama.data.entity.EspecialistaEntity;

import java.io.Serializable;
import java.util.Optional;

@Named("loginBean")
@ViewScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EspecialistaService especialistaService;

    private String usuario;
    private String password;
    private boolean recordarme;

    public String login() {
        try {
            Optional<EspecialistaEntity> optEspecialista = especialistaService.buscarPorUsuarioOEmail(usuario);

            if (optEspecialista.isPresent() && especialistaService.verificarPassword(password, optEspecialista.get().getPasswordHash())) {
                // Credenciales correctas: Redirige al Directorio de Estudiantes
                return "/ama_directorio_de_estudiantes.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario o contraseña incorrectos."));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ocurrió un error al iniciar sesión."));
            return null;
        }
    }

    // Getters y Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isRecordarme() { return recordarme; }
    public void setRecordarme(boolean recordarme) { this.recordarme = recordarme; }
}