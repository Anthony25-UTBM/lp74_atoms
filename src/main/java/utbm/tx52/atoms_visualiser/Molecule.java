package utbm.tx52.atoms_visualiser;
//package Molecules;

//import Agent;

// Agent Molecule
public class Molecule extends Agent {
    protected double radius;
    protected double speedX;
    protected double speedY;
    protected ElementState state = ElementState.attached;

    public Molecule(double posX, double posY, double radius) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void update() { }
}
    
