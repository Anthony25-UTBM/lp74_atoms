package utbm.tx52.atoms_visualiser.octree;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by anthony on 03/12/16.
 */
public class OctreeDistanceHelperTest {
    public OctreeDistanceHelper octreeDistanceHelper;
    public Octree octree;
    int maxObjects = 2;
    double size = Math.pow(2, 4);

    @Before
    public void setUp() throws Exception {
        octreeDistanceHelper = new OctreeDistanceHelper();
        octree = new Octree(size, maxObjects);
    }

    @Test
    public void getSurroundingCubesIn() throws Exception, OctreeSubdivisionException {
        octree.subdivide();
        for(Octree child : octree.children)
            child.subdivide();

        Octree centerChildCube = octree.children[0].children[7];
        ArrayList surroundingCubes = octreeDistanceHelper.getSurroundingCubesIn(centerChildCube, octree);

        ArrayList surroundingWithNaiveAlgorithm = new ArrayList();
        for(Octree child : octree.children) {
            for (Octree subchild : child.children) {
                if(subchild != centerChildCube && octreeDistanceHelper.areOctreesNeighbours(subchild, centerChildCube))
                    surroundingWithNaiveAlgorithm.add(subchild);
            }
        }

        assertEquals(
            surroundingCubes.containsAll(surroundingWithNaiveAlgorithm),
            surroundingWithNaiveAlgorithm.containsAll(surroundingCubes)
        );
    }

    @Test
    public void areOctreesNeighbours() throws Exception, OctreeSubdivisionException {
        octree.subdivide();
        assertTrue(octreeDistanceHelper.areOctreesNeighbours(octree.children[0], octree.children[7]));
    }

    @Test
    public void areOctreesNeighboursDistantOctrees() throws Exception, OctreeSubdivisionException {
        octree.subdivide();
        octree.children[0].subdivide();
        assertFalse(octreeDistanceHelper.areOctreesNeighbours(
            octree.children[0].children[0], octree.children[1]
        ));
    }

}