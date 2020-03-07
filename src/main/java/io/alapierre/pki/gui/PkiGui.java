package io.alapierre.pki.gui;
/**
 * @author Adrian Lapierre {@literal <al@alapierre.io>}
 * created 19.09.18
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.alapierre.rsa.RsaUtil;

import java.io.IOException;
import java.util.Objects;

public class PkiGui extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        RsaUtil.init();



        primaryStage.setTitle("PKI Certificate Request Generator");

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("main.fxml"),
                        "problem z odczytem main.fxml"
                ));

        Scene scene = new Scene(root, 529, 347);

        scene.getStylesheets().add(
                getClass().getClassLoader().getResource("error.css").toString());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
