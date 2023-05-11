module com.example.tictactoe_fx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.tictactoe_fx to javafx.fxml;
    exports com.example.tictactoe_fx;
}