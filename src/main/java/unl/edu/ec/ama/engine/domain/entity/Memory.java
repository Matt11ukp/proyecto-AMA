package unl.edu.ec.ama.engine.domain.entity;

import unl.edu.ec.ama.engine.view.render.GamePanel;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Memory extends Test {

    public Memory(GamePanel gp) {
        super(gp); // <-- ¡ESTO CONECTA TODO! Le pasa la pantalla a Test.java
        // ... el resto del código de tu memoria ...
    }

    @Override
    protected void onStart() {
        // TODO: inicializar recursos de la prueba Memory
    }

    @Override
    public void update() {
        // TODO: lógica de tick de la prueba Memory
        // Cuando el test termine: result = endTest(); ConsoleLogger.log(result);
    }
}
