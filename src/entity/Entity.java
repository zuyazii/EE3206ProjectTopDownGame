package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

// This stores variables that will be used in player, monster and NPC classes
public class Entity {

    public boolean isBeatened;
    GamePanel gamePanel;

    public int worldx, worldy;
    public int speed;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNumber = 1;
    public Rectangle collisionBounds = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public  boolean collisonOn = false;
    public int actionLockCounter = 0;
    String dialogues[] = new String[20];
    int dialogueIndex = 0;
    public String optionDialog;
    public boolean isEnemy = false;
    public int enemyNum;

    public int hp;
    public int maxHP;
    public Random attackVal = new Random();
    public Random defenseVal = new Random();
    public boolean isDefending = false;

    public Entity(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setAction() {}

    public void update() {
        setAction();

        collisonOn = false;
        gamePanel.collisionChecker.checkTile(this);
        gamePanel.collisionChecker.checkPlayer(gamePanel.player);

        if (!collisonOn) {
            switch (direction) {
                case "up":
                    worldy -= speed;
                    break;
                case "down":
                    worldy += speed;
                    break;
                case "left":
                    worldx -= speed;
                    break;
                case "right":
                    worldx += speed;
                    break;
            }
        }

        spriteCounter++;
        if (spriteCounter > 10) {       // Player image changes in every 10 frames
            if (spriteNumber == 1) {
                spriteNumber = 2;
            }
            else if (spriteNumber == 2) {
                spriteNumber = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2d) {

        BufferedImage image = null;
        int screenX = worldx - gamePanel.player.worldx + gamePanel.player.screenX;
        int screenY = worldy - gamePanel.player.worldy + gamePanel.player.screenY;

        if (worldx + gamePanel.tileSize > gamePanel.player.worldx - gamePanel.player.screenX &&
                worldx - gamePanel.tileSize < gamePanel.player.worldx + gamePanel.player.screenX &&
                worldy + gamePanel.tileSize > gamePanel.player.worldy - gamePanel.player.screenY &&
                worldy - gamePanel.tileSize < gamePanel.player.worldy + gamePanel.player.screenY ) {

            switch (direction) {
                case "up":
                    if (spriteNumber == 1) {
                        image = up1;
                    }
                    if (spriteNumber == 2) {
                        image = up2;
                    }
                    break;
                case "down":
                    if (spriteNumber == 1) {
                        image = down1;
                    }
                    if (spriteNumber == 2) {
                        image = down2;
                    }
                    break;
                case "left":
                    if (spriteNumber == 1) {
                        image = left1;
                    }
                    if (spriteNumber == 2) {
                        image = left2;
                    }
                    break;
                case "right":
                    if (spriteNumber == 1) {
                        image = right1;
                    }
                    if (spriteNumber == 2) {
                        image = right2;
                    }
                    break;
            }

            g2d.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
        }
    }

    public BufferedImage setup(String imagePath/*, int width, int height*/)
    {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try
        {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTool.scaleImage(image, gamePanel.tileSize, gamePanel.tileSize);   //it scales to tilesize , will fix for player attack(16px x 32px) by adding width and height
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return image;
    }

    public void speak() {
        // If the dialogue session was ended previously, reinitialize it.
        if (dialogueIndex == -1) {
            dialogueIndex = 0;
        }

        // If there's a next dialogue line, show it.
        if (dialogues[dialogueIndex] != null) {
            gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
            dialogueIndex++;
        } else {
            // End the dialogue session:
            gamePanel.ui.currentDialogue = "";
            dialogueIndex = -1;  // Mark that this dialogue session has ended.
            gamePanel.gameState = gamePanel.playState;  // Allow the player to walk away.
        }

        // Make NPC face the player.
        switch (gamePanel.player.direction) {
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    }
    // Optionally, a method to reset HP:
    public void resetHP() {
        hp = maxHP;
    }
}