package object;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SuperObject {

    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int worldx, worldy;
    public Rectangle collisionBounds = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public void draw(Graphics2D g2d, GamePanel gamePanel) {

        int screenX = worldx - gamePanel.player.worldx + gamePanel.player.screenX;
        int screenY = worldy - gamePanel.player.worldy + gamePanel.player.screenY;

        if (worldx + gamePanel.tileSize > gamePanel.player.worldx - gamePanel.player.screenX &&
                worldx - gamePanel.tileSize < gamePanel.player.worldx + gamePanel.player.screenX &&
                worldy + gamePanel.tileSize > gamePanel.player.worldy - gamePanel.player.screenY &&
                worldy - gamePanel.tileSize < gamePanel.player.worldy + gamePanel.player.screenY ) {
            g2d.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
        }
    }

    public void setNextMap(int i) {
    }
    
    
}
