package utbm.tx52.atoms_visualiser.controllers;

import com.jfoenix.controls.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import utbm.tx52.atoms_visualiser.entities.Environment;
import utbm.tx52.atoms_visualiser.view.AScene;

/**
 * Created by adah on 06/12/16.
 */
public interface IController {

    AnchorPane getUIAnchor();

    void setUIAnchor(AnchorPane uiAnchor);

    Environment getEnvironnement();

    void setEnvironnement(Environment env);

    void init(AController a);

    void setupScene();

    void clear_pool();

    void random_elem_gen();

    AScene getSubScene();

    int getNumberOfAtoms();

    void setNumberOfAtoms(int nbAtoms);

    JFXTextField getUINumberOfAtoms();

    JFXComboBox getUIAtomType();

    ListView getUIListAtoms();

    VBox getUIAtomsVBOX();

    String getDraggedAtom();

    void setDraggedAtom(String draggedAtom);

    JFXTextField getUISearch();

    JFXButton getUIPlayBtn();

    JFXSlider getUISpeedSlider();

    JFXTreeTableView getUIStatistics();


}
