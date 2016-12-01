package utbm.tx52.atoms_visualiser.octree;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Iterator;

public class Octree<T extends OctreePoint> {
    private ArrayList<T> objects;
    private int maxObjects;
    public Octree children[];
    public Octree parent = null;
    private Point3D center;
    public double size;

    public Octree(double size, int maxObjects) {
        this.maxObjects = maxObjects;
        this.objects = new ArrayList<T>();
        this.children = new Octree[0];
        this.center = new Point3D(size/2, size/2, size/2);
        this.size = size;
    }

    public Octree(double size, int maxObjects, ArrayList<T> objects) {
        this.maxObjects = maxObjects;
        this.objects = new ArrayList<T>(objects);
        this.children = new Octree[0];
        this.center = new Point3D(size/2, size/2, size/2);
        this.size = size;
    }

    public int getMaxObjects() {
        return maxObjects;
    }

    public void setMaxObjects(int maxObjects) {
        this.maxObjects = maxObjects;
        for(Octree child : children)
            child.setMaxObjects(maxObjects);
    }

    public Point3D getCenter() {
        return new Point3D(center.getX(), center.getY(), center.getZ());
    }

    public void setCenter(Point3D center) {
        this.center = center;
    }

    public ArrayList getObjects() {
        if(isLeaf())
            return new ArrayList<T>(objects);

        ArrayList<Object> childrenObjects = new ArrayList<>();
        for(Octree child : children) {
            childrenObjects.addAll(child.getObjects());
        }

        return childrenObjects;
    }

    /**
     * Get the tiniest cube/octree containing a 3D point
     * @param coord
     * @return
     */
    public Octree getOctreeForPoint(Point3D coord) throws PointOutsideOctreeException {
        if(!isPointInOctree(coord))
            throw new PointOutsideOctreeException("Point is not in octree");

        if(isParent()) {
            int i = getChildIndexForPoint(coord);
            return children[i].getOctreeForPoint(coord);
        }
        else
            return this;
    }

    public boolean isPointInOctree(Point3D coord) {
        return (
            (coord.getX() >= (center.getX() - size/2)) && (coord.getX() < (center.getX() + size/2) ) &&
            (coord.getY() >= (center.getY() - size/2)) && (coord.getY() < (center.getY() + size/2) ) &&
            (coord.getZ() >= (center.getZ() - size/2)) && (coord.getZ() < (center.getZ() + size/2) )
        );
    }

    /**
     * Return the child associated with the received coordinates
     *
     * Use powers of 2 to quickly determine the index
     *
     * @param coord
     * @return childIndex
     */
    private int getChildIndexForPoint(Point3D coord) {
        int x_split = (coord.getX() > center.getX()) ? 1 : 0;
        int y_split = (coord.getY() > center.getY()) ? 1 : 0;
        int z_split = (coord.getZ() > center.getZ()) ? 1 : 0;

        return x_split + 2 * y_split + 4 * z_split;
    }

    /**
     * Add object into the octree or one of its n-children
     *
     * @param object: object to add
     * @return storedIn: octree where the object has been stored
     */
    public Octree add(T object) throws OctreeSubdivisionException {
        // TODO: to be thread safe, should lock here
        if(!isParent() && maxObjects < objects.size() + 1)
            subdivide();

        if(isParent()) {
            int childIndex = getChildIndexForPoint(object.getCoordinates());
            return children[childIndex].add(object);
        }
        else {
            objects.add(object);
            return this;
        }
    }

    /**
     * Subdivide the octree into 8 octrees
     *
     * @throws OctreeSubdivisionException if the octree is already parent
     */
    protected void subdivide() throws OctreeSubdivisionException {
        if(isParent())
            throw new OctreeSubdivisionException("Octree already has children");
        else if(size % 2 > 0)
            throw new OctreeSubdivisionException("Size cannot be subdivided anymore");

        children = new Octree[8];
        for(int i = 0; i < 8; i++) {
            Octree child = new Octree<T>(size, maxObjects);
            child.parent = this;
            child.size = size/2;
            child.setCenter(getNewChildCenter(i));
            children[i] = child;
        }

        ArrayList<T> objectsCopy = objects;
        objects = new ArrayList<T>();

        for(T o : objectsCopy)
            add(o);
    }

    /**
     * Define coordinates for a new child based on its future index in the tree
     *
     * The algorithm is based on a shift on the binary
     * @param index
     * @return
     */
    protected Point3D getNewChildCenter(int index) {
        final double CHILD_SIZE = size/2;
        String binaryIndex = String.format("%03d", Integer.valueOf(Integer.toBinaryString(index)));

        double z = (index < 4) ? center.getZ() - CHILD_SIZE/2 : center.getZ() + CHILD_SIZE/2;
        index -= (int)(index/4) * 4;

        double y = (index < 2) ? center.getY() - CHILD_SIZE/2 : center.getY() + CHILD_SIZE/2;
        index -= (int)(index/2) * 2;

        double x = (index == 0) ? center.getX() - CHILD_SIZE/2 : center.getX() + CHILD_SIZE/2;

        return new Point3D(x, y, z);
    }

    public void remove(T object) throws PointOutsideOctreeException, OctreeSubdivisionException {
        if(isParent())
            getOctreeForPoint(object.getCoordinates()).remove(object);
        else {
            objects.remove(object);
            if(!isRoot())
                try { parent.mergeAllChildren(); } catch (Exception ignore) { };
        }
    }

    /**
     * Merge all children to re-form one bloc/cube
     * @throws Exception
     * @throws OctreeSubdivisionException
     */
    protected void mergeAllChildren() throws Exception, OctreeSubdivisionException {
        if(!isPossibleToMergeChildren())
            throw new Exception("One of the children is not leaf and/or max objects limit would be reached");

        ArrayList<T> oldObjects = getObjects();
        children = new Octree[0];

        for(T o : oldObjects)
            add(o);
    }

    protected boolean isPossibleToMergeChildren() {
        boolean allChildrenAreLeaf = true;
        for(Octree c : children)
            allChildrenAreLeaf = allChildrenAreLeaf && c.isLeaf();

        boolean underMaxObjects = getObjects().size() <= maxObjects;

        return allChildrenAreLeaf && underMaxObjects;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isParent(){
        return children.length > 0;
    }

    public boolean isLeaf() {
        return children.length == 0;
    }
}
