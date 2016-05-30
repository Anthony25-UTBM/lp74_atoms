package Molecules_V3;

class Point3D {
double x, y, z;
public Point3D ( double x0 , double y0 , double z0) {
this .x = x0;
this .y = y0;
this .z = z0;
}
static double sqr ( double x) { return x*x; }
public static double distance ( Point3D p, Point3D q) {
return Math . sqrt (sqr (q.x - p.x) + sqr (q.y - p.y)
+ sqr(q.z - p.z));
}
public static Point3D add ( Point3D p, Point3D q) {
return new Point3D (p.x + q.x, p.y + q.y, p.z + q.z);
}
public static void scale ( Point3D p, double k) {
p.x *= k;
p.y *= k;
p.z *= k;
}
}


class Atom {
	Point3D center ;
	double radius ;
	public static final double H_RADIUS = 1.2;
	public static final double O_RADIUS = 1.5;
	public Atom ( double x, double y, double z, double rad ) {
		this . center = new Point3D (x, y, z);
		this . radius = rad ;
	}
	
	public static boolean bump ( Atom a, Atom b) {
		return Point3D . distance (a.center , b. center )
				< a. radius + b. radius ;
	}

	public static Point3D middle ( Atom [] t) {
		Point3D middle = t [0]. center ;
		for (int i = 1; i < t. length ; ++i)
			middle = Point3D .add (middle , t[i]. center );
		Point3D . scale (middle , 1.0/ t. length );
		return middle ;
	}

	public static double maxDistance ( Point3D p, Atom a) {
		return Point3D . distance (p, a. center ) + a. radius ;
	}
}


public class Molecule2 {
	Atom[] atoms ;
	Atom sphere ;
	public Molecule2(Atom[] t) {
		this.atoms = t;
		Point3D center = Atom.middle( atoms );
		double r = 0;
		for (int i = 0; i < atoms.length; ++i) {
			double ri = Atom.maxDistance(center, atoms[i]);
			if (r < ri) r = ri;
		}
		this.sphere = new Atom(center.x, center.y, center.z, r);
	}

	public static boolean bump( Atom a, Molecule2 b) {
		if (! Atom.bump(a, b.sphere ))
			return false ;
		for (int i = 0; i < b. atoms.length; ++i)
			if ( Atom.bump(a, b. atoms[i]))
				return true ;
		return false ;
	}

	public static boolean bump ( Molecule2 a, Molecule2 b) {
		if (! Atom . bump (a.sphere , b.sphere ))
			return false ;
		for (int i = 0; i < a.atoms.length ; ++i)
			if ( bump (a. atoms [i], b))
				return true ;
		return false ;
	}
}		
		
		
class Test {
	public static void main ( String [] args ) {
		Atom o = new Atom (0, 0.4 , 0, Atom . O_RADIUS );
		Atom h1 = new Atom (0.76 , -0.19 , 0, Atom . H_RADIUS );
		Atom h2 = new Atom ( -0.76 , -0.19 , 0, Atom . H_RADIUS );
		Atom [] H2O = { o, h1 , h2 };
		@SuppressWarnings("unused")
		Molecule2 mol = new Molecule2 (H2O );
	}
}



