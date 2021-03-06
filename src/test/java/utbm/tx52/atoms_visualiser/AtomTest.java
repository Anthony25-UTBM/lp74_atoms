package utbm.tx52.atoms_visualiser;

import org.junit.Before;
import org.junit.Test;
import utbm.tx52.atoms_visualiser.entities.Atom;
import utbm.tx52.atoms_visualiser.entities.Environment;

import static org.junit.Assert.assertEquals;

public class AtomTest {
    private Atom atom;
    private double posX;
    private double posY;
    private double posZ;
    private int n;
    private double dir;
    private boolean isCHNO;
    private Environment environment;


    @Before
    public void setUp() {
        posX = 0;
        posY = 0;
        posZ = 0;
        n = 1;
        dir = 0;
        isCHNO = false;
        environment = new Environment(0, 1, isCHNO);
        initAgent();
    }

    private void initAgent() {
        atom = new Atom(environment, n, posX, posY, posZ, dir, isCHNO);
    }

    @Test
    public void distance() {
        Atom a = new Atom(environment, n, 5, 10, 20, dir, isCHNO);
        assertEquals(Math.sqrt(525), atom.distance(a), 0.001);
    }

    @Test
    public void distanceSquared() {
        Atom a = new Atom(environment, n, 0, 10, 10, dir, isCHNO);
        assertEquals(200, atom.distanceSquared(a), 0.001);
    }
}
