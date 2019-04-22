import java.util.ArrayList;

public class PointDouble {
	
	private double x;
	private double y; 
	
	public PointDouble (double x, double y) {
		this.x = x;
		this.y = y;
	}

	
	//return the euclidean distance between invoking point p and q
	public double distanceTo (PointDouble that) {
		double dx = this.x - that.x;
		double dy = this.y - that.y;
		return Math.round(Math.sqrt(dx*dx + dy*dy));
		
	}
	/**
	public void drawTo (PointDouble that ) {
		StdDraw.line(this.x, this.y, that.x, that.y);
	}
	**/
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public static void main(String[] args) {
		
		ArrayList<PointDouble> pointy = new ArrayList<PointDouble>();
		PointDouble p = new PointDouble (0.6, 0.2);
		System.out.println("p point: " + p);
		PointDouble q = new PointDouble (0.5, 0.5);
		System.out.println("q point: " + q);
		
		System.out.println("distance (p,q) = " + p.distanceTo(q));
		
		System.out.println(p.toString());
		pointy.add(p);
		pointy.add(q);
		System.out.println("size: " + pointy.size());
		System.out.println(pointy.toString());
		System.out.println(pointy.get(1));

		
	}
}
