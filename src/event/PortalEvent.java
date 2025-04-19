package event;

import main.GamePanel;
import javax.imageio.ImageIO;

import entity.ENEMY_Boss01;
import entity.Entity;
import main.UtilityTool;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class PortalEvent extends EventObject {


    private GamePanel gp;
    private UtilityTool util;
    private int nextMap;
    private BufferedImage[] emergeFrames, idleFrames, disappearFrames;
    private int frameIndex = 0, frameCounter = 0;
    private enum State { HIDDEN, EMERGE, IDLE, DISAPPEAR }
    private State state = State.HIDDEN;
    private boolean triggered = false;

    public PortalEvent(int x, int y, int w, int h, int nextMap, GamePanel gp) {
        super(x, y, w, h);
        collisionBounds = new Rectangle((int)(gp.tileSize/2), 0, w/2, h);
        this.nextMap = nextMap;
        this.gp = gp;
        util = new UtilityTool();

        // Assuming your sheet is laid out row‑by‑row:
        BufferedImage sheet = util.loadImage("/object/portal_sheet.png");
        BufferedImage[] all = util.splitSheet(sheet, 8, 3);
        emergeFrames    = Arrays.copyOfRange(all,  8*1, 8*2);  // row 1
        idleFrames      = Arrays.copyOfRange(all,  8*0, 8*1);  // row 0
        disappearFrames = Arrays.copyOfRange(all, 8*2, 8*3);  // row 2
    }

//    @Override
    public void update() {
        switch(state) {
            case HIDDEN:
                state = State.EMERGE;
                frameIndex = frameCounter = 0;
                break;
            case EMERGE:
                if (++frameCounter > 5) { // adjust speed
                    frameCounter = 0;
                    if (++frameIndex >= emergeFrames.length) {
                        state = State.IDLE;
                        frameIndex = 0;
                    }
                }
                break;
            case IDLE:
                if (++frameCounter > 10) {
                    frameCounter = 0;
                    frameIndex = (frameIndex + 1) % idleFrames.length;
                }
                break;
            case DISAPPEAR:
                if (++frameCounter > 5) {
                    frameCounter = 0;
                    if (++frameIndex >= disappearFrames.length) {
                        // fully gone—remove from event list
                        gp.eventObjects.remove(this);
                    }
                }
                break;
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        BufferedImage img = null;
        switch(state) {
            case EMERGE:    img = emergeFrames[frameIndex];    break;
            case IDLE:      img = idleFrames[frameIndex];      break;
            case DISAPPEAR: img = disappearFrames[frameIndex]; break;
            default: return;
        }
        int sx = worldx - gp.player.worldx + gp.player.screenX;
        int sy = worldy - gp.player.worldy + gp.player.screenY;
        g2.drawImage(img, sx, sy, width, height, null);
    }

    @Override
    public void triggerEvent(GamePanel gp) {
        // only when fully idling, and not already asked
        if (!triggered && state == State.IDLE) {
            triggered = true;
            gp.ui.optionText       = promptMessage;
            gp.ui.showDialogueOptions = true;     // draws YES/NO
            gp.gameState          = gp.dialogueState;
            gp.currentPortalEvent = this;         // you’ll need to add this field
        }
    }

    public void resetTriggered() {
        triggered = false;
    }

    public boolean isTriggered() {
        return triggered;
    }

    // called when player selects “YES” in KeyHandler:
    public void triggerPortalTransition(GamePanel gp) {
        if (!gp.isTransitionActive()) {
            gp.startTransition(nextMap, 1000);
            gp.ui.showDialogueOptions = false;
            gp.gameState = gp.playState;
        }
    }

}
