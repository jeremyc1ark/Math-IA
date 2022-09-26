package main.java;

public final class Orientation {
	private double yaw;
	private double pitch;
	private double roll;
	
	public Orientation (double yaw, double pitch, double roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}
	
	public double getYaw () {
		return this.yaw;
	}
	
	public double getPitch () {
		return this.pitch;
	}
	
	public double getRoll() {
		return this.roll;
	}
}
