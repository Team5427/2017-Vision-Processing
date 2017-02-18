package com.Team5427.res;

/**
 * Made to store all of the variables that will be accessed in multiple classes
 * and will not be changed very often. All measurements are in inches.
 *
 */
public class Config {

	///link for IP Camera M1013 https://www.axis.com/files/manuals/um_axis_M1013_63352_en_1509.pdf
	//This is the real one 
	
	//vals for auto shoot
	public static final double SHOOT_MIN_SLOPE=-1;
	public static final double SHOOT_MAX_SLOPE=-1;

	/**
	 * Number of targets, will be used if we use the same code for another game
	 */
	public static final int NUM_OF_TARGETS = 2;

	public static final String NAME = "Team5427RoboCode";

	public static final boolean DEBUG_MODE = false; // displays 'Log.debug' in
													// console
	public static final boolean LOGGING = true;

	public static final boolean BYTE_SENDER_THREAD_ENABLE = true;

	public static final String DEFAULT_CAMERA_IP = "http://10.54.27.13/mjpg/video.mjpg";

	
	
	/**
	 * Determines whether or not the user will be able to calibrate the camera
	 * from the GraphicsPanel.
	 */
	public static final boolean ENABLE_FOV_CALIBRATION = false;
	/**
	 * Horizontal FOV of the attached Camera
	 */
//	 public static double horizontalFOV = 55.689320368051696;
	public static final double horizontalFOV = 67;
//	public static double horizontalFOV = 75;
	/**
	 * Vertical FOV of the attached Camera
	 */
	public static final double verticalFOV = 51;
	/**
	 * Angle at which the camera is mounted on the robot, in degrees.
	 */
	public static double CAMERA_START_ANGLE = 0;//19.5;

	
	/**
	 * Actual height of the tapes.
	 */

//	public static final double TOP_TAPE_HEIGHT = 4;
//	public static final double BOTTOM_TAPE_HEIGHT = 2;
	
	/**
	 * Dimensions of Resolution of Camera
	 */
	public static final int RESOLUTION_WIDTH = 640;
	public static final int RESOLUTION_HEIGHT = 480;

	/**
	 *  center coordinates on image so that we are lined up to shoot properly
	 */
	public static final double ALIGNED_CENTER_X=0;
	public static final double ALIGNED_CENTER_Y=0;	

	
	/**
	 * Elevation of the camera from the carpet, at the point where it is
	 * attached to the robot.
	 */
	public static final double ROBOT_HEIGHT = 27;
	/**
	 * Elevation of the goal from the bottom of the reflective tape to the
	 * carpet.
	 */
	public static final double TARGET_HEIGHT_TOP = 88;//88;
	public static final double TARGET_HEIGHT_BOTTOM = 80;//80;
	/**Values for the equations for the theta vs. pixels line*/
	private static final double ACTUAL_DISTANCE_X_1=133;
	private static final double ACTUAL_DISTANCE_X_2=104;//TODO 2nd data point
	private static final double TOP_THETA_1=Math.atan((TARGET_HEIGHT_TOP-ROBOT_HEIGHT)/ACTUAL_DISTANCE_X_1);
	private static final double TOP_THETA_2=Math.atan((TARGET_HEIGHT_TOP-ROBOT_HEIGHT)/ACTUAL_DISTANCE_X_2);
	private static final double BOTTOM_THETA_1=Math.atan((TARGET_HEIGHT_BOTTOM-ROBOT_HEIGHT)/ACTUAL_DISTANCE_X_1);
	private static final double BOTTOM_THETA_2=Math.atan((TARGET_HEIGHT_BOTTOM-ROBOT_HEIGHT)/ACTUAL_DISTANCE_X_2);
	private static final double TOP_PEAK_1=218;
	private static final double TOP_PEAK_2=147;
	private static final double BOTTOM_PEAK_1=253;
	private static final double BOTTOM_PEAK_2=171;
	private static final double TOP_PIXELS_1=Math.abs(RESOLUTION_HEIGHT/2-TOP_PEAK_1);
	private static final double TOP_PIXELS_2=Math.abs(RESOLUTION_HEIGHT/2-TOP_PEAK_2);
	private static final double BOTTOM_PIXELS_1=Math.abs(RESOLUTION_HEIGHT/2-BOTTOM_PEAK_1);
	private static final double BOTTOM_PIXELS_2=Math.abs(RESOLUTION_HEIGHT/2-BOTTOM_PEAK_2);

	//TODO make sure we align left and right first, then distance

	/**Distance Equation Values*/
	public static final double SLOPE_TOP_EQN= (TOP_THETA_2-TOP_THETA_1)/(TOP_PIXELS_2-TOP_PIXELS_1);
	public static final double INTERCEPT_TOP_EQN= -TOP_PIXELS_1*SLOPE_TOP_EQN+TOP_THETA_1;
	public static final double SLOPE_BOTTOM_EQN= (BOTTOM_THETA_2-BOTTOM_THETA_1)/(BOTTOM_PIXELS_2-BOTTOM_PIXELS_1);
	public static final double INTERCEPT_BOTTOM_EQN= -BOTTOM_PIXELS_1*SLOPE_BOTTOM_EQN+BOTTOM_THETA_1;
	

	/**

	 * The distance between the center of the turret to the camera
	 */
	public static final double CAMERA_TURRET_DISTANCE = 5;

	/** ------------Repaint Thread------------**/
	// Config for VisionPanel thread repainting
	public static final double MAX_FPS = 60f;

	/**
	 * Amount of time in autonomous in seconds
	 */
	public static final long AUTO_TIME = 15;

	/**
	 * Amount of time in teleop in seconds
	 */

	public static final long TELEOP_TIME = 135;

	/** -----------------Legacy Code----------------- **/

	/**
	 * Actual height of the goal.
	 */
	public static final double TRUE_GOAL_HEIGHT = 14;

	/**
	 * Elevation of the goal from the bottom of the reflective tape to the
	 * carpet.
	 */
	public static final double TOWER_HEIGHT = 85;
}
