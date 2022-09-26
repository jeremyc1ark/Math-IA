/**
 * Create a simple GUI to see the transformation between 3D and 2D.
 * 
 * @author Jeremy Clark
 * @version September 19, 2022
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GUI extends JPanel implements Runnable{

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new GUI());
	}
	/*
	 * @param frame: The frame that we are collecting the input data from.
	 * @param labels: A list of labels for the dialog boxes. For example, "X Coordinate."
	 * @param messageCaption: A caption for the prompt.
	 * @param message: The prompt itself
	 * @return A list of doubles collected from the user.
	 */
	private double[] getInputData(JFrame frame, String[] labels, String messageCaption, String message) {

		// Display the input prompt.
		JOptionPane.showMessageDialog(frame, message, messageCaption, JOptionPane.QUESTION_MESSAGE);

		// Create an empty list to store the input.
		// The length of the list should be equal to the number of inputs we're getting.
		int labelListLength = labels.length;
		double inputDataset[] = new double[labelListLength];
	
		// For every input label...
		for (int x = 0; x < labelListLength; x++)
		{
			// Loops indefinitely until the user enters the proper data type.
			while (true) {
				try {
					// Get the user input, add it to `inputDataset` in the corresponding position.
					String datumAsString = JOptionPane.showInputDialog(frame, labels[x]);
					double datum = Double.parseDouble(datumAsString);
					inputDataset[x] = datum;
					break;
				}
				catch(Exception e) {
					// If the user enters anything other than a number, display this message.
					JOptionPane.showMessageDialog(null, "Error: You must enter a number.");
					continue;
				}
				
			}
		}
		return inputDataset;
	}
	
	/* Gets the dimensions of the user's screen in pixels,
	 * then ensures that the ratio between the size of the JFrame
	 * and the size of the screen remains constant regardless
	 * of what kind of screen the user is employing.
	 * 
	 * @param frame: The frame whose size we want to set.
	 */
	private void frameBoilerplate (JFrame frame) {
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		Dimension screenSizeInPixels = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidthInPixels = (int) screenSizeInPixels.getWidth();
		int screenHeightInPixels = (int) screenSizeInPixels.getHeight();
		double frameScalingFactor = (double) 5 / 8;
		int frameWidthInPixels = (int) (screenWidthInPixels * frameScalingFactor);
		int frameHeightInPixels = (int) (screenHeightInPixels * frameScalingFactor);
		frame.setSize(frameWidthInPixels, frameHeightInPixels);
	}
	
	public void run ()
	{
		// Create new frame, do boilerplate stuff.
		// Don't worry about it, it's abstracted ;)
		JFrame frame = new JFrame("GUI");
		frameBoilerplate(frame);

		// All of the strings that we use to prompt the user for input.
		String camCoordMessageCaption = "Camera Coordinates";
		String camCoordIntroduction = "Enter the coordinates of the camera in 3D space.";
		String[] camCoordInputLabels = { "X:", "Y:", "Z:"};
		
		String orientationMessageCaption = "Orientation";
		String orientationIntroduction = "Enter the rotation about each of the axes in degrees so that we can create an orientation unit vector.";
		String[] orientationInputLabels = {
				"Pitch - Rotation about the X axis:",
				"Yaw - Rotation about the Y axis:",
				"Roll - Rotation about the Z axis:"};
		
		String targetPointMessageCaption = "Target Point";
		String targetPointIntroduction = "Enter the 3D coordinates of the point that you would like to view.";
		String[] targetPointInputLabels = {"X:", "Y:", "Z:"};
		
		String aspectRatioMessageCaption = "Aspect Ratio";
		String aspectRatioIntroduction = "Enter the width and height of the 2D canvas in pixels.";
		String[] aspectRatioLabels = {
				"Width of the canvas in pixels: ",
				"Height of the canvas in pixels: "};

		// Collect user input, bind the input to variables.
		double[] camCoords = getInputData(frame, camCoordInputLabels, camCoordMessageCaption, camCoordIntroduction);
		double[] orientationInDegrees = getInputData(frame, orientationInputLabels, orientationMessageCaption, orientationIntroduction);
		double[] targetPoint = getInputData(frame, targetPointInputLabels, targetPointMessageCaption, targetPointIntroduction);
		// Technically this should be an integer rather than a double, but we need all the data types to be the same so that
		// they can be passed into our helper functions.
		double[] aspectRatioInPixels = getInputData(frame, aspectRatioLabels, aspectRatioMessageCaption, aspectRatioIntroduction);
	}
	
	public void paint(Graphics g)
	{
		int width = this.getWidth();
		int height = this.getHeight();
		int centerX = width / 2;
		int centerY = height / 2;

		// These will probably be user inputs eventually, but for now,
		// I'll just set them to something.
		
		Color backgroundColor = Color.WHITE;
		Color pointColor = Color.BLACK;
		int pointRadiusInPixels = width / 200;
		
		// Set the background color.
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		
		g.setColor(pointColor);
		g.fillOval(
				centerX - pointRadiusInPixels,
				centerY - pointRadiusInPixels, 
				pointRadiusInPixels * 2, 
				pointRadiusInPixels * 2
				);
		
	}

}
