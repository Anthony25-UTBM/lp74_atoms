package Molecules_V4;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.*;
import com.jfoenix.controls.JFXComboBox;


/**
 * Created by adahs on 11/06/2016.
 */
public class AController {

    final AGroup world = new AGroup();
    //camera
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final AGroup camera2 = new AGroup();
    final AGroup camera3 = new AGroup();
    final int cameraDistance = 450;
    //axis
    final Group axisGroup = new Group();
    protected Environnement_2 env;
    protected Timer timer;
    protected AnimationTimer animTimer;
    //scene
    Parent parent;
    Scene scene;
    Scene rootScene;
    Group cameraRoot = new Group();
    Group rootDraw = new Group();
    TimerTask tache;
    // right menu
    @FXML
    JFXTextField uiSearch;
    @FXML
    VBox    uiAtomsVbox;
    @FXML
    VBox    uiTableMolecules;
    @FXML
    JFXComboBox uiAtomType;
    @FXML
    AnchorPane uiAnchor;
    @FXML
    AnchorPane uiStatisticsAnchor;
    @FXML
    AnchorPane uiAtomesAnchor;
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

    SubScene    m_subScene;
    Group       m_root3D;
    private boolean updateStat;

    private int screen_width = 1024;
    private int screen_height = 768;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private double ratio = 10;
    private  TreeItem<StatsElement> atoms_groups;

    private String m_draggedAtom;
    static protected Color  couleurs [] = {Color.WHITE, Color.BLUE, Color.CHARTREUSE, Color.INDIGO, Color.IVORY, Color.LEMONCHIFFON, Color.BLACK, Color.PINK, Color.RED};
    protected  static ObservableList<String> items = FXCollections.observableArrayList ();
    javafx.scene.control.ScrollPane uiScrollPane;

    static protected void setElement(String string)
    {
        items.add(string);
    }
    private void refresh()
    {
        uiList.refresh();
    }
    private void initListView()
    {

        uiList.setItems(items);
    }


    public void setStatsUpdate(boolean update)
    {
        updateStat = update;
    }

