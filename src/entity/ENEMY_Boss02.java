package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ENEMY_Boss02 extends Enemy {

    public int bossWidth;
    public int bossHeight;
    private final int scaleFactor = 3;

//    private BufferedImage[] idleFrames;
//    private BufferedImage[] walkFrames;
//    private BufferedImage[] atkFrames;
//    private BufferedImage[] takeHitFrames;
//    private BufferedImage[] deathFrames;

    private int  battSpriteCounter = 0;
    private int  battSpriteNum     = 1;
    private String battState       = "idle";

    public ENEMY_Boss02(GamePanel gamePanel) {
        super(gamePanel);

        speed      = 0;
        direction  = "down";
        enemyNum   = 2;
        isBeatened = false;

        maxHP = 100;
        hp    = maxHP;

        this.optionDialog = "Continue Battle?";

        setDialogue();
        setBossSize(scaleFactor, scaleFactor);

        // load battle sprites
        idleFrames     = loadSeries("/enemies/boss_03/idle/idle_", 6);
        atkFrames   = loadSeries("/enemies/boss_03/1_atk/1_atk_", 14);
        takeHitFrames  = loadSeries("/enemies/boss_03/take_hit/take_hit_", 7);
        deathFrames    = loadSeries("/enemies/boss_03/death/death_", 16);
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

    private void setBossSize(int wMul, int hMul) {
        bossWidth  = gamePanel.tileSize * wMul;
        bossHeight = gamePanel.tileSize * hMul;

        // decide your actual collision box here:
        // e.g. offset it from the sprite's top-left by (offsetX,offsetY),
        // and make it as big as you need (cbW×cbH)
        int offsetX = 35 * scaleFactor;
        int offsetY = 20 * scaleFactor;
        int cbW     = 40 * scaleFactor;
        int cbH     = (int)(87);

        // 1) set the collisionBounds rectangle
        collisionBounds = new Rectangle(offsetX, offsetY, cbW, cbH);

        // 2) ALSO update the defaults that CollisionChecker will restore
        solidAreaDefaultX = offsetX;
        solidAreaDefaultY = offsetY;
    }

    @Override
    public void update() {
        if (hp < 1) {
            collisonOn  = false;
            isBeatened  = true;
            return;
        }

        collisonOn = true;
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNumber++;
            if (spriteNumber > idleFrames.length) {
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

        int screenX = worldx - gamePanel.player.worldx + gamePanel.player.screenX;
        int screenY = worldy - gamePanel.player.worldy + gamePanel.player.screenY;

        if (worldx + bossWidth  > gamePanel.player.worldx - gamePanel.player.screenX &&
                worldx - bossWidth  < gamePanel.player.worldx + gamePanel.player.screenX &&
                worldy + bossHeight > gamePanel.player.worldy - gamePanel.player.screenY &&
                worldy - bossHeight < gamePanel.player.worldy + gamePanel.player.screenY) {

            BufferedImage frame = idleFrames[spriteNumber - 1];
            g2d.drawImage(frame, screenX, screenY, (int)(bossWidth * 1.8), (int)(bossHeight * 1.2), null);
        }
    }

    @Override
    public void speak() {
        if (!gamePanel.ui.showDialogueOptions) {
            if (dialogues[dialogueIndex] == null) {
                if (optionDialog != null && !optionDialog.isEmpty()) {
                    gamePanel.ui.optionText = optionDialog;
                    gamePanel.ui.showDialogueOptions = true;
                } else {
                    gamePanel.gameState = gamePanel.playState;
                }
                dialogueIndex = 0;
            } else {
                gamePanel.ui.currentDialogue = dialogues[dialogueIndex++];
            }
        }
    }

    @Override
    public void performBattleAction() {
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
                (int)(x - gamePanel.tileSize*4.1),
                y - gamePanel.tileSize*3,
                gamePanel.tileSize*8, gamePanel.tileSize*6,
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
