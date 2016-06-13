package Molecules_V4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationV4 extends Application {


    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        AController c = new AController();
        try {
            FXMLLoader f = new FXMLLoader(getClass().getResource("uiAtoms.fxml"));
            Parent parent = f.load();
            c = f.getController();
            c.setParent(parent);

        } catch (IOException ex) {
            System.out.println("cant read fxml file");
        }
        c.AStart(stage);
    }

}