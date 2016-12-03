package utbm.tx52.atoms_visualiser.octree;

import javafx.geometry.Point3D;
import org.junit.*;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import utbm.tx52.atoms_visualiser.Atome;
import utbm.tx52.atoms_visualiser.Environment;

import java.sql.Time;
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
        maxObjects = 2;
        size = Math.pow(2, 20);
        initOctree();
    }

    private void initOctree() {
        octree = new Octree(size, maxObjects);
    }

    public class AtomsAdderWorker implements Runnable {
        ArrayList<Atome> atoms;
        Octree octree;

        public AtomsAdderWorker(ArrayList<Atome> atoms, Octree octree) {
            super();
            this.atoms = new ArrayList(atoms);
            this.octree = octree;
        }

        @Override
        public void run() {
            for(Atome a : atoms) {
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

        Octree t_octree = new Octree<Atome>(size, maxObjects, environment.getAtoms());
        assertEquals(environment.getAtoms(), t_octree.getObjects());
    }

    @Test
    public void getOctreeForPoint() throws OctreeSubdivisionException, PointOutsideOctreeException, Exception {
        Environment environment = genEnvironment(50, false);
        for(Atome a : environment.getAtoms()) {
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
        Atome a = environment.getAtoms().get(0);

        Octree storedIn = octree.add(a);
        assertEquals(octree, storedIn);
        assertEquals(a, octree.getObjects().get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addMoreThanMaxItems() throws OctreeSubdivisionException, Exception {
        Environment environment = genEnvironment(1000, false);
        ArrayList<Atome> env_atoms = environment.getAtoms();

        for(Atome a : env_atoms)
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
            ArrayList<Atome> env_atoms = environment.getAtoms();

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
        for(Atome a : environment.getAtoms())
            octree.add(a);
        octree.subdivide();

        Atome a = new Atome(1, 0, 0, 0, 0, false);

        a.posX = 0;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[0]));
        a.posY = size;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[2]));
        a.posZ = size;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[6]));
        a.posY = 0;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[4]));

        a.posX = size;
        a.posY = 0;
        a.posZ = 0;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[1]));
        a.posY = size;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[3]));
        a.posZ = size;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[7]));
        a.posY = 0;
        assertTrue(addThenCheckAtomIsInChild(a, octree.children[5]));
    }

    private boolean addThenCheckAtomIsInChild(Atome a, Octree child) throws OctreeSubdivisionException, Exception {
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
        Atome a = new Atome(1, 0, 0, 0, 0, false);
        octree.add(a);
        octree.remove(a);

        assertEquals(0, octree.getObjects().size());
    }

    @Test
    public void removeAndMerge() throws Exception, OctreeSubdivisionException, PointOutsideOctreeException {
        octree.add(new Atome(1, 0, 0, 0, 0, false));
        octree.add(new Atome(1, 1, 1, 0, 0, false));

        Atome a = new Atome(1, 1, 2, 0, 0, false);
        octree.add(a);
        octree.remove(a);

        assertTrue("The root cube should have re-merged all children", octree.isLeaf());
    }

    @Test
    public void removeButCannotMerge() throws Exception, OctreeSubdivisionException, PointOutsideOctreeException {
        Environment environment = genEnvironment(maxObjects + 1, false);
        for(Atome a : environment.getAtoms())
            octree.add(a);

        Atome a = new Atome(1, 0, 0, 0, 0, false);
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