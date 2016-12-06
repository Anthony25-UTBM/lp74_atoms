package utbm.tx52.atoms_visualiser.octree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import utbm.tx52.atoms_visualiser.entities.Atom;
import utbm.tx52.atoms_visualiser.entities.Environment;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by anthony on 03/12/16.
 */
public class OctreeDistanceHelperTest {
    private static final Logger logger = LogManager.getLogger("OctreeDistanceHelperTest");
    public OctreeDistanceHelper octreeDistanceHelper;
    public Octree octree;
    @Rule
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void succeeded(long nanos, Description description) {
            logger.info("succeeded", nanos, description);
        }

        @Override
        protected void failed(long nanos, Throwable e, Description description) {
            logger.info("failed", nanos, description);
        }

        @Override
        protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
            logger.info("skipped", nanos, description);
        }

        @Override
        protected void finished(long nanos, Description description) {
            logger.info("finished", nanos, description);
        }
    };
    int maxObjects = 1000;
    double size = Math.pow(2, 10);

    @Before
    public void setUp() throws Exception {
        octreeDistanceHelper = new OctreeDistanceHelper();
        octree = new Octree(size, maxObjects);
    }

    @Test
    public void getSurroundingCubesIn() throws Exception {
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
    public void getAllNeighInSphere() throws Exception {
        Environment environment = genEnvironment(100000, false);
        ArrayList<Atom> atoms = environment.getAtoms().getObjects();
        for(Atom a : atoms)
            octree.add(a);

        Atom atom = atoms.get(0);
        double perimeter = 100;

        double start_naive_algorithm_time = stopwatch.runtime(TimeUnit.MICROSECONDS);
        ArrayList<Atom> neighboursInSphereWithNaiveAlgorithm = new ArrayList<Atom>();
        for(Atom a : atoms) {
            if(a != atom && a.getCoordinates().distance(atom.getCoordinates()) <= perimeter)
                neighboursInSphereWithNaiveAlgorithm.add(a);
        }
        double naive_algorithm_time = stopwatch.runtime(TimeUnit.MICROSECONDS) - start_naive_algorithm_time;

        double start_algorithm_time = stopwatch.runtime(TimeUnit.MICROSECONDS);
        ArrayList neighboursInSphere = octreeDistanceHelper.getAllNeighInSphere(octree, atom.getCoordinates(), perimeter);
        double algorithm_time = stopwatch.runtime(TimeUnit.MICROSECONDS) - start_algorithm_time;

        assertEquals(neighboursInSphereWithNaiveAlgorithm.size(), neighboursInSphere.size());
        assertEquals(
            neighboursInSphere.containsAll(neighboursInSphereWithNaiveAlgorithm),
            neighboursInSphereWithNaiveAlgorithm.containsAll(neighboursInSphere)
        );
    }

    private Environment genEnvironment(int nbAtoms, boolean isCHNO) {
        return new Environment(nbAtoms, size, isCHNO);
    }

    @Test
    public void areOctreesNeighbours() throws Exception {
        octree.subdivide();
        assertTrue(octreeDistanceHelper.areOctreesNeighbours(octree.children[0], octree.children[7]));
    }

    @Test
    public void areOctreesNeighboursDistantOctrees() throws Exception {
        octree.subdivide();
        octree.children[0].subdivide();
        assertFalse(octreeDistanceHelper.areOctreesNeighbours(
            octree.children[0].children[0], octree.children[1]
        ));
    }

}