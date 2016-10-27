package utbm.tx52.atoms_visualiser;
//package Molecules;

//import Agent;

// Agent Molecule
public class Molecule extends Agent {
    protected double rayon;
    protected int tempsRestant = 500;
    protected double vitesseX;
    protected double vitesseY;
    protected int etat = 2;

    public Molecule(double _x, double _y, double _rayon) {
        posX = _x;
        posY = _y;
        rayon = _rayon;
    }

    public double getRayon() {
        return rayon;
    }

    public void MiseAJour() {
        tempsRestant--;
    }

    public boolean estMort() {
        return tempsRestant <= 0;
    }
}
    
