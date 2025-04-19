package entity;

import main.GamePanel;

public abstract class Enemy extends Entity {
	
    public Enemy(GamePanel gamePanel) {
        super(gamePanel);
        isEnemy = true;
    }
    
    
    // Abstract method for enemy-specific battle actions.
    public abstract void performBattleAction();
}
