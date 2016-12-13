package utbm.tx52.atoms_visualiser;

import jade.core.Agent;
import javafx.application.Application;

public class ApplicationAgent extends Agent {


    @Override
    public void setup() {
        System.out.println("Setup agent application");
        System.setProperty("prism.dirtyopts", "false");
        Application.launch(FactoryGui.class);
    }


}