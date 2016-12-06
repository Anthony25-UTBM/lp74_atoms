package utbm.tx52.atoms_visualiser.controllers;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import utbm.tx52.atoms_visualiser.AScene;
import utbm.tx52.atoms_visualiser.Atom;
import utbm.tx52.atoms_visualiser.Formula;

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
    private AController acontroller;
    private String m_draggedAtom;

    public void init(AController taController) {
        this.acontroller = taController;
        subSceneMolecule = new AScene(uiAnchorMolecule.getPrefWidth(), uiAnchorMolecule.getPrefHeight());
        initFormula();
        setupScene();

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
        ArrayList<Atom> atoms = null;
        atoms = f.parse(acontroller.env, m_Formula, acontroller.isCHNO());
        for (Atom a : atoms) {
            try {
                acontroller.env.addAtom(a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        uiFormula.setText("");
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
