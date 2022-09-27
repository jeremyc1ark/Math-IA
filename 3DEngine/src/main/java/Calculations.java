/*
 * Contains all of the helper functions that actually calculate the
 * transformation between 3D and 2D.
 * 
 * @author Jeremy Clark
 * @version September 19, 2022
 */
package main.java;

import java.awt.Frame;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public final class Calculations {
	
	/*
	 * To write an equation for our camera's line of sight, we need some way of determining its
	 * "slope" on all three of the axes. To find the slope, you need to find two points on the line. We
	 * already have one point (the coordinates of the camera itself), so we just need to find another point.
	 * We can use the orientation vector to find a unit vector that is parallel to the camera's line of
	 * sight, then add that vector to the camera's coordinates, which will give us the coordinates of another
	 * point on the line with a distance of 1.0 from the coordinates of the camera.
	 * 
	 * @param cameraCoords the coordinates of the camera. 
	 * @param orientation the orientation of the camera.
	 * @return a point on the camera's line of sight that has a distance of 1 from the coordinates of the camera.
	 */
	public static RealVector findPointOnLineOfSight (RealVector cameraCoords, Orientation orientation) {
		double yaw = orientation.getYaw();
		double pitch = orientation.getPitch();
		double[][] yawRotationMatrixData = {
				{Math.cos(yaw), 0., Math.sin(yaw) * -1},
				{0., 1., 0.},
				{Math.sin(yaw), 0., Math.cos(yaw)}
		};
		double[][] pitchRotationMatrixData = {
				{1., 0., 0.},
				{0., Math.cos(pitch), Math.sin(pitch) * -1},
				{0., Math.sin(pitch), Math.cos(pitch)}
		};

		RealMatrix yawRotationMatrix = MatrixUtils.createRealMatrix(yawRotationMatrixData);
		RealMatrix pitchRotationMatrix = MatrixUtils.createRealMatrix(pitchRotationMatrixData);
		double[] unrotatedUnitVecData = {0., 0., 1.};
		RealVector unrotatedUnitVec = new ArrayRealVector(unrotatedUnitVecData, false);
		
		// The line below is basically equivalent to:
		// unrotatedUnitVec * pitchRotationMatrix * yawRotationMatrix + cameraCoords
		RealVector pointOnLineOfSight = yawRotationMatrix.operate(pitchRotationMatrix.operate(unrotatedUnitVec)).add(cameraCoords);
		
		return pointOnLineOfSight;
	}

	/*
	 * Given the coordinates of the camera and a point on the camera's line of sight that has a distance
	 * of 1 from the coordinates of the camera, return a unit vector that is parallel to the camera's line
	 * of sight by finding the difference between these two points.
	 * 
	 * @param pointOnLineOfSight a point on the camera's line of sight that has a distance of 1 from
	 * the coordinates of the camera
	 * @param cameraCoords the coordinates of the camera
	 * @return a unit vector that is parallel to the camera's line of sight
	 */
	public static RealVector findLineOfSightUnitVec (RealVector pointOnLineOfSight, RealVector cameraCoords) {
		RealVector unitVec = pointOnLineOfSight.subtract(cameraCoords);
		return unitVec;
	}
	/*
	 * This method returns a method that, given some distance along the camera's line of sight, returns
	 * the coordinates corresponding to that distance.
	 * 
	 * @param cameraCoords the coordinates of the camera.
	 * @param pointOnLineOfSight a point on the camera's line of sight that is 1 unit away from the camera.
	 * and on the camera's line of sight.
	 * @return a method for the camera's line of sight.
	 */
	public static RealVector lineOfSight (double a, RealVector cameraCoords, RealVector lineOfSightUnitVec) {
		return lineOfSightUnitVec.mapMultiply(a).add(cameraCoords);
	}

	/*
	 * Given a target point, the coordinates of the camera and a point on the line that is one unit away from
	 * the camera, this method returns an 'a' value such that cam(a) produces a point that can share a plane
	 * that is orthogonal to the camera's line of sight with the target point. Cam(a) is some function produced
	 * by lineOfSightGenerator, where a is the independent variable and the values of the xyz axis are the
	 * dependent variables.
	 * 
	 * @param camCoords the coordinates of the camera.
	 * @param targetPoint the point which we are trying to convert to 2D.
	 * @param pointOnLineOfSight a point on the camera's line of sight that is 1 unit away from the camera.
	 * @return an 'a' value such that cam(a) produces a point that can share a plane that is orthogonal to the
	 * camera's line of sight with the target point.
	 */
	public static double getA (RealVector camCoords, RealVector targetPoint, RealVector unitVector) {
		return (targetPoint.dotProduct(unitVector) - camCoords.getEntry(0) - camCoords.getL1Norm()) / unitVector.getL1Norm();
	}
	
	/*
	 * At some distance away from the camera's origin point (a), this method determines the distance between
	 * the center of the frame to the corners using the aspect ratio of the 2D canvas. In 2D, the distance 
	 * between the center of the frame and the corners is constant, but in 3D, it changes depending on how
	 * far away you are from the camera. In 3D, the frame is a set of four points that share a plane which is
	 * orthogonal to the camera's line of sight, and which correspond to the camera's aspect ratio. This 
	 * essentially means that as you get farther from the camera, the frame gets bigger, which is why this
	 * function is necessary.
	 * 
	 * @param a the distance to the camera
	 * @param aspectRatio an object containing the width and height of the canvas in pixels
	 * @return the distance from the center of the frame to the corners at this particular `a` value
	 */
	public static double distanceFromTheCenterOfTheFrameToTheCorners (double a, double divergenceAngle) {
		return a * Math.tan(divergenceAngle);
	}
	
	/*
	 * Produces a function for a circle that contains all of the frame points for a 3D frame at a given
	 * distance away from the camera.
	 * 
	 * @param a the distance between the center of the circle and the camera
	 * @param firstOrthogonalUnitVector a unit vector that is orthogonal to the camera's line of sight
	 * @param secondOrthogonalUnitVector a unit vector is orthogonal to the camera's line of sight
	 * and the `firstOrthogonalUnitVector` 
	 * @param lineOfSight the camera's line of sight
	 * @param distanceToFrameCorners the radius of the circle
	 * @return a function for a circle that contains all of the frame points a 3D frame at a given distance
	 * away from the camera.
	 */
	public static Function<Double, RealVector> circleGenerator
	(
			double a,
			RealVector firstOrthogonalUnitVector,
			RealVector secondOrthogonalUnitVector,
			RealVector cameraCoords,
			RealVector lineOfSightUnitVec,
			double radius
	)
	{
		
		return (circleAngle) -> {
			RealVector centerOfCircle = lineOfSight(a, cameraCoords, lineOfSightUnitVec);
			
			RealVector circleAxisOne = firstOrthogonalUnitVector.mapMultiply(Math.sin(circleAngle));
			RealVector circleAxisTwo = secondOrthogonalUnitVector.mapMultiply(Math.cos(circleAngle));
			
			RealVector unitCircle = circleAxisOne.add(circleAxisTwo);
			RealVector scaledCircle = unitCircle.mapMultiply(radius);
			RealVector translatedCircle = scaledCircle.add(centerOfCircle);

			return translatedCircle;
		};
	}
	
	/*
	 * Returns the angle for a circle function that produces a point which forms a line with the center of
	 * the circle that is parallel to the plane formed by the XZ axes, and which, when viewed from the perspective
	 * of the camera, is to the right of the center of the circle.
	 * 
	 * @param firstOrthogonalUnitVector a unit vector that is orthogonal to the camera's line of sight
	 * @param secondOrthogonalUnitVector a unit vector that is orthogonal to the camera's line of sight
	 * and the `firstOrthogonalUnitVector`
	 * @return the angle for a circle function that produces a point which forms a line with the center of
	 * the circle that is parallel to the plane formed by the XZ axes, and which, when viewed from the perspective
	 * of the camera, is to the right of the center of the circle.
	 */
	public static double getInitialCircleAngleInRadians
	(
			RealVector firstOrthogonalUnitVector,
			RealVector secondOrthogonalUnitVector,
			Function<Double, RealVector> circle
	)
	{
		double optionOneInRadians = Math.atan(-1.0 * secondOrthogonalUnitVector.getEntry(1) / firstOrthogonalUnitVector.getEntry(1));
		double optionTwoInRadians = optionOneInRadians + Math.PI;

		double epsilon = 0.1;
		
		boolean optionOneIsOnTheRightHandSideOfTheFrame = (
				circle.apply(optionOneInRadians - epsilon).getEntry(1) < circle.apply(optionOneInRadians).getEntry(1)
				&& circle.apply(optionOneInRadians).getEntry(1) < circle.apply(optionOneInRadians + epsilon).getEntry(1)
				);
		
		if (optionOneIsOnTheRightHandSideOfTheFrame)
			return optionOneInRadians;
		else
			return optionTwoInRadians;
	}
	
	
	/*
	 * Returns the angle that you would need to rotate from the angle obtained by `getInitialCircleAngleInRadians`
	 * such that you would get the first frame point.
	 * 
	 * @param aspectRatio the aspect ratio of the camera in pixels.
	 * @return Returns the angle that you would need to rotate from the angle obtained by `getInitialCircleAngleInRadians`
	 * such that you would get the first frame point. 
	 */
	public static double getFirstFramePointAngleInRadians (AspectRatio aspectRatio) {
		return;
	}
	
	/*
	 * Returns the angle that you would need to rotate from the angle obtained by `getFirstFramePointAngleInRadians`
	 * such that you would get the second frame point.
	 * 
	 * @param yFrameAngleInRadians the angle that you would need to rotate from the angle obtained by `getInitialCircleAngleInRadians`
	 * such that you would get the first frame point.
	 * @return the angle that you would need to rotate from the angle obtained by `getFirstFramePointAngleInRadians`
	 * such that you would get the second frame point
	 */
	public static double getSecondFramePointAngleInRadians (double yFrameAngleInRadians) {
		return;
	}
	
	/*
	 * Returns a list containing the four angles which, when plugged into a predefined circle function, produce
	 * the four frame points.
	 * 
	 * @param orientation the orientation of the camera
	 * @param firstFramePointAngleInRadians angle that you would need to rotate from the angle obtained by `getInitialCircleAngleInRadians`
	 * such that you would get the first frame point
	 * @param secondFramePointInRadians angle that you would need to rotate from the angle obtained by `getFirstFramePointAngleInRadians`
	 * such that you would get the second frame point
	 * @param initialCircleAngleInRadians angle for a circle function that produces a point which forms a line with the center of
	 * the circle that is parallel to the plane formed by the XZ axes, and which, when viewed from the perspective
	 * of the camera, is to the right of the center of the circle
	 * @return a list containing the four angles which, when plugged into a predefined circle function, produce
	 * the four frame points
	 */
	public static double[] getFrameAngles (Orientation orientation, double firstFramePointAngleInRadians, double secondFramePointAngleInRadians, double initialCircleAngleInRadians) {
		return;
	}
	
	/*
	 * Returns a list containing the four frame points, starting from the top right and going clockwise.
	 * 
	 * @param lineOfSight the camera's line of sight
	 * @param frameAngles a list containing the four angles which, when plugged into a predefined circle function, produce
	 * the four frame points
	 * @return a list containing the four frame points.
	 */
	public static Frame getFramePoints (Method lineOfSight, double[] frameAngles) {
		return;
	}
	
	/*
	 * Returns the 3D vector representing the X-axis of the 2D canvas.
	 * 
	 * @param frame the frame of the camera in 3D
	 * @return the 3D vector representing the X-axis of the 2D canvas
	 */
	public static RealVector get3DXAxis (Frame frame) {
		return;
	}

	/*
	 * Returns the 3D vector representing the Y-axis of the 2D canvas.
	 * 
	 * @param frame the frame of the camera in 3D
	 * @return the 3D vector representing the Y-axis of the 2D canvas
	 */
	public static RealVector get3DYAxis (Frame frame) {
		return;
	}
	
	/*
	 * Returns what the target point would be if the bottom left corner of the frame became the origin of the 3D space
	 * 
	 * @param targetPoint the target point
	 * @param frame the frame of the camera in 3D
	 * @return a vector representing what the target point would be if the bottom left corner of the frame became
	 * the origin of the 3D space
	 */
	public static RealVector getPointRelativeToBottomLeftOfFrame (RealVector targetPoint, Frame frame) {
		return;
	}
	
	/*
	 * Returns the projection of the point relative to the bottom left of the frame onto one of the axes.
	 * You can think of the projection as the shadow cast by one vector onto another. Another way to think
	 * about it is how much of the same direction the two vectors share. Look it up if you're still confused.
	 * If the projection is greater than or equal to the magnitude of the axis, return 0 because this means
	 * that the point is not contained in the frame.
	 * 
	 * @param axis a vector representing one of the 2D axes.
	 * @param pointRelativeToBottomLeftOfFrame the target point relative to the bottom left of the frame.
	 * @return the projection of the point relative to the bottom left of the frame onto one of the axes.
	 */
	public static double axisProjection (RealVector axis, RealVector pointRelativeToBottomLeftOfFrame) {
		return;
	}
	
	/*
	 * Returns the ratio between the magnitude of an axis and the projection of the target point onto that axis.
	 * 
	 * @param pointRelativeToBottomLeftOfFrame the target point relative to the bottom left of the frame.
	 * @param axis a vector representing one of the 2D axes.
	 * @return the ratio between the magnitude of an axis and the projection of the target point onto that axis.
	 */
	public static double getRatioBetweenAxisMagnitudeAndPointProjection (RealVector pointRelativeToBottomLeftOfFrame, RealVector axis) {
		return;
	}
	
	/*
	 * Given a character specifying which axis we're working with, returns the number of pixels up that axis the
	 * point should be.
	 * 
	 * @param aspectRatio the aspect ratio of the camera
	 * @param ratio the ratio between the distance up the axis and and length of the axis
	 * @param axis a character representing which axis we are on
	 * @return the number of pixels up the axis the point should be
	 */
	public static int getAxisRealVectorInPixels (AspectRatio aspectRatio, double ratio, char axis) {
		return;
	}
	
	/*
	 * Given a point in 3D space, convert it into 2D space. Return the point in a format that is compatible
	 * with the axis system of the Java 2D GUI, with (0, 0) in the top left corner.
	 * 
	 * @param camera an object representing the variables associated with the camera
	 * @param targetPoint the point that we are converting from 3D to 2D
	 * @return a 2-element list of integers representing the X and Y coordinates, in pixels, of the target
	 * point on the 2D canvas
	 */
	public static int[] convert3DRealVectorTo2DRealVector (Camera camera, RealVector targetPoint) {
		return;
	}
}
