//package main;
//
//import entity.Player;
//import entity.Entity;
//
//import java.awt.*;
//import java.util.Random;
//
//public class Battle {
//
//    GamePanel gp;
//    Player player;
//    Entity enemy;
//    Random random = new Random();
//
//    // Animation trigger fields
//    private int roundAnimationTimer = 0;
//
//    public boolean awaitingPendingAction = false;
//    private Object pendingAttacker;
//    private Object pendingDefender;
//    private Action pendingAction;
//    private boolean pendingIsPlayer;
//    public boolean pendingEscapeSuccess = false;  // Flag to indicate escape success.
//
//    // The possible actions
//    public enum Action {
//        ESCAPE, INVENTORY, DEFENSE, ATTACK;
//    }
//
//    public Battle(GamePanel gp, Player player, Entity enemy) {
//        this.gp = gp;
//        this.player = player;
//        this.enemy = enemy;
//
//        // Reset HP to the stored maximum values
////        this.player.resetHP();
//        this.enemy.resetHP();
//    }
//
//    // Map UI battleCommandNum (0: ATTACK, 1: DEFENSE, 2: ESCAPE, 3: INVENTORY)
//    // to an Action.
//    public Action getPlayerAction() {
//        int cmd = gp.ui.battleCommandNum;
//        switch(cmd) {
//            case 0: return Action.ATTACK;
//            case 1: return Action.DEFENSE;
//            case 2: return Action.ESCAPE;
//            case 3: return Action.INVENTORY;
//            default: return Action.ATTACK;
//        }
//    }
//
//    // For the enemy, choose a random action.
//    public Action getEnemyAction() {
//        int r = random.nextInt(2);  // returns 0-3
//        switch(r) {
//            case 0: return Action.ATTACK;
//            case 1: return Action.DEFENSE;
////            case 2: return Action.INVENTORY2
////            case 3: return Action.ESCAPE;
//            default: return Action.ATTACK;
//        }
//    }
//
//    // Execute a full round of battle.
//    public void executeRound() {
//        // Reset defending flags and new pending flags.
//        player.isDefending = false;
//        enemy.isDefending = false;
//        pendingEscapeSuccess = false;
//        awaitingPendingAction = false;
//
//        Action playerAction = getPlayerAction();
//        Action enemyAction = getEnemyAction();
//
//        System.out.println("Player action: " + playerAction);
//        System.out.println("Enemy action: " + enemyAction);
//
//        // Determine turn order.
//        int playerPrio = getPriority(playerAction);
//        int enemyPrio  = getPriority(enemyAction);
//        boolean playerFirst;
//        if (playerPrio < enemyPrio) {
//            playerFirst = true;
//        } else if (playerPrio > enemyPrio) {
//            playerFirst = false;
//        } else {
//            playerFirst = random.nextBoolean();
//        }
//
//        if (playerFirst) {
//            // Execute the player's action.
//            executeAction(player, enemy, playerAction, true);
//            if (enemy.hp <= 0) {
//                System.out.println("Enemy defeated immediately!");
//                gp.gameState = gp.playState;
//                return;
//            }
//            // If player's action was escape and it succeeded, pendingEscapeSuccess will be true.
//            if (playerAction == Action.ESCAPE && pendingEscapeSuccess) {
//                // Stop here and wait for the user to press Enter.
//                return;
//            }
//            // Otherwise, store enemy's action for later execution.
//            pendingAttacker = enemy;
//            pendingDefender = player;
//            pendingAction = enemyAction;
//            pendingIsPlayer = false;
//            awaitingPendingAction = true;
//        } else {
//            // Enemy acts first.
//            executeAction(enemy, player, enemyAction, false);
//            if (player.hp <= 0) {
//                System.out.println("Player defeated!");
//                gp.gameState = gp.gameOverState; // Now going to gameOverState.
//                return;
//            }
//            // If enemy action was escape and successful, (optional)
//            if (enemyAction == Action.ESCAPE && pendingEscapeSuccess) {
//                return;
//            }
//            // Store player's action as pending.
//            pendingAttacker = player;
//            pendingDefender = enemy;
//            pendingAction = playerAction;
//            pendingIsPlayer = true;
//            awaitingPendingAction = true;
//        }
//    }
//
//    // A helper to get action priority (lower value means the action goes first)
//    private int getPriority(Action action) {
//        switch(action) {
//            case ESCAPE:    return 1;
//            case INVENTORY: return 2;
//            case DEFENSE:   return 3;
//            case ATTACK:    return 4;
//            default:        return 5;
//        }
//    }
//
//    // Execute a single action.
//    // isPlayer indicates if the attacker is the player.
//    public void executeAction(Object attacker, Object defender, Action action, boolean isPlayer) {
//        if (action == Action.ATTACK) {
//            int atkVal = getAttackValue(attacker);
//            int defVal = (isDefending(defender)) ? getDefenseValue(defender) : 0;
//            int damage = atkVal - defVal;
//            if (damage < 0) damage = 0;
//            reduceHP(defender, damage);
//
//            // Then check if battle is over now:
//            if (isBattleOver()) {
//                // Stop further action if the battle ended.
//                return;
//            }
//
//            if (isPlayer) {
//                // Player attacks enemy: trigger enemy "take_hit" animation.
//                triggerAnimation("take_hit", 30);
//                gp.playSE(5);
//                System.out.println("Player attacks enemy for " + damage + " damage.");
//                gp.ui.showBattleNotification("Player Attacked. Enemy takes " + damage + " damage!");
//            } else {
//                // Enemy attacks player: trigger enemy "cleave" animation.
////                triggerAnimation("cleave", 30);
//                System.out.println("Enemy attacks player for " + damage + " damage.");
//                gp.ui.showBattleNotification("Enemy Attacked. Player takes " + damage + " damage!");
//                // Trigger red vignette effect on the UI for, say, 20 frames.
//                gp.ui.triggerRedVignette(20);
//            }
//        }
//        else if (action == Action.DEFENSE) {
//            markAsDefending(attacker);
//            System.out.println((isPlayer ? "Player" : "Enemy") + " defends.");
//            gp.ui.showBattleNotification((isPlayer ? "Player" : "Enemy") + " is defending!");
//        }
//        else if (action == Action.ESCAPE) {
//            boolean escaped = random.nextBoolean(); // 50% chance.
//            if (escaped) {
//                System.out.println((isPlayer ? "Player" : "Enemy") + " escapes successfully!");
//                if (isPlayer) {
//                    gp.ui.showBattleEscapeNotification("Player escaped!");
//                    pendingEscapeSuccess = true;
//                } else {
//                    gp.ui.showBattleNotification("Enemy escaped!");
//                }
//            } else {
//                System.out.println((isPlayer ? "Player" : "Enemy") + " fails to escape.");
//                gp.ui.showBattleNotification((isPlayer ? "Player" : "Enemy") + " failed to escape!");
//            }
//        }
//
//        else if (action == Action.INVENTORY) {
//            if (isPlayer) {
//                // Open the battle inventory mode.
//                gp.ui.battleInventoryActive = true;
//                gp.ui.battleInvSelection = 0;
//                gp.ui.battleInvOffset = 0;
//                System.out.println("Player opens battle inventory.");
//                // Optionally show a notification message on the battle UI:
////                gp.ui.showBattleNotification("Select a consumable to use");
//            } else {
//                // For an enemy, you might not implement inventory use.
//                gp.ui.showBattleNotification("Enemy inventory use not allowed!");
//            }
//        }
//    }
//
//    private int getAttackValue(Object actor) {
//        if (actor instanceof Player) {
//            Player p = (Player) actor;
//            // Generate a base attack value.
//            int baseAttack = p.attackVal.nextInt(11) + 10;
//            // Sum the attack bonus from all equipment items.
//            int equipmentBonus = 0;
//            for (item.Item i : p.inventory.items) {
//                if(i.type == item.ItemType.EQUIPMENT) {
//                    equipmentBonus += i.attackBoost;
//                }
//            }
//            return baseAttack + equipmentBonus;
//        } else if (actor instanceof Entity) {
//            return ((Entity) actor).attackVal.nextInt(11) + 10;
//        }
//        return 0;
//    }
//
//    private int getDefenseValue(Object actor) {
//        if (actor instanceof Player) {
//            Player p = (Player) actor;
//            // Generate a base defense value.
//            int baseDefense = p.defenseVal.nextInt(11) + 5;
//            // Sum the defense bonus from all equipment items.
//            int equipmentBonus = 0;
//            for (item.Item i : p.inventory.items) {
//                if(i.type == item.ItemType.EQUIPMENT) {
//                    equipmentBonus += i.defenseBoost;
//                }
//            }
//            return baseDefense + equipmentBonus;
//        } else if (actor instanceof Entity) {
//            return ((Entity) actor).defenseVal.nextInt(11) + 5;
//        }
//        return 0;
//    }
//
//    private boolean isDefending(Object actor) {
//        if(actor instanceof Player) {
//            return ((Player)actor).isDefending;
//        } else if(actor instanceof Entity) {
//            return ((Entity)actor).isDefending;
//        }
//        return false;
//    }
//
//    private void markAsDefending(Object actor) {
//        if(actor instanceof Player) {
//            ((Player)actor).isDefending = true;
//        } else if(actor instanceof Entity) {
//            ((Entity)actor).isDefending = true;
//        }
//    }
//
//    private void reduceHP(Object actor, int damage) {
//        if(actor instanceof Player) {
//            ((Player)actor).hp -= damage;
//        } else if(actor instanceof Entity) {
//            ((Entity)actor).hp -= damage;
//        }
//    }
//
//    private boolean isBattleOver() {
//        // If the player is defeated, go to GAME OVER.
//        if (player.hp <= 0) {
//            System.out.println("Player defeated!");
//            gp.gameState = gp.gameOverState;
//            return true;
//        }
//        // If the enemy is defeated, show a battle notification without changing state immediately.
//        if (enemy.hp <= 0) {
//            System.out.println("Enemy defeated!");
//            gp.ui.showBattleNotification("Enemy is defeated!");
//            // Do not change the game state yet; let the key handler (or UI) handle dismissal.
//            return true;
//        }
//        return false;
//    }
//
//    // Helper method:
//    public boolean isEnemyDefeated() {
//        return enemy.hp <= 0;
//    }
//
//    public void resumeRound() {
//        // If a successful escape occurred, transition to playState.
//        if (pendingEscapeSuccess) {
//            gp.gameState = gp.playState;
//            pendingEscapeSuccess = false;
//            return;
//        }
//
//        // Otherwise, if there's a pending action, execute it.
//        if (!awaitingPendingAction) return;
//
//        executeAction(pendingAttacker, pendingDefender, pendingAction, pendingIsPlayer);
//        awaitingPendingAction = false;
//
//        if ((pendingDefender instanceof Player && ((Player)pendingDefender).hp <= 0) ||
//                (pendingDefender instanceof Entity && ((Entity)pendingDefender).hp <= 0)) {
//            if (pendingDefender instanceof Player) {
//                gp.gameState = gp.gameOverState;  // If player is defeated, show Game Over.
//            } else {
//                gp.gameState = gp.playState;
//            }
//            return;
//        }
//
//        gp.ui.battleCommandNum = 0;
//    }
//
//    // --- Animation Methods for Battle Rounds ---
//
//    // Call this each frame from UI.drawBattleScreen().
//    public void updateRoundAnimation() {
//        if (roundAnimationTimer > 0) {
//            roundAnimationTimer--;
//            if (roundAnimationTimer == 0) {
//                // When animation time is up, revert enemy state to idle.
//                gp.ui.setEnemyState("idle");
//            }
//        }
//    }
//
//    // This method is here if you want to draw extra round effects.
//    // In this example, the UI already draws the enemy animation based on its state.
//    public void drawRoundAnimation(Graphics2D g2) {
//        // You can add additional visual effects here if needed.
//    }
//
//    // Call this to trigger an enemy animation for a set duration.
//    public void triggerAnimation(String anim, int duration) {
//        roundAnimationTimer = duration;
//        gp.ui.setEnemyState(anim);
//    }
//
//}


