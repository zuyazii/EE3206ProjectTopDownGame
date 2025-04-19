package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gamePanel;

    public CollisionChecker(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

    }

    public void checkTile(Entity entity) {
        int entityLeftWorldX = entity.worldx + entity.collisionBounds.x;
        int entityRightWorldX = entity.worldx + entity.collisionBounds.x + entity.collisionBounds.width;
        int entityTopWorldY = entity.worldy + entity.collisionBounds.y;
        int entityBottomWorldY = entity.worldy + entity.collisionBounds.y + entity.collisionBounds.height;

        int entityLeftCol = entityLeftWorldX / gamePanel.tileSize;
        int entityRightCol = entityRightWorldX / gamePanel.tileSize;
        int entityTopRow = entityTopWorldY / gamePanel.tileSize;
        int entityBottomRow = entityBottomWorldY / gamePanel.tileSize;

        int tileNum1, tileNum2;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityRightCol][entityTopRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.collisonOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityLeftCol][entityBottomRow];
                tileNum2 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityRightCol][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.collisonOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityLeftCol][entityBottomRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.collisonOn = true;
                }
                break;
            case "right":
                entityTopRow = (entityTopWorldY + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityLeftCol][entityTopRow];
                tileNum2 = gamePanel.tileManager.mapTileNum[gamePanel.currentMap][entityRightCol][entityTopRow];
                if (gamePanel.tileManager.tiles[tileNum1].collision || gamePanel.tileManager.tiles[tileNum2].collision) {
                    entity.collisonOn = true;
                }
                break;
        }
    }

    public int checkObject(Entity entity, boolean isPlayer) {
        int index = 999;

        for (int i = 0; i < gamePanel.superObject.length; i++) {

            if (gamePanel.superObject[gamePanel.currentMap][i] != null) {
                // Get entity's solid area position
                entity.collisionBounds.x = entity.worldx + entity.collisionBounds.x;
                entity.collisionBounds.y = entity.worldy + entity.collisionBounds.y;

                // Get object's solid area position
                gamePanel.superObject[gamePanel.currentMap][i].collisionBounds.x = gamePanel.superObject[gamePanel.currentMap][i].worldx + gamePanel.superObject[gamePanel.currentMap][i].collisionBounds.x;
                gamePanel.superObject[gamePanel.currentMap][i].collisionBounds.y = gamePanel.superObject[gamePanel.currentMap][i].worldy + gamePanel.superObject[gamePanel.currentMap][i].collisionBounds.y;

                switch(entity.direction) {
                    case "up":
                        entity.collisionBounds.y -= entity.speed;
                        if (entity.collisionBounds.intersects(gamePanel.superObject[gamePanel.currentMap][i].collisionBounds)) {
                            System.out.println("Collision Detected");
                            if (gamePanel.superObject[gamePanel.currentMap][i].collision) {
                                entity.collisonOn = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        entity.collisionBounds.y += entity.speed;
                        if (entity.collisionBounds.intersects(gamePanel.superObject[gamePanel.currentMap][i].collisionBounds)) {
                            System.out.println("Collision Detected");
                            if (gamePanel.superObject[gamePanel.currentMap][i].collision) {
                                entity.collisonOn = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        entity.collisionBounds.x -= entity.speed;
                        if (entity.collisionBounds.intersects(gamePanel.superObject[gamePanel.currentMap][i].collisionBounds)) {
                            System.out.println("Collision Detected");
                            if (gamePanel.superObject[gamePanel.currentMap][i].collision) {
                                entity.collisonOn = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        entity.collisionBounds.x += entity.speed;
                        if (entity.collisionBounds.intersects(gamePanel.superObject[gamePanel.currentMap][i].collisionBounds)) {
                            if (gamePanel.superObject[gamePanel.currentMap][i].collision) {
                                entity.collisonOn = true;
                            }
                            if (isPlayer) {
                                index = i;
                            }
                        }
                        break;
                }
                entity.collisionBounds.x = entity.solidAreaDefaultX;
                entity.collisionBounds.y = entity.solidAreaDefaultY;
                gamePanel.superObject[gamePanel.currentMap][i].collisionBounds.x = gamePanel.superObject[gamePanel.currentMap][i].solidAreaDefaultX;
                gamePanel.superObject[gamePanel.currentMap][i].collisionBounds.y = gamePanel.superObject[gamePanel.currentMap][i].solidAreaDefaultY;

            }
        }
        return index;
    }

    public int checkEntity(Entity entity, Entity[] target) {
        int index = 999;

        for (int i = 0; i < target.length; i++) {

            Entity e = target[i];
            if (e == null) continue;

            // skip if the entity is beaten
            if (e.isBeatened) {
                continue;
            }

            if (target[i] != null) {
                // Get entity's solid area position
                entity.collisionBounds.x = entity.worldx + entity.collisionBounds.x;
                entity.collisionBounds.y = entity.worldy + entity.collisionBounds.y;

                // Get object's solid area position
                target[i].collisionBounds.x = target[i].worldx + target[i].collisionBounds.x;
                target[i].collisionBounds.y = target[i].worldy + target[i].collisionBounds.y;

                switch(entity.direction) {
                    case "up":
                        entity.collisionBounds.y -= entity.speed;
                        if (entity.collisionBounds.intersects(target[i].collisionBounds)) {
                            entity.collisonOn = true;
                            index = i;
                        }
                        break;
                    case "down":
                        entity.collisionBounds.y += entity.speed;
                        if (entity.collisionBounds.intersects(target[i].collisionBounds)) {
                            entity.collisonOn = true;
                            index = i;
                        }
                        break;
                    case "left":
                        entity.collisionBounds.x -= entity.speed;
                        if (entity.collisionBounds.intersects(target[i].collisionBounds)) {
                            entity.collisonOn = true;
                            index = i;
                        }
                        break;
                    case "right":
                        entity.collisionBounds.x += entity.speed;
                        if (entity.collisionBounds.intersects(target[i].collisionBounds)) {
                            entity.collisonOn = true;
                            index = i;
                        }
                        break;
                }
                entity.collisionBounds.x = entity.solidAreaDefaultX;
                entity.collisionBounds.y = entity.solidAreaDefaultY;
                target[i].collisionBounds.x = target[i].solidAreaDefaultX;
                target[i].collisionBounds.y = target[i].solidAreaDefaultY;

            }
        }
        return index;
    }

    public void checkPlayer(Entity entity) {
        // Get entity's solid area position
        entity.collisionBounds.x = entity.worldx + entity.collisionBounds.x;
        entity.collisionBounds.y = entity.worldy + entity.collisionBounds.y;

        // Get object's solid area position
        gamePanel.player.collisionBounds.x = gamePanel.player.worldx + gamePanel.player.collisionBounds.x;
        gamePanel.player.collisionBounds.y = gamePanel.player.worldy + gamePanel.player.collisionBounds.y;

        switch(entity.direction) {
            case "up":
                entity.collisionBounds.y -= entity.speed;
                if (entity.collisionBounds.intersects(gamePanel.player.collisionBounds)) {
                    entity.collisonOn = true;
                }
                break;
            case "down":
                entity.collisionBounds.y += entity.speed;
                if (entity.collisionBounds.intersects(gamePanel.player.collisionBounds)) {
                    entity.collisonOn = true;
                }
                break;
            case "left":
                entity.collisionBounds.x -= entity.speed;
                if (entity.collisionBounds.intersects(gamePanel.player.collisionBounds)) {
                    entity.collisonOn = true;
                }
                break;
            case "right":
                entity.collisionBounds.x += entity.speed;
                if (entity.collisionBounds.intersects(gamePanel.player.collisionBounds)) {
                    entity.collisonOn = true;
                }
                break;
        }
        entity.collisionBounds.x = entity.solidAreaDefaultX;
        entity.collisionBounds.y = entity.solidAreaDefaultY;
        gamePanel.player.collisionBounds.x = gamePanel.player.solidAreaDefaultX;
        gamePanel.player.collisionBounds.y = gamePanel.player.solidAreaDefaultY;
    }
}