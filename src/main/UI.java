package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import object.OBJ_Key;

import javax.imageio.ImageIO;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    Font maruMonica, purisaB;
    public boolean messageOn = false;
    public String message = "";
    int messageCountdown = 0;
    public String currentDialogue = "";
    public int commandNum = 0;
    public int battleCommandNum = 0;
    public int optionNum = 0;

    public boolean showDialogueOptions = false;

    // Animation arrays for the demon/boss
    private BufferedImage[] demonIdleFrames;
    private BufferedImage[] demonWalkFrames;
    private BufferedImage[] demonCleaveFrames;
    private BufferedImage[] demonTakeHitFrames;
    private BufferedImage[] demonDeathFrames;

    // Variables to control animation
    private int enemySpriteCounter = 0;
    private int enemySpriteNum = 1;      // Current frame index (1-based for convenience)
    private String enemyState = "idle";  // Current animation state

    private boolean animationLocked = false;

    private int redVignetteTimer = 0;



    public UI(GamePanel gp) {
        this.gp = gp;

        try {
            InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/font/Purisa Bold.ttf");
            purisaB = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadEnemySprites();
    }

    public void showMessage(String msg) {
        message = msg;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(maruMonica);
        g2.setColor(Color.white);

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        // PLAY STATE
        if (gp.gameState == gp.playState) {

        }
        // PAUSE STATE
        if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState) {
            if (!showDialogueOptions) {
                drawDialogueScreen();
            }

            // Draw dialogue options on top when active
            if (showDialogueOptions) {
                drawDialogueOption();
            }

        }
        // BATTLE STATE
        if (gp.gameState == gp.battleState) {
            drawBattleScreen(gp.npc[gp.currentMap][0].enemyNum);
        }
        // TRANSISITON STATE
        if (gp.doorTransitionActive) {
            // Using alpha of 100 for a lighter dark overlay.
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    private void drawTitleScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96f));
        String text = "A VERY QUICK GAME";
        int x = getXForCenteredText(text);
        int y = gp.tileSize * 3;
        // TITLE TEXT SHADOW
        g2.setColor(Color.gray);
        g2.drawString(text, x + 3, y + 3);
        // TITLE TEXT COLOR
        g2.setColor(Color.white);
        g2.drawString(text, x, y);

        // MAIN CHARACTER IMAGE
        x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2;
        y += gp.tileSize * 1.6;
        g2.drawImage(gp.player.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);

        // MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));

        text = "NEW GAME";
        x = getXForCenteredText(text);
        y += gp.tileSize * 3.7;
        g2.drawString(text, x, y);
        if (commandNum == 0) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "LOAD GAME";
        x = getXForCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 1) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "QUIT";
        x = getXForCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 2) {
            g2.drawString(">", x - gp.tileSize, y);
        }
    }

    private void drawDialogueScreen() {
        // WINDOW
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 5;

        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32f));
        x += gp.tileSize;
        y += gp.tileSize;
        for (String line:currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawDialogueOption() {
        // Define window dimensions (you can tweak these values)
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 3;
        int x = (gp.screenWidth - width) / 2;
        int y = (gp.screenHeight - height) / 2;

        // Draw the option window in the center
        drawSubWindow(x, y, width, height);

        // Set font for the options (matching the dialogue style)
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32f));

        // Define positions for the options text
        int textY = y + gp.tileSize * 2; // vertically centered within the subwindow
        int yesX = x + (int)(gp.tileSize * 1.5);
        int noX = x + gp.tileSize * 4;

        // Draw the "YES" and "NO" options
        g2.drawString("NO", yesX, textY);
        g2.drawString("YES", noX, textY);

        // Draw selection arrow. Here, commandNum is used as the selection index (0 for YES, 1 for NO).
        // Adjust the arrow position accordingly.
        if (optionNum == 0) {
            g2.drawString(">", yesX - gp.tileSize, textY);
        } else if (optionNum == 1) {
            g2.drawString(">", noX - gp.tileSize, textY);
        }

        // For this example, return true if "YES" is currently selected,
        // false if "NO" is selected. Adjust as necessary for your game logic.
        System.out.println("drawDialogueOption");
    }

    public void drawBattleScreen(int enemyNum) {

        // Show enemy HP
        String text = "HP: " + gp.npc[gp.currentMap][0].hp;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32f));
        int x = getXForCenteredText(text);
        int y = (int) (gp.tileSize);
        g2.drawString(text, x, y);

        // Coordinates to draw the enemy
        x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2 + gp.tileSize;
        y = (int) (gp.tileSize * 4.5);

        // Update which frame we should be on
        updateEnemyAnimation();
        // Now draw the current frame
        drawEnemyAnimation(g2, x, y);

        // Subwindow with battle menu, etc.
        int subX = (int)(gp.tileSize * 2.5);
        int subY = gp.tileSize * 7;
        int width = gp.screenWidth - (gp.tileSize * 5);
        int height = (int) (gp.tileSize * 4.5);
        drawSubWindow(subX, subY, width, height);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));

        // Player HP
        text = "HP: " + gp.player.hp;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25f));
        x = subX + (int)(0.1 * gp.tileSize);
        y = subY - (int)(0.2 * gp.tileSize);
        g2.drawString(text, x, y);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));


        // MENU
        text = "ATTACK";
        x = (int)(gp.tileSize * 4.4);
        y = (int)(gp.tileSize * 8.5);
        g2.drawString(text, x, y);
        if (battleCommandNum == 0) {
            g2.drawString(">", (x - (int)(gp.tileSize / 1.4)), y);
        }
        text = "DEFENSE";
        x = (int)(gp.tileSize * 9.2);
        g2.drawString(text, x, y);
        if (battleCommandNum == 1) {
            g2.drawString(">", (x - (int)(gp.tileSize / 1.4)), y);
        }
        text = "ESCAPE";
        y += (int)(gp.tileSize * 2);
        g2.drawString(text, x, y);
        if (battleCommandNum == 2) {
            g2.drawString(">", (x - (int)(gp.tileSize / 1.4)), y);
        }
        text = "INVENTORY";
        x = (int)(gp.tileSize * 4.4);
        g2.drawString(text, x, y);
        if (battleCommandNum == 3) {
            g2.drawString(">", (x - (int)(gp.tileSize / 1.4)), y);
        }

        // Update and draw round animation effects (if any)
        if(gp.battle != null) {
            gp.battle.updateRoundAnimation();
            gp.battle.drawRoundAnimation(g2);
        }

        if(gp.battle != null) {
            gp.battle.updateRoundAnimation();
            gp.battle.drawRoundAnimation(g2);
        }

        // Draw red vignette overlay if triggered
        if (redVignetteTimer > 0) {
            // Create a semi-transparent red color
            Color redOverlay = new Color(255, 0, 0, 100);  // adjust alpha (transparency) as needed
            g2.setColor(redOverlay);
            // Draw a rectangle covering the entire screen
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            redVignetteTimer--; // Decrease timer each frame
        }
    }

    public void drawSubWindow(int x, int y, int width, int height) {

        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public void drawPauseScreen() {
        String text = "Pause";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60f));

        int y = gp.screenHeight/2;
        int x = getXForCenteredText(text);

        g2.drawString(text, x, y);
    }

    public int getXForCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return (gp.screenWidth / 2 - length / 2);
    }

    public void loadEnemySprites() {
        // Load "idle" frames
        demonIdleFrames = new BufferedImage[6];
        for (int i = 0; i < 6; i++) {
            demonIdleFrames[i] = loadImage("/enemies/boss_02/01_demon_idle/demon_idle_" + (i+1));
        }

        // Load "walk" frames (example, if you have 6 frames)
//        demonWalkFrames = new BufferedImage[12];
//        for (int i = 0; i < 12; i++) {
//            demonWalkFrames[i] = loadImage("/enemies/boss_02/02_demon_walk/demon_walk_" + (i+1));
//        }

        // Load "cleave" frames (example, if you have 6 frames)
        demonCleaveFrames = new BufferedImage[15];
        for (int i = 0; i < 15; i++) {
            demonCleaveFrames[i] = loadImage("/enemies/boss_02/03_demon_cleave/demon_cleave_" + (i+1));
        }

        // Load "take hit" frames
        demonTakeHitFrames = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            demonTakeHitFrames[i] = loadImage("/enemies/boss_02/04_demon_take_hit/demon_take_hit_" + (i+1));
        }

        // Load "death" frames
        demonDeathFrames = new BufferedImage[22];
        for (int i = 0; i < 22; i++) {
            demonDeathFrames[i] = loadImage("/enemies/boss_02/05_demon_death/demon_death_" + (i+1));
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateEnemyAnimation() {
        // Increase the counter
        enemySpriteCounter++;

        // Change frames every 10 ticks (adjust as needed for speed)
        if (enemySpriteCounter > 10) {
            enemySpriteNum++;
            enemySpriteCounter = 0;
            int frameCount = getFrameCountForState(enemyState);

            if (enemySpriteNum > frameCount) {
                // Animation for non-idle states should finish fully before reverting.
                if (!enemyState.equals("idle")) {
                    // Lock is active until animation finishes; here we revert to idle.
                    enemyState = "idle";
                    enemySpriteNum = 1;
                    animationLocked = false;
                } else {
                    // For idle, just loop.
                    enemySpriteNum = 1;
                }
            }
        }
    }

    private void drawEnemyAnimation(Graphics2D g2, int x, int y) {
        BufferedImage frame = null;

        switch (enemyState) {
            case "idle":
                frame = demonIdleFrames[enemySpriteNum - 1];
                break;
            case "walk":
                frame = demonWalkFrames[enemySpriteNum - 1];
                break;
            case "cleave":
                frame = demonCleaveFrames[enemySpriteNum - 1];
                break;
            case "take_hit":
                frame = demonTakeHitFrames[enemySpriteNum - 1];
                break;
            case "death":
                frame = demonDeathFrames[enemySpriteNum - 1];
                break;
        }

        // Example: draw at double tile size. Adjust as needed.
        g2.drawImage(frame, x - gp.tileSize * 2, y - gp.tileSize * 3, gp.tileSize * 5, gp.tileSize * 5, null);
    }

    public void setEnemyState(String newState) {
        enemyState = newState;
        enemySpriteNum = 1; // reset frame index for the new animation
    }

    public void triggerAnimation(String anim, int duration) {
        // Only trigger the new animation if no animation is locked
        if (!animationLocked || enemyState.equals("idle")) {
            enemyState = anim;
            enemySpriteNum = 1;
            enemySpriteCounter = 0;
            animationLocked = true;
            // Optionally, you can store the duration in a timer if you want a fixed duration.
            // For simplicity, we are using frame counts here.
        }
    }

    // Helper to get the number of frames for the current animation state.
    private int getFrameCountForState(String state) {
        switch(state) {
            case "idle": return demonIdleFrames.length;
            case "cleave": return demonCleaveFrames.length;
            case "take_hit": return demonTakeHitFrames.length;
            case "death": return demonDeathFrames.length;
            // Add other states as needed.
            default: return 1;
        }
    }

    // Call this to trigger the red vignette for a given duration (in frames)
    public void triggerRedVignette(int duration) {
        redVignetteTimer = duration;
    }
}
