package unl.edu.ec.ama.engine.view.render;

import unl.edu.ec.ama.data.entity.User;
import unl.edu.ec.ama.engine.domain.user.UserSelectionMenu;

import java.awt.*;
import java.util.List;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UserSelectionDrawer {

    public void draw(Graphics2D g2, UserSelectionMenu menu, int screenWidth, int screenHeight) {
        drawBackground(g2, screenWidth, screenHeight);
        drawTitle(g2, screenWidth);
        drawProfiles(g2, menu, screenWidth, screenHeight);
    }

private void drawBackground(Graphics2D g2, int screenWidth, int screenHeight) {
    g2.setColor(Color.WHITE);
    g2.fillRect(0, 0, screenWidth, screenHeight);
}

private void drawTitle(Graphics2D g2, int screenWidth) {
    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 64F));

    String title = "SELECCIONAR USUARIO";
    int x = getCenteredX(g2, title, screenWidth);
    int y = 110;

    g2.setColor(Color.BLACK);
    g2.drawString(title, x + 4, y + 4);

    g2.setColor(Color.PINK);
    g2.drawString(title, x, y);
}

private void drawProfiles(Graphics2D g2, UserSelectionMenu menu, int screenWidth, int screenHeight) {
    int diameter = 100;
    int spacing = 40;
    int y = screenHeight / 2 - 60;

    List<User> users = menu.getUsers();

    int totalProfiles = users.size() + 1; // +1 por el boton Nuevo
    int totalWidth = (totalProfiles * diameter) + ((totalProfiles - 1) * spacing);
    int startX = (screenWidth - totalWidth) / 2;

    drawNewUserSlot(g2, startX, y, diameter, menu.getSelectedIndex() == 0);

    for (int i = 0; i < users.size(); i++) {
        int x = startX + (i + 1) * (diameter + spacing);
        boolean selected = menu.getSelectedIndex() == i + 1;
        drawUserSlot(g2, users.get(i), x, y, diameter, selected);
    }
}

    private void drawNewUserSlot(Graphics2D g2, int x, int y, int diameter, boolean selected) {
        drawCircle(g2, x, y, diameter, selected);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.drawString("+", x + 37, y + 62);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.drawString("Nuevo", x + 18, y + diameter + 25);
    }

private void drawUserSlot(Graphics2D g2, User user, int x, int y, int diameter, boolean selected) {
    drawCircle(g2, x, y, diameter, selected);

    String initials = getInitials(user.getName());

    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Arial", Font.BOLD, 28));

    int initialsWidth = g2.getFontMetrics().stringWidth(initials);
    int initialsHeight = g2.getFontMetrics().getAscent();

    int initialsX = x + (diameter - initialsWidth) / 2;
    int initialsY = y + (diameter + initialsHeight) / 2 - 6;

    g2.drawString(initials, initialsX, initialsY);

    if (selected) {
    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));

    String name = user.getName();
    int nameWidth = g2.getFontMetrics().stringWidth(name);
    int nameX = x + (diameter - nameWidth) / 2;

    g2.setColor(Color.BLACK);
    g2.drawString(name, nameX + 3, y + diameter + 35);

    g2.setColor(Color.PINK);
    g2.drawString(name, nameX, y + diameter + 32);
}
}

private void drawCircle(Graphics2D g2, int x, int y, int diameter, boolean selected) {
    g2.setColor(new Color(255, 182, 193));
    g2.fillOval(x, y, diameter, diameter);

    if (selected) {
        g2.setColor(Color.BLACK);
        g2.drawOval(x - 3, y - 3, diameter + 6, diameter + 6);

        g2.setColor(Color.PINK);
        g2.drawOval(x - 6, y - 6, diameter + 12, diameter + 12);
    } else {
        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, diameter, diameter);
    }
}
    

    private int getCenteredX(Graphics2D g2, String text, int screenWidth) {
        int textWidth = g2.getFontMetrics().stringWidth(text);
        return (screenWidth - textWidth) / 2;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}