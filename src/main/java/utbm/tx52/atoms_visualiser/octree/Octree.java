package utbm.tx52.atoms_visualiser.octree;

import java.util.ArrayList;

public class Octree<T> {
    private ArrayList<T> objects;
    private int maxObjects;
    public Octree children[];
    public Octree<T> parent = null;

    public Octree(int maxObjects) {
        this.maxObjects = maxObjects;
        this.objects = new ArrayList<T>();
        this.children = new Octree[0];
    }

    public Octree(int maxObjects, ArrayList<T> objects) {
        this.maxObjects = maxObjects;
        this.objects = new ArrayList<T>(objects);
        this.children = new Octree[0];
    }

    public int getMaxObjects() {
        return maxObjects;
    }

    public void setMaxObjects(int maxObjects) {
        this.maxObjects = maxObjects;
        for(Octree child : children)
            child.setMaxObjects(maxObjects);
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
     * Add object into the octree or one of its n-children
     *
     * @param object: object to add
     * @return storedIn: octree where the object has been stored
     */
    public Octree add(T object) {
        // TODO: to be thread safe, should lock here
        objects.add(object);

        if(maxObjects < objects.size()) {
            try { subdivide(); } catch (OctreeSubdivisionException ignored) { }
            // TODO think about how adding the object in the corresponding child
        }

        return this;
    }

    /**
     * Subdivide the octree into 8 octrees
     *
     * @throws OctreeSubdivisionException if the octree is already parent
     */
    protected void subdivide() throws OctreeSubdivisionException {
        if(isParent())
            throw new OctreeSubdivisionException("Octree already has children");

        children = new Octree[8];
        for(int i = 0; i < 8; i++)
            children[i] = new Octree<T>(maxObjects);
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
