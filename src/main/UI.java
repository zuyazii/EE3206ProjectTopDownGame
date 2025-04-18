package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import entity.Player;
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
    public String optionText = "";      // The text to be shown in the yes/no prompt

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

    // Battle Notification Fields
    private String battleNotificationMessage = "";
    private boolean battleNotificationOn = false;
    // Removed timer fields since we no longer want auto-dismiss.
    // All notifications are persistent—they only clear when Enter is pressed.
    private boolean battleNotificationPersistent = true;

    // Inventory Fields
    public boolean inventoryActive = false;
    public int playerSlotCol = 0;
    public int playerSlotRow = 0;
    public boolean itemNotificationActive = false;
    private String itemNotificationMessage = "";

    // Battle Inventory Fields (for consumables only)
    public boolean battleInventoryActive = false;
    public int battleInvSelection = 0;  // Index of the currently selected consumable (in the filtered list)
    public int battleInvOffset = 0;     // For scrolling: the index in the filtered list of the first visible item

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

    // Show a battle notification that remains on screen until dismissed.
    public void showBattleNotification(String msg) {
        battleNotificationMessage = msg;
        battleNotificationOn = true;
        // All battle notifications are now persistent (auto-dismiss is removed).
        battleNotificationPersistent = true;
    }

    // These methods let KeyHandler check/dismiss the notification.
    public boolean isBattleNotificationActive() {
        return battleNotificationOn;
    }

    public void dismissBattleNotification() {
        battleNotificationOn = false;
        battleNotificationPersistent = false;
    }

    public void draw(Graphics2D g2) {

        this.g2 = g2;
        g2.setFont(maruMonica);
        g2.setColor(Color.white);

        // Item Notification
        if (itemNotificationActive) {
            drawItemNotification();
            return;
        }

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        // PLAY STATE
        else if (gp.gameState == gp.playState) {
            if (inventoryActive) {
                drawInventory();
                return; // Inventory menu takes full screen overlay.
            }
        }
        // PAUSE STATE
        else if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        // DIALOGUE STATE
        else if (gp.gameState == gp.dialogueState) {
            // If we are currently showing a yes/no prompt:
            if (showDialogueOptions) {
                drawDialogueOption(optionText);  // <–– Use optionText, not currentDialogue
            }
            else {
                drawDialogueScreen();
            }
        }
        // BATTLE STATE
        else if (gp.gameState == gp.battleState) {
            // 1) Always draw the main battle screen (the demon, HP, commands, etc.)
            drawBattleScreen(gp.npc[gp.currentMap][0].enemyNum);

            // 2) If battleInventoryActive is true, overlay the smaller inventory subwindow
            if (battleInventoryActive) {
                drawBattleInventory();
            }
            // No 'return;' here — so that the demon and background remain drawn behind the subwindow.
        }

        // GAMEOVER STATE
        else if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen();
        }
    }

    private void drawTitleScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96f));
        String text = "ERM";
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

        drawSubWindow(x, y, width, height, 210);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32f));
        x += gp.tileSize;
        y += gp.tileSize;
        for (String line: currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawDialogueOption(String message) {
        // Define window dimensions
        int width = gp.tileSize * 6;
        int height = gp.tileSize * 3;
        int x = (gp.screenWidth - width) / 2;
        int y = (gp.screenHeight - height) / 2;

        drawSubWindow(x, y, width, height, 210);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32f));

        // Main option text
        int dTextY = y + gp.tileSize;
        int dTextX = getXForCenteredText(message);
        g2.drawString(message, dTextX, dTextY);

        // "YES" and "NO"
        int textY = y + gp.tileSize * 2;
        int yesX = x + (int)(gp.tileSize * 1.5);
        int noX  = x + gp.tileSize * 4;

        g2.drawString("YES", yesX, textY);
        g2.drawString("NO",  noX,  textY);

        // Draw selection arrow
        if (optionNum == 0) {
            g2.drawString(">", yesX - gp.tileSize, textY);
        } else {
            g2.drawString(">", noX - gp.tileSize, textY);
        }
    }

    public void drawBattleScreen(int enemyNum) {

        // Show enemy HP
        String text = "HP: " + gp.npc[gp.currentMap][0].hp;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32f));
        int x = getXForCenteredText(text);
        int y = (int)(gp.tileSize);
        g2.drawString(text, x, y);

        // Coordinates to draw the enemy
        x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2 + gp.tileSize;
        y = (int)(gp.tileSize * 4.5);

        // Update which frame we should be on and draw the enemy
        updateEnemyAnimation();
        drawEnemyAnimation(g2, x, y);

        // Define the subwindow for the battle menu/notification.
        int subX = (int)(gp.tileSize * 2.5);
        int subY = gp.tileSize * 7;
        int width = gp.screenWidth - (gp.tileSize * 5);
        int height = (int)(gp.tileSize * 4.5);

        // Draw the subwindow background.
        drawSubWindow(subX, subY, width, height, 255);

        // Check if a battle notification is active.
        if (battleNotificationOn) {
            // Draw the notification message centered in the subwindow.
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 38f));
            int centeredX = getXForCenteredText(battleNotificationMessage);
            int textY = subY + (int)(gp.tileSize * 1.8);
            g2.drawString(battleNotificationMessage, centeredX, textY);

            // Draw the prompt telling the player how to dismiss it.
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            String prompt = "- Press Enter to continue -";
            centeredX = getXForCenteredText(prompt);
            g2.drawString(prompt, centeredX, (int)(textY + gp.tileSize * 1.2));
            // Note: No timer update here since notifications are persistent.
        } else {
            // No notification: draw the battle menu commands normally.
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));
            text = "HP: " + gp.player.hp;
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25f));
            x = subX + (int)(0.1 * gp.tileSize);
            y = subY - (int)(0.2 * gp.tileSize);
            g2.drawString(text, x, y);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));

            // Draw the battle command texts.
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
        }

        // Update and draw round animation effects (if any)
        if (gp.battle != null) {
            gp.battle.updateRoundAnimation();
            gp.battle.drawRoundAnimation(g2);
        }

        // Draw red vignette overlay if triggered.
        if (redVignetteTimer > 0) {
            Color redOverlay = new Color(255, 0, 0, 100);
            g2.setColor(redOverlay);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            redVignetteTimer--;
        }
    }

    public void drawSubWindow(int x, int y, int width, int height, int a) {
        Color c = new Color(0, 0, 0, a);
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
        int y = gp.screenHeight / 2;
        int x = getXForCenteredText(text);
        g2.drawString(text, x, y);
    }

    public int getXForCenteredText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return (gp.screenWidth / 2 - length / 2);
    }

    public void loadEnemySprites() {
        // Load "idle" frames
        demonIdleFrames = new BufferedImage[6];
        for (int i = 0; i < 6; i++) {
            demonIdleFrames[i] = loadImage("/enemies/boss_02/01_demon_idle/demon_idle_" + (i + 1));
        }

        // Load "cleave" frames (example, if you have 6 frames)
        demonCleaveFrames = new BufferedImage[15];
        for (int i = 0; i < 15; i++) {
            demonCleaveFrames[i] = loadImage("/enemies/boss_02/03_demon_cleave/demon_cleave_" + (i + 1));
        }

        // Load "take hit" frames
        demonTakeHitFrames = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            demonTakeHitFrames[i] = loadImage("/enemies/boss_02/04_demon_take_hit/demon_take_hit_" + (i + 1));
        }

        // Load "death" frames
        demonDeathFrames = new BufferedImage[22];
        for (int i = 0; i < 22; i++) {
            demonDeathFrames[i] = loadImage("/enemies/boss_02/05_demon_death/demon_death_" + (i + 1));
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
        enemySpriteCounter++;
        if (enemySpriteCounter > 10) {
            enemySpriteNum++;
            enemySpriteCounter = 0;
            int frameCount = getFrameCountForState(enemyState);
            if (enemySpriteNum > frameCount) {
                if (!enemyState.equals("idle")) {
                    enemyState = "idle";
                    enemySpriteNum = 1;
                    animationLocked = false;
                } else {
                    enemySpriteNum = 1;
                }
            }
        }
    }

    private int getFrameCountForState(String state) {
        switch(state) {
            case "idle":
                return demonIdleFrames != null ? demonIdleFrames.length : 0;
            case "walk":
                return demonWalkFrames != null ? demonWalkFrames.length : 0;
            case "cleave":
                return demonCleaveFrames != null ? demonCleaveFrames.length : 0;
            case "take_hit":
                return demonTakeHitFrames != null ? demonTakeHitFrames.length : 0;
            case "death":
                return demonDeathFrames != null ? demonDeathFrames.length : 0;
            default:
                return 1;
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
        g2.drawImage(frame, x - gp.tileSize * 2, y - gp.tileSize * 3, gp.tileSize * 5, gp.tileSize * 5, null);
    }

    public void setEnemyState(String newState) {
        enemyState = newState;
        enemySpriteNum = 1;
    }

    public void triggerRedVignette(int duration) {
        redVignetteTimer = duration;
    }

    public void showBattleEscapeNotification(String msg) {
        // For escapes, use the same persistent notification mechanism.
        battleNotificationMessage = msg;
        battleNotificationOn = true;
        battleNotificationPersistent = true;
    }

    private void drawGameOverScreen() {
        g2.setColor(new Color(0, 0, 0));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96f));
        String text = "GAME OVER!";
        int x = getXForCenteredText(text);
        int y = gp.tileSize * 4;
        // TITLE TEXT SHADOW
        g2.setColor(Color.gray);
        g2.drawString(text, x + 3, y + 3);
        // TITLE TEXT COLOR
        g2.setColor(Color.white);
        g2.drawString(text, x, y);

        // MAIN CHARACTER IMAGE
