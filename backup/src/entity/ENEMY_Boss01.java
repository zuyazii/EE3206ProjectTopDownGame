package entity;

import event.DoorEvent;
import event.EventObject;
import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ENEMY_Boss01 extends Enemy {

    // Additional idle frames for the boss (total 6 frames)
    public BufferedImage down3, down4, down5, down6;
    public int bossWidth;
    public int bossHeight;
    private final int scaleFactor = 3;

    // --- battle animation fields ---
    private BufferedImage[] idleFrames;
    private BufferedImage[] atkFrames;
    private BufferedImage[] takeHitFrames;
    private BufferedImage[] deathFrames;

    private int  battSpriteCounter = 0;
    private int  battSpriteNum     = 1;
    private String battState       = "idle";
    private boolean animLocked     = false;

    public ENEMY_Boss01(GamePanel gamePanel) {
        super(gamePanel);
        // Boss is static.
        speed = 0;
        direction = "down";
        enemyNum = 1;
        isBeatened = false;

        maxHP = 50;
        hp = maxHP;

        this.optionDialog = "Continue Battle?";

        // Load idle animation images.
        down1 = setup("/enemies/boss_02/01_demon_idle/demon_idle_1");
        down2 = setup("/enemies/boss_02/01_demon_idle/demon_idle_2");
        down3 = setup("/enemies/boss_02/01_demon_idle/demon_idle_3");
        down4 = setup("/enemies/boss_02/01_demon_idle/demon_idle_4");
        down5 = setup("/enemies/boss_02/01_demon_idle/demon_idle_5");
        down6 = setup("/enemies/boss_02/01_demon_idle/demon_idle_6");

        setDialogue();
        setBossSize(scaleFactor, scaleFactor);

        // load battle sprites
        idleFrames     = loadSeries("/enemies/boss_02/01_demon_idle/demon_idle_", 6);
        atkFrames   = loadSeries("/enemies/boss_02/03_demon_cleave/demon_cleave_", 15);
        takeHitFrames  = loadSeries("/enemies/boss_02/04_demon_take_hit/demon_take_hit_", 5);
        deathFrames    = loadSeries("/enemies/boss_02/05_demon_death/demon_death_", 22);
    }

    private BufferedImage[] loadSeries(String basePath, int count) {
        BufferedImage[] arr = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            arr[i] = setup(basePath + (i+1));
        }
        return arr;
    }

    private void setDialogue() {
        dialogues[0] = "erm";
    }

    public void setBossSize(int widthMultiplier, int heightMultiplier) {
        bossWidth = gamePanel.tileSize * widthMultiplier;
        bossHeight = gamePanel.tileSize * heightMultiplier;
        collisionBounds = new Rectangle(13 * scaleFactor, 17 * scaleFactor, 56 * scaleFactor, (int)(87 * 1.8));
    }

    @Override
    public void update() {
        if (hp < 1) {
            collisonOn = false;
            isBeatened = true;

            for (EventObject ev : gamePanel.eventObjects) {
                if (ev instanceof DoorEvent) {
                    DoorEvent door = (DoorEvent) ev;
                    if (door.getNextMap() == 2) {
                        door.promptMessage = "Enter the forest..?";
                    }
                }
            }
            return;
        }

        // Boss remains idle.
        collisonOn = true;
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNumber++;
            if (spriteNumber > 6) {
                spriteNumber = 1;
            }
            spriteCounter = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (hp < 1) {
            return;
        }

        BufferedImage image = null;
        int screenX = worldx - gamePanel.player.worldx + gamePanel.player.screenX;
        int screenY = worldy - gamePanel.player.worldy + gamePanel.player.screenY;

        // Only draw if within the player's view.
        if (worldx + bossWidth > gamePanel.player.worldx - gamePanel.player.screenX &&
                worldx - bossWidth < gamePanel.player.worldx + gamePanel.player.screenX &&
                worldy + bossHeight > gamePanel.player.worldy - gamePanel.player.screenY &&
                worldy - bossHeight < gamePanel.player.worldy + gamePanel.player.screenY) {

            switch (spriteNumber) {
                case 1: image = down1; break;
                case 2: image = down2; break;
                case 3: image = down3; break;
                case 4: image = down4; break;
                case 5: image = down5; break;
                case 6: image = down6; break;
            }
            g2d.drawImage(image, screenX, screenY, bossWidth, bossHeight, null);
        }
    }

    @Override
    public void speak() {
        // Only proceed if we're not already showing a dialogue option.
        if (!gamePanel.ui.showDialogueOptions) {
            // Check if there is a dialogue line to show.
            if (dialogues[dialogueIndex] == null) { // No more dialogue lines.
                if (optionDialog != null && !optionDialog.isEmpty()) {
                    // For enemies: trigger a yes/no option prompt.
                    gamePanel.ui.optionText = optionDialog;
                    gamePanel.ui.showDialogueOptions = true;
                    System.out.println("Dialogue complete: showing option prompt (" + optionDialog + ")");
                    // (Do not leave dialogue state until the player answers the prompt.)
                } else {
                    // If no option is defined, return to playState.
                    gamePanel.gameState = gamePanel.playState;
                }
                // Reset the dialogue index so next conversation starts at the beginning.
                dialogueIndex = 0;
            } else {
                // There is another dialogue line: show it.
                gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
                dialogueIndex++;
            }
        }
    }

    @Override
    public void performBattleAction() {
        // Implement enemy-specific battle logic here.
    }

    @Override
    public void updateBattleAnimation() {
        battSpriteCounter++;
        if (battSpriteCounter > 10) {
            battSpriteNum++;
            battSpriteCounter = 0;
            int frameCount = getFrameCount(battState);
            if (battSpriteNum > frameCount) {
                if (!battState.equals("idle")) {
                    battState      = "idle";
                    battSpriteNum  = 1;
                    animLocked     = false;
                } else {
                    battSpriteNum = 1;
                }
            }
        }
    }

    @Override
    public void drawBattleSprite(Graphics2D g2, int x, int y) {
        if (hp < 1) return;               // dead = don't draw
        BufferedImage frame = null;
        switch (battState) {
            case "idle":     frame = idleFrames[battSpriteNum-1];     break;
            case "cleave":   frame = atkFrames[battSpriteNum-1];   break;
            case "take_hit": frame = takeHitFrames[battSpriteNum-1];  break;
            case "death":    frame = deathFrames[battSpriteNum-1];    break;
        }
        // same offsets as before:
        g2.drawImage(frame,
                x - gamePanel.tileSize*2,
                y - gamePanel.tileSize*3,
                gamePanel.tileSize*5, gamePanel.tileSize*5,
                null
        );
    }

    private int getFrameCount(String state) {
        switch(state) {
            case "idle":     return idleFrames.length;
            case "cleave":   return atkFrames.length;
            case "take_hit": return takeHitFrames.length;
            case "death":    return deathFrames.length;
        }
        return 1;
    }

    public void triggerBattleAnimation(String state, int duration) {
        // e.g.
        this.battState       = state;
        this.battSpriteNum   = 1;
        this.battSpriteCounter = 0;
        // optionally lock it so updateBattleAnimation knows
    }

}
