package main;

import entity.ENEMY_Boss01;
import entity.NPC_OldMan;
import object.OBJ_Boots;
import object.OBJ_Chest;
import object.OBJ_Door;
import object.OBJ_Key;

import java.awt.*;

public class AssetSetter {

    GamePanel gamePanel;

    public AssetSetter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }


    // Instantiate some default object and place them on the map
    public void setObject(int currentMap) {
        switch (currentMap) {
            case 0:
                OBJ_Door door = new OBJ_Door(gamePanel);
                // Set the door's world coordinates (multiply by tileSize to convert from tile coordinates to pixels)
                door.worldx = 25 * gamePanel.tileSize;
                door.worldy = 30 * gamePanel.tileSize;
                // Set next map number if needed
                door.setNextMap(1); // For example, switch to map 2

                gamePanel.superObject[0][0] = door;
        }
    }

    public void setNPC(int currentMap) {
        switch (currentMap) {
            case 0:
                gamePanel.npc[0][0] = new ENEMY_Boss01(gamePanel);
                gamePanel.npc[0][0].worldx = gamePanel.tileSize * 24;
                gamePanel.npc[0][0].worldy = gamePanel.tileSize * 22;
            case 1:
                gamePanel.npc[1][0] = new NPC_OldMan(gamePanel);
                gamePanel.npc[1][0].worldx = gamePanel.tileSize * 31;
                gamePanel.npc[1][0].worldy = gamePanel.tileSize * 26;
        }
    }
}

