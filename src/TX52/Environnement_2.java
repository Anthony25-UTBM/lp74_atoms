package TX52;
//package Molecules;

import java.util.*;

//import java.util.Collections;

// Grille repr�sentant l'environnement + les atomes
public class Environnement_2 extends Observable {
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
                       else { if (alea < 100) contenu[i][j][0] = 1;	// 8% atomes d'hydrog�ne
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
    // protected Atome[] atomes;
    protected ArrayList<Atome> atomes;
    protected ArrayList<Molecule> molecules;
    protected Random generateur;
    protected double largeur;
    protected double hauteur;
    protected double profondeur;

    // M�thodes
    public Environnement_2(int _nbAtomes, double _largeur, double _profondeur, double _hauteur, boolean isCHNO) {
        largeur = _largeur;
        hauteur = _hauteur;
        profondeur = _profondeur;
        generateur = new Random();
        molecules = new ArrayList();
        atomes = new ArrayList();
        int nbSamples;

        if (isCHNO) {
            nbSamples = 4;
        } else {
            PeriodicTable t_periodic = PeriodicTable.getInstance();
            nbSamples = t_periodic.getSymbole().size();
            System.out.println("Number of samples " + nbSamples);
            if (nbSamples == 0) return;
        }


        for (int i = 0; i < _nbAtomes; i++) {
            int number = 0;
            number = generateur.nextInt(nbSamples - 1);
            if (isCHNO) number = CHNO.getInstance().getANumber(number);

            atomes.add(new Atome(number, generateur.nextDouble() * largeur, generateur.nextDouble() * hauteur, generateur.nextDouble() * profondeur, generateur.nextDouble() * 2 * Math.PI, isCHNO));
        }
    }

    public void AjouterMolecule(double _posX, double _posY, double rayon) {
        molecules.add(new Molecule(_posX, _posY, rayon));
        if (!molecules.isEmpty()) {
            System.out.println("Molecule Yes");
        } else System.out.println("Molecule NO");
    }

    protected void MiseAJourMolecule() {
        for (Molecule m : molecules) {
            m.MiseAJour();
        }
        //TODO:
        //atomes.removeIf(a2 -> a2.estMort());
    }

    protected void MiseAJourAtomes(AGroup world) {
        for (Atome a : atomes) {
            a.MiseAJour(atomes, molecules, largeur, hauteur, profondeur);
            a.draw(world);
        }
    }

    public void addAtome(Atome a) {

        atomes.add(a);
    }

    public void MiseAJourEnv(AGroup world) {
        MiseAJourAtomes(world);
        MiseAJourMolecule();
        setChanged();
        notifyObservers();
    }

    public Map<String, Integer> nbOfEachAtoms() {
        Map<String, Integer> atom_groups = new Hashtable<String, Integer>();
        for (Atome a : atomes) {
            int current_nb = atom_groups.containsKey(a.getSymb()) ? atom_groups.get(a.getSymb()) : 0;
            atom_groups.put(a.getSymb(), current_nb + 1);
        }

        return atom_groups;
    }

    public int nbOfNotActiveAtoms() {
        int not_active_atoms = 0;
        for (Atome a : atomes) {
            if (a.isNotActive())
                not_active_atoms++;
        }

        return not_active_atoms;
    }


    public void setAtomesSpeed(int speed) {
        for (Atome a : atomes) {
            a.setSpeed(speed);
        }
    }

    public double getSpeed() {
        return atomes.get(0).getSpeed();
    }
}
    
    
    

