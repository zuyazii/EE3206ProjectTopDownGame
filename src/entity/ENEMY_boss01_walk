package entity;

import main.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import event.DoorEvent;
import event.EventObject;

public class ENEMY_Boss01 extends Enemy {

    // Additional idle frames for the boss (total 6 frames)
    public BufferedImage down1,down2,down3, down4, down5, down6,left1,left2,right1,right2;
    public int bossWidth;
    public int bossHeight;
    private final int scaleFactor = 3;
    private boolean defeated = false;
 // Add to your image declarations
    
    public BufferedImage[] deathFrames;
    private int currentDeathFrame = 0;
    private boolean isDying = false;
    private int deathAnimationCounter = 0;
    private final int DEATH_ANIMATION_SPEED = 8; // Adjust for speed

    private String lastMovingDirection = "down"; // Tracks last movement direction
    private boolean wasMoving = false; // Tracks if entity was moving last frame
    
    public ENEMY_Boss01(GamePanel gamePanel) {
        super(gamePanel);
        // Boss is static.
        speed = 1;
        direction = "down";
        enemyNum = 1;

        maxHP = 20;
        hp = maxHP;
        

        this.optionDialog = "Battle?";

       
        setDialogue();
        setBossSize(scaleFactor, scaleFactor);
        getImage();
    }
    
    public void getImage() {
    	 // Load idle animation images.
        down1 = setup("/enemies/boss_02/01_demon_idle/demon_idle_1");
        down2 = setup("/enemies/boss_02/01_demon_idle/demon_idle_2");
        down3 = setup("/enemies/boss_02/01_demon_idle/demon_idle_3");
        down4 = setup("/enemies/boss_02/01_demon_idle/demon_idle_4");
        down5 = setup("/enemies/boss_02/01_demon_idle/demon_idle_5");
        down6 = setup("/enemies/boss_02/01_demon_idle/demon_idle_6");

        left1 = setup("/enemies/boss_02/02_demon_walk/demon_walk_left1");
        left2 = setup("/enemies/boss_02/02_demon_walk/demon_walk_left3");
        
        right1 = setup("/enemies/boss_02/02_demon_walk/demon_walk_right1");
        right2 = setup("/enemies/boss_02/02_demon_walk/demon_walk_right3");
        
     // Add to your getImage() method
        deathFrames = new BufferedImage[4]; // Assuming 4 frames
        deathFrames[0] = setup("/enemies/boss_02/05_demon_death/demon_death_10");
        deathFrames[1] = setup("/enemies/boss_02/05_demon_death/demon_death_12");
        deathFrames[2] = setup("/enemies/boss_02/05_demon_death/demon_death_14");
        deathFrames[3] = setup("/enemies/boss_02/05_demon_death/demon_death_16");
    }

    private void setDialogue() {
        dialogues[0] = "erm!!!!!!!!!!!!!!!!";
    }

    public void setBossSize(int widthMultiplier, int heightMultiplier) {
        bossWidth = gamePanel.tileSize * widthMultiplier;
        bossHeight = gamePanel.tileSize * heightMultiplier;
        collisionBounds = new Rectangle(13 * scaleFactor, 17 * scaleFactor, 56 * scaleFactor, (int)(87 * 1.8));
    }

    @Override
    public void update() {
        // Boss remains idle.
    	if (defeated) return; // Don't update if defeated
    	
        // Check if HP reached zero
        if (hp <= 0 && !isDying) {
           startDeathAnimation();
        }
        
        if (isDying) {
            updateDeathAnimation();
            return;
        }
        
        
        
    	 setAction();
         
         // Check collision
         collisonOn = false;
         gamePanel.collisionChecker.checkTile(this);
         
         boolean isMovingThisFrame = false;
         // If collision is false, enemy can move
         if (!collisonOn) {
             switch(direction) {
                 case "up": worldy -= speed; isMovingThisFrame = true; break;
                 case "down": worldy += speed; isMovingThisFrame = true; break;
                 case "left": worldx -= speed; isMovingThisFrame = true; break;
                 case "right": worldx += speed; isMovingThisFrame = true; break;
             }
         }   
         
         
             
      // Animation counter
         spriteCounter++;
         if (spriteCounter > 6) {  // Animation speed
             if (spriteNumber < 4) {
                 spriteNumber++;
             } else {
                 spriteNumber = 1;
             }
             spriteCounter = 0;
         } 
         
         if (isMovingThisFrame) {
             lastMovingDirection = direction;
             wasMoving = true;
         } else {
             wasMoving = false;
         }
    }
    
