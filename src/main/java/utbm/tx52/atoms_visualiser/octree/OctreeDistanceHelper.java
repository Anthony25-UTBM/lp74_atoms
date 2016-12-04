package utbm.tx52.atoms_visualiser.octree;

import javafx.geometry.Point3D;

import java.util.ArrayList;

/**
 * Created by anthony on 03/12/16.
 */
public class OctreeDistanceHelper {
    public ArrayList<Octree> getSurroundingCubesIn(Octree octree, Octree target) throws Exception {
        return getSurroundingCubesIn(octree, target, 0);
    }

    public ArrayList<Octree> getAllCubesInPerimeter(Octree octree, Octree target, double perimeter) throws Exception {
        return getSurroundingCubesIn(octree, target, perimeter);
    }

    protected ArrayList<Octree> getSurroundingCubesIn(Octree octree, Octree target, double minSize) throws Exception {
        ArrayList<Octree> neighbours = new ArrayList<Octree>();

        if(target == octree) { }
        else if(target.isLeaf() && areOctreesNeighbours(octree, target))
            neighbours.add(target);
        else if (target.isParent()) {
            if(minSize > 0 && (target.size/4 < minSize))
                neighbours.add(target);
            else if(target.isPointInOctree(octree.getCenter()) || areOctreesNeighbours(octree, target)) {
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
            isOffsetBetweenPlans(octreeCenter.getX(), potentialNeighCenter.getX(), offset) &&
            isOffsetBetweenPlans(octreeCenter.getY(), potentialNeighCenter.getY(), offset) &&
            isOffsetBetweenPlans(octreeCenter.getZ(), potentialNeighCenter.getZ(), offset)
        );
    }

    protected boolean isOffsetBetweenPlans(double plan1Val, double plan2Val, double offsetToValidate) {
        double offset = Math.abs(plan1Val - plan2Val);
        return (offset <= offsetToValidate);
    }
}
