package entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ENEMY_Boss01 extends Entity {

    // Additional idle frames for the boss (total 6 frames)
    public BufferedImage down3, down4, down5, down6;

    // Additional size variables for the boss
    public int bossWidth;
    public int bossHeight;
    private final int scaleFactor = 3;
//    public int health;
//    public Random attackVal = new Random();
//    public Random defenseVal = new Random();

    public ENEMY_Boss01(GamePanel gamePanel) {
        super(gamePanel);
        // The boss is static so we set speed to 0 and always face downwards
        speed = 0;
        direction = "down";
        enemyNum = 1;
        hp = 100;

        // Load idle animation images for the boss.
        // Ensure that your resource paths match your project structure.
        down1 = setup("/enemies/boss_02/01_demon_idle/demon_idle_1");
        down2 = setup("/enemies/boss_02/01_demon_idle/demon_idle_2");
        down3 = setup("/enemies/boss_02/01_demon_idle/demon_idle_3");
        down4 = setup("/enemies/boss_02/01_demon_idle/demon_idle_4");
        down5 = setup("/enemies/boss_02/01_demon_idle/demon_idle_5");
        down6 = setup("/enemies/boss_02/01_demon_idle/demon_idle_6");

        setDialogue();

        // Set the boss size relative to the tileSize (example: 2x the tileSize)
        setBossSize(scaleFactor, scaleFactor);
    }

    public void setDialogue() {
        dialogues[0] = "erm";
//        dialogues[1] = "So you've come to this island to find the treasure?";
//        dialogues[2] = "I used to be a great wizard but now... I'm a bit too old \nfor taking an adventure.";
//        dialogues[3] = "Well, good luck on you.";
    }

    public void setBossSize(int widthMultiplier, int heightMultiplier) {
        bossWidth = gamePanel.tileSize * widthMultiplier;
        bossHeight = gamePanel.tileSize * heightMultiplier;
        // Update the collision bounds to match the boss's new size
        collisionBounds = new Rectangle(13 * scaleFactor, 17 * scaleFactor, 56 * scaleFactor, (int)(87 * 1.8));
    }

    @Override
    public void update() {
        // The boss does not move so we skip any world position updates.
        // Keep collision active with the player.
        collisonOn = true;

        // Animate idle animation (6 frames)
        spriteCounter++;
        if (spriteCounter > 10) { // Adjust this value to control animation speed
            spriteNumber++;
            if (spriteNumber > 6) { // Cycle through frames 1 to 6
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

        // Only draw if the boss is within the player's view
        if (worldx + bossWidth > gamePanel.player.worldx - gamePanel.player.screenX &&
                worldx - bossWidth < gamePanel.player.worldx + gamePanel.player.screenX &&
                worldy + bossHeight > gamePanel.player.worldy - gamePanel.player.screenY &&
                worldy - bossHeight < gamePanel.player.worldy + gamePanel.player.screenY) {

            // Since the boss is always idle and facing down, we use only the down images
            switch (spriteNumber) {
                case 1:
                    image = down1;
                    break;
                case 2:
                    image = down2;
                    break;
                case 3:
                    image = down3;
                    break;
                case 4:
                    image = down4;
                    break;
                case 5:
                    image = down5;
                    break;
                case 6:
                    image = down6;
                    break;
            }
            // Draw the image scaled to the boss's width and height.
            g2d.drawImage(image, screenX, screenY, bossWidth, bossHeight, null);
        }

        if (gamePanel.npc[0][0].hp < 1) {
            gamePanel.npc[0][0].worldx = gamePanel.tileSize * 60;
            gamePanel.npc[0][0].worldy = gamePanel.tileSize * 60;
        }
    }

    public void speak() {
        if (!gamePanel.ui.showDialogueOptions) {
            if (dialogues[dialogueIndex] == null) {
                // End of dialogue: show dialogue option
                gamePanel.ui.showDialogueOptions = true;
                // Do not change gameState here.
                // Wait for the player to make a selection (handled via key input)
                dialogueIndex = 0;  // Optionally reset if needed.
            } else {
                gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
                dialogueIndex++;
            }
        }

//        if (gamePanel.ui.optionNum == 0) {
//            // YES selected: change state to PLAY.
//            gamePanel.gameState = gamePanel.playState;
//        } else {
//            // NO selected: change state to BATTLE.
//            gamePanel.gameState = gamePanel.battleState;
//        }
    }
}
