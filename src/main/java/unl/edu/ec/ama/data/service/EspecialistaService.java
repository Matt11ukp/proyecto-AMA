package unl.edu.ec.ama.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import unl.edu.ec.ama.data.entity.EspecialistaEntity;

import java.util.Optional;

/**
 * Lógica de negocio para el acceso y registro de especialistas.
 * El hash/verificación de contraseñas se resuelve aquí con jBCrypt.
 */
@ApplicationScoped
public class EspecialistaService {

    @PersistenceContext(unitName = "jbrewPU")
    private EntityManager em;

    /**
     * Busca un especialista por su nombre de usuario O por su correo electrónico.
     * Es la base del "login dual": el mismo campo del formulario sirve para ambos.
     */
    public Optional<EspecialistaEntity> buscarPorUsuarioOEmail(String usuarioOEmail) {
        try {
            EspecialistaEntity especialista = em.createQuery(
                            "SELECT e FROM EspecialistaEntity e " +
                                    "WHERE e.usuario = :valor OR e.email = :valor",
                            EspecialistaEntity.class)
                    .setParameter("valor", usuarioOEmail)
                    .getSingleResult();
            return Optional.of(especialista);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Verifica la contraseña en texto plano contra el hash almacenado en BD.
     */
    public boolean verificarPassword(String passwordPlano, String passwordHash) {
        if (passwordPlano == null || passwordHash == null) {
            return false;
        }
        return BCrypt.checkpw(passwordPlano, passwordHash);
    }

    /**
     * Registra un nuevo especialista, hasheando la contraseña con BCrypt.
     * Usado por RegistroBean.
     *
     * @throws EspecialistaExistenteException si el usuario o el email ya están registrados.
     */
    @Transactional
    public EspecialistaEntity registrar(String nombreCompleto, String usuario, String email, String passwordPlano) {
        Long existentes = em.createQuery(
                        "SELECT COUNT(e) FROM EspecialistaEntity e " +
                                "WHERE e.usuario = :usuario OR e.email = :email", Long.class)
                .setParameter("usuario", usuario)
                .setParameter("email", email)
                .getSingleResult();

        if (existentes > 0) {
            throw new EspecialistaExistenteException("El usuario o correo ya está registrado.");
        }

        EspecialistaEntity especialista = new EspecialistaEntity();
        especialista.setNombreCompleto(nombreCompleto);
        especialista.setUsuario(usuario);
        especialista.setEmail(email);
        especialista.setPasswordHash(BCrypt.hashpw(passwordPlano, BCrypt.gensalt()));
        especialista.setActivo(true);

        em.persist(especialista);
        return especialista;
    }
}
