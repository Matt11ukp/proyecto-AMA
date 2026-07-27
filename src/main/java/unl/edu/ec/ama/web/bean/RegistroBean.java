package unl.edu.ec.ama.web.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.ec.ama.data.service.EspecialistaExistenteException;
import unl.edu.ec.ama.data.service.EspecialistaService;

import java.io.Serializable;

@Named("registroBean")
@ViewScoped
public class RegistroBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EspecialistaService especialistaService;

    private String nombre;
    private String usuario;
    private String email;
    private String password;

    public String registrar() {
        try {
            // El correo es opcional en el formulario; si no lo llenaron,
            // se genera uno por defecto a partir del usuario.
            String emailFinal = (email == null || email.isBlank())
                    ? usuario + "@unl.edu.ec"
                    : email;

            // EspecialistaService.registrar() hashea la contraseña con BCrypt
            // internamente: aquí solo viaja el texto plano que el usuario
            // escribió en el formulario, nunca se guarda tal cual en la BD.
            especialistaService.registrar(nombre, usuario, emailFinal, password);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario registrado correctamente. Inicia sesión."));

            return "/ama_acceso_especialistas.xhtml?faces-redirect=true";

        } catch (EspecialistaExistenteException e) {
            // Usuario o email ya existen: mensaje amigable, no un stacktrace.
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El usuario o correo ya está registrado."));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el usuario."));
            return null;
        }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
