
public class Material {
	
	protected CustomColor ambient, diffuse, specular, reflect;
	float shine;
	
	public Material(CustomColor ambient, CustomColor diffuse, CustomColor specular, float shine) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular; 
		reflect = CustomColor.BLACK;
		this.shine = shine;
	}
	
	public Material(CustomColor ambient, CustomColor diffuse, CustomColor specular, CustomColor reflect, float shine) {
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular; 
		this.reflect = reflect;
		this.shine = shine;
	}
	
}
