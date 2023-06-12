
public class Light {

	protected Vector position;
	protected float diffuse, specular;
	protected CustomColor color;
	
	public Light(Vector position) {
		this.position = position;
		diffuse = .6f;
		specular = .4f;
		color = CustomColor.WHITE;
	}
	
	public Light(Vector position, float diffuse, float specular, CustomColor color) {
		this.position = position;
		this.diffuse = diffuse;
		this.specular = specular;
		this.color = color;
	}
	
}
