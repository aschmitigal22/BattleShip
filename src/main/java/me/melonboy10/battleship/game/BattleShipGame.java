package me.melonboy10.battleship.game;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import me.melonboy10.battleship.BattleShipApplication;

public class BattleShipGame {

    public enum GameState {NOT_STARTED, PLACING_SHIPS, GUESSING, GAME_OVER;}

    public NetworkManager networkManager;
    BattleShipApplication gameWindow;
    public GridPane borderGrid;
    public ShipGrid shipGrid;
    public GuessGrid guessGrid;
    public GameInformationGrid gameInformationGrid;
    public Text infoText;

    volatile public GameState state;
    volatile boolean yourTurn = false;
    volatile public boolean confirmedPieces;
    volatile public boolean opponentConfirmedPieces;
    volatile boolean won = false;

    public BattleShipGame(BattleShipApplication gameWindow, GridPane borderGrid, ShipGrid shipGrid, GuessGrid guessGrid, GameInformationGrid gameInformationGrid, Text infoText) {
        this.gameWindow = gameWindow;
        this.borderGrid = borderGrid;
        this.shipGrid = shipGrid;
        this.guessGrid = guessGrid;
        this.gameInformationGrid = gameInformationGrid;
        this.infoText = infoText;
        this.networkManager = new NetworkManager(this);
    }

    public void stop() {
        networkManager.running = false;
        networkManager.thread.interrupt();
    }

    public void checkForWin() {
        if (gameInformationGrid.shotsHit >= 31) {
            won = true;
            networkManager.sendInfo(NetworkManager.PacketType.END, "");
        }
    }

    public void checkPieceConfirm() {
        if (confirmedPieces) {
            gameInformationGrid.showButton(false);
            shipGrid.canMoveShips = false;
            if (opponentConfirmedPieces) {
                yourTurn = !networkManager.guest;
                setGameState(GameState.GUESSING);
            }
        }
    }

    public void setGameState(GameState state) {
        this.state = state;
        switch (state) {
            case NOT_STARTED -> {
                infoText.setText("Waiting for Opponent");
                borderGrid.setVisible(false);
                shipGrid.setVisible(false);
                guessGrid.setVisible(false);
                gameInformationGrid.setVisible(false);
                gameInformationGrid.showButton(false);
                shipGrid.canMoveShips = false;
            }
            case PLACING_SHIPS -> {
                infoText.setText("Move Your Ships");
                borderGrid.setVisible(true);
                shipGrid.setVisible(true);
                guessGrid.setVisible(false);
                gameInformationGrid.setVisible(true);
                gameInformationGrid.showButton(true);
                shipGrid.canMoveShips = true;
            }
            case GUESSING -> {
                shipGrid.canMoveShips = false;
                gameInformationGrid.setVisible(true);
                gameInformationGrid.showButton(false);
                borderGrid.setVisible(true);
                if (yourTurn) {
                    infoText.setText("Guess a Location");
                    shipGrid.setVisible(false);
                    guessGrid.setVisible(true);
                } else {
                    infoText.setText("Opponent is Guessing");
                    shipGrid.setVisible(true);
                    guessGrid.setVisible(false);
                }
            }
            case GAME_OVER -> {
                if (won) infoText.setText("YOU WIN!");
                else infoText.setText("Better Luck Next Time");
                borderGrid.setVisible(true);
                shipGrid.setVisible(true);
                guessGrid.setVisible(false);
                gameInformationGrid.setVisible(true);
                gameInformationGrid.showButton(false);
                shipGrid.canMoveShips = false;
            }
        }
        gameInformationGrid.updateText();
    }
}
