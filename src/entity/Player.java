package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Player extends Entity {
    KeyHandler keyHandler;

    public final int screenX, screenY;


    public Player (GamePanel gamePanel, KeyHandler keyHandler) {
        super(gamePanel);

        hp = 200;

        this.keyHandler = keyHandler;

        screenX = gamePanel.screenWidth /2 - (gamePanel.tileSize/2);
        screenY = gamePanel.screenHeight/2 - (gamePanel.tileSize/2);

        collisionBounds = new Rectangle(8, 16, 32, 40);
        solidAreaDefaultX = collisionBounds.x;
        solidAreaDefaultY = collisionBounds.y;

        setDefaultValues();

        getPlayerImage();
    }

    public void setDefaultValues () {
        worldx = gamePanel.tileSize * 25;
        worldy = gamePanel.tileSize * 26;
        speed = 4;
        direction = "up";
    }

    public void getPlayerImage() {
        try {
//            up1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_1.png"));
//            up2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_2.png"));
//            down1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_1.png"));
//            down2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_2.png"));
//            left1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_1.png"));
//            left2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_2.png"));
//            right1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_1.png"));
//            right2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_2.png"));

            up1 = ImageIO.read(getClass().getResourceAsStream("/player2/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player2/up2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player2/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player2/down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player2/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player2/left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player2/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player2/right2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (keyHandler.upPressed || keyHandler.leftPressed || keyHandler.rightPressed || keyHandler.downPressed || keyHandler.enterPressed) {
            if (keyHandler.upPressed) {
                direction = "up";
            }
            else if (keyHandler.downPressed) {
                direction = "down";
            }
            else if (keyHandler.leftPressed) {
                direction = "left";
            }
            else if (keyHandler.rightPressed) {
                direction = "right";
            }

            // CHECK TILE COLLISION
            collisonOn = false;
            gamePanel.collisionChecker.checkTile(this);

            // CHECK OBJECT COLLISION
            int objectIndex = gamePanel.collisionChecker.checkObject(this, true);
            pickUpObject(objectIndex);

            // CHECK NPC COLLISION
            int npcIndex = gamePanel.collisionChecker.checkEntity(this, gamePanel.npc[gamePanel.currentMap]);
            interactNPC(npcIndex);

            if (!collisonOn && !keyHandler.enterPressed) {
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

            gamePanel.keyHandler.enterPressed = false;

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
    }

    private void interactNPC(int npcIndex) {
        if (npcIndex != 999) {
            if (gamePanel.keyHandler.enterPressed) {
                gamePanel.gameState = gamePanel.dialogueState;
                gamePanel.npc[gamePanel.currentMap][npcIndex].speak();
            }
        }
        gamePanel.keyHandler.enterPressed = false;
    }

    public void pickUpObject(int index) {
        if (index != 999) {

        }
    }

    public void draw(Graphics2D g2d) {
        // g2d.setColor(Color.BLACK);
        // g2d.fillRect(x, y, gamePanel.tileSize, gamePanel.tileSize);

        BufferedImage image = null;

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

    public Rectangle getCollisionArea() {
        return new Rectangle(worldx + collisionBounds.x, worldy + collisionBounds.y,
                collisionBounds.width, collisionBounds.height);
    }

    public void drawCollisionBounds(Graphics2D g2) {
        // Calculate the player's collision area on screen:
        int drawX = screenX + collisionBounds.x;
        int drawY = screenY + collisionBounds.y;

        g2.setColor(Color.RED);
        g2.drawRect(drawX, drawY, collisionBounds.width, collisionBounds.height);
    }
}
