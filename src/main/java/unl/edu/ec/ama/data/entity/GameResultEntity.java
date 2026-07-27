package unl.edu.ec.ama.data.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "gameresult")
public class GameResultEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_prueba", length = 50)
    private String testName;

    @ManyToOne
    @JoinColumn(name = "nombre_usuario")
    private User user;

    @Column(nullable = false)
    private int successes;

    @Column(nullable = false)
    private int mistakes;

    @Column(name = "duracion")
    private double time;

    // constructor vacío obligatorio para JPA
    public GameResultEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSuccesses() {
        return successes;
    }

    public void setSuccesses(int successes) {
        this.successes = successes;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
