package utbm.tx52.atoms_visualiser.controllers;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import utbm.tx52.atoms_visualiser.view.AScene;

/**
 * Created by adah on 06/12/16.
 */
public class UIAtomController implements IController {
    @FXML
    AnchorPane uiAnchorAtome;
    AScene subSceneAtome;
    @FXML
    JFXComboBox uiAtomType;
    @FXML
    VBox uiAtomsVbox;
    @FXML
    JFXTextField uiSearch;
    @FXML
    ListView uiAtomDetail;
    private AController aController;
    private String m_draggedAtom;

    public void init(AController aController) {
        this.aController = aController;
        subSceneAtome = new AScene(uiAnchorAtome.getPrefWidth(), uiAnchorAtome.getPrefHeight());
        setupScene();
    }

    @Override
    public void setupScene() {
        SetupSceneHelper.setupScene(uiAnchorAtome, subSceneAtome, aController, this);
    }


    @FXML
    public void clear_pool() {
        aController.clear_pool(this);
    }

    @FXML
    public void random_elem_gen() {
        aController.random_elem_gen(this);
    }

    @Override
    public AScene getSubScene() {
        return subSceneAtome;
    }

    @Override
    public int getNumberOfAtoms() {
        return 1;
    }

    @Override
    public void setNumberOfAtoms(int nbAtoms) {
    }

    @Override
    public JFXTextField getUINumberOfAtoms() {
        return null;
    }

    @Override
    public JFXComboBox getUIAtomType() {
        return uiAtomType;
    }

    @Override
    public ListView getUIListAtoms() {
        return uiAtomDetail;
    }

    @Override
    public VBox getUIAtomsVBOX() {
        return uiAtomsVbox;
    }

    @Override
    public String getDraggedAtom() {
        return m_draggedAtom;
    }

    @Override
    public void setDraggedAtom(String draggedAtom) {
        this.m_draggedAtom = draggedAtom;
    }

    @Override
    public JFXTextField getUISearch() {
        return uiSearch;
    }

    @Override
    public JFXButton getUIPlayBtn() {
        return null;
    }

    @Override
    public JFXSlider getUISpeedSlider() {
        return null;
    }

    @Override
    public JFXTreeTableView getUIStatistics() {
        return null;
    }

    @FXML
    public void switchMode() {
        aController.switchMode();
    }


}
