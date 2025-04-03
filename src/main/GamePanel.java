package main;

import entity.Entity;
import entity.Player;
import event.EventObject;
import object.SuperObject;
import tiles.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16;  // 16x16 tile
    final int scale = 4;

    public final int tileSize = originalTileSize * scale;

    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;

    public final int screenWidth = maxScreenCol * tileSize;
    public final int screenHeight = maxScreenRow * tileSize;

    // FPS
    int FPS = 60;

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
//    public final int worldWidth = tileSize * maxWorldCol;
//    public final int worldHeight = tileSize * maxWorldRow;
    public final int maxMap = 10;
    public int currentMap = 0;

    // SYSTEM
    TileManager tileManager = new TileManager(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public UI ui = new UI(this);
    Thread gameThread;
    public CollisionChecker collisionChecker = new CollisionChecker(this);
    public AssetSetter assetSetter = new AssetSetter(this);


    // ENTITY AND OBJECT
    public Player player = new Player(this, keyHandler);
    public SuperObject superObject[][] = new SuperObject[maxMap][10];
    public Entity npc[][] = new Entity[maxMap][10];

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int battleState = 4;

    // --- Transition Fields ---
    private boolean transitionActive = false;
    private long transitionStartTime;
    private long transitionDuration;
    private int nextMap;
    private boolean mapChanged = false;  // to track when the map switch occurs

    // --- Event Objects ---
    private List<EventObject> eventObjects = new ArrayList<>();

    // BATTLE
    public Battle battle;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true); // GamePanel can be "focused" to receive key input
        this.addKeyListener(keyHandler);
    }

    public void setupGame() {
        assetSetter.setObject(currentMap);
        assetSetter.setNPC(currentMap);
//        playMusic(0);
//        stopMusic();
        gameState = titleState;
    }

    public void startGameThread() {
        gameThread = new Thread(this); // passing GamePanel class to this thread's constructor and to instantiate a thread
        gameThread.start();
    }

    @Override
    public void run() {
        // If the FPS is 30, the program does this (UPDATE and DRAW) 30 times per second

        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            // 1. UPDATE: Update information such as character positions
            update();

            // 2. DRAW: Draw the screen with the updated information
            repaint();

            // FPS limiting
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // Convert that from nanoseconds to milliseconds

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                // Handle the interruption, or at least log it.
                e.printStackTrace();
                // Optionally, if you want to break out of the game loop:
                // break;
            }
        }
    }

    public void update() {

        if (gameState == playState) {
            player.update();

            // Process event objects.
            for (EventObject event : eventObjects) {
                if (event.checkCollision(this)) {
                    event.triggerEvent(this);
                }
            }

            // Update NPCs.
            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }
        }

        // Process fade transition.
        if (transitionActive) {
            long elapsed = System.currentTimeMillis() - transitionStartTime;
            long halfDuration = transitionDuration / 2;

            // At half time, switch the map if not already done.
            if (elapsed > halfDuration && !mapChanged) {
                completeTransition();
                mapChanged = true;
            }
            // End transition when total duration has passed.
            if (elapsed >= transitionDuration) {
                transitionActive = false;
            }
        }

        if (gameState == pauseState) {
            // Pause state logic.
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // TILE SCREEN
        if (gameState == titleState) {
            ui.draw(g2d);
        } else if (gameState == battleState) {
            ui.draw(g2d);
        }
        // OTHERS
        else {
            // TILE
            tileManager.draw(g2d);

            // NPC
            for (int i = 0; i < npc.length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].draw(g2d);
                }
            }

            player.draw(g2d);

            // OBJECT
            for (int i = 0; i < superObject.length; i++) {
                if (superObject[currentMap][i] != null) {
                    superObject[currentMap][i].draw(g2d, this);
                }
            }

            // Draw event objects (like doors).
            for (EventObject event : eventObjects) {
                event.draw(g2d, this);
            }

            ui.draw(g2d);
        }

        // Draw dark overlay during map transition.
        if (transitionActive) {
            g2d.setColor(new java.awt.Color(0, 0, 0, 255));
            g2d.fillRect(0, 0, screenWidth, screenHeight);
        }
        g2d.dispose();
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() { music.stop(); }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    // Called from AssetSetter to register an event object.
    public void addEventObject(EventObject event) {
        eventObjects.add(event);
    }

    public boolean isTransitionActive() {
        return transitionActive;
    }

    // Start a fade transition. Total duration should be 3000 ms.
    public void startTransition(int nextMap, long duration) {
        transitionActive = true;
        this.nextMap = nextMap;
        transitionDuration = duration;
        transitionStartTime = System.currentTimeMillis();
        mapChanged = false;
    }

    // Once transition time has passed, complete the map change.
    private void completeTransition() {
        eventObjects.clear();

        currentMap = nextMap;
        assetSetter.setObject(currentMap);
        assetSetter.setNPC(currentMap);

        // Set player's new start position based on the map.
        // For example, for map 1, you might want to position the player at (5, 5) tiles.
        switch (currentMap) {
            case 0:
                player.worldx = tileSize * 25;
                player.worldy = tileSize * 26;
                player.direction = "up";
                break;
            case 1:
                player.worldx = tileSize * 27;  // example starting x coordinate for map 1
                player.worldy = tileSize * 26;  // example starting y coordinate for map 1
                player.direction = "right";
                break;
            // Add additional cases for other maps as needed.
        }

        transitionActive = false;
    }

}
