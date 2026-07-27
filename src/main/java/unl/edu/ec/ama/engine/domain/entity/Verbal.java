package unl.edu.ec.ama.engine.domain.entity;



import unl.edu.ec.ama.engine.view.render.GamePanel;
import unl.edu.ec.ama.engine.view.sound.SoundName;

import java.awt.*;
import java.util.ArrayList;

public class Verbal extends Test {

    private enum VerbalState {
        WAITING, AUDIO_PLAYING, INTERACTIVE, COMPLETED
    }

    private VerbalState state;

    private ArrayList<GeometricFigure> figures;
    private int[] sequenceOrder;
    private int currentSequenceIndex = 0;

    private String[] audioText;
    private int audioFrameCounter = 0;
    private boolean audioPlaying = false;
    private int totalAudioFrames = 0;
    private ArrayList<Integer> textChangeFrames;
    private int currentTextIndex = 0;
    public String displayedText = "";

    // Validación de secuencia, enfriamiento y DETECTOR DE SALIDA
    private boolean[] visitedFigures;
    private int errorCooldown = 0;
    private int justVisitedFigureIndex = -1; // Recuerda qué figura acabas de tocar para no castigarte mientras sales de ella

    private int correctSequences = 0;
    private int wrongSequences = 0;
    private long testStartTime;
    private long completionTime = 0;

    public Verbal(GamePanel gp, Instruction actualInstruction) {
        super(gp);
        this.gp = gp;
        this.state = VerbalState.WAITING;
        initializeFigures();
        initializeAudio();
    }

    /**
     * Inicializa las figuras en los márgenes extremos del mapa (Formando un gran anillo)
     */
    private void initializeFigures() {
        figures = new ArrayList<>();

        // El centro de la arena es (18, 8). Dispersamos las figuras hacia los bordes:

        // 1. Círculo - Rojo (Borde Superior, bien al norte)
        figures.add(new GeometricFigure(18, 3, "circle",   new Color(255, 40, 40),   gp.tileSize));

        // 2. Cuadrado - Verde (Esquina Inferior Izquierda, margen suroeste)
        figures.add(new GeometricFigure(12, 12, "square",   new Color(40, 230, 40),   gp.tileSize));

        // 3. Estrella - Amarillo (Borde Superior Derecha, margen noreste)
        figures.add(new GeometricFigure(25, 5, "star",     new Color(255, 230, 0),   gp.tileSize));

        // 4. Triángulo - Magenta (Esquina Inferior Derecha, margen sureste)
        figures.add(new GeometricFigure(24, 12, "triangle", new Color(255, 50, 255),  gp.tileSize));

        // 5. Pentágono - Cian (Borde Superior Izquierda, margen noroeste)
        figures.add(new GeometricFigure(11, 5, "pentagon", new Color(0, 240, 255),   gp.tileSize));

        sequenceOrder = new int[] {0, 2, 4, 1, 3};
        visitedFigures = new boolean[figures.size()];
    }

    private void initializeAudio() {
        // 1. Textos sincronizados con tu locución
        this.audioText = new String[] {
                "Hola aventurero. Necesito tu ayuda.",
                "Debes visitar cinco figuras en un orden específico.",
                "Primero, el CÍRCULO rojo.",
                "Luego, la ESTRELLA amarilla.",
                "Después, el PENTÁGONO cian.",
                "Seguido del CUADRADO verde.",
                "Finalmente, el TRIÁNGULO magenta.",
                "¡Buena suerte!"
        };

        // 2. Fotogramas exactos (Segundo * 60) calculados para 21 segundos de duración
        this.textChangeFrames = new ArrayList<>();
        textChangeFrames.add(0);     // 0.0 seg -> "Hola aventurero..."
        textChangeFrames.add(150);   // 2.5 seg -> "Debes visitar..."
        textChangeFrames.add(360);   // 6.0 seg -> "Primero, el CÍRCULO..."
        textChangeFrames.add(510);   // 8.5 seg -> "Luego, la ESTRELLA..."
        textChangeFrames.add(660);   // 11.0 seg -> "Después, el PENTÁGONO..."
        textChangeFrames.add(810);   // 13.5 seg -> "Seguido del CUADRADO..."
        textChangeFrames.add(960);   // 16.0 seg -> "Finalmente, el TRIÁNGULO..."
        textChangeFrames.add(1110);  // 18.5 seg -> "¡Buena suerte!"

        // 3. Duración total del archivo de audio: 21.0 segundos exactos
        this.totalAudioFrames = 1260;
    }

