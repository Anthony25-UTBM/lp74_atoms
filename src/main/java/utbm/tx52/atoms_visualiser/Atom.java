package utbm.tx52.atoms_visualiser;


import com.jfoenix.controls.JFXDialog;
import javafx.application.ConditionalFeature;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.pow;

/*
Class atom
 */

public class Atom extends Agent {
    private static final Logger logger = LogManager.getLogger("Atom");
    public static final double DISTANCE_MIN = 10;
    public static final double DISTANCE_MIN_CARRE = 100;
    public static final double DISTANCE_MAX = 40;
    public static final double DISTANCE_MAX_CARRE = 1600;
    // Constantes
    public static double PAS = 0;
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
    protected Point3D speedVector;
    protected Random generateur;
    protected double ratioSpeed;
    private ASphere sphere;
    private ArrayList<Covalence> covalence;
    private long vanderWaalsRadius;
    private boolean isCHNO;

    public Atom(String symbole, boolean isCHNO) {
        this(Point3D.ZERO, 45, isCHNO);
        int n = 0;
        if (isCHNO) {
            CHNO t_chno = CHNO.getInstance();
            int indexOfSymbole = t_chno.getSymbole().indexOf(symbole);
            a_number = t_chno.getANumber(indexOfSymbole);
        } else {
            PeriodicTable t_periodic = PeriodicTable.getInstance();
            a_number = t_periodic.getNumber().get(t_periodic.getSymbole().indexOf(symbole));
        }
        setPropertiesFromPeriodicTable();
    }

    public Atom(int n, double coordX, double coordY, double coordZ, double dir, boolean isCHNO) {
        this(n, new Point3D(coordX, coordY, coordZ), dir, isCHNO);
    }

    public Atom(int n, Point3D coord, double dir, boolean isCHNO) {
        this(coord, dir, isCHNO);
        a_number = n;
        setPropertiesFromPeriodicTable();
    }

    protected Atom(Point3D coord, double dir, boolean isCHNO) {
        ratioSpeed = 1;
        this.isCHNO = isCHNO;

        state = ElementState.free;

        this.coord = coord;
        speedVector = new Point3D(Math.cos(dir), Math.sin(dir), 0);

        logger.debug("Atom crée (" + symb + ")");
    }

    protected void setPropertiesFromPeriodicTable() {
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
            logger.debug(" index  = " + index + " a_number " + a_number);

            symb = t_periodic.getSymbole().get(index);
            liaison = t_periodic.getLiaisons().get(index);
            rayon = t_periodic.getRayons().get(index);
            jcouleur = t_periodic.getCouleurs()[index % t_periodic.getCouleurs().length];
        }

