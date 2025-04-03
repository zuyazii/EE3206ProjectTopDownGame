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
                }
            }
            if (code == KeyEvent.VK_S) {
                if (gamePanel.ui.commandNum < 2) {
                    gamePanel.ui.commandNum++;
                }
            }
            if (code == KeyEvent.VK_ENTER) {
                if (gamePanel.ui.commandNum == 0) {
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
                    if (code == KeyEvent.VK_D) { gamePanel.ui.battleCommandNum = 1; }
                    if (code == KeyEvent.VK_S) { gamePanel.ui.battleCommandNum = 3; }
                    break;
                case 1:
                    if (code == KeyEvent.VK_A) { gamePanel.ui.battleCommandNum = 0; }
                    if (code == KeyEvent.VK_S) { gamePanel.ui.battleCommandNum = 2; }
                    break;
                case 2:
                    if (code == KeyEvent.VK_W) { gamePanel.ui.battleCommandNum = 1; }
                    if (code == KeyEvent.VK_A) { gamePanel.ui.battleCommandNum = 3; }
                    break;
                case 3:
                    if (code == KeyEvent.VK_W) { gamePanel.ui.battleCommandNum = 0; }
                    if (code == KeyEvent.VK_D) { gamePanel.ui.battleCommandNum = 2; }
                    break;
            }
            System.out.println(gamePanel.ui.battleCommandNum);

            // When ENTER is pressed in battle mode, set a flag or directly call executeRound.
            if (code == KeyEvent.VK_ENTER) {
                if (gamePanel.battle != null) {
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
            // When dialogue options are active, process option input.
            if (gamePanel.ui.showDialogueOptions) {
                // Use A/Left and D/Right to change selection.
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    gamePanel.ui.optionNum = 0;  // Select YES.
                }
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    gamePanel.ui.optionNum = 1;  // Select NO.
                }
                // Confirm the selection.
                if (code == KeyEvent.VK_ENTER) {
                    if (gamePanel.ui.optionNum == 1) {
                        // NO selected: change state to PLAY.
                        gamePanel.gameState = gamePanel.playState;
                    } else {
                        // YES selected: change state to BATTLE.
                        gamePanel.battle = new Battle(gamePanel, gamePanel.player, gamePanel.npc[gamePanel.currentMap][0]);
                        gamePanel.gameState = gamePanel.battleState;
                    }
                    gamePanel.ui.showDialogueOptions = false;
                }
            }
            // Otherwise, if no dialogue option is active, use ENTER to progress dialogue.
            else {
                if (code == KeyEvent.VK_ENTER) {
                    // Typically you might call a method to progress dialogue here.
                    // For example:
                    // gamePanel.npc[someIndex].speak();
                    // For now, we'll simply set a flag.
                    gamePanel.gameState = gamePanel.playState;
                    enterPressed = true;
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
