package utbm.tx52.atoms_visualiser;


import com.jfoenix.controls.JFXDialog;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

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
    public static double PLANCK_CONSTANT = 6.62607 * pow(10, -34);// en J/s
    // nouvelle architecture proposé pr aymen
    // Attributs de l'atome
    protected int a_number;
    protected String symb;
    protected ElementState state;
    protected int liaison;
    protected double rayon;
    // protected Color couleur;
    protected Color jcouleur;
    //attributs simulation
    protected double speedX;
    protected double speedY;
    protected double speedZ;
    protected Random generateur;
    protected double ratioSpeed;
    private ASphere sphere;
    private ArrayList<Covalence> covalence;
    private long vanderWaalsRadius;
    private boolean isCHNO;

    public Atome(String symbole, boolean isCHNO) {
        ratioSpeed = 1;
        this.isCHNO = isCHNO;
        if (isCHNO) {

            CHNO t_chno = CHNO.getInstance();
            int indexOfSymbole = t_chno.getSymbole().indexOf(symbole);
            a_number = t_chno.getANumber(indexOfSymbole);
            symb = t_chno.getLimitedAtomsSymbole()[indexOfSymbole];
            liaison = t_chno.getLimitedAtomsLiaison()[indexOfSymbole];
            rayon = t_chno.getLimitedAtomsRayon()[indexOfSymbole];
            jcouleur = t_chno.getLimitedAtomsColor()[indexOfSymbole];
            vanderWaalsRadius = t_chno.getVanderWaalsRadius()[indexOfSymbole];


        } else {
            PeriodicTable t_periodic = PeriodicTable.getInstance();
            a_number = t_periodic.getNumber().get(t_periodic.getSymbole().indexOf(symbole));
            int index = t_periodic.getNumber().indexOf(a_number);
            symb = t_periodic.getSymbole().get(index);
            liaison = t_periodic.getLiaisons().get(index);
            rayon = t_periodic.getRayons().get(index);
            jcouleur = t_periodic.getCouleurs()[index % t_periodic.getCouleurs().length];
        }
        state = ElementState.free;
        //TODO RANDOM !
        posX = 0;
        posY = 0;
        posZ = 0;
        speedX = Math.cos(45);
        speedY = Math.sin(45);
        speedZ = 0;
        double[] pos = {posX, posY, posZ};
        double[] colors = {jcouleur.getRed(), jcouleur.getGreen(), jcouleur.getBlue()};
        sphere = new ASphere(rayon, pos, colors);


        System.out.println("Atome crée" +
                " (" + symb + ")");
    }

    public Atome(int _n, double _x, double _y, double _z, double _dir, boolean isCHNO) {
        ratioSpeed = 1;
        a_number = _n;
        this.isCHNO = isCHNO;
        if (isCHNO) {
            CHNO t_chno = CHNO.getInstance();
            int nb = t_chno.getNumber(a_number);
            symb = t_chno.getLimitedAtomsSymbole()[nb];
            liaison = t_chno.getLimitedAtomsLiaison()[nb];
            rayon = t_chno.getLimitedAtomsRayon()[nb];
            jcouleur = t_chno.getLimitedAtomsColor()[nb];
            vanderWaalsRadius = t_chno.getVanderWaalsRadius()[nb];
        } else {
            PeriodicTable t_periodic = PeriodicTable.getInstance();
            int index = t_periodic.getNumber().indexOf(a_number);
            System.out.println(" index  = " + index + " a_number " + a_number);
            symb = t_periodic.getSymbole().get(index);
            liaison = t_periodic.getLiaisons().get(index);
            rayon = t_periodic.getRayons().get(index);
            jcouleur = t_periodic.getCouleurs()[index % t_periodic.getCouleurs().length];
        }
        state = ElementState.free;

        posX = _x;
        posY = _y;
        posZ = _z;
        speedX = Math.cos(_dir);
        speedY = Math.sin(_dir);
        speedZ = 0;
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

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getSpeedZ() {
        return speedZ;
    }

    public void start() {}

    //TODO: exploiter cette m�thode
    public int estlibre() {
        return liaison;
    }

    protected void Normaliser() {
        double longueur = Math.sqrt(speedX * speedX + speedY * speedY + speedZ * speedZ);
        speedX /= longueur;
        speedY /= longueur;
        speedZ /= longueur;
    }

    protected void MiseAJourPosition() {
        posX += PAS * speedX;
        posY += PAS * speedY;
        posZ += PAS * speedZ;
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
        double distanceCarre = distanceSquared(a);
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
        double distanceCarre = distanceSquared(a);
        for (Atome atome : atomes) {
            if (distanceSquared(atome) < distanceCarre && !atome.equals(this)) {
                a = atome;
                distanceCarre = distanceSquared(a);
            }
        }

        // Liaison possible ?
        //if (distanceCarre < DISTANCE_MIN_CARRE) {
        if (distanceCarre < (a.rayon * a.rayon * 4) && a_number != a.a_number) {
            /*double distance = Math.sqrt(distanceCarre);
            double diffX = (a.posX - posX) / distance;
            double diffY = (a.posY - posY) / distance;
            speedX = speedX - diffX / 2;
            speedY = speedY - diffY / 2;
            Normaliser();*/
            if (a.liaison != 0) {
                // System.out.println("Une liaison avec distance Carre = "+distanceCarre);
                liaison--;
                a.liaison--;
                state = ElementState.partially_attached;
                a.state = ElementState.partially_attached;
                return true;
            }
            if (this.isCHNO && liaison == 0) {
                //TODO FIX UNIT !
                double deltaEnergy = this.PLANCK_CONSTANT * Math.abs(a.getSpeed() - getSpeed());
                double A = 4 * deltaEnergy * Math.pow(vanderWaalsRadius, 12);
                double B = 4 * deltaEnergy * Math.pow(vanderWaalsRadius, 6);
                double V = (A / Math.pow(getRayon(), 12)) * (B / Math.pow(getRayon(), 6));
                this.setSpeed(V);
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
        double distanceCarre = distanceSquared(a);
        for (Atome atome : atomes) {
            if (distanceSquared(atome) < distanceCarre && !atome.equals(this)) {
                a = atome;
                distanceCarre = distanceSquared(a);
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
            speedX = speedX - diffX / alea;
            speedY = speedY - diffY / alea;
            speedZ = speedZ - diffZ / alea;
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
                speedX += 0.3;
            } else if (distance == (posY - envYMin)) {
                speedY += 0.3;
            } else if (distance == (envXMax - posX)) {
                speedX -= 0.3;
            } else if (distance == (envYMax - posY)) {
                speedY -= 0.3;
            } else if (distance == (envZMax - posZ)) {
                speedZ -= 0.3;
            } else if (distance == (posZ - envZMin)) {
                speedZ += 0.3;
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
            double distanceCarre = distanceSquared(m);
            for (Molecule m_2 : molecules) {
                if (distanceSquared(m) < distanceCarre) {
                    m = m_2;
                    distanceCarre = distanceSquared(m_2);
                }
            }

            if (distanceCarre < (m.radius * m.radius * 4)) {
                // Si collision, calcul du vecteur diff
                double distance = Math.sqrt(distanceCarre);
                double diffX = (m.posX - posX) / distance;
                double diffY = (m.posY - posY) / distance;
                double diffZ = (m.posZ - posZ) / distance;
                speedX = speedX - diffX / 2;
                speedY = speedY - diffY / 2;
                speedZ = speedZ - diffZ / 2;
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
                vitesseXTotal += a.speedX;
                vitesseYTotal += a.speedY;
                vitesseZTotal += a.speedZ;
                nbTotal++;
            }
        }
        if (nbTotal >= 1) {
            speedX = (vitesseXTotal / nbTotal + speedX) / 2;
            speedY = (vitesseYTotal / nbTotal + speedY) / 2;
            speedZ = (vitesseZTotal / nbTotal + speedZ) / 2;
            Normaliser();
        }
    }

    public void MiseAJour(ArrayList<Atome> atomes, ArrayList<Molecule> molecules, double largeur, double hauteur, double profondeur) {
        lifetime--;
        if (state == ElementState.free) {
            if (!EviterLimiteEnv(0, 0, 0, largeur, hauteur, profondeur)) {
                if (!LierAtomes(atomes)) {
                    if (!EviterMolecule(molecules)) {
                        if (!EviterAtomes(atomes)) {
                            CalculerDirectionMoyenne(atomes);
                        }
                    }
                }
            }
            if (liaison == 0) state = ElementState.attached;
            MiseAJourPosition();
        }

        if (liaison == 0) state = ElementState.attached;
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
                speedX < threshold &&
                        speedY < threshold &&
                        speedZ < threshold
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

    public long getVanderWaalsRadius() {
        return vanderWaalsRadius;
    }

    public void setVanderWaalsRadius(short vanderWaalsRadius) {
        this.vanderWaalsRadius = vanderWaalsRadius;
    }
}


    
