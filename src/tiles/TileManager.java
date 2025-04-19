package tiles;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

public class TileManager {
    GamePanel gamePanel;
    public Tile[] tiles;
    public int mapTileNum[][][];

    public TileManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        tiles = new Tile[36];
        mapTileNum = new int[gamePanel.maxMap][gamePanel.maxWorldCol][gamePanel.maxWorldRow];

        giveTileImage();
        loadMap("/map/map_getQuest.txt", 0);
        loadMap("/map/map_boss01.txt", 1);
        loadMap("/map/map_boss02.txt", 2);
    }

    public void loadMap(String filePath, int map) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            int col = 0;
            int row = 0;
            while (col < gamePanel.maxWorldCol  && row < gamePanel.maxWorldRow) {
                String line = br.readLine();

                while (col < gamePanel.maxWorldCol) {
                    String numbers[] = line.split(" ");

                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[map][col][row] = num;
                    col++;
                }

                if (col == gamePanel.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (IOException e) {}
    }

    public void giveTileImage() {
        try {
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/earth.png"));

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/floor01.png"));

            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass00.png"));

            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass01.png"));

            tiles[4] = new Tile();
            tiles[4].image = ImageIO.read(getClass().getResourceAsStream("/tiles/hut.png"));
            tiles[4].collision = true;

            tiles[5] = new Tile();
            tiles[5].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road00.png"));

            tiles[6] = new Tile();
            tiles[6].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road01.png"));

            tiles[7] = new Tile();
            tiles[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road02.png"));

            tiles[8] = new Tile();
            tiles[8].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road03.png"));

            tiles[9] = new Tile();
            tiles[9].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road04.png"));

            tiles[10] = new Tile();
            tiles[10].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road05.png"));

            tiles[11] = new Tile();
            tiles[11].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road06.png"));

            tiles[12] = new Tile();
            tiles[12].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road07.png"));

            tiles[13] = new Tile();
            tiles[13].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road08.png"));

            tiles[14] = new Tile();
            tiles[14].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road09.png"));

            tiles[15] = new Tile();
            tiles[15].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road10.png"));

            tiles[16] = new Tile();
            tiles[16].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road11.png"));

            tiles[17] = new Tile();
            tiles[17].image = ImageIO.read(getClass().getResourceAsStream("/tiles/road12.png"));

            tiles[18] = new Tile();
            tiles[18].image = ImageIO.read(getClass().getResourceAsStream("/tiles/table01.png"));
            tiles[18].collision = true;

            tiles[19] = new Tile();
            tiles[19].image = ImageIO.read(getClass().getResourceAsStream("/tiles/tree.png"));
            tiles[19].collision = true;

            tiles[20] = new Tile();
            tiles[20].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall.png"));
//            tiles[20].collision = true;

            tiles[21] = new Tile();
            tiles[21].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water00.png"));
            tiles[21].collision = true;

            tiles[22] = new Tile();
            tiles[22].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water01.png"));
            tiles[22].collision = true;

            tiles[23] = new Tile();
            tiles[23].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water02.png"));
            tiles[23].collision = true;

            tiles[24] = new Tile();
            tiles[24].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water03.png"));
            tiles[24].collision = true;

            tiles[25] = new Tile();
            tiles[25].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water04.png"));
            tiles[25].collision = true;

            tiles[26] = new Tile();
            tiles[26].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water05.png"));
            tiles[26].collision = true;

            tiles[27] = new Tile();
            tiles[27].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water06.png"));
            tiles[27].collision = true;

            tiles[28] = new Tile();
            tiles[28].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water07.png"));
            tiles[28].collision = true;

            tiles[29] = new Tile();
            tiles[29].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water08.png"));
            tiles[29].collision = true;

            tiles[30] = new Tile();
            tiles[30].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water09.png"));
            tiles[30].collision = true;

            tiles[31] = new Tile();
            tiles[31].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water10.png"));
            tiles[31].collision = true;

            tiles[32] = new Tile();
            tiles[32].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water11.png"));
            tiles[32].collision = true;

            tiles[33] = new Tile();
            tiles[33].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water12.png"));
            tiles[33].collision = true;

            tiles[34] = new Tile();
            tiles[34].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water13.png"));
            tiles[34].collision = true;

            tiles[35] = new Tile();
            tiles[35].image = ImageIO.read(getClass().getResourceAsStream("/tiles/z.png"));
            tiles[35].collision = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d) {

        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gamePanel.maxWorldCol  && worldRow < gamePanel.maxWorldRow) {

            int tileNum = mapTileNum[gamePanel.currentMap][worldCol][worldRow];

            int worldX = worldCol * gamePanel.tileSize;
            int worldY = worldRow * gamePanel.tileSize;
            int screenX = worldX - gamePanel.player.worldx + gamePanel.player.screenX;
            int screenY = worldY - gamePanel.player.worldy + gamePanel.player.screenY;

            if (worldX + gamePanel.tileSize > gamePanel.player.worldx - gamePanel.player.screenX &&
                worldX - gamePanel.tileSize < gamePanel.player.worldx + gamePanel.player.screenX &&
                worldY + gamePanel.tileSize > gamePanel.player.worldy - gamePanel.player.screenY &&
                worldY - gamePanel.tileSize < gamePanel.player.worldy + gamePanel.player.screenY ) {
                g2d.drawImage(tiles[tileNum].image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
            }

            worldCol++;

            if (worldCol == gamePanel.maxWorldCol) {
                worldCol = 0;

                worldRow++;

            }
        }

    }
}
