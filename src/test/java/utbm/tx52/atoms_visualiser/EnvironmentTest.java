package utbm.tx52.atoms_visualiser;

import javafx.geometry.Point3D;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import utbm.tx52.atoms_visualiser.controllers.AController;
import utbm.tx52.atoms_visualiser.controllers.UIReactionController;
import utbm.tx52.atoms_visualiser.entities.Atom;
import utbm.tx52.atoms_visualiser.entities.Environment;
import utbm.tx52.atoms_visualiser.entities.Molecule;
import utbm.tx52.atoms_visualiser.exceptions.NegativeSpeedException;
import utbm.tx52.atoms_visualiser.octree.Octree;
import utbm.tx52.atoms_visualiser.view.AGroup;

import javax.lang.model.type.UnknownTypeException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by anthony on 15/11/16.
 */
public class EnvironmentTest {
    private Environment environment;
    private int nbMolecules;
    private int nbAtoms;
    private double size;
    private boolean isCHNO;

    @Before
    public void setUp() {
        nbMolecules = 5;
        nbAtoms = 5;
        size = 100;
        isCHNO = false;

        initEnvironment();
    }

    private void initEnvironment() {
        environment = new Environment(nbAtoms, size, isCHNO);
        generatePoolMoleculesFor(environment, nbMolecules);
    }

    private void generatePoolMoleculesFor(Environment env, int nb) {
        Random random_generator = new Random();
        double max = 1000;
        for(int i = 0; i < nb; i++) {
            double posX = random_generator.nextDouble() * max;
            double posY = random_generator.nextDouble() * max;
            double rayon = random_generator.nextDouble() * max;
            environment.addMolecule(posX, posY, rayon);
        }
    }

    @Test
    public void addMolecule() {
        Point3D coord = new Point3D(1, 2, 0);
        double radius = 3;

        environment.addMolecule(coord.getX(), coord.getY(), radius);

        Molecule lastMolecule = environment.molecules.get(environment.molecules.size() - 1);
        assertTrue(coord.equals(lastMolecule.getCoordinates()));
        assertEquals(radius, lastMolecule.getRadius(), 0.001);
    }

    @Test
    public void addAtomCHNO() throws Exception {

        Atom a = new Atom(environment, "C",Point3D.ZERO,45, true);
        environment.addAtom(a);

        Atom addedAtom = environment.atoms.getObjects().get(environment.atoms.getObjects().size() - 1);
        assertEquals(a, addedAtom);
    }

    @Test
    public void addAtomNotCHNO() throws Exception {
        Atom a = new Atom(environment, "Ar",Point3D.ZERO,45, false);
        environment.addAtom(a);

        Atom addedAtom = environment.atoms.getObjects().get(environment.atoms.getObjects().size() - 1);
        assertEquals(a, addedAtom);
    }

    @Test
    public void updateMolecules() throws Exception {
        spyAllMoleculesOf(environment);

        environment.updateMolecules();
        for(Molecule m : environment.molecules) {
            verify(m).update();
        }
    }

    @Test
    @Ignore
    public void updateAtoms() throws Exception {
        AController controller = new AController();
        UIReactionController reactionController = new UIReactionController();
        reactionController.init(controller);
        environment.controller = reactionController;

        spyAllAtomsOf(environment);

        for(Atom a : environment.atoms.getObjects()) {
            doNothing().when(a).update();
            doNothing().when(a).draw();
        }

        environment.updateAtoms();
        for(Atom a : environment.atoms.getObjects()) {
            verify(a).update();
            verify(a).draw();
        }
    }

    private void spyAllMoleculesOf(Environment env) throws Exception {
        spyAllElemsOf(env, Molecule.class);
    }

    private void spyAllAtomsOf(Environment env) throws Exception {
        spyAllElemsOf(env, Atom.class);
    }

    @SuppressWarnings("unchecked")
    private <T> void spyAllElemsOf(Environment env, Class<T> elementType) throws Exception {
        int index = 0;

        // It should be way cleaner when molecules will use an octree (or be removed)
        if(elementType == Molecule.class) {
            ArrayList<T> elemList = (ArrayList<T>) environment.molecules;
            for (T e : elemList) {
                elemList.set(index, spy(e));
                index++;
            }
        }
        else if(elementType == Atom.class) {
            ArrayList<Atom> atomObjects = environment.atoms.getObjects();

            for(Atom a : atomObjects) {
                Octree<Atom> octree = environment.atoms.getOctreeForPoint(a.getCoordinates());
                octree.remove(a);
                octree.add(spy(a));
                index++;
            }
        }
        else {
            throw new UnknownTypeException(null, null);
        }

    }

    @Test
    public void updateEnv() throws Exception {
        UIReactionController controller = new UIReactionController();
        environment = PowerMockito.spy(environment);
        environment.controller = controller;

        doNothing().when(environment).drawAtoms();
        doNothing().when(environment).updateMolecules();
        PowerMockito.doNothing().when(environment, "setChanged");
        doNothing().when(environment).notifyObservers();

        environment.updateEnv();

        verify(environment).drawAtoms();
        verify(environment).updateMolecules();
        PowerMockito.verifyPrivate(environment).invoke("setChanged");
        verify(environment).notifyObservers();
    }

    @Test
    public void nbOfEachAtoms() throws Exception {
        environment.atoms = new Octree<Atom>(size, 20);
        Atom[] atomList = new Atom[] {
            new Atom(environment, "C", true),
            new Atom(environment, "H", true),
            new Atom(environment, "H", true),
            new Atom(environment, "Ar", false),
        };

        for(Atom a : atomList)
            environment.addAtom(a);

        Map<String, Integer> expectedResult = new Hashtable<>();
        expectedResult.put("C", 1);
        expectedResult.put("H", 2);
        expectedResult.put("Ar", 1);

        assertEquals(expectedResult, environment.nbOfEachAtoms());
    }

    @Test
    public void numberOfNotActiveAtomsAllActive() throws Exception {
        activateAllAtomsOf(environment);
        assertEquals(0, environment.nbOfNotActiveAtoms());
    }

    @Test
    public void numberOfNotActiveAtomsAllDisabled() throws Exception {
        disableAllAtomsOf(environment);
        assertEquals(nbAtoms, environment.nbOfNotActiveAtoms());
    }

    public void activateAllAtomsOf(Environment e) throws InterruptedException {
        setAllAtomsSpeedDirOfEnvTo(e, 1);
    }

    public void disableAllAtomsOf(Environment e) throws InterruptedException {
        setAllAtomsSpeedDirOfEnvTo(e, 0);
    }

    public void setAllAtomsSpeedDirOfEnvTo(Environment env, double speed) throws InterruptedException {
        environment.atoms.forEach(a->{
            a.setSpeedVector(new Point3D(speed, speed, speed));
        });
    }

    @Test
    public void setAtomsSpeed() throws NegativeSpeedException, InterruptedException {
        environment.setAtomsSpeed(10);

        environment.atoms.forEach(a->{
            assertEquals(10, a.getSpeed(), 0.001);
        });
    }

    @Test(expected = NegativeSpeedException.class)
    public void setAtomsSpeedNegative() throws NegativeSpeedException {
        environment.setAtomsSpeed(-10);
    }

    @Test
    public void getSpeed() throws NegativeSpeedException {
        environment.setAtomsSpeed(10);
        assertEquals(10, environment.getSpeed(), 0.001);
    }
}