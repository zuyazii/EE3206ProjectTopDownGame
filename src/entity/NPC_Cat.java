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
    

    public NPC_Cat(GamePanel gamePanel) {
        super(gamePanel);
        direction = "down";
        speed = 0; // cat doesn't move

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
    	dialogues[0] = "0w0 \nI am glad that you finally woke up. ";
    	dialogues[1] = "030 \nYou are the hero I summoned. ";
        dialogues[2] = ">w< \nI have a task for you.\nThere are two monsters in the forest. ";
        dialogues[3] = "@w@ \nThey are killing my friends.  \nPlease defeat them!" ;
        dialogues[4] = ">w< \nI will send you back home after you defeating them. ";
        dialogues[5] = "0w0 \nThese potions will help you.\nDrink them to heal! ";
        
        // If you have more lines, add them.
    }

    @Override
    public void update() {
        // Simple idle "animation"
        spriteCounter++;
        if(spriteCounter > 30) {
            spriteNumber = (spriteNumber == 1) ? 2 : 1;
            spriteCounter = 0;
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

        if (dialogues[dialogueIndex] != null) {
            // Show the next dialogue line.
            gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
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
            // Reset dialogue and (optionally) wait for player to press Enter to exit dialogue.
            dialogueIndex = 0;
            gamePanel.ui.currentDialogue = "";
            // The NPC does NOT trigger a yes/no prompt so remain in dialogueState until the notification is dismissed.
            gamePanel.gameState = gamePanel.playState;
        }
    }
}
