package unl.edu.ec.ama.engine.view.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UtilityTool {
    public static BufferedImage scaleImage(BufferedImage original, int width, int height){
        // lienzo en blanco con el tamanio al que estiraremos
        BufferedImage scaledImage = new BufferedImage(width, height, 2);
        // sacamos el pincel para dibujar
        Graphics2D g2 = scaledImage.createGraphics();
        // pinta la imagen original escalandola
        g2.drawImage(original, 0, 0, width, height, null);
        // destruimos el pincel para ahorrar memoria
        g2.dispose();
        // returna la imagen escalada lista para usar
        return scaledImage;
    }
}
