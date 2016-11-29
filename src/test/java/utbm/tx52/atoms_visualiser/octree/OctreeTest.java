package utbm.tx52.atoms_visualiser.octree;

import org.junit.*;
import utbm.tx52.atoms_visualiser.Atome;
import utbm.tx52.atoms_visualiser.Environment;

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
        size = Math.pow(8, 3);
        initOctree();
    }

    private void initOctree() {
        octree = new Octree(maxObjects);
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

        Octree t_octree = new Octree<Atome>(maxObjects, environment.getAtoms());
        assertEquals(environment.getAtoms(), t_octree.getObjects());
    }

    @Test
    public void add() {
        Environment environment = genEnvironment(1, false);
        Atome a = environment.getAtoms().get(0);

        Octree storedIn = octree.add(a);
        assertEquals(octree, storedIn);
        assertEquals(a, octree.getObjects().get(0));
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