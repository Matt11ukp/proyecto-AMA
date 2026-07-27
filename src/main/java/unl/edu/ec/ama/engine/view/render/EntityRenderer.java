package unl.edu.ec.ama.engine.view.render;


import unl.edu.ec.ama.engine.domain.entity.Entity;
import unl.edu.ec.ama.engine.domain.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class EntityRenderer {

    private final GamePanel gp;

    public EntityRenderer(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Entity entity, Graphics2D g2) {
        if (!isInCamera(entity)) return;

        int screenX = gp.computeScreenX(entity.getWorldX());
        int screenY = gp.computeScreenY(entity.getWorldY());

        g2.drawImage(selectFrame(entity), screenX, screenY,
                     gp.tileSize, gp.tileSize, null);
        resetAlpha(g2);
    }


    private BufferedImage selectFrame(Entity e) {
        return switch (e.getDirection()) {
            case "up"    -> e.getSpriteNum() == 1 ? e.getUp1()    : e.getUp2();
            case "down"  -> e.getSpriteNum() == 1 ? e.getDown1()  : e.getDown2();
            case "left"  -> e.getSpriteNum() == 1 ? e.getLeft1()  : e.getLeft2();
            case "right" -> e.getSpriteNum() == 1 ? e.getRight1() : e.getRight2();
            default      -> e.getDown1();
        };
    }
    private boolean isInCamera(Entity entity) {
        Player p  = gp.player;
        int    ts = gp.tileSize;
        int    ex = entity.getWorldX(), ey = entity.getWorldY();

        boolean h = ex + ts > p.getWorldX() - p.getScreenX()
                 && ex - ts < p.getWorldX() + p.getScreenX();
        boolean v = ey + ts > p.getWorldY() - p.getScreenY()
                 && ey - ts < p.getWorldY() + p.getScreenY();

        boolean atEdge =
                p.getScreenX() > p.getWorldX() ||
                p.getScreenY() > p.getWorldY() ||
                (gp.screenWidth  - p.getScreenX()) > gp.maxWorldCol * ts - p.getWorldX() ||
                (gp.screenHeight - p.getScreenY()) > gp.maxWorldRow * ts - p.getWorldY();

        return (h && v) || atEdge;
    }

    private int cameraX(Player p) {
        int x = p.getScreenX();
        if (p.getScreenX() > p.getWorldX())
            x = p.getWorldX();
        if (gp.screenWidth - p.getScreenX() > gp.maxWorldCol * gp.tileSize - p.getWorldX())
            x = gp.screenWidth - (gp.maxWorldCol * gp.tileSize - p.getWorldX());
        return x;
    }

    private int cameraY(Player p) {
        int y = p.getScreenY();
        if (p.getScreenY() > p.getWorldY())
            y = p.getWorldY();
        if (gp.screenHeight - p.getScreenY() > gp.maxWorldRow * gp.tileSize - p.getWorldY())
            y = gp.screenHeight - (gp.maxWorldRow * gp.tileSize - p.getWorldY());
        return y;
    }

    private void applyAlpha(Graphics2D g2, float alpha) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    }

    private void resetAlpha(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
