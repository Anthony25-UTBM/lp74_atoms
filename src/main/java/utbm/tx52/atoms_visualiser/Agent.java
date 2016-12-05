package utbm.tx52.atoms_visualiser;
//package Molecules;

import javafx.geometry.Point3D;
import utbm.tx52.atoms_visualiser.octree.OctreePoint;

public abstract class Agent implements OctreePoint {
    public double posX;
    public double posY;
    public double posZ;

    public Agent() {
    }

    public Agent(double _x, double _y, double _z) {
        posX = _x;
        posY = _y;
        posZ = _z;
    }

    /**
     * Start the agent
     */
    public abstract void start();

    public Point3D getCoordinates() {
        return new Point3D(posX, posY, posZ);
    }

    public double distance(Agent a) {
        return Math.sqrt(distanceSquared(a));
    }

    public double distanceSquared(Agent a) {
        return (
            Math.pow(a.posX - posX, 2) +
            Math.pow(a.posY - posY, 2) +
            Math.pow(a.posZ - posZ, 2)
        );
    }

}





