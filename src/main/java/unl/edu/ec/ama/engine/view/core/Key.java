package unl.edu.ec.ama.engine.view.core;

import unl.edu.ec.ama.data.entity.User;
import unl.edu.ec.ama.engine.view.render.GamePanel;
import unl.edu.ec.ama.engine.view.sound.SoundName;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class Key implements KeyListener {

    GamePanel gp;

    public boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed, spacePressed, enterPressed;

    // Debug
    public Boolean checkDrawTime = false;

    public Key(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyPressed(KeyEvent e) {

       int code = e.getKeyCode();
if(gp.gameState == GameState.USER_SELECTION){

    if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
        gp.getUserSelectionMenu().selectNext();
        gp.playSE(SoundName.SLIDE);
    }

    if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
        gp.getUserSelectionMenu().selectPrevious();
        gp.playSE(SoundName.SLIDE);
    }
if(code == KeyEvent.VK_DELETE || code == KeyEvent.VK_BACK_SPACE){
    if(!gp.getUserSelectionMenu().isNewUserSelected()){
        gp.deleteSelectedUser();
        gp.playSE(SoundName.SELECT);
    }
}
    if(code == KeyEvent.VK_ENTER){
        gp.playSE(SoundName.SELECT);

        if(gp.getUserSelectionMenu().isNewUserSelected()){
            gp.gameState = GameState.REGISTER;
        } else {
         User selectedUser = gp.getUserSelectionMenu().getSelectedUser();
gp.setCurrentUser(selectedUser);
gp.applyCurrentUserProgress();
gp.gameState = GameState.TITLE;
        }
    }

    return;

}
if(gp.gameState == GameState.REGISTER){

    if(code == KeyEvent.VK_ENTER){
        if(gp.getUserRegistrationForm().isComplete()){
            User user = gp.getUserRegistrationForm().createUser();

            gp.setCurrentUser(user);
            gp.addUserToSelection(user);
            gp.selectLastCreatedUser();
            gp.getUserRegistrationForm().reset();

            gp.gameState = GameState.USER_SELECTION;
        }

        return;
    }

    gp.getUserRegistrationController().processKey(e);
    return;
}
        // MENÚ PRINCIPAL

        if (gp.gameState == GameState.TITLE) {

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.ui.commandNumber--;
                gp.playSE(SoundName.SLIDE);

              if (gp.ui.commandNumber < 0) {
    gp.ui.commandNumber = 3;
}
            }

            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.ui.commandNumber++;
                gp.playSE(SoundName.SLIDE);

              if (gp.ui.commandNumber > 3) {
    gp.ui.commandNumber = 0;
}
            }

           if (code == KeyEvent.VK_ENTER) {
    gp.playSE(SoundName.SELECT);

 if (gp.ui.commandNumber == 0) {
     gp.player.getPlayerImage();
     if (gp.getCurrentUser() != null) {
         gp.npc[0].getDialogues()[0] = "Hola " + gp.getCurrentUser().getName() + ", bienvenido a AMA (Presione Enter para continuar)";
     }
     gp.gameState = GameState.DIALOGUE;

     gp.npc[0].setDialogueIndex(0);
     gp.ui.currentDialogue = gp.npc[0].getDialogues()[gp.npc[0].getDialogueIndex()];
     gp.stopMusic();
     gp.playMusic(SoundName.LOBBY);
     return;
}

                if (gp.ui.commandNumber == 1) {
                    gp.gameState = GameState.SKIN_SELECTION;
                    gp.ui.commandNumber = 0;
                    gp.ui.commandNumber1 = 0;
                }

              if (gp.ui.commandNumber == 2) {
    gp.updateCurrentUserProgress();
    gp.gameState = GameState.USER_SELECTION;
    gp.ui.commandNumber = 0;
}

if (gp.ui.commandNumber == 3) {
    System.exit(0);
}
            }
        }
        
        // PAUSA
        
        else if (gp.gameState == GameState.PAUSE) {

            if (code == KeyEvent.VK_ESCAPE) {
                gp.gameState = GameState.PLAY;
            }

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                gp.playSE(SoundName.SLIDE);
                gp.ui.commandNumber--;

                if (gp.ui.commandNumber < 0) {
                    gp.ui.commandNumber = 2;
                }
            }

            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                gp.playSE(SoundName.SLIDE);
                gp.ui.commandNumber++;

                if (gp.ui.commandNumber > 2) {
                    gp.ui.commandNumber = 0;
                }
            }

            if (code == KeyEvent.VK_ENTER) {
                gp.playSE(SoundName.SELECT);

                if (gp.ui.commandNumber == 0) {
                    gp.gameState = GameState.PLAY;
                }

                if (gp.ui.commandNumber == 1) {
                    gp.gameState = GameState.TITLE;
                    gp.stopMusic();
                    gp.playMusic(SoundName.MENU);
                }

                if (gp.ui.commandNumber == 2) {
                    System.exit(0);
                }
            }
        }


        // PERSONALIZACIÓN PRINCIPAL
     
        else if (gp.gameState == GameState.SKIN_SELECTION) {

            // Menú izquierdo / género / volver
            if (gp.ui.commandNumber1 != 1) {

                // ARRIBA
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.ui.commandNumber == 0) {
                        gp.ui.commandNumber = 5;
                    } else if (gp.ui.commandNumber == 3 || gp.ui.commandNumber == 4) {
                        gp.ui.commandNumber = 2;
                    } else {
                        gp.ui.commandNumber--;
                    }
                }

                // ABAJO
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.ui.commandNumber == 2) {
                        gp.ui.commandNumber = 3;
                    } else if (gp.ui.commandNumber == 3 || gp.ui.commandNumber == 4) {
                        gp.ui.commandNumber = 5;
                    } else if (gp.ui.commandNumber == 5) {
                        gp.ui.commandNumber = 0;
                    } else {
                        gp.ui.commandNumber++;
                    }
                }

                // IZQUIERDA: de hombre a mujer
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.ui.commandNumber == 4) {
                        gp.ui.commandNumber = 3;
                    }
                }

                // DERECHA
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.ui.commandNumber == 3) {
                        gp.ui.commandNumber = 4;
                    } else {
                        gp.ui.commandNumber1 = 1;
                        gp.skins.setSkinI(gp.skins.getActualSkin());
                    }
                }

                // ENTER
                if (code == KeyEvent.VK_ENTER) {
                    gp.playSE(SoundName.SELECT);

                    if (gp.ui.commandNumber == 0) {
                        gp.gameState = GameState.SKIN_HAIR_SELECTION;
                    }

                    if (gp.ui.commandNumber == 1) {
                        gp.gameState = GameState.SKIN_SHIRT_SELECTION;
                    }

                    if (gp.ui.commandNumber == 2) {
                        gp.gameState = GameState.SKIN_EYES_SELECTION;
                    }

                    if (gp.ui.commandNumber == 3) {
                        gp.skins.setGender(true);
                    }

                    if (gp.ui.commandNumber == 4) {
                        gp.skins.setGender(false);
                    }

                    if (gp.ui.commandNumber == 5) {
    gp.updateCurrentUserProgress();

    gp.gameState = GameState.TITLE;
    gp.ui.commandNumber = 0;
    gp.ui.commandNumber1 = 0;
}
                }
            }

            // Selector de piel / color
            else {

                int columnas = 4;
                int totalPieles = gp.skins.image.getSkinBlocks().length;

                // IZQUIERDA
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.skins.getSkinI() % columnas == 0) {
                        gp.ui.commandNumber1 = 0;
                        gp.ui.commandNumber = 0;
                    } else {
                        gp.skins.setSkinI(gp.skins.getSkinI() - 1);
                    }
                }

                // DERECHA
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.skins.getSkinI() % columnas != columnas - 1 && gp.skins.getSkinI() + 1 < totalPieles) {
                        gp.skins.setSkinI(gp.skins.getSkinI() + 1);
                    }
                }

                // ARRIBA
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.skins.getSkinI() - columnas >= 0) {
                        gp.skins.setSkinI(gp.skins.getSkinI() - columnas);
                    }
                }

                // ABAJO
                if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                    gp.playSE(SoundName.SLIDE);

                    if (gp.skins.getSkinI() + columnas < totalPieles) {
                        gp.skins.setSkinI(gp.skins.getSkinI() + columnas);
                    } else {
                        gp.ui.commandNumber1 = 0;
                        gp.ui.commandNumber = 5;
                    }
                }

                // SELECCIONAR PIEL
               if (code == KeyEvent.VK_ENTER) {
                    gp.playSE(SoundName.SELECT);
                    gp.skins.setActualSkin(gp.skins.getSkinI());
                    gp.updateCurrentUserProgress();
                }
            }
        }

      
        // CARRUSEL DE PELO    
        else if (gp.gameState == GameState.SKIN_HAIR_SELECTION) {

            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.playSE(SoundName.SLIDE);

                int maxHair = !gp.skins.isGender()
                        ? gp.skins.image.getHairsMale().length
                        : gp.skins.image.getHairsFemale().length;
                gp.skins.setActualHair(gp.skins.getActualHair() + 1);

                if (gp.skins.getActualHair() >= maxHair) {
                    gp.skins.setActualHair(0);
                }

                gp.skins.setActualHair(gp.skins.getActualHair() + 1);
            }

            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                gp.playSE(SoundName.SLIDE);

                int maxHair = !gp.skins.isGender()
                        ? gp.skins.image.getHairsMale().length
                        : gp.skins.image.getHairsFemale().length;

                gp.skins.setActualHair(gp.skins.getActualHair() - 1);

                if (gp.skins.getActualHair() < 0) {
                    gp.skins.setActualHair(maxHair - 1);
                }

                gp.skins.setHairI(gp.skins.getActualHair());
            }

         if (code == KeyEvent.VK_ENTER) {
    gp.playSE(SoundName.SELECT);
    gp.updateCurrentUserProgress();

    gp.gameState = GameState.SKIN_SELECTION;
    gp.ui.commandNumber = 0;
    gp.ui.commandNumber1 = 0;
}
        }

   
        // CARRUSEL DE CAMISA

        else if (gp.gameState == GameState.SKIN_SHIRT_SELECTION) {

            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.playSE(SoundName.SLIDE);

                gp.skins.setActualShirt(gp.skins.getActualShirt() + 1);

                if (gp.skins.getActualShirt() >= gp.skins.image.getShirts().length) {
                    gp.skins.setActualShirt(0);
                }

                gp.skins.setShirtI(gp.skins.getActualShirt());
            }

            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                gp.playSE(SoundName.SLIDE);

                gp.skins.setActualShirt(gp.skins.getActualShirt() - 1);

                if (gp.skins.getActualShirt() < 0) {
                    gp.skins.setActualShirt(gp.skins.image.getShirts().length - 1);
                }

                gp.skins.setShirtI(gp.skins.getActualShirt());
            }

           if (code == KeyEvent.VK_ENTER) {
    gp.playSE(SoundName.SELECT);
    gp.updateCurrentUserProgress();

    gp.gameState = GameState.SKIN_SELECTION;
    gp.ui.commandNumber = 1;
    gp.ui.commandNumber1 = 0;
}
        }

       
        // CARRUSEL DE OJOS

        else if (gp.gameState == GameState.SKIN_EYES_SELECTION) {

            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                gp.playSE(SoundName.SLIDE);

                gp.skins.setActualEye(gp.skins.getActualEye() + 1);

                if (gp.skins.getActualEye() >= gp.skins.image.getEyes().length) {
                    gp.skins.setActualEye(0);
                }

                gp.skins.setEyesI(gp.skins.getActualEye());
            }

            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                gp.playSE(SoundName.SLIDE);

                gp.skins.setActualEye(gp.skins.getActualEye() - 1);

                if (gp.skins.getActualEye() < 0) {
                    gp.skins.setActualEye(gp.skins.image.getEyes().length - 1);
                }

                gp.skins.setEyesI(gp.skins.getActualEye());
            }

           if (code == KeyEvent.VK_ENTER) {
    gp.playSE(SoundName.SELECT);
    gp.updateCurrentUserProgress();

    gp.gameState = GameState.SKIN_SELECTION;
    gp.ui.commandNumber = 2;
    gp.ui.commandNumber1 = 0;
}
        }

        
        // JUEGO
