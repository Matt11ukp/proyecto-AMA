package unl.edu.ec.ama.engine.view.core;

/**
 * @author Matias Romero, Freddy Ordoñez, Luis Armijos, Ezequiel Chamba, Arlette Quezada
 */

public class GameLoop implements Runnable {

    private static final int DEFAULT_FPS = 60;

    private final IGameLoop client;
    private final int fps;

    private Thread  gameThread;
    private boolean running = false;

    public GameLoop(IGameLoop client) {
        this(client, DEFAULT_FPS);
    }

    public GameLoop(IGameLoop client, int fps) {
        if (client == null) throw new IllegalArgumentException("client no puede ser null");
        if (fps <= 0)       throw new IllegalArgumentException("fps debe ser > 0, recibido: " + fps);
        this.client = client;
        this.fps    = fps;
    }

    public void start() {
        if (running) return;
        running    = true;
        gameThread = new Thread(this, "GameLoop-Thread");
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public void stop() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join(1_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        final double drawInterval = 1_000_000_000.0 / fps;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (running) {
            client.update();
            client.repaint();

            double remaining = (nextDrawTime - System.nanoTime()) / 1_000_000.0;
            if (remaining > 0) {
                try {
                    Thread.sleep((long) remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
            nextDrawTime += drawInterval;
        }
    }
}
