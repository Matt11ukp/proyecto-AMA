package unl.edu.ec.ama.data.dto;

import unl.edu.ec.ama.data.entity.User;

import java.io.Serializable;


 // @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada

public class UserSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private int schoolGrade;

    public UserSnapshot() {
    }

    public UserSnapshot(String name, int age, int schoolGrade) {
        this.name = name;
        this.age = age;
        this.schoolGrade = schoolGrade;
    }

    public static UserSnapshot from(User user) {
        if (user == null) {
            return null;
        }
        return new UserSnapshot(user.getName(), user.getAge(), user.getSchoolGrade());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSchoolGrade() {
        return schoolGrade;
    }

    public void setSchoolGrade(int schoolGrade) {
        this.schoolGrade = schoolGrade;
    }
}
