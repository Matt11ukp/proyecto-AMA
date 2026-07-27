package unl.edu.ec.ama.data.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;


/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

@Entity
@Table(name = "usuarios")

public class User implements Serializable {
    @Id
    @Column(unique = true, nullable = false, length = 30)
    private String name;
    private LocalDate birthDate;
    private int schoolGrade;
    @Transient
    private int skinIndex;
    @Transient
    private int hairIndex;
    @Transient
    private int shirtIndex;
    @Transient
    private int eyeIndex;
    @Transient
    private boolean female;
    @Transient
    private int coins;
    @Transient
    private int keys;
    @Transient
    private int life;
  public User(String name, LocalDate birthDate, int schoolGrade) {
    this.name = name;
    this.birthDate = birthDate;
    this.schoolGrade = schoolGrade;

    skinIndex = 0;
    hairIndex = 0;
    shirtIndex = 0;
    eyeIndex = 0;
    female = false;

    coins = 0;
    keys = 0;
    life = 6;
}
    public User() {
    }

    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }

public int getSkinIndex() {
    return skinIndex;
}
public void setSkinIndex(int skinIndex) {
    this.skinIndex = skinIndex;
}

public int getHairIndex() {
    return hairIndex;
}

public void setHairIndex(int hairIndex) {
    this.hairIndex = hairIndex;
}

public int getShirtIndex() {
    return shirtIndex;
}

public void setShirtIndex(int shirtIndex) {
    this.shirtIndex = shirtIndex;
}

public int getEyeIndex() {
    return eyeIndex;
}

public void setEyeIndex(int eyeIndex) {
    this.eyeIndex = eyeIndex;
}

public boolean isFemale() {
    return female;
}

public void setFemale(boolean female) {
    this.female = female;
}

public int getCoins() {
    return coins;
}

public void setCoins(int coins) {
    this.coins = coins;
}

public int getKeys() {
    return keys;
}

public void setKeys(int keys) {
    this.keys = keys;
}

public int getLife() {
    return life;
}

public void setLife(int life) {
    this.life = life;
}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public int getSchoolGrade() {
        return schoolGrade;
    }

    public void setSchoolGrade(int schoolGrade) {
        this.schoolGrade = schoolGrade;
    }
}
