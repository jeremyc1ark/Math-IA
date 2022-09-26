package main.java;

public class Camera {
	public final AspectRatio aspectRatio;
	public final Orientation orientation;
	public final Coordinate camCoords;
	
	public Camera (AspectRatio aspectRatio, Orientation orientation, Coordinate camCoords) {
		this.aspectRatio = aspectRatio;
		this.orientation = orientation;
		this.camCoords = camCoords;
	}
	
}
