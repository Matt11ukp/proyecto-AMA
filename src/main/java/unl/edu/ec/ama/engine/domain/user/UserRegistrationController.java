package unl.edu.ec.ama.engine.domain.user;

import java.awt.event.KeyEvent;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class UserRegistrationController {

    private final UserRegistrationForm form;

    public UserRegistrationController(UserRegistrationForm form) {
        this.form = form;
    }

    public boolean processKey(KeyEvent event) {
        int code = event.getKeyCode();

        if (code == KeyEvent.VK_UP) {
            form.selectPreviousField();
            return false;
        }

        if (code == KeyEvent.VK_DOWN) {
            form.selectNextField();
            return false;
        }

        if (code == KeyEvent.VK_BACK_SPACE) {
            form.deleteCharacter();
            return false;
        }

        if (code == KeyEvent.VK_ENTER) {
            return form.isComplete();
        }

        processCharacter(event);
        return false;
    }

private void processCharacter(KeyEvent event) {
    if (event.getID() != KeyEvent.KEY_TYPED) {
        return;
    }

    char character = event.getKeyChar();

    if (!Character.isISOControl(character)) {
        form.addCharacter(character);
    }
}
    }
