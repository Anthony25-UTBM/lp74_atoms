package utbm.tx52.atoms_visualiser;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by adahs on 11/06/2016.
 *
 * @author Aymen DAHECH Anthony ruhier Elbaroudi Soumaya
 * @version 1.0
 *          <p>
 *          cette classe permet de controller toutes actions et d'intéragir avec FXML
 *          la variable env qui contient l'environnement génére les atoms
 *          La composition de molécule peut etre speedy en utilisant le slider
 *          la caméra a été placé à l'intérieur de cube //TODO shortkey to navigate from different point of view
 *          les axes sont masqué //TODO décommenté la ligne //setupAxes() a fin de les avoirs
 *          la tree de la scene peut etre nettoyé suite aux tests quelque dirty node peut etre supprimer (redondance)
 *          features : les molécules peuvent etre géré il faut adopté la classe molécule
 */
//TODO fix subscene width and height

public class AController {
    private static final Logger logger = LogManager.getLogger("AController");
    static protected Color couleurs[] = {Color.WHITE, Color.BLUE, Color.CHARTREUSE, Color.INDIGO, Color.IVORY, Color.LEMONCHIFFON, Color.BLACK, Color.PINK, Color.RED};
    protected static ObservableList<String> items = FXCollections.observableArrayList();

    //axis
    final Group axisGroup = new Group();
    protected Stage mainStage;
    protected Environment env;
    protected Timer timer;
    protected AnimationTimer animTimer;
    protected boolean is_playing = false;
    Scene rootScene;

    // right menu
    @FXML
    JFXTextField uiSearch;
    @FXML
    VBox uiAtomsVbox;
    @FXML
    VBox uiTableMolecules;
    @FXML
    JFXComboBox uiAtomType;
    @FXML
    AnchorPane uiAnchor;
    // left menu
    @FXML
    TreeTableView uiStatistics;
    // bottom bar
    @FXML
    Button uiPlayBtn;
    @FXML
    Slider uiSpeedSlider;
    @FXML
    ListView<String> uiList;
    @FXML
    MenuItem uiEgg;
    @FXML
    JFXToggleButton fullmode;
    @FXML
    JFXTextField uiGenAtomNumber;
    AScene subScene;

    //Atom tab //

    @FXML
    AnchorPane uiAnchorAtome;
    AScene subSceneAtome;
    //Molecular tab//
    @FXML
    AnchorPane uiAnchorMolecule;
    AScene subSceneMolecule;
    @FXML
    JFXTextField uiFormula;




    private
    //scene
            Parent parent;
    private boolean isCHNO;
    private double m_numberOfAtoms;
    private boolean updateStat;
    private int screen_width = 1024;
    private int screen_height = 768;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private final EventHandler<MouseEvent> mouseEventHandler = event -> {
        handleMouse(subScene, parent);
        handleMouse(subSceneAtome, parent);
        handleMouse(subSceneMolecule, parent);
    };

    private IPeriodicTableFactory periodicTableFactory;
    private double ratio = 10;
    private TreeItem<StatsElement> atoms_groups;
    private String m_draggedAtom;
    private String m_Formula;

    static protected void setElement(String string) {
        items.add(string);
    }

    private void refresh() {
        uiList.refresh();
    }

    private void initListView() {

        uiList.setItems(items);
    }

    @FXML
    public void generateAtomsByFormula() {
        Formula f = new Formula();
        ArrayList<Atom> atoms = null;
        atoms = f.parse(m_Formula, isCHNO);
        for (Atom a : atoms) {
            env.addAtom(a);
        }
        uiFormula.setText("");
        refresh();
    }

    public void setStatsUpdate(boolean update) {
        updateStat = update;
    }

