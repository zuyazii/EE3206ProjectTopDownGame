package entity;

import main.GamePanel;

public abstract class NPC extends Entity {

    // Dialogue fields.
    protected String[] dialogues;
    protected int dialogueIndex = 0;
    // Flag to make sure the item is given only once per session.
    protected boolean itemGiven = false;

    public NPC(GamePanel gp) {
        super(gp);
        // Allocate a dialogue array (size 20 is arbitrary, adjust as needed).
        dialogues = new String[20];
    }

    // Subclasses must initialize their dialogue lines.
    public abstract void setDialogue();

    // Subclasses must load their images (for simplicity, here we assume the same image
    // will be used for all directions or only one facing is needed).
    public abstract void setNPCImage();

    // Override the speak method to manage dialogue and eventually give an item.
    @Override
    public void speak() {
        // Only process if not already showing an option (for NPCs, we assume no yes/no prompt)
        if (!gamePanel.ui.showDialogueOptions) {
            // If there is dialogue remaining, display the next line.
            if (dialogues[dialogueIndex] != null) {
                gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
                dialogueIndex++;
            } else {
                // Dialogue finished; reset dialogue index so it can be repeated.
                dialogueIndex = 0;
                // If the item has not been given yet, give it now.
                if (!itemGiven) {
                    giveItem();
                    itemGiven = true;
                }
                // Return to playState.
                gamePanel.gameState = gamePanel.playState;
            }
        }
    }

    // This abstract method must be implemented by subclasses to give items to the player.
    public abstract void giveItem();
}
