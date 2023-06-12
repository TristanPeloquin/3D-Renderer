
public class ImagePlane {
	
	private Vector x1, x2, x3, x4;
	
	public ImagePlane() {
		x1 = new Vector(-1, .75f, 0);
		x2 = new Vector(1, .75f, 0);
		x3 = new Vector(-1, -.75f, 0);
		x4 = new Vector(1, -.75f, 0);
	}
	
	public ImagePlane(Vector x1, Vector x2, Vector x3, Vector x4) {
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
		this.x4 = x4;
	}
	
	public Vector bilerp(float alpha, float beta) {
		Vector t = (x1.scale(1-alpha)).add(x2.scale(alpha));
		Vector b = (x3.scale(1-alpha)).add(x4.scale(alpha));
		Vector p = (t.scale(1-beta)).add(b.scale(beta));
		return p;
	}
	
}
