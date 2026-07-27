package unl.edu.ec.ama.engine.view.core;

import unl.edu.ec.ama.engine.view.render.GamePanel;
import unl.edu.ec.ama.engine.domain.entity.Visual;
import unl.edu.ec.ama.engine.view.sound.SoundName;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Mouse implements MouseListener, MouseMotionListener {

    private final GamePanel gp;
    private boolean draggingFlashlight;
    private static final int FLASHLIGHT_SIZE = 80;

    public Mouse(GamePanel gp) {
        this.gp = gp;
        draggingFlashlight = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gp.gameState != GameState.VISUAL) return;

        Visual visual = gp.getVisualTest();
        if (visual.isIntroActive()) return;

        if (visual.isFlashlightClicked(e.getX(), e.getY(), FLASHLIGHT_SIZE)) {
            draggingFlashlight = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (gp.gameState != GameState.VISUAL) return;

        if (draggingFlashlight) {
            gp.getVisualTest().moveFlashlight(e.getX(), e.getY(), FLASHLIGHT_SIZE);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (gp.gameState != GameState.VISUAL) {
            draggingFlashlight = false;
            return;
        }

        if (draggingFlashlight) {
            gp.getVisualTest().moveFlashlight(e.getX(), e.getY(), FLASHLIGHT_SIZE);
        }

        draggingFlashlight = false; // Terminamos de arrastrar
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gp.gameState != GameState.VISUAL) return;

        Visual visual = gp.getVisualTest();
        if (visual.isIntroActive()) return;

        // Si el jugador hace un clic limpio en la pantalla (sin arrastrar)
        // y NO es sobre la linterna, entonces sí evaluamos si le dio a una gema o a un hongo.
        if (!visual.isFlashlightClicked(e.getX(), e.getY(), FLASHLIGHT_SIZE)) {
            checkGemClick(e);
        }
    }

    private void checkGemClick(MouseEvent e) {
        boolean correct = gp.getVisualTest().checkClick(e.getX(), e.getY());
        gp.playSE(correct ? SoundName.SELECT : SoundName.SLIDE);
    }

    @Override public void mouseMoved(MouseEvent e)     { }
    @Override public void mouseEntered(MouseEvent e)   { }
    @Override public void mouseExited(MouseEvent e)    { }
}
