package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.gui.searchtool.Searcher;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class EpflLogo extends Application {
    public static void main(String[] args) { System.out.println("lul");launch(args); }

    @Override
    public void start(Stage primaryStage) {
        try (InputStream hs = resourceStream()) {
            /*StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build();

            Searcher searcher = new Searcher(5, p -> true, catalogue);

            StackPane root = new StackPane();
            root.getChildren().add(searcher);
            searcher.setEditable(true);
            searcher.setVisible(true);

            primaryStage.setScene(new Scene(root, 300, 250));
            primaryStage.show();*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private InputStream resourceStream() {
        return getClass().getResourceAsStream("/hygdata_v3.csv");
    }
}