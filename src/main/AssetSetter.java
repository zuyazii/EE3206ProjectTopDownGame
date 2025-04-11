package main;

import event.DoorEvent;
import entity.ENEMY_Boss01;
import entity.NPC_OldMan;

public class AssetSetter {
    GamePanel gamePanel;

    public AssetSetter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setObject(int currentMap) {
        switch (currentMap) {
            case 0:
                // Create a DoorEvent with door dimensions: width = tileSize, height = tileSize*2.
                DoorEvent door = new DoorEvent(
                        25 * gamePanel.tileSize,
                        30 * gamePanel.tileSize,
                        gamePanel.tileSize,
                        gamePanel.tileSize,
                        1,  // next map number
                        gamePanel
                );
                door.promptMessage = "Leave the room?";
                gamePanel.addEventObject(door);
                break;
        }
    }

    public void setNPC(int currentMap) {
        switch (currentMap) {
            case 0:
                gamePanel.npc[0][0] = new ENEMY_Boss01(gamePanel);
                gamePanel.npc[0][0].worldx = gamePanel.tileSize * 24;
                gamePanel.npc[0][0].worldy = gamePanel.tileSize * 22;

//                gamePanel.npc[1][0] = new NPC_OldMan(gamePanel);
//                gamePanel.npc[1][0].worldx = gamePanel.tileSize * 24;
//                gamePanel.npc[1][0].worldy = gamePanel.tileSize * 22;

                break;
            case 1:
                gamePanel.npc[1][0] = new NPC_OldMan(gamePanel);
                gamePanel.npc[1][0].worldx = gamePanel.tileSize * 31;
                gamePanel.npc[1][0].worldy = gamePanel.tileSize * 26;
                break;
        }
    }
}
