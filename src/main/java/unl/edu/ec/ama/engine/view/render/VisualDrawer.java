package unl.edu.ec.ama.engine.view.render;

import unl.edu.ec.ama.engine.domain.entity.Visual;
import unl.edu.ec.ama.engine.view.util.ImageGetter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class VisualDrawer {

    private BufferedImage mushroomImage;
    private BufferedImage gemImage;
    private BufferedImage flashlightImage;

    private static final int MUSHROOM_SIZE   = 96;
    private static final int GEM_SIZE        = 20;
    private static final int FLASHLIGHT_SIZE = 80;

    public VisualDrawer(GamePanel gp) {
        loadImages(gp);
    }

    private void loadImages(GamePanel gp) {
        ImageGetter imageGetter = new ImageGetter(gp.tileSize);
        mushroomImage  = imageGetter.setup("/tiles/hongo",    MUSHROOM_SIZE,   MUSHROOM_SIZE);
        gemImage       = imageGetter.setup("/tiles/gema",     GEM_SIZE,        GEM_SIZE);
        flashlightImage= imageGetter.setup("/tiles/linterna", FLASHLIGHT_SIZE, FLASHLIGHT_SIZE);
    }

    public void draw(Graphics2D g2, GamePanel gp, Visual visual) {
        drawBackground(g2, gp);

        if (visual.isIntroActive()) {
            drawIntroMessage(g2, gp);
            return;
        }

        visual.configureForest(gp.screenWidth, gp.screenHeight, MUSHROOM_SIZE);

        int initX = (gp.screenWidth  - FLASHLIGHT_SIZE) / 2;
        int initY =  gp.screenHeight - FLASHLIGHT_SIZE  - gp.tileSize;
        visual.initializeFlashlight(initX, initY);

        drawForest(g2, visual);

        if (visual.isFlashlightOn()) {
            drawStardewLighting(g2, gp, visual);
        } else {
            drawFullDarkness(g2, gp);
        }

        drawFlashlight(g2, visual);
        drawCollectedGems(g2, visual);
        drawMessage(g2, gp, visual);
    }

    private void drawBackground(Graphics2D g2, GamePanel gp) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }

    private void drawIntroMessage(Graphics2D g2, GamePanel gp) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int panelW = gp.tileSize * 16, panelH = gp.tileSize * 8;
        int panelX = (gp.screenWidth  - panelW) / 2;
        int panelY = (gp.screenHeight - panelH) / 2;

        drawMenuStyleBox(g2, panelX, panelY, panelW, panelH);
        drawPixelTitle(g2, "AMA", gp, panelY + gp.tileSize * 2);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        drawCenteredPixelText(g2, "TEST VISUAL", gp, panelY + gp.tileSize * 4);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        drawCenteredPixelText(g2, "ENCUENTRA LAS GEMAS", gp, panelY + gp.tileSize * 5 + 10);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        drawCenteredPixelText(g2, "ESCONDIDAS ENTRE LOS HONGOS", gp, panelY + gp.tileSize * 6 + 10);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30F));
        drawCenteredPixelText(g2, "ENTER = Antorcha  |  ESC = salir", gp, panelY + gp.tileSize * 7 + 5);
    }

    private void drawMenuStyleBox(Graphics2D g2, int x, int y, int w, int h) {
        Color pink = new Color(255, 170, 180);
        g2.setColor(Color.BLACK); g2.fillRect(x + 8, y + 8, w, h);
        g2.setColor(Color.WHITE); g2.fillRect(x, y, w, h);
        g2.setColor(pink);
        g2.fillRect(x, y, w, 8); g2.fillRect(x, y + h - 8, w, 8);
        g2.fillRect(x, y, 8, h); g2.fillRect(x + w - 8, y, 8, h);
        g2.setColor(new Color(255, 210, 215));
        g2.drawRect(x + 18, y + 18, w - 36, h - 36);
    }

    private void drawPixelTitle(Graphics2D g2, String text, GamePanel gp, int y) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 70F));
        int x = (gp.screenWidth - g2.getFontMetrics().stringWidth(text)) / 2;
        g2.setColor(Color.BLACK); g2.drawString(text, x + 6, y + 6);
        g2.setColor(new Color(255, 170, 180)); g2.drawString(text, x, y);
    }

    private void drawCenteredPixelText(Graphics2D g2, String text, GamePanel gp, int y) {
        int x = (gp.screenWidth - g2.getFontMetrics().stringWidth(text)) / 2;
        g2.setColor(new Color(255, 170, 180)); g2.drawString(text, x, y);
    }

    private void drawForest(Graphics2D g2, Visual visual) {
        for (int i = 0; i < visual.getMushroomCount(); i++) {
            g2.drawImage(mushroomImage, visual.getMushroomX(i), visual.getMushroomY(i),
                         MUSHROOM_SIZE, MUSHROOM_SIZE, null);
            if (visual.isGemIndex(i)) {
                g2.drawImage(gemImage,
                    visual.getMushroomX(i) + 60,
                    visual.getMushroomY(i) + 62,
                    GEM_SIZE, GEM_SIZE, null);
            }
        }
    }

    private void drawStardewLighting(Graphics2D g2, GamePanel gp, Visual visual) {
        int srcX = visual.getFlashlightX() + 40; // Centro de la linterna (FLASHLIGHT_SIZE / 2)
        int srcY = visual.getFlashlightY() + 40;
        int lightRadius = 250; // Qué tan lejos llega la luz

        RadialGradientPaint warmTint = new RadialGradientPaint(srcX, srcY, lightRadius,
                new float[]{0.0f, 0.4f, 1.0f},
                new Color[]{
                        new Color(255, 180, 50, 150), // Naranja cálido fuerte en el centro
                        new Color(255, 200, 100, 60), // Transición amarillenta
                        new Color(255, 200, 100, 0)   // Transparente al final
                }
        );
        g2.setPaint(warmTint);
        g2.fillOval(srcX - lightRadius, srcY - lightRadius, lightRadius * 2, lightRadius * 2);

        BufferedImage darknessLayer = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gDark = (Graphics2D) darknessLayer.getGraphics();

        gDark.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        gDark.setColor(new Color(12, 16, 45, 235));
        gDark.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        gDark.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1f));

        RadialGradientPaint softEraser = new RadialGradientPaint(srcX, srcY, lightRadius,
                new float[]{0.0f, 0.6f, 1.0f},
                new Color[]{
                        new Color(0, 0, 0, 255),
                        new Color(0, 0, 0, 150),
                        new Color(0, 0, 0, 0)
                }
        );
        gDark.setPaint(softEraser);

        gDark.fillOval(srcX - lightRadius, srcY - lightRadius, lightRadius * 2, lightRadius * 2);
        gDark.dispose();

        g2.drawImage(darknessLayer, 0, 0, null);
    }

    private void drawFlashlight(Graphics2D g2, Visual visual) {
        g2.drawImage(flashlightImage, visual.getFlashlightX(), visual.getFlashlightY(),
                     FLASHLIGHT_SIZE, FLASHLIGHT_SIZE, null);
    }

    private void drawCollectedGems(Graphics2D g2, Visual visual) {
        final int boxX = 12, boxY = 12, boxW = 300, boxH = 48;
        g2.setColor(new Color(0, 0, 0, 180)); g2.fillRect(boxX, boxY, boxW, boxH);
        g2.setColor(new Color(255, 170, 180)); g2.drawRect(boxX, boxY, boxW, boxH);

        for (int i = 0; i < visual.getFoundGemCount(); i++) {
            g2.drawImage(gemImage, boxX + 14 + i * 24, boxY + 15, 18, 18, null);
        }
        String counter = visual.getFoundGemCount() + " / " + visual.getTotalGems();
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
        g2.setColor(Color.BLACK); g2.drawString(counter, boxX + 238, boxY + 31);
        g2.setColor(new Color(255, 170, 180)); g2.drawString(counter, boxX + 236, boxY + 29);
    }

    private void drawMessage(Graphics2D g2, GamePanel gp, Visual visual) {
        if (!visual.isTestCompleted()) return;
        String msg = "!!Encontraste todas las gemas!!";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 34F));
        int x = (gp.screenWidth - g2.getFontMetrics().stringWidth(msg)) / 2;
        int y = gp.screenHeight / 2;
        g2.setColor(Color.BLACK); g2.drawString(msg, x + 4, y + 4);
        g2.setColor(new Color(255, 170, 180)); g2.drawString(msg, x, y);
    }

    private void drawFullDarkness(Graphics2D g2, GamePanel gp) {
        g2.setColor(new Color(0, 0, 0, 245)); g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setColor(new Color(80, 95, 85, 18));  g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }
}
