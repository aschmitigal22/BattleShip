module me.melonboy10.battleship {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires eu.hansolo.tilesfx;
    requires javafx.graphics;

    opens me.melonboy10.battleship to javafx.fxml;
    exports me.melonboy10.battleship;
    exports me.melonboy10.battleship.game;
    opens me.melonboy10.battleship.game to javafx.fxml;
}