package utbm.tx52.atoms_visualiser.controllers;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import utbm.tx52.atoms_visualiser.entities.Atom;
import utbm.tx52.atoms_visualiser.entities.Environment;
import utbm.tx52.atoms_visualiser.entities.Formula;
import utbm.tx52.atoms_visualiser.view.AScene;

import java.util.ArrayList;

/**
 * Created by adah on 06/12/16.
 */
public class UIMoleculeController implements IController {
    @FXML
    AnchorPane uiAnchorMolecule;
    @FXML
    JFXTextField uiFormula;
    @FXML
    JFXComboBox uiAtomType;
    @FXML
    VBox uiAtomsVbox;
    @FXML
    JFXTextField uiSearch;
    @FXML
    ListView uiList;
    @FXML
    JFXButton uiPlayBtn;
    @FXML
    JFXSlider uiSpeedSlider;
    @FXML
    JFXToggleButton fullmode;
    @FXML
    JFXTreeTableView uiStatisticsMolecule;
    int nbAtoms;
    AScene subSceneMolecule;
    String m_Formula;
    private Environment env;
    private AController acontroller;
    private String m_draggedAtom;

    @Override
    public AnchorPane getUIAnchor() {
        return this.uiAnchorMolecule;
    }

    @Override
    public void setUIAnchor(AnchorPane uiAnchor) {
        this.uiAnchorMolecule = uiAnchor;
    }

    @Override
    public Environment getEnvironnement() {
        return env;
    }

    @Override
    public void setEnvironnement(Environment env) {
        this.env = env;
    }

    public void init(AController taController) {
        this.acontroller = taController;
        env = new Environment(acontroller.getEnvSize());
        subSceneMolecule = new AScene(uiAnchorMolecule.getPrefWidth(), uiAnchorMolecule.getPrefHeight());
        initFormula();
        setupScene();
        this.getSubScene().heightProperty().bind(this.getUIAnchor().heightProperty());
        this.getSubScene().widthProperty().bind(this.getUIAnchor().widthProperty());


    }

    @Override
    public void setupScene() {
        SetupSceneHelper.setupScene(uiAnchorMolecule
                , subSceneMolecule
                , acontroller,
                this);
    }


    private void initFormula() {
        uiFormula.textProperty().addListener((observable, oldValue, newValue) -> {
            m_Formula = newValue;
        });
    }

    @FXML
    public void generateAtomsByFormula() {
        Formula f = new Formula();
        ArrayList<Atom> atoms = f.parse(env, m_Formula, acontroller.isCHNO());
        for (Atom a : atoms) {
            try {
                env.addAtom(a);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        uiFormula.setText("");
        this.env.updateAtoms();
        this.acontroller.setStatsUpdate(true);
        //acontroller.refresh(this);
    }

    @FXML
    public void speedSliderHandler() {
        acontroller.speedSliderHandler(this);
    }

    @FXML
    public void switchMode() {
        acontroller.switchMode();
    }

    @FXML
    public void clear_pool() {
        acontroller.clear_pool(this);
    }

    @Override
    public void random_elem_gen() {
        acontroller.random_elem_gen(this);
    }

    @Override
    public AScene getSubScene() {
        return subSceneMolecule;
    }

    @Override
    public int getNumberOfAtoms() {
        return nbAtoms;
    }

    @Override
    public void setNumberOfAtoms(int nbAtoms) {
        this.nbAtoms = nbAtoms;
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
        return uiStatisticsMolecule;
    }

    @FXML
    public void play() {
        acontroller.play(this);
    }

}
