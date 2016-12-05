package utbm.tx52.atoms_visualiser;

import javafx.geometry.Point3D;
import org.junit.*;
import org.powermock.api.mockito.PowerMockito;

import javax.lang.model.type.UnknownTypeException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by anthony on 15/11/16.
 */
public class EnvironmentTest {
    private Environment environment;
    private int nbMolecules;
    private int nbAtoms;
    private double height;
    private double width;
    private double depth;
    private boolean isCHNO;

    @Before
    public void setUp() {
        nbMolecules = 5;
        nbAtoms = 5;
        width = 100;
        depth = 100;
        height = 100;
        isCHNO = false;

        initEnvironment();
    }

    private void initEnvironment() {
        environment = new Environment(nbAtoms, width, depth, height, isCHNO);
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
    public void addAtomCHNO() {
        Atom a = new Atom("C", true);
        environment.addAtom(a);

        Atom addedAtom = environment.atoms.get(environment.atoms.size() - 1);
        assertEquals(a, addedAtom);
    }

    @Test
    public void addAtomNotCHNO() {
        Atom a = new Atom("Ar", false);
        environment.addAtom(a);

        Atom addedAtom = environment.atoms.get(environment.atoms.size() - 1);
        assertEquals(a, addedAtom);
    }

    @Test
    public void updateMolecules() {
        spyAllMoleculesOf(environment);

        environment.updateMolecules();
        for(Molecule m : environment.molecules) {
            verify(m).update();
        }
    }

    @Test
    public void updateAtoms() {
        spyAllAtomsOf(environment);

        AGroup world = new AGroup();
        for(Atom a : environment.atoms) {
            doNothing().when(a).MiseAJour(
                any(), any(), anyDouble(), anyDouble(), anyDouble()
            );
            doNothing().when(a).draw(any());
        }

        environment.updateAtoms(world);
        for(Atom a : environment.atoms) {
            verify(a).MiseAJour(any(), any(), anyDouble(), anyDouble(), anyDouble());
            verify(a).draw(any());
        }
    }

    private void spyAllMoleculesOf(Environment env) {
        spyAllElemsOf(env, Molecule.class);
    }

    private void spyAllAtomsOf(Environment env) {
        spyAllElemsOf(env, Atom.class);
    }

    @SuppressWarnings("unchecked")
    private <T> void spyAllElemsOf(Environment env, Class<T> elementType) throws UnknownTypeException {
        int index = 0;

        ArrayList<T> elemList;
        if(elementType == Molecule.class)
            elemList = (ArrayList<T>) environment.molecules;
        else if(elementType == Atom.class)
            elemList = (ArrayList<T>) environment.atoms;
        else {
            throw new UnknownTypeException(null, null);
        }

        for(T e : elemList) {
            elemList.set(index, spy(e));
            index++;
        }
    }

    @Test
    public void updateEnv() throws Exception {
        environment = PowerMockito.spy(environment);

        doNothing().when(environment).updateAtoms(any());
        doNothing().when(environment).updateMolecules();
        PowerMockito.doNothing().when(environment, "setChanged");
        doNothing().when(environment).notifyObservers();

        AGroup world = new AGroup();
        environment.updateEnv(world);

        verify(environment).updateAtoms(any());
        verify(environment).updateMolecules();
        PowerMockito.verifyPrivate(environment).invoke("setChanged");
        verify(environment).notifyObservers();
    }

    @Test
    public void nbOfEachAtoms() {
        environment.atoms = new ArrayList<>();
        Atom[] atomList = new Atom[] {
            new Atom("C", true),
            new Atom("H", true),
            new Atom("H", true),
            new Atom("Ar", false),
        };
        environment.atoms.addAll(Arrays.asList(atomList));

        Map<String, Integer> expectedResult = new Hashtable<>();
        expectedResult.put("C", 1);
        expectedResult.put("H", 2);
        expectedResult.put("Ar", 1);

        assertEquals(expectedResult, environment.nbOfEachAtoms());
    }

    @Test
    public void numberOfNotActiveAtomsAllActive() throws NegativeSpeedException {
        activateAllAtomsOf(environment);
        assertEquals(0, environment.nbOfNotActiveAtoms());
    }

    @Test
    public void numberOfNotActiveAtomsAllDisabled() throws NegativeSpeedException {
        disableAllAtomsOf(environment);
        assertEquals(nbAtoms, environment.nbOfNotActiveAtoms());
    }

    public void activateAllAtomsOf(Environment e) {
        setAllAtomsSpeedDirOfEnvTo(e, 1);
    }

    public void disableAllAtomsOf(Environment e) {
        setAllAtomsSpeedDirOfEnvTo(e, 0);
    }

    public void setAllAtomsSpeedDirOfEnvTo(Environment env, double speed) {
        environment.atoms.forEach(a->{
            a.setSpeedVector(new Point3D(speed, speed, speed));
        });
    }

    @Test
    public void setAtomsSpeed() throws NegativeSpeedException {
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