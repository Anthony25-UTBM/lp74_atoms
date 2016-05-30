package Molecules_V2;
//package Molecules;

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
	    
	    // Méthodes
	    public Environnement(int _nbAtomes, double _largeur, double _hauteur) {
	        largeur = _largeur;
	        hauteur = _hauteur;
	        generateur = new Random();
	        molecules = new ArrayList();        
	        atomes = new Atome[_nbAtomes];
	        for (int i = 5; i < _nbAtomes; i++) {
	        	int number=0;
	        	int alea = generateur.nextInt(100);
	        	// Ratio à prendre en considération pour C,H,O : 1/4/0,5 en admettant une chaîne carbonée non ramifiée n’excédant pas plus de 3 C.
	            if (alea < 18) number = 6;					// 18% atomes de carbone
	            else { if (alea < 27) number = 8;		    // 9% atomes d'oxygene
	                   else { if (alea < 100) number = 1;	// 73% atomes d'hydrogène
	                   }
	            }
	            atomes[i] = new Atome(i,number, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * 2 * Math.PI);
	        }
            atomes[0] = new Atome(0,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * 2 * Math.PI);
            atomes[1] = new Atome(1,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * 2 * Math.PI);
            atomes[2] = new Atome(2,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * 2 * Math.PI);
            atomes[3] = new Atome(3,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * 2 * Math.PI);
            atomes[4] = new Atome(4,7, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * 2 * Math.PI);
            //atomes[_nbAtomes-1] = new Atome(9, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * 2 * Math.PI);
	    }
	    
	    public void AjouterMolecule(double _posX, double _posY, double rayon) {
	        molecules.add(new Molecule(_posX, _posY, rayon));
	        if (!molecules.isEmpty()) {
	        	System.out.println("Molecule Yes");
	        } else System.out.println("Molecule NO");
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
    
    
    