    @Override
    public void startTest() {
        super.startTest();
        state = VerbalState.AUDIO_PLAYING;
        audioPlaying = true;
        audioFrameCounter = 0;
        currentTextIndex = 0;
        currentSequenceIndex = 0;
        correctSequences = 0;
        wrongSequences = 0;
        completionTime = 0;
        justVisitedFigureIndex = -1;
        testCompleted = false;

        visitedFigures = new boolean[figures.size()];
        displayedText = audioText[0];
        testStartTime = System.currentTimeMillis();

        // ── TELETRANSPORTE AL CENTRO DE LA ARENA ──
        gp.player.setWorldX(18 * gp.tileSize);
        gp.player.setWorldY(8 * gp.tileSize);

        // ── REPRODUCIR EL AUDIO DE INSTRUCCIONES REAL ──
        gp.playSE(SoundName.INSTRUCCION_VERBAL); // <-- Aquí disparas tu audio oficial

        gp.player.movementLocked = true; // Bloquea el movimiento mientras el audio habla
    }

    @Override
    protected void onStart() { }

    public void update() {
        if (!testCompleted) {
            switch(state) {
                case AUDIO_PLAYING -> updateAudioPhase();
                case INTERACTIVE   -> updateInteractivePhase();
                case COMPLETED     -> completeTest();
                default            -> { }
            }
        }

    }

    private void updateAudioPhase() {
        audioFrameCounter++;

        if (currentTextIndex < textChangeFrames.size() - 1) {
            if (audioFrameCounter >= textChangeFrames.get(currentTextIndex + 1)) {
                currentTextIndex++;
                if (currentTextIndex < audioText.length) {
                    displayedText = audioText[currentTextIndex];
                }
            }
        }

        if (audioFrameCounter >= totalAudioFrames) {
            audioPlaying = false;
            state = VerbalState.INTERACTIVE;
            gp.player.movementLocked = false;
            displayedText = "Visita las figuras en el orden correcto...";
        }
    }

    private void updateInteractivePhase() {
        gp.player.movementLocked = false;

        if (errorCooldown > 0) {
            errorCooldown--;
        }

        int expectedFigureIndex = sequenceOrder[currentSequenceIndex];
        GeometricFigure expectedFigure = figures.get(expectedFigureIndex);

        Rectangle playerSolidArea = new Rectangle(
                gp.player.worldX + gp.player.solidArea.x,
                gp.player.worldY + gp.player.solidArea.y,
                gp.player.solidArea.width,
                gp.player.solidArea.height
        );

        // 1. DETECTOR DE SALIDA: Si venías pisando una figura correcta, verificamos si ya saliste de ella
        if (justVisitedFigureIndex != -1) {
            GeometricFigure recentlyVisited = figures.get(justVisitedFigureIndex);
            if (!playerSolidArea.intersects(recentlyVisited.getBounds())) {
                // ¡El jugador ya dio un paso fuera de la figura! Se elimina la inmunidad.
                justVisitedFigureIndex = -1;
            }
        }

        // 2. VERIFICAR COLISIÓN CON LA FIGURA CORRECTA
        if (playerSolidArea.intersects(expectedFigure.getBounds())) {
            correctSequences++;
            visitedFigures[expectedFigureIndex] = true;
            justVisitedFigureIndex = expectedFigureIndex; // Le damos inmunidad temporal solo para esta figura
            currentSequenceIndex++;
            gp.playSE(SoundName.DASH);
            displayedText = "¡Correcto! (" + currentSequenceIndex + "/5)";

            if (currentSequenceIndex >= sequenceOrder.length) {
                state = VerbalState.COMPLETED;
            }
            return;
        }

        // 3. VERIFICAR COLISIÓN CON FIGURA INCORRECTA
        for (int i = 0; i < figures.size(); i++) {
            // Castiga si tocas cualquier figura que NO sea la esperada Y que NO sea la que estás abandonando en este momento:
            if (i != expectedFigureIndex && i != justVisitedFigureIndex && playerSolidArea.intersects(figures.get(i).getBounds())) {
                if (errorCooldown == 0) {
                    wrongSequences++;
                    gp.playSE(SoundName.RECEIVE_DAMAGE);
                    displayedText = "¡Error! Esperaba: " + figures.get(expectedFigureIndex).getType();
                    errorCooldown = 60; // 1 segundo de cooldown para no ametrallar el oído
                }
            }
        }
    }