else if (gp.gameState == GameState.VISUAL) {

    if (code == KeyEvent.VK_ENTER) {
        // ENTER enciende/apaga la linterna
        gp.getVisualTest().toggleFlashlight();
        gp.playSE(SoundName.SELECT);
    }
    if (code == KeyEvent.VK_ESCAPE) {
        // ESC: salida anticipada — vuelve al estado anterior (TITLE o PLAY)
        gp.returnFromTest();
    }
}
        // JUEGO Y PRUEBA VERBAL UNIFICADOS
        else if (gp.gameState == GameState.PLAY || gp.gameState == GameState.VERBAL || gp.gameState == GameState.CONCENTRATION) {

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                upPressed = true;
            }

            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                downPressed = true;
            }

            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }

            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            }

            if (code == KeyEvent.VK_SHIFT) {
                shiftPressed = true;
            }

            if (code == KeyEvent.VK_SPACE) {
                spacePressed = true;
            }

            if (code == KeyEvent.VK_T) {
                checkDrawTime = !checkDrawTime;
            }

            if (code == KeyEvent.VK_ESCAPE) {
                if (gp.gameState == GameState.VERBAL || gp.gameState == GameState.CONCENTRATION) {
                    gp.returnFromTest(); // Salida anticipada con ESC
                } else {
                    gp.gameState = GameState.PAUSE;
                }
            }

            if (code == KeyEvent.VK_ENTER) {
                // Si la prueba ya terminó, presionar ENTER te regresa al mapa al instante
                if ((gp.gameState == GameState.VERBAL && gp.verbalTest.isTestCompleted()) ||
                        (gp.gameState == GameState.CONCENTRATION && gp.concentrationTest.isTestCompleted())) {
                    gp.returnFromTest();
                    gp.playSE(SoundName.SELECT);
                } else {
                    enterPressed = true;
                }
            }
        }

     
        // DIÁLOGO

        if (gp.gameState == GameState.DIALOGUE) {
            if (code == KeyEvent.VK_ENTER) {

                if (gp.eHandler.isPortalEvent()) {
                    // Evaluamos cuál de los dos portales se pisó para lanzar la prueba correcta
                    switch (gp.eHandler.activePortalType) {
                        case "VISUAL" -> {
                            gp.launchVisualTest();
                            gp.gameState = GameState.VISUAL;
                        }
                        case "VERBAL" -> {
                            gp.launchVerbalTest();
                            gp.gameState = GameState.VERBAL;
                        }
                        case "CONCENTRATION" -> {
                            gp.launchConcentrationTest();
                            gp.gameState = GameState.CONCENTRATION;
                        }
                    }
                    gp.eHandler.setPortalEvent(false);
                } else {
                    gp.npc[0].setDialogueIndex(gp.npc[0].getDialogueIndex() + 1);

                    if (gp.npc[0].getDialogues()[gp.npc[0].getDialogueIndex()] != null) {
                        // Si hay texto, lo actualizamos en la pantalla
                        gp.ui.currentDialogue = gp.npc[0].getDialogues()[gp.npc[0].getDialogueIndex()];
                        gp.playSE(SoundName.SELECT);
                    } else {
                        gp.npc[0].setDialogueIndex(0);
                        gp.gameState = GameState.PLAY;
                        gp.playSE(SoundName.SELECT);
                    }
                }
            }
            if (code == KeyEvent.VK_ESCAPE) {
                if (gp.eHandler.isPortalEvent()) {
                    gp.eHandler.setPortalEvent(false);
                    gp.gameState = GameState.PLAY;
                    gp.player.setWorldY(gp.player.getWorldY() + gp.tileSize);
                } else {

                    gp.gameState = GameState.PLAY;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }

        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }

        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }

        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = false;
        }

        if (code == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

   @Override
public void keyTyped(KeyEvent e) {
    if (gp.gameState == GameState.REGISTER) {
        gp.getUserRegistrationController().processKey(e);
    }
}

}