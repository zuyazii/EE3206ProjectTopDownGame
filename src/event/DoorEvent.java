package event;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DoorEvent extends EventObject {
    private int nextMap;
    private BufferedImage doorImage;

    public DoorEvent(int worldx, int worldy, int width, int height, int nextMap, GamePanel gamePanel) {
        super(worldx, worldy, width, height);
        this.nextMap = nextMap;
        // Load the door image.
        try {
            doorImage = ImageIO.read(getClass().getResourceAsStream("/object/door.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Adjust collision bounds: start half a tile above the door and extend 1.5 tiles tall.
        collisionBounds = new Rectangle(0, -gamePanel.tileSize / 2, gamePanel.tileSize, (int)(gamePanel.tileSize * 1.5));
    }

    @Override
    public void triggerEvent(GamePanel gp) {
        // Trigger the map transition only if one is not active.
        if (!gp.isTransitionActive()) {
            System.out.println("Door triggered: transitioning to map " + nextMap);
            // Start a fade transition that lasts 3000 ms (3 secs).
            gp.startTransition(nextMap, 3000);
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldx - gp.player.worldx + gp.player.screenX;
        int screenY = worldy - gp.player.worldy + gp.player.screenY;
        // Draw the door image scaled to one tile width and two tile heights.
        if (doorImage != null) {
            g2.drawImage(doorImage, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
        drawCollisionBounds(g2, gp);
    }

    public void drawCollisionBounds(Graphics2D g2, GamePanel gp) {
        // Convert world coordinates to screen coordinates for collision bounds.
        int screenX = worldx - gp.player.worldx + gp.player.screenX + collisionBounds.x;
        int screenY = worldy - gp.player.worldy + gp.player.screenY + collisionBounds.y;
        g2.setColor(Color.RED);
        g2.drawRect(screenX, screenY, collisionBounds.width, collisionBounds.height);
    }
}
