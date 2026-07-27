package unl.edu.ec.ama.engine.domain.entity;

import unl.edu.ec.ama.engine.view.render.GamePanel;
import unl.edu.ec.ama.engine.view.sound.SoundName;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Concentration extends Test {

    private enum ConcentrationState {
        SETUP, RUNNING, DIFFICULTY_INCREASE, COMPLETED
    }

    private ConcentrationState state;

    // LÍMITES DE LA ARENA EN EL MUNDO (Carril central frente al portal, Columnas 13 a 23)
    private int arenaMinCol = 13;
    private int arenaMaxCol = 23;
    private int playerMinX;
    private int playerMaxX;
    private int playerFixedY; // Fila 11 del mapa (parte inferior del carril)

    // Lista de objetos cayendo
    private ArrayList<FallingObject> fallingObjects;
    private Random random;

    // Control de generación de objetos
    private int spawnCounter = 0;
    private int spawnRate = 35; // Frecuencia inicial de obstáculos
    private int minSpawnRate = 15;

    // Control de objeto objetivo
    private int targetSpawnCounter = 0;
    private int targetSpawnInterval = 110; // Frecuencia de manzanas doradas

    // Velocidad de caída
    private float baseSpeed = 4f; // Velocidad inicial un poco más ágil
    private float maxSpeed = 10f;
    private float speedIncrement = 0.4f;

    // Control de tiempo y dificultad
    private int difficultyTimer = 0;
    private int difficultyIncreaseInterval = 600; // Cada 10 segundos (600 frames)
    private long completionTime = 0;

    // Métricas
    private int hits = 0;
    private int errors = 0;
    private long testStartTime;
    private long testDuration = 45000; // 45 segundos de prueba intensa

    public Concentration(GamePanel gp, Instruction actualInstruction) {
        super(gp);
        this.gp = gp;
        this.state = ConcentrationState.SETUP;
        this.random = new Random();
        this.fallingObjects = new ArrayList<>();
    }

    @Override
    public void startTest() {
        super.startTest();
        state = ConcentrationState.SETUP;
        fallingObjects.clear();

        // 1. DEFINIR LÍMITES FÍSICOS DEL JUGADOR EN EL MAPA
        playerMinX = arenaMinCol * gp.tileSize;
        playerMaxX = (arenaMaxCol - 1) * gp.tileSize;
        playerFixedY = 11 * gp.tileSize; // Fila 11 (suelo de la arena)

        // 2. TELETRANSPORTAR AL JUGADOR AL CENTRO DEL CARRIL
        gp.player.setWorldX(18 * gp.tileSize);
        gp.player.setWorldY(playerFixedY);

        // 3. BLOQUEAR MOVIMIENTO NORMAL (Para que Player.java no lea W/S ni flechas verticales)
        gp.player.movementLocked = true;

        // Reiniciar métricas
        hits = 0;
        errors = 0;
        baseSpeed = 4f;
        spawnRate = 35;
        completionTime = 0;
        testCompleted = false;
        testStartTime = System.currentTimeMillis();

        gp.playSE(SoundName.DASH);
        state = ConcentrationState.RUNNING;
    }

    @Override
    protected void onStart() { }

    public void update() {
        if (!testCompleted && state != ConcentrationState.SETUP) {
            long elapsedTime = System.currentTimeMillis() - testStartTime;
            if (elapsedTime >= testDuration) {
                state = ConcentrationState.COMPLETED;
                completeTest();
                return;
            }

            switch(state) {
                case RUNNING             -> updateRunningPhase();
                case DIFFICULTY_INCREASE -> updateDifficultyPhase();
                case COMPLETED           -> completeTest();
                default                  -> { }
            }
        }
    }

    private void updateRunningPhase() {
        difficultyTimer++;
        if (difficultyTimer >= difficultyIncreaseInterval) {
            state = ConcentrationState.DIFFICULTY_INCREASE;
            difficultyTimer = 0;
        }

        spawnCounter++;
        if (spawnCounter >= spawnRate) {
            spawnRandomObstacle();
            spawnCounter = 0;
        }

        targetSpawnCounter++;
        if (targetSpawnCounter >= targetSpawnInterval) {
            spawnTargetObject();
            targetSpawnCounter = 0;
        }

        updateFallingObjects();
        updatePlayerMovement();
        checkCollisions();
        removeFallingObjects();
    }

    private void updateDifficultyPhase() {
        if (baseSpeed < maxSpeed) baseSpeed += speedIncrement;
        if (spawnRate > minSpawnRate) spawnRate -= 2;
        gp.playSE(SoundName.SELECT); // Pequeño aviso sonoro al subir la velocidad
        state = ConcentrationState.RUNNING;
    }

    private void updateFallingObjects() {
        for (FallingObject obj : fallingObjects) {
            obj.update(baseSpeed);
        }
    }

    /**
     * Movimiento horizontal exclusivo tipo Arcade (Izquierda / Derecha)
     */
    private void updatePlayerMovement() {
        int speed = 7; // Velocidad ágil para esquivar

        if (gp.keyH.leftPressed || gp.keyH.upPressed) { // Permitir 'A' o flecha Izquierda
            gp.player.worldX -= speed;
            if (gp.player.worldX < playerMinX) gp.player.worldX = playerMinX;
            gp.player.direction = "left";
        }
        if (gp.keyH.rightPressed || gp.keyH.downPressed) { // Permitir 'D' o flecha Derecha
            gp.player.worldX += speed;
            if (gp.player.worldX > playerMaxX) gp.player.worldX = playerMaxX;
            gp.player.direction = "right";
        }

        // Mantener siempre fijo en el eje Y
        gp.player.worldY = playerFixedY;
    }

    private void checkCollisions() {
        Rectangle playerBounds = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height
        );

        for (FallingObject obj : fallingObjects) {
            if (!obj.markedForRemoval && playerBounds.intersects(obj.getBounds())) {
                if (obj.isTarget) {
                    hits++;
                    gp.playSE(SoundName.DASH);
                    obj.markedForRemoval = true;
                } else {
                    errors++;
                    gp.playSE(SoundName.RECEIVE_DAMAGE);
                    obj.markedForRemoval = true;
                }
            }
        }
    }

    private void removeFallingObjects() {
        ArrayList<FallingObject> toRemove = new ArrayList<>();
        int floorLimitY = (12 * gp.tileSize); // Límite inferior de la arena

        for (FallingObject obj : fallingObjects) {
            if (obj.worldY > floorLimitY || obj.markedForRemoval) {
                // Si la manzana dorada tocó el suelo sin ser atrapada, cuenta como error (omisión)
                if (obj.isTarget && !obj.markedForRemoval) {
                    errors++;
                    gp.playSE(SoundName.RECEIVE_DAMAGE);
                }
                toRemove.add(obj);
            }
        }
        fallingObjects.removeAll(toRemove);
    }

    private void spawnRandomObstacle() {
        // Generar en una columna aleatoria dentro del carril permitido
        int randomCol = arenaMinCol + random.nextInt(arenaMaxCol - arenaMinCol);
        int spawnX = randomCol * gp.tileSize;
        int spawnY = 3 * gp.tileSize; // Nacen en la fila 3 (arriba en la pantalla)

        fallingObjects.add(new FallingObject(spawnX, spawnY, false, random.nextInt(3)));
    }

    private void spawnTargetObject() {
        int randomCol = arenaMinCol + random.nextInt(arenaMaxCol - arenaMinCol);
        int spawnX = randomCol * gp.tileSize;
        int spawnY = 3 * gp.tileSize;

        fallingObjects.add(new FallingObject(spawnX, spawnY, true, 0));
    }

    private void completeTest() {
        this.successes = hits;
        this.mistakes = errors;
        testCompleted = true;

        // 1. Envía los datos al servidor (RecepcionDatosServlet)
        endTest();
        completionTime = System.currentTimeMillis();

        // 2. Redirección automática al navegador web para ver los resultados
        try {
            String url = "http://localhost:9080/jbrew/resultados.xhtml";
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                System.out.println("¡Prueba de Concentración finalizada! Navegador abierto en: " + url);
            }
        } catch (Exception e) {
            System.err.println("Error al intentar abrir la página de resultados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dibuja la Arena "Lluvia Digital" en 16-Bit Pixel Art
     */
    public void drawArena(Graphics2D g2) {
        // 1. FONDO CYBER-DARK
        g2.setColor(new Color(10, 15, 25));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. CORTINA DIGITAL (Líneas verticales estilo Matrix / Tron)
        g2.setColor(new Color(18, 30, 50));
        g2.setStroke(new BasicStroke(2));
        for (int col = arenaMinCol; col <= arenaMaxCol; col++) {
            int screenX = gp.computeScreenX(col * gp.tileSize);
            if (screenX >= 0 && screenX <= gp.screenWidth) {
                g2.drawLine(screenX, 0, screenX, gp.screenHeight);
            }
        }

        // 3. ZONA DE SUELO / LÍNEA DE META (Donde se mueve el jugador)
        int floorScreenY = gp.computeScreenY(playerFixedY + gp.tileSize);
        g2.setColor(new Color(0, 255, 200, 80));
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(0, floorScreenY, gp.screenWidth, floorScreenY);

        // 4. DIBUJAR LOS OBJETOS CAYENDO (Con Anti-Aliasing apagado para estilo retro)
        Object oldAA = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        for (FallingObject obj : fallingObjects) {
            obj.draw(g2, gp);
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);

        // 5. DIBUJAR INTERFAZ HUD RETRO
        drawHUD(g2);
    }

    /**
     * Interfaz Superior Estilo Arcade 16-Bit
     */
    private void drawHUD(Graphics2D g2) {
        int panelWidth = 500;
        int panelHeight = 60;
        int panelX = (gp.screenWidth - panelWidth) / 2;
        int panelY = 20;

        // Fondo oscuro del HUD
        g2.setColor(new Color(8, 12, 22, 240));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);

        // Borde neón doble
        g2.setColor(new Color(0, 220, 255));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(panelX + 4, panelY + 4, panelWidth - 8, panelHeight - 8);

        // Textos del HUD
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));

        if (testCompleted) {
            g2.setColor(new Color(255, 230, 0));
            g2.drawString("★ PRUEBA COMPLETADA - CALCULANDO DATOS... ★", panelX + 35, panelY + 35);
        } else {
            // Aciertos (Verde Neón)
            g2.setColor(new Color(40, 255, 100));
            g2.drawString("ATRAPADOS: " + hits, panelX + 20, panelY + 36);

            // Errores (Rojo Neón)
            g2.setColor(new Color(255, 60, 60));
            g2.drawString("FALLOS: " + errors, panelX + 190, panelY + 36);

            // Tiempo y Velocidad (Amarillo)
            long elapsedTime = System.currentTimeMillis() - testStartTime;
            long remainingTime = Math.max(0, (testDuration - elapsedTime) / 1000);
            g2.setColor(new Color(255, 220, 0));
            g2.drawString("TIEMPO: " + remainingTime + "s [" + String.format("%.1f", baseSpeed) + "x]", panelX + 320, panelY + 36);
        }
    }

    /**
     * Clase interna para Objetos Cayendo en Estilo 16-Bit Pixel Art
     */
    private static class FallingObject {
        int worldX, worldY;
        boolean isTarget;
        int spriteType; // 0: Roca, 1: Bomba/Espectro, 2: Chatarra digital
        boolean markedForRemoval = false;
        int size = 40;

        int flashCounter = 0;
        boolean isFlashing = false;

        FallingObject(int worldX, int worldY, boolean isTarget, int spriteType) {
            this.worldX = worldX;
            this.worldY = worldY;
            this.isTarget = isTarget;
            this.spriteType = spriteType;
        }

        void update(float speed) {
            worldY += (int)speed;
            if (isTarget) {
                flashCounter++;
                if (flashCounter > 8) {
                    isFlashing = !isFlashing;
                    flashCounter = 0;
                }
            }
        }

        Rectangle getBounds() {
            // Caja de colisión un poco más indulgente en el centro del sprite
            return new Rectangle(worldX + 6, worldY + 6, size - 12, size - 12);
        }

        void draw(Graphics2D g2, GamePanel gp) {
            int screenX = gp.computeScreenX(worldX);
            int screenY = gp.computeScreenY(worldY);

            // Solo dibujar si está visible en pantalla
            if (screenY > -gp.tileSize && screenY < gp.screenHeight + gp.tileSize) {

                if (isTarget) {
                    drawGoldenApple(g2, screenX, screenY);
                } else {
                    drawObstacle(g2, screenX, screenY);
                }
            }
        }

        /**
         * Sprite 16-Bit: Manzana Dorada Cibernética
         */
        private void drawGoldenApple(Graphics2D g2, int x, int y) {
            // Brillo exterior parpadeante
            if (isFlashing) {
                g2.setColor(new Color(255, 255, 0, 80));
                g2.fillRect(x - 4, y - 4, size + 8, size + 8);
            }

            // Cuerpo dorado (Pixel art octogonal)
            g2.setColor(isFlashing ? new Color(255, 240, 50) : new Color(230, 180, 0));
            g2.fillRect(x + 6, y + 2, size - 12, size - 4);
            g2.fillRect(x + 2, y + 6, size - 4, size - 12);

            // Contorno negro grueso
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(x + 6, y + 2, size - 12, size - 4);
            g2.drawRect(x + 2, y + 6, size - 4, size - 12);

            // Brillo blanco de 8 bits en la esquina superior izquierda
            g2.setColor(Color.WHITE);
            g2.fillRect(x + 8, y + 8, 6, 6);

            // Hoja esmeralda arriba
            g2.setColor(new Color(0, 255, 100));
            g2.fillRect(x + (size/2) - 2, y - 4, 8, 6);
            g2.setColor(Color.BLACK);
            g2.drawRect(x + (size/2) - 2, y - 4, 8, 6);
        }

        /**
         * Sprites 16-Bit para Obstáculos
         */
        private void drawObstacle(Graphics2D g2, int x, int y) {
            switch (spriteType) {
                case 0 -> { // ROCA ESPACIAL / METEORITO GRIS
                    g2.setColor(new Color(110, 115, 130));
                    g2.fillRect(x + 4, y + 4, size - 8, size - 8);
                    g2.setColor(new Color(70, 75, 90));
                    g2.fillRect(x + 12, y + 12, size - 16, size - 16);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect(x + 4, y + 4, size - 8, size - 8);
                }
                case 1 -> { // MINA / ESPECTRO ROJO NEÓN
                    g2.setColor(new Color(220, 30, 60));
                    g2.fillOval(x + 4, y + 4, size - 8, size - 8);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(x + 4, y + 4, size - 8, size - 8);
                    // Ojo amarillo central
                    g2.setColor(Color.YELLOW);
                    g2.fillRect(x + (size/2) - 4, y + (size/2) - 4, 8, 8);
                }
                case 2 -> { // CHATARRA CIBERNÉTICA PÚRPURA
                    g2.setColor(new Color(160, 40, 220));
                    int[] xP = {x + size/2, x + size - 4, x + size/2, x + 4};
                    int[] yP = {y + 4, y + size/2, y + size - 4, y + size/2};
                    g2.fillPolygon(xP, yP, 4);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawPolygon(xP, yP, 4);
                }
            }
        }
    }

    public boolean shouldReturnToGame(int delayMillis) {
        if (!isTestCompleted() || completionTime == 0) {
            return false;
        }
        return (System.currentTimeMillis() - completionTime) >= delayMillis;
    }
}