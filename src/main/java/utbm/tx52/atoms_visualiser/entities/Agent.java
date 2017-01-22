package utbm.tx52.atoms_visualiser.entities;

import javafx.geometry.Point3D;
import utbm.tx52.atoms_visualiser.octree.OctreePoint;
import utbm.tx52.atoms_visualiser.utils.RandomHelper;

public abstract class Agent extends jade.core.Agent implements OctreePoint, Comparable<Agent> {
    public String id;
    protected Point3D coord;

    public Agent() {
        this(Point3D.ZERO);
    }

    public Agent(double coordX, double coordY, double coordZ) {
        this(new Point3D(coordX, coordY, coordZ));
    }

    public Agent(Point3D coord) {
        this.coord = coord;
        this.id = RandomHelper.getRandomID();
    }

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

    public int compareTo(Agent a) {
        return Integer.compare(this.id.hashCode(), a.id.hashCode());
    }

}





