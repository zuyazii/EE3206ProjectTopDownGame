package object;

import item.Item;
import item.ItemType;
import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OBJ_Sword_Normal extends Item {

    private static UtilityTool utilTool;

    public OBJ_Sword_Normal(GamePanel gp) {
        super(1,
                "Normal Sword",
                "[Normal Sword]\nA basic sword that increases \nattack by 1.",
                ItemType.EQUIPMENT,
                1,
                0,    // No HP boost
                5,    // +5 attack boost
                0,    // No defense boost
                30,   // Price
                loadImage(gp, "/object/sword_normal", gp.tileSize, gp.tileSize));
    }

    private static BufferedImage loadImage(GamePanel gp, String path, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(OBJ_Sword_Normal.class.getResourceAsStream(path + ".png"));
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
