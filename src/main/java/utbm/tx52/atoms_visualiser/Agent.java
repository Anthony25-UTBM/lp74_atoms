package utbm.tx52.atoms_visualiser;
//package Molecules;

public abstract class Agent {
    public double posX;
    public double posY;
    public double posZ;
    protected int lifetime = 500;

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