package main;

import entity.Player;
import entity.Entity;
import java.awt.*;
import java.util.Random;

public class Battle {

    GamePanel gp;
    Player player;
    Entity enemy;  // This must be non-null when starting a battle!
    Random random = new Random();

    // Animation trigger fields
    private int roundAnimationTimer = 0;

    public boolean awaitingPendingAction = false;
    private Object pendingAttacker;
    private Object pendingDefender;
    private Action pendingAction;
    private boolean pendingIsPlayer;
    public boolean pendingEscapeSuccess = false;  // Indicates a successful escape.

    // The possible actions
    public enum Action {
        ESCAPE, INVENTORY, DEFENSE, ATTACK;
    }

    public Battle(GamePanel gp, Player player, Entity enemy) {
        this.gp = gp;
        this.player = player;
        // Check that enemy is not null!
        if (enemy == null) {
            System.err.println("Error: Cannot start battle because enemy is null!");
        }
        this.enemy = enemy;
        // Reset enemy HP when battle starts
        if (this.enemy != null) {
            this.enemy.resetHP();
        }
    }

    // Maps the UI battleCommandNum to a battle action.
    public Action getPlayerAction() {
        int cmd = gp.ui.battleCommandNum;
        switch (cmd) {
            case 0: return Action.ATTACK;
            case 1: return Action.DEFENSE;
            case 2: return Action.ESCAPE;
            case 3: return Action.INVENTORY;
            default: return Action.ATTACK;
        }
    }

