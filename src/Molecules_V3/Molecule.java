package Molecules_V3;

import java.util.ArrayList;
//import java.util.Collections;

// Agent Molecule
public class Molecule extends Agent {
    private   int TEMOIN = 10;		//pour les tests
    protected int num;
    protected ArrayList<Atome> atomes;
    protected String symboles  [] = {"", "H", "HE", "LI", "BE", "B", "C", "N", "O", "F"};
    protected int[] formuleBrute;
	protected double rayon;
    protected int tempsRestant = 500;
    protected double vitesseX;
    protected double vitesseY;
    protected int etat_saturation=0;	//1 -> partiellement saturée;	2 -> saturée (stable)

    public Molecule() {
    }
    public Molecule(int _num, Point p, double _rayon) {
    	num = _num;
    	pos = p;
        rayon = _rayon;
    	atomes = new ArrayList<Atome> ();
    	System.out.println("Molecule Num = "+num);
    }
    public Molecule(int _num, Atome _a1, Atome _a2) {
    	num = _num;
    	pos = _a1.pos;
        rayon = _a1.rayon;
    	atomes = new ArrayList<Atome> ();
    	formuleBrute = new int[10];
    	atomes.add(_a1); atomes.add(_a2);
    	formuleBrute[_a1.a_number]++; formuleBrute[_a2.a_number]++;
    	if (saturation() == false ) etat_saturation = 1;
    	else etat_saturation = 2;
    	if (num == TEMOIN) afficheMolecule();
    }

    public void DeveloppeMolecule(Atome _a1) {
    	atomes.add(_a1);
    	formuleBrute[_a1.a_number]++;
    	if (saturation() == false ) etat_saturation = 1;
    	else {etat_saturation = 2; afficheMoleculeBrute();}
    	//if (num == TEMOIN) afficheMolecule();
    }
  
    public String getFormuleBrute() {
    	String formule = "";
        for (int i = 2; i < 10; i++){
        	if (formuleBrute[i] == 1) formule = formule.concat(symboles[i]);
        	else if (formuleBrute[i] > 1) formule = formule.concat(symboles[i]+formuleBrute[i]);              	
        }
       	if (formuleBrute[1] == 1) formule = formule.concat(symboles[1]);
    	else if (formuleBrute[1] > 1) formule = formule.concat(symboles[1]+formuleBrute[1]);
      return formule;
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

    public boolean estSurMolecule(int x, int y) {
    	if ((x > pos.x-rayon && x < pos.x+rayon) && (y > pos.y-rayon && y < pos.y+rayon)) return true;
    	else return false;
    }
    
    public void afficheMolecule() {
    	System.out.print("Molécule "+num+" : ");
       	for (Atome a : atomes)
    		System.out.print(a.symb +"-");
    	if (etat_saturation == 2) System.out.println("(saturée)");
    	else System.out.println("(non saturée)");
    }

    public void afficheMoleculeBrute() {
    	System.out.print("Molécule "+num+" : "+getFormuleBrute());      
    	if (etat_saturation == 2) System.out.println(" (saturée)");
    	else System.out.println(" (non saturée)");
    }
    
    public boolean saturation() {
    	int n = atomes.size();
    	while (n > 0) {
        	if (atomes.get(n-1).etat_liaisons != 2) {
        		if (num == TEMOIN) System.out.println(atomes.get(n-1).etat_liaisons);
        		return false;
        	}
    		n--;
    	}
    	return true;
    }

}
    
