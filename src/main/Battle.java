package main;

import entity.Player;
import entity.Entity;

import java.awt.*;
import java.util.Random;

public class Battle {

    GamePanel gp;
    Player player;
    Entity enemy;
    Random random = new Random();

    // Animation trigger fields
    private int roundAnimationTimer = 0;

    // The possible actions
    public enum Action {
        ESCAPE, INVENTORY, DEFENSE, ATTACK;
    }

    public Battle(GamePanel gp, Player player, Entity enemy) {
        this.gp = gp;
        this.player = player;
        this.enemy = enemy;
    }

    // Map UI battleCommandNum (0: ATTACK, 1: DEFENSE, 2: ESCAPE, 3: INVENTORY)
    // to an Action.
    public Action getPlayerAction() {
        int cmd = gp.ui.battleCommandNum;
        switch(cmd) {
            case 0: return Action.ATTACK;
            case 1: return Action.DEFENSE;
            case 2: return Action.ESCAPE;
            case 3: return Action.INVENTORY;
            default: return Action.ATTACK;
        }
    }

    // For the enemy, choose a random action.
    public Action getEnemyAction() {
        int r = random.nextInt(2);  // returns 0-3
        switch(r) {
            case 0: return Action.ATTACK;
            case 1: return Action.DEFENSE;
//            case 2: return Action.INVENTORY2
//            case 3: return Action.ESCAPE;
            default: return Action.ATTACK;
        }
    }

    // Execute a full round of battle.
    public void executeRound() {
        // Reset defending flags at the start of the round.
        player.isDefending = false;
        enemy.isDefending = false;

        Action playerAction = getPlayerAction();
        Action enemyAction = getEnemyAction();

        System.out.println("Player action: " + playerAction);
        System.out.println("Enemy action: " + enemyAction);

        // Decide order based on priority.
        int playerPrio = getPriority(playerAction);
        int enemyPrio  = getPriority(enemyAction);
        boolean playerFirst;
        if(playerPrio < enemyPrio) {
            playerFirst = true;
        } else if(playerPrio > enemyPrio) {
            playerFirst = false;
        } else {
            playerFirst = random.nextBoolean();
        }

        // Execute actions in order with immediate HP check.
        if (playerFirst) {
            executeAction(player, enemy, playerAction, true);
            if (enemy.hp <= 0) {
                System.out.println("Enemy defeated immediately!");
                gp.gameState = gp.playState;  // Or a victory state if you have one.
                return;  // End the round immediately.
            }
            executeAction(enemy, player, enemyAction, false);
            if (player.hp <= 0) {
                System.out.println("Player defeated!");
                gp.gameState = gp.playState;  // Or a defeat/game-over state.
                return;
            }
        } else {
            executeAction(enemy, player, enemyAction, false);
            if (player.hp <= 0) {
                System.out.println("Player defeated!");
                gp.gameState = gp.playState;
                return;
            }
            executeAction(player, enemy, playerAction, true);
            if (enemy.hp <= 0) {
                System.out.println("Enemy defeated immediately!");
                gp.gameState = gp.playState;
                return;
            }
        }

        // Reset battle command for the next round.
        gp.ui.battleCommandNum = 0;
    }


    // A helper to get action priority (lower value means the action goes first)
    private int getPriority(Action action) {
        switch(action) {
            case ESCAPE:    return 1;
            case INVENTORY: return 2;
            case DEFENSE:   return 3;
            case ATTACK:    return 4;
            default:        return 5;
        }
    }

    // Execute a single action.
    // isPlayer indicates if the attacker is the player.
    private void executeAction(Object attacker, Object defender, Action action, boolean isPlayer) {
        if (action == Action.ATTACK) {
            int atkVal = getAttackValue(attacker);
            int defVal = (isDefending(defender)) ? getDefenseValue(defender) : 0;
            int damage = atkVal - defVal;
            if (damage < 0) damage = 0;
            reduceHP(defender, damage);

            if (isPlayer) {
                // Player attacks enemy: trigger enemy "take_hit" animation.
                triggerAnimation("take_hit", 30);
                System.out.println("Player attacks enemy for " + damage + " damage.");
            } else {
                // Enemy attacks player: trigger enemy "cleave" animation.
//                triggerAnimation("cleave", 30);
                System.out.println("Enemy attacks player for " + damage + " damage.");
                // Trigger red vignette effect on the UI for, say, 20 frames.
                gp.ui.triggerRedVignette(20);
            }
        }
        else if (action == Action.DEFENSE) {
            markAsDefending(attacker);
            System.out.println((isPlayer ? "Player" : "Enemy") + " defends.");
        }
        else if (action == Action.ESCAPE) {
            boolean escaped = random.nextBoolean(); // 50% chance
            if (escaped) {
                System.out.println((isPlayer ? "Player" : "Enemy") + " escapes successfully!");
                if (isPlayer) {
                    gp.gameState = gp.playState;
                }
            } else {
                System.out.println((isPlayer ? "Player" : "Enemy") + " fails to escape.");
            }
        }
        else if (action == Action.INVENTORY) {
            System.out.println((isPlayer ? "Player" : "Enemy") + " uses inventory (not implemented).");
        }
    }

    private int getAttackValue(Object actor) {
        if(actor instanceof Player) {
            return ((Player)actor).attackVal.nextInt(11) + 10;
        } else if(actor instanceof Entity) {
            return ((Entity)actor).attackVal.nextInt(11) + 10;
        }
        return 0;
    }

    private int getDefenseValue(Object actor) {
        if(actor instanceof Player) {
            return ((Player)actor).defenseVal.nextInt(11) + 5;
        } else if(actor instanceof Entity) {
            return ((Entity)actor).defenseVal.nextInt(11) + 5;
        }
        return 0;
    }

    private boolean isDefending(Object actor) {
        if(actor instanceof Player) {
            return ((Player)actor).isDefending;
        } else if(actor instanceof Entity) {
            return ((Entity)actor).isDefending;
        }
        return false;
    }

    private void markAsDefending(Object actor) {
        if(actor instanceof Player) {
            ((Player)actor).isDefending = true;
        } else if(actor instanceof Entity) {
            ((Entity)actor).isDefending = true;
        }
    }

    private void reduceHP(Object actor, int damage) {
        if(actor instanceof Player) {
            ((Player)actor).hp -= damage;
        } else if(actor instanceof Entity) {
            ((Entity)actor).hp -= damage;
        }
    }

    private boolean isBattleOver() {
        if(player.hp <= 0) {
            System.out.println("Player defeated!");
            gp.gameState = gp.playState; // Switch to play or game over state.
            return true;
        }
        if(enemy.hp <= 0) {
            System.out.println("Enemy defeated!");
            gp.gameState = gp.playState;
            return true;
        }
        return false;
    }

    // --- Animation Methods for Battle Rounds ---

    // Call this each frame from UI.drawBattleScreen().
    public void updateRoundAnimation() {
        if (roundAnimationTimer > 0) {
            roundAnimationTimer--;
            if (roundAnimationTimer == 0) {
                // When animation time is up, revert enemy state to idle.
                gp.ui.setEnemyState("idle");
            }
        }
    }

    // This method is here if you want to draw extra round effects.
    // In this example, the UI already draws the enemy animation based on its state.
    public void drawRoundAnimation(Graphics2D g2) {
        // You can add additional visual effects here if needed.
    }

    // Call this to trigger an enemy animation for a set duration.
    public void triggerAnimation(String anim, int duration) {
        roundAnimationTimer = duration;
        gp.ui.setEnemyState(anim);
    }


}
