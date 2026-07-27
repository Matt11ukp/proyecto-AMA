package unl.edu.ec.ama.engine.domain.user;

import java.awt.event.KeyEvent;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UserSelectionController {

    private final UserSelectionMenu menu;

    public UserSelectionController(UserSelectionMenu menu) {
        this.menu = menu;
    }

    public void processKey(KeyEvent event) {
        int code = event.getKeyCode();

        if (code == KeyEvent.VK_RIGHT) {
            menu.selectNext();
        } else if (code == KeyEvent.VK_LEFT) {
            menu.selectPrevious();
        }
    }
}