package Molecules_V3;

/*
class ase.atom.Atom(symbol='X', position=(0, 0, 0), tag=None, momentum=None, mass=None,
                    magmom=None, charge=None, atoms=None, index=None)
Parameters:
symbol: str or intCan be a chemical symbol (str) or an atomic number (int).
position: sequence of 3 floatsAtomi position.
tag: intSpecial purpose tag.
momentum: sequence of 3 floatsMomentum for atom.
mass: floatAtomic mass in atomic units.
magmom: float or 3 floatsMagnetic moment.
charge: floatAtomic charge. 
 */


//import Agent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

// Agent Atome
public class Atome extends Agent {
    // TODO: TRIER Constantes
    public static final double PAS = 3;
    public static final double DISTANCE_MIN = 10;
    public static final double DISTANCE_MIN_CARRE = 100;
    public static final double DISTANCE_MAX = 40;
    public static final double DISTANCE_MAX_CARRE = 1600;
    public static final double DISTANCE_MAX_LIAISON = 20;
    
    // TODO: TRIER Attributs communs aux atomes
    protected String noms      [] = {"", "Hydrogène", "Hélium", "Lithium", "Béryllium", "Bore", "Carbone", "Azote", "Oxygène", "Fluor"};
    protected String symboles  [] = {"", "H", "HE", "LI", "BE", "B", "C", "N", "O", "F"};
    protected int    covalence [] = {0,1,0,0,0,0,4,3,2,1};   
    //protected double rayons    [] = {0,5,0,0,0,0,14,13,12,11};	//rayons : H=25pm; C=70pm; O=60pm
    //protected double rayons    [] = {0,2.5,0,0,0,0,7,6.5,6,5.5};	//rayons : H=25pm; C=70pm; O=60pm
    protected double rayons    [] = {0,4,0,0,0,0,10,9,8,7};	//rayons : H=25pm; C=70pm; O=60pm
    protected Color  couleurs  [] = {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.BLACK, Color.BLUE, Color.RED, Color.YELLOW};
    public int TEMOIN = 10;
    protected int num;
    protected int a_number;		//1 -> H;  6 -> C;  8 -> 0
    protected String nom;
    protected String symb;
    protected int    etat_liaisons;		//0 -> libre;    1 -> partiellement lié;	2 -> lié (stable)
    protected int    nbLiaisonsPotentielles;
    protected ArrayList <String> liaisons;
    protected double rayon;
    protected Color couleur;
    protected Environnement env;

    //TODO: TRIER attributs simulation
    protected double vitesseX;
    protected double vitesseY;
    protected Random generateur;
    protected int tempsRestant = 500;
        
    public Atome(int _num, int _n, double _x, double _y, double _z, double _dir, Environnement e) {
    	num = _num;
    	a_number = _n;
    	nom = noms[a_number];
    	symb = symboles[a_number];
    	etat_liaisons = 0;
    	nbLiaisonsPotentielles = covalence[a_number];
    	//liaisons = new ArrayList ();
    	liaisons = new ArrayList<String>();
    	rayon = rayons[a_number];
    	couleur = couleurs[a_number];
    	this.pos= new Point(_x, _y, _z);
        vitesseX = Math.cos(_dir);
        vitesseY = Math.sin(_dir);
        env = e;
        //System.out.println("Atome créé ("+symb+")");
    }
    
    public double getRayon() {
    	return rayon;
    }
    
    public boolean estSurAtome(int x, int y) {
    	if ((x > pos.x-rayon && x < pos.x+rayon) && (y > pos.y-rayon && y < pos.y+rayon)) return true;
    	else return false;
    }

    public void ajoutLiaison(String symb) {
    	liaisons.add(symb);
    	nbLiaisonsPotentielles--;
		if (nbLiaisonsPotentielles == 0) etat_liaisons = 2;
		else etat_liaisons = 1;
    }
    
    public void majMolecule(Atome a) {
    	//Environnement env = new Environnement();
    	env.setMolecule(this, a);
    }
    
    //TODO: exploiter cette méthode
    public int estLibre() {
    	return nbLiaisonsPotentielles;
    }

    //
    // mouvement des atomes
    //
    public double getVitesseX() {
        return vitesseX;
    }
    
    public double getVitesseY() {
        return vitesseY;
    }

    
    protected void Normaliser() {
        double longueur = Math.sqrt(vitesseX * vitesseX + vitesseY * vitesseY);
        vitesseX /= longueur;
        vitesseY /= longueur;
    }
    
    protected void MiseAJourPosition() {
        pos.x += PAS * vitesseX;
        pos.y += PAS * vitesseY;
    }

