package main;

import entity.Entity;
import entity.Enemy;
import entity.Player;

import java.awt.Graphics2D;
import java.util.Random;

public class Battle {

    public enum Action {
        ESCAPE, INVENTORY, DEFENSE, ATTACK;
    }

    private GamePanel gp;
    private Player player;
    private Enemy enemy;
    private Random random = new Random();

    // For sequencing two‐step rounds
    public boolean awaitingPendingAction = false;
    private Object pendingAttacker;
    private Object pendingDefender;
    private Action pendingAction;
    private boolean pendingIsPlayer;
    public boolean pendingEscapeSuccess = false;

    public Battle(GamePanel gp, Player player, Enemy enemy) {
        this.gp = gp;
        this.player = player;
        if (enemy == null) {
            throw new IllegalArgumentException("Cannot start a battle with a null enemy");
        }
        this.enemy = enemy;
        this.enemy.resetHP();  // start fresh
    }

    public boolean isEnemyDefeated() {
        return enemy.hp <= 0;
    }

    public Action getPlayerAction() {
        switch (gp.ui.battleCommandNum) {
            case 0: return Action.ATTACK;
            case 1: return Action.DEFENSE;
            case 2: return Action.ESCAPE;
            case 3: return Action.INVENTORY;
            default: return Action.ATTACK;
        }
    }

    public Action getEnemyAction() {
        // simple 50/50 between ATTACK and DEFENSE
        return random.nextBoolean() ? Action.ATTACK : Action.DEFENSE;
    }

    private int getPriority(Action action) {
        switch (action) {
            case ESCAPE:    return 1;
            case INVENTORY: return 2;
            case DEFENSE:   return 3;
            case ATTACK:    return 4;
            default:        return 5;
        }
    }

    public void executeRound() {
        // reset defend flags
        player.isDefending = false;
        enemy.isDefending  = false;
        pendingEscapeSuccess = false;
        awaitingPendingAction = false;

        Action pAct = getPlayerAction();
        Action eAct = getEnemyAction();

        boolean playerFirst =
                getPriority(pAct) < getPriority(eAct)
                        || (getPriority(pAct) == getPriority(eAct) && random.nextBoolean());

        if (playerFirst) {
            executeAction(player, enemy, pAct, true);
            if (checkBattleEnd()) return;
            if (pAct == Action.ESCAPE && pendingEscapeSuccess) return;

            // queue the enemy
            pendingAttacker       = enemy;
            pendingDefender       = player;
            pendingAction         = eAct;
            pendingIsPlayer       = false;
            awaitingPendingAction = true;

        } else {
            executeAction(enemy, player, eAct, false);
            if (checkBattleEnd()) return;
            if (eAct == Action.ESCAPE && pendingEscapeSuccess) return;

            // queue the player
            pendingAttacker       = player;
            pendingDefender       = enemy;
            pendingAction         = pAct;
            pendingIsPlayer       = true;
            awaitingPendingAction = true;
        }
    }

    private boolean checkBattleEnd() {
        if (player.hp <= 0) {
            gp.ui.showBattleNotification("Player defeated!");
            gp.gameState = gp.gameOverState;
            return true;
        }
        if (enemy.hp <= 0) {
            gp.ui.showBattleNotification("Enemy defeated!");
            return true;
        }
        return false;
    }

    public void resumeRound() {
        if (pendingEscapeSuccess) {
            gp.gameState = gp.playState;
            return;
        }
        if (!awaitingPendingAction) return;

        executeAction(pendingAttacker, pendingDefender, pendingAction, pendingIsPlayer);
        awaitingPendingAction = false;

        if (pendingDefender instanceof Player && ((Player)pendingDefender).hp <= 0) {
            gp.ui.showBattleNotification("Player defeated!");
            gp.gameState = gp.gameOverState;
        } else if (pendingDefender instanceof Enemy && ((Enemy)pendingDefender).hp <= 0) {
            gp.ui.showBattleNotification("Enemy defeated!");
        }
    }

    private void executeAction(Object attacker, Object defender, Action action, boolean isPlayer) {
        switch (action) {
            case ATTACK:
                int atkVal = getAttackValue(attacker);
                int defVal = isDefending(defender) ? getDefenseValue(defender) : 0;
                int damage = Math.max(0, atkVal - defVal);
                reduceHP(defender, damage);

                // trigger the appropriate battle animation on the actor being hit or attacking
                if (defender instanceof Enemy) {
                    ((Enemy)defender).triggerBattleAnimation("take_hit", 30);
                } else if (attacker instanceof Enemy) {
                    ((Enemy)attacker).triggerBattleAnimation("cleave", 30);
                }

                gp.ui.showBattleNotification(
                        (isPlayer ? "Player attacked: " : "Enemy attacked: ") + damage + " damage!"
                );
                break;

            case DEFENSE:
                markAsDefending(attacker);
                gp.ui.showBattleNotification(
                        (isPlayer ? "Player is defending" : "Enemy is defending")
                );
                break;

            case ESCAPE:
                boolean escaped = random.nextBoolean();
                if (escaped && isPlayer) {
                    pendingEscapeSuccess = true;
                    gp.ui.showBattleEscapeNotification("Player escaped!");
                } else if (!escaped && isPlayer) {
                    gp.ui.showBattleNotification("Escape failed!");
                } else {
                    gp.ui.showBattleNotification("Enemy escaped!");
                }
                break;

            case INVENTORY:
                if (attacker instanceof Player) {
                    gp.ui.battleInventoryActive = true;
                } else {
                    gp.ui.showBattleNotification("Enemy cannot open inventory");
                }
                break;
        }
    }

    private int getAttackValue(Object actor) {
        if (actor instanceof Player) {
            return ((Player)actor).attackVal.nextInt(11) + 10;
        } else if (actor instanceof Entity) {
            return ((Entity)actor).attackVal.nextInt(11) + 10;
        }
        return 0;
    }

    private int getDefenseValue(Object actor) {
        if (actor instanceof Player) {
            return ((Player)actor).defenseVal.nextInt(11) + 5;
        } else if (actor instanceof Entity) {
            return ((Entity)actor).defenseVal.nextInt(11) + 5;
        }
        return 0;
    }

    private boolean isDefending(Object actor) {
        if (actor instanceof Player)   return ((Player)actor).isDefending;
        if (actor instanceof Enemy)    return ((Enemy)actor).isDefending;
        return false;
    }

    private void markAsDefending(Object actor) {
        if (actor instanceof Player)   ((Player)actor).isDefending = true;
        if (actor instanceof Enemy)    ((Enemy)actor).isDefending  = true;
    }

    private void reduceHP(Object actor, int dmg) {
        if (actor instanceof Player)   ((Player)actor).hp  -= dmg;
        if (actor instanceof Enemy)    ((Enemy)actor).hp   -= dmg;
    }

    /**
     * Called each frame by UI.drawBattleScreen() to advance any pending
     * animation timers on the enemy.
     */
    public void updateRoundAnimation() {
        enemy.updateBattleAnimation();
    }

    /**
     * Called right after updateRoundAnimation(); UI doesn't need
     * anything here since the actual drawing was done via drawBattleSprite().
     */
    public void drawRoundAnimation(Graphics2D g2) {
        // no‐op
    }
}
