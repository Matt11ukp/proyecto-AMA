package unl.edu.ec.ama.engine.view.render;

import java.awt.*;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class LoadingScreenDrawer {

    public void draw(Graphics2D g2, int screenWidth, int screenHeight, int progress) {
        drawBackground(g2, screenWidth, screenHeight);
        drawTitle(g2, screenWidth);
        drawBar(g2, screenWidth, screenHeight, progress);
        drawText(g2, screenWidth, screenHeight, progress);
    }

    private void drawBackground(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, screenWidth, screenHeight);
    }

    private void drawTitle(Graphics2D g2, int screenWidth) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));

        String text = "AMA";
        int x = getCenteredX(g2, text, screenWidth);
        int y = 180;

        drawTextWithShadow(g2, text, x, y, Color.PINK);
    }

    private void drawBar(Graphics2D g2, int screenWidth, int screenHeight, int progress) {
        int width = 500;
        int height = 30;
        int x = (screenWidth - width) / 2;
        int y = screenHeight / 2;

        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);

        g2.setColor(Color.PINK);
        int fillWidth = (width * progress) / 100;
        g2.fillRect(x + 2, y + 2, fillWidth - 4, height - 4);
    }

    private void drawText(Graphics2D g2, int screenWidth, int screenHeight, int progress) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28F));

        String text = "CARGANDO PERFILES... " + progress + "%";
        int x = getCenteredX(g2, text, screenWidth);
        int y = screenHeight / 2 + 80;

        drawTextWithShadow(g2, text, x, y, Color.PINK);
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