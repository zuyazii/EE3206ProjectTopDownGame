package entity;

import main.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Enemy extends Entity {

    protected int battSpriteCounter = 0;
    protected int battSpriteNum     = 1;
    protected String battState      = "idle";
    protected boolean animLocked    = false;


    protected BufferedImage[] idleFrames;
    protected BufferedImage[] atkFrames;
    protected BufferedImage[] takeHitFrames;
    protected BufferedImage[] deathFrames;


    public Enemy(GamePanel gamePanel) {
        super(gamePanel);
        isEnemy = true;
    }

    // Abstract method for enemy-specific battle actions.
    public abstract void performBattleAction();

    /**
     * Trigger a battle‐mode animation on this enemy.
     * @param state one of "idle", "cleave", "take_hit", "death"
     * @param duration how many frames the anim should run
     */
    public void triggerBattleAnimation(String state, int duration) {
        this.battState          = state;
        this.battSpriteNum      = 1;
        this.battSpriteCounter  = 0;
        this.animLocked         = true;            // optional: block returning to idle until duration
        // you can store duration if you want to automatically revert to "idle" later
    }

    /**
     * Call this each frame from your UI so the Enemy steps through its own
     * battle‐animation frames.
     */
    public void updateBattleAnimation() {
        if (!animLocked) return;
        battSpriteCounter++;
        if (battSpriteCounter > 10) {
            battSpriteNum++;
            battSpriteCounter = 0;
            int max = 1;
            switch(battState) {
                case "cleave":   max = atkFrames.length;      break;
                case "take_hit": max = takeHitFrames.length;  break;
                case "death":    max = deathFrames.length;    break;
                default:         max = idleFrames.length;     break;
            }
            if (battSpriteNum > max) {
                // once we finish a non‐idle anim, go back to idle
                battState     = "idle";
                battSpriteNum = 1;
                animLocked    = false;
            }
        }
    }

    /**
     * Draws the current battle‐mode frame of this enemy.
     * Called from UI.drawBattleScreen().
     */
    public void drawBattleSprite(Graphics2D g2, int x, int y) {
        BufferedImage frame = idleFrames[battSpriteNum - 1];
        switch (battState) {
            case "cleave":   frame = atkFrames[battSpriteNum - 1];      break;
            case "take_hit": frame = takeHitFrames[battSpriteNum - 1];  break;
            case "death":    frame = deathFrames[battSpriteNum - 1];    break;
        }
        g2.drawImage(frame,
                x - gamePanel.tileSize*3,
                y - gamePanel.tileSize*3,
                gamePanel.tileSize*8,
                gamePanel.tileSize*6,
                null);
    }
}