    // Chooses a random action for the enemy.
    public Action getEnemyAction() {
        int r = random.nextInt(2);  // 0 or 1
        switch (r) {
            case 0: return Action.ATTACK;
            case 1: return Action.DEFENSE;
            default: return Action.ATTACK;
        }
    }

    // Execute a full round of battle.
    public void executeRound() {
        // Reset defending flags and pending action flags.
        player.isDefending = false;
        enemy.isDefending = false;
        pendingEscapeSuccess = false;
        awaitingPendingAction = false;

        Action playerAction = getPlayerAction();
        Action enemyAction = getEnemyAction();

        System.out.println("Player action: " + playerAction);
        System.out.println("Enemy action: " + enemyAction);

        // Determine turn order by comparing priorities.
        int playerPrio = getPriority(playerAction);
        int enemyPrio = getPriority(enemyAction);
        boolean playerFirst = (playerPrio < enemyPrio) ||
                (playerPrio == enemyPrio && random.nextBoolean());

        if (playerFirst) {
            executeAction(player, enemy, playerAction, true);
            if (enemy.hp <= 0) {
                System.out.println("Enemy defeated immediately!");
                // Instead of immediately changing state, we let the UI show a notification.
                gp.ui.showBattleNotification("Enemy is defeated!");
                // (The key handler will dismiss the notification and then change to playState.)
                return;
            }
            if (playerAction == Action.ESCAPE && pendingEscapeSuccess) {
                return;
            }
            // Save enemy's action as pending.
            pendingAttacker = enemy;
            pendingDefender = player;
            pendingAction = enemyAction;
            pendingIsPlayer = false;
            awaitingPendingAction = true;
        } else {
            executeAction(enemy, player, enemyAction, false);
            if (player.hp <= 0) {
                System.out.println("Player defeated!");
                gp.gameState = gp.gameOverState;
                return;
            }
            if (enemyAction == Action.ESCAPE && pendingEscapeSuccess) {
                return;
            }
            // Save player's action as pending.
            pendingAttacker = player;
            pendingDefender = enemy;
            pendingAction = playerAction;
            pendingIsPlayer = true;
            awaitingPendingAction = true;
        }
    }