//        x = gp.screenWidth / 2 - (gp.tileSize * 2) / 2;
//        y += gp.tileSize * 1.6;
//        g2.drawImage(gp.player.down1, x, y, gp.tileSize * 2, gp.tileSize * 2, null);

        // MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));

        text = "MENU";
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

    public void drawInventory() {
        // Define the subwindow for the inventory screen.
        int frameX = gp.tileSize * 9;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 6;
        int frameHeight = gp.tileSize * 5;
        // Use existing inventory fields:
        int slotCol = playerSlotCol;
        int slotRow = playerSlotRow;

        drawSubWindow(frameX, frameY, frameWidth, frameHeight, 210);

        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;
        int slotSize = gp.tileSize + 3;

        // Draw each item in the player's inventory.
        for (int i = 0; i < gp.player.inventory.size(); i++) {
            item.Item itm = gp.player.inventory.get(i);
            // Highlight if this item is currently equipped:
            if(itm == gp.player.currentWeapon || itm == gp.player.currentShield) {
                g2.setColor(new Color(240, 190, 90));
                g2.fillRoundRect(slotX, slotY, gp.tileSize, gp.tileSize, 10, 10);
            }
            // Draw the item icon.
            g2.drawImage(itm.icon, slotX, slotY, null);
            // If the item is consumable and quantity > 1, display the quantity.
            if(itm.type == item.ItemType.CONSUMABLE && itm.quantity > 1) {
                g2.setFont(g2.getFont().deriveFont(32f));
                String s = "" + itm.quantity;
                int amountX = getXforAlignToRight(s, slotX + 44);
                int amountY = slotY + gp.tileSize;
                g2.setColor(new Color(60,60,60));
                g2.drawString(s, amountX, amountY);
                g2.setColor(Color.white);
                g2.drawString(s, amountX - 3, amountY - 3);
            }

            slotX += slotSize;
            if(i == 4 || i == 9 || i == 14) {
                slotX = slotXstart;
                slotY += slotSize;
            }
        }

        // Draw the selection cursor:
        int cursorX = slotXstart + (slotSize * slotCol);
        int cursorY = slotYstart + (slotSize * slotRow);
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, gp.tileSize, gp.tileSize, 10, 10);

        // Draw a description window for the selected item.
        int dFrameX = frameX;
        int dFrameY = frameY + frameHeight;
        int dFrameWidth = frameWidth;
        int dFrameHeight = gp.tileSize * 3;
        drawSubWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight, 210);
        int textX = dFrameX + 20;
        int textY = dFrameY + gp.tileSize;
        g2.setFont(g2.getFont().deriveFont(28f));

        int selectedIndex = playerSlotCol + (playerSlotRow * 5);
        if(selectedIndex < gp.player.inventory.size()){
            String desc = gp.player.inventory.get(selectedIndex).description;
            for(String line : desc.split("\n")){
                g2.drawString(line, textX, textY);
                textY += 32;
            }
        }
    }

    public void drawItemNotification() {
        int width = gp.tileSize * 10;
        int height = gp.tileSize * 2;
        int x = (gp.screenWidth - width) / 2;
        int y = (gp.screenHeight - height) / 2;
        drawSubWindow(x, y, width, height, 210);
        g2.setFont(g2.getFont().deriveFont(28f));
        int textX = getXForCenteredText(itemNotificationMessage);
        int textY = y + (int)(gp.tileSize * 0.9);
        g2.drawString(itemNotificationMessage, textX, textY);
        // Optionally, draw a prompt such as:
        g2.setFont(g2.getFont().deriveFont(18f));
        String prompt = "- Press Enter to continue -";
        int promptX = getXForCenteredText(prompt);
        g2.drawString(prompt, promptX, textY + gp.tileSize / 2);
    }

    public void showItemNotification(String msg) {
        itemNotificationMessage = msg;
        itemNotificationActive = true;
    }

    public int getXforAlignToRight(String text, int tailX) {
        int textWidth = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return tailX - textWidth;
    }

    public void drawBattleInventory() {
        // Subwindow = the same area as the normal battle command box:
        int subX = (int)(gp.tileSize * 2.5);
        int subY = gp.tileSize * 7;
        int width = gp.screenWidth - (gp.tileSize * 5);
        int height = (int)(gp.tileSize * 4.5);

        drawSubWindow(subX, subY, width, height, 255);

        // Then inside this rectangle, lay out your consumable list.
        // For example, an offset from subX + 20, subY + 40, etc.

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 30f));
        g2.setColor(Color.white);

        // Filter out just the consumable items, like before:
        java.util.List<item.Item> consumables = new java.util.ArrayList<>();
        java.util.List<Integer> consIndices = new java.util.ArrayList<>();
        for (int i = 0; i < gp.player.inventory.size(); i++) {
            item.Item itm = gp.player.inventory.get(i);
            if (itm.type == item.ItemType.CONSUMABLE) {
                consumables.add(itm);
                consIndices.add(i);
            }
        }

        int visibleRows = 5;
        int listStartX = subX + gp.tileSize;
        int listStartY = subY + gp.tileSize; // maybe start 40 px down to have space for a title
        int lineHeight = 28;

        if (consumables.isEmpty()) {
            String msg = "No consumables!";
            int msgX = getXForCenteredText(msg);
            int msgY = subY + height/2;
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 30f));
            g2.drawString(msg, msgX, msgY);
        }
        else {
            // Keep the code for clamping battleInvSelection and battleInvOffset
            // Then draw the items line by line
            for (int i = battleInvOffset; i < consumables.size() && i < battleInvOffset + visibleRows; i++) {
                item.Item itm = consumables.get(i);
                String line = itm.name + " x" + itm.quantity;
                int textX = listStartX;
                int textY = listStartY + (i - battleInvOffset) * lineHeight;

                if (i == battleInvSelection) {
                    g2.drawString(">", textX - 20, textY);
                }
                g2.drawString(line, textX, textY);
            }
        }

        // Draw a small prompt at the bottom of the subwindow
        String prompt = "Enter: Use   Q: Back";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
        int promptX = getXForCenteredText(prompt);
        int promptY = subY + height - 20;
        g2.drawString(prompt, promptX, promptY);
    }
}