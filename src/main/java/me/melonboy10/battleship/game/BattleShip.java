package me.melonboy10.battleship.game;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.paint.Color;

import static me.melonboy10.battleship.BattleShipApplication.scale;

public class BattleShip extends Canvas {

    final int length;
    final boolean up;
    private final ShipGrid grid;
    private boolean dragging;
    private int x, y;
    BoundingBox boundingBox;

    public BattleShip(ShipGrid grid, int x, int y, int length, boolean up) {
        super(up ? 10 * scale : length * 10 * scale, up ? length * 10 * scale : 10 * scale);
        this.grid = grid;
        this.x = x;
        this.y = y;
        grid.add(this, x, y, up ? 1 : length, up ? length : 1);

        this.length = length;
        this.up = up;
        this.boundingBox = new BoundingBox(this.x + 0.01, this.y + 0.01, 0, this.up ? 1 - 0.02 : this.length - 0.02, this.up ? this.length - 0.02 : 1 - 0.02, 0);

        GraphicsContext graphics = this.getGraphicsContext2D();
        graphics.setFill(Color.hsb(Math.random() * 250, 0.5, 0.5));
        graphics.fillRect(0,0,1000,1000);

        addListeners();
    }

    private void addListeners() {
        this.addEventHandler(MouseDragEvent.DRAG_DETECTED, mouseEvent -> {
            if (grid.canMoveShips) {
                dragging = true;
                startFullDrag();
            }
        });
        this.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, mouseEvent -> {
            if (dragging && grid.canMoveShips) {
                /*!this.contains(mouseX, mouseY) && */
                int cellWidth = (int) (10 * scale);
                int gridX = (int) (10 * scale);
                int gridY = (int) (35 * scale);
                double mouseX = mouseEvent.getSceneX();
                double mouseY = mouseEvent.getSceneY();
                double mouseXOffset = mouseEvent.getX();
                double mouseYOffset = mouseEvent.getY();

                double relativeMouseX = mouseX - gridX;
                double relativeMouseY = mouseY - gridY;

                if (grid.contains(relativeMouseX, relativeMouseY)) {

                    int cellX = (int) ((int) (relativeMouseX / cellWidth) /*- (int) (mouseXOffset / cellWidth)*/);
                    int cellY = (int) ((int) (relativeMouseY / cellWidth) /*- (int) (mouseYOffset / cellWidth)*/);

                    this.boundingBox = new BoundingBox(cellX + 0.01, cellY + 0.01, 0, this.up ? 1 - 0.02 : this.length - 0.02, this.up ? this.length - 0.02 : 1 - 0.02, 0);

                    if (canFit()) {
                        this.setTranslateX((cellX - this.x) * 10 * scale);
                        this.setTranslateY((cellY - this.y) * 10 * scale);
                    }
                }
            }
        });
        this.addEventHandler(MouseDragEvent.MOUSE_RELEASED, mouseEvent -> {
            dragging = false;
            if (grid.canMoveShips) {
                grid.getChildren().remove(this);
                int cellX = (int) (this.getTranslateX() / scale / 10 + this.x);
                int cellY = (int) (this.getTranslateY() / scale / 10 + this.y);
                grid.add(this, cellX, cellY, up ? 1 : length, up ? length : 1);
                System.out.println("Released to (" + cellX + ", " + cellY + ")");
                this.x = cellX;
                this.y = cellY;
                this.setTranslateX(0);
                this.setTranslateY(0);
                this.boundingBox = new BoundingBox(this.x + 0.01, this.y + 0.01, 0, this.up ? 1 - 0.02 : this.length - 0.02, this.up ? this.length - 0.02 : 1 - 0.02, 0);
            }
        });
    }

    private boolean canFit() {
        if (this.grid.boundingBox.contains(this.boundingBox)) {
            for (BattleShip ship : grid.ships) {
                if (ship != this && this.overlaps(ship))
                    return false;
            }
            return true;
        }
        return false;
    }

    private boolean overlaps(BattleShip ship) {
        // loop over this ship points
        // see if any enemy points overlap this one;
        return ship.boundingBox.intersects(this.boundingBox);
    }

    @Override
    public String toString() {
        return "BattleShip{" +
                "length=" + length +
                ", up=" + up +
                ", dragging=" + dragging +
                ", x=" + x +
                ", y=" + y +
                "}";
    }
}
