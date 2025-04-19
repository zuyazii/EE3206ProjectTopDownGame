package object;

import item.Item;
import item.ItemType;
import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OBJ_Shield_Wood extends Item {

    private static UtilityTool utilTool;

    public OBJ_Shield_Wood(GamePanel gp) {
        super(2,
                "Wood Shield",
                "[Wood Shield]\nA simple wooden shield that \nincreases defense by 1.",
                ItemType.EQUIPMENT,
                1,
                0,    // No HP boost
                0,    // No attack boost
                5,    // +5 defense boost
                30,   // Price
                loadImage(gp, "/object/shield_wood", gp.tileSize, gp.tileSize));
    }

    private static BufferedImage loadImage(GamePanel gp, String path, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(OBJ_Shield_Wood.class.getResourceAsStream(path + ".png"));
            if (utilTool == null) {
                utilTool = new UtilityTool();
            }
            img = utilTool.scaleImage(img, width, height);
            return img;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
