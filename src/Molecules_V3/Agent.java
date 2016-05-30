package Molecules_V3;

//import java.awt.Color;

public class Agent {
 public Point pos ;
 //public int type;	//0 : atome;    1 : liaison;    2 : molecule

 public Agent() {}
 
 public Agent(double _x, double _y, double _z) {
 	this.pos = new Point(_x, _y, _z);
 }
 
 public double Distance(Agent a) {
     return Math.sqrt((a.pos.x - pos.x) * (a.pos.x - pos.x) + (a.pos.y - pos.y) * (a.pos.y - pos.y));
 }
 
 public double DistanceCarre(Agent a) {
     return (a.pos.x - pos.x) * (a.pos.x - pos.x) + (a.pos.y - pos.y) * (a.pos.y - pos.y);
 }

}





