package utbm.tx52.atoms_visualiser.view;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

/**
 * Created by adah on 01/11/16.
 */
public class AScene extends SubScene {
    final private Group root3D = new Group();

    private AGroup world;
    private PerspectiveCamera camera1;
    private AGroup camera2;
    private AGroup camera3;
    private int cameraDistance = 450;
    private Group cameraRoot;


    public AScene(double screen_width, double screen_height) {
        super(new Group(), screen_width, screen_height, false, SceneAntialiasing.BALANCED);
        init();
        setupCamera(screen_width, screen_height);
        setupScene(screen_width, screen_height);

    }

    public void setupCamera(double screen_width, double screen_height) {

        getCameraRoot().getChildren().add(getCamera2());
        getCamera2().getChildren().add(getCamera3());
        getCamera3().getChildren().add(getCamera1());

        getCamera1().setNearClip(0.1);
        getCamera1().setFarClip(10000.0);
        getCamera1().setTranslateZ(getCameraDistance());
        getCamera1().setTranslateX(screen_height);
        getCamera1().setTranslateY(screen_width);
        getCamera3().setRotateZ(0);
        getCamera2().ry.setAngle(0);
        getCamera2().rx.setAngle(0);
    }

    private void init() {
        setWorld(new AGroup());
        setCamera1(new PerspectiveCamera(true));
        setCamera2(new AGroup());
        setCamera3(new AGroup());
        setCameraDistance(450);
        setCameraRoot(new Group());
    }

    public void setupScene(double screen_width, double screen_height) {
        this.setHeight(screen_height);
        this.setWidth(screen_width);
        this.setManaged(false);
        this.setCamera(getCamera1());
        this.setFill(Color.GRAY);
        super.setRoot(root3D);
        root3D.getChildren().add(getWorld());
        root3D.getChildren().add(getCameraRoot());
    }

    public AGroup getWorld() {
        return world;
    }

    public void setWorld(AGroup world) {
        this.world = world;
    }

    public PerspectiveCamera getCamera1() {
        return camera1;
    }

    public void setCamera1(PerspectiveCamera camera1) {
        this.camera1 = camera1;
    }

    public AGroup getCamera2() {
        return camera2;
    }

    public void setCamera2(AGroup camera2) {
        this.camera2 = camera2;
    }

    public AGroup getCamera3() {
        return camera3;
    }

    public void setCamera3(AGroup camera3) {
        this.camera3 = camera3;
    }

    public int getCameraDistance() {
        return cameraDistance;
    }

    public void setCameraDistance(int cameraDistance) {
        this.cameraDistance = cameraDistance;
    }

    public Group getCameraRoot() {
        return cameraRoot;
    }

    public void setCameraRoot(Group cameraRoot) {
        this.cameraRoot = cameraRoot;
    }
}
