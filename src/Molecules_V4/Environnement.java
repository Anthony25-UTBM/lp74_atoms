package Molecules_V4;
//package jeuDeLaVie;

import java.util.Observable;
import java.util.Random;

// Grille représentant l'environnement + les atomes
public class Environnement extends Observable {
    protected int largeur;
    protected int hauteur;
    protected int couleur;
    protected int [][][] contenu;
    
    public Environnement(int _largeur, int _hauteur, int _densite) {
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
}

