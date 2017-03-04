package com.Team5427.res;

/**
 * Made to store all of the variables that will be accessed in multiple classes
 * and will not be changed very often. All measurements are in inches.
 *
 */
public class Config {

	/**---------IMPORTANT SOFTWARECONSTANTS----------*/
	//Values that we need for interaction with other programs, or for the GraphicsPanel
	/**link for IP Camera M1013 https://www.axis.com/files/manuals/um_axis_M1013_63352_en_1509.pdf*/
	//This is the real IP camera address one
	//public static final String DEFAULT_CAMERA_IP = "http://10.54.27.13/mjpg/video.mjpg";
	public static final String DEFAULT_CAMERA_IP = "http://169.254.101.224/mjpg/video.mjpg";
	/** Number of targets, will be used if we use the same code for another game*/
	public static final int NUM_OF_TARGETS = 2;
	public static final String NAME = "Team5427RoboCode";
	public static final boolean DEBUG_MODE = false; // displays 'Log.debug' in console
	public static final boolean LOGGING = true;
	public static final boolean BYTE_SENDER_THREAD_ENABLE = true;	
	
	/**----------PERMANENT CONSTANTS----------*/
	//distances in inches for field distances or distances on robot
	/** Elevation of the goal from the top of the reflective tape to the carpet.*/
	public static final double TARGET_HEIGHT_TOP = 88;// from ground to top of top tape;
	public static final double TARGET_HEIGHT_BOTTOM = 80;//80; from ground to top of bottom tape
	/** Elevation of the camera from the carpet, at the point where it is attached to the robot.*/
	public static final double ROBOT_HEIGHT = 28.5;
	/**Angle at which the camera is mounted on the robot, in degrees.*/
	public static double CAMERA_START_ANGLE = 11.5;//30
		
	/*----------CAMERA VIDEO VALUES----------*/
	//Values of FOV, or used for calibrating FOV,or Camera resolution
	/** Horizontal FOV of the attached Camera*/
	public static final double HORIZONTAL_FOV = 67;
	//public static double horizontalFOV = 55.689320368051696;
	//public static double horizontalFOV = 75;
	/**Vertical FOV of the attached Camera */
	public static final double VERTICAL_FOV = 51;
	//public static final double verticalFOV = 42.296048;
	/** Determines whether or not the user will be able to calibrate the camera
	 * from the GraphicsPanel.*/
	public static final boolean ENABLE_FOV_CALIBRATION = false;
	/** Dimensions of Resolution of Camera*/
	public static final int RESOLUTION_WIDTH = 640;
	public static final int RESOLUTION_HEIGHT = 480;
	
	/*----------FILTERING CONTOURS----------*/
	//Values used in Main to filter contours
	/**Tolerated Difference btw. CenterX's of Correct Tape Contours**/
	public static final int TAPE_DIF_CENTER_X=5;
	/**Tolerated Difference btw.Widths of Correct Tape Contours**/
	public static final int TAPE_DIF_WIDTH=5;
	
	/** ------------Repaint Thread------------**/
	// Config for VisionPanel thread repainting
	public static final double MAX_FPS = 60f;
	/**Amount of time in autonomous in seconds */
	public static final long AUTO_TIME = 15;
	/** Amount of time in teleop in seconds*/
	public static final long TELEOP_TIME = 135;
	
	/**-----------PAINTING OF GUIDELINES----------*/
	//Values used for the range lines that print to screen
	/**coordinates on image so that we are lined up to shoot properly*/
	public static final int ALIGNED_LEFT_X=200;
	public static final int ALIGNED_RIGHT_X=500;
	/**max and min distances for shooting*/
	public static final double MAX_SHOOTING_DISTANCE=100;
	public static final double MIN_SHOOTING_DISTANCE=80;
	public static final int LOWEST_SHOOT_LINE =getPixelHeight(MIN_SHOOTING_DISTANCE,TARGET_HEIGHT_TOP-ROBOT_HEIGHT);
	public static final int HIGHEST_SHOOT_LINE =getPixelHeight(MAX_SHOOTING_DISTANCE,TARGET_HEIGHT_BOTTOM-ROBOT_HEIGHT);
	/**takes a horizontal distance and a vertical distance and returns the y-value for a pixel that would show
	 * up on the camera for that distance and height
	 * @param horizontalDistance the distance in inches to the robot, horizontally from the goal
	 * @param height the true height, in inches, of the goal you are aiming for
	 * @return the pixelY-value for the given horizontalDistance
	 */
	public static int getPixelHeight(double horizontalDistance, double height)
	{
		double theta = Math.abs(Math.atan(height/horizontalDistance));
		double addedAngle=Math.toDegrees(theta)-CAMERA_START_ANGLE;
		double ratio= addedAngle/((VERTICAL_FOV)/2);
		double pixelHeight= ratio*(RESOLUTION_HEIGHT/2);
		return (int)(Math.round(Math.abs(RESOLUTION_HEIGHT/2-pixelHeight)));
	}
}