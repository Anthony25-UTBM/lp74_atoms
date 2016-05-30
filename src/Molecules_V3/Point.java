package Molecules_V3;

public class Point {
	double x, y, z;
	public Point(double x0 , double y0, double z0) {
		this.x = x0;
		this.y = y0;
		this.z = z0;
	}
	static double sqr(double x) { return x*x; }
	public static double distance(Point p, Point q) {
		return Math.sqrt(sqr(q.x - p.x) + sqr(q.y - p.y) + sqr(q.z - p.z));
	}
	public static Point add(Point p, Point q) {
		return new Point (p.x + q.x, p.y + q.y, p.z + q.z);
	}
	public static void scale(Point p, double k) {
		p.x *= k;
		p.y *= k;
		p.z *= k;
	}
}