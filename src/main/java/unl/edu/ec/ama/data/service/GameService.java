package unl.edu.ec.ama.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import unl.edu.ec.ama.data.entity.GameResultEntity;
import unl.edu.ec.ama.data.dto.GameResult;
import unl.edu.ec.ama.data.entity.User;
import unl.edu.ec.ama.data.dto.UserSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class GameService {

    // inyecta el entityManager usando la persistencia configurada, para operaciones con base de datos
    @PersistenceContext(unitName = "jbrewPU")
    private EntityManager em;

    @Transactional
    public GameResult registrarResultado(String nombre, LocalDate fechaNacimiento, String grado, String nombrePrueba,
                                          int aciertos, int errores, double tiempo) {
        User usuario = buscarOCrearUsuario(nombre, fechaNacimiento, grado);

        GameResultEntity entity = new GameResultEntity();
        entity.setUser(usuario);
        entity.setTestName(nombrePrueba);
        entity.setSuccesses(aciertos);
        entity.setMistakes(errores);
        entity.setTime(tiempo);

        // Guardamos en PostgreSQL
        em.persist(entity);

        return toDto(entity);
    }

    public List<User> listarEstudiantes() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.name", User.class)
                .getResultList();
    }

    public List<GameResult> obtenerResultadosPorUsuario(String nombreUsuario) {
        TypedQuery<GameResultEntity> query = em.createQuery(
                "SELECT g FROM GameResultEntity g WHERE g.user.name = :nombre ORDER BY g.id DESC",
                GameResultEntity.class);
        query.setParameter("nombre", nombreUsuario);

        List<GameResult> resultados = new ArrayList<>();
        for (GameResultEntity entity : query.getResultList()) {
            resultados.add(toDto(entity));
        }
        return resultados;
    }

    @Transactional
    public boolean eliminarEstudiante(String nombreUsuario) {
        // 1. Borrar en cascada los resultados de pruebas del estudiante
        em.createQuery("DELETE FROM GameResultEntity g WHERE g.user.name = :nombre")
                .setParameter("nombre", nombreUsuario)
                .executeUpdate();

        // 2. Borrar al estudiante (si existe)
        User usuario = em.find(User.class, nombreUsuario);
        if (usuario != null) {
            em.remove(usuario);
            return true;
        }
        return false;
    }

    public Optional<User> buscarUsuarioPorNombre(String nombreUsuario) {
        try {
            User usuario = em.createQuery(
                            "SELECT u FROM User u WHERE u.name = :nombre", User.class)
                    .setParameter("nombre", nombreUsuario)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public GameResult obtenerUltimoResultado() {
        TypedQuery<GameResultEntity> query = em.createQuery(
                "SELECT g FROM GameResultEntity g ORDER BY g.id DESC", GameResultEntity.class);
        query.setMaxResults(1);
        List<GameResultEntity> resultados = query.getResultList();
        // si la tabla esta vacia se envuelve en ceros
        return resultados.isEmpty() ? new GameResult(0, 0, 0.0) : toDto(resultados.get(0));
    }

    private User buscarOCrearUsuario(String nombre, LocalDate fechaNacimiento, String grado) {
        // 1. Convertimos el grado a entero una sola vez aquí arriba para todo el método:
        int gradoInt = Integer.parseInt(grado);

        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class);
            query.setParameter("name", nombre);
            User usuario = query.getSingleResult();

            // 2. Le pasamos la fecha y el entero directamente:
            usuario.setBirthDate(fechaNacimiento);
            usuario.setSchoolGrade(gradoInt);
            return usuario;

        } catch (NoResultException e) {
            // 3. Al crear el usuario nuevo, le pasamos también el entero (¡adiós línea roja!):
            User usuario = new User(nombre, fechaNacimiento, gradoInt);
            em.persist(usuario);
            return usuario;
        }
    }

    private GameResult toDto(GameResultEntity entity) {
        return new GameResult(
                entity.getId(),
                entity.getTestName(),
                UserSnapshot.from(entity.getUser()),
                entity.getSuccesses(),
                entity.getMistakes(),
                entity.getTime()
        );
    }
}
