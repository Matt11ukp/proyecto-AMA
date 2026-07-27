package unl.edu.ec.ama.engine.view.render;

import unl.edu.ec.ama.engine.domain.user.UserRegistrationForm;

import java.awt.*;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UserRegistrationDrawer {

    public void draw(Graphics2D g2, UserRegistrationForm form, int screenWidth, int screenHeight) {
        drawBackground(g2, screenWidth, screenHeight);
        drawTitle(g2, screenWidth);
        drawFields(g2, form);
    }

    private void drawBackground(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, screenWidth, screenHeight);
    }

    private void drawTitle(Graphics2D g2, int screenWidth) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 64F));

        String title = "REGISTRO DEL ESTUDIANTE";
        int x = getCenteredX(g2, title, screenWidth);
        int y = 120;

        drawTextWithShadow(g2, title, x, y, Color.PINK);
    }

private void drawFields(Graphics2D g2, UserRegistrationForm form) {
    Font pixelFont = g2.getFont().deriveFont(Font.BOLD, 42F);

    drawField(g2, pixelFont, "Nombre", form.getName(), 360, 260, form.getSelectedField() == 0);
    drawField(g2, pixelFont, "Nacimiento", form.getBirthDate(), 360, 350, form.getSelectedField() == 1);
    drawField(g2, pixelFont, "Grado", form.getSchoolGrade(), 360, 440, form.getSelectedField() == 2);
}

private void drawField(Graphics2D g2, Font pixelFont, String label, String value, int x, int y, boolean selected) {
    String prefix = selected ? "> " : "  ";
    String labelText = prefix + label + ": ";

    // Etiqueta pixelada
    g2.setFont(pixelFont);
    drawTextWithShadow(g2, labelText, x, y, Color.PINK);

    int labelWidth = g2.getFontMetrics().stringWidth(labelText);

    // Valor escrito con fuente lisa
    Font smoothFont = new Font("Arial", Font.BOLD, 36);
    g2.setFont(smoothFont);

    int valueX = x + labelWidth + 20;

    g2.setColor(Color.BLACK);
    g2.drawString(value, valueX + 3, y + 3);

    g2.setColor(Color.PINK);
    g2.drawString(value, valueX, y);

    // Volver a fuente pixel para el siguiente campo
    g2.setFont(pixelFont);
}

    private void drawTextWithShadow(Graphics2D g2, String text, int x, int y, Color color) {
        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 4, y + 4);

        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    private int getCenteredX(Graphics2D g2, String text, int screenWidth) {
        int textWidth = g2.getFontMetrics().stringWidth(text);
        return (screenWidth - textWidth) / 2;
    }
}