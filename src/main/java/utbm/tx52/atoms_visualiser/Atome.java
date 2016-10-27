package utbm.tx52.atoms_visualiser;


import com.jfoenix.controls.JFXDialog;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.pow;

/*
Class atom
 */

public class Atome extends Agent {
    public static final double DISTANCE_MIN = 10;
    public static final double DISTANCE_MIN_CARRE = 100;
    public static final double DISTANCE_MAX = 40;
    public static final double DISTANCE_MAX_CARRE = 1600;
    // Constantes
    public static double PAS = 1;
    // nouvelle architecture proposé pr aymen
    // Attributs de l'atome
    protected int a_number;
    protected String symb;
    protected int etat;        //0 -> libre;    1 -> partiellement lié;	2 -> lié (stable)
    protected int liaison;
    protected double rayon;
    // protected Color couleur;
    protected Color jcouleur;
    //attributs simulation
    protected double vitesseX;
    protected double vitesseY;
    protected double vitesseZ;
    protected Random generateur;
    protected int tempsRestant = 500;
    protected double ratioSpeed;
    protected Rectangle m_rect;
    protected Tooltip m_tooltip;
    private ASphere sphere;
    private ArrayList<Covalence> covalence;

    public Atome(int _n, double _x, double _y, double _z, double _dir, boolean isCHNO) {
        ratioSpeed = 1;
        a_number = _n;
        if (isCHNO) {

            CHNO t_chno = CHNO.getInstance();
            int nb = t_chno.getNumber(a_number);
            symb = t_chno.getLimitedAtomsSymbole()[nb];
            liaison = t_chno.getLimitedAtomsLiaison()[nb];
            rayon = t_chno.getLimitedAtomsRayon()[nb];
            jcouleur = t_chno.getLimitedAtomsColor()[nb];


        } else {
            PeriodicTable t_periodic = PeriodicTable.getInstance();
            symb = t_periodic.getSymbole().get(a_number);
            liaison = t_periodic.getLiaisons().get(a_number);
            rayon = t_periodic.getRayons().get(a_number);
            jcouleur = t_periodic.getCouleurs()[a_number % t_periodic.getCouleurs().length];
        }
        etat = 0;

        posX = _x;
        posY = _y;
        posZ = _z;
        vitesseX = Math.cos(_dir);
        vitesseY = Math.sin(_dir);
        vitesseZ = 0;
        double[] pos = {posX, posY, posZ};
        double[] colors = {jcouleur.getRed(), jcouleur.getGreen(), jcouleur.getBlue()};
        sphere = new ASphere(rayon, pos, colors);


        System.out.println("Atome crée" +
                " (" + symb + ")");
    }

    public double getRayon() {
        return rayon;
    }

    public String getSymb() {
        return symb;
    }

