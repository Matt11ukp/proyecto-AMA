package unl.edu.ec.ama.engine.domain.entity;

import unl.edu.ec.ama.engine.domain.entity.objects.Type;
import unl.edu.ec.ama.engine.view.core.GameState;
import unl.edu.ec.ama.engine.view.core.Key;
import unl.edu.ec.ama.engine.view.render.GamePanel;
import unl.edu.ec.ama.engine.view.sound.SoundName;

import java.awt.*;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Player extends Entity {

    private final Key keyH;

    private boolean dash = false;
    private int dashCounter = 0;
    private int cooldown = 200;

    private int winCounter  = 0;
    private int lostCounter = 0;

    private final int screenX;
    private final int screenY;

    public boolean movementLocked = false;

    public Player(GamePanel gp, Key keyH) {
        super(gp);
        this.keyH = keyH;

        screenX = gp.screenWidth  / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea         = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        attackArea.width  = 36;
        attackArea.height = 36;

        setDefaultValues();

    }

    public void setDefaultValues() {
        dash     = false;
        setWorldX(gp.tileSize * 15);
        setWorldY(gp.tileSize * 8);
        setSpeed(4);
        setDirection("up");
        setType(EntityType.PLAYER);
    }

    public void getPlayerImage() {
        down  = gp.skins.frame(
                    gp.skins.image.getSkins(),
                    gp.skins.image.getShirts(),
                    gp.skins.image.getEyes(),
                    gp.skins.image.getHairsMale(),
                    gp.skins.image.getHairsFemale());
        down1  = gp.skins.frame(
                gp.skins.image.getSkinDown1(),
                gp.skins.image.getShirtDown1(),
                gp.skins.image.getEyeDown1(),
                gp.skins.image.getMaleDown1(),
                null);
        down2  = gp.skins.frame(
                gp.skins.image.getSkinDown2(),
                gp.skins.image.getShirtDown2(),
                gp.skins.image.getEyeDown2(),
                gp.skins.image.getMaleDown2(),
                null);
        left1  = gp.skins.frame(
                gp.skins.image.getSkinLeft1(),
                gp.skins.image.getShirtLeft1(),
                gp.skins.image.getEyeLeft1(),
                gp.skins.image.getMaleLeft1(),
                null);
        left2  = gp.skins.frame(
                gp.skins.image.getSkinLeft2(),
                gp.skins.image.getShirtLeft2(),
                gp.skins.image.getEyeLeft2(),
                gp.skins.image.getMaleLeft2(),
                null);
        right1 = gp.skins.frame(
                gp.skins.image.getSkinRight1(),
                gp.skins.image.getShirtRight1(),
                gp.skins.image.getEyeRight1(),
                gp.skins.image.getMaleRight1(),
                null);
        right2 = gp.skins.frame(
                gp.skins.image.getSkinRight2(),
                gp.skins.image.getShirtRight2(),
                gp.skins.image.getEyeRight2(),
                gp.skins.image.getMaleRight2(),
                null);
        up = gp.skins.frame(gp.skins.image.getSkinUp(),
                     gp.skins.image.getShirtUp(),
                     gp.skins.image.getEyeUp(),
                     gp.skins.image.getMaleUp(), null);
        up1 = gp.skins.frame(gp.skins.image.getSkinUp1(),
                gp.skins.image.getShirtUp1(),
                gp.skins.image.getEyeUp1(),
                gp.skins.image.getMaleUp1(), null);
        up2 = gp.skins.frame(gp.skins.image.getSkinUp2(),
                gp.skins.image.getShirtUp2(),
                gp.skins.image.getEyeUp2(),
                gp.skins.image.getMaleUp2(), null);
    }

    @Override
    public void update() {
        updateSpeed();
        updateDash();
        handleMovementInput();
    }

    private void updateSpeed() {
        setSpeed((keyH.shiftPressed) ? 6 : 4);
    }

    private void updateDash() {
        if (cooldown < 200) cooldown++;

        if (keyH.spacePressed && !dash && cooldown > 180) {
            gp.playSE(SoundName.DASH);
            dash        = true;
            dashCounter = 0;
            cooldown    = 0;
        }

        if (dash) {
            setSpeed(20);
            dashCounter++;
            if (dashCounter > 5) {
                dash = false;
                dashCounter = 0;
            }
        }
    }

    private void handleMovementInput() {
        // CLÁUSULA DE GUARDA: Si el movimiento está bloqueado por un test o evento, salimos inmediatamente
        if (movementLocked) {
            return;
        }

        boolean anyKey = keyH.upPressed || keyH.downPressed
                || keyH.leftPressed || keyH.rightPressed
                || keyH.enterPressed;

        if (!anyKey) return;

        if (!keyH.upPressed && !keyH.downPressed
                && !keyH.leftPressed && !keyH.rightPressed
                && keyH.enterPressed) {
            int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
            interactNPC(npcIndex);
        }

        if (keyH.upPressed)    moveInDirection("up");
        if (keyH.downPressed)  moveInDirection("down");
        if (keyH.leftPressed)  moveInDirection("left");
        if (keyH.rightPressed) moveInDirection("right");

        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum     = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private void moveInDirection(String dir) {
        setDirection(dir);
        colissionOn = false;

        // ── DESACTIVAR COLISIONES DEL MAPA DURANTE LA PRUEBA VERBAL ──
        // Al estar en la arena cibernética, ignoramos los árboles, el agua,
        // los ítems invisibles del suelo y los NPCs para movernos libremente.
        if (gp.gameState != GameState.VERBAL) {
            gp.cChecker.checkTile(this);

            int objIndex     = gp.cChecker.checkObject(this, true);
            int npcIndex     = gp.cChecker.checkEntity(this, gp.npc);
            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);

            pickUpObject(objIndex);
            interactNPC(npcIndex);
        }
        // ─────────────────────────────────────────────────────────────

        if (!colissionOn && !keyH.enterPressed) {
            switch (dir) {
                case "up"    -> worldY -= speed;
                case "down"  -> worldY += speed;
                case "left"  -> worldX -= speed;
                case "right" -> worldX += speed;
            }
        }
        gp.keyH.enterPressed = false;
    }

    private void performAttack() {
        spriteCounter++;
        if (spriteCounter <= 5) {
            spriteNum = 1;
        }
        if (spriteCounter > 5 && spriteCounter <= 25) {
            spriteNum = 2;

            int savedX = worldX, savedY = worldY;
            int savedW = solidArea.width, savedH = solidArea.height;

            switch (getDirection()) {
                case "up"    -> worldY -= attackArea.height;
                case "down"  -> worldY += attackArea.height;
                case "left"  -> worldX -= attackArea.width;
                case "right" -> worldX += attackArea.width;
            }
            solidArea.width  = attackArea.width;
            solidArea.height = attackArea.height;

            int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);

            // Restaurar hitbox
            worldX = savedX; worldY = savedY;
            solidArea.width = savedW; solidArea.height = savedH;
        }
        if (spriteCounter > 25) {
            spriteNum = 1;
            spriteCounter = 0;
        }
    }

    public void interactNPC(int i) {
        if (keyH.enterPressed) {
            if (i != 999) {
                gp.gameState = GameState.DIALOGUE;
                gp.npc[i].speak();
            } else {
                gp.playSE(SoundName.SWING);
            }
        }
        gp.keyH.enterPressed = false;
    }

    public void pickUpObject(int i) {
        if (i == 999) return;
        Type type = gp.obj[i].getType();
    }

    public boolean isMoving() {
        return keyH.upPressed || keyH.downPressed
            || keyH.leftPressed || keyH.rightPressed;
    }

    public int getScreenX() { return screenX; }
    public int getScreenY() { return screenY; }
    public int getWinCounter() { return winCounter; }
    public void setWinCounter(int v) { this.winCounter = v; }
    public void incrementWinCounter() { this.winCounter++; }
    public int getLostCounter() { return lostCounter; }
    public void setLostCounter(int v) { this.lostCounter = v; }
    public void incrementLostCounter() { this.lostCounter++; }
}