    // Returns a lower number for higher-priority actions.
    private int getPriority(Action action) {
        switch (action) {
            case ESCAPE:    return 1;
            case INVENTORY: return 2;
            case DEFENSE:   return 3;
            case ATTACK:    return 4;
            default:        return 5;
        }
    }

    // Execute a single action.
    // The parameter isPlayer indicates if the actor is the player.
    public void executeAction(Object attacker, Object defender, Action action, boolean isPlayer) {
        if (action == Action.ATTACK) {
            int atkVal = getAttackValue(attacker);
            int defVal = isDefending(defender) ? getDefenseValue(defender) : 0;
            int damage = atkVal - defVal;
            if (damage < 0) damage = 0;
            reduceHP(defender, damage);

            if (isBattleOver()) {
                return;
            }

            if (isPlayer) {
                // Trigger enemy hit animation.
                triggerAnimation("take_hit", 30);
                gp.playSE(5);
                System.out.println("Player attacks enemy for " + damage + " damage.");
                gp.ui.showBattleNotification("Player Attacked. Enemy takes " + damage + " damage!");
            } else {
                System.out.println("Enemy attacks player for " + damage + " damage.");
                gp.ui.showBattleNotification("Enemy Attacked. Player takes " + damage + " damage!");
                gp.ui.triggerRedVignette(20);
            }
        } else if (action == Action.DEFENSE) {
            markAsDefending(attacker);
            System.out.println((isPlayer ? "Player" : "Enemy") + " defends.");
            gp.ui.showBattleNotification((isPlayer ? "Player" : "Enemy") + " is defending!");
        } else if (action == Action.ESCAPE) {
            boolean escaped = random.nextBoolean(); // 50% chance
            if (escaped) {
                System.out.println((isPlayer ? "Player" : "Enemy") + " escapes successfully!");
                if (isPlayer) {
                    gp.ui.showBattleEscapeNotification("Player escaped!");
                    pendingEscapeSuccess = true;
                } else {
                    gp.ui.showBattleNotification("Enemy escaped!");
                }
            } else {
                System.out.println((isPlayer ? "Player" : "Enemy") + " fails to escape.");
                gp.ui.showBattleNotification((isPlayer ? "Player" : "Enemy") + " failed to escape!");
            }
        } else if (action == Action.INVENTORY) {
            if (isPlayer) {
                // Open battle inventory mode.
                gp.ui.battleInventoryActive = true;
                gp.ui.battleInvSelection = 0;
                gp.ui.battleInvOffset = 0;
                System.out.println("Player opens battle inventory.");
                // (The UI will now show the battle inventory window.)
            } else {
                gp.ui.showBattleNotification("Enemy inventory use not allowed!");
            }
        }
    }

