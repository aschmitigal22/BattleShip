package me.melonboy10.battleship.game;

import javafx.geometry.BoundingBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import static me.melonboy10.battleship.BattleShipApplication.scale;

public class ShipGrid extends GridPane {

    boolean hovered;
    boolean canMoveShips = true;
    ArrayList<BattleShip> ships = new ArrayList<>();
    BoundingBox boundingBox = new BoundingBox(0, 0, 0, 10, 10, 0);

    public ShipGrid() {
        for (int i = 0; i < 10; i++) {
            getRowConstraints().add(new RowConstraints(10 * scale));
            getColumnConstraints().add(new ColumnConstraints(10 * scale));
        }

        addShips();

        addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEvent -> hovered = true);
        addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> hovered = false);
    }

    private void addShips() {
        ships.add(new BattleShip(this, 0, 0, 2, false));
        ships.add(new BattleShip(this, 3, 0, 3, true));
        ships.add(new BattleShip(this, 6, 0, 4, false));
        ships.add(new BattleShip(this, 0, 2, 4, true));
        ships.add(new BattleShip(this, 5, 2, 3, false));
        ships.add(new BattleShip(this, 9, 2, 2, true));
        ships.add(new BattleShip(this, 5, 5, 2, true));
        ships.add(new BattleShip(this, 9, 5, 3, true));
        ships.add(new BattleShip(this, 0, 7, 2, false));
        ships.add(new BattleShip(this, 4, 9, 6, false));
    }

    public boolean isHit(int x, int y) {
        BoundingBox box = new BoundingBox(x + 0.01, y + 0.01, 0.08, 0.08);
        for (BattleShip ship : ships) {
            if (ship.boundingBox.contains(box)) {
                Circle circle = new Circle(4 * scale, Color.RED);
                circle.setTranslateX(4);
                this.add(circle, x, y);
                return true;
            }
        }
        Circle circle = new Circle(4 * scale, Color.GRAY);
        circle.setTranslateX(4);
        this.add(circle, x, y);
        return false;
    }
}
