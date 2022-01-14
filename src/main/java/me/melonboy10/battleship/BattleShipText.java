package me.melonboy10.battleship;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class BattleShipText extends Text {

    public BattleShipText(String s, double size, boolean v) {
        super(s);
        setScaleX(size);
        setScaleY(size);
        if (v) VBox.setVgrow(this, Priority.ALWAYS);
        else HBox.setHgrow(this, Priority.ALWAYS);

    }
}
