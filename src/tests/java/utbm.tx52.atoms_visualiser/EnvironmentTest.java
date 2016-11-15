package utbm.tx52.atoms_visualiser;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Created by anthony on 15/11/16.
 */
public class EnvironmentTest {
    private Environment environment;
    private int nbAtomes;
    private double largeur;
    private double profondeur;
    private double hauteur;
    private boolean isCHNO;

    @Before
    public void setUp() {
        nbAtomes = 5;
        largeur = 100;
        profondeur = 100;
        hauteur = 100;
        isCHNO = false;

        initEnvironment();
    }

    private void initEnvironment() {
        environment = new Environment(nbAtomes, largeur, profondeur, hauteur, isCHNO);
    }

    @Test
    public void numberOfNotActiveAtomsAllActive() throws NegativeSpeedException {
        activateAllAtomsOf(environment);
        assertEquals(0, environment.nbOfNotActiveAtoms());
    }

    @Test
    public void numberOfNotActiveAtomsAllDisabled() throws NegativeSpeedException {
        disableAllAtomsOf(environment);
        assertEquals(nbAtomes, environment.nbOfNotActiveAtoms());
    }

    public void activateAllAtomsOf(Environment e) {
        setAllAtomsSpeedDirOfEnvTo(e, 1);
    }

    public void disableAllAtomsOf(Environment e) {
        setAllAtomsSpeedDirOfEnvTo(e, 0);
    }

    public void setAllAtomsSpeedDirOfEnvTo(Environment env, double speed) {
        environment.atoms.forEach(a->{
            a.speedX = speed;
            a.speedY = speed;
            a.speedZ = speed;
        });
    }

    @Test
    public void setAtomsSpeed() throws NegativeSpeedException {
        environment.setAtomsSpeed(10);

        environment.atoms.forEach(a->{
            assertEquals(10, a.getSpeed(), 0.001);
        });
    }

    @Test(expected = NegativeSpeedException.class)
    public void setAtomsSpeedNegative() throws NegativeSpeedException {
        environment.setAtomsSpeed(-10);
    }

    @Test
    public void getSpeed() throws NegativeSpeedException {
        environment.setAtomsSpeed(10);
        assertEquals(10, environment.getSpeed(), 0.001);
    }
}