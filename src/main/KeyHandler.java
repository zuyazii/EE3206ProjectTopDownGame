package main;

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
                    gamePanel.ui.dismissBattleNotification();
                    if (gamePanel.battle != null &&
                            (gamePanel.battle.awaitingPendingAction || gamePanel.battle.pendingEscapeSuccess)) {
                        gamePanel.battle.resumeRound();
                    }
                } else if (gamePanel.battle != null) {
                    gamePanel.battle.executeRound();
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
            if (gamePanel.ui.showDialogueOptions) {
                // Use A/Left and D/Right to change selection.
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    gamePanel.ui.optionNum = 0;  // YES
                    gamePanel.playSE(9);
                }
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    gamePanel.ui.optionNum = 1;  // NO
                    gamePanel.playSE(9);
                }
                // Confirm selection.
                if (code == KeyEvent.VK_ENTER) {
                    if (gamePanel.currentDoorEvent != null) {
                        if (gamePanel.ui.optionNum == 0) {  // YES selected.
                            gamePanel.currentDoorEvent.triggerDoorTransition(gamePanel);
                        } else {  // NO selected.
                            System.out.println("Player declined to enter the door.");
                            // Do not mark the door as responded (or triggered remains true) so that it
                            // won’t trigger repeatedly while still colliding.
                        }
                        // In either case, clear the dialogue.
                        gamePanel.currentDoorEvent = null;
                        gamePanel.ui.showDialogueOptions = false;
                        gamePanel.ui.currentDialogue = "";
                        gamePanel.gameState = gamePanel.playState;
                    } else {
                        // Process other dialogue options if needed.
                        gamePanel.ui.showDialogueOptions = false;
                        gamePanel.ui.currentDialogue = "";
                        gamePanel.gameState = gamePanel.playState;
                    }
                }
            } else {
                if (code == KeyEvent.VK_ENTER) {
                    gamePanel.gameState = gamePanel.playState;
                    gamePanel.ui.showDialogueOptions = false;
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
