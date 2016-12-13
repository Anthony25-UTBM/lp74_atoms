package utbm.tx52.atoms_visualiser.entities;

import javafx.geometry.Point3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utbm.tx52.atoms_visualiser.exceptions.NegativeSpeedException;
import utbm.tx52.atoms_visualiser.octree.Octree;
import utbm.tx52.atoms_visualiser.octree.OctreeDistanceHelper;
import utbm.tx52.atoms_visualiser.octree.PointOutsideOctreeException;
import utbm.tx52.atoms_visualiser.utils.PeriodicTable;
import utbm.tx52.atoms_visualiser.view.AGroup;

import java.util.*;


// Grille repr�sentant l'environnement + les atoms
public class Environment extends Observable {
    private static final Logger logger = LogManager.getLogger("Environment");
    public OctreeDistanceHelper octreeDistanceHelper = new OctreeDistanceHelper();
    public Octree<Atom> atoms;
    public ArrayList<Molecule> molecules;
    protected Random random_generator;
    /**
     * Environment is a cube, `size` is the size of an edge
     */
    protected double size;
    protected int maxObjects = 200;

    public Environment(double size) {

        this.size = size;
        molecules = new ArrayList();
        atoms = new Octree<>(size*2, maxObjects);


    }
    public double getSize()
    {
        return size;
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
                atoms.add(new Atom(this, number, a_coord, a_dir, isCHNO));
            } catch (Exception e) {
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

    public Octree<Atom> getAtoms() throws InterruptedException {
        return new Octree<Atom>(atoms);
    }

    public void addAtom(Atom a) throws Exception {
        atoms.add(a);

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

    public void updateAtoms(AGroup world) {
        ArrayList<Atom> atoms_objects = null;
        try {
            atoms_objects = atoms.getObjects();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        for (Atom a : atoms_objects) {
            try {
                a.MiseAJour(molecules);
            } catch (Exception e) {
                e.printStackTrace();
            }
            a.draw(world);
        }
    }

    public void updateEnv(AGroup world) {
        updateAtoms(world);
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

        atoms.forEach(a->{
            a.setSpeed(speed);
        });
    }
}
