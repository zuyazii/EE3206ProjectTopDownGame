package object;

import item.Item;
import item.ItemType;
import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OBJ_Potion_Red extends Item {

    private static UtilityTool utilTool;

    public OBJ_Potion_Red(GamePanel gp) {
        super(3,                                  // ID (unique)
                "Red Potion",                     // Name
                "[Red Potion]\nHeals 10 HP.",       // Description (displayed in inventory)
                ItemType.CONSUMABLE,              // Type (consumable)
                1,                                // Initial quantity
                10,                                // Healing value: heals 10 HP
                0,                                // No attack boost
                0,                                // No defense boost
                50,                               // Price
                loadImage(gp, "/object/potion_red", gp.tileSize, gp.tileSize)); // Load icon image
    }

    private static BufferedImage loadImage(GamePanel gp, String path, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(OBJ_Potion_Red.class.getResourceAsStream(path + ".png"));
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
