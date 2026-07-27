package unl.edu.ec.ama.engine.view.render;


import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Lighting {
    private BufferedImage darknessLayer;
    public void drawPlayerLighting(Graphics2D g2, GamePanel gp) {

        // 1. Reciclamos la capa para evitar lag
        if (darknessLayer == null) {
            darknessLayer = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D gDark = (Graphics2D) darknessLayer.getGraphics();
        gDark.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 2. Limpiamos el frame anterior
        gDark.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        gDark.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 3. EL FILTRO NOCTURNO MÁGICO
        // Usamos un azul profundo con un toque de violeta y una opacidad de 140
        gDark.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        gDark.setColor(new Color(25, 20, 70, 140));
        gDark.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        gDark.dispose();

        // 4. Dibujamos el filtro sobre el mundo entero
        g2.drawImage(darknessLayer, 0, 0, null);
    }
}
