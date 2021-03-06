package utbm.tx52.atoms_visualiser.entities;


import com.jfoenix.controls.JFXDialog;
import com.sun.jndi.toolkit.ctx.Continuation;
import jade.core.behaviours.CyclicBehaviour;
import com.google.common.collect.Iterators;
import com.jfoenix.controls.JFXDialog;
import jade.core.behaviours.TickerBehaviour;
import javafx.application.ConditionalFeature;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utbm.tx52.atoms_visualiser.controllers.AController;
import utbm.tx52.atoms_visualiser.octree.*;
import utbm.tx52.atoms_visualiser.utils.ElementState;
import utbm.tx52.atoms_visualiser.utils.PeriodicTable;
import utbm.tx52.atoms_visualiser.view.AGroup;
import utbm.tx52.atoms_visualiser.view.ASphere;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.pow;

/*
Class atom
 */

public class Atom extends Agent implements OctreePoint {
    private static final Logger logger = LogManager.getLogger("Atom");
    public static final double DISTANCE_MIN = 10;
    public static final double DISTANCE_MIN_CARRE = 100;
    public static final double DISTANCE_MAX = 40;
    public static final double DISTANCE_MAX_CARRE = 1600;
    // Constantes
    public static double PAS = 0;
    public static double PLANCK_CONSTANT = 6.62607 * pow(10, -34);// en J/s
    public Environment environment;
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

    public Atom() {
        super();
        System.out.println("With no argument");
    }

    public Atom(Environment environment, String symbole, boolean isCHNO) {
        this(environment,symbole, Point3D.ZERO,45, isCHNO);
    }

