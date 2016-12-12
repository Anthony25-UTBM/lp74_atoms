package utbm.tx52.atoms_visualiser.entities;

import javafx.geometry.Point3D;
import utbm.tx52.atoms_visualiser.octree.OctreePoint;

public abstract class Agent implements OctreePoint {
    public Point3D coord;

    public Agent() {
        coord = Point3D.ZERO;
    }

    public Agent(Point3D coord) {
        this(coord.getX(), coord.getY(), coord.getZ());
    }

    public Agent(double coordX, double coordY, double coordZ) {
        coord = new Point3D(coordX, coordY, coordZ);
    }

    /**
     * Start the agent
     */
    public abstract void start();

    public Point3D getCoordinates() {
        return coord;
    }

    public void setCoordinates(Point3D newCoord) {
        coord = newCoord;
    }

    public void setCoordinates(double coordX, double coordY, double coordZ) {
        coord = new Point3D(coordX, coordY, coordZ);
    }

    public double distance(Agent a) {
        return coord.distance(a.getCoordinates());
    }

    public double distanceSquared(Agent a) {
        return (
            Math.pow(a.getCoordinates().getX() - coord.getX(), 2) +
            Math.pow(a.getCoordinates().getY() - coord.getY(), 2) +
            Math.pow(a.getCoordinates().getZ() - coord.getZ(), 2)
        );
    }

}





