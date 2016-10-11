package TX52;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class ASphere extends Sphere implements IASphere {

    private double rayon;
    private double[] couleurs; // r b g
    private double[] position;// x y z
    private EventHandler<MouseEvent> mouseEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent me) {

                    setTranslateX(me.getX());  // -
                    setTranslateY(me.getY());  // -
                    setTranslateZ(me.getX());  // -

                }
            });
        }
    };

    ASphere(double _rayon, double[] _position, double[] _couleurs) {
        super();
        rayon = _rayon;
        couleurs = _couleurs;
        position = _position;

        setup();
    }

    private void setup() {
        this.setRadius(rayon);
        this.setT(position);
        this.setColors(couleurs);
        //this.addEventHandler(MouseEvent.ANY,mouseEventHandler);

    }

    public void setT(double[] position) {
        this.setTranslateX(position[0]);
        this.setTranslateY(position[1]);
        this.setTranslateZ(position[2]);
    }

    public void setColors(double[] couleurs) {
        final PhongMaterial material = new PhongMaterial();

        material.setDiffuseColor(new Color(couleurs[0], couleurs[1], couleurs[2], 1));
        material.setSpecularColor(new Color(couleurs[0], couleurs[1], couleurs[2], 0.2));
        this.setMaterial(material);
    }

    public void setRayon(double r) {
        rayon = r;
        this.setRadius(rayon);

    }

    public void setCouleurs(double[] couleurs_) {
        couleurs = couleurs_;
        setColors(couleurs_);
    }

    public void setPosition(double[] _position) {
        position = _position;
        this.setT(_position);
    }


    //TODO add drag and drop!
}
