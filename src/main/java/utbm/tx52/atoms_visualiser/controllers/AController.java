package utbm.tx52.atoms_visualiser.controllers;

import jade.wrapper.AgentContainer;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utbm.tx52.atoms_visualiser.entities.Atom;
import utbm.tx52.atoms_visualiser.entities.Environment;
import utbm.tx52.atoms_visualiser.exceptions.NegativeSpeedException;
import utbm.tx52.atoms_visualiser.octree.Octree;
import utbm.tx52.atoms_visualiser.utils.IPeriodicTableFactory;
import utbm.tx52.atoms_visualiser.utils.Pair;
import utbm.tx52.atoms_visualiser.utils.StatsElement;
import utbm.tx52.atoms_visualiser.view.AScene;

import java.util.*;


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

public class AController extends jade.core.Agent {
    private static final Logger logger = LogManager.getLogger("AController");
    public static ObservableList<String> items = FXCollections.observableArrayList();
    static protected Color couleurs[] = {Color.WHITE, Color.BLUE, Color.CHARTREUSE, Color.INDIGO, Color.IVORY, Color.LEMONCHIFFON, Color.BLACK, Color.PINK, Color.RED};
    //axis
    final Group axisGroup = new Group();
    protected Stage mainStage;
    protected AnimationTimer animTimer;
    protected AnimationTimer animTimerMolecule;
    protected boolean is_playing = false;
    protected IPeriodicTableFactory periodicTableFactory;
    // right menu
    Scene rootScene;
    @FXML
    VBox uiTableMolecules;
    // left menu
    @FXML
    MenuItem uiEgg;


