package unl.edu.ec.ama.engine.domain.entity;

import unl.edu.ec.ama.engine.view.render.GamePanel;

import java.awt.*;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class CollisionCheck {

    private final GamePanel gp;

    public CollisionCheck(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        Rectangle solid = entity.getSolidArea();

        int entityLeftWorldX   = entity.getWorldX() + solid.x;
        int entityRightWorldX  = entity.getWorldX() + solid.x + solid.width;
        int entityTopWorldY    = entity.getWorldY() + solid.y;
        int entityBottomWorldY = entity.getWorldY() + solid.y + solid.height;

        int entityLeftCol   = entityLeftWorldX   / gp.tileSize;
        int entityRightCol  = entityRightWorldX  / gp.tileSize;
        int entityTopRow    = entityTopWorldY    / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int speed = entity.getSpeed();
        String dir = entity.getDirection();
        int tileNum1, tileNum2;

        switch (dir) {
            case "up" -> {
                entityTopRow = (entityTopWorldY - speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if (gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision())
                    entity.setColissionOn(true);
            }
            case "down" -> {
                entityBottomRow = (entityBottomWorldY + speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision())
                    entity.setColissionOn(true);
            }
            case "right" -> {
                entityRightCol = (entityRightWorldX + speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision())
                    entity.setColissionOn(true);
            }
            case "left" -> {
                entityLeftCol = (entityLeftWorldX - speed) / gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if (gp.tileM.tile[tileNum1].isCollision() || gp.tileM.tile[tileNum2].isCollision())
                    entity.setColissionOn(true);
            }
        }
    }

    public int checkObject(Entity entity, boolean isPlayer) {
        int index = 999;
        Rectangle eArea = entity.getSolidArea();

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) continue;

            Rectangle oArea = gp.obj[i].getSolidArea();

            eArea.x = entity.getWorldX() + eArea.x;
            eArea.y = entity.getWorldY() + eArea.y;
            oArea.x = gp.obj[i].getWorldX() + oArea.x;
            oArea.y = gp.obj[i].getWorldY() + oArea.y;

            applyDirectionOffset(eArea, entity.getDirection(), entity.getSpeed());

            if (eArea.intersects(oArea)) {
                if (gp.obj[i].isCollision()) entity.setColissionOn(true);
                if (isPlayer) index = i;
            }

            eArea.x = entity.getSolidAreaDefaultX();
            eArea.y = entity.getSolidAreaDefaultY();
            oArea.x = gp.obj[i].getSolidAreaDefaultX();
            oArea.y = gp.obj[i].getSolidAreaDefaultY();
        }

        return index;
    }

    public int checkEntity(Entity entity, Entity[] targets) {
        int index = 999;
        Rectangle eArea = entity.getSolidArea();

        for (int i = 0; i < targets.length; i++) {
            if (targets[i] == null) continue;

            Rectangle tArea = targets[i].getSolidArea();

            eArea.x = entity.getWorldX() + eArea.x;
            eArea.y = entity.getWorldY() + eArea.y;
            tArea.x = targets[i].getWorldX() + tArea.x;
            tArea.y = targets[i].getWorldY() + tArea.y;

            applyDirectionOffset(eArea, entity.getDirection(), entity.getSpeed());

            if (eArea.intersects(tArea) && targets[i] != entity) {
                entity.setColissionOn(true);
                index = i;
            }

            eArea.x = entity.getSolidAreaDefaultX();
            eArea.y = entity.getSolidAreaDefaultY();
            tArea.x = targets[i].getSolidAreaDefaultX();
            tArea.y = targets[i].getSolidAreaDefaultY();
        }

        return index;
    }

    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;
        Rectangle eArea = entity.getSolidArea();
        Rectangle pArea = gp.player.getSolidArea();

        eArea.x = entity.getWorldX() + eArea.x;
        eArea.y = entity.getWorldY() + eArea.y;
        pArea.x = gp.player.getWorldX() + pArea.x;
        pArea.y = gp.player.getWorldY() + pArea.y;

        applyDirectionOffset(eArea, entity.getDirection(), entity.getSpeed());

        if (eArea.intersects(pArea)) {
            entity.setColissionOn(true);
            contactPlayer = true;
        }

        eArea.x = entity.getSolidAreaDefaultX();
        eArea.y = entity.getSolidAreaDefaultY();
        pArea.x = gp.player.getSolidAreaDefaultX();
        pArea.y = gp.player.getSolidAreaDefaultY();

        return contactPlayer;
    }

    private void applyDirectionOffset(Rectangle area, String direction, int speed) {
        switch (direction) {
            case "up"    -> area.y -= speed;
            case "down"  -> area.y += speed;
            case "left"  -> area.x -= speed;
            case "right" -> area.x += speed;
        }
    }
}
