package GeneticObjects;

/**
 * stores the cities as points of type doubles
 * @author achilles armendariz
 *
 */
public class City {
	
	private double x;
	private double y; 
	
	public City (double x, double y) {
		this.x = x;
		this.y = y;
	}

	
	//return the euclidean distance between invoking point p and q
	public double distanceTo (City that) {
		double dx = this.x - that.x;
		double dy = this.y - that.y;
		return Math.round(Math.sqrt(dx*dx + dy*dy));
		
	}
	
	
	//write points that connect to each other once they touch??
	
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	
}
