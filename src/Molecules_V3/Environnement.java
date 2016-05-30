package Molecules_V3;

import java.util.ArrayList;
//import java.util.Collections;
import java.util.Observable;
import java.util.Random;

// Grille représentant l'environnement + les atomes
public class Environnement extends Observable {
/*
	// Attributs		
	protected int largeur;
    protected int hauteur;
    protected int couleur;
    protected int [][][] contenu;
    
    public Environnement_V2(int _largeur, int _hauteur, int _densite) {
        largeur = _largeur;
        hauteur = _hauteur;
        couleur = 1;	//vide, bleu, rouge, noir
        Random generateur = new Random();
        
        contenu = new int [largeur][hauteur][couleur];
        for (int i = 0; i < largeur; i++) {
            for (int j = 0; j < hauteur; j++) {
            	int alea = generateur.nextInt(_densite);
                if (alea < 5) contenu[i][j][0] = 3;					// 0,5% atomes de carbone
                else { if (alea < 20) contenu[i][j][0] = 2;		    // 1,5% atomes d'oxygene
                       else { if (alea < 100) contenu[i][j][0] = 1;	// 8% atomes d'hydrogène
                              else contenu[i][j][0] = 0;			// 90% vide
                       }
            	}
            }
        }
    }
    
    public void ChangerEtat(int ligne, int colonne) {
        //contenu[ligne][colonne][0] = !contenu[ligne][colonne][0];
        if (contenu[ligne][colonne][0] >= 1) contenu[ligne][colonne][0]=0;
        else contenu[ligne][colonne][0]=1;
    }
    
    public int NbVoisinsVivants(int colonne, int ligne) {
        int i_min = Math.max(0, colonne-1);
        int i_max = Math.min(largeur-1, colonne+1);
        int j_min = Math.max(0, ligne-1);
        int j_max = Math.min(hauteur-1, ligne+1);
        int nb = 0;
        for (int i = i_min; i <= i_max; i++) {
            for (int j = j_min; j <= j_max; j++) {
                //if (contenu[i][j][0] && !(i==colonne && j==ligne)) {
                if (contenu[i][j][0]>=1 && !(i==colonne && j==ligne)) {
                    nb++;
                }
            }
        }
        return nb;
    }
    
    public void MiseAJour(boolean avecApplication) {
        if (avecApplication) {
            int [][][] nouvelEnv = new int [largeur][hauteur][couleur];
            for (int i = 0; i < largeur; i++) {
                for (int j = 0; j < hauteur; j++) {
                    int nb = NbVoisinsVivants(i, j);
                    //if (nb == 3 || (nb == 2 && contenu[i][j][0])) {
                    if (nb == 3 || (nb == 2 && contenu[i][j][0]>=1)) {
                    	//nouvelEnv[i][j][0] = 1;
                    	nouvelEnv[i][j][0] = contenu[i][j][0];
                    } else nouvelEnv[i][j][0] = contenu[i][j][0];
                }
            }
            contenu = nouvelEnv;
        }
        setChanged();
        notifyObservers();
    }
*/   
    
   
	    // Attributs
	    protected Atome[] atomes;
	    protected ArrayList<Molecule> molecules;
	    protected Random generateur;
	    protected double largeur;
	    protected double hauteur;
	    protected int num_a;
	    protected int num_m;
	    protected int nb_a;
	    protected int nb_m;
	    protected int [][] _molecules;	//TOD : intéret / Arraylist molecules
	    
	    // Méthodes
	    public Environnement(int _nbAtomes, double _largeur, double _hauteur) {
	    	nb_a = _nbAtomes;
	    	nb_m = 0;
	        largeur = _largeur;
	        hauteur = _hauteur;
	        generateur = new Random();
	        molecules = new ArrayList<Molecule>();        
	        atomes = new Atome[nb_a];
	        _molecules = new int [nb_a][1];
	        for (int i = 3; i < nb_a; i++) {
	        	int number=0;
	        	int alea = generateur.nextInt(100);
	        	// Ratio à prendre en considération pour C,H,O : 1/4/0,5 en admettant une chaîne carbonée non ramifiée n’excédant pas plus de 3 C.
	            if (alea < 18) number = 6;					// 18% atomes de carbone (6)
	            else { if (alea < 27) number = 8;		    // 9% atomes d'oxygene (8)
	                   else { if (alea < 100) number = 1;	// 73% atomes d'hydrogène (1)
	                   }
	            }
	            atomes[i] = new Atome(i,number, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, 1, generateur.nextDouble() * 2 * Math.PI, this);
	        }
            atomes[0] = new Atome(0,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, 1, generateur.nextDouble() * 2 * Math.PI, this);
            atomes[1] = new Atome(1,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, 1, generateur.nextDouble() * 2 * Math.PI, this);
            atomes[2] = new Atome(2,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, 1, generateur.nextDouble() * 2 * Math.PI, this);
            //atomes[nb_a-1] = new Atome(9, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, 1, generateur.nextDouble() * 2 * Math.PI);
	    }

	    public void setMolecule(Atome a1, Atome a2) {
	    	if (_molecules[a1.num][0] == 0 && _molecules[a2.num][0] == 0) {
	    		nb_m++;
	    		AjouterMolecule(a1, a2);
	    		_molecules[a1.num][0]=_molecules[a2.num][0]=nb_m;
	    	} else {
	    		_molecules[a1.num][0]=_molecules[a2.num][0];
	    		CompleterMolecule(_molecules[a1.num][0], a1);
	    	}
	    }
	    
	    public void AjouterMolecule(Atome a1, Atome a2) {
	    	molecules.add(new Molecule(nb_m, a1, a2));

	    }
	    
	    public void CompleterMolecule(int _n, Atome a1) {
    	molecules.get(_n-1).DeveloppeMolecule(a1);
    }
	    
	    
	    protected void MiseAJourMolecule() {
	        for(Molecule m : molecules) {
	            m.MiseAJour();
	        }
	        //TODO:
	       //atomes.removeIf(a2 -> a2.estMort());
	    }
	    
	    protected void MiseAJourAtomes() {
	        for (Atome a : atomes) {
	            a.MiseAJour(atomes,molecules,largeur,hauteur);
	        }
	    }
	    
	    public void MiseAJourEnv() {
	        MiseAJourAtomes();
	        MiseAJourMolecule();
	        setChanged();
	        notifyObservers();
	    }
}
    
    
    


