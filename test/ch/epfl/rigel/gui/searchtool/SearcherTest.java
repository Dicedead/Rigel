package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

final class SearcherTest  extends Application {
    public static void main(String[] args)
    {
        launch(args);
    }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";
    private static final String AST_CATALOGUE_NAME =
            "/asterisms.txt";

    @Override
    public void start(Stage primaryStage) throws IOException {
        try (InputStream hs = resourceStream()) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build();

            Searcher searcher = new Searcher(10, p -> true, catalogue);
            Canvas sky = new Canvas();
            searcher.setEditable(true);
            searcher.setVisible(true);

            StackPane root = new StackPane(sky);
            root.getChildren().add(searcher);

            primaryStage.setScene(new Scene(root, 300, 250));
            primaryStage.show();
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            primaryStage.setY(100);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        }
    }
    private InputStream resourceStream() {
        return getClass().getResourceAsStream("/hygdata_v3.csv");
    }
}