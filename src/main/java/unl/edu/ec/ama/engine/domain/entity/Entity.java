package unl.edu.ec.ama.engine.domain.entity;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

import unl.edu.ec.ama.engine.view.render.GamePanel;
import unl.edu.ec.ama.engine.view.util.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Entity {
    protected GamePanel gp;
    protected int worldX, worldY;
    protected int speed;
    protected String direction;
    protected BufferedImage down, down1, down2;
    protected BufferedImage up, up1, up2;
    protected BufferedImage left1, left2;
    protected BufferedImage right1, right2;

    protected int spriteCounter = 0;
    protected int spriteNum     = 1;

    protected Rectangle solidArea = new Rectangle(0, 0, 32, 32);
    protected Rectangle attackArea = new Rectangle(0, 0, 0, 0);
    protected int solidAreaDefaultX;
    protected int solidAreaDefaultY;
    protected boolean colissionOn = false;

    private EntityType type;
    private String name;

    private boolean collision = false;

    private final String[] dialogues = new String[20];
    private int dialogueIndex = 0;

    protected int actionLockCounter = 0;

    protected Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void setAction() { }
    public void damageReaction() { }

    public void update() {
        setAction();
        colissionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkEntity(this, gp.npc);
        gp.cChecker.checkEntity(this, gp.monster);

        boolean contactPlayer = gp.cChecker.checkPlayer(this);

        if (!colissionOn) {
            switch (direction) {
                case "up"    -> worldY -= speed;
                case "down"  -> worldY += speed;
                case "left"  -> worldX -= speed;
                case "right" -> worldX += speed;
            }
        }

        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum     = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

    }

    public void speak() {
        if (dialogues[dialogueIndex] == null) dialogueIndex = 0;
        gp.ui.currentDialogue = dialogues[dialogueIndex];
        dialogueIndex++;

        switch (gp.player.getDirection()) {
            case "up"    -> direction = "down";
            case "down"  -> direction = "up";
            case "left"  -> direction = "right";
            case "right" -> direction = "left";
        }
    }

    protected BufferedImage setup(String imageName, int width, int height) {
        try {
            BufferedImage raw = ImageIO.read(
                getClass().getResourceAsStream(imageName + ".png"));
            return UtilityTool.scaleImage(raw, width, height);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int  getWorldX() { return worldX; }
    public void setWorldX(int v) { this.worldX = v; }
    public int  getWorldY() { return worldY; }
    public void setWorldY(int v) { this.worldY = v; }

    public int getSpeed() { return speed; }
    public void setSpeed(int v) { this.speed = v; }
    public String getDirection() { return direction; }
    public void setDirection(String v) { this.direction = v; }



    public EntityType getType() { return type; }
    public void setType(EntityType v) { this.type = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }

    public boolean isCollision() { return collision; }
    public void setCollision(boolean v) { this.collision = v; }
    public Rectangle getSolidArea() { return solidArea; }
    public Rectangle getAttackArea() { return attackArea; }
    public int getSolidAreaDefaultX() { return solidAreaDefaultX; }
    public int getSolidAreaDefaultY() { return solidAreaDefaultY; }
    public boolean isColissionOn() { return colissionOn; }
    public void setColissionOn(boolean v) { this.colissionOn = v; }

    public int getSpriteNum() { return spriteNum; }
    public int getSpriteCounter() { return spriteCounter; }

    public BufferedImage getDown() { return down; }
    public BufferedImage getDown1() { return down1; }
    public BufferedImage getDown2() { return down2; }
    public BufferedImage getUp() { return up; }
    public BufferedImage getUp1() { return up1; }
    public BufferedImage getUp2() { return up2; }
    public BufferedImage getLeft1() { return left1; }
    public BufferedImage getLeft2() { return left2; }
    public BufferedImage getRight1() { return right1; }
    public BufferedImage getRight2() { return right2; }

    public int getDialogueIndex() {
        return dialogueIndex;
    }

    public void setDialogueIndex(int dialogueIndex) {
        this.dialogueIndex = dialogueIndex;
    }

    public String[] getDialogues() {
        return dialogues;
    }

    protected void setDialogue(int index, String text) {
        if (index >= 0 && index < dialogues.length) dialogues[index] = text;
    }
}
