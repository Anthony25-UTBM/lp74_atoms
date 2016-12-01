package utbm.tx52.atoms_visualiser.octree;

/**
 * Created by anthony on 01/12/16.
 */
public class PointOutsideOctreeException extends Throwable {
    public PointOutsideOctreeException()
    {
    }

    public PointOutsideOctreeException(String message)
    {
        super(message);
    }
}