    @FXML
    UIAtomController uiAtomController;
    @FXML
    UIMoleculeController uiMoleculeController;
    @FXML
    UIReactionController uiReactionController;
    private Parent parent;
    private boolean isCHNO;
    private int m_numberOfAtoms;
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
        handleMouse(uiReactionController.subScene, parent);
        handleMouse(uiAtomController.subSceneAtome, parent);
        handleMouse(uiMoleculeController.subSceneMolecule, parent);
    };
    private double ratio = 10;
    private TreeItem<StatsElement> atoms_groups;
    private AgentContainer container;

    static protected void setElement(String string) {
        items.add(string);
    }

    @Override
    protected void setup() {
        super.setup();
        System.out.println("init Controller");
    }

    public double getScreenWidth() {
        return screen_width;
    }

    public double getScreenHeight() {
        return screen_height;
    }



    public void setStatsUpdate(boolean update) {
        updateStat = update;
    }


    private void initAtomsNumber(IController controller) {
        controller.setNumberOfAtoms(0);
        if (controller.getUINumberOfAtoms() != null) {
            controller.getUINumberOfAtoms().textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.isEmpty()) controller.setNumberOfAtoms(0);
                else controller.setNumberOfAtoms(Integer.parseInt(newValue));
            });
        }

    }


    private void setListeners(boolean addListeners, IController controller) {
        if (addListeners) {
            controller.getSubScene().addEventHandler(MouseEvent.ANY, mouseEventHandler);
        } else {
            controller.getSubScene().removeEventHandler(MouseEvent.ANY, mouseEventHandler);
        }
    }


    public void random_elem_gen(IController controller, int nb_atoms) {

        controller.getSubScene().getWorld().getChildren().clear();
        if (nb_atoms > 0) {
            double size = screen_width * ratio;

            controller.setEnvironnement(new Environment(
                    this.container, nb_atoms, size, isCHNO()));

        } else
            controller.getEnvironnement().atoms = new Octree<Atom>(controller.getEnvironnement().atoms.getSize(), controller.getEnvironnement().atoms.getMaxObjects());
        updateStat = true;
    }

    public void random_elem_gen(IController controller) {
        random_elem_gen(controller, controller.getNumberOfAtoms());
        updateStat = true;
    }

    public void clear_pool(IController controller) {
        random_elem_gen(controller, 0);
    }

    public void setParent(Parent p) {
        parent = p;
    }

    private void searchListener(IController controller) {
        controller.getUISearch().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                controller.getUIAtomsVBOX().getChildren().clear();
                for (int i = 0; i < periodicTableFactory.getInstance().getSymbole().size(); ++i) {
                    if (periodicTableFactory.getInstance().getSymbole().get(i).toLowerCase().startsWith(newValue.toLowerCase())) {
                        addLabel(controller, periodicTableFactory.getInstance().getSymbole().get(i), AController.couleurs[i % 9]);
                    }
                }
            }
        });
    }


    private void addAtomsTableListners(IController controller) {

        controller.getUIAtomType().valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != null) {
                    String tmpLabel = ((Label) newValue).getText();
                    String ancienLabel = ((Label) newValue).getText();
                    if (!tmpLabel.equals("Show all")) {
                        controller.getUIAtomsVBOX().getChildren().clear();
                        for (int i = 0; i < periodicTableFactory.getInstance().getSymbole().size(); ++i) {
                            if (periodicTableFactory.getInstance().getGroup().get(i).equals(tmpLabel)) {
                                addLabel(controller, periodicTableFactory.getInstance().getSymbole().get(i), AController.couleurs[i % 9]);
                            }
                        }
                    } else {
                        controller.getUIAtomsVBOX().getChildren().clear();
                        for (int i = 0; i < periodicTableFactory.getInstance().getSymbole().size(); ++i) {
                            addLabel(controller, periodicTableFactory.getInstance().getSymbole().get(i), AController.couleurs[i % 9]);
                        }
                    }
                }
            }
        });
    }

    private void addLabel(IController controller, String label, Color couleur) {
        Label l = new Label(label, new Circle(8, couleur));

        controller.getUIAtomsVBOX().getChildren().add(l);
        l.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = l.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(label);
                db.setContent(content);
                controller.setDraggedAtom(label);

                event.consume();
            }
        });

        l.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.setDraggedAtom(label);
            }
        });


    }

    protected void addSearchLabel(IController controller) {
        controller.getUIAtomType().getItems().clear();
        if (!periodicTableFactory.getInstance().getUniqGroup().isEmpty()) {
            controller.getUIAtomType().getItems().add(new Label("Show all"));
            for (String grp : periodicTableFactory.getInstance().getUniqGroup())
                controller.getUIAtomType().getItems().add(new Label(grp));
            controller.getUIAtomType().setEditable(false);
            controller.getUIAtomType().setPromptText("Atom Type");
        }
    }


    private void initTables(IController controller) {
        initListView(controller);
        for (String s : periodicTableFactory.getInstance().getSymbole()) {
            Color couleur = AController.couleurs[(periodicTableFactory.getInstance()).getSymbole().indexOf(s) % 9];
            addLabel(controller, s, couleur);
        }
        addAtomsTableListners(controller);
        searchListener(controller);
    }

    private void initListView(IController controller) {

        controller.getUIListAtoms().setItems(items);
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
        uiReactionController.subScene.getWorld().getChildren().addAll(axisGroup);
    }

    private void startControllers(IController controller) {
        double size = screen_width * ratio;
        controller.init(this);
        initAtomsNumber(controller);
        controller.setEnvironnement(new Environment(this.container, (int) m_numberOfAtoms, size, this.isCHNO()));
        stop(controller);
        initStatsTable(controller);
        setTimers();

        handleMouse(controller.getSubScene(), controller.getSubScene().getWorld());
    }

    public void setupContainers(IController controller) {
        controller.getSubScene().heightProperty().bind(controller.getUIAnchor().heightProperty());
        controller.getSubScene().widthProperty().bind(controller.getUIAnchor().widthProperty());
        controller.getEnvironnement().updateAtoms(controller.getSubScene().getWorld());
        updateStats(controller);
    }

    public AnimationTimer createTimer(IController controller) {
        return new AnimationTimer() {
            @Override
            public void handle(long l) {

                controller.getSubScene().heightProperty().bind(controller.getUIAnchor().heightProperty());
                controller.getSubScene().widthProperty().bind(controller.getUIAnchor().widthProperty());
                controller.getEnvironnement().updateAtoms(controller.getSubScene().getWorld());
                updateStats(controller);
            }
        };
    }

    public void setTimers() {
        animTimer = createTimer(uiReactionController);
        animTimerMolecule = createTimer(uiMoleculeController);
    }

    public void AStart(Stage stage, boolean isCHNO) throws Exception {
        this.periodicTableFactory = new IPeriodicTableFactory(isCHNO);
        stage.show();
        updateStat = true;
        setCHNO(isCHNO);
        rootScene = new Scene(parent);
        rootScene.setFill(Color.GRAY);
        double size = screen_width * ratio;
        startControllers(uiMoleculeController);
        startControllers(uiReactionController);

        animTimer.start();
        animTimerMolecule.start();
        stage.setTitle("Atom pour les nuls");
        stage.setScene(rootScene);
        //stage.setFullScreen(true);
        stage.show();
        updateStat = true;
        container.start();


        //rootScene.setCamera(camera);
    }

    public void initStatsTable(IController controller) {
        ObservableList stats_columns = controller.getUIStatistics().getColumns();

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
        controller.getUIStatistics().setRoot(stats_root);
    }

    public void updateStats(IController controller) {
        int nbAtoms;
        try {
            nbAtoms = controller.getEnvironnement().atoms.getObjects().size();
        } catch (InterruptedException e) {
            e.printStackTrace();
            nbAtoms = 0;
        }

        List<StatsElement> elem = Arrays.asList(
                new StatsElement("Atomes inactifs", String.valueOf(controller.getEnvironnement().nbOfNotActiveAtoms())),
                new StatsElement("Nombre d'atoms", String.valueOf(nbAtoms))
        );
        TreeItem root = controller.getUIStatistics().getRoot();
        root.getChildren().clear();
        elem.stream().forEach((e) -> {
            root.getChildren().add(new TreeItem<StatsElement>(e));
        });
        if (updateStat) {


            atoms_groups.getChildren().clear();

            // show number of each atom
            {
                updateStat = false;
                Map<String, Integer> atoms_groups_map = controller.getEnvironnement().nbOfEachAtoms();
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

    public void stop(IController controller) {
        controller.getUIPlayBtn().setText("Play");
        controller.getUIPlayBtn().setStyle("-fx-Background-color: #1E88E5;");
        controller.getUIPlayBtn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                play(controller);
            }
        });
        try {
            setSpeed(0, controller);
        } catch (NegativeSpeedException e) {
            e.printStackTrace();
        }
        is_playing = false;
    }

    public void play(IController controller) {
        controller.getUIPlayBtn().setText("Stop");
        controller.getUIPlayBtn().setStyle("-fx-Background-color: #F44336;");

        controller.getUIPlayBtn().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stop(controller);
            }
        });
        is_playing = true;
        speedSliderHandler(controller);
    }

    public void setSpeed(int speed, IController controller) throws NegativeSpeedException {
        if (is_playing || speed == 0)
            controller.getEnvironnement().setAtomsSpeed(speed);
    }

    public void speedSliderHandler(IController controller) {
        try {
            setSpeed((int) controller.getUISpeedSlider().getValue(), controller);
        } catch (NegativeSpeedException e) {
            try {
                setSpeed(0, controller);
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


    private void switchMode(IController controller) {
        clear_pool(controller);
        addSearchLabel(controller);
        initTables(controller);
        initAtomsNumber(controller);
        initListView(controller);
        refresh(controller);

    }
    @FXML
    public void switchMode() {
        this.isCHNO = !this.isCHNO;
        this.periodicTableFactory.setIsCHNO(isCHNO);
        //uiAtomsVbox.getChildren().clear();
        switchMode(uiMoleculeController);
        switchMode(uiReactionController);
        updateStat = true;
    }

    public void setAtomMode() {
    }

    public void setMolecularMode() {
    }

    public void setReactionMode() {
    }

    private void refresh(IController controller) {
        controller.getUIListAtoms().refresh();
    }


    public void setContainer(AgentContainer container) {
        this.container = container;
    }
}