        // Should be static, in global properties for our app so it is no checked everytime
        if(javafx.application.Platform.isSupported(ConditionalFeature.SCENE3D)) {
            double[] colors = {jcouleur.getRed(), jcouleur.getGreen(), jcouleur.getBlue()};
            sphere = new ASphere(rayon, new double[]{coord.getX(), coord.getY(), coord.getZ()}, colors);
        }
    }


    public double getRayon() {
        return rayon;
    }

    public String getSymb() {
        return symb;
    }

    public Point3D getSpeedVector() {
        return speedVector;
    }

    public void setSpeedVector(Point3D speedVector) {
        this.speedVector = speedVector;
    }

    public void start() {}

    //TODO: exploiter cette m�thode
    public int estlibre() {
        return liaison;
    }

    protected void Normaliser() {
        speedVector.normalize();
    }

    protected void MiseAJourPosition() {
        coord = coord.add(speedVector.multiply(PAS));
    }

    protected double DistanceLimiteEnv(double envXMin, double envYMin, double envZMin, double envXMax, double envYMax, double envZMax) {
        double posX = coord.getX(), posY = coord.getY(), posZ = coord.getZ();
        double min = Math.min(posX - envXMin, Math.min(posY - envYMin, posZ - envZMin));
        min = Math.min(min, envXMax - posX);
        min = Math.min(min, envYMax - posY);
        min = Math.min(min, envZMax - posZ);
        return min;
    }

    //TODO: Interet de cette methode !!
    protected boolean DansAlignement(Atom a) {
        double distanceCarre = distanceSquared(a);
        return (distanceCarre < DISTANCE_MAX_CARRE && distanceCarre > DISTANCE_MIN_CARRE);
    }

    //TODO: URGENT
    protected boolean LierAtomes(ArrayList<Atom> atoms) {
        // Recherche de l'atome le plus proche
        Atom a;
        if (!atoms.get(0).equals(this)) {
            a = atoms.get(0);
        } else {
            a = atoms.get(1);
        }
        double distanceCarre = distanceSquared(a);
        for (Atom atom : atoms) {
            if (distanceSquared(atom) < distanceCarre && !atom.equals(this)) {
                a = atom;
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

    protected boolean EviterAtomes(ArrayList<Atom> atoms) {
        // Recherche de l'atome le plus proche
        Atom a;
        generateur = new Random();
        if (!atoms.get(0).equals(this)) {
            a = atoms.get(0);
        } else {
            a = atoms.get(1);
        }
        double distanceCarre = distanceSquared(a);
        for (Atom atom : atoms) {
            if (distanceSquared(atom) < distanceCarre && !atom.equals(this)) {
                a = atom;
                distanceCarre = distanceSquared(a);
            }
        }

        // Evitement
        //if (distanceCarre < DISTANCE_MIN_CARRE) {
        if (distanceCarre < (a.rayon * a.rayon * a.rayon)) {
            double distance = Math.sqrt(distanceCarre);
            double diffX = (a.getCoordinates().getX() - coord.getX()) / distance;
            double diffY = (a.getCoordinates().getY() - coord.getY()) / distance;
            double diffZ = (a.getCoordinates().getZ() - coord.getZ()) / distance;
            double alea = generateur.nextDouble() * 4;
            speedVector = speedVector.subtract(diffX/alea, diffY/alea, diffZ/alea);
            Normaliser();
            return true;
        }
        return false;
    }

    protected boolean EviterLimiteEnv(double envXMin, double envYMin, double envZMin, double envXMax, double envYMax, double envZMax) {
        // On s'arrete aux limites de l'environnement
        double posX = coord.getX(), posY = coord.getY(), posZ = coord.getZ();
        if (coord.getX() < envXMin) {
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
                speedVector = speedVector.add(0.3, 0, 0);
            } else if (distance == (posY - envYMin)) {
                speedVector = speedVector.add(0, 0.3, 0);
            } else if (distance == (envXMax - posX)) {
                speedVector = speedVector.add(-0.3, 0, 0);
            } else if (distance == (envYMax - posY)) {
                speedVector = speedVector.add(0, -0.3, 0);
            } else if (distance == (envZMax - posZ)) {
                speedVector = speedVector.add(0, 0, -0.3);
            } else if (distance == (posZ - envZMin)) {
                speedVector = speedVector.add(0, 0, 0.3);
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
                double diffX = (m.getCoordinates().getX() - coord.getX()) / distance;
                double diffY = (m.getCoordinates().getY() - coord.getY()) / distance;
                double diffZ = (m.getCoordinates().getZ() - coord.getZ()) / distance;

                speedVector = speedVector.subtract(diffX/2, diffY/2, diffZ/2);
                Normaliser();
                return true;
            }
        }
        return false;
    }

    protected void CalculerDirectionMoyenne(ArrayList<Atom> atoms) {
        Point3D totalSpeedVector = Point3D.ZERO;
        int nbTotal = 0;
        for (Atom a : atoms) {
            if (DansAlignement(a)) {
                totalSpeedVector = totalSpeedVector.add(a.getSpeedVector());
                nbTotal++;
            }
        }
        if (nbTotal >= 1) {
            speedVector = totalSpeedVector.
                multiply(1/(2 * nbTotal)).
                add(speedVector.multiply(1/2));
            Normaliser();
        }
    }

    public void MiseAJour(ArrayList<Atom> atoms, ArrayList<Molecule> molecules, double largeur, double hauteur, double profondeur) {
        if (state == ElementState.free) {
            if (!EviterLimiteEnv(0, 0, 0, largeur, hauteur, profondeur)) {
                if (!LierAtomes(atoms)) {
                    if (!EviterMolecule(molecules)) {
                        if (!EviterAtomes(atoms)) {
                            CalculerDirectionMoyenne(atoms);
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
        sphere.setT(new double[]{coord.getX(), coord.getY(), coord.getZ()});
    }

    protected void draw(AGroup root) {
        updatePosition();
        if (!root.getChildren().contains(sphere)) {
            root.getChildren().add(sphere);
            sphere.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    AController.items.clear();

                    AController.items.add(" Atom : " + getSymb());
                    AController.items.add(" Speed : " + getSpeed());
                    AController.items.add(" Rayon : " + getRayon());
                }
            });
        }
    }

    public boolean isNotActive() {
        double threshold = pow(10, -9);

        return (
            speedVector.getX() < threshold &&
            speedVector.getY() < threshold &&
            speedVector.getZ() < threshold
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


    
