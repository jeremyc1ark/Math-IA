package main.tests;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import Jama.Matrix;
import main.java.Calculations;
import main.java.Coordinate;
import main.java.Orientation;

class CalculationsTest {
	
	/*
	 * If 90 degree yaw is applied, the point should be equal to ___
	 * If 90 degree pitch is applied, the point should be equal to ___
	 * If 45 degree yaw and pitch are applied, the point should be equal to ___
	 * If a random rotation is selected and another one is generated based off the original that is orthogonal to it, their dot products should be 0.
	 * Roll should not affect output
	 */
	
	private double generateRandomAngle () {
		double radiansInACircle = 2 * Math.PI;
		return Math.random() * radiansInACircle;
	}
	
	private Coordinate generateRandomCoordinate () {
		double x = Math.random() * 15 - 10;
		double y = Math.random() * 15 - 10;
		double z = Math.random() * 15 - 10;

		return new Coordinate (x, y, z);
	}
	
	private Orientation generateRandomOrientation () {
		double pitch = generateRandomAngle();
		double yaw = generateRandomAngle();
		double roll = generateRandomAngle();

		return new Orientation (pitch, yaw, roll);
	}
	
	
	private boolean doublesEqualWithinTolerance (double firstDouble, double secondDouble, double tolerance) {
		return (Math.abs(firstDouble - secondDouble) < tolerance);
	}
	
	private boolean coordinatesAreEqualWithinTolerance (Coordinate firstCoordinate, Coordinate secondCoordinate, double tolerance) {
		double x1 = firstCoordinate.getX();
		double y1 = firstCoordinate.getY();
		double z1 = firstCoordinate.getZ();
		
		double x2 = secondCoordinate.getX();
		double y2 = secondCoordinate.getY();
		double z2 = secondCoordinate.getZ();
		
		return (doublesEqualWithinTolerance(x1, x2, 0.01)
				&& doublesEqualWithinTolerance(y1, y2, 0.01)
				&& doublesEqualWithinTolerance(z1, z2, 0.01));

	}
	
	@RepeatedTest(10)
	void findPointOnLineOfSight_noRotationApplied () {
		Orientation orientation = new Orientation(0., 0., 0.);
		Coordinate cameraCoords = generateRandomCoordinate();
		Coordinate point = Calculations.findPointOnLineOfSight(cameraCoords, orientation);
		double pointZCoordinate = point.getZ();
		Coordinate shouldBeEqualToCameraCoords = new Coordinate (cameraCoords.getX(), cameraCoords.getY(), pointZCoordinate - 1);

		assertTrue(coordinatesAreEqualWithinTolerance(
				cameraCoords,
				shouldBeEqualToCameraCoords,
				0.01));
	}
	
	@RepeatedTest(10)
	void findPointOnLineOfSight_distanceIsOne () {
		Orientation orientation = generateRandomOrientation();
		Coordinate cameraCoords = generateRandomCoordinate();
		Coordinate point = Calculations.findPointOnLineOfSight(cameraCoords, orientation);

		Matrix difference = cameraCoords.getCoordinate().minus(point.getCoordinate());
		double norm = difference.norm1();

		assertEquals(norm, 1.0, 0.01);
	}

	@RepeatedTest(10)
	void findPointOnLineOfSight_rollHasNoEffect () {
		double pitch = generateRandomAngle();
		double yaw = generateRandomAngle();
		double rollOne = generateRandomAngle();
		double rollTwo = generateRandomAngle();
		
		Orientation orientationOne = new Orientation(pitch, yaw, rollOne);
		Orientation orientationTwo = new Orientation(pitch, yaw, rollTwo);
		
		Coordinate cameraCoords = generateRandomCoordinate();
		Coordinate pointOne = Calculations.findPointOnLineOfSight(cameraCoords, orientationOne);
		Coordinate pointTwo = Calculations.findPointOnLineOfSight(cameraCoords, orientationTwo);
		
		assertTrue(coordinatesAreEqualWithinTolerance(pointOne, pointTwo, 0.01));
	}
	
	@RepeatedTest(10)
	void findPointOnLineOfSight_orientationDeltaPitchIsAccurate () {
		double quarterRotation = Math.PI / 2;
		double pitchOne = generateRandomAngle();
		double pitchTwo = pitchOne + quarterRotation;
		double pitchThree = pitchOne - quarterRotation;
		double yaw = generateRandomAngle();
		double roll = generateRandomAngle();

		Orientation orientationOne = new Orientation(pitchOne, yaw, roll);
		Orientation orientationTwo = new Orientation(pitchTwo, yaw, roll);
		Orientation orientationThree = new Orientation(pitchThree, yaw, roll);
		
		Coordinate cameraCoords = generateRandomCoordinate();
		
		Coordinate pointOne = Calculations.findPointOnLineOfSight(cameraCoords, orientationOne);
		Coordinate pointTwo = Calculations.findPointOnLineOfSight(cameraCoords, orientationTwo);
		Coordinate pointThree = Calculations.findPointOnLineOfSight(cameraCoords, orientationThree);
		
		
	}
}