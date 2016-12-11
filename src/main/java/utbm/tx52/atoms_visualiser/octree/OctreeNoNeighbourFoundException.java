package utbm.tx52.atoms_visualiser.octree;

/**
 * Created by anthony on 07/12/16.
 */
public class OctreeNoNeighbourFoundException extends Exception {
    public OctreeNoNeighbourFoundException() {
    }

    public OctreeNoNeighbourFoundException(String message) {
        super(message);
    }
}
