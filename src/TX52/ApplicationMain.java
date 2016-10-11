package TX52;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ApplicationMain extends Application {


    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        AController c = new AController();
        try {
            FXMLLoader f = new FXMLLoader(ApplicationMain.class.getClassLoader().getResource(("./uiAtoms.fxml")));
            Parent parent = f.load();
            c = f.getController();
            c.setParent(parent);


        } catch (Exception ex) {
            System.out.println("cant read fxml file Error message : " + ex.getMessage());
        }
        c.AStart(stage, false);
    }

}