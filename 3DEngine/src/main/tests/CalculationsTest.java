package main.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.jupiter.api.RepeatedTest;

import main.java.Calculations;
import main.java.Orientation;

class CalculationsTest {
	
	
	// HELPERS
	
	private double generateRandomAngle () {
		double radiansInACircle = 2 * Math.PI;
		return Math.random() * radiansInACircle;
	}
	
	private RealVector generateRandomCoordinate () {
		double rangeMin = -10.;
		double rangeMax = 10.;
		Random r = new Random();
		double x = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		double y = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
		double z = rangeMin + (rangeMax - rangeMin) * r.nextDouble();

		double[] randomCoordinateRealVectorData = {x, y, z};
		RealVector randomCoordinate =  new ArrayRealVector (randomCoordinateRealVectorData, false);
		return randomCoordinate;
	}
	
	private Orientation generateRandomOrientation () {
		double pitch = generateRandomAngle();
		double yaw = generateRandomAngle();
		double roll = generateRandomAngle();

		return new Orientation (pitch, yaw, roll);
	}
	
	
	
	
	// FINDPOINTONLINEOFSIGHT

	@RepeatedTest(100)
	void findPointOnLineOfSight_noRotationApplied () {
		Orientation orientation = new Orientation(0., 0., 0.);
		RealVector cameraCoords = generateRandomCoordinate();
		RealVector point = Calculations.findPointOnLineOfSight(cameraCoords, orientation);
		double[] shouldBeEqualToCameraCoordsVectorData = {point.getEntry(0), point.getEntry(1), point.getEntry(2) - 1};
		RealVector shouldBeEqualToCameraCoords = new ArrayRealVector (shouldBeEqualToCameraCoordsVectorData, false);

		assertTrue(coordinatesAreEqualWithinTolerance(cameraCoords, shouldBeEqualToCameraCoords));
	}
	
	@RepeatedTest(100)
	void findPointOnLineOfSight_distanceIsOne () {
		Orientation orientation = generateRandomOrientation();
		RealVector cameraCoords = generateRandomCoordinate();
		RealVector point = Calculations.findPointOnLineOfSight(cameraCoords, orientation);

		RealVector difference = cameraCoords.subtract(point);
		double norm = difference.getL1Norm();

		assertEquals(norm, 1.0, 0.05);
	}

	@RepeatedTest(100)
	void findPointOnLineOfSight_rollHasNoEffect () {
		double pitch = generateRandomAngle();
		double yaw = generateRandomAngle();
		double rollOne = generateRandomAngle();
		double rollTwo = generateRandomAngle();
		
		Orientation orientationOne = new Orientation(pitch, yaw, rollOne);
		Orientation orientationTwo = new Orientation(pitch, yaw, rollTwo);
		
		RealVector cameraCoords = generateRandomCoordinate();
		RealVector pointOne = Calculations.findPointOnLineOfSight(cameraCoords, orientationOne);
		RealVector pointTwo = Calculations.findPointOnLineOfSight(cameraCoords, orientationTwo);
		
		assertTrue(coordinatesAreEqualWithinTolerance(pointOne, pointTwo));
	}
	
	@RepeatedTest(100)
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
		
		RealVector cameraCoords = generateRandomCoordinate();
		
		RealVector pointOne = Calculations.findPointOnLineOfSight(cameraCoords, orientationOne);
		RealVector pointTwo = Calculations.findPointOnLineOfSight(cameraCoords, orientationTwo);
		RealVector pointThree = Calculations.findPointOnLineOfSight(cameraCoords, orientationThree);
		
		assertEquals(pointOne.dotProduct(pointTwo), 0., 0.01);
		assertEquals(pointOne.dotProduct(pointThree), 0., 0.01);
	}
	
	
	@RepeatedTest(100)
	void findPointOnLineOfSight_45DegreeOrientationWorks () {
		double fortyFiveDegreeAngleInRadians = Math.PI / 4;
		double pitch, yaw;
		pitch = yaw = fortyFiveDegreeAngleInRadians;

		
		Orientation orientation = new Orientation(pitch, yaw, 0.);
		RealVector cameraCoords = generateRandomCoordinate();
		RealVector point = Calculations.findPointOnLineOfSight(cameraCoords, orientation);
		RealVector fortyFiveDegreeUnitVector = new ArrayRealVector (3, Math.sqrt(1.0 / 3.0));
		
		RealVector difference = point.subtract(cameraCoords);
		
		assertTrue(coordinatesAreEqualWithinTolerance(difference, fortyFiveDegreeUnitVector));
	}
	
	// 
}






