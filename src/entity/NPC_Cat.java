package entity;

import main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NPC_Cat extends Entity {

    private boolean hasGivenItem = false; // only give items once
    private int npcWidth;
    private int npcHeight;
    private boolean finalDialoguesStarted = false;

    public NPC_Cat(GamePanel gamePanel) {
        super(gamePanel);
        direction = "down";
        speed = 0; // cat doesn't move
        isBeatened = false;

        // Enlarge sprite to 2x tileSize (for example)
        npcWidth = (int)(gamePanel.tileSize * 1.3);
        npcHeight = (int)(gamePanel.tileSize * 1.3);

        // Adjust collision so it matches the bigger sprite
        collisionBounds = new Rectangle(0, 0, npcWidth, npcHeight);
        solidAreaDefaultX = collisionBounds.x;
        solidAreaDefaultY = collisionBounds.y;

        getImage();
        setDialogue();
    }

    public void getImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/npc/WhiteCat_Stand_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/npc/WhiteCat_Stand_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDialogue() {
        // Add multiple lines
        if (gamePanel.npc[2][0] == null && !gamePanel.portalNotYetAdded) {
            finalDialoguesStarted = true;
            dialogues[0] = "0w0 \nYou did it! You defeated all the monsters!";
            dialogues[1] = ">w< \nThank you for saving my friends!";
            dialogues[2] = "030 \nOur land is safe again because of you.";
            dialogues[3] = ">w< \nFarewell, brave hero!";
            dialogues[4] = "0w0 \nMeow~ The adventure ends here!";
            dialogues[5] = null;
            dialogues[6] = null;
        } else {
            dialogues[0] = "0w0 \nI am glad that you finally woke up. ";
            dialogues[1] = "030 \nYou are the hero I summoned. ";
            dialogues[2] = ">w< \nI have a task for you.\nThere are two monsters in the forest. ";
            dialogues[3] = "@w@ \nThey are killing my friends.  \nPlease defeat them!";
            dialogues[4] = ">w< \nI will send you back home after you defeating them. ";
            dialogues[5] = "0w0 \nThese potions will help you.\nPress [e] to open the inventory.\nYou can drink them to heal! ";
            dialogues[6] = ">w0 \nCome back here after defeating the monsters! \nI will wait you here!";
        }
    }

    @Override
    public void update() {
        // Simple idle "animation"
        spriteCounter++;
        if(spriteCounter > 30) {
            spriteNumber = (spriteNumber == 1) ? 2 : 1;
            spriteCounter = 0;
        }
        
        // Check if we should switch to final dialogues
        if (gamePanel.npc[2][0] == null && !gamePanel.portalNotYetAdded && !finalDialoguesStarted) {
            setDialogue();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldx - gamePanel.player.worldx + gamePanel.player.screenX;
        int screenY = worldy - gamePanel.player.worldy + gamePanel.player.screenY;

        // Only draw if on screen
        if (worldx + npcWidth > gamePanel.player.worldx - gamePanel.player.screenX &&
                worldx - npcWidth < gamePanel.player.worldx + gamePanel.player.screenX &&
                worldy + npcHeight > gamePanel.player.worldy - gamePanel.player.screenY &&
                worldy - npcHeight < gamePanel.player.worldy + gamePanel.player.screenY) {

            BufferedImage image;
            if (spriteNumber == 1)
                image = down1;
            else
                image = down2;
            g2.drawImage(image, screenX, screenY, npcWidth, npcHeight, null);
        }
    }

    @Override
    public void speak() {
        if (dialogueIndex < 0) {
            dialogueIndex = 0;
        }

        // Check if we should switch to final dialogues
        if (gamePanel.npc[2][0] == null && !gamePanel.portalNotYetAdded && !finalDialoguesStarted) {
            setDialogue();
        }

        if (dialogues[dialogueIndex] != null) {
            // Show the next dialogue line.
            gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
            
            // Check if this is the final line
            if (finalDialoguesStarted && dialogues[dialogueIndex].equals("0w0 \nMeow~ The adventure ends here!")) {
                // Immediately return to title after showing this line
                gamePanel.gameState = gamePanel.titleState;
                return;
            }
            
            dialogueIndex++;
            gamePanel.gameState = gamePanel.dialogueState;
        } else {
            // All dialogue lines have been shown.
            if (!hasGivenItem) {
                hasGivenItem = true;
                // Create two potions and add to player's inventory.
                object.OBJ_Potion_Red potion1 = new object.OBJ_Potion_Red(gamePanel);
                object.OBJ_Potion_Red potion2 = new object.OBJ_Potion_Red(gamePanel);
                gamePanel.player.inventory.addItem(potion1);
                gamePanel.player.inventory.addItem(potion2);
                // Show a notification that the items have been given.
                gamePanel.ui.showItemNotification("Received 2 Red Potions!");

                dialogues[0] = "I have nothing more to say.\nGood luck!";
                dialogues[1] = null;
                dialogues[2] = null;
            }
            
            // Reset dialogue
            dialogueIndex = 0;
            gamePanel.ui.currentDialogue = "";
            gamePanel.gameState = gamePanel.playState;
        }
    }
}