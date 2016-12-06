package utbm.tx52.atoms_visualiser.octree;

import com.google.common.collect.Iterators;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;

public class Octree<T extends OctreePoint> {
    public Octree children[] = new Octree[0];
    public Octree parent = null;
    public double size;
    private ArrayList<T> objects;
    private int maxObjects;
    private Point3D center;
    private StampedLock rwlock;

    public Octree(double size, int maxObjects) {
        this.maxObjects = maxObjects;
        this.objects = new ArrayList<T>();
        this.center = Point3D.ZERO;
        this.size = size;
        rwlock =  new StampedLock();
    }

    public Octree(double size, int maxObjects, ArrayList<T> objects) {
        this(size, maxObjects);
        for(T o : objects) {
            try {
                add(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Octree(Octree<T> octree) throws InterruptedException {
        this(octree.getSize(), octree.getMaxObjects(), octree.getObjects());
    }

    public double getSize() {
        return size;
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

    public boolean hasObjects() {
        return (objects.size() > 0 || isParent());
    }

    public ArrayList<T> getObjects() throws InterruptedException {
        if(isLeaf())
            return new ArrayList<T>(objects);

        ArrayList<T> childrenObjects = new ArrayList<T>();
        long stamp = rwlock.tryOptimisticRead();
        for (Octree child : children) {
            if(child.hasObjects())
                Iterators.addAll(childrenObjects, child.getObjectsIterator());
        }

        return childrenObjects;
    }

    public Iterator getObjectsIterator() throws InterruptedException {
        if(isLeaf())
            return objects.iterator();

        ArrayList childrenObjects = new ArrayList();
        long stamp = rwlock.readLockInterruptibly();
        try {
            for (Octree child : children) {
                if(child.hasObjects())
                    childrenObjects.add(child.getObjectsIterator());
            }
        } finally {
            rwlock.unlockRead(stamp);
        }

        return Iterators.concat(childrenObjects.iterator());
    }

    /**
     * Get the tiniest cube/octree containing a 3D point
     * @param coord
     * @return
     */
    public Octree getOctreeForPoint(Point3D coord) throws PointOutsideOctreeException, InterruptedException {
        if(!isPointInOctree(coord))
            throw new PointOutsideOctreeException("Point is not in octree");

        if(isParent()) {
            long stamp = rwlock.readLockInterruptibly();
            try {
                int i = getChildIndexForPoint(coord);
                return children[i].getOctreeForPoint(coord);
            } finally {
                rwlock.unlockRead(stamp);
            }
        }
        else
            return this;
    }

    public boolean isPointInOctree(Point3D coord) {
        return isPointInOctree(coord, 0);
    }

    public boolean isPointInOctree(Point3D coord, double delta) {
        return (
            (coord.getX() >= (center.getX() - size/2 - delta)) && (coord.getX() < (center.getX() + size/2 + delta)) &&
            (coord.getY() >= (center.getY() - size/2 - delta)) && (coord.getY() < (center.getY() + size/2 + delta)) &&
            (coord.getZ() >= (center.getZ() - size/2 - delta)) && (coord.getZ() < (center.getZ() + size/2 + delta))
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
        int x_split = (coord.getX() < center.getX()) ? 0 : 1;
        int y_split = (coord.getY() < center.getY()) ? 0 : 1;
        int z_split = (coord.getZ() < center.getZ()) ? 0 : 1;

        return x_split + 2 * y_split + 4 * z_split;
    }

    /**
     * Add object into the octree or one of its n-children
     *
     * @param object: object to add
     * @return storedIn: octree where the object has been stored
     */
    public Octree add(T object) throws OctreeSubdivisionException, InterruptedException {
        long stamp;
        stamp = rwlock.writeLock();
        try {
            if(isLeaf() && maxObjects < objects.size() + 1)
                subdivide();
        } catch(OctreeAlreadyParentException ignored) {
        } finally {
            rwlock.unlockWrite(stamp);
        }

        Octree addedIn;
        stamp = rwlock.readLock();
        try {
            if(isParent()) {
                int childIndex = getChildIndexForPoint(object.getCoordinates());
                addedIn = children[childIndex].add(object);
            }
            else {
                objects.add(object);
                addedIn = this;
            }
        } finally {
            rwlock.unlockRead(stamp);
        }

        return addedIn;
    }

    /**
     * Subdivide the octree into 8 octrees
     *
     * @throws OctreeSubdivisionException if the octree is already parent
     */
    protected void subdivide() throws OctreeSubdivisionException, InterruptedException {
        if (isParent())
            throw new OctreeAlreadyParentException("Octree already has children");

        children = new Octree[8];
        for (int i = 0; i < 8; i++) {
            Octree child = new Octree<T>(size, maxObjects);
            child.parent = this;
            child.size = size / 2;
            child.setCenter(getNewChildCenter(i));
            children[i] = child;
        }

        ArrayList<T> objectsCopy = objects;
        objects = new ArrayList<T>();

        for (T o : objectsCopy) {
            int childIndex = getChildIndexForPoint(o.getCoordinates());
            children[childIndex].add(o);
        }
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

        double z = (index < 4) ? center.getZ() - CHILD_SIZE/2 : center.getZ() + CHILD_SIZE/2;
        index -= index / 4 * 4;

        double y = (index < 2) ? center.getY() - CHILD_SIZE/2 : center.getY() + CHILD_SIZE/2;
        index -= index / 2 * 2;

        double x = (index == 0) ? center.getX() - CHILD_SIZE/2 : center.getX() + CHILD_SIZE/2;

        return new Point3D(x, y, z);
    }

    public void remove(T object) throws Exception {
        if (isParent())
            getOctreeForPoint(object.getCoordinates()).remove(object);
        else {
            objects.remove(object);
            if (!isRoot())
                try {
                    parent.mergeAllChildren();
                } catch (OctreeCannotMergeException ignore) {
                }
        }
    }

    /**
     * Merge all children to re-form one bloc/cube
     * @throws Exception
     * @throws OctreeSubdivisionException
     */
    protected void mergeAllChildren() throws Exception {
        ArrayList<T> oldObjects;
        long stamp = rwlock.readLock();
        try {
            if (!isPossibleToMergeChildren())
                throw new OctreeCannotMergeException(
                    "One of the children is not leaf and/or max objects limit would be reached"
                );
            oldObjects = getObjects();
        } finally {
            rwlock.unlockRead(stamp);
        }

        children = new Octree[0];
        for (T o : oldObjects)
            add(o);

        if (!isRoot())
            try {
                parent.mergeAllChildren();
            } catch (OctreeCannotMergeException ignore) {
            }
    }

    protected boolean isPossibleToMergeChildren() throws InterruptedException {
        boolean allChildrenAreLeaf = true;
        for (Octree c : children)
            allChildrenAreLeaf = allChildrenAreLeaf && c.isLeaf();

        boolean underMaxObjects = getObjects().size() <= maxObjects;

        return allChildrenAreLeaf && underMaxObjects;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isParent() {
        return children.length > 0;
    }

    public boolean isLeaf() throws InterruptedException {
        return !isParent();
    }
}
