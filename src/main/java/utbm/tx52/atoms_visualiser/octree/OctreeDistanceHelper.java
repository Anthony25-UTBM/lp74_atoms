package utbm.tx52.atoms_visualiser.octree;

import com.google.common.collect.Iterators;
import javafx.geometry.Point3D;
import utbm.tx52.atoms_visualiser.utils.Pair;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by anthony on 03/12/16.
 */
public class OctreeDistanceHelper {
    public ArrayList<OctreePoint> getAllNeighInSphere(Octree<OctreePoint> node, Point3D sphereCenter, double radius)
            throws Exception {
        ArrayList<OctreePoint> neighbours = new ArrayList<OctreePoint>();
        double distanceCubeCenterSphereCenter = node.getCenter().distance(sphereCenter);

        if(isCubeMaybeInSphere(node, distanceCubeCenterSphereCenter, radius)) {
            if(node.isLeaf() || distanceCubeCenterSphereCenter <= radius) {
                // The entire cube is (or at least all children are) in the sphere
                Iterator itrObjects = node.iterator();
                while(itrObjects.hasNext()) {
                    OctreePoint p = (OctreePoint) itrObjects.next();
                    double pSphereDistance = p.getCoordinates().distance(sphereCenter);
                    if (pSphereDistance <= radius && !p.getCoordinates().equals(sphereCenter))
                        neighbours.add(p);
                }
            }
            else {
                for(Octree<OctreePoint> child : node.children) {
                    if (child.hasObjects()) {
                        neighbours.addAll(getAllNeighInSphere(child, sphereCenter, radius));
                    }
                }
            }
        }

        return neighbours;
    }

    public ArrayList<OctreePoint> getFarthestNeighbours(Octree root, OctreePoint object) throws Exception {
        OctreePoint randomPoint = getRandomObjInSameCube(object, root);
        ArrayList neighbours;
        Octree objectOctree = root.getOctreeForPoint(object.getCoordinates());

        neighbours = objectOctree.getObjects();
        for(Octree o : getSurroundingCubesIn(root, objectOctree))
            Iterators.addAll(neighbours, o.iterator());

        Iterator neighboursIterator = neighbours.iterator();
        Pair<ArrayList<OctreePoint>, Double> farthestNeighs = null;

        while(neighboursIterator.hasNext()) {
            try {
                farthestNeighs = refreshFarthestNeighsIfNextIsFarther(object, farthestNeighs, neighboursIterator);
            } catch (OctreeNoNeighbourFoundException ignored) {
                if(farthestNeighs == null)
                    throw new OctreeNoNeighbourFoundException();
                break;
            }
        }

        return farthestNeighs.x;
    }

    private OctreePoint getRandomObjInSameCube(OctreePoint object, Octree<OctreePoint> root) throws Exception {
        Octree<OctreePoint> pointsOctree = root.getOctreeForPoint(object.getCoordinates());
        Iterator pointsOctreeIterator = pointsOctree.iterator();

        OctreePoint randomObject = (OctreePoint) pointsOctreeIterator.next();
        if(randomObject == object)
            randomObject = (OctreePoint) pointsOctreeIterator.next();

        return randomObject;
    }

    private Pair<ArrayList<OctreePoint>, Double> refreshFarthestNeighsIfNextIsFarther(
            OctreePoint point, Pair<ArrayList<OctreePoint>, Double> farthestNeighs, Iterator neighboursIterator)
            throws Exception {

        OctreePoint neighbour = (OctreePoint) neighboursIterator.next();
        if(neighbour == point)
            return farthestNeighs;

        double distance = neighbour.getCoordinates().distance(point.getCoordinates());
        if(farthestNeighs == null || distance < farthestNeighs.y) {
            ArrayList<OctreePoint> neighbours = new ArrayList<>();
            neighbours.add(neighbour);
            farthestNeighs = new Pair<>(neighbours, distance);
        }
        else if(distance == farthestNeighs.y)
            farthestNeighs.x.add(neighbour);

        return farthestNeighs;
    }

    /**
     * Check if cube (octree) is maybe in sphere
     *
     * In a cube, the farthest points are its corners. To check if a cube is in a sphere, we check if at least one
     * of the corners are in the sphere (if the distance between it and the sphere center is at least equal to the
     * sphere radius). You can see the approximation here, as a cube could not be in a circle even if the sphere
     * ends next to one of its edge.
     * However, it is super fast, so very useful for functions that do not mind if there is an error here (typically for
     * the getAllNeighInSphere).
     *
     * @param cube
     * @param radius
     * @return true if the cube can be in the corner. if false, it is certainly not.
     */
    protected boolean isCubeMaybeInSphere(Octree cube, double distanceCubeCenterSphereCenter, double radius) {
        if(distanceCubeCenterSphereCenter <= radius)
            return true;

        Point3D farthestCorner = cube.getCenter().add(cube.size/2, cube.size/2, cube.size/2);
        double distanceFromCubeCenterToCorner = cube.getCenter().distance(farthestCorner);
        return distanceCubeCenterSphereCenter - radius <= distanceFromCubeCenterToCorner;
    }

    public ArrayList<Octree> getSurroundingCubesIn(Octree octree, Octree node) throws Exception {
        ArrayList<Octree> neighbours = new ArrayList<Octree>();

        if(node == octree) { }
        else if(node.isLeaf() && areOctreesNeighbours(octree, node))
            neighbours.add(node);
        else if (node.isParent()) {
            if(node.isPointInOctree(octree.getCenter()) || areOctreesNeighbours(octree, node)) {
                for (Octree child : node.children)
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
