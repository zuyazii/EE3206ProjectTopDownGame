package main;

import entity.Enemy;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gamePanel;
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;

    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if (gamePanel.gameState == gamePanel.titleState) {
            if (code == KeyEvent.VK_W) {
                if (gamePanel.ui.commandNum > 0) {
                    gamePanel.ui.commandNum--;
                    gamePanel.playSE(9);
                }
            }
            if (code == KeyEvent.VK_S) {
                if (gamePanel.ui.commandNum < 2) {
                    gamePanel.ui.commandNum++;
                    gamePanel.playSE(9);
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gamePanel.ui.commandNum == 0) {
                    gamePanel.playSE(9);
                    gamePanel.gameState = gamePanel.playState;
                    gamePanel.playMusic(0);
                }
                if (gamePanel.ui.commandNum == 1) {

                }
                if (gamePanel.ui.commandNum == 2) {
                    System.exit(0);
                }
            }
        }

        // BATTLE STATE
        else if (gamePanel.gameState == gamePanel.battleState) {
            switch (gamePanel.ui.battleCommandNum) {
                case 0:
                    if (code == KeyEvent.VK_D) { gamePanel.ui.battleCommandNum = 1; gamePanel.playSE(9); }
                    if (code == KeyEvent.VK_S) { gamePanel.ui.battleCommandNum = 3; gamePanel.playSE(9); }
                    break;
                case 1:
                    if (code == KeyEvent.VK_A) { gamePanel.ui.battleCommandNum = 0; gamePanel.playSE(9); }
                    if (code == KeyEvent.VK_S) { gamePanel.ui.battleCommandNum = 2; gamePanel.playSE(9); }
                    break;
                case 2:
                    if (code == KeyEvent.VK_W) { gamePanel.ui.battleCommandNum = 1; gamePanel.playSE(9); }
                    if (code == KeyEvent.VK_A) { gamePanel.ui.battleCommandNum = 3; gamePanel.playSE(9); }
                    break;
                case 3:
                    if (code == KeyEvent.VK_W) { gamePanel.ui.battleCommandNum = 0; gamePanel.playSE(9); }
                    if (code == KeyEvent.VK_D) { gamePanel.ui.battleCommandNum = 2; gamePanel.playSE(9); }
                    break;
            }
            System.out.println(gamePanel.ui.battleCommandNum);

            // When ENTER is pressed in battle mode, set a flag or directly call executeRound.
            // In KeyHandler.java, within battle state block:
            if (code == KeyEvent.VK_ENTER) {
                if (gamePanel.ui.isBattleNotificationActive()) {
                    System.out.println(">> Dismissing battle notification!");
                    gamePanel.ui.dismissBattleNotification();

                    if (gamePanel.battle != null &&
                            (gamePanel.battle.awaitingPendingAction || gamePanel.battle.pendingEscapeSuccess)) {
                        System.out.println(">> Resuming round...");
                        gamePanel.battle.resumeRound();
                    }
                    else {
                        System.out.println(">> No pending action or no battle object!");
                    }
                }
                else if (gamePanel.battle != null) {
                    System.out.println(">> Enter pressed => executeRound()");
                    gamePanel.battle.executeRound();
                }
                else {
                    System.out.println(">> (battle is null, so no action)");
                }
            }

        }

        // PLAY STATE
        else if (gamePanel.gameState == gamePanel.playState) {
            if (code == KeyEvent.VK_W) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_A) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_S) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_D) {
                rightPressed = true;
            }
            if (code == KeyEvent.VK_P) {
                gamePanel.gameState = gamePanel.pauseState;
                System.out.println("Game Paused");
            }
            if (code == KeyEvent.VK_ENTER) {
                enterPressed = true;
            }
        }

        // PAUSE STATE
        else if (gamePanel.gameState == gamePanel.pauseState) {
            if (code == KeyEvent.VK_P) {
                gamePanel.gameState = gamePanel.playState;
                System.out.println("Game Resumed");
            }
        }

        // DIALOGUE STATE
        else if (gamePanel.gameState == gamePanel.dialogueState) {

            // If the door’s yes/no is up, that logic remains:
            if (gamePanel.ui.showDialogueOptions && gamePanel.currentDoorEvent != null) {
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    gamePanel.ui.optionNum = 0;  // YES
                    gamePanel.playSE(9);
                }
                else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    gamePanel.ui.optionNum = 1;  // NO
                    gamePanel.playSE(9);
                }
                else if (code == KeyEvent.VK_ENTER) {
                    if (gamePanel.ui.optionNum == 0) {
                        // YES selected for the door
                        gamePanel.currentDoorEvent.triggerDoorTransition(gamePanel);
                    }
                    else {
                        // NO selected for the door
                        System.out.println("Player declined to enter the door.");
                    }
                    // Clear out the door event
                    gamePanel.currentDoorEvent = null;
                    gamePanel.ui.showDialogueOptions = false;
                    gamePanel.ui.currentDialogue = "";
                    gamePanel.gameState = gamePanel.playState;
                }
            }

            // Otherwise, if it's not a door but an NPC/enemy’s prompt:
            else if (gamePanel.ui.showDialogueOptions) {
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    gamePanel.ui.optionNum = 0;  // YES
                    gamePanel.playSE(9);
                }
                else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    gamePanel.ui.optionNum = 1;  // NO
                    gamePanel.playSE(9);
                }
                else if (code == KeyEvent.VK_ENTER) {
                    // Suppose optionNum == 0 means "YES" => Start battle or do something:
                    if (gamePanel.ui.optionNum == 0) {
                        // Example: go to battleState
                        // or call a method to set up the battle

                        gamePanel.battle = new Battle(gamePanel, gamePanel.player, (Enemy) gamePanel.player.talkingTo);
                        gamePanel.gameState = gamePanel.battleState;
                        // The next lines are up to your logic:
                        // e.g., gamePanel.battle = new Battle(gamePanel.player, (Enemy)someEnemyObject)
                    }
                    // If NO => go back to normal play
                    else {
                        gamePanel.gameState = gamePanel.playState;
                    }
                    // Cleanup
                    gamePanel.ui.showDialogueOptions = false;
                    gamePanel.ui.currentDialogue = "";
                }
            }

            // If no options are showing, the user is reading lines of dialogue:
            else {
                if (code == KeyEvent.VK_ENTER) {
                    // Let the NPC or Enemy speak again => this triggers the next line
                    // or the final optionDialog if we just finished the last line
                    // You might need to store which NPC/Enemy is currently talking.
                    if (gamePanel.player.talkingTo != null) {
                        gamePanel.player.talkingTo.speak();
                    }
                    // If there's no next line and no option, speak() puts us in playState automatically.
                }
            }
        }

        // GAME OVER STATE
        else if (gamePanel.gameState == gamePanel.gameOverState) {
            if (code == KeyEvent.VK_W) {
                if (gamePanel.ui.commandNum > 0) {
                    gamePanel.ui.commandNum--;
                    gamePanel.playSE(9);
                }
            }
            if (code == KeyEvent.VK_S) {
                if (gamePanel.ui.commandNum < 2) {
                    gamePanel.ui.commandNum++;
                    gamePanel.playSE(9);
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gamePanel.ui.commandNum == 0) {
                    gamePanel.playSE(9);
                    gamePanel.gameState = gamePanel.titleState;
                    gamePanel.playMusic(0);
                }
                if (gamePanel.ui.commandNum == 1) {
                    gamePanel.playSE(9);
                    gamePanel.gameState = gamePanel.playState;
                    gamePanel.playMusic(0);
                }
                if (gamePanel.ui.commandNum == 2) {
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}
