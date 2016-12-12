package utbm.tx52.atoms_visualiser.controllers;

import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import utbm.tx52.atoms_visualiser.entities.Atom;
import utbm.tx52.atoms_visualiser.view.AScene;

/**
 * Created by adah on 06/12/16.
 */
public class SetupSceneHelper {
    public static void setupScene(AnchorPane uiAnchor, AScene subSceneAtome, AController aController, IController controller) {
        uiAnchor.getChildren().add(subSceneAtome);
        uiAnchor.setOnDragOver(event -> {
            if (event.getGestureSource() != uiAnchor &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        AScene finalSubSceneAtome = subSceneAtome;
        uiAnchor.setOnDragDropped(event -> {
            if (!controller.getDraggedAtom().equals("")) {

                Atom atom = new Atom(
                        controller.getEnvironnement(),
                        aController.periodicTableFactory.getInstance().getSymbole().indexOf(controller.getDraggedAtom()),
                        event.getSceneX(),
                        event.getSceneY(), 0, 0, aController.isCHNO()
                );
                atom.draw(finalSubSceneAtome.getWorld());
                controller.setDraggedAtom("");
                aController.setStatsUpdate(true);
                try {
                    controller.getEnvironnement().addAtom(atom);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                event.consume();
            }
        });
        //aController.addSearchLabel();
    }


}
