package utbm.tx52.atoms_visualiser;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
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
    private AgentContainer container;


    @Before
    public void setUp() {
        posX = 0;
        posY = 0;
        posZ = 0;
        n = 1;
        dir = 0;
        isCHNO = false;
        // create agent container
        Runtime rt = Runtime.instance();
        Properties p = new ExtendedProperties();
        p.setProperty(Profile.GUI, "true");
        ProfileImpl pc = new ProfileImpl(p);
        pc.setParameter(ProfileImpl.MAIN_HOST, "127.0.0.1");
        pc.setParameter(Profile.PLATFORM_ID, "sink-platform");
        pc.setParameter(Profile.LOCAL_HOST, "127.0.0.1");
        pc.setParameter(Profile.CONTAINER_NAME, "sink-container");
        pc.setParameter(Profile.NO_MTP, "true");
        container = rt.createAgentContainer(pc);
        environment = new Environment(container, 0, 1, isCHNO);
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
