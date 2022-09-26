/*
 * Helper functions for `circleGenerator` in the `Calculations` class.
 * 
 * @author Jeremy Clark
 * @version September 23, 2022
 */

package main.java;

import java.util.Vector;

import Jama.Matrix;

public class CircleHelpers {
	
	/*
	 * Given a point on the line of sight that is one unit away from the camera, this method
	 * returns the z-coordinate of a unit vector that is orthogonal to the camera's line of sight.
	 * 
	 * @param pointOnLineOfSight a point on the camera's line of sight that is one unit away from the camera
	 * @return the z-coordinate of a unit vector that is orthogonal to the camera's line of sight
	 */
	private static double getZCoordinateForFirstOrthogonalVector (Matrix pointOnLineOfSight) {
		return;
	}
	
	/*
	 * Given a the z-coordinate of a unit vector that is orthogonal to the camera's line of sight,
	 * this method completes the vector.
	 * 
	 * @param zCoordinateForFirstOrthogonalVector z-coordinate of a unit vector that is orthogonal
	 * to the camera's line of sight
	 * @return a vector that is orthogonal to the camera's line of sight.
	 */
	public static Vector<Double> getFirstOrthogonalVector (double zCoordinateForFirstOrthogonalVector) {
		return;
	}
	
	/*
	 * Given a unit vector that is parallel to the camera's line of sight and a unit vector that is orthogonal
	 * to the unit vector that is parallel to the camera's line of sight, this method returns a unit vector
	 * that is orthogonal to both of these unit vectors.
	 * 
	 * @param firstOrthogonalVector a unit vector that is orthogonal to the unit vector that is parallel to the 
	 * camera's line of sight.
	 * @param lineOfSightUnitVec the unit vector that is parallel to the camera's line of sight
	 * @return a vector that is orthogonal to both of the parameters
	 */
	public static Vector<Double> getSecondOrthogonalVector (Vector<Double> firstOrthogonalVector, Vector<Double> lineOfSightUnitVec) {
		return;
	}

}
