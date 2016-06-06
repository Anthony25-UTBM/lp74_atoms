package Molecules_V4;
//package Molecules;

//import Agent;
import java.util.ArrayList;
import java.util.Collections;

// Agent Molecule
public class Molecule extends Agent {
	protected double rayon;
    protected int tempsRestant = 500;
    protected double vitesseX;
    protected double vitesseY;
    protected int etat=2;
        
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
    
