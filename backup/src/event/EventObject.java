package event;

import main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class EventObject {
    protected int worldx, worldy;
    protected int width, height;
    protected Rectangle collisionBounds;
    public String promptMessage;

    public EventObject(int worldx, int worldy, int width, int height) {
        this.worldx = worldx;
        this.worldy = worldy;
        this.width = width;
        this.height = height;
        collisionBounds = new Rectangle(worldx, worldy, width, height);
    }

    // Check collision with the player.
    public boolean checkCollision(GamePanel gp) {
        // Get the player's collision area.
        Rectangle playerRect = gp.player.getCollisionArea();
        // Create the event's collision rectangle using its world position.
        Rectangle eventRect = new Rectangle(worldx + collisionBounds.x,
                worldy + collisionBounds.y,
                collisionBounds.width,
                collisionBounds.height);
        return playerRect.intersects(eventRect);
    }

    // Trigger the event (for example, start a map transition).
    public abstract void triggerEvent(GamePanel gp);

    // Optional: draw the event object (default does nothing).
    public void draw(Graphics2D g2, GamePanel gp) { }

    // Optional: draw collision bounds for debugging.
    public void drawCollisionBounds(Graphics2D g2, GamePanel gp) {
        int screenX = worldx - gp.player.worldx + gp.player.screenX;
        int screenY = worldy - gp.player.worldy + gp.player.screenY;
        g2.drawRect(screenX, screenY, width, height);
    }
}