    protected double DistanceLimiteEnv(double envXMin, double envYMin, double envXMax, double envYMax) {
        double min = Math.min(pos.x - envXMin, pos.y - envYMin);
        min = Math.min(min, envXMax - pos.x);
        min = Math.min(min, envYMax - pos.y);
        return min;
    }
    
    //TODO: Interet de cette methode !!    
    protected boolean DansAlignement(Atome a) {
        double distanceCarre = DistanceCarre(a);
        return (distanceCarre < DISTANCE_MAX_CARRE && distanceCarre > DISTANCE_MIN_CARRE);
    }

    
    //TODO: URGENT
    protected void ObserverEnv(Atome[] atomes) {
        // Recherche de l'atome le plus proche
/*        Atome a;
        if (!atomes[0].equals(this)) {
            a = atomes[0];
        }
        else {
            a = atomes[1];
        }
        double distanceCarre = DistanceCarre(a);
*/
        for (Atome atome : atomes) {
            if (Distance(atome) < 40 && !atome.equals(this) && atomes[0].equals(this)) {
                //a = atome;
                //distanceCarre = DistanceCarre(a);
//System.out.println("Un atome proche !");
            }
        }
        
        // Liaison possible ?
/*
        if (distanceCarre < (a.rayon * a.rayon * 4) && a_number != a.a_number) {
        	if (a.liaison != 0) {
        		System.out.println("Une liaison avec Distance Carre = "+distanceCarre);
        	    liaison --; a.liaison--; 
        		etat = 1; a.etat = 1; 
        		//return true;
        	}
        }
*/
        //return false;
    }    

    //TODO: DEVT méthode
    protected boolean LibererAtomes(Atome[] atomes) {
        //Atome a;
        boolean liberation = false;      
        return liberation;
    }
    
    //TODO: FINALISER méthode
    protected boolean LierAtomes(Atome[] atomes) {
        Atome a;
        ArrayList <Atome> atomesLiables = new ArrayList<Atome>();
        double d=1000, d1;
        boolean _liaison = false;
        if (!atomes[0].equals(this)) {
            a = atomes[0];
        }
        else {
            a = atomes[1];
        }
        // Rechercher l'atome le plus proche à une distance max donnée
        for (Atome atome : atomes) {
        	d1 = Distance(atome);
            if (d1 <= DISTANCE_MAX_LIAISON && !atome.equals(this)) {
            	//TODO: Poursuivre la recherche des atomes situés à une distance donnée
            	atomesLiables.add(atome);
            	if (d1 < d) {
            		d = d1;
            		a = atome;
            		_liaison = true;
            	}
            }
        }
/*TODO:        if (atomesLiables.size() > 0) {
        	Atome a1 = ChoisirAtome(atomesLiables);
        }
*/
        
        // lier les atomes si liaison possible
/*        
  		if (_liaison && nbLiaisonsPotentielles >= 2 && a.nbLiaisonsPotentielles >= 2) {
        	nbLiaisonsPotentielles = nbLiaisonsPotentielles-2; 
        	a.nbLiaisonsPotentielles = a.nbLiaisonsPotentielles-2; 
        	etat = 1; a.etat = 1;
        	ajoutLiaison(a.symb); a.ajoutLiaison(symb);
        	return true;
        } else {
*/  
        if (_liaison && a.nbLiaisonsPotentielles != 0) {
        //if (_liaison && a.nbLiaisonsPotentielles != 0 && a.a_number != 1) {//pas de liaisons entre atomes d'hydrogène !
        //if (_liaison && a.nbLiaisonsPotentielles != 0 && a_number != a.a_number) {//pas de liaisons entre atomes identiques !
        	ajoutLiaison(a.symb); a.ajoutLiaison(symb);
        	majMolecule(a);
        	return true;
        }
//        }
        return false;
    }    
    
    
    //TODO: TEST intérêt
    protected boolean LierAtomes2(Atome[] atomes) {
        // Recherche de l'atome le plus proche
        Atome a;
        if (!atomes[0].equals(this)) {
            a = atomes[0];
        }
        else {
            a = atomes[1];
        }
        double distanceCarre = DistanceCarre(a);
        for (Atome atome : atomes) {
            if (DistanceCarre(atome) < distanceCarre && !atome.equals(this)) {
                a = atome;
                distanceCarre = DistanceCarre(a);
            }
        }
        
        // Liaison possible ?
        //if (distanceCarre < DISTANCE_MIN_CARRE) {
        if (distanceCarre < (a.rayon * a.rayon * 4) && a_number != a.a_number) {
            /*double distance = Math.sqrt(distanceCarre);
            double diffX = (a.posX - posX) / distance;
            double diffY = (a.posY - posY) / distance;
            vitesseX = vitesseX - diffX / 2;
            vitesseY = vitesseY - diffY / 2;
            Normaliser();*/
        	if (a.nbLiaisonsPotentielles != 0) {
        		System.out.println("Une liaison avec Distance Carre = "+distanceCarre);
        		nbLiaisonsPotentielles --; a.nbLiaisonsPotentielles--; 
        		etat_liaisons = 1; a.etat_liaisons = 1; 
        		return true;
        	}
        }
        return false;
    }    
    
