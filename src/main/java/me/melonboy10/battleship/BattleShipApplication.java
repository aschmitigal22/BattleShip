package me.melonboy10.battleship;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import me.melonboy10.battleship.game.GuessGrid;
import me.melonboy10.battleship.game.ShipGrid;
import me.melonboy10.battleship.game.BattleShipGame;
import me.melonboy10.battleship.game.GameInformationGrid;

import java.io.IOException;

public class BattleShipApplication extends Application {

    public static final double scale = 3;
    private BattleShipGame game;
    public static boolean customIp = false;

    @Override
    public void start(Stage stage) throws IOException {
        //Window Grid
        GridPane windowGrid = new GridPane();
        Scene scene = new Scene(windowGrid);
        stage.setResizable(false);

        windowGrid.getColumnConstraints().add(new ColumnConstraints(110 * scale));
        windowGrid.getColumnConstraints().add(new ColumnConstraints(50 * scale));
        windowGrid.getRowConstraints().add(new RowConstraints(25 * scale));
        windowGrid.getRowConstraints().add(new RowConstraints(110 * scale));
//        windowGrid.setGridLinesVisible(true);

        /*ArrayList<Group> buts = new ArrayList<>();
        for (NetworkManager.PacketType value : NetworkManager.PacketType.values()) {
            Button button = new Button(value.name());
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> game.networkManager.sendInfo(value, "testinfo"));
            buts.add(new Group(button));
        }
        HBox testPacketBox = new HBox(buts.toArray(new Group[buts.size()]));
        windowGrid.add(testPacketBox, 0, 0);*/

        // Border for BattleGrid
        GridPane battleBorderGrid = new GridPane();
        windowGrid.add(battleBorderGrid, 0, 1, 1, 2);
        battleBorderGrid.getColumnConstraints().add(new ColumnConstraints(10 * scale));
        battleBorderGrid.getRowConstraints().add(new RowConstraints(10 * scale));

        HBox numberTextBox = new HBox(
            new BattleShipText("1",  2, false),
            new BattleShipText("2",  2, false),
            new BattleShipText("3",  2, false),
            new BattleShipText("4",  2, false),
            new BattleShipText("5",  2, false),
            new BattleShipText("6",  2, false),
            new BattleShipText("7",  2, false),
            new BattleShipText("8",  2, false),
            new BattleShipText("9",  2, false),
            new BattleShipText("10", 2, false)
        );
        numberTextBox.setAlignment(Pos.CENTER);
        numberTextBox.setSpacing(23);
        battleBorderGrid.add(numberTextBox, 1, 0);
        VBox letterTextBox = new VBox(
            new BattleShipText("A",  2, true),
            new BattleShipText("B",  2, true),
            new BattleShipText("C",  2, true),
            new BattleShipText("D",  2, true),
            new BattleShipText("E",  2, true),
            new BattleShipText("F",  2, true),
            new BattleShipText("G",  2, true),
            new BattleShipText("H",  2, true),
            new BattleShipText("I",  2, true),
            new BattleShipText("J", 2, true)
        );
        letterTextBox.setAlignment(Pos.CENTER);
        letterTextBox.setSpacing(14);
        battleBorderGrid.add(letterTextBox, 0, 1);
        battleBorderGrid.setGridLinesVisible(true);

        Text infoText = new Text("Waiting for Opponent");
        infoText.setScaleX(2);
        infoText.setScaleY(2);
        infoText.setTranslateX(40 * scale);
        infoText.setTextAlignment(TextAlignment.CENTER);
        windowGrid.add(infoText, 0, 0, 2, 1);

        ShipGrid shipGrid = new ShipGrid();
        battleBorderGrid.add(shipGrid, 1, 1);

        GuessGrid guessGrid = new GuessGrid();
        battleBorderGrid.add(guessGrid, 1, 1);

        GameInformationGrid gameInformationGrid = new GameInformationGrid();
        windowGrid.add(gameInformationGrid, 1, 1);

        game = new BattleShipGame(this, battleBorderGrid, shipGrid, guessGrid, gameInformationGrid, infoText);
        gameInformationGrid.init(game);
        guessGrid.init(game);
        if (game.networkManager.accepted) game.setGameState(BattleShipGame.GameState.PLACING_SHIPS);
        else game.setGameState(BattleShipGame.GameState.NOT_STARTED);
        stage.setTitle("BATTLESHIP!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println(args);
        if (args.length > 0 && args[0].equals("--customip")) customIp = true;
        launch();
    }

    @Override
    public void stop() throws Exception {
        game.stop();
        System.out.println("Stopping Game");
    }


}