package utbm.tx52.atoms_visualiser.entities;

import jade.wrapper.*;
import javafx.geometry.Point3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utbm.tx52.atoms_visualiser.controllers.IController;
import utbm.tx52.atoms_visualiser.exceptions.NegativeSpeedException;
import utbm.tx52.atoms_visualiser.octree.Octree;
import utbm.tx52.atoms_visualiser.octree.OctreeDistanceHelper;
import utbm.tx52.atoms_visualiser.octree.PointOutsideOctreeException;
import utbm.tx52.atoms_visualiser.utils.PeriodicTable;
import utbm.tx52.atoms_visualiser.utils.RandomHelper;
import utbm.tx52.atoms_visualiser.view.AGroup;

import java.util.*;


public class Environment extends Observable {
    private static final Logger logger = LogManager.getLogger("Environment");
    public IController controller;
    public OctreeDistanceHelper octreeDistanceHelper = new OctreeDistanceHelper();
    public Octree<Atom> atoms;
    public ArrayList<Molecule> molecules;
    protected Random random_generator;
    /**
     * Environment is a cube, `size` is the size of an edge
     */
    protected double size;
    protected int maxObjects = 200;
    protected AgentContainer container = null;

    public Environment() {
    }

    public Environment(double size) {
        this.size = size;
        molecules = new ArrayList();
        atoms = new Octree<>(size*2, maxObjects);
    }

    public Environment(int nbAtoms, double size, boolean isCHNO) {
        this(size);
        random_generator = new Random();
        int nbSamples;

        PeriodicTable t_periodic = PeriodicTable.getInstance();
        if (isCHNO) {
            nbSamples = 4;
        } else {
            nbSamples = t_periodic.getSymbole().size();
            logger.debug("Number of samples " + nbSamples);
            if (nbSamples == 0) return;
        }

        for (int i = 0; i < nbAtoms; i++) {
            int number = random_generator.nextInt(nbSamples - 1);
            if (isCHNO)
                number = CHNO.getInstance().getANumber(number);
            else
                number = t_periodic.getNumber().get(number);

            Point3D a_coord = new Point3D(
                random_generator.nextDouble() * (this.size/2 - 1),
                random_generator.nextDouble() * (this.size/2 - 1),
                random_generator.nextDouble() * (this.size/2 - 1)
            );
            double a_dir = random_generator.nextDouble() * 2 * Math.PI;
            try {
                Atom a = new Atom(this, number, a_coord, a_dir, isCHNO);
                addAtom(a);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Environment(IController controller, AgentContainer container, int nbAtoms, double size, boolean isCHNO) {
        this(nbAtoms, size, isCHNO);
        this.controller = controller;
        try {
            setContainer(container);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public void setContainer(AgentContainer container) throws StaleProxyException {
        // TODO: stop all running agents
        if(this.container != null) {
            this.container.kill();
            copyAtomAgentsToContainer(container);
        }

        this.container = container;
    }

    protected void copyAtomAgentsToContainer(AgentContainer targetContainer) {
        for(Atom a : atoms) {
            try {
                targetContainer.acceptNewAgent(a.id, a);
            } catch (StaleProxyException e) {
                logger.error("Error when adding Atom " + a.id);
                e.printStackTrace();
            }
        }
    }

    public void addMolecule(double coordX, double coordY, double rayon) {
        addMolecule(new Point3D(coordX, coordY, 0), rayon);
    }

    public void addMolecule(Point3D coord, double rayon) {
        molecules.add(new Molecule(coord, rayon));
        if (!molecules.isEmpty()) {
            logger.debug("Molecule Yes");
        } else logger.debug("Molecule NO");
    }

    public double getSize() {
        return size;
    }

    public Octree<Atom> getAtoms() throws InterruptedException {
        return new Octree<Atom>(atoms);
    }

    public void addAtom(Atom a) throws Exception {
        /* TODO:
            * start the atom if the container is running
            * search if the atom is not already in the tree (to avoid duplicates)
         */
        atoms.add(a);
        if(container != null)
            this.container.acceptNewAgent(a.id, a);
    }

    public void start() throws ControllerException {
        for(Atom a : atoms)
            container.getAgent(a.id).start();
        container.start();
    }

    public void stop() throws ControllerException {
        for(Atom a : atoms)
            container.getAgent(a.id).kill();
        container.kill();
    }

    public void move(Atom a, Point3D dest) throws Exception {
        Octree a_octree = atoms.getOctreeForPoint(a.getCoordinates());

        if(a_octree.isPointInOctree(dest))
            a.setCoordinates(dest);
        else {
            Point3D oldCoord = a.getCoordinates();
            try {
                a_octree.remove(a);
                a.setCoordinates(dest);
                atoms.add(a);
            } catch(PointOutsideOctreeException e) {
                logger.debug(e);
                a.setCoordinates(oldCoord);
                atoms.add(a);
            }
        }
    }

    public void updateMolecules() {
        molecules.forEach(Molecule::update);
    }

    public void updateAtoms() {
        /* Using a different list avoids ConcurrentModificationException, because a.update() will maybe change
           the structure of our tree */
        ArrayList<Atom> atomObjects;
        try {
            atomObjects = atoms.getObjects();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        for (Atom a : atomObjects) {
            try {
                a.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
            a.draw(controller.getSubScene().getWorld());
        }
    }

    public void updateEnv() {
        updateAtoms();
        updateMolecules();
        setChanged();
        notifyObservers();
    }

    public Map<String, Integer> nbOfEachAtoms() {
        Map<String, Integer> atom_groups = new Hashtable<String, Integer>();
        for (Atom a : atoms) {
            int current_nb = atom_groups.containsKey(a.getSymb()) ? atom_groups.get(a.getSymb()) : 0;
            atom_groups.put(a.getSymb(), current_nb + 1);
        }

        return atom_groups;
    }

    public int nbOfNotActiveAtoms() {
        int not_active_atoms = 0;
        for (Atom a : atoms) {
            if (a.isNotActive())
                not_active_atoms++;
        }

        return not_active_atoms;
    }

    public double getSpeed() {
        Iterator atomsIterator = atoms.iterator();
        if(atomsIterator.hasNext())
            return ((Atom) atomsIterator.next()).getSpeed();
        else
            return 0;
    }

    public void setAtomsSpeed(int speed) throws NegativeSpeedException {
        if (speed < 0) {
            throw new NegativeSpeedException("Speed should be positive or null");
        }

        atoms.forEach(a -> {
            a.setSpeed(speed);
        });
    }
}
