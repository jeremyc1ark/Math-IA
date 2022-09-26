package main.java;

public final class AspectRatio {
	private int width;
	private int height;
	
	public AspectRatio (int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getHeight () {
		return this.height;
	}
	
	public int getWidth () {
		return this.width;
	}
}
