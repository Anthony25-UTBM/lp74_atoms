package utbm.tx52.atoms_visualiser.octree;

import javafx.geometry.Point3D;
import org.junit.*;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import utbm.tx52.atoms_visualiser.Atom;
import utbm.tx52.atoms_visualiser.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class OctreeTest {
    private Octree octree;
    private int maxObjects;
    private double size;

    // Tests will timeout and break if they wait for too long
    @Rule
    public TestRule timeout = new DisableOnDebug(Timeout.seconds(20));

    @Before
    public void setUp() {
        maxObjects = 100;
        size = Math.pow(2, 20);
        initOctree();
    }

    private void initOctree() {
        octree = new Octree(size, maxObjects);
    }

    public class AtomsAdderWorker implements Runnable {
        ArrayList<Atom> atoms;
        Octree octree;

        public AtomsAdderWorker(ArrayList<Atom> atoms, Octree octree) {
            super();
            this.atoms = new ArrayList(atoms);
            this.octree = octree;
        }

        @Override
        public void run() {
            for(Atom a : atoms) {
                try {
                    octree.add(a);
                } catch (OctreeSubdivisionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ArrayList octree_atoms = null;
            try {
                octree_atoms = octree.getObjects();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(atoms.containsAll(octree_atoms) && octree_atoms.containsAll(atoms));
        }
    }

    @Test
    public void setMaxObjects() throws OctreeSubdivisionException, Exception {
        final int NEW_MAX_OBJECTS = 10;

        octree.subdivide();
        octree.setMaxObjects(NEW_MAX_OBJECTS);
        for(Octree child : octree.children)
            assertEquals(NEW_MAX_OBJECTS, child.getMaxObjects());
    }

    @Test
    public void getObjects() throws Exception {
        Environment environment = genEnvironment(2, false);

        Octree t_octree = new Octree<Atom>(size, maxObjects, environment.getAtoms());
        assertEquals(environment.getAtoms(), t_octree.getObjects());
    }

    @Test
    public void getOctreeForPoint() throws OctreeSubdivisionException, PointOutsideOctreeException, Exception {
        Environment environment = genEnvironment(50, false);
        for(Atom a : environment.getAtoms()) {
            Octree storedIn = octree.add(a);
            assertEquals(storedIn, octree.getOctreeForPoint(a.getCoordinates()));
        }
    }

    @Test(expected = PointOutsideOctreeException.class)
    public void getOctreeForPointOutsideOctree() throws OctreeSubdivisionException, PointOutsideOctreeException, Exception {
        octree.getOctreeForPoint(new Point3D(size, size, size));
    }

    @Test
    public void add() throws OctreeSubdivisionException, Exception {
        Environment environment = genEnvironment(1, false);
        Atom a = environment.getAtoms().get(0);

        Octree storedIn = octree.add(a);
        assertEquals(octree, storedIn);
        assertEquals(a, octree.getObjects().get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addMoreThanMaxItems() throws OctreeSubdivisionException, Exception {
        Environment environment = genEnvironment(1000, false);
        ArrayList<Atom> env_atoms = environment.getAtoms();

        for(Atom a : env_atoms)
            octree.add(a);
        ArrayList octree_atoms = octree.getObjects();

        assertTrue(env_atoms.containsAll(octree_atoms) && octree_atoms.containsAll(env_atoms));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addWithThreading() throws OctreeSubdivisionException, Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Callable<Object>> calls = new ArrayList<Callable<Object>>();
        for(int i = 0; i < 10; i++) {
            Environment environment = genEnvironment(1000, false);
            ArrayList<Atom> env_atoms = environment.getAtoms();

            Runnable worker = new AtomsAdderWorker(env_atoms, octree);
            calls.add(Executors.callable(worker));
        }
        executor.invokeAll(calls);
        assertEquals(10000, octree.getObjects().size());
    }

    /**
     * Check that an object goes into the correct child
     * @throws OctreeSubdivisionException
     */
    @Test
    public void addCheckIfPlacedInCorrectChild() throws OctreeSubdivisionException, Exception {
        Environment environment = genEnvironment(maxObjects, false);
        for(Atom a : environment.getAtoms())
            octree.add(a);
        octree.subdivide();

        Atom a = new Atom(1, 0, 0, 0, 0, false);

        a.setCoordinates(0, 0, 0);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[0]));
        a.setCoordinates(0, size, 0);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[2]));
        a.setCoordinates(0, size, size);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[6]));
        a.setCoordinates(0, 0, size);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[4]));

        a.setCoordinates(size, 0, 0);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[1]));
        a.setCoordinates(size, size, 0);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[3]));
        a.setCoordinates(size, size, size);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[7]));
        a.setCoordinates(size, 0, size);
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[5]));
    }

    private boolean addThenCheckAtomIsInChild(Atom a, Octree child) throws OctreeSubdivisionException, Exception {
        octree.add(a);
        return child.getObjects().contains(a);
    }

    private Environment genEnvironment(int nbAtoms, boolean isCHNO) {
        return new Environment(nbAtoms, size, size, size, isCHNO);
    }

    @Test
    public void subdivide() throws Exception, OctreeSubdivisionException {
        assertEquals(0, octree.children.length);
        octree.subdivide();
        assertEquals(8, octree.children.length);
    }

    @Test(expected = OctreeSubdivisionException.class)
    public void subdivideWhenAlreadyParent() throws Exception, OctreeSubdivisionException {
        octree.subdivide();
        // octree is parent now, subdivision should not work again
        octree.subdivide();
    }

    @Test
    public void remove() throws Exception, OctreeSubdivisionException, PointOutsideOctreeException {
        Atom a = new Atom(1, 0, 0, 0, 0, false);
        octree.add(a);
        octree.remove(a);

        assertEquals(0, octree.getObjects().size());
    }

    @Test
    public void removeAndMerge() throws Exception, OctreeSubdivisionException, PointOutsideOctreeException {
        octree.add(new Atom(1, 0, 0, 0, 0, false));
        octree.add(new Atom(1, 1, 1, 0, 0, false));

        Atom a = new Atom(1, 1, 2, 0, 0, false);
        octree.add(a);
        octree.remove(a);

        assertTrue("The root cube should have re-merged all children", octree.isLeaf());
    }

    @Test
    public void removeButCannotMerge() throws Exception, OctreeSubdivisionException, PointOutsideOctreeException {
        Environment environment = genEnvironment(maxObjects + 1, false);
        for(Atom a : environment.getAtoms())
            octree.add(a);

        Atom a = new Atom(1, 0, 0, 0, 0, false);
        octree.add(a);
        octree.remove(a);

        assertTrue(
            "The root cube cannot remerge all children as one also has children",
            octree.children.length > 0
        );
    }

    @Test
    public void isRoot() throws Exception {
        assertTrue(octree.isRoot());
    }

    @Test
    public void isParent() throws Exception {
        assertFalse(octree.isParent());
    }

    @Test
    public void isLeaf() throws Exception {
        assertTrue(octree.isLeaf());
    }
}