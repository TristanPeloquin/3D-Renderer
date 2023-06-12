import javafx.scene.paint.Color;
//A color represented by 3 floats in range [0,1]
public class CustomColor {
	
	protected float red, green, blue;
	
	public CustomColor(float r, float g, float b) {
		red = r;
		green = g;
		blue = b;
	}
	
	public Color toColor() {
		return Color.rgb((int)(red*255), (int)(green*255), (int)(blue*255));
	}
	
	public CustomColor scale(float a) {
		return new CustomColor(red*a, green*a, blue*a);
	}
	
	public CustomColor add(CustomColor a) {
		return new CustomColor(red+a.red, green+a.green, blue+a.blue);
	}
	
	public CustomColor mult(CustomColor a) {
		return new CustomColor(red*a.red, green*a.green, blue*a.blue);
	}
	
	public void clamp() {
		red = (red>1) ? 1 : red;
		red = (red<0) ? 0 : red;
		green = (green>1) ? 1 : green;
		green = (green<0) ? 0 : green;
		blue = (blue>1) ? 1 : blue;
		blue = (blue<0) ? 0 : blue;
	}
	
	public static final CustomColor BLACK = new CustomColor(0,0,0);
	public static final CustomColor RED = new CustomColor(1,0,0);
	public static final CustomColor GREEN = new CustomColor(0,1,0);
	public static final CustomColor BLUE = new CustomColor(0,0,1);
	public static final CustomColor WHITE = new CustomColor(1,1,1);
	
}