    private int getAttackValue(Object actor) {
        if (actor instanceof Player) {
            Player p = (Player) actor;
            int baseAttack = p.attackVal.nextInt(11) + 10;
            int equipmentBonus = 0;
            for (item.Item i : p.inventory.items) {
                if (i.type == item.ItemType.EQUIPMENT) {
                    equipmentBonus += i.attackBoost;
                }
            }
            return baseAttack + equipmentBonus;
        } else if (actor instanceof Entity) {
            return ((Entity) actor).attackVal.nextInt(11) + 10;
        }
        return 0;
    }

    private int getDefenseValue(Object actor) {
        if (actor instanceof Player) {
            Player p = (Player) actor;
            int baseDefense = p.defenseVal.nextInt(11) + 5;
            int equipmentBonus = 0;
            for (item.Item i : p.inventory.items) {
                if (i.type == item.ItemType.EQUIPMENT) {
                    equipmentBonus += i.defenseBoost;
                }
            }
            return baseDefense + equipmentBonus;
        } else if (actor instanceof Entity) {
            return ((Entity) actor).defenseVal.nextInt(11) + 5;
        }
        return 0;
    }

    private boolean isDefending(Object actor) {
        if (actor instanceof Player) {
            return ((Player) actor).isDefending;
        } else if (actor instanceof Entity) {
            return ((Entity) actor).isDefending;
        }
        return false;
    }

    private void markAsDefending(Object actor) {
        if (actor instanceof Player) {
            ((Player) actor).isDefending = true;
        } else if (actor instanceof Entity) {
            ((Entity) actor).isDefending = true;
        }
    }

    private void reduceHP(Object actor, int damage) {
        if (actor instanceof Player) {
            ((Player) actor).hp -= damage;
        } else if (actor instanceof Entity) {
            ((Entity) actor).hp -= damage;
        }
    }

    // Instead of immediately switching state when the enemy is defeated,
    // show the battle notification and let the key handler dismiss it.
    private boolean isBattleOver() {
        if (player.hp <= 0) {
            System.out.println("Player defeated!");
            gp.gameState = gp.gameOverState;
            return true;
        }
        if (enemy.hp <= 0) {
            System.out.println("Enemy defeated!");
            gp.ui.showBattleNotification("Enemy is defeated!");
            // Do not change gameState here—wait for the user to press Enter.
            return true;
        }
        return false;
    }

    // Helper method to let the KeyHandler check after notification is dismissed.
    public boolean isEnemyDefeated() {
        return enemy != null && enemy.hp <= 0;
    }

    public void resumeRound() {
        if (pendingEscapeSuccess) {
            gp.gameState = gp.playState;
            pendingEscapeSuccess = false;
            return;
        }
        if (!awaitingPendingAction) return;
        executeAction(pendingAttacker, pendingDefender, pendingAction, pendingIsPlayer);
        awaitingPendingAction = false;

        if ((pendingDefender instanceof Player && ((Player) pendingDefender).hp <= 0) ||
                (pendingDefender instanceof Entity && ((Entity) pendingDefender).hp <= 0)) {
            if (pendingDefender instanceof Player) {
                gp.gameState = gp.gameOverState;
            } else {
                // Instead of immediately returning to playState, if the enemy is defeated,
                // let the battle notification remain until dismissed.
                // gp.gameState = gp.playState;
            }
            return;
        }
        gp.ui.battleCommandNum = 0;
    }

    // --- Animation Methods ---

    public void updateRoundAnimation() {
        if (roundAnimationTimer > 0) {
            roundAnimationTimer--;
            if (roundAnimationTimer == 0) {
                gp.ui.setEnemyState("idle");
            }
        }
    }

    public void drawRoundAnimation(Graphics2D g2) {
        // Additional round visual effects can be added here.
    }

    public void triggerAnimation(String anim, int duration) {
        roundAnimationTimer = duration;
        gp.ui.setEnemyState(anim);
    }
}
