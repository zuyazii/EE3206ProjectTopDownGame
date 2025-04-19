package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class UtilityTool {

    public BufferedImage scaleImage(BufferedImage original,int width, int height)
    {
        BufferedImage scaledImage = new BufferedImage(width,height,original.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(original,0,0,width,height,null);
        g2.dispose();

        return scaledImage;
    }

    public BufferedImage[] splitSheet(BufferedImage sheet, int cols, int rows) {
        int fw = sheet.getWidth()  / cols;
        int fh = sheet.getHeight() / rows;
        BufferedImage[] frames = new BufferedImage[cols * rows];
        int idx = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                frames[idx++] = sheet.getSubimage(x*fw, y*fh, fw, fh);
            }
        }
        return frames;
    }

    public BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }


}
