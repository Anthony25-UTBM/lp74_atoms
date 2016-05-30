package Molecules_V3;

//import Agent;
import java.awt.Color;
//import java.util.ArrayList;
//import java.util.Collections;
import java.util.Random;

// Agent Atome
public class Liaison extends Agent {
    // Constantes
    public static final int L_SIMPLE = 1;
    public static final int L_DOUBLE = 2;
    public static final int L_TRIPLE = 3;

    // Attributs communs aux liaisons
    
    // Attributs de la liaison
	//private   int TEMOIN = 10;		//pour les tests
    protected int num;
    protected int etat_liaisons;
    protected int    nbLiaisonsPotentielles;
    protected Atome [] atomes_lies;
    protected double dim;
    protected Point posA;
    protected Point posB;
    protected Color couleur;

    //attributs simulation
    protected double vitesseX;
    protected double vitesseY;
    protected Random generateur;
    protected int tempsRestant = 500;
        
    public Liaison(int _num, int _n, double _x, double _y, double _z) {
    	num = _num;
        //System.out.println("Liaison créée");
    }
    
    public double getNum() {
    	return num;
    }
    
}
    
