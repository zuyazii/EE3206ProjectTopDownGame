package main;

import entity.ENEMY_Boss01;
import entity.Enemy;
import entity.Entity;
import entity.Player;
import event.DoorEvent;
import event.EventObject;
import event.PortalEvent;
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
    public final int gameOverState = 5;


    // --- Transition Fields ---
    private boolean transitionActive = false;
    private long transitionStartTime;
    private long transitionDuration;
    private int nextMap;
    private boolean mapChanged = false;  // to track when the map switch occurs

    public boolean portalNotYetAdded = true;

    // --- Event Objects ---
    public List<EventObject> eventObjects = new ArrayList<>();
    public DoorEvent currentDoorEvent = null;
    public event.PortalEvent currentPortalEvent;

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
        assetSetter.setNPC(currentMap);
        assetSetter.setObject(currentMap);
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

            // Process door events.
            if (currentDoorEvent == null) {
                for (EventObject event : eventObjects) {
                    if (event instanceof DoorEvent) {
                        DoorEvent door = (DoorEvent) event;
                        // Only trigger if the door is not already triggered and collision is detected.
                        if (!door.isTriggered() && door.checkCollision(this)) {
                            door.triggerEvent(this);
                            currentDoorEvent = door;
                            break;  // Trigger one door event at a time.
                        }
                    } else {
                        if (event.checkCollision(this)) {
                            event.triggerEvent(this);
                        }
                    }
                }
            } else {
                // If a door event is active, check if the player is still colliding.
                if (!currentDoorEvent.checkCollision(this)) {
                    // Player left this door; clear the active door event.
                    currentDoorEvent = null;
                }
            }

            if (currentMap == 2 && npc[2][0] == null && portalNotYetAdded) {
                System.out.println("Adding portal event");
                PortalEvent portal = new PortalEvent(35 * tileSize, 19 * tileSize, 3*tileSize, 3*tileSize, 0, this);
                portal.promptMessage = "Teleport back to the castle?";
                addEventObject(portal);
                System.out.println("Added portal event");
                portalNotYetAdded = false;
            }

            // Update NPCs.
            for (int i = 0; i < npc[currentMap].length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                    
                    // Additional check for boss defeat
                    if (npc[currentMap][i] instanceof ENEMY_Boss01 && npc[currentMap][i].isBeatened) {
                        // This will ensure door events are updated
                        for (EventObject event : eventObjects) {
                            if (event instanceof DoorEvent) {
                                DoorEvent door = (DoorEvent) event;
                                if (door.getNextMap() == 2) {
                                    door.promptMessage = "Enter the forest..?";
                                }
                            }
                        }
                    }
                }
            }

            for (EventObject e : eventObjects) {
                if (e instanceof PortalEvent) {
                    ((PortalEvent)e).update();
                }
            }

        // At the end, reset door events for which collision is no longer occurring.
        // This ensures that door events can re-trigger upon re-entry.
        for (EventObject event : eventObjects) {
            if (event instanceof DoorEvent) {
                DoorEvent door = (DoorEvent) event;
                if (door.isTriggered() && !door.checkCollision(this)) {
                    door.resetTriggered();
                }
            }

            if (event instanceof PortalEvent) {
                PortalEvent portal = (PortalEvent) event;
                if (portal.isTriggered() && !portal.checkCollision(this)) {
                    portal.resetTriggered();
                }
            }
        }

        // Process fade transition.
        if (transitionActive) {
            long elapsed = System.currentTimeMillis() - transitionStartTime;
            long halfDuration = transitionDuration / 2;

            if (elapsed > halfDuration && !mapChanged) {
                completeTransition();
                mapChanged = true;
            }
            if (elapsed >= transitionDuration) {
                transitionActive = false;
            }
        }

        if (gameState == pauseState) {
            // Pause state logic.
        
        }
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
            g2d.setColor(new Color(0, 0, 0, 255));
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
        if (!transitionActive) {
            System.out.println("Starting transition to map " + nextMap);
            this.nextMap = nextMap;
            transitionActive = true;
            transitionDuration = duration;
            transitionStartTime = System.currentTimeMillis();
            mapChanged = false;
        }
    }

    // Once transition time has passed, complete the map change.
    private void completeTransition() {
        eventObjects.clear();

        currentMap = nextMap;
        assetSetter.setNPC(currentMap);
        assetSetter.setObject(currentMap);

        // Set player's new start position based on the map.
        // For example, for map 1, you might want to position the player at (5, 5) tiles.
        switch (currentMap) {
            case 0:
                player.worldx = tileSize * 25;
                player.worldy = tileSize * 28;
                player.direction = "up";
                break;
            case 1:
                player.worldx = tileSize * 13;
                player.worldy = tileSize * 17;
                player.direction = "down";
                break;
            case 2:
                player.worldx = tileSize * 10;
                player.worldy = tileSize * 20;
                player.direction = "down";
                break;
        }
        assetSetter.setNPC(currentMap);
        assetSetter.setObject(currentMap);
        
        System.out.println("Transition complete! Player at: " + 
            player.worldx + "," + player.worldy);
        
        mapChanged = true;
        transitionActive = false;
    }

}