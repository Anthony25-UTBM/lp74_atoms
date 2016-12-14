package utbm.tx52.atoms_visualiser;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import utbm.tx52.atoms_visualiser.controllers.AController;

/**
 * Created by adah on 11/12/16.
 */
public class FactoryGui extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Runtime rt = Runtime.instance();
        Properties p = new ExtendedProperties();
        p.setProperty(Profile.GUI, "true");
        ProfileImpl pc = new ProfileImpl(p);
        pc.setParameter(ProfileImpl.MAIN_HOST, "127.0.0.1");
        pc.setParameter(Profile.PLATFORM_ID, "sink-platform");
        pc.setParameter(Profile.LOCAL_HOST, "127.0.0.1");
        pc.setParameter(Profile.CONTAINER_NAME, "sink-container");
        pc.setParameter(Profile.NO_MTP, "true");
        AgentContainer container = rt.createAgentContainer(pc);

        AController c;
        try {
            FXMLLoader f = new FXMLLoader(ApplicationAgent.class.getClassLoader().getResource(("uiAtoms.fxml")));
            Parent parent = f.load();
            c = f.getController();
            c.setParent(parent);
            c.setContainer(container);
            container.acceptNewAgent("controller", c);
            c.AStart(stage, true);

        } catch (Exception ex) {
            System.out.println("cant read fxml file Error message : " + ex.getMessage());
        }
    }
}
