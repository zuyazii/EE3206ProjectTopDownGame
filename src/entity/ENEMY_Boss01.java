package entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ENEMY_Boss01 extends Enemy {

    // Additional idle frames for the boss (total 6 frames)
    public BufferedImage down3, down4, down5, down6;
    public int bossWidth;
    public int bossHeight;
    private final int scaleFactor = 3;

    public ENEMY_Boss01(GamePanel gamePanel) {
        super(gamePanel);
        // Boss is static.
        speed = 0;
        direction = "down";
        enemyNum = 1;
        hp = 100;

        // Load idle animation images.
        down1 = setup("/enemies/boss_02/01_demon_idle/demon_idle_1");
        down2 = setup("/enemies/boss_02/01_demon_idle/demon_idle_2");
        down3 = setup("/enemies/boss_02/01_demon_idle/demon_idle_3");
        down4 = setup("/enemies/boss_02/01_demon_idle/demon_idle_4");
        down5 = setup("/enemies/boss_02/01_demon_idle/demon_idle_5");
        down6 = setup("/enemies/boss_02/01_demon_idle/demon_idle_6");

        setDialogue();
        setBossSize(scaleFactor, scaleFactor);
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

        // Example: reposition another NPC if defeated.
        if (gamePanel.npc[0][0].hp < 1) {
            gamePanel.npc[0][0].worldx = gamePanel.tileSize * 60;
            gamePanel.npc[0][0].worldy = gamePanel.tileSize * 60;
        }
    }

    @Override
    public void speak() {
        if (!gamePanel.ui.showDialogueOptions) {
            if (dialogues[dialogueIndex] == null) {
                gamePanel.ui.showDialogueOptions = true;
                dialogueIndex = 0;
            } else {
                gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
                dialogueIndex++;
            }
        }
    }

    @Override
    public void performBattleAction() {
        // Implement enemy-specific battle logic here.
    }
}
