package utbm.tx52.atoms_visualiser;

import javafx.geometry.Point3D;

// Agent Molecule
public class Molecule extends Agent {
    protected double radius;
    protected Point3D speed = Point3D.ZERO;
    protected Point3D coord = Point3D.ZERO;
    protected ElementState state = ElementState.attached;

    public Molecule(Point3D coord, double radius) {
        super(coord);
        this.radius = radius;
    }

    public Molecule(double coordX, double coordY, double radius) {
        super(coordX, coordY, 0);
        this.radius = radius;
    }

    public void start() {};

    public double getRadius() {
        return radius;
    }

    public void update() { }
}
    
