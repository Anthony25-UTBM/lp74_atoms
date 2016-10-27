package utbm.tx52.atoms_visualiser;
//package Molecules;

public class Agent {
    public double posX;
    public double posY;
    public double posZ;
    //public int type;	//0 : atome;    1 : molecule

    public Agent() {
    }

    public Agent(double _x, double _y, double _z) {
        posX = _x;
        posY = _y;
        posZ = _z;
    }

    public double Distance(Agent a) {
        return
                Math.sqrt(
                        (a.posX - posX) * (a.posX - posX)
                                + (a.posY - posY) * (a.posY - posY)
                                + (a.posZ - posZ) * (a.posZ - posZ));
    }

    public double DistanceCarre(Agent a) {
        return (a.posX - posX) * (a.posX - posX)
                + (a.posY - posY) * (a.posY - posY)
                + (a.posZ - posZ) * (a.posZ - posZ);
    }

}





