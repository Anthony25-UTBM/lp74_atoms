package utbm.tx52.atoms_visualiser;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Created by anthony on 15/11/16.
 */
public class AgentTest {
    private Agent agent;
    public double posX;
    public double posY;
    public double posZ;

    @Before
    public void setUp() {
        posX = 0;
        posY = 0;
        posZ = 0;
        initAgent();
    }

    private void initAgent() {
        agent = new Agent(posX, posY, posZ);
    }

    @Test
    public void distance() {
        Agent a = new Agent(5, 10, 20);
        assertEquals(Math.sqrt(525), agent.distance(a), 0.001);
    }

    @Test
    public void distanceSquared() {
        Agent a = new Agent(0, 10, 10);
        assertEquals(200, agent.distanceSquared(a), 0.001);
    }
}
