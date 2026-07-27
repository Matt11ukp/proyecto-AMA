package unl.edu.ec.ama.engine.view.render;

import unl.edu.ec.ama.engine.view.core.GameState;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Ui {
    Avatar skins;
    GamePanel gp;
    Graphics2D g2;
    Font pixel;
    private LoadingScreenDrawer loadingScreenDrawer = new LoadingScreenDrawer();
    private UserRegistrationDrawer userRegistrationDrawer;
    private UserSelectionDrawer userSelectionDrawer;
    private VisualDrawer visualDrawer;

    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public String currentDialogue;
    public int commandNumber = 0;
    public int commandNumber1 = 0;

    public Ui(GamePanel gp){
        this.gp = gp;
        userSelectionDrawer = new UserSelectionDrawer();
        userRegistrationDrawer = new UserRegistrationDrawer();
        visualDrawer = new VisualDrawer(gp);
        // lo hacemos antes del draw para optimizar
        InputStream is = getClass().getResourceAsStream("/fonts/LowresPixel-Regular.otf");
        try{
            pixel = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch(FontFormatException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }

    }
    public void showMessage(String text){
        message = text;
        messageOn = true;
    }
    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(pixel);
        g2.setColor(Color.white);
        //Menu
        if(gp.gameState == GameState.LOADING){
            loadingScreenDrawer.draw(g2, gp.screenWidth, gp.screenHeight, gp.getLoadingProgress());
            return;
        }
        if(gp.gameState == GameState.VISUAL){
            visualDrawer.draw(g2, gp, gp.getVisualTest());
            return;
        }
        if(gp.gameState == GameState.USER_SELECTION){
            userSelectionDrawer.draw(g2, gp.getUserSelectionMenu(), gp.screenWidth, gp.screenHeight);
            return;
        }
        if(gp.gameState == GameState.REGISTER){
            userRegistrationDrawer.draw(g2, gp.getUserRegistrationForm(), gp.screenWidth, gp.screenHeight);
            return;
        }
        if(gp.gameState == GameState.TITLE){
            drawTitleScreen();
        }

        if(gp.gameState == GameState.PAUSE){
            drawPauseScreen();
        }
        if(gp.gameState == GameState.DIALOGUE){
            drawDialogueScreen();
        }
        if(gp.gameState == GameState.SKIN_SELECTION){
            skinCustomization();
        }
        if(gp.gameState == GameState.SKIN_HAIR_SELECTION){
            hairSelection();
        }
        if(gp.gameState == GameState.SKIN_SHIRT_SELECTION){
            shirtSelection();
        }
        if(gp.gameState == GameState.SKIN_EYES_SELECTION){
            eyesSelection();
        }
    }

    public void drawTitleScreen(){
        // Cambiar el color del fondo
        g2.setColor(new Color(255, 255, 255));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        //nombre del titulo
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 128F));
        String text = "AMA";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;
        // Sombra de las letras
        g2.setColor(Color.black);
        g2.drawString(text, x + 5, y + 5);
        // las letras del titulo
        g2.setColor(Color.pink);
        g2.drawString(text, x, y);
        // Imagen
        x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2;
        y += gp.tileSize * 2;
        g2.drawImage(gp.skins.image.getSkins()[gp.skins.getActualSkin()],  x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        g2.drawImage(gp.skins.image.getShirts()[gp.skins.getActualShirt()],  x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        if(!gp.skins.isGender()){
            g2.drawImage(gp.skins.image.getHairsMale()[gp.skins.getActualHair()],  x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        } else{
            g2.drawImage(gp.skins.image.getHairsFemale()[gp.skins.getActualHair()],  x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        }
        g2.drawImage(gp.skins.image.getEyes()[gp.skins.getActualEye()],  x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        // El menu
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "NUEVO JUEGO";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if(commandNumber == 0){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "PERSONALIZAR AVATAR";
        x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNumber == 1){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "CAMBIAR PERFIL";
        x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNumber == 2){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "SALIR";
        x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNumber == 3){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
    }
    public void drawDialogueScreen() {
        //ventana de dialogo
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 4;
        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32));
        x += gp.tileSize;
        y += gp.tileSize;
        for(String line : currentDialogue.split("\n")){
            g2.drawString(line, x, y);
            y += 40;
        }

    }
    public void drawSubWindow(int x, int y, int width, int height){
        // El cuarto numero indica transparencia
        Color c = new Color(0, 0, 0, 210); // rgb en negro
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height,35, 35);
        c = new Color(255, 255, 255, 210); // blanco
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }
    public void drawPauseScreen(){
        // Cambiar el color del fondo
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        //nombre del titulo
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 128F));
        String text = "!!PAUSADO!!";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;
        // Sombra de las letras
        g2.setColor(Color.black);
        g2.drawString(text, x + 5, y + 5);
        // las letras del titulo
        g2.setColor(Color.white);
        g2.drawString(text, x, y);
        // El menu
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "VOLVER AL JUEGO";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if(commandNumber == 0){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "MENU PRINCIPAL";
        x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNumber == 1){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "SALIR";
        x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNumber == 2){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
    }
    // metodo para obtener la X centrada
    public int getXforCenteredText(String text){
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }
    public void winScreen(){
        // Cambiar el color del fondo
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        //nombre del titulo
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 128F));
        String text = "!!HAS GANADO!!";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;
        // Sombra de las letras
        g2.setColor(Color.black);
        g2.drawString(text, x + 5, y + 5);
        // las letras del titulo
        g2.setColor(Color.white);
        g2.drawString(text, x, y);
        // El menu
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "INTENTAR OTRA VEZ";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if(commandNumber == 0){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "MENU PRINCIPAL";
        x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNumber == 1){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "SALIR";
        x = getXforCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNumber == 2){
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            g2.drawString(">", x - gp.tileSize, y - 5);
        }

    }
    public void skinCustomization(){
        // Cambiar el color del fondo

        g2.setColor(new Color(255, 255, 255));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);


        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));

        String text = "PERSONALIZA TU PERSONAJE";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 3;
        g2.setColor(Color.pink);
        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
        text = "PELO";
        g2.drawString(text, gp.tileSize * 5, gp.tileSize * 7);
        text = "CAMISA";
        g2.drawString(text, gp.tileSize * 5, gp.tileSize * 9);
        text = "OJOS";
        g2.drawString(text, gp.tileSize * 5, gp.tileSize * 11);
        text = "COLOR";
        g2.drawString(text, gp.tileSize * 23, gp.tileSize * 5);
        text = "VOLVER";
        g2.drawString(text, gp.tileSize * 14, gp.tileSize * 15);
        if(commandNumber1 != 1){
            if(commandNumber == 0){
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
                g2.drawString(">", gp.tileSize * 4, gp.tileSize * 7);
            }
            if(commandNumber == 1){
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
                g2.drawString(">", gp.tileSize * 4, gp.tileSize * 9);
            }
            if(commandNumber == 2){
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
                g2.drawString(">", gp.tileSize * 4, gp.tileSize * 11);
            }
            if(commandNumber == 3){
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
                g2.drawString(">", gp.tileSize * 12, gp.tileSize * 13);
            }
            if(commandNumber == 4){
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
                g2.drawString("<", gp.tileSize * 18, gp.tileSize * 13);
            }
            if(commandNumber == 5){
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
                g2.drawString(">", gp.tileSize * 13, gp.tileSize * 15);
            }
        }
        g2.drawImage(gp.skins.image.getSkins()[gp.skins.getActualSkin()], gp.tileSize * 12, gp.tileSize * 4, gp.tileSize * 7, gp.tileSize * 7, null);
        g2.drawImage(gp.skins.image.getShirts()[gp.skins.getActualShirt()], gp.tileSize * 12, gp.tileSize * 4, gp.tileSize * 7, gp.tileSize * 7, null);
        if(!gp.skins.isGender()){
            g2.drawImage(gp.skins.image.getHairsMale()[gp.skins.getActualHair()], gp.tileSize * 12, gp.tileSize * 4, gp.tileSize * 7, gp.tileSize * 7, null);
        } else{
            g2.drawImage(gp.skins.image.getHairsFemale()[gp.skins.getActualHair()], gp.tileSize * 12, gp.tileSize * 4, gp.tileSize * 7, gp.tileSize * 7, null);
        }
        g2.drawImage(gp.skins.image.getEyes()[gp.skins.getActualEye()], gp.tileSize * 12, gp.tileSize * 4, gp.tileSize * 7, gp.tileSize * 7, null);
        g2.drawImage(gp.skins.image.getFemale(), gp.tileSize * 13, gp.tileSize * 12, gp.tileSize * 2, gp.tileSize * 2, null);
        g2.drawImage(gp.skins.image.getMale(), gp.tileSize * 16, gp.tileSize * 12, gp.tileSize * 2, gp.tileSize * 2, null);

        gp.skins.skinsMenu(g2);
        if(commandNumber1 == 1){
            gp.skins.selectDrawSkins(g2, gp.skins.getSkinI());
        }

    }
    public void hairSelection(){
        drawAvatarCarousel("Cabello", gp.skins.getActualHair(), "hair");
    }

    public void shirtSelection(){
        drawAvatarCarousel("Camisa", gp.skins.getActualShirt(), "shirt");
    }

    public void eyesSelection(){
        drawAvatarCarousel("Ojos", gp.skins.getActualEye(), "eyes");
    }

    public void drawAvatarCarousel(String title, int currentIndex, String type) {
        g2.setColor(new Color(255, 255, 255));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int total = 0;

        if(type.equals("hair")) {
            if(!gp.skins.isGender()) {
                total = gp.skins.image.getHairsMale().length;
            } else {
                total = gp.skins.image.getHairsFemale().length;
            }
        }

        if(type.equals("shirt")) {
            total = gp.skins.image.getShirts().length;
        }

        if(type.equals("eyes")) {
            total = gp.skins.image.getEyes().length;
        }

        if(total <= 0) {
            return;
        }

        if(currentIndex < 0) {
            currentIndex = total - 1;
        }

        if(currentIndex >= total) {
            currentIndex = 0;
        }

        int previousIndex = currentIndex - 1;
        int nextIndex = currentIndex + 1;

        if(previousIndex < 0) {
            previousIndex = total - 1;
        }

        if(nextIndex >= total) {
            nextIndex = 0;
        }

        int centerX = gp.screenWidth / 2;
        int centerY = gp.screenHeight / 2;

        int smallSize = gp.tileSize * 3;
        int bigSize = gp.tileSize * 5;

        // Solo título y flechas, sin instrucciones extra
        g2.setColor(Color.pink);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60F));
        g2.drawString(title, centerX - gp.tileSize * 2, gp.tileSize * 2);

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 90F));
        g2.drawString("<", centerX - gp.tileSize * 6, centerY + 20);
        g2.drawString(">", centerX + gp.tileSize * 5, centerY + 20);

        drawAvatarOption(type, previousIndex, centerX - gp.tileSize * 5, centerY - smallSize / 2, smallSize);
        drawAvatarOption(type, currentIndex, centerX - bigSize / 2, centerY - bigSize / 2, bigSize);
        drawAvatarOption(type, nextIndex, centerX + gp.tileSize * 2, centerY - smallSize / 2, smallSize);
    }

    public void drawAvatarOption(String type, int optionIndex, int x, int y, int size) {
        int skinIndex = gp.skins.getActualSkin();
        int shirtIndex = gp.skins.getActualShirt();
        int eyeIndex = gp.skins.getActualEye();
        int hairIndex = gp.skins.getActualHair();

        if(type.equals("hair")) {
            hairIndex = optionIndex;
        }

        if(type.equals("shirt")) {
            shirtIndex = optionIndex;
        }

        if(type.equals("eyes")) {
            eyeIndex = optionIndex;
        }

        g2.drawImage(gp.skins.image.getSkins()[skinIndex], x, y, size, size, null);
        g2.drawImage(gp.skins.image.getShirts()[shirtIndex], x, y, size, size, null);

        if(!gp.skins.isGender()) {
            g2.drawImage(gp.skins.image.getHairsMale()[hairIndex], x, y, size, size, null);
        } else {
            g2.drawImage(gp.skins.image.getHairsFemale()[hairIndex], x, y, size, size, null);
        }

        g2.drawImage(gp.skins.image.getEyes()[eyeIndex], x, y, size, size, null);
    }
}
