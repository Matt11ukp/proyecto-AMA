package unl.edu.ec.ama.engine.view.core;

import javax.swing.*;
import unl.edu.ec.ama.engine.view.render.GamePanel;


/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Game{
    public static void main(String[] args) {
        // Crear una ventana
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("AMA");
        GamePanel gamePanel = new GamePanel();

        window.add(gamePanel);
        window.pack();// la ventana encajara en los estandares que le hemos dado
        window.setLocationRelativeTo(null); // null indica que siempre se ejecutara al centro
        window.setVisible(true);
        unl.edu.ec.ama.engine.domain.entity.Test.warmUpConexionServidor();
        gamePanel.setUpGame();
        gamePanel.startGameThread();
    }
}
