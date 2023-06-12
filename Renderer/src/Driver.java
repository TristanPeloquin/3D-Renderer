import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Driver extends Application{
	
	private static final int x = 1800, y = 1350;
	
	public static void main(String[] args) throws InterruptedException {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {

        WritableImage writableImage = new WritableImage(x, y);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        
        //[start] OBJECTS
        //Premade objects to render in the scene.
        Vector camera = new Vector(0f,0f,-1f);
        ImagePlane plane = new ImagePlane();
               
        Sphere sphere1 = new Sphere(new Vector(3f,2f,5), 1f, CustomColor.WHITE);
        Sphere sphere2 = new Sphere(new Vector(0f,0f,6), 1f, CustomColor.BLUE);
        Sphere sphere3 = new Sphere(new Vector(-2f,0f,8f), 1f, CustomColor.GREEN);
        Sphere sphere4 = new Sphere(new Vector(-5f,-2,7), 1, CustomColor.RED);
        Sphere sphere5 = new Sphere(new Vector(-3f,1,5), 1, new CustomColor(0f,1f,1f));
        Sphere sphere6 = new Sphere(new Vector(5f,-3,10), 1, new CustomColor(1f,0f,1f));
        Sphere sphere7 = new Sphere(new Vector(-1f,3,5), 1, new CustomColor(.5f,.3f,1f));
        Sphere sphere8 = new Sphere(new Vector(0f,-3,5), 1, new CustomColor(1f,.5f,0f));
        Sphere sphere9 = new Sphere(new Vector(-5f,-5,7.5f), 1, new CustomColor(0f,.5f,1f));
        Sphere[] spheres = {sphere1, sphere3, sphere2, sphere4, sphere5, sphere6, sphere7, sphere8, sphere9};
        
        Light light1 = new Light(new Vector(5, 5, 3));
        Light light2 = new Light(new Vector(-5,2, 2));
        Light[] lights = {light1, light2};
        
        float ambientIntensity = .2f;
        
        //[end] OBJECTS
        
        //[start] RENDERER
        //These loops iterate through each pixel in the given screen space.
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                CustomColor shader = CustomColor.BLACK;
                
                //alpha and beta represent the % x or y we are on the screen.
            	float alpha = (float)(i+1)/x;
            	float beta = (float)(j+1)/y;
            	
            	//These simply are variations of alpha and beta and give us multiple points to sample in a given pixel.
            	float varyAlpha = alpha + (((float)1/x)/2);
            	float varyBeta = beta + (((float)1/y)/2);
            	
            	float[] alphas = {alpha, varyAlpha};
            	float[] betas = {beta, varyBeta};
            	//These loops iterate through alphas and betas and allows us to anti-alias the image
            	//by sampling multiple pixels on the screen. Also known as 4x SSAA.
            	for(int a = 0; a<alphas.length; a++) {
            		for(int b = 0; b<betas.length; b++) {
                    	Vector point = plane.bilerp(alphas[a], betas[b]);
                    	Ray ray = new Ray(camera, point.subtract(camera));
                    	shader = shader.add(render(ray, camera, spheres, lights, ambientIntensity, 3));
            		}
            	}
            	//total allows us to average the sampled pixels to a single color.
            	float total = alphas.length+betas.length;
            	shader = shader.mult(new CustomColor(1/total,1/total,1/total));
            	pixelWriter.setColor(i,j,shader.toColor());
            }
        }
        //[end] RENDERER
        
        //[start] WINDOW
        //JavaFX to show the window.
        ImageView imageView = new ImageView(writableImage);
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, x, y);

        primaryStage.setScene(scene);
        primaryStage.setTitle("3D Renderer");
        primaryStage.show();
        //[end] WINDOW
    }

	//This method determines the color to shade a pixel - it is a method in order to attain recursive reflections.
	public CustomColor render(Ray ray, Vector camera, Sphere[] spheres, Light[] lights, float ambientIntensity, int reflectionDepth) {
		Vector intersectPoint;
		CustomColor shader = CustomColor.BLACK;
		//This loop iterates through all spheres in the scene in order to check if the given ray intersects with the sphere.
    	for(int k = 0; k<spheres.length; k++) {
    		float intersect = intersect(ray, spheres[k]);
    		//If the sphere intersects the ray;
    		if(intersect>=0) {
    			//Calculate the intersect point and the surface normal at that point
    			intersectPoint = ray.origin.add(ray.direction.scale(intersect));
    			Vector normal = (intersectPoint.subtract(spheres[k].center)).normalize();
    			
    			//These are the terms the rest of the current iteration will determine.
    			CustomColor ambient = CustomColor.BLACK;
				CustomColor diffuse = CustomColor.BLACK;
				CustomColor specular = CustomColor.BLACK;
				CustomColor reflect = CustomColor.BLACK;
				
				//This condition will recursively calculate the reflections of spheres to a certain depth by running this method
				//and adding up the colors from the spheres that are reflected.
				if(reflectionDepth>0) {
					//v represents the view vector.
					Vector v = (ray.direction.scale(-1)).normalize();
					//Uses the equation R = 2(N . V)N - V to calculate the reflectance vector.
					Vector reflectanceVector = (normal.scale(2*(normal.dot(v)))).subtract(v).normalize();
					
					//Makes a new ray going from the intersect towards the reflectance vector, and passes that back into this method.
					Ray newRay = new Ray(intersectPoint, reflectanceVector);
					reflect = reflect.add(render(newRay, camera, spheres, lights, ambientIntensity, reflectionDepth-1));
				}
				else {
					return CustomColor.BLACK;
				}
				
				//Iterates through all the lights in the scene to determine if its in shadow, the diffuse color, and the specular color.
    			for(int w = 0; w<lights.length; w++) {
    				//If its in shadow from this light, then don't account for this lights components.
    				if(isShadowed(spheres, lights[w], intersectPoint, k)) {
    					reflect = CustomColor.BLACK;
    					break;
    				}

    				Vector lightVector = ((lights[w].position).subtract(intersectPoint)).normalize();
    				
    				//Calculates the reflectance vector for the specular component using the equation: R = 2(N . L)N - L 
    				Vector specularVector = normal.scale(2*(lightVector.dot(normal))).subtract(lightVector);
    				
    				Vector view = camera.subtract(intersectPoint).normalize();
    				
    				//Uses the smoothstep function to smooth over the artifact that occurs at L . N = 0;
    				//most visible when two lights shade a sphere from opposite angles, creating an X shape.
    				float dot = smoothstep(0, 1,lightVector.dot(normal));
    				//If the dot product is at least 0 (or facing the outside of the sphere) then shade the diffuse component.
    				diffuse = dot>=0 ? diffuse.add((spheres[k].material.diffuse.scale(lights[w].diffuse)).scale(dot)) : diffuse;
    				
    				dot = view.dot(specularVector);
    				//If the dot product is at least 0, then scale the specular component by the equation (V . R)^shine
    				float scalar = dot>=0 ? (float)Math.pow(dot, spheres[k].material.shine) : 0;
    				specular = specular.add(spheres[k].material.specular.scale(lights[w].specular).scale(scalar));
    			}
    			//Finalizes the scaling of the components, adds them up, and clamps them between 0 and 1. 
    			ambient = spheres[k].material.ambient.scale(ambientIntensity);
    			reflect = spheres[k].material.reflect.mult(reflect);
    			shader = ambient.add(diffuse).add(specular).add(reflect);
    			shader.clamp();
    		}
    	}
    	return shader;
	}
	
	//This method casts a ray from the intersect point to find if another sphere occludes the intersect point from a light source.
	//Returns true if it is occluded.
	private boolean isShadowed(Sphere[] spheres, Light light, Vector point, int k) {
		for(Sphere s : spheres) {
			if(spheres[k].equals(s))
				continue;
			
			Ray shadowRay = new Ray(point, light.position.subtract(point));
			float shadowIntersect = intersect(shadowRay, s);
			
			if(shadowIntersect>0 && shadowIntersect<1) {
				return true;
			}
		}
		return false;
	}
	
	//This method finds whether or not the sphere intersects with the ray. 
	//Uses the quadratic equation on the formula (||d||^2)t^2 + 2(<c',d>)t + (||c'||^2 - r^2) = 0;
	//where c' = o - c.
	//Returns the smallest t (a.k.a the closest intersect point), or -1 if it does not intersect.
	private float intersect(Ray ray, Sphere sphere) {
		Vector temp = ray.origin.subtract(sphere.center);
		
		float a = ray.direction.dot(ray.direction);
		float b = 2*ray.direction.dot(temp);
		float c = temp.dot(temp)-(float)Math.pow(sphere.radius, 2);
		
		float discriminant = (float)Math.pow(b, 2) - (4*a*c);
		if(discriminant>=0) {
			float t1 = (-b+(float)Math.sqrt(discriminant))/(2*a);
			float t2 = (-b-(float)Math.sqrt(discriminant))/(2*a);
			if(t1<=t2)
				return t1;
			return t2;
		}
		return -1;
	}
	
	private static float smoothstep(float edge0, float edge1, float x) {
	    float t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
	    return t * t * (3.0f - 2.0f * t);
	}

	private static float clamp(float value, float min, float max) {
	    return Math.max(min, Math.min(max, value));
	}
	
}
