package net.k1ra.Teleport;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;


public class Edit {
    Stage stage;
    File img;
    Runnable refresh;

    @FXML AnchorPane ap;
    @FXML JFXTextField name;
    @FXML JFXTextField command;
    @FXML JFXColorPicker color;
    @FXML Button select_image;
    @FXML ImageView image;
    @FXML Button save;
    @FXML JFXCheckBox exit;

    void init(Database.Item i) {
        //styling
        ap.setBackground(new Background(new BackgroundFill(Color.web("#202225"), CornerRadii.EMPTY, Insets.EMPTY)));
        name.setStyle("-fx-prompt-text-fill: #b9bbbe; -fx-text-fill: #ffffff;");
        command.setStyle("-fx-prompt-text-fill: #b9bbbe; -fx-text-fill: #ffffff;");
        image.setStyle("-fx-background: #36393f;");
        select_image.setStyle("-fx-background-color: #40444b");
        save.setStyle("-fx-background-color: #40444b");

        if (i != null) {
            name.setText(i.name);
            command.setText(i.command);
            exit.setSelected(i.exit);
            color.setValue(Color.web(i.color));

            if (i.image) {
                String file_extension = null;

                if (Files.exists(Paths.get(Utils.get_local_storage_dir() + i.id + ".jpg")))
                    file_extension = ".jpg";
                else if (Files.exists(Paths.get(Utils.get_local_storage_dir() + i.id + ".jpeg")))
                    file_extension = ".jpeg";
                else if (Files.exists(Paths.get(Utils.get_local_storage_dir() + i.id + ".png")))
                    file_extension = ".png";

                img = new File(Utils.get_local_storage_dir() + i.id + file_extension);
                image.setImage(new Image(img.toURI().toString()));
            }
        }

        select_image.setOnMouseClicked(event -> {
            FileChooser file_chooser = new FileChooser();
            file_chooser.setTitle("Select image");
            file_chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg","*.jpeg", "*.png"));
            img = file_chooser.showOpenDialog(stage);

            image.setImage(new Image(img.toURI().toString()));
        });

        save.setOnMouseClicked(event -> {
            if (!name.getText().isBlank() && !command.getText().isBlank()) {
                if (i == null) {
                    int id = Database.insert_connection(new Database.Item(0,
                            name.getText(),
                            command.getText(),
                            img != null,
                            exit.isSelected(),
                            "#" + Integer.toHexString(color.getValue().hashCode())));
                    if (img != null) {
                        Utils.copy_image(img, id);
                    }
                } else {
                    Database.update_connection(new Database.Item(i.id,
                            name.getText(),
                            command.getText(),
                            img != null,
                            exit.isSelected(),
                            "#" + Integer.toHexString(color.getValue().hashCode())));
                    if (!i.image) {
                        Utils.copy_image(img, i.id);
                    }
                }

                refresh.run();
                stage.close();
            } else {
                Utils.handle_error("Name and command cannot be blank");
            }
        });
    }
}
