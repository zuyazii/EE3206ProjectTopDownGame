package main;

import entity.Entity;
import entity.Player;
import object.SuperObject;
import tiles.TileManager;

import javax.swing.*;
import java.awt.*;

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

    // Fields for door transition
    public boolean doorTransitionActive = false;
    public long doorTransitionStartTime;
    public int doorNextMap;

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

            // Check door collisions:
            if (gameState == playState && !doorTransitionActive) {
                // Create the player's absolute collision rectangle.
                Rectangle playerRect = player.getCollisionArea();

                for (int i = 0; i < superObject[currentMap].length; i++) {
                    if (superObject[currentMap][i] != null && superObject[currentMap][i] instanceof object.OBJ_Door) {
                        System.out.println("test");
                        object.OBJ_Door door = (object.OBJ_Door) superObject[currentMap][i];
                        // Create door's absolute collision rectangle.
                        Rectangle doorRect = door.getCollisionArea();
//                        System.out.println("Door: " + doorRect.x + " " + doorRect.y + " " + doorRect.width + " " + doorRect.height);
//                        System.out.println("Player: " + playerRect.x + " " + playerRect.y + " " + playerRect.width + " " + playerRect.height);
                        if (playerRect.intersects(doorRect)) {
                            System.out.println("moving to next map");
                            startDoorTransition(door.nextmapNum);
                            break;  // Stop checking after triggering a door.
                        }
                    }
                }
            }

            // NPC updates, etc.
            for (int i = 0; i < npc.length; i++) {
                if (npc[currentMap][i] != null) {
                    npc[currentMap][i].update();
                }
            }
        }


        if (gameState == pauseState) {
            // nothing
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

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

            ui.draw(g2d);
        }

        // If door transition is active, draw a dark overlay.
        if (doorTransitionActive) {
            // Here we simply draw a semi-transparent black rectangle over the whole screen.
            g2d.setColor(new Color(0, 0, 0, 200)); // Adjust alpha as needed.
            g2d.fillRect(0, 0, screenWidth, screenHeight);
        }

        // For debugging: draw player's collision bounds
        player.drawCollisionBounds(g2d);

        // For debugging: draw collision bounds for each door in the current map
        for (int i = 0; i < superObject[currentMap].length; i++) {
            if (superObject[currentMap][i] != null && superObject[currentMap][i] instanceof object.OBJ_Door) {
                ((object.OBJ_Door)superObject[currentMap][i]).drawCollisionBounds(g2d, this);
            }
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

    public void startDoorTransition(int nextMap) {
        doorTransitionActive = true;
        doorTransitionStartTime = System.currentTimeMillis();
//        doorNextMap = nextMap;
//        assetSetter.setObject(nextMap);
//        assetSetter.setNPC(nextMap);
    }
}
