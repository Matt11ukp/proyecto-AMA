package unl.edu.ec.ama.engine.view.render;

import unl.edu.ec.ama.data.entity.User;
import unl.edu.ec.ama.engine.domain.entity.*;
import unl.edu.ec.ama.engine.domain.entity.objects.Item;
import unl.edu.ec.ama.engine.domain.user.UserProgressManager;
import unl.edu.ec.ama.engine.domain.user.UserRegistrationController;
import unl.edu.ec.ama.engine.domain.user.UserRegistrationForm;
import unl.edu.ec.ama.engine.domain.user.UserRepository;
import unl.edu.ec.ama.engine.domain.user.UserSelectionController;
import unl.edu.ec.ama.engine.domain.user.UserSelectionMenu;
import unl.edu.ec.ama.engine.view.core.GameLoop;
import unl.edu.ec.ama.engine.view.core.GameState;
import unl.edu.ec.ama.engine.view.core.IGameLoop;
import unl.edu.ec.ama.engine.view.core.Key;
import unl.edu.ec.ama.engine.view.core.Mouse;
import unl.edu.ec.ama.engine.view.sound.Sound;
import unl.edu.ec.ama.engine.view.sound.SoundName;
import unl.edu.ec.ama.engine.view.util.ImageGetter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class GamePanel extends JPanel implements IGameLoop {
    public final int originalTileSize = 16;
    public final int scale            = 3;
    public final int tileSize         = originalTileSize * scale;
    public final int maxScreenCol     = 30;
    public final int maxScreenRow     = 16;
    public final int screenWidth      = tileSize * maxScreenCol;
    public final int screenHeight     = tileSize * maxScreenRow;
    public final int maxWorldCol      = 30;
    public final int maxWorldRow      = 16;
    public final int worldWidth       = tileSize * maxWorldCol;
    public final int worldHeight      = tileSize * maxWorldRow;

    public  final TileManager    tileM    = new TileManager(this);    // 1
    public  final Key            keyH     = new Key(this);            // 2
    public  final CollisionCheck cChecker = new CollisionCheck(this); // 3
    public  final AssetSetter sett     = new AssetSetter(this);    // 4
    public  final Avatar         skins    = new Avatar(this);         // 5
    public  final Ui             ui       = new Ui(this);             // 6
    public  final EventHandler   eHandler = new EventHandler(this);   // 7
    public  final Player         player   = new Player(this, keyH);   // 8
    public  final EntityRenderer renderer = new EntityRenderer(this); // 9
    public Lighting lighting = new Lighting();

    // ── ENTIDADES ─────────────────────────────────────────────────────────────
    public Item[]   obj     = new Item[50];
    public Entity[] npc     = new Entity[10];
    public Entity[] monster = new Entity[10];

    private final GameLoop loop       = new GameLoop(this);         // 11

    // ── AUDIO ─────────────────────────────────────────────────────────────────
    private final Sound music      = new Sound();
    private final Sound soundEfect = new Sound();

    private Visual visualTest;
    private GameState   previousGameState = GameState.TITLE;
    public Verbal verbalTest;
    public Concentration concentrationTest;

    private User                      currentUser;
    private int                       loadingProgress = 0;
    private boolean                   usersLoaded     = false;
    private UserProgressManager       userProgressManager;
    private final UserRepository         userRepository         = new UserRepository();
    private final UserRegistrationForm   userRegistrationForm   = new UserRegistrationForm();
    private final UserRegistrationController userRegistrationController =
                                        new UserRegistrationController(userRegistrationForm);
    private final UserSelectionMenu      userSelectionMenu      = new UserSelectionMenu();
    private final UserSelectionController userSelectionController =
                                        new UserSelectionController(userSelectionMenu);

    // ── ESTADO ────────────────────────────────────────────────────────────────
    public GameState gameState = GameState.LOADING;

    // ── INPUT ─────────────────────────────────────────────────────────────────
    // Mouse en view (corregido — antes estaba en domain violando MVC)
    private final Mouse mouseH = new Mouse(this);

    // ── LISTAS DE RENDERIZADO ─────────────────────────────────────────────────
    private final ArrayList<Entity> entityList = new ArrayList<>();
    private final ArrayList<Item>   itemList   = new ArrayList<>();

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.setFocusable(true);

        // Creamos el objeto sin iniciar el test (startTest se llama en launchVisualTest)
        visualTest = new Visual(this);
        verbalTest = new Verbal(this, null);
        concentrationTest = new Concentration(this, null);
    }

    // =========================================================================
    // SETUP
    // =========================================================================
    public void setUpGame() {
        player.getPlayerImage();
        sett.setObject();
        sett.setNPC();
        sett.setMonster();

        userProgressManager = new UserProgressManager(
                userRepository,
                userSelectionMenu.getUsers(),
                skins,
                player);

        gameState = GameState.LOADING;
        playMusic(SoundName.MENU);
    }


    public void startGameThread() { loop.start(); }

    public void launchVisualTest() {
        previousGameState = gameState;      // memoriza dónde estaba el jugador
        visualTest.startTest();             // llama Test.startTest() → onStart()
        gameState = GameState.VISUAL;
        stopMusic();
        playMusic(SoundName.VISUAL);
    }

    public void launchVerbalTest() {
        previousGameState = GameState.PLAY;
        verbalTest.startTest();             // Llama a Test.startTest() -> onStart()
        gameState = GameState.VERBAL;
        stopMusic();
        playMusic(SoundName.VISUAL);        // O el sonido que prefieras para este test
    }

    public void launchConcentrationTest() {
        previousGameState = GameState.PLAY; // Vital para no bugear el portal al salir
        concentrationTest.startTest();
        gameState = GameState.CONCENTRATION;
        stopMusic();
        playMusic(SoundName.VISUAL); // O la música techno/arcade que prefieras
    }

    public void returnFromTest() {
        gameState = previousGameState;
        player.movementLocked = false;
        stopMusic();
        if (previousGameState == GameState.TITLE || previousGameState == GameState.USER_SELECTION) {
            playMusic(SoundName.MENU);
        } else {
            playMusic(SoundName.LOBBY);
        }
        ui.commandNumber = 0;
    }

    @Override
    public void update() {
        switch (gameState) {
            case LOADING        -> updateLoading();
            case USER_SELECTION -> { }
            case REGISTER       -> { }
            case VERBAL         -> updateVerbal();
            case VISUAL         -> updateVisual();
            case CONCENTRATION  -> updateConcentration();
            case PLAY           -> updatePlay();
            default             -> { }
        }
    }

    private void updateLoading() {
        if (!usersLoaded) {
            userSelectionMenu.setUsers(userRepository.loadUsers());
            usersLoaded = true;
        }
        loadingProgress = Math.min(loadingProgress + 1, 100);
        if (loadingProgress >= 100) {
            gameState = GameState.USER_SELECTION;
        }
    }

    private void updateVisual() {
        visualTest.update();

        if (visualTest.isTestCompleted() && visualTest.shouldReturnToGame(2000)) {
            returnFromTest();
        }
        if (visualTest.shouldReturnToGame(2000)) {
            gameState = GameState.PLAY;
            player.setWorldY(5 * tileSize);
            eHandler.setPortalEvent(false);
        }
    }

    private void updateVerbal() {
        verbalTest.update();
        player.update();

        // Añade estas dos líneas para que el mundo y los demás personajes sigan vivos y moviéndose:
        for (Entity e : npc) { if (e != null) e.update(); }
        updateMonsters();

        // Si ya terminó y pasaron 2 segundos, regresamos al juego automáticamente
        if (verbalTest.isTestCompleted() && verbalTest.shouldReturnToGame(2000)) {
            returnFromTest();
        }
    }

    private void updateConcentration() {
        concentrationTest.update();
        player.update(); // Necesario para que el jugador lea las teclas A/D o Izq/Der

        // Retorno automático tras 2 segundos de ver la victoria
        if (concentrationTest.isTestCompleted() && concentrationTest.shouldReturnToGame(2000)) {
            player.setWorldX(21 * tileSize);
            player.setWorldY(5 * tileSize); // Regresar al mapa un paso abajo del portal
            eHandler.setPortalEvent(false);
            returnFromTest();
        }
    }

    private void updatePlay() {
        eHandler.checkEvent();
        player.update();
        for (Entity e : npc) { if (e != null) e.update(); }
        updateMonsters();
    }

    private void updateWinShow() {
        player.incrementWinCounter();
        if (player.getWinCounter() > 280) {
            gameState = GameState.WIN;
            player.setWinCounter(0);
        }
    }

    private void updateMonsters() {
        for (Entity entity : monster) {
            if (entity != null){
                entity.update();
            }
        }
    }

    public void retry() {
        player.setDefaultValues();
        player.getPlayerImage();
        sett.setObject();
        sett.setNPC();
        sett.setMonster();
        applyCurrentUserProgress();
        gameState = GameState.PLAY;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        long drawStart = 0;
        if (keyH.checkDrawTime) drawStart = System.nanoTime();

        renderFrame(g2);

        if (keyH.checkDrawTime) {
            long passed = System.nanoTime() - drawStart;
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
            g2.drawString("Draw: " + passed + "ns", 10, 400);
        }
        g2.dispose();
    }

    private boolean isMenuState() {
        return switch (gameState) {
            // Dejamos VISUAL como estaba originalmente (sin Concentration)
            case TITLE, SKIN_SELECTION, SKIN_HAIR_SELECTION,
                 SKIN_SHIRT_SELECTION, SKIN_EYES_SELECTION,
                 LOADING, USER_SELECTION, REGISTER, VISUAL -> true;
            default -> false;
        };
    }

    private void renderFrame(Graphics2D g2) {

        // ── 1. SALA AISLADA PARA LA PRUEBA VERBAL (NUEVO) ──────────────────────────
        // Se ejecuta PRIMERO. Dibuja la sala digital, únicamente a tu jugador adentro
        // y el cuadro de texto. Al hacer "return", evita que se dibuje el mapa normal.
        if (gameState == GameState.VERBAL) {
            verbalTest.drawArena(g2);       // Fondo cyber y figuras flotantes
            renderer.draw(player, g2);      // Tu personaje caminando dentro
            verbalTest.drawTextBox(g2);     // Interfaz futurista de texto
            return;
        }
        if (gameState == GameState.CONCENTRATION) {
            concentrationTest.drawArena(g2); // 1. Dibuja la lluvia digital y objetos
            renderer.draw(player, g2);       // 2. Dibuja a tu personaje esquivando
            return;
        }
        // ── 2. INTERFAZ DE MENÚS Y PRUEBA VISUAL ──────────────────────────────────
        if (isMenuState()) {
            ui.draw(g2);
            return;
        }

        // ── 3. DIBUJO NORMAL DEL MUNDO (Bosque, ítems, NPCs, etc.) ────────────────
        tileM.draw(g2);
        collectEntities();
        entityList.sort(Comparator.comparingInt(Entity::getWorldY));
        for (Entity e : entityList) {
            if (e instanceof Player p) renderer.draw(p, g2);
            else                        renderer.draw(e, g2);
        }
        entityList.clear();
        lighting.drawPlayerLighting(g2, this);
        ui.draw(g2);
        collectItems();
        itemList.sort(Comparator.comparingInt(Item::getWorldY));
        for (Item item : itemList) drawItem(item, g2);
        itemList.clear();

        // (¡AQUÍ BORRAMOS EL IF VIEJO DE VERBAL QUE ESTABA AL FINAL!)
    }
    private void collectEntities() {
        entityList.add(player);
        for (Entity e : npc)     { if (e != null) entityList.add(e); }
        for (Entity e : monster) { if (e != null) entityList.add(e); }
    }

    private void collectItems() {
        for (Item item : obj) { if (item != null) itemList.add(item); }
    }

    private void drawItem(Item item, Graphics2D g2) {
        int sx = computeScreenX(item.getWorldX());
        int sy = computeScreenY(item.getWorldY());
        BufferedImage img = ImageGetter.getObjects()[item.getType().getIndex()];
        if (img != null) g2.drawImage(img, sx, sy, null);
    }

    public int computeScreenX(int worldX) {
        int x = worldX - player.getWorldX() + player.getScreenX();
        if (player.getScreenX() > player.getWorldX()) x = worldX;
        if (screenWidth - player.getScreenX() > worldWidth - player.getWorldX())
            x = screenWidth - (worldWidth - worldX);
        return x;
    }

    public int computeScreenY(int worldY) {
        int y = worldY - player.getWorldY() + player.getScreenY();
        if (player.getScreenY() > player.getWorldY()) y = worldY;
        if (screenHeight - player.getScreenY() > worldHeight - player.getWorldY())
            y = screenHeight - (worldHeight - worldY);
        return y;
    }

    public Visual                    getVisualTest()                   { return visualTest; }
    public User                      getCurrentUser()                  { return currentUser; }
    public void                      setCurrentUser(User user)         { this.currentUser = user; }
    public void                      updateCurrentUserProgress()       { userProgressManager.saveProgress(currentUser); }
    public void                      applyCurrentUserProgress()        { userProgressManager.applyProgress(currentUser); }
    public UserSelectionMenu         getUserSelectionMenu()            { return userSelectionMenu; }
    public UserSelectionController   getUserSelectionController()      { return userSelectionController; }
    public UserRegistrationForm      getUserRegistrationForm()         { return userRegistrationForm; }
    public UserRegistrationController getUserRegistrationController()  { return userRegistrationController; }
    public int                       getLoadingProgress()              { return loadingProgress; }
    public void                      setLoadingProgress(int v)         { this.loadingProgress = v; }

    public void addUserToSelection(User user) {
        userSelectionMenu.addUser(user);
        userProgressManager.saveProgress(user);
    }
    public void selectLastCreatedUser() { userSelectionMenu.selectLastUser(); }
    public void deleteSelectedUser() {
        userSelectionMenu.removeSelectedUser();
        userRepository.saveUsers(userSelectionMenu.getUsers());
        currentUser = null;
    }

    public void playMusic(SoundName name) { music.setFile(name); music.play(); music.loop(); }
    public void stopMusic()               { music.stop(); }
    public void playSE(SoundName name)    { soundEfect.setFile(name); soundEfect.play(); }
}
