
public class Sphere {
	
	protected Vector center;
	protected float radius;
	protected Material material;
	
	//This constructor simply gives the sphere a "default" material.
	public Sphere(Vector center, float radius) {
		this.center = center;
		this.radius = radius;
		material = new Material(new CustomColor(.5f,.5f,.5f),CustomColor.WHITE,CustomColor.WHITE,20f);
	}
	
	//This constructor allows us to give the overall diffuse color of the sphere without touching the rest of the material properties.
	public Sphere(Vector center, float radius, CustomColor diffuse) {
		this.center = center;
		this.radius = radius;
		material = new Material(new CustomColor(.5f,.5f,.5f), diffuse, CustomColor.WHITE, new CustomColor(.3f,.3f,.3f), 30f);
	}
	
	public Sphere(Vector center, float radius, Material material) {
		this.center = center;
		this.radius = radius;
		this.material = material;
	}
	
}
