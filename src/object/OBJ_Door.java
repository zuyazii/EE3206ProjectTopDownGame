package object;

import entity.Player;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class OBJ_Door extends SuperObject {
    public int nextmapNum;
    public int worldx, worldy;  // Make sure these are assigned!

    public OBJ_Door(GamePanel gamePanel){
        name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/object/door.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;

        collisionBounds = new Rectangle(0, 0, gamePanel.tileSize, gamePanel.tileSize * 2);
    }

    public void setNextMap(int mapNum) {
        nextmapNum = mapNum;
    }

    // Optional: a helper to get the door's absolute collision area.
    public Rectangle getCollisionArea() {
        return new Rectangle(worldx + collisionBounds.x, worldy + collisionBounds.y,
                collisionBounds.width, collisionBounds.height);
    }

    public void drawCollisionBounds(Graphics2D g2, GamePanel gp) {
        // Convert world coords to screen coords
        int screenX = worldx - gp.player.worldx + gp.player.screenX + collisionBounds.x;
        int screenY = worldy - gp.player.worldy + gp.player.screenY + collisionBounds.y;

        g2.setColor(Color.RED);
        g2.drawRect(screenX, screenY, collisionBounds.width, collisionBounds.height);
    }

    // In SuperObject or OBJ_Door:
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldx - gp.player.worldx + gp.player.screenX;
        int screenY = worldy - gp.player.worldy + gp.player.screenY;

        // Only draw if the door is on screen:
        if (worldx + gp.tileSize > gp.player.worldx - gp.player.screenX &&
                worldx - gp.tileSize < gp.player.worldx + gp.player.screenX &&
                worldy + gp.tileSize * 2 > gp.player.worldy - gp.player.screenY &&  // if door image is 2 tiles high
                worldy - gp.tileSize < gp.player.worldy + gp.player.screenY) {

            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}


