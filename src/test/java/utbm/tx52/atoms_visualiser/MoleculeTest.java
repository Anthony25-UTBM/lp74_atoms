package utbm.tx52.atoms_visualiser;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Created by anthony on 03/11/16.
 */
public class MoleculeTest {
    private Molecule molecule;
    private double rayon;
    private double posX;
    private double posY;

    @Before
    public void setUp() {
        posX = 0;
        posY = 0;
        rayon = 1;
        initMolecule();
    }

    private void initMolecule() {
        molecule = new Molecule(posX, posY, rayon);
    }

    @Test
    public void update() {
        molecule.update();
    }
}
