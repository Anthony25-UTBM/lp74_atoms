package Molecules_V4;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;


public class ApplicationV4 extends Application {
	 
	//scene
    final Group root = new Group();
    final AGroup world = new AGroup();
    //camera
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final AGroup camera2 = new AGroup();
    final AGroup camera3 = new AGroup();
    final int cameraDistance = 450;
    private Timeline timeline;
    boolean timelinePlaying = false;
    double ONE_FRAME = 1.0/24.0;
    double DELTA_MULTIPLIER = 200.0;
    double CONTROL_MULTIPLIER = 0.1;
    double SHIFT_MULTIPLIER = 0.1;
    double ALT_MULTIPLIER = 0.5;
        
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    //axis
    final Group axisGroup = new Group();
    
    
    private void setupCamera()
    {
    	root.getChildren().add(camera2);
    	camera2.getChildren().add(camera3);
    	camera3.getChildren().add(camera);
    	
    	camera.setNearClip(0.1);
    	camera.setFarClip(10000.0);
    	camera.setTranslateZ(-cameraDistance);
    	
    	camera3.setRotateZ(180.0);
    	camera2.ry.setAngle(320.0);
    	camera2.rx.setAngle(40);
    }
 
    private void setupScene() {    	
        root.getChildren().add(world);
    }
    
    private void setupAxes() {
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
    
    
    
    
    
    
    
    
    
    
    // start 
 
    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(args);
    }
    

	@Override
	public void start(Stage stage) throws Exception {
	    setupScene();
	    setupCamera();
	    setupAxes();
 
        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        
        double rayon = 10;
        double [] position= {20,20,20};
        double [] couleurs= {0,0,1};
        
        
        ASphere atom = new ASphere(rayon,position,couleurs);
        world.getChildren().add(atom);
        
        
        handleMouse(scene, world);
 
        stage.setTitle("Atom pour les nuls");
        stage.setScene(scene);
        stage.show();
        
        scene.setCamera(camera);
		
	}
	
	private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override public void handle(MouseEvent me) {
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
                	camera2.ry.setAngle(camera2.ry.getAngle() - mouseDeltaX*modifierFactor*modifier*2.0);  // +
                	camera2.rx.setAngle(camera2.rx.getAngle() + mouseDeltaY*modifierFactor*modifier*2.0);  // -
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*modifierFactor*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                	camera3.t.setX(camera3.t.getX() + mouseDeltaX*modifierFactor*modifier*0.3);  // -
                    camera3.t.setY(camera3.t.getY() + mouseDeltaY*modifierFactor*modifier*0.3);  // -
                }
            }
        });
    }
 
}