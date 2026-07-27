package unl.edu.ec.ama.engine.view.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class ImageGetter {

    private final int size;

    private final BufferedImage[][] shirtImages   = new BufferedImage[15][10];
    private final BufferedImage[][] skinImages    = new BufferedImage[12][10];
    private final BufferedImage[][] hairMaleImages = new BufferedImage[15][4];
    private final BufferedImage[][] eyeImages     = new BufferedImage[15][3];

    private static BufferedImage[] objects = new BufferedImage[10];

    private final BufferedImage[] shirts     = new BufferedImage[15];
    private final BufferedImage[] hairsFemale = new BufferedImage[15];
    private final BufferedImage[] hairsMale  = new BufferedImage[15];
    private final BufferedImage[] skins      = new BufferedImage[12];
    private final BufferedImage[] eyes       = new BufferedImage[15];
    private final BufferedImage[] skinBlocks = new BufferedImage[12];

    // Direccion

    private final BufferedImage[] skinDown1 = new BufferedImage[12];
    private final BufferedImage[] shirtDown1 = new BufferedImage[15];
    private final BufferedImage[] eyeDown1 = new BufferedImage[15];
    private final BufferedImage[] maleDown1 = new BufferedImage[15];
    private final BufferedImage[] femaleDown1 = new BufferedImage[15];

    private final BufferedImage[] skinDown2 = new BufferedImage[12];
    private final BufferedImage[] shirtDown2 = new BufferedImage[15];
    private final BufferedImage[] eyeDown2 = new BufferedImage[15];
    private final BufferedImage[] maleDown2 = new BufferedImage[15];
    private final BufferedImage[] femaleDown2 = new BufferedImage[15];

    private final BufferedImage[] skinUp = new BufferedImage[12];
    private final BufferedImage[] shirtUp = new BufferedImage[15];
    private final BufferedImage[] eyeUp = new BufferedImage[15];
    private final BufferedImage[] maleUp = new BufferedImage[15];
    private final BufferedImage[] femaleUp = new BufferedImage[15];

    private final BufferedImage[] skinUp1 = new BufferedImage[12];
    private final BufferedImage[] shirtUp1 = new BufferedImage[15];
    private final BufferedImage[] eyeUp1 = new BufferedImage[15];
    private final BufferedImage[] maleUp1 = new BufferedImage[15];
    private final BufferedImage[] femaleUp1 = new BufferedImage[15];

    private final BufferedImage[] skinUp2 = new BufferedImage[12];
    private final BufferedImage[] shirtUp2 = new BufferedImage[15];
    private final BufferedImage[] eyeUp2 = new BufferedImage[15];
    private final BufferedImage[] maleUp2 = new BufferedImage[15];
    private final BufferedImage[] femaleUp2 = new BufferedImage[15];

    private final BufferedImage[] skinLeft1 = new BufferedImage[12];
    private final BufferedImage[] shirtLeft1 = new BufferedImage[15];
    private final BufferedImage[] eyeLeft1 = new BufferedImage[15];
    private final BufferedImage[] maleLeft1 = new BufferedImage[15];
    private final BufferedImage[] femaleLeft1 = new BufferedImage[15];

    private final BufferedImage[] skinLeft2 = new BufferedImage[12];
    private final BufferedImage[] shirtLeft2 = new BufferedImage[15];
    private final BufferedImage[] eyeLeft2 = new BufferedImage[15];
    private final BufferedImage[] maleLeft2 = new BufferedImage[15];
    private final BufferedImage[] femaleLeft2 = new BufferedImage[15];

    private final BufferedImage[] skinRight1 = new BufferedImage[12];
    private final BufferedImage[] shirtRight1 = new BufferedImage[15];
    private final BufferedImage[] eyeRight1 = new BufferedImage[15];
    private final BufferedImage[] maleRight1 = new BufferedImage[15];
    private final BufferedImage[] femaleRight1 = new BufferedImage[15];

    private final BufferedImage[] skinRight2 = new BufferedImage[12];
    private final BufferedImage[] shirtRight2 = new BufferedImage[15];
    private final BufferedImage[] eyeRight2 = new BufferedImage[15];
    private final BufferedImage[] maleRight2 = new BufferedImage[15];
    private final BufferedImage[] femaleRight2 = new BufferedImage[15];

    private BufferedImage female;
    private BufferedImage male;

    public ImageGetter(int tileSize) {
        this.size = tileSize;
        getSkin();
    }

    public void getSkin() {
        imageSingle(10,  "objects",        objects);
        imageSingle(12, "skin/Block",     skinBlocks);
        imageSingle(15, "skin/HairFemale", hairsFemale);

        getImages(15, 4,  "Hair",  "hair",  hairMaleImages);
        getImages(15, 10, "Shirt", "shirt", shirtImages);
        getImages(12, 10, "Skin",  "skin",  skinImages);
        getImages(15, 3,  "Eye",   "eye",   eyeImages);

        female = setup("/skin/female", size, size);
        male   = setup("/skin/male",   size, size);

        for (int i = 0; i < 15; i++) hairsMale[i] = hairMaleImages[i][0];
        for (int i = 0; i < 15; i++) shirts[i] = shirtImages[i][0];
        for (int i = 0; i < 15; i++) eyes[i] = eyeImages[i][0];
        for (int i = 0; i < 12; i++) skins[i] = skinImages[i][0];
        //down1
        for (int i = 0; i < 15; i++) maleDown1[i] = hairMaleImages[i][0];
        for (int i = 0; i < 15; i++) shirtDown1[i] = shirtImages[i][1];
        for (int i = 0; i < 15; i++) eyeDown1[i] = eyeImages[i][0];
        for (int i = 0; i < 12; i++) skinDown1[i] = skinImages[i][1];
        //down2
        for (int i = 0; i < 15; i++) maleDown2[i] = hairMaleImages[i][0];
        for (int i = 0; i < 15; i++) shirtDown2[i] = shirtImages[i][2];
        for (int i = 0; i < 15; i++) eyeDown2[i] = eyeImages[i][0];
        for (int i = 0; i < 12; i++) skinDown2[i] = skinImages[i][2];
        //up
        for (int i = 0; i < 15; i++) maleUp[i] = hairMaleImages[i][3];
        for (int i = 0; i < 15; i++) shirtUp[i] = shirtImages[i][7];
        for (int i = 0; i < 15; i++) eyeUp[i] = eyeImages[i][2];
        for (int i = 0; i < 12; i++) skinUp[i] = skinImages[i][7];
        //up1
        for (int i = 0; i < 15; i++) maleUp1[i] = hairMaleImages[i][3];
        for (int i = 0; i < 15; i++) shirtUp1[i] = shirtImages[i][8];
        for (int i = 0; i < 15; i++) eyeUp1[i] = eyeImages[i][2];
        for (int i = 0; i < 12; i++) skinUp1[i] = skinImages[i][8];
        //up2
        for (int i = 0; i < 15; i++) maleUp2[i] = hairMaleImages[i][3];
        for (int i = 0; i < 15; i++) shirtUp2[i] = shirtImages[i][9];
        for (int i = 0; i < 15; i++) eyeUp2[i] = eyeImages[i][2];
        for (int i = 0; i < 12; i++) skinUp2[i] = skinImages[i][9];
        //left1
        for (int i = 0; i < 15; i++) maleLeft1[i] = hairMaleImages[i][1];
        for (int i = 0; i < 15; i++) shirtLeft1[i] = shirtImages[i][3];
        for (int i = 0; i < 15; i++) eyeLeft1[i] = eyeImages[i][1];
        for (int i = 0; i < 12; i++) skinLeft1[i] = skinImages[i][3];
        //left2
        for (int i = 0; i < 15; i++) maleLeft2[i] = hairMaleImages[i][1];
        for (int i = 0; i < 15; i++) shirtLeft2[i] = shirtImages[i][4];
        for (int i = 0; i < 15; i++) eyeLeft2[i] = eyeImages[i][1];
        for (int i = 0; i < 12; i++) skinLeft2[i] = skinImages[i][4];
        //right1
        for (int i = 0; i < 15; i++) maleRight1[i] = hairMaleImages[i][2];
        for (int i = 0; i < 15; i++) shirtRight1[i] = shirtImages[i][5];
        for (int i = 0; i < 15; i++) eyeRight1[i] = eyeImages[i][2];
        for (int i = 0; i < 12; i++) skinRight1[i] = skinImages[i][5];
        //right2
        for (int i = 0; i < 15; i++) maleRight2[i] = hairMaleImages[i][2];
        for (int i = 0; i < 15; i++) shirtRight2[i] = shirtImages[i][6];
        for (int i = 0; i < 15; i++) eyeRight2[i] = eyeImages[i][2];
        for (int i = 0; i < 12; i++) skinRight2[i] = skinImages[i][6];
    }

    public void getImages(int count, int dirs, String folder,
                          String type, BufferedImage[][] dest) {
        for (int i = 0; i < count; i++)
            for (int j = 0; j < dirs; j++)
                dest[i][j] = setup("/skin/" + folder + "/" + type + "_" + i + "_" + j,
                                   size, size);
    }

    public void imageSingle(int count, String type, BufferedImage[] dest) {
        for (int i = 0; i < count; i++)
            dest[i] = setup("/" + type + "/" + i, size, size);
    }

    public BufferedImage setup(String imageName, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(
                getClass().getResourceAsStream(imageName + ".png"));
            return UtilityTool.scaleImage(img, width, height);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BufferedImage[]   getSkins()        { return skins; }
    public BufferedImage[]   getHairsFemale()  { return hairsFemale; }
    public BufferedImage[]   getHairsMale()    { return hairsMale; }
    public BufferedImage[]   getShirts()       { return shirts; }
    public BufferedImage[]   getEyes()         { return eyes; }
    public BufferedImage[]   getSkinBlocks()   { return skinBlocks; }
    public BufferedImage     getFemale()       { return female; }
    public BufferedImage     getMale()         { return male; }
    public static BufferedImage[] getObjects() { return objects; }

    public BufferedImage[] getSkinUp() {
        return skinUp;
    }

    public BufferedImage[] getShirtUp() {
        return shirtUp;
    }

    public BufferedImage[] getEyeUp() {
        return eyeUp;
    }

    public BufferedImage[] getMaleUp() {
        return maleUp;
    }

    public BufferedImage[] getFemaleUp() {
        return femaleUp;
    }

    public BufferedImage[] getSkinUp1() {
        return skinUp1;
    }

    public BufferedImage[] getShirtUp1() {
        return shirtUp1;
    }

    public BufferedImage[] getEyeUp1() {
        return eyeUp1;
    }

    public BufferedImage[] getMaleUp1() {
        return maleUp1;
    }

    public BufferedImage[] getFemaleUp1() {
        return femaleUp1;
    }

    public BufferedImage[] getSkinUp2() {
        return skinUp2;
    }

    public BufferedImage[] getShirtUp2() {
        return shirtUp2;
    }

    public BufferedImage[] getEyeUp2() {
        return eyeUp2;
    }

    public BufferedImage[] getMaleUp2() {
        return maleUp2;
    }

    public BufferedImage[] getFemaleUp2() {
        return femaleUp2;
    }

    public BufferedImage[] getSkinLeft1() {
        return skinLeft1;
    }

    public BufferedImage[] getShirtLeft1() {
        return shirtLeft1;
    }

    public BufferedImage[] getEyeLeft1() {
        return eyeLeft1;
    }

    public BufferedImage[] getMaleLeft1() {
        return maleLeft1;
    }

    public BufferedImage[] getFemaleLeft1() {
        return femaleLeft1;
    }

    public BufferedImage[] getSkinLeft2() {
        return skinLeft2;
    }

    public BufferedImage[] getShirtLeft2() {
        return shirtLeft2;
    }

    public BufferedImage[] getEyeLeft2() {
        return eyeLeft2;
    }

    public BufferedImage[] getMaleLeft2() {
        return maleLeft2;
    }

    public BufferedImage[] getFemaleLeft2() {
        return femaleLeft2;
    }

    public BufferedImage[] getSkinRight1() {
        return skinRight1;
    }

    public BufferedImage[] getShirtRight1() {
        return shirtRight1;
    }

    public BufferedImage[] getEyeRight1() {
        return eyeRight1;
    }

    public BufferedImage[] getMaleRight1() {
        return maleRight1;
    }

    public BufferedImage[] getFemaleRight1() {
        return femaleRight1;
    }

    public BufferedImage[] getSkinRight2() {
        return skinRight2;
    }

    public BufferedImage[] getShirtRight2() {
        return shirtRight2;
    }

    public BufferedImage[] getEyeRight2() {
        return eyeRight2;
    }

    public BufferedImage[] getMaleRight2() {
        return maleRight2;
    }

    public BufferedImage[] getFemaleRight2() {
        return femaleRight2;
    }

    public BufferedImage[] getSkinDown1() {
        return skinDown1;
    }

    public BufferedImage[] getShirtDown1() {
        return shirtDown1;
    }

    public BufferedImage[] getEyeDown1() {
        return eyeDown1;
    }

    public BufferedImage[] getMaleDown1() {
        return maleDown1;
    }

    public BufferedImage[] getFemaleDown1() {
        return femaleDown1;
    }

    public BufferedImage[] getSkinDown2() {
        return skinDown2;
    }

    public BufferedImage[] getShirtDown2() {
        return shirtDown2;
    }

    public BufferedImage[] getEyeDown2() {
        return eyeDown2;
    }

    public BufferedImage[] getMaleDown2() {
        return maleDown2;
    }

    public BufferedImage[] getFemaleDown2() {
        return femaleDown2;
    }
}
