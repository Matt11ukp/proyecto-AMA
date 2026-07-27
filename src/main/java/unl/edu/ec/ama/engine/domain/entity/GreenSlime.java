package unl.edu.ec.ama.engine.domain.entity;


import unl.edu.ec.ama.engine.view.render.GamePanel;

import java.util.Random;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class GreenSlime extends Entity {

    private static final Random RANDOM = new Random();

    public GreenSlime(GamePanel gp) {
        super(gp);

        setName("Green Slime");
        setSpeed(1);
        setDirection("down");
        setType(EntityType.MONSTER);

        solidArea.x      = 3;
        solidArea.y      = 18;
        solidArea.width  = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        loadImages();
    }

    private void loadImages() {
        up1    = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        up2    = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
        down1  = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        down2  = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
        left1  = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        left2  = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
        right1 = setup("/monster/greenslime_down_1", gp.tileSize, gp.tileSize);
        right2 = setup("/monster/greenslime_down_2", gp.tileSize, gp.tileSize);
    }

    @Override
    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter >= 120) {
            int roll = RANDOM.nextInt(100) + 1;
            if      (roll <= 25)  setDirection("up");
            else if (roll <= 50)  setDirection("down");
            else if (roll <= 75)  setDirection("left");
            else                  setDirection("right");
            actionLockCounter = 0;
        }
    }

}