    private void startDeathAnimation() {
        isDying = true;
        currentDeathFrame = 0;
        deathAnimationCounter = 0;
        gamePanel.playSE(4); // Play death sound
    }

    private void updateDeathAnimation() {
        deathAnimationCounter++;
        if (deathAnimationCounter > DEATH_ANIMATION_SPEED) {
            currentDeathFrame++;
            deathAnimationCounter = 0;
            
            if (currentDeathFrame >= deathFrames.length) {
                defeated = true;
                onDefeated();
            }
        }
    }

    
    private void onDefeated() {
        // Remove this enemy from the NPC array
    	for (int i = 0; i < gamePanel.npc[gamePanel.currentMap].length; i++) {
            if (gamePanel.npc[gamePanel.currentMap][i] == this) {
                gamePanel.npc[gamePanel.currentMap][i] = null;
                break;
            }
        }
        
        // Update door events
        for (EventObject ev : gamePanel.eventObjects) {
            if (ev instanceof DoorEvent) {
                DoorEvent door = (DoorEvent) ev;
                if (door.getNextMap() == 2) { // Make sure this matches your target map
                    door.promptMessage = "Enter the forest..?";
                    // Set the boss as defeated for door checking
                    this.isBeatened = true;
                }
            }
        }
    }
    @Override
    public void draw(Graphics2D g2d) {
    	if(defeated)return;
        
        int screenX = worldx - gamePanel.player.worldx + gamePanel.player.screenX;
        int screenY = worldy - gamePanel.player.worldy + gamePanel.player.screenY;

        if (isDying) {
            // Draw current death frame
            g2d.drawImage(deathFrames[currentDeathFrame], screenX, screenY, bossWidth, bossHeight, null);
        } 
        else if (isInView()) {
            // Draw movement animation
            BufferedImage image = getCurrentAnimationFrame();
            g2d.drawImage(image, screenX, screenY, bossWidth, bossHeight, null);
        }
    }
        private BufferedImage getCurrentAnimationFrame() {
        	String useDirection = wasMoving ? direction : lastMovingDirection;
        	switch (useDirection) {
                case "up":
                    switch(spriteNumber) {
                        case 1: return right1;
                        case 2: return right2;

                    }
                    break;
                case "down":
                    switch(spriteNumber) {
                        case 1: return left1;
                        case 2: return left2;

                    }
                    break;
                case "left":
                    switch(spriteNumber) {
                        case 1: return left1;
                        case 2: return left2;

                    }
                    break;
                case "right":
                    switch(spriteNumber) {
                        case 1: return right1;
                        case 2: return right2;
         
                    }
                    break;
            }
        	switch(lastMovingDirection) {
            case "up": return right2;
            case "down": return left2;
            case "left": return left2;
            case "right": return right2;
            default: return left2;
        }
           
        }
        
        public void setAction() {
        	if (isDying) return;
            
            actionLockCounter++;
            if(actionLockCounter == 80) {
            	Random random = new Random();
                int i = random.nextInt(100) + 1;
                
                if (i <= 25) direction = "up";
                else if (i <= 50) direction = "down";
                else if (i <= 75) direction = "left";
                else direction = "right";
                
                actionLockCounter = 0;
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
    	if (defeated) return;// Implement enemy-specific battle logic here.
    }
    
    private boolean isInView() {
        return worldx + bossWidth > gamePanel.player.worldx - gamePanel.player.screenX &&
               worldx - bossWidth < gamePanel.player.worldx + gamePanel.player.screenX &&
               worldy + bossHeight > gamePanel.player.worldy - gamePanel.player.screenY &&
               worldy - bossHeight < gamePanel.player.worldy + gamePanel.player.screenY;
    }
}

