

public class Vector {
	
	protected float x,y,z;
	
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector add(Vector a) {
		return new Vector(a.x+x, a.y+y, a.z+z);
	}
	
	public Vector scale(float c) {
		return new Vector(x*c, y*c, z*c);
	}
	
	public Vector subtract(Vector a) {
		return new Vector(x-a.x, y-a.y, z-a.z);
	}
	
	public float dot(Vector a) {
		return (x*a.x)+(y*a.y)+(z*a.z);
	}
	
	public float norm() {
		return (float)Math.abs(Math.sqrt(this.dot(this)));
	}
	
	public Vector normalize() {
		float norm = this.norm();
		return new Vector(x/norm, y/norm, z/norm);
	}
	
}
