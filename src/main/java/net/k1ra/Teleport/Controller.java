package net.k1ra.Teleport;

import com.jfoenix.controls.JFXMasonryPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.TextStyle;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public Stage stage;

    @FXML JFXMasonryPane mp;
    @FXML ScrollPane sp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //pane color setting
        sp.setStyle("-fx-background: #36393f; -fx-border-color: #36393f");

        //scroll pane scrollbar settings
        sp.getStylesheets().add(this.getClass().getResource("/CSS/scroll_main.css").toExternalForm());
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        get_panes();
    }

    void get_panes() {
        mp.getChildren().clear();
        List<Database.Item> list = Database.get_connections();

        for (Database.Item i : list)
            add_pane(i);

        new_connection_pane();
    }

    void add_pane(Database.Item i) {
        StackPane contents = new StackPane();
        contents.setPadding(Insets.EMPTY);
        HBox text_box = new HBox();
        Text text = new Text(i.name);
        text.setFont(Font.font("system", FontWeight.BOLD, FontPosture.REGULAR, 20));
        text.setFill(Color.WHITE);
        text.setStyle(".outline.label .text {-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 1px; }");
        text_box.getChildren().add(text);
        text_box.setAlignment(Pos.TOP_CENTER);
        text_box.setPadding(new Insets(8));
        contents.getChildren().add(text_box);

        if (i.image) {
            String file_extension = null;

            if (Files.exists(Paths.get(Utils.get_local_storage_dir() + i.id + ".jpg")))
                file_extension = ".jpg";
            else if (Files.exists(Paths.get(Utils.get_local_storage_dir() + i.id + ".jpeg")))
                file_extension = ".jpeg";
            else if (Files.exists(Paths.get(Utils.get_local_storage_dir() + i.id + ".png")))
                file_extension = ".png";

            contents.setStyle("-fx-background-image: url("+new File(Utils.get_local_storage_dir() + i.id + file_extension).toURI()+"); -fx-background-size: cover;");
        }

        final Label l = new Label(null, contents);
        l.setPrefSize(160, 90);
        l.setStyle("-fx-background-color:"+i.color+";");
        l.setTextAlignment(TextAlignment.RIGHT);
        l.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                ContextMenu context_menu = new ContextMenu();
                MenuItem edit_mi = new MenuItem("Edit");
                MenuItem delete_mi = new MenuItem("Delete");

                edit_mi.setOnAction(event1 -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/edit.fxml"));
                        Pane pane = loader.load();
                        Stage stage = new Stage();
                        Scene scene = new Scene(pane);

                        Edit edit = loader.getController();
                        edit.stage = stage;
                        edit.refresh = this::get_panes;
                        edit.init(i);

                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(scene);
                        stage.setMaximized(true);
                        stage.setTitle("Connection editor");
                        stage.setMinHeight(600);
                        stage.setMinWidth(600);
                        stage.show();
                    } catch (Exception e) {
                        Utils.handle_error(e.toString());
                    }
                });

                delete_mi.setOnAction(event2 -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete connection");
                    alert.setHeaderText("Are you sure you want to delete this connection?");

                    alert.showAndWait().ifPresent((btnType) -> {
                        if (btnType.equals(ButtonType.OK)) {
                            Database.delete_connection(i);
                            get_panes();
                        }
                    });
                });

                context_menu.getItems().add(edit_mi);
                context_menu.getItems().add(delete_mi);
                Point mouse = java.awt.MouseInfo.getPointerInfo().getLocation();
                context_menu.show(stage, mouse.x, mouse.y);
            } else {
                try {
                    Process proc = new ProcessBuilder(i.command.split(" ")).start();
                    if (i.exit)
                        System.exit(0);
                } catch (Exception e) {
                    Utils.handle_error(e.toString());
                }
            }
        });
        mp.getChildren().add(l);
    }

    void new_connection_pane() {
        StackPane contents = new StackPane();
        HBox text_box = new HBox();
        Text text = new Text("New connection");
        text.setFont(Font.font("system", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        text.setFill(Color.WHITE);
        text_box.getChildren().add(text);
        text_box.setAlignment(Pos.TOP_CENTER);
        text_box.setPadding(new Insets(8));
        contents.getChildren().add(text_box);

        ImageView image = new ImageView();
        image.setFitHeight(60);
        image.setFitWidth(60);
        image.setPreserveRatio(true);
        image.setImage(new Image(getClass().getResource("/images/add.png").toExternalForm()));
        contents.getChildren().add(image);

        final Label l = new Label(null, contents);
        l.setPrefSize(160, 90);
        l.setTextAlignment(TextAlignment.RIGHT);
        l.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.SECONDARY) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/edit.fxml"));
                    Pane pane = loader.load();
                    Stage stage = new Stage();
                    Scene scene = new Scene(pane);

                    Edit edit = loader.getController();
                    edit.stage = stage;
                    edit.refresh = this::get_panes;
                    edit.init(null);

                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(scene);
                    stage.setMaximized(true);
                    stage.setTitle("Connection editor");
                    stage.setMinHeight(600);
                    stage.setMinWidth(600);
                    stage.show();
                } catch (Exception e) {
                    Utils.handle_error(e.toString());
                }
            }
        });
        mp.getChildren().add(l);
    }
}