    private void completeTest() {
        this.successes = correctSequences;
        this.mistakes = wrongSequences;

        // 1. Envía los datos al servidor (RecepcionDatosServlet) y marca testCompleted = true
        endTest();
        completionTime = System.currentTimeMillis();
        displayedText = "¡Test completado! Abriendo resultados...";

        // 2. Redirección automática al navegador web
        try {
            String url = "http://localhost:9080/jbrew/resultados.xhtml";
            if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                System.out.println("¡Prueba Verbal finalizada! Navegador abierto en: " + url);
            }
        } catch (Exception e) {
            System.err.println("Error al intentar abrir la página de resultados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Dibuja la Sala Cyber-Retro en estilo 16-Bit Pixel Art
     */
    public void drawArena(Graphics2D g2) {
        // 1. FONDO OSCURO RETRO (Azul medianoche sólido)
        g2.setColor(new Color(12, 16, 28));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // 2. CUADRÍCULA ESTILO TRON / RETRO (Líneas finas sin difuminado)
        g2.setColor(new Color(24, 34, 56));
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < gp.maxWorldCol; i++) {
            int screenX = gp.computeScreenX(i * gp.tileSize);
            if (screenX >= 0 && screenX <= gp.screenWidth) g2.drawLine(screenX, 0, screenX, gp.screenHeight);
        }
        for (int j = 0; j < gp.maxWorldRow; j++) {
            int screenY = gp.computeScreenY(j * gp.tileSize);
            if (screenY >= 0 && screenY <= gp.screenHeight) g2.drawLine(0, screenY, gp.screenWidth, screenY);
        }

        // 3. DIBUJAR LAS FIGURAS PIXEL ART
        for (GeometricFigure figure : figures) {
            figure.draw(g2, gp);
        }

        // 4. MIRA DE SELECCIÓN RETRO (Solo se dibuja mientras el test NO haya terminado)
        if (!testCompleted && currentSequenceIndex < sequenceOrder.length) {
            GeometricFigure expectedFigure = figures.get(sequenceOrder[currentSequenceIndex]);
            int worldX = expectedFigure.col * gp.tileSize;
            int worldY = expectedFigure.row * gp.tileSize;
            int screenX = gp.computeScreenX(worldX);
            int screenY = gp.computeScreenY(worldY);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            int len = 12; // Largo de las esquinas
            int sx = screenX - 6;
            int sy = screenY - 6;
            int sw = gp.tileSize + 12;
            int sh = gp.tileSize + 12;

            // Esquina Sup-Izq
            g2.drawLine(sx, sy, sx + len, sy);
            g2.drawLine(sx, sy, sx, sy + len);
            // Esquina Sup-Der
            g2.drawLine(sx + sw - len, sy, sx + sw, sy);
            g2.drawLine(sx + sw, sy, sx + sw, sy + len);
            // Esquina Inf-Izq
            g2.drawLine(sx, sy + sh - len, sx, sy + sh);
            g2.drawLine(sx, sy + sh, sx + len, sy + sh);
            // Esquina Inf-Der
            g2.drawLine(sx + sw - len, sy + sh, sx + sw, sy + sh);
            g2.drawLine(sx + sw, sy + sh - len, sx + sw, sy + sh);
        }
    }

    /**
     * Caja de Diálogo Estilo RPG Clásico (Bordes rectos, doble recuadro)
     */
    public void drawTextBox(Graphics2D g2) {
        int boxWidth = gp.screenWidth - (gp.screenWidth / 4);
        int boxHeight = 110;
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = gp.screenHeight - boxHeight - 30;

        // Fondo principal sólido
        g2.setColor(new Color(8, 12, 22, 245));
        g2.fillRect(boxX, boxY, boxWidth, boxHeight);

        // Borde exterior grueso color Cian
        g2.setColor(new Color(0, 220, 255));
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(boxX, boxY, boxWidth, boxHeight);

        // Borde interior blanco fino (Estilo Pokémon / Final Fantasy)
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(boxX + 6, boxY + 6, boxWidth - 12, boxHeight - 12);

        // Indicador de estado (¡Ahora muestra un título especial al ganar!)
        g2.setColor(new Color(0, 220, 255));
        g2.setFont(new Font("Monospaced", Font.BOLD, 14));
        if (testCompleted) {
            g2.setColor(new Color(255, 230, 0)); // Color oro para la victoria
            g2.drawString("★ MISIÓN CIBERNÉTICA SUPERADA ★", boxX + 22, boxY + 28);
        } else if (audioPlaying) {
            g2.drawString("▶ INSTRUCCIÓN DE AUDIO...", boxX + 22, boxY + 28);
        } else {
            g2.drawString("⚡ FASE DE MEMORIA (" + currentSequenceIndex + "/5)", boxX + 22, boxY + 28);
        }

        // Texto principal
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        String[] lines = wrapText(displayedText, 62);
        int yOffset = boxY + 58;
        for (String line : lines) {
            g2.drawString(line, boxX + 25, yOffset);
            yOffset += 24;
        }
    }
    private String[] wrapText(String text, int maxCharsPerLine) {
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (char c : text.toCharArray()) {
            currentLine.append(c);
            if (currentLine.length() >= maxCharsPerLine || c == '\n') {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.toArray(new String[0]);
    }

    private static class GeometricFigure {
        int col, row;
        String type;
        Color color;
        int size;

        GeometricFigure(int col, int row, String type, Color color, int size) {
            this.col = col;
            this.row = row;
            this.type = type;
            this.color = color;
            this.size = size;
        }

        Rectangle getBounds() {
            int worldX = col * size;
            int worldY = row * size;
            return new Rectangle(worldX - 4, worldY - 4, size + 8, size + 8);
        }

        String getType() {
            return type;
        }

        void draw(Graphics2D g2, GamePanel gp) {
            int worldX = col * gp.tileSize;
            int worldY = row * gp.tileSize;

            int screenX = gp.computeScreenX(worldX);
            int screenY = gp.computeScreenY(worldY);

            boolean inCamera = worldX + gp.tileSize > gp.player.worldX - gp.player.getScreenX() &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.getScreenX() &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.getScreenY() &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.getScreenY();

            if (inCamera) {
                Object oldAA = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(screenX + 6, screenY + 8, gp.tileSize - 12, gp.tileSize - 10);

                g2.setColor(color);
                switch (type) {
                    case "circle"   -> g2.fillOval(screenX + 4, screenY + 4, gp.tileSize - 8, gp.tileSize - 8);
                    case "square"   -> g2.fillRect(screenX + 6, screenY + 6, gp.tileSize - 12, gp.tileSize - 12);
                    case "star"     -> drawStar(g2, screenX, screenY, gp.tileSize, true);
                    case "triangle" -> drawTriangle(g2, screenX, screenY, gp.tileSize, true);
                    case "pentagon" -> drawPentagon(g2, screenX, screenY, gp.tileSize, true);
                }

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                switch (type) {
                    case "circle"   -> g2.drawOval(screenX + 4, screenY + 4, gp.tileSize - 8, gp.tileSize - 8);
                    case "square"   -> g2.drawRect(screenX + 6, screenY + 6, gp.tileSize - 12, gp.tileSize - 12);
                    case "star"     -> drawStar(g2, screenX, screenY, gp.tileSize, false);
                    case "triangle" -> drawTriangle(g2, screenX, screenY, gp.tileSize, false);
                    case "pentagon" -> drawPentagon(g2, screenX, screenY, gp.tileSize, false);
                }

                g2.setColor(Color.WHITE);
                g2.fillRect(screenX + 14, screenY + 12, 4, 4);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
            }
        }

        private void drawStar(Graphics2D g2, int x, int y, int size, boolean fill) {
            int cx = x + size / 2;
            int cy = y + size / 2;
            int[] xPoints = { cx, cx + 7, x + size - 4, cx + 9, cx + 14, cx, cx - 14, cx - 9, x + 4, cx - 7 };
            int[] yPoints = { y + 3, cy - 5, cy - 4, cy + 6, y + size - 4, cy + 11, y + size - 4, cy + 6, cy - 4, cy - 5 };

            if (fill) g2.fillPolygon(xPoints, yPoints, 10);
            else      g2.drawPolygon(xPoints, yPoints, 10);
        }

        private void drawTriangle(Graphics2D g2, int x, int y, int size, boolean fill) {
            int cx = x + size / 2;
            int[] xPoints = { cx, x + size - 6, x + 6 };
            int[] yPoints = { y + 6, y + size - 6, y + size - 6 };

            if (fill) g2.fillPolygon(xPoints, yPoints, 3);
            else      g2.drawPolygon(xPoints, yPoints, 3);
        }

        private void drawPentagon(Graphics2D g2, int x, int y, int size, boolean fill) {
            int cx = x + size / 2;
            int[] xPoints = { cx, x + size - 5, x + size - 11, x + 11, x + 5 };
            int[] yPoints = { y + 5, y + 19, y + size - 6, y + size - 6, y + 19 };

            if (fill) g2.fillPolygon(xPoints, yPoints, 5);
            else      g2.drawPolygon(xPoints, yPoints, 5);
        }
    }

    public boolean shouldReturnToGame(int delayMillis) {
        if (!isTestCompleted() || completionTime == 0) {
            return false;
        }
        return (System.currentTimeMillis() - completionTime) >= delayMillis;
    }
}