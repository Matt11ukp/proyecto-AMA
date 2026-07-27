package unl.edu.ec.ama.engine.domain.entity;

import unl.edu.ec.ama.data.dto.GameResult;
import unl.edu.ec.ama.engine.view.render.GamePanel;

import java.awt.*;
import java.util.Random;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Visual extends Test {

    private static final String TEST_NAME = "Test Visual - Linterna y Hongos";

    private final Random random = new Random();

    private Rectangle[] mushroomAreas;
    private int[] mushroomX;
    private int[] mushroomY;
    private int   mushroomCount;
    private boolean forestConfigured;

    private final int  totalGems  = 10;
    private int[]      gemIndexes = new int[totalGems];
    private boolean[]  foundGems  = new boolean[totalGems];
    private int        foundGemCount;

    private boolean flashlightOn;
    private int     flashlightX;
    private int     flashlightY;
    private boolean flashlightInitialized;

    private long introStartTime;
    private final long introDuration = 5000;

    private GameResult result;
    private long       completedAt = 0;

    public Visual(GamePanel gp) {
        super(gp);
    }

    @Override
    protected void onStart() {
        introStartTime    = System.currentTimeMillis();
        forestConfigured  = false;
        flashlightInitialized = false;
        foundGemCount     = 0;
        flashlightOn      = false;
        completedAt       = 0;
        result            = null;
        for (int i = 0; i < foundGems.length; i++) foundGems[i] = false;
    }

    @Override
    public void update() {
        if (!testCompleted && foundGemCount >= totalGems) {
            result      = endTest();
            completedAt = System.currentTimeMillis();


            try {
                // CORREGIDO: Apuntamos directamente a la vista de la tabla en Jakarta EE.
                // Como endTest() ya envió el POST oculto con todos los datos a /finalizarPrueba,
                // aquí solo abrimos la página final para ver el registro guardado.
                String url = "http://localhost:9080/jbrew/resultados.xhtml";
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void configureForest(int screenWidth, int screenHeight, int mushroomSize) {
        if (forestConfigured) return;

        int cols = (int) Math.ceil((double) screenWidth  / mushroomSize);
        int rows = (int) Math.ceil((double) screenHeight / mushroomSize);
        mushroomCount  = cols * rows;

        mushroomX      = new int[mushroomCount];
        mushroomY      = new int[mushroomCount];
        mushroomAreas  = new Rectangle[mushroomCount];

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = col * mushroomSize;
                int y = row * mushroomSize;
                mushroomX[index]     = x;
                mushroomY[index]     = y;
                mushroomAreas[index] = new Rectangle(x, y, mushroomSize, mushroomSize);
                index++;
            }
        }
        generateGems();
        forestConfigured = true;
    }

    private void generateGems() {
        boolean[] used = new boolean[mushroomCount];
        for (int i = 0; i < totalGems; i++) {
            int idx;
            do { idx = random.nextInt(mushroomCount); } while (used[idx]);
            used[idx]      = true;
            gemIndexes[i]  = idx;
            foundGems[i]   = false;
        }
        foundGemCount = 0;
    }

    public boolean checkClick(int mouseX, int mouseY) {
        if (isIntroActive() || mushroomAreas == null || testCompleted) return false;

        for (int i = 0; i < mushroomAreas.length; i++) {
            if (mushroomAreas[i].contains(mouseX, mouseY)) {
                return checkGemInMushroom(i);
            }
        }
        return false;
    }

    private boolean checkGemInMushroom(int mushroomIndex) {
        for (int i = 0; i < gemIndexes.length; i++) {
            if (gemIndexes[i] == mushroomIndex && !foundGems[i]) {
                foundGems[i] = true;
                foundGemCount++;
                addSuccess();
                return true;
            }
        }
        addMistake();
        return false;
    }

    public void initializeFlashlight(int centerX, int startY) {
        if (flashlightInitialized) return;
        flashlightX           = centerX;
        flashlightY           = startY;
        flashlightInitialized = true;
    }

    public void moveFlashlight(int mouseX, int mouseY, int flashlightSize) {
        flashlightX = mouseX - flashlightSize / 2;
        flashlightY = mouseY - flashlightSize / 2;
    }

    public boolean isFlashlightClicked(int mouseX, int mouseY, int flashlightSize) {
        return mouseX >= flashlightX && mouseX <= flashlightX + flashlightSize
            && mouseY >= flashlightY && mouseY <= flashlightY + flashlightSize;
    }

    public void toggleFlashlight() { flashlightOn = !flashlightOn; }

    public boolean isIntroActive() {
        if (introStartTime == 0) return false;
        return System.currentTimeMillis() - introStartTime < introDuration;
    }

    public boolean shouldReturnToGame(long delayMs) {
        if (!testCompleted || completedAt == 0) return false;
        return System.currentTimeMillis() - completedAt >= delayMs;
    }

    public boolean isGemIndex(int index) {
        for (int i = 0; i < gemIndexes.length; i++) {
            if (gemIndexes[i] == index && !foundGems[i]) return true;
        }
        return false;
    }

    public int     getFlashlightX()     { return flashlightX; }
    public int     getFlashlightY()     { return flashlightY; }
    public boolean isFlashlightOn()     { return flashlightOn; }
    public int     getMushroomCount()   { return mushroomCount; }
    public int     getMushroomX(int i)  { return mushroomX[i]; }
    public int     getMushroomY(int i)  { return mushroomY[i]; }
    public int     getFoundGemCount()   { return foundGemCount; }
    public int     getTotalGems()       { return totalGems; }
    public GameResult getResult()       { return result; }
}
