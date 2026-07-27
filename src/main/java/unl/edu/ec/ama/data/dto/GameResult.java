package unl.edu.ec.ama.data.dto;

import java.io.Serializable;

 // @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada

public class GameResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String testName;
    private UserSnapshot user;
    private int successes;
    private int mistakes;
    private double time;

    public GameResult() {
    }

    public GameResult(int successes, int mistakes, double time) {
        this.successes = successes;
        this.mistakes = mistakes;
        this.time = time;
    }

    public GameResult(Long id, String testName, UserSnapshot user, int successes, int mistakes, double time) {
        this.id = id;
        this.testName = testName;
        this.user = user;
        this.successes = successes;
        this.mistakes = mistakes;
        this.time = time;
    }

    public String getFormattedTime() {
        return String.format("%.2f seg", this.time);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserSnapshot getUser() {
        return user;
    }

    public void setUser(UserSnapshot user) {
        this.user = user;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}
