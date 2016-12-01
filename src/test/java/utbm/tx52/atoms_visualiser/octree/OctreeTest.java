package utbm.tx52.atoms_visualiser.octree;

import javafx.geometry.Point3D;
import org.junit.*;
import utbm.tx52.atoms_visualiser.Atome;
import utbm.tx52.atoms_visualiser.Environment;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by anthony on 30/11/16.
 */
public class OctreeTest {
    private Octree octree;
    private int maxObjects;
    private double size;

    @Before
    public void setUp() {
        maxObjects = 2;
        size = Math.pow(2, 20);
        initOctree();
    }

    private void initOctree() {
        octree = new Octree(size, maxObjects);
    }

    @Test
    public void setMaxObjects() throws OctreeSubdivisionException {
        final int NEW_MAX_OBJECTS = 10;

        octree.subdivide();
        octree.setMaxObjects(NEW_MAX_OBJECTS);
        for(Octree child : octree.children)
            assertEquals(NEW_MAX_OBJECTS, child.getMaxObjects());
    }

    @Test
    public void getObjects() {
        Environment environment = genEnvironment(2, false);

        Octree t_octree = new Octree<Atome>(size, maxObjects, environment.getAtoms());
        assertEquals(environment.getAtoms(), t_octree.getObjects());
    }

    @Test
    public void getOctreeForPoint() throws OctreeSubdivisionException, PointOutsideOctreeException {
        Environment environment = genEnvironment(50, false);
        for(Atome a : environment.getAtoms()) {
            Octree storedIn = octree.add(a);
            assertEquals(storedIn, octree.getOctreeForPoint(a.getCoordinates()));
        }
    }

    @Test(expected = PointOutsideOctreeException.class)
    public void getOctreeForPointOutsideOctree() throws OctreeSubdivisionException, PointOutsideOctreeException {
        octree.getOctreeForPoint(new Point3D(size, size, size));
    }

    @Test
    public void add() throws OctreeSubdivisionException {
        Environment environment = genEnvironment(1, false);
        Atome a = environment.getAtoms().get(0);

        Octree storedIn = octree.add(a);
        assertEquals(octree, storedIn);
        assertEquals(a, octree.getObjects().get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addMoreThanMaxItems() throws OctreeSubdivisionException {
        Environment environment = genEnvironment(1000, false);
        ArrayList<Atome> env_atoms = environment.getAtoms();

        for(Atome a : env_atoms)
            octree.add(a);
        ArrayList octree_atoms = octree.getObjects();

        assertTrue(env_atoms.containsAll(octree_atoms) && octree_atoms.containsAll(env_atoms));
    }

    /**
     * Check that an object goes into the correct child
     * @throws OctreeSubdivisionException
     */
    @Test
    public void addCheckIfPlacedInCorrectChild() throws OctreeSubdivisionException {
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

    private boolean addThenCheckAtomIsInChild(Atome a, Octree child) throws OctreeSubdivisionException {
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
        Environment environment = genEnvironment(maxObjects, false);
        for(Atome a : environment.getAtoms())
            octree.add(a);

        Atome a = new Atome(1, 0, 0, 0, 0, false);
        octree.add(a);
        octree.remove(a);

        assertEquals("The root cube should have re-merged all children", 0, octree.children.length);
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