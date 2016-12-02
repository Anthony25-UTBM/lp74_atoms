package utbm.tx52.atoms_visualiser.octree;

/**
 * Created by anthony on 02/12/16.
 */
public class OctreeAlreadyParentException extends OctreeSubdivisionException{
    public OctreeAlreadyParentException()
    {
    }

    public OctreeAlreadyParentException(String message)
    {
        super(message);
    }
}

