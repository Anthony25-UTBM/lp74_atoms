package TX52;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

public class ABranch extends Cylinder {
    private double[] position;
    private double[] couleurs;
    private double height;
    private double radius;


    public ABranch(double[] _position, double[] couleurs, double height, double radius) {
        super();
        this.couleurs = couleurs;
        this.position = _position;
        this.height = height;
        this.radius = radius;
        this.setHeight(height);
        this.setRadius(radius);

        setup();
    }

    private void setup() {
        this.setT(position);
        this.setColors(couleurs);
    }


    private void setT(double[] position) {
        this.setTranslateX(position[0]);
        this.setTranslateY(position[0]);
        this.setTranslateZ(position[0]);
    }

    private void setColors(double[] couleurs) {
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(new Color(couleurs[0], couleurs[1], couleurs[2], 1));
        material.setSpecularColor(new Color(couleurs[0], couleurs[1], couleurs[2], 0.2));
        this.setMaterial(material);
    }


    public double[] getPosition() {
        return position;
    }


    public void setPosition(double[] position) {
        this.position = position;
        this.setT(position);
    }


    public double[] getCouleurs() {
        return couleurs;
    }


    public void setCouleurs(double[] couleurs) {
        this.couleurs = couleurs;
        this.setColors(couleurs);
    }


    public void setR(double rayon) {
        this.radius = rayon;
        this.setRadius(rayon);
    }

}
