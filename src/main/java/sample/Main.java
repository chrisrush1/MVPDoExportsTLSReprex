package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;


public class Main extends Application {
    private static final String BASE_URL = "https://wmts10.geo.admin.ch/1.0.0/ch.swisstopo.pixelkarte-farbe/default/current/3857/11/1074/";
    private ImageView iv;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Alternate way of forcing TLSv1.2 to using JVM param. This also fails when used in the App image created by beryx runtime :(
//        try {
//            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
//            ctx.init(null, null, null);
//            SSLContext.setDefault(ctx);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

        HBox hBox = new HBox(0.0);
        hBox.setPrefHeight(400);
        hBox.setPrefWidth(600);
        iv = new ImageView();
        iv.setFitWidth(200);
        iv.setFitHeight(150);
        iv.setPreserveRatio(true);
        hBox.getChildren().add(iv);

        primaryStage.setTitle("Reprex");
        primaryStage.setScene(new Scene(hBox, 300, 275));
        primaryStage.show();

        //load map tile from server
        Task mapTileLoadTask = new Task() {

            @Override
            protected Object call() throws Exception {
                Thread.sleep(1000);

                Image image = null;
                for (int i = 715; i < 739; i++) {
                    String urlString = BASE_URL + i + ".jpeg";
                    System.out.println(urlString);
                    URL url = new URL(urlString);
                    try {
                        //open inputStream to show SSLHandshakeExpception seen in main project
                        InputStream inputStream = url.openConnection().getInputStream();

                        image = new Image(urlString, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (image != null)
                        setMapImage(image);
                    Thread.sleep(10);
                }

                return null;
            }
        };
        new Thread(mapTileLoadTask).start();
            }

    public synchronized void setMapImage(Image image) {
        Platform.runLater(() -> {
            if (iv != null)
                iv.setImage(image);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
