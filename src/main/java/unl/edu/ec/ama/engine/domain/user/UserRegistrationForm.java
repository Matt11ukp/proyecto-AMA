package unl.edu.ec.ama.engine.domain.user;

import unl.edu.ec.ama.data.entity.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UserRegistrationForm {

    private String name;
    private String birthDate; // ── NUEVO: Reemplazamos age por birthDate ──
    private String schoolGrade;
    private int selectedField;

    // ── NUEVO: Formato oficial para validar y convertir la fecha ──
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public UserRegistrationForm() {
        name = "";
        birthDate = "";
        schoolGrade = "";
        selectedField = 0;
    }

    public void reset() {
        name = "";
        birthDate = "";
        schoolGrade = "";
        selectedField = 0;
    }

    public void addCharacter(char character) {
        if (selectedField == 0) {
            addCharacterToName(character);
            return;
        }

        if (selectedField == 1) {
            addCharacterToBirthDate(character); // ── NUEVO: Redirigimos al nuevo método ──
            return;
        }

        addCharacterToSchoolGrade(character);
    }

    private void addCharacterToName(char character) {
        if (Character.isLetter(character) || character == ' ') {
            name += character;
        }
    }

    // ── NUEVO: Permite números y la barra '/' (Límite de 10 caracteres: DD/MM/AAAA) ──
    private void addCharacterToBirthDate(char character) {
        if ((Character.isDigit(character) || character == '/') && birthDate.length() < 10) {
            birthDate += character;
        }
    }

    private void addCharacterToSchoolGrade(char character) {
        if (Character.isDigit(character)) {
            schoolGrade += character;
        }
    }

    public void deleteCharacter() {
        if (selectedField == 0) {
            name = removeLastCharacter(name);
            return;
        }

        if (selectedField == 1) {
            birthDate = removeLastCharacter(birthDate); // ── NUEVO: Borramos de birthDate ──
            return;
        }

        schoolGrade = removeLastCharacter(schoolGrade);
    }

    private String removeLastCharacter(String text) {
        if (text.isEmpty()) {
            return text;
        }

        return text.substring(0, text.length() - 1);
    }

    public void selectNextField() {
        selectedField++;

        if (selectedField > 2) {
            selectedField = 0;
        }
    }

    public void selectPreviousField() {
        selectedField--;

        if (selectedField < 0) {
            selectedField = 2;
        }
    }

    public boolean isComplete() {
        return !name.trim().isEmpty()
                && !birthDate.trim().isEmpty()
                && !schoolGrade.trim().isEmpty();
    }

    // ── NUEVO: Convertimos el String a LocalDate al crear el objeto User ──
    public User createUser() {
        LocalDate fechaParsed = LocalDate.parse(birthDate, formatter);

        return new User(
                name.trim(),
                fechaParsed,
                Integer.parseInt(schoolGrade)
        );
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() { // ── NUEVO: Cambiamos el getter getAge() por getBirthDate() ──
        return birthDate;
    }

    public String getSchoolGrade() {
        return schoolGrade;
    }

    public int getSelectedField() {
        return selectedField;
    }
}