    public Atom(Environment environment, String symbole,Point3D point, double dir, boolean isCHNO) {
        this(environment, point,dir, isCHNO);
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

    public Atom(Environment environment, int n, double coordX, double coordY, double coordZ, double dir, boolean isCHNO) {
        this(environment, n, new Point3D(coordX, coordY, coordZ), dir, isCHNO);
    }

    public Atom(Environment environment, int n, Point3D coord, double dir, boolean isCHNO) {
        this(environment, coord, dir, isCHNO);
        a_number = n;
        setPropertiesFromPeriodicTable();
    }

    protected Atom(Environment environment, Point3D coord, double dir, boolean isCHNO) {
        super();
        this.environment = environment;
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

    //TODO: exploiter cette m�thode
    public int estlibre() {
        return liaison;
    }

    protected void normalize() {
        speedVector.normalize();
    }

    public void move(Point3D dest) throws Exception {
        environment.move(this, dest);
    }

    protected void updateCoordinates() {
        try {
            move(coord.add(speedVector.multiply(PAS)));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    protected double DistanceLimiteEnv() {
        double posX = coord.getX(), posY = coord.getY(), posZ = coord.getZ();
        double minDistanceFromMinCoord = Math.abs(Math.min(posX, Math.min(posY, posZ)) - environment.size/2);
        double minDistanceFromMaxCoord = Math.abs(environment.size/2 - Math.max(posX, Math.max(posY, posZ)));

        return Math.min(minDistanceFromMinCoord, minDistanceFromMaxCoord);
    }

    //TODO: URGENT
    protected boolean attachAtoms() throws Exception {
        Atom a;
        try {
            a = (Atom) environment.octreeDistanceHelper.getFarthestNeighbours(environment.atoms, this).get(0);
        } catch (OctreeNoNeighbourFoundException e) {
            // not a big deal, tree has probably changed, and it will be changed at the next frame
            return false;
        }

        double distanceSquaredToA = distanceSquared(a);
        if (distanceSquaredToA < (a.rayon * a.rayon * 4) && a_number != a.a_number) {
            if (a.liaison != 0) {
                liaison--;
                a.liaison--;
                state = ElementState.partially_attached;
                a.state = ElementState.partially_attached;
                return true;
            }
            if (this.isCHNO && liaison == 0) {
                //TODO FIX UNIT !
                if (a.getSpeed() - getSpeed() < 0) changeDirection(1);
                double deltaEnergy = PLANCK_CONSTANT * Math.abs(a.getSpeed() - getSpeed());
                double A = 4 * deltaEnergy * Math.pow(vanderWaalsRadius, 12);
                double B = 4 * deltaEnergy * Math.pow(vanderWaalsRadius, 6);
                double V = (A / Math.pow(getRayon(), 12)) * (B / Math.pow(getRayon(), 6));
                this.setSpeed(V);
            }
        }
        return false;
    }

    protected boolean EviterAtomes() throws Exception {
        Atom a;
        try {
            a = (Atom) environment.octreeDistanceHelper.getFarthestNeighbours(environment.atoms, this).get(0);
        } catch (OctreeNoNeighbourFoundException e) {
            // not a big deal, tree has probably changed, and it will be changed at the next frame
            return false;
        }

        // Evitement
        double distanceSquaredToA = distanceSquared(a);
        if (distanceSquaredToA < (a.rayon * a.rayon * a.rayon)) {
            double distance = Math.sqrt(distanceSquaredToA);
            double diffX = (a.getCoordinates().getX() - coord.getX()) / distance;
            double diffY = (a.getCoordinates().getY() - coord.getY()) / distance;
            double diffZ = (a.getCoordinates().getZ() - coord.getZ()) / distance;
            generateur = new Random();
            double alea = generateur.nextDouble() * 4;
            speedVector = speedVector.subtract(diffX/alea, diffY/alea, diffZ/alea);
            normalize();
            return true;
        }
        return false;
    }

    protected boolean EviterLimiteEnv() {
        // On s'arrete aux limites de l'environnement
        coord = new Point3D(
            Math.max(-environment.size/2, Math.min(coord.getX(), environment.size/2)),
            Math.max(-environment.size/2, Math.min(coord.getY(), environment.size/2)),
            Math.max(-environment.size/2, Math.min(coord.getZ(), environment.size/2))
        );

        double distance = DistanceLimiteEnv();
        boolean isNearEnvEdge = (distance <= DISTANCE_MIN);
        if(isNearEnvEdge)
            changeDirection(0.3);

        return isNearEnvEdge;
    }

    protected void changeDirection(double ratio) {
        // Changer de direction
        double addSpeedX = 0, addSpeedY = 0, addSpeedZ = 0;
        if(Math.abs(environment.size/2 - coord.getX()) < DISTANCE_MIN)
            addSpeedX = ratio * ((environment.size/2 - coord.getX() < DISTANCE_MIN) ? -1 : 1);
        if(Math.abs(environment.size/2 - coord.getY()) < DISTANCE_MIN)
            addSpeedY = ratio * ((environment.size/2 - coord.getY() < DISTANCE_MIN) ? -1 : 1);
        if(Math.abs(environment.size/2 - coord.getZ()) < DISTANCE_MIN)
            addSpeedZ = ratio * ((environment.size/2 - coord.getZ() < DISTANCE_MIN) ? -1 : 1);

        speedVector = speedVector.add(addSpeedX, addSpeedY, addSpeedZ);
        normalize();
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    protected void setup() {
        super.setup();
        System.out.println("init atom");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                try {
                    update();
                    TimeUnit.MILLISECONDS.sleep(35);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void update() throws Exception {
        if (state == ElementState.free) {
            if (!EviterLimiteEnv()) {
                if (!attachAtoms()) {
                    if (!EviterAtomes()) {
                        //CalculerDirectionMoyenne();
                    }
                }
            }
            if (liaison == 0) state = ElementState.attached;
            updateCoordinates();
        }

        if (liaison == 0) state = ElementState.attached;
    }

    public void updatePosition() {
        sphere.setT(new double[]{coord.getX(), coord.getY(), coord.getZ()});
    }

    public void draw() {
        AGroup root = environment.controller.getSubScene().getWorld();
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


    
