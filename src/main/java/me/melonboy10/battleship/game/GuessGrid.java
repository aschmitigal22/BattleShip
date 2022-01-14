package me.melonboy10.battleship.game;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.HashMap;

import static me.melonboy10.battleship.BattleShipApplication.scale;

public class GuessGrid extends GridPane {

    enum Guess {HIT, MISS}
    HashMap<Pair<Integer, Integer>, Guess> guessLocations = new HashMap<>();
    BattleShipGame game;

    public GuessGrid() {
        for (int i = 0; i < 10; i++) {
            getRowConstraints().add(new RowConstraints(10 * scale));
            getColumnConstraints().add(new ColumnConstraints(10 * scale));
        }
    }

    public void init(BattleShipGame game) {
        this.game = game;

        addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (game.state.equals(BattleShipGame.GameState.GUESSING) && game.yourTurn) {
                int cellWidth = (int) (10 * scale);
                int gridX = (int) (10 * scale);
                int gridY = (int) (35 * scale);
                double mouseX = mouseEvent.getSceneX();
                double mouseY = mouseEvent.getSceneY();
                double relativeMouseX = mouseX - gridX;
                double relativeMouseY = mouseY - gridY;

                int cellX = (int) ((int) (relativeMouseX / cellWidth) /*- (int) (mouseXOffset / cellWidth)*/);
                int cellY = (int) ((int) (relativeMouseY / cellWidth) /*- (int) (mouseYOffset / cellWidth)*/);

                if (!guessLocations.containsKey(new Pair<>(cellX, cellY))) {
                    game.networkManager.sendInfo(NetworkManager.PacketType.GUESS, cellX + "," + cellY);
                    game.gameInformationGrid.shotsFired++;
                }
            }
        });
    }

    public void guess(int cellX, int cellY, boolean hit) {
        Circle circle = new Circle(4 * scale, hit ? Color.RED : Color.GRAY);
        circle.setTranslateX(4);
        this.add(circle, cellX, cellY);
        guessLocations.put(new Pair<>(cellX, cellY), hit ? Guess.HIT : Guess.MISS);
        if (hit) game.gameInformationGrid.shotsHit++;
        game.gameInformationGrid.updateText();
        game.checkForWin();
    }
}
