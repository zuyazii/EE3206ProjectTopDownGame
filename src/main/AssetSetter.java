package main;

import entity.NPC_Cat;
import event.DoorEvent;
import entity.ENEMY_Boss01;
import event.PortalEvent;

public class AssetSetter {
    GamePanel gamePanel;

    public AssetSetter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setObject(int currentMap) {
        switch (currentMap) {
            case 0:
                // Create a DoorEvent with door dimensions: width = tileSize, height = tileSize*2.
                DoorEvent door1 = new DoorEvent(
                        25 * gamePanel.tileSize,
                        30 * gamePanel.tileSize,
                        gamePanel.tileSize,
                        gamePanel.tileSize,
                        1,  // next map number
                        gamePanel,
                        false
                );
                door1.promptMessage = "Leave the room?";
                gamePanel.addEventObject(door1);
                break;
            case 1:
                // Create a DoorEvent with door dimensions: width = tileSize, height = tileSize*2.
                DoorEvent door2 = new DoorEvent(
                        13 * gamePanel.tileSize,
                        15 * gamePanel.tileSize,
                        gamePanel.tileSize,
                        gamePanel.tileSize,
                        0,  // next map number
                        gamePanel,
                        false
                );
                door2.promptMessage = "Enter the room?";
                gamePanel.addEventObject(door2);

                DoorEvent blackholeDoor1 = new DoorEvent(
                        41 * gamePanel.tileSize,
                        18 * gamePanel.tileSize,
                        gamePanel.tileSize,
                        gamePanel.tileSize * 4,
                        2,  // next map number
                        gamePanel,
                        true
                );
                blackholeDoor1.needEnemyBeDefeated = true;
                if (gamePanel.npc[1][0].isBeatened) {
                    blackholeDoor1.promptMessage = "Enter the forest..?";
                } else {
                    blackholeDoor1.promptMessage = "You can't enter the forest now. Beat the enemy first!";
                }
                gamePanel.addEventObject(blackholeDoor1);
                break;
            case 2:
                DoorEvent blackholeDoor2 = new DoorEvent(
                        8 * gamePanel.tileSize,
                        19 * gamePanel.tileSize,
                        gamePanel.tileSize,
                        gamePanel.tileSize * 3,
                        1,  // next map number
                        gamePanel,
                        true
                );
//                blackholeDoor2.promptMessage = "Back to the pond?";
//                gamePanel.addEventObject(blackholeDoor2);
//
//                PortalEvent portal1 = new PortalEvent(
//                        35 * gamePanel.tileSize,
//                        19 * gamePanel.tileSize,
//                        gamePanel.tileSize,
//                        gamePanel.tileSize * 3,
//                        1,  // next map number
//                        gamePanel
//                );
//                portal1.promptMessage = "Back to the castle?";
//                gamePanel.addEventObject(portal1);

                break;
        }
    }

    public void setNPC(int currentMap) {
        switch (currentMap) {
            case 0:
//                gamePanel.npc[0][0] = new ENEMY_Boss01(gamePanel);
//                gamePanel.npc[0][0].worldx = gamePanel.tileSize * 24;
//                gamePanel.npc[0][0].worldy = gamePanel.tileSize * 22;

                gamePanel.npc[currentMap][0] = new NPC_Cat(gamePanel);
                gamePanel.npc[currentMap][0].worldx = (int)(gamePanel.tileSize * 24.9);
                gamePanel.npc[currentMap][0].worldy = gamePanel.tileSize * 24;

                break;
            case 1:
                gamePanel.npc[currentMap][0] = new ENEMY_Boss01(gamePanel);
                gamePanel.npc[currentMap][0].worldx = gamePanel.tileSize * 36;
                gamePanel.npc[currentMap][0].worldy = gamePanel.tileSize * 19;
                break;
            case 2:
                gamePanel.npc[currentMap][0] = new ENEMY_Boss01(gamePanel);
                gamePanel.npc[currentMap][0].worldx = gamePanel.tileSize * 34;
                gamePanel.npc[currentMap][0].worldy = gamePanel.tileSize * 19;
                break;
        }
    }
}