package net.k1ra.Teleport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage primary_stage) throws Exception{
        //check for data/folder and create if does not exit
        if (!Files.exists(Paths.get(Utils.get_local_storage_dir()))) {
            new File(Utils.get_local_storage_dir()).mkdir();

            //if SQlite DB does not exist, create tables
            Utils.connect_db();
            Database.create_tables();
        } else {
            Utils.connect_db();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/main.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.stage = primary_stage;

        primary_stage.setTitle("Teleport");
        primary_stage.setScene(new Scene(root, 600, 600));
        primary_stage.setMinHeight(600);
        primary_stage.setMinWidth(600);
        primary_stage.setMaximized(true);
        primary_stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
