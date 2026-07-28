package unl.edu.ec.ama.engine.domain.entity;

import unl.edu.ec.ama.engine.view.core.GameState;
import unl.edu.ec.ama.engine.view.render.GamePanel;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

import java.awt.*;

public class EventHandler {

    private final GamePanel gp;
    private final EventRect[][] eventRect;
    private boolean isPortalEvent = false;
    private int previousEventX;
    private int previousEventY;
    private boolean canTouchEvent = true;

    // Variable para identificar qué portal se pisó ("VISUAL" o "VERBAL")
    public String activePortalType = "";

    public EventHandler(GamePanel gp) {
        this.gp = gp;

        eventRect = new EventRect[gp.maxWorldCol][gp.maxWorldRow];
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                eventRect[col][row] = new EventRect(23, 23, 2, 2);
                eventRect[col][row].x = eventRect[col][row].getEventRectDefaultX();
                eventRect[col][row].y = eventRect[col][row].getEventRectDefaultY();
            }
        }
    }

    public void checkEvent() {

        // Evitamos que se activen eventos si ya estamos en diálogo, visual o verbal
        if (gp.gameState == GameState.DIALOGUE ||
                gp.gameState == GameState.VISUAL ||
                gp.gameState == GameState.VERBAL) return;

        // --- PRUEBA VISUAL (Casilla 15, 4) ---
        if (hit(gp.player, 15, 4, "any")) {
            gp.gameState = GameState.DIALOGUE;
            gp.ui.currentDialogue = "La prueba visual te espera.\n¿Deseas entrar?";
            isPortalEvent = true;
            activePortalType = "VISUAL";
        }

        // --- PRUEBA VERBAL (Ejemplo: Casilla 18, 4) ---
        if (hit(gp.player, 18, 4, "any")) {
            gp.gameState = GameState.DIALOGUE;
            gp.ui.currentDialogue = "La prueba verbal (El Eco de las Figuras) te espera.\n¿Deseas entrar?";
            isPortalEvent = true;
            activePortalType = "VERBAL";
        }

        // --- PORTAL CONCENTRACIÓN (Casilla 21, 4) ---
        if (hit(gp.player, 12, 4, "any")) { // O como sea que llames a tu función de colisión de evento
            activePortalType = "CONCENTRATION";
            setPortalEvent(true);
            gp.gameState = GameState.DIALOGUE;
            gp.ui.currentDialogue = "¿Deseas entrar a la prueba de CONCENTRACIÓN?";
        }
    }

    private void resetCooldownIfFarEnough() {
        int dx = Math.abs(gp.player.getWorldX() - previousEventX);
        int dy = Math.abs(gp.player.getWorldY() - previousEventY);
        if (Math.max(dx, dy) > gp.tileSize) {
            canTouchEvent = true;
        }
    }

    public boolean hit(Entity entity, int col, int row, String reqDirection) {
        boolean hit = false;

        Rectangle eArea = entity.getSolidArea();
        EventRect eRect = eventRect[col][row];

        eArea.x = entity.getWorldX() + entity.getSolidAreaDefaultX();
        eArea.y = entity.getWorldY() + entity.getSolidAreaDefaultY();
        eRect.x = col * gp.tileSize + eRect.getEventRectDefaultX();
        eRect.y = row * gp.tileSize + eRect.getEventRectDefaultY();

        if (eArea.intersects(eRect) && !eRect.isEventDone()) {
            String dir = entity.getDirection();
            if (dir.equals(reqDirection) || reqDirection.equals("any")) {
                hit = true;
                previousEventX = entity.getWorldX();
                previousEventY = entity.getWorldY();
            }
        }

        eArea.x = entity.getSolidAreaDefaultX();
        eArea.y = entity.getSolidAreaDefaultY();
        eRect.x = eRect.getEventRectDefaultX();
        eRect.y = eRect.getEventRectDefaultY();

        return hit;
    }

    public boolean isPortalEvent() {
        return isPortalEvent;
    }

    public void setPortalEvent(boolean portalEvent) {
        isPortalEvent = portalEvent;
    }
}
