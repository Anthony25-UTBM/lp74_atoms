package utbm.tx52.atoms_visualiser;

import utbm.tx52.atoms_visualiser.Molecule;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Created by anthony on 03/11/16.
 */
public class MoleculeTest {
    private Molecule molecule;
    private double rayon;
    private double posx;
    private double posy;

    @Before
    public void setUp() {
        posx = 0;
        posy = 0;
        rayon = 1;
        initMolecule();
    }

    private void initMolecule() {
        molecule = new Molecule(posx, posy, rayon);
    }

    @Test
    public void update() {
        int lifetime = molecule.lifetime;
        molecule.update();
        assertEquals(molecule.lifetime, lifetime - 1);
    }

    @Test
    public void isDead() {
        molecule.lifetime = 1;
        assertFalse(molecule.isDead());

        molecule.update();
        assertTrue(molecule.isDead());
    }
}