    private void searchListener() {
        uiSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                uiAtomsVbox.getChildren().clear();
                for (int i = 0; i < periodicTableFactory.getInstance().getSymbole().size(); ++i) {
                    if (periodicTableFactory.getInstance().getSymbole().get(i).toLowerCase().startsWith(newValue.toLowerCase())) {
                        addLabel(periodicTableFactory.getInstance().getSymbole().get(i), AController.couleurs[i % 9]);
                    }
                }
            }
        });
    }

    private void addAtomsTableListners() {

        uiAtomType.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    String tmpLabel = ((Label) newValue).getText();
                    String ancienLabel = ((Label) newValue).getText();
                    if (!tmpLabel.equals("Show all")) {
                        uiAtomsVbox.getChildren().clear();
                        for (int i = 0; i < periodicTableFactory.getInstance().getSymbole().size(); ++i) {
                            if (periodicTableFactory.getInstance().getGroup().get(i).equals(tmpLabel)) {
                                addLabel(periodicTableFactory.getInstance().getSymbole().get(i), AController.couleurs[i % 9]);
                            }
                        }
                    } else {
                        uiAtomsVbox.getChildren().clear();
                        for (int i = 0; i < periodicTableFactory.getInstance().getSymbole().size(); ++i) {
                            addLabel(periodicTableFactory.getInstance().getSymbole().get(i), AController.couleurs[i % 9]);
                        }
                    }
                }
            }
        });
    }

    @FXML
    public void eggLaunch() {
        EasterEgg egg = new EasterEgg(screen_width, screen_height - 120);
        uiAnchor.getChildren().removeAll();
        uiAnchor.getChildren().clear();
        //m_subScene = new SubScene(egg, screen_width, screen_height, false, SceneAntialiasing.BALANCED);
        uiAnchor.getChildren().add(egg);
        egg.play();
    }

    private void addLabel(String label, Color couleur) {
        Label l = new Label(label, new Circle(8, couleur));

        uiAtomsVbox.getChildren().add(l);
        l.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                logger.debug("OnDrag Detected");
                Dragboard db = l.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(label);
                db.setContent(content);
                m_draggedAtom = label;

                event.consume();
            }
        });

        l.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                m_draggedAtom = label;
            }
        });


    }

    private void initFormula() {
        uiFormula.textProperty().addListener((observable, oldValue, newValue) -> {
            m_Formula = newValue;
        });
    }

    private void initAtomsNumber() {
        m_numberOfAtoms = 0;

        uiGenAtomNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            m_numberOfAtoms = Double.parseDouble(newValue);
        });

    }

    private void initTables() {
        initListView();
        for (String s : periodicTableFactory.getInstance().getSymbole()) {
            Color couleur = AController.couleurs[(periodicTableFactory.getInstance()).getSymbole().indexOf(s) % 9];
            addLabel(s, couleur);
        }
        addAtomsTableListners();
        searchListener();
    }

    private void setListeners(boolean addListeners) {
        if (addListeners) {
            subScene.addEventHandler(MouseEvent.ANY, mouseEventHandler);
        } else {
            subScene.removeEventHandler(MouseEvent.ANY, mouseEventHandler);
        }
    }

    public void setupMoleculeScene() {
        screen_height = (int) uiAnchorMolecule.getPrefHeight();
        screen_width = (int) uiAnchorMolecule.getPrefWidth();
        subSceneMolecule = new AScene(screen_width, screen_height);
        uiAnchorMolecule.getChildren().add(subSceneMolecule);
        uiAnchorMolecule.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                logger.debug("drag drop");
                if (event.getGestureSource() != uiAnchorMolecule &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });
        uiAnchorMolecule.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (!m_draggedAtom.equals("")) {
                    logger.debug("Dragged exit");

                    Atom atom = new Atom(
                        periodicTableFactory.getInstance().getSymbole().indexOf(m_draggedAtom),
                        event.getSceneX(), event.getSceneY(), 0, 0, isCHNO()
                    );
                    atom.draw(subSceneMolecule.getWorld());
                    m_draggedAtom = "";
                    updateStat = true;
                    env.addAtom(atom);
                    event.consume();
                }
            }
        });


    }

    public void setupAtomeScene() {
        screen_height = (int) uiAnchorAtome.getPrefHeight();
        screen_width = (int) uiAnchorAtome.getPrefWidth();
        subSceneAtome = new AScene(screen_width, screen_height);
        uiAnchorAtome.getChildren().add(subSceneAtome);
        uiAnchorAtome.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                logger.debug("drag drop");
                if (event.getGestureSource() != uiAnchorAtome &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });
        uiAnchorAtome.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (!m_draggedAtom.equals("")) {
                    logger.debug("Dragged exit");

                    Atom atom = new Atom(periodicTableFactory.getInstance().getSymbole().indexOf(m_draggedAtom), event.getSceneX(), event.getSceneY(), 0, 0, isCHNO());
                    atom.draw(subSceneAtome.getWorld());
                    m_draggedAtom = "";
                    updateStat = true;
                    env.addAtom(atom);
                    event.consume();
                }
            }
        });


    }


    public void setupScene() {
        screen_height = (int) uiAnchor.getPrefHeight();
        screen_width = (int) uiAnchor.getPrefWidth();

        subScene = new AScene(screen_width, screen_height);
        uiAnchor.getChildren().add(subScene);
        // set slide colors
        uiSpeedSlider.getStylesheets().add(AController.class.getClassLoader().getResource("customSlider.css").toExternalForm());
        uiSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                speedSliderHandler();
            }
        });

        uiAnchor.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                logger.debug("drag drop");
                if (event.getGestureSource() != uiAnchor &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });
        uiAnchor.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (!m_draggedAtom.equals("")) {
                    logger.debug("Dragged exit");

                    Atom atom = new Atom(periodicTableFactory.getInstance().getSymbole().indexOf(m_draggedAtom), event.getSceneX(), event.getSceneY(), 0, 0, isCHNO());
                    atom.draw(subScene.getWorld());
                    m_draggedAtom = "";
                    updateStat = true;
                    env.addAtom(atom);
                    event.consume();
                }
            }
        });

        addSearchLabel();

    }

    private void addSearchLabel() {
        uiAtomType.getItems().clear();
        if (!periodicTableFactory.getInstance().getUniqGroup().isEmpty()) {
            uiAtomType.getItems().add(new Label("Show all"));
            for (String grp : periodicTableFactory.getInstance().getUniqGroup())
                uiAtomType.getItems().add(new Label(grp));
            uiAtomType.setEditable(false);
            uiAtomType.setPromptText("Atom Type");
        }
    }

    public void random_elem_gen(int nb_atoms) {

        subScene.getWorld().getChildren().clear();
        if (nb_atoms > 0) {
            env = new Environment(
                    nb_atoms, screen_width * ratio, screen_height * ratio,
                    ratio * (screen_height + screen_width) / 2
                    , isCHNO()
            );
        } else
            env.atoms.clear();
        updateStat = true;
    }

    public void random_elem_gen() {
        random_elem_gen((int) m_numberOfAtoms);
        updateStat = true;
    }

    public void clear_pool() {
        random_elem_gen(0);
    }

    void setParent(Parent p) {
        parent = p;
    }

    public void setupAxes() {
        //xColor
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        //yColor
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        //zColor
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(240.0, 1, 1);
        final Box yAxis = new Box(1, 240.0, 1);
        final Box zAxis = new Box(1, 1, 240.0);


        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        this.subScene.getWorld().getChildren().addAll(axisGroup);
    }

    public void AStart(Stage stage, boolean isCHNO) throws Exception {
        this.periodicTableFactory = new IPeriodicTableFactory(isCHNO);
        stage.show();
        setupScene();
        setupAtomeScene();
        setupMoleculeScene();
        // setupAxes();
        initTables();
        initAtomsNumber();
        initFormula();
        updateStat = true;
        setCHNO(isCHNO);


        rootScene = new Scene(parent);

        rootScene.setFill(Color.GRAY);
        final ReentrantLock lock = new ReentrantLock();
        env = new Environment(
                (int) m_numberOfAtoms, screen_width * ratio, screen_height * ratio,
                ratio * (screen_height + screen_width) / 2
                , this.isCHNO()
        );
        stop();
        initStatsTable();

        animTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                env.updateAtoms(subScene.getWorld());

                subScene.heightProperty().bind(uiAnchor.heightProperty());
                subScene.widthProperty().bind(uiAnchor.widthProperty());
                refresh();
                updateStats();


                // updateStats();
            }
        };

        animTimer.start();

        handleMouse(subScene, subScene.getWorld());
        handleMouse(subSceneAtome, subSceneAtome.getWorld());
        stage.setTitle("Atom pour les nuls");
        stage.setScene(rootScene);
        //stage.setFullScreen(true);

        stage.show();
        updateStat = true;


        //rootScene.setCamera(camera);
    }

    public void initStatsTable() {
        ObservableList stats_columns = uiStatistics.getColumns();

        TreeTableColumn description_column = (
                new TreeTableColumn<StatsElement, String>("Description")
        );
        TreeTableColumn value_column = (
                new TreeTableColumn<StatsElement, String>("Valeur")
        );

        List<Pair> l_columns_pairs = Arrays.asList(
                new Pair<>(description_column, "description"),
                new Pair<>(value_column, "value")
        );
        l_columns_pairs.forEach((col_p) -> {
            TreeTableColumn col = (TreeTableColumn) col_p.x;
            String attr = (String) col_p.y;
            col.setCellValueFactory(
                    new Callback<TreeTableColumn.CellDataFeatures<StatsElement, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<StatsElement, String> p) {
                            StatsElement e = p.getValue().getValue();
                            return new ReadOnlyObjectWrapper<String>(e.globalGetter(attr));
                        }
                    }
            );
        });

        stats_columns.addAll(description_column, value_column);

        TreeItem<StatsElement> stats_root = (
                new TreeItem<StatsElement>(new StatsElement("Root node", ""))
        );
        atoms_groups = (
                new TreeItem<StatsElement>(new StatsElement("Nombre d'atoms par groupe", ""))
        );
        uiStatistics.setRoot(stats_root);
    }

    public void updateStats() {

        List<StatsElement> elem = Arrays.asList(
                new StatsElement("Atomes inactifs", String.valueOf(env.nbOfNotActiveAtoms())),
                new StatsElement("Nombre d'atoms", String.valueOf(env.atoms.size()))
        );
        TreeItem root = uiStatistics.getRoot();
        root.getChildren().clear();
        elem.stream().forEach((e) -> {
            root.getChildren().add(new TreeItem<StatsElement>(e));
        });
        if (updateStat) {


            atoms_groups.getChildren().clear();

            // show number of each atom
            {
                updateStat = false;
                Map<String, Integer> atoms_groups_map = env.nbOfEachAtoms();
                SortedSet<String> keys = new TreeSet<String>(atoms_groups_map.keySet());
                for (String key : keys) {
                    int nb = atoms_groups_map.get(key);
                    atoms_groups.getChildren().add(
                            new TreeItem<StatsElement>(new StatsElement(key, String.valueOf(nb)))
                    );
                }
            }
        }

        root.getChildren().add(atoms_groups);
    }

    public void stop() {
        uiPlayBtn.setText("Play");
        uiPlayBtn.setStyle("-fx-Background-color: #1E88E5;");
        uiPlayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                play();
            }
        });
        try {
            setSpeed(0);
        } catch (NegativeSpeedException e) {
            e.printStackTrace();
        }
        is_playing = false;
    }

    public void play() {
        uiPlayBtn.setText("Stop");
        uiPlayBtn.setStyle("-fx-Background-color: #F44336;");

        uiPlayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stop();
            }
        });
        is_playing = true;
        speedSliderHandler();
    }

    public void setSpeed(int speed) throws NegativeSpeedException {
        if (is_playing || speed == 0)
            env.setAtomsSpeed(speed);
    }

    public void speedSliderHandler() {
        try {
            setSpeed((int) uiSpeedSlider.getValue());
        } catch (NegativeSpeedException e) {
            try {
                setSpeed(0);
            } catch (NegativeSpeedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void handleMouse(AScene scene, final Node root) {
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();

            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 0.1;
                double modifierFactor = 1.0;

                if (me.isControlDown()) {
                    modifierFactor = 10;
                }
                if (me.isShiftDown()) {
                    modifier = 20;
                }
                if (me.isPrimaryButtonDown()) {
                    scene.getCamera2().ry.setAngle(scene.getCamera2().ry.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0);  // +
                    scene.getCamera2().rx.setAngle(scene.getCamera2().rx.getAngle() + mouseDeltaY * modifierFactor * modifier * 2.0);  // -
                } else if (me.isSecondaryButtonDown()) {
                    double z = scene.getCamera1().getTranslateZ();
                    double newZ = z + mouseDeltaX * modifierFactor * modifier;
                    scene.getCamera1().setTranslateZ(newZ);
                } else if (me.isMiddleButtonDown()) {
                    scene.getCamera3().t.setX(scene.getCamera3().t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                    scene.getCamera3().t.setY(scene.getCamera3().t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
                }
            }

        });
    }


    public boolean isCHNO() {
        return isCHNO;
    }


    public void setCHNO(boolean CHNO) {
        isCHNO = CHNO;
    }


    @FXML
    public void switchMode(ActionEvent event) {
        clear_pool();
        this.isCHNO = !this.isCHNO;
        this.periodicTableFactory.setIsCHNO(isCHNO);
        uiAtomsVbox.getChildren().clear();
        addSearchLabel();
        initTables();
        initAtomsNumber();
        initFormula();
        updateStat = true;
        initListView();
        refresh();
    }

    public void setAtomMode() {
    }

    public void setMolecularMode() {
    }

    public void setReactionMode() {
    }

}