    protected boolean EviterAtomes(Atome[] atomes) {
        // Recherche de l'atome le plus proche
        Atome a;
        generateur = new Random();
        if (!atomes[0].equals(this)) {
            a = atomes[0];
        }
        else {
            a = atomes[1];
        }
        double distanceCarre = DistanceCarre(a);
        for (Atome atome : atomes) {
            if (DistanceCarre(atome) < distanceCarre && !atome.equals(this)) {
                a = atome;
                distanceCarre = DistanceCarre(a);
            }
        }
        
        // Evitement
        //if (distanceCarre < DISTANCE_MIN_CARRE) {
        if (distanceCarre < (a.rayon * a.rayon * a.rayon)) {
            double distance = Math.sqrt(distanceCarre);
            double diffX = (a.pos.x - pos.x) / distance;
            double diffY = (a.pos.y - pos.y) / distance;
            double alea = generateur.nextDouble() * 4;
            vitesseX = vitesseX - diffX / alea;
            vitesseY = vitesseY - diffY / alea;
//System.out.println("Atome évité!!");
            Normaliser();
            return true;
        }
        return false;
    }

    
    protected boolean EviterLimiteEnv(double envXMin, double envYMin, double envXMax, double envYMax) {
        // On s'arrete aux limites de l'environnement
        if (pos.x < envXMin) {
            pos.x = envXMin;
        }
        else if (pos.y < envYMin) {
            pos.y = envYMin;
        }
        else if (pos.x > envXMax) {
            pos.x = envXMax;
        }
        else if (pos.y > envYMax) {
            pos.y = envYMax;
        }
        
        // Changer de direction
        double distance = DistanceLimiteEnv(envXMin, envYMin, envXMax, envYMax);
        if (distance < DISTANCE_MIN) {
            if (distance == (pos.x - envXMin)) {
                vitesseX += 0.3;
            }
            else if (distance == (pos.y - envYMin)) { 
                vitesseY += 0.3; 
            } 
            else if (distance == (envXMax - pos.x)) {
                vitesseX -= 0.3;
            } 
            else if (distance == (envYMax - pos.y)) {
                vitesseY -= 0.3;
            }   
            Normaliser();
            return true;
        }
        return false;
    }

    protected boolean EviterMolecule(ArrayList<Molecule> molecules) {
        if (!molecules.isEmpty()) {
            // Recherche de la molécule la plus proche
        	Molecule m = molecules.get(0);
            double distanceCarre = DistanceCarre(m);
            for (Molecule m_2 : molecules) {
                if (DistanceCarre(m) < distanceCarre) {
                    m = m_2;
                    distanceCarre = DistanceCarre(m_2);
                }
            }
            
            if (distanceCarre < (m.rayon * m.rayon * 4)) {
                // Si collision, calcul du vecteur diff
                double distance = Math.sqrt(distanceCarre);
                double diffX = (m.pos.x - pos.x) / distance;
                double diffY = (m.pos.y - pos.y) / distance;
                vitesseX = vitesseX - diffX / 2;
                vitesseY = vitesseY - diffY / 2;
                Normaliser();
                return true;
            }
        }
        return false;        
    }
    
    
    protected void CalculerDirectionMoyenne(Atome[] atomes) {
        double vitesseXTotal = 0;
        double vitesseYTotal = 0;
        int nbTotal = 0;
        for (Atome a : atomes) {
            if (DansAlignement(a)) {
                vitesseXTotal += a.vitesseX;
                vitesseYTotal += a.vitesseY;
                nbTotal++;
            }
        }
        if (nbTotal >= 1) {
            vitesseX = (vitesseXTotal / nbTotal + vitesseX) / 2;
            vitesseY = (vitesseYTotal / nbTotal + vitesseY) / 2;
            Normaliser();
        }
    }

 
    // comportement de mise à jour : Observe -> Agit
    public void MiseAJour(Atome[] atomes, ArrayList<Molecule> molecules, double largeur, double hauteur) {
    	tempsRestant--;
    	if (etat_liaisons == 0) {
    		ObserverEnv(atomes);
    		if (!EviterLimiteEnv(0,0,largeur,hauteur)) {
    			if (!LierAtomes(atomes)) {
    				if (!EviterMolecule(molecules)) {
    					if (!EviterAtomes(atomes)) {
    						CalculerDirectionMoyenne(atomes);
    					}
    				}
    			}
    		}
    		MiseAJourPosition();
        }
    }

}


    
