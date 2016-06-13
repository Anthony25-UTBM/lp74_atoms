package Molecules_V4;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

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
    //scene
    Parent parent;
    Scene scene;
    Scene rootScene;
    Group cameraRoot = new Group();
    Group rootDraw = new Group();
    TimerTask tache;
    @FXML
    AnchorPane uiViewer;
    private int screen_width = 1024;
    private int screen_height = 768;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;

    public void setupCamera() {
        cameraRoot.getChildren().add(camera2);
        camera2.getChildren().add(camera3);
        camera3.getChildren().add(camera);

        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-cameraDistance);

        camera3.setRotateZ(180.0);
        camera2.ry.setAngle(320.0);
        camera2.rx.setAngle(40);
    }

    public void setupScene() {
        scene = new Scene(uiViewer);
        uiViewer.getChildren().add(world);
        uiViewer.getChildren().add(cameraRoot);
    }

    public void random_elem_gen(int nb_atoms) {
        world.getChildren().clear();
        env = new Environnement_2(nb_atoms, screen_width, screen_height, 1000);
    }

    public void random_elem_gen() {
        random_elem_gen(1000);
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
        setupAxes();

        rootScene = new Scene(parent);

        scene.setFill(Color.GREY);
        final ReentrantLock lock = new ReentrantLock();
        env = new Environnement_2(1000, screen_width, screen_height, 1000);

        AnimationTimer animTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                env.MiseAJourAtomes(world);
            }

        };
        animTimer.start();

        handleMouse(scene, world);

        stage.setTitle("Atom pour les nuls");
        stage.setScene(rootScene);
        stage.show();

        ///rootScene.setCamera(camera);

    }


    private void handleMouse(Scene scene, final Node root) {

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

                double modifier = 1.0;
                double modifierFactor = 0.1;

                if (me.isControlDown()) {
                    modifier = 0.1;
                }
                if (me.isShiftDown()) {
                    modifier = 10.0;
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