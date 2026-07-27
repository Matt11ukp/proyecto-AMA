package unl.edu.ec.ama.engine.view.render;



import unl.edu.ec.ama.engine.domain.user.IAvatarConfig;
import unl.edu.ec.ama.engine.view.util.ImageGetter;
import unl.edu.ec.ama.engine.view.util.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Avatar implements IAvatarConfig {

    GamePanel gp;
    public ImageGetter image;

    private int hairI = 0;
    private int actualHair = 0;

    private int shirtI = 0;
    private int actualShirt = 0;

    private int skinI = 0;
    private int actualSkin = 0;

    private int eyesI = 0;
    private int actualEye = 0;

    private boolean gender = false;

    public Avatar(GamePanel gp) {
        this.gp = gp;
        image = new ImageGetter(gp.tileSize);
    }

    // Hace que el carrusel dé la vuelta.
    private int fixIndex(int index, int length) {
        if (length <= 0) {
            return 0;
        }

        if (index < 0) {
            return length - 1;
        }

        if (index >= length) {
            return 0;
        }

        return index;
    }
    // Dibuja el avaatar en el carrusel
    private void drawAvatarPreview(
            Graphics2D g2,
            int x,
            int y,
            int size,
            int skinIndex,
            int shirtIndex,
            int eyeIndex,
            int hairIndex
    ) {
        skinIndex = fixIndex(skinIndex, image.getSkins().length);
        shirtIndex = fixIndex(shirtIndex, image.getShirts().length);
        eyeIndex = fixIndex(eyeIndex, image.getEyes().length);

        BufferedImage skin = image.getSkins()[skinIndex];
        BufferedImage shirt = image.getShirts()[shirtIndex];
        BufferedImage eye = image.getEyes()[eyeIndex];

        BufferedImage hair;

        if (!gender) {
            hairIndex = fixIndex(hairIndex, image.getHairsMale().length);
            hair = image.getHairsMale()[hairIndex];
        } else {
            hairIndex = fixIndex(hairIndex, image.getHairsFemale().length);
            hair = image.getHairsFemale()[hairIndex];
        }

        g2.drawImage(skin, x, y, size, size, null);
        g2.drawImage(shirt, x, y, size, size, null);
        g2.drawImage(eye, x, y, size, size, null);
        g2.drawImage(hair, x, y, size, size, null);
    }
    // Carrusel para camisa, ojos, cabello
    private void drawAvatarCarousel(Graphics2D g2, String title, int currentIndex, int total, String type) {
        if (total <= 0) {
            g2.setColor(Color.RED);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40F));
            g2.drawString("No hay imágenes para mostrar", gp.tileSize * 4, gp.tileSize * 5);
            return;
        }
        currentIndex = fixIndex(currentIndex, total);

        int previousIndex = fixIndex(currentIndex - 1, total);
        int nextIndex = fixIndex(currentIndex + 1, total);

        int centerX = gp.screenWidth / 2;
        int centerY = gp.screenHeight / 2;

        int smallSize = gp.tileSize * 3;
        int bigSize = gp.tileSize * 5;

        g2.setColor(Color.pink);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 52F));
        g2.drawString(title, centerX - gp.tileSize * 2, gp.tileSize * 2);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));
        g2.drawString("<", centerX - gp.tileSize * 6, centerY + 20);
        g2.drawString(">", centerX + gp.tileSize * 5, centerY + 20);

        int previousHair = actualHair;
        int currentHair = actualHair;
        int nextHair = actualHair;

        int previousShirt = actualShirt;
        int currentShirt = actualShirt;
        int nextShirt = actualShirt;

        int previousEye = actualEye;
        int currentEye = actualEye;
        int nextEye = actualEye;

        if (type.equals("hair")) {
            previousHair = previousIndex;
            currentHair = currentIndex;
            nextHair = nextIndex;
        }

        if (type.equals("shirt")) {
            previousShirt = previousIndex;
            currentShirt = currentIndex;
            nextShirt = nextIndex;
        }

        if (type.equals("eyes")) {
            previousEye = previousIndex;
            currentEye = currentIndex;
            nextEye = nextIndex;
        }

        // Avatar anterior
        drawAvatarPreview(
                g2,
                centerX - gp.tileSize * 5,
                centerY - smallSize / 2,
                smallSize,
                actualSkin,
                previousShirt,
                previousEye,
                previousHair
        );
        // Avatar actual grande
        drawAvatarPreview(
                g2,
                centerX - bigSize / 2,
                centerY - bigSize / 2,
                bigSize,
                actualSkin,
                currentShirt,
                currentEye,
                currentHair
        );

        // Avatar siguiente
        drawAvatarPreview(
                g2,
                centerX + gp.tileSize * 2,
                centerY - smallSize / 2,
                smallSize,
                actualSkin,
                nextShirt,
                nextEye,
                nextHair
        );

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        String counter = (currentIndex + 1) + " / " + total;
        g2.drawString(counter, centerX - 35, centerY + gp.tileSize * 4);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 30F));
        g2.drawString("A / <-  Anterior     D / ->  Siguiente", centerX - gp.tileSize * 5, gp.tileSize * 13);
        g2.drawString("ENTER PARA ELEGIR", centerX - gp.tileSize * 3, gp.tileSize * 15);
    }

    // Menus de cada carrusel

    public void hairMenuMale(Graphics2D g2) {
        drawAvatarCarousel(g2, "Cabello", actualHair, image.getHairsMale().length, "hair");
    }

    public void hairMenuFemale(Graphics2D g2) {
        drawAvatarCarousel(g2, "Cabello", actualHair, image.getHairsFemale().length, "hair");
    }

    public void shirtMenu(Graphics2D g2) {
        drawAvatarCarousel(g2, "Camisa", actualShirt, image.getShirts().length, "shirt");
    }

    public void eyesMenu(Graphics2D g2) {
        drawAvatarCarousel(g2, "Ojos", actualEye, image.getEyes().length, "eyes");
    }

    public void skinsMenu(Graphics2D g2) {
        int p = 0;
        int multiplierY = 5;

        for (int i = 0; i < image.getSkinBlocks().length; i++) {
            if (p == 4) {
                multiplierY = multiplierY + 2;
                p = 0;
            }

            int multiplierX = 21 + (p * 2);

            g2.drawImage(
                    image.getSkinBlocks()[i],
                    gp.tileSize * multiplierX - 25,
                    gp.tileSize * multiplierY,
                    gp.tileSize * 2,
                    gp.tileSize * 2,
                    null
            );

            p++;
        }
    }

    public void selectDrawSkins(Graphics2D g2, int skinsI) {
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60F));

        int multiplierY = 6;
        int posicionX = skinI;

        while (posicionX >= 4) {
            multiplierY = multiplierY + 2;
            posicionX -= 4;
        }

        int multiplier = 21 + (posicionX * 2);
        g2.drawString(">", gp.tileSize * multiplier -25, gp.tileSize * multiplierY);
    }

    public BufferedImage setup(String imageName, int width, int height) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imageName + ".png"));
            image = uTool.scaleImage(image, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public BufferedImage avatar(
            BufferedImage actualSkin,
            BufferedImage actualShirt,
            BufferedImage actualEye,
            BufferedImage actualHair,
            int width,
            int height
    ) {
        BufferedImage avatar = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D pincel = avatar.createGraphics();

        pincel.drawImage(actualSkin, 0, 0, width, height, null);
        pincel.drawImage(actualShirt, 0, 0, width, height, null);
        pincel.drawImage(actualEye, 0, 0, width, height, null);
        pincel.drawImage(actualHair, 0, 0, width, height, null);

        pincel.dispose();
        return avatar;
    }

    // DEVOLVER AVATAR ACTUAL
    public BufferedImage frame(
            BufferedImage[] skins,
            BufferedImage[] shirts,
            BufferedImage[] eyes,
            BufferedImage[] hairsMale,
            BufferedImage[] hairsFemale
    ) {
        BufferedImage actualHairImage;

        actualSkin = fixIndex(actualSkin, skins.length);
        actualShirt = fixIndex(actualShirt, shirts.length);
        actualEye = fixIndex(actualEye, eyes.length);

        if (!gender) {
            actualHair = fixIndex(actualHair, hairsMale.length);
            actualHairImage = hairsMale[actualHair];
        } else {
            actualHair = fixIndex(actualHair, hairsFemale.length);
            actualHairImage = hairsFemale[actualHair];
        }

        BufferedImage actualShirtImage = shirts[actualShirt];
        BufferedImage actualEyeImage = eyes[actualEye];
        BufferedImage actualSkinImage = skins[actualSkin];

        return avatar(
                actualSkinImage,
                actualShirtImage,
                actualEyeImage,
                actualHairImage,
                gp.tileSize,
                gp.tileSize
        );
    }

    // =========================================================================
    // IAvatarConfig — getters y setters (Regla de Oro #6 + DIP Paso 8 pattern)
    // =========================================================================
    @Override public int  getActualSkin()      { return actualSkin; }
    @Override public void setActualSkin(int v) { this.actualSkin = v; }
    @Override public int  getActualHair()      { return actualHair; }
    @Override public void setActualHair(int v) { this.actualHair = v; }
    @Override public int  getActualShirt()     { return actualShirt; }
    @Override public void setActualShirt(int v){ this.actualShirt = v; }
    @Override public int  getActualEye()       { return actualEye; }
    @Override public void setActualEye(int v)  { this.actualEye = v; }
    @Override public boolean isGender()        { return gender; }
    @Override public void setGender(boolean v) { this.gender = v; }

    // Índices de selección UI (usados por Key.java)
    public int getSkinI()    { return skinI; }
    public void setSkinI(int v) { this.skinI = v; }
    public int getHairI()    { return hairI; }
    public void setHairI(int v) { this.hairI = v; }
    public int getShirtI()   { return shirtI; }
    public void setShirtI(int v){ this.shirtI = v; }
    public int getEyesI()    { return eyesI; }
    public void setEyesI(int v) { this.eyesI = v; }
}
