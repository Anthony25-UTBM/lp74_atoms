package utbm.tx52.atoms_visualiser.octree;

import com.google.common.collect.Iterators;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by anthony on 03/12/16.
 */
public class OctreeDistanceHelper {
    public ArrayList getAllNeighInSphere(Octree<OctreePoint> node, Point3D sphereCenter, double perimeter) throws Exception {
        ArrayList neighbours = new ArrayList<>();
        double distanceCubeCenterSphereCenter = node.getCenter().distance(sphereCenter);

        if(isCubeMaybeInSphere(node, distanceCubeCenterSphereCenter, perimeter)) {
            if(node.isLeaf() || distanceCubeCenterSphereCenter <= perimeter) {
                // The entire cube is (or at least all children are) in the sphere
                Iterator itrObjects = node.getObjectsIterator();
                while(itrObjects.hasNext()) {
                    OctreePoint p = (OctreePoint) itrObjects.next();
                    double pSphereDistance = p.getCoordinates().distance(sphereCenter);
                    if (pSphereDistance <= perimeter && !p.getCoordinates().equals(sphereCenter))
                        neighbours.add(p);
                }
            }
            else {
                for(Octree child : node.children)
                    if(child.hasObjects()) {
                        neighbours.addAll(getAllNeighInSphere(child, sphereCenter, perimeter));
                    }
            }
        }

        return neighbours;
    }

    /**
     * Check if cube (octree) is maybe in sphere
     *
     * In a cube, the farthest points are its corners. To check if a cube is in a sphere, we check if at least one
     * of the corners are in the sphere (if the distance between it and the sphere center is at least equal to the
     * sphere perimeter). You can see the approximation here, as a cube could not be in a circle even if the sphere
     * ends next to one of its edge.
     * However, it is super fast, so very useful for functions that do not mind if there is an error here (typically for
     * the getAllNeighInSphere).
     *
     * @param cube
     * @param perimeter
     * @return true if the cube can be in the corner. if false, it is certainly not.
     */
    protected boolean isCubeMaybeInSphere(Octree cube, double distanceCubeCenterSphereCenter, double perimeter) {
        if(distanceCubeCenterSphereCenter <= perimeter)
            return true;

        Point3D farthestCorner = cube.getCenter().add(cube.size/2, cube.size/2, cube.size/2);
        double distanceFromCubeCenterToCorner = cube.getCenter().distance(farthestCorner);
        return distanceCubeCenterSphereCenter - perimeter <= distanceFromCubeCenterToCorner;
    }

    protected ArrayList<Octree> getSurroundingCubesIn(Octree octree, Octree target) throws Exception {
        ArrayList<Octree> neighbours = new ArrayList<Octree>();

        if(target == octree) { }
        else if(target.isLeaf() && areOctreesNeighbours(octree, target))
            neighbours.add(target);
        else if (target.isParent()) {
            if(target.isPointInOctree(octree.getCenter()) || areOctreesNeighbours(octree, target)) {
                for (Octree child : target.children)
                    neighbours.addAll(getSurroundingCubesIn(octree, child));
            }
        }

        return neighbours;
    }

    public boolean areOctreesNeighbours(Octree octree, Octree potentialNeigh) {
        Point3D octreeCenter = octree.getCenter();
        Point3D potentialNeighCenter = potentialNeigh.getCenter();
        double offset = octree.size/2 + potentialNeigh.size/2;

        return (
            isOffsetBetweenPlansAtLeast(octreeCenter.getX(), potentialNeighCenter.getX(), offset) &&
            isOffsetBetweenPlansAtLeast(octreeCenter.getY(), potentialNeighCenter.getY(), offset) &&
            isOffsetBetweenPlansAtLeast(octreeCenter.getZ(), potentialNeighCenter.getZ(), offset)
        );
    }

    protected boolean isOffsetBetweenPlansAtLeast(double plan1Val, double plan2Val, double offsetToValidate) {
        double offset = Math.abs(plan1Val - plan2Val);
        return (offset <= offsetToValidate);
    }
}