    //TODO: exploiter cette m�thode
    public int estlibre() {
        return liaison;
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

    public double getVitesseZ() {
        return vitesseZ;
    }

    protected void Normaliser() {
        double longueur = Math.sqrt(vitesseX * vitesseX + vitesseY * vitesseY + vitesseZ * vitesseZ);
        vitesseX /= longueur;
        vitesseY /= longueur;
        vitesseZ /= longueur;
    }

    protected void MiseAJourPosition() {
        posX += PAS * vitesseX;
        posY += PAS * vitesseY;
        posZ += PAS * vitesseZ;
    }

    protected double DistanceLimiteEnv(double envXMin, double envYMin, double envZMin, double envXMax, double envYMax, double envZMax) {
        double min = Math.min(posX - envXMin, Math.min(posY - envYMin, posZ - envZMin));
        min = Math.min(min, envXMax - posX);
        min = Math.min(min, envYMax - posY);
        min = Math.min(min, envZMax - posZ);
        return min;
    }

    //TODO: Interet de cette methode !!
    protected boolean DansAlignement(Atome a) {
        double distanceCarre = DistanceCarre(a);
        return (distanceCarre < DISTANCE_MAX_CARRE && distanceCarre > DISTANCE_MIN_CARRE);
    }

    //TODO: URGENT
    protected boolean LierAtomes(ArrayList<Atome> atomes) {
        // Recherche de l'atome le plus proche
        Atome a;
        if (!atomes.get(0).equals(this)) {
            a = atomes.get(0);
        } else {
            a = atomes.get(1);
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
            if (a.liaison != 0) {
                // System.out.println("Une liaison avec Distance Carre = "+distanceCarre);
                liaison--;
                a.liaison--;
                etat = 1;
                a.etat = 1;
                return true;
            }
        }
        return false;
    }

    protected boolean EviterAtomes(ArrayList<Atome> atomes) {
        // Recherche de l'atome le plus proche
        Atome a;
        generateur = new Random();
        if (!atomes.get(0).equals(this)) {
            a = atomes.get(0);
        } else {
            a = atomes.get(1);
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
            double diffX = (a.posX - posX) / distance;
            double diffY = (a.posY - posY) / distance;
            double diffZ = (a.posZ - posZ) / distance;
            double alea = generateur.nextDouble() * 4;
            vitesseX = vitesseX - diffX / alea;
            vitesseY = vitesseY - diffY / alea;
            vitesseZ = vitesseZ - diffZ / alea;
            //System.out.println("Atome �vit�!!");
            Normaliser();
            return true;
        }
        return false;
    }

    protected boolean EviterLimiteEnv(double envXMin, double envYMin, double envZMin, double envXMax, double envYMax, double envZMax) {
        // On s'arrete aux limites de l'environnement
        if (posX < envXMin) {
            posX = envXMin;
        } else if (posY < envYMin) {
            posY = envYMin;
        } else if (posX > envXMax) {
            posX = envXMax;
        } else if (posY > envYMax) {
            posY = envYMax;
        } else if (posZ < envZMin) {
            posZ = envZMin;
        } else if (posZ > envZMax) {
            posZ = envZMax;
        }

        // Changer de direction
        double distance = DistanceLimiteEnv(envXMin, envYMin, envZMin, envXMax, envYMax, envZMax);
        if (distance < DISTANCE_MIN) {
            if (distance == (posX - envXMin)) {
                vitesseX += 0.3;
            } else if (distance == (posY - envYMin)) {
                vitesseY += 0.3;
            } else if (distance == (envXMax - posX)) {
                vitesseX -= 0.3;
            } else if (distance == (envYMax - posY)) {
                vitesseY -= 0.3;
            } else if (distance == (envZMax - posZ)) {
                vitesseZ -= 0.3;
            } else if (distance == (posZ - envZMin)) {
                vitesseZ += 0.3;
            }
            Normaliser();
            return true;
        }
        return false;
    }

    protected boolean EviterMolecule(ArrayList<Molecule> molecules) {
        if (!molecules.isEmpty()) {
            // Recherche de la mol�cule la plus proche
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
                double diffX = (m.posX - posX) / distance;
                double diffY = (m.posY - posY) / distance;
                double diffZ = (m.posZ - posZ) / distance;
                vitesseX = vitesseX - diffX / 2;
                vitesseY = vitesseY - diffY / 2;
                vitesseZ = vitesseZ - diffZ / 2;
                Normaliser();
                return true;
            }
        }
        return false;
    }

    protected void CalculerDirectionMoyenne(ArrayList<Atome> atomes) {
        double vitesseXTotal = 0;
        double vitesseYTotal = 0;
        double vitesseZTotal = 0;
        int nbTotal = 0;
        for (Atome a : atomes) {
            if (DansAlignement(a)) {
                vitesseXTotal += a.vitesseX;
                vitesseYTotal += a.vitesseY;
                vitesseZTotal += a.vitesseZ;
                nbTotal++;
            }
        }
        if (nbTotal >= 1) {
            vitesseX = (vitesseXTotal / nbTotal + vitesseX) / 2;
            vitesseY = (vitesseYTotal / nbTotal + vitesseY) / 2;
            vitesseZ = (vitesseZTotal / nbTotal + vitesseZ) / 2;
            Normaliser();
        }
    }

    public void MiseAJour(ArrayList<Atome> atomes, ArrayList<Molecule> molecules, double largeur, double hauteur, double profondeur) {
        tempsRestant--;
        if (etat == 0) {
            if (!EviterLimiteEnv(0, 0, 0, largeur, hauteur, profondeur)) {
                if (!LierAtomes(atomes)) {
                    if (!EviterMolecule(molecules)) {
                        if (!EviterAtomes(atomes)) {
                            CalculerDirectionMoyenne(atomes);
                        }
                    }
                }
            }
            if (liaison == 0) etat = 2;
            MiseAJourPosition();
        }
        if (liaison == 0) etat = 2;
    }

    protected void updatePosition() {
        double[] pos = {posX, posY, posZ};
        sphere.setT(pos);
    }

    protected void draw(AGroup root) {
        updatePosition();
        if (!root.getChildren().contains(sphere)) {
            root.getChildren().add(sphere);
            sphere.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    AController.items.clear();

                    AController.items.add(" Atome : " + getSymb());
                    AController.items.add(" Speed : " + getSpeed());
                    AController.items.add(" Rayon : " + getRayon());


                }
            });
        }
    }

    public boolean isNotActive() {
        double threshold = pow(10, -9);

        return (
                vitesseX < threshold &&
                        vitesseY < threshold &&
                        vitesseZ < threshold
        );
    }

    public double getSpeed() {
        return PAS;
    }

    public void setSpeed(double speed) {
        PAS = speed;
    }

    public void toStringDialog(AGroup world) {
        JFXDialog dialog = new JFXDialog();

        world.getChildren().add(dialog);
    }


    public ArrayList<Covalence> getCovalence() {
        return covalence;
    }

    public void setCovalence(ArrayList<Covalence> covalence) {
        this.covalence = covalence;
    }

    public void computeCovalence() {

    }
}


    
