package utbm.tx52.atoms_visualiser.controllers;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import utbm.tx52.atoms_visualiser.entities.Environment;
import utbm.tx52.atoms_visualiser.utils.EasterEgg;
import utbm.tx52.atoms_visualiser.view.AScene;

/**
 * Created by adah on 06/12/16.
 */
public class UIReactionController implements IController {
    @FXML
    AnchorPane uiAnchor;
    AController aController;
    AScene subScene;
    String m_draggedAtom;
    @FXML
    JFXButton uiPlayBtn;
    @FXML
    JFXSlider uiSpeedSlider;
    @FXML
    JFXToggleButton fullmode;
    int nbAtoms;
    @FXML
    VBox uiAtomsVbox;
    @FXML
    JFXTextField uiSearch;
    @FXML
    JFXComboBox uiAtomType;
    @FXML
    ListView<String> uiList;
    @FXML
    JFXTreeTableView uiStatistics;
    @FXML
    JFXTextField uiGenAtomNumber;
    private Environment env;

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
        return subScene;
    }

    @Override
    public int getNumberOfAtoms() {
        setNumberOfAtoms(Integer.parseInt(getUINumberOfAtoms().getText()));
        return nbAtoms;
    }

    @Override
    public void setNumberOfAtoms(int nbAtoms) {
        this.nbAtoms = nbAtoms;
    }

    @Override
    public JFXTextField getUINumberOfAtoms() {
        return uiGenAtomNumber;
    }

    @Override
    public JFXComboBox getUIAtomType() {
        return this.uiAtomType;
    }

    @Override
    public ListView getUIListAtoms() {
        return uiList;
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

    @FXML
    public void play() {
        aController.play(this);
    }

    @FXML
    public void switchMode() {
        aController.switchMode();
    }

    @FXML
    public void speedSliderHandler()

    {
        aController.speedSliderHandler(this);
    }

    @Override
    public AnchorPane getUIAnchor() {
        return this.uiAnchor;
    }

    @Override
    public void setUIAnchor(AnchorPane uiAnchor) {
        this.uiAnchor = uiAnchor;
    }

    @Override
    public Environment getEnvironnement() {
        return env;
    }

    @Override
    public void setEnvironnement(Environment env) {
        this.env = env;
    }

    public void init(AController aController) {

        this.aController = aController;
        subScene = new AScene(uiAnchor.getPrefWidth(), uiAnchor.getPrefHeight());

        setupScene();
    }

    @Override
    public void setupScene() {
        SetupSceneHelper.setupScene(
                uiAnchor,
                subScene,
                aController,
                this);
        uiAnchor.setOnDragOver(event -> {
            if (event.getGestureSource() != uiAnchor &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

    }

    @FXML
    public void eggLaunch() {
        EasterEgg egg = new EasterEgg(aController.getScreenWidth(), aController.getScreenHeight() - 120);
        uiAnchor.getChildren().removeAll();
        uiAnchor.getChildren().clear();
        //m_subScene = new SubScene(egg, screen_width, screen_height, false, SceneAntialiasing.BALANCED);
        uiAnchor.getChildren().add(egg);
        egg.play();
    }

    @Override
    public JFXButton getUIPlayBtn() {
        return uiPlayBtn;
    }

    @Override
    public JFXSlider getUISpeedSlider() {
        return uiSpeedSlider;
    }

    @Override
    public JFXTreeTableView getUIStatistics() {
        return uiStatistics;
    }


}
