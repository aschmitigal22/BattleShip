package me.melonboy10.battleship.game;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GameInformationGrid extends VBox {

    BattleShipGame game;
    String name = "Unnamed Player";
    String opponentName = "Enemy Player";
    int shotsFired = 0;
    int shotsHit = 0;

    TextField nameInput;
    Text vs = new Text("vs.");
    Text opponentNameText = new Text();
    Text shotsFiredText = new Text();
    Text shotsHitText = new Text();

    Button confirmShipsButton;

    public void init(BattleShipGame game) {
        this.game = game;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);

        nameInput = new TextField(name);
        nameInput.clear();
        nameInput.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (!name.equals(nameInput.getText())) {
                name = nameInput.getText();
                game.networkManager.sendInfo(NetworkManager.PacketType.NAME_CHANGE, name);
            }
        });
        getChildren().add(new Group(nameInput));

        vs.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(new Group(vs));

        opponentNameText.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(new Group(opponentNameText));


        shotsFiredText.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(shotsFiredText);

        shotsHitText.setTextAlignment(TextAlignment.CENTER);
        getChildren().add(shotsHitText);


        confirmShipsButton = new Button("Confirm Ship Positions");
        confirmShipsButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            game.networkManager.sendInfo(NetworkManager.PacketType.CONFIRM_PIECES, "");
            game.confirmedPieces = true;
            game.checkPieceConfirm();
        });
        getChildren().add(confirmShipsButton);

        updateText();
    }

    public void updateText() {
        opponentNameText.setText(opponentName);
        shotsFiredText.setText("Shots Fired: " + shotsFired);
        shotsHitText.setText("Shots Hit: " + shotsHit);
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
        updateText();
    }

    public void showButton(boolean show) {
        confirmShipsButton.setVisible(show);
    }
}