    private void searchListener()
    {
        uiSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                uiAtomsVbox.getChildren().clear();
                for(int i = 0; i < Atome.m_symbole.size();++i)
                {
                    if(Atome.m_symbole.get(i).toLowerCase().startsWith(newValue.toLowerCase()))
                    {
                        addLabel(Atome.m_symbole.get(i),AController.couleurs[i%9]);
                    }
                }
            }
        });
    }

    private void addAtomsTableListners()
    {

        uiAtomType.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String tmpLabel = ((Label)newValue).getText();
                String ancienLabel = ((Label)newValue).getText();
                if(!tmpLabel.equals ("Show all")) {
                    uiAtomsVbox.getChildren().clear();
                    for (int i = 0; i < Atome.m_symbole.size(); ++i) {
                        if (Atome.m_group.get(i).equals(tmpLabel)) {
                            addLabel(Atome.m_symbole.get(i), AController.couleurs[i % 9]);
                        }
                    }
                }
                else
                {
                    uiAtomsVbox.getChildren().clear();
                    for (int i = 0; i < Atome.m_symbole.size(); ++i) {
                            addLabel(Atome.m_symbole.get(i), AController.couleurs[i % 9]);
                    }
                }
            }
        });
    }

    @FXML
    public void eggLaunch()
    {
        EasterEgg egg = new EasterEgg(screen_width,screen_height-120);
        uiAnchor.getChildren().removeAll();
        uiAnchor.getChildren().clear();
        m_subScene = new SubScene(egg, screen_width, screen_height, false, SceneAntialiasing.BALANCED);
        uiAnchor.getChildren().add(egg);
        egg.play();
    }

    private void addLabel(String label, Color couleur)
    {
        Label l = new Label(label,new Circle(8,couleur));

        uiAtomsVbox.getChildren().add(l);
        l.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println("OnDrag Detected");
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

    private void initTables()
    {
        initListView();
        for(String s : Atome.m_symbole)
        {
            Color couleur = AController.couleurs[Atome.m_symbole.indexOf(s)%9];
            addLabel(s,couleur);
        }
        addAtomsTableListners();
        searchListener();
        uiEgg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eggLaunch();
            }
        });


    }

    private void setListeners(boolean addListeners){
        if(addListeners){
            m_subScene.addEventHandler(MouseEvent.ANY, mouseEventHandler);
        } else {
            m_subScene.removeEventHandler(MouseEvent.ANY, mouseEventHandler);
        }
    }
    private final EventHandler<MouseEvent> mouseEventHandler = event -> {
        handleMouse(m_subScene,parent);
    };

    public void setupCamera() {

        cameraRoot.getChildren().add(camera2);
        camera2.getChildren().add(camera3);
        camera3.getChildren().add(camera);

        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(cameraDistance);
        camera.setTranslateX(screen_height);
        camera.setTranslateY(screen_width);
        camera3.setRotateZ(0);
        camera2.ry.setAngle(0);
        camera2.rx.setAngle(0);
    }

    public void setupScene() {


        m_root3D = new Group();
        screen_height = (int)uiAnchor.getPrefHeight();
        screen_width = (int)uiAnchor.getPrefWidth();

        m_subScene = new SubScene(m_root3D, screen_width, screen_height, false, SceneAntialiasing.BALANCED);
        m_subScene.setHeight((double) screen_height);
        m_subScene.setWidth((double) screen_width);
        m_subScene.setHeight(Control.USE_COMPUTED_SIZE);
        m_subScene.setWidth(Control.USE_COMPUTED_SIZE);
        m_subScene.setManaged(false);

        uiAnchor.getChildren().add(m_subScene);
        m_subScene.setCamera(camera);
        m_subScene.setFill(Color.GRAY);
        m_root3D.getChildren().add(world);
        m_root3D.getChildren().add(cameraRoot);

        uiAnchor.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                System.out.println("drag drop");
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
                if(!m_draggedAtom.equals("")){
                    System.out.println("Dragged exit");

                    Atome atom = new Atome(Atome.m_symbole.indexOf(m_draggedAtom),event.getSceneX(),event.getSceneY(),0,0);
                    atom.draw(world);
                    m_draggedAtom = "";
                    updateStat = true;
                    env.addAtome(atom);
                    event.consume();
                }
            }
        });




        if(!Atome.m_uniqGroup.isEmpty())
        {
            uiAtomType.getItems().add(new Label("Show all"));
            for(String grp : Atome.m_uniqGroup)
                uiAtomType.getItems().add(new Label(grp));
            uiAtomType.setEditable(false);
            uiAtomType.setPromptText("Atome Type");
        }

    }

    public void random_elem_gen(int nb_atoms) {
        world.getChildren().clear();
        env = new Environnement_2(
            nb_atoms, screen_width*ratio, screen_height*ratio,
            ratio*(screen_height + screen_width)/2
        );
        updateStat = true;
    }

    public void random_elem_gen() {
        random_elem_gen(1000);
        updateStat = true;
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
        world.getChildren().addAll(axisGroup);
    }

    public void AStart(Stage stage) throws Exception {
        stage.show();
        setupScene();
        setupCamera();
       // setupAxes();
        initTables();
        updateStat = true;


        rootScene = new Scene(parent);

        rootScene.setFill(Color.GREY);
        final ReentrantLock lock = new ReentrantLock();
        env = new Environnement_2(
            1000, screen_width*ratio, screen_height*ratio,
            ratio*(screen_height + screen_width)/2
        );
        setSpeed(0);
        initStatsTable();

        animTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                env.MiseAJourAtomes(world);

                m_subScene.heightProperty().bind(uiAnchor.heightProperty());
                m_subScene.widthProperty().bind(uiAnchor.widthProperty());
                refresh();
                updateStats();


                // updateStats();
            }
        };

        animTimer.start();

        handleMouse(m_subScene, world);

        stage.setTitle("Atom pour les nuls");
        stage.setScene(rootScene);
        // stage.setFullScreen(true);
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

        List<Pair> l_columns_pairs = Arrays.<Pair> asList(
            new Pair<>(description_column, "description"),
            new Pair<>(value_column, "value")
        );
        l_columns_pairs.forEach((col_p) -> {
            TreeTableColumn col = (TreeTableColumn) col_p.x;
            String attr = (String) col_p.y;
            col.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<StatsElement, String>, ObservableValue<String>>() {
                    @Override public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<StatsElement, String> p) {
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
                new TreeItem<StatsElement>(new StatsElement("Nombre d'atomes par groupe", ""))
        );
        uiStatistics.setRoot(stats_root);
    }

    public void updateStats() {

        List<StatsElement> elem = Arrays.<StatsElement> asList(
            new StatsElement("Atomes inactifs", String.valueOf(env.nbOfNotActiveAtoms())),
            new StatsElement("Nombre d'atomes", String.valueOf(env.atomes.size()))
        );
        TreeItem root = uiStatistics.getRoot();
        root.getChildren().clear();
        elem.stream().forEach((e) -> {
            root.getChildren().add(new TreeItem<StatsElement>(e));
        });
        if(updateStat) {


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

    public void stop()
    {
        uiPlayBtn.setText("Play");
        uiPlayBtn.setStyle("-fx-Background-color: #1E88E5;");
        uiPlayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                play();
            }
        });
        setSpeed(0);
    }

    public void play()
    {
        uiPlayBtn.setText("Stop");
        uiPlayBtn.setStyle("-fx-Background-color: #F44336;");

        uiPlayBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stop();
            }
        });
        setSpeed(10);
    }

    public void setSpeed(int speed)
    {
        env.setAtomesSpeed(speed);
    }

    public void speedSliderHandler()
    {
        setSpeed((int) uiSpeedSlider.getValue());
    }

    private void handleMouse(SubScene scene, final Node root) {
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
                double modifierFactor = 10;

                if (me.isControlDown()) {
                    modifier = 1;
                }
                if (me.isShiftDown()) {
                    modifier = 0.1;
                }
                if (me.isPrimaryButtonDown()) {
                    camera2.ry.setAngle(camera2.ry.getAngle() - mouseDeltaX * modifierFactor * modifier * 2.0);  // +
                    camera2.rx.setAngle(camera2.rx.getAngle() + mouseDeltaY * modifierFactor * modifier * 2.0);  // -
                } else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX * modifierFactor * modifier;
                    camera.setTranslateZ(newZ);
                } else if (me.isMiddleButtonDown()) {
                    camera3.t.setX(camera3.t.getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // -
                    camera3.t.setY(camera3.t.getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // -
                }
            }

        });
    }
}

