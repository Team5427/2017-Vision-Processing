package com.Team5427.res;

/**
 * Made to store all of the variables that will be accessed in multiple classes
 * and will not be changed very often. All measurements are in inches.
 *
 */
public class Config {

	//vals for auto shoot
	public static final double SHOOT_MIN_SLOPE=-1;
	public static final double SHOOT_MAX_SLOPE=-1;

	
	
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
	// public static double horizontalFOV = 55.689320368051696;
	public static double horizontalFOV = 67;
	/**
	 * Vertical FOV of the attached Camera
	 */
	public static double verticalFOV = 45.39860400495973;
	/**
	 * Angle at which the camera is mounted on the robot, in degrees.
	 */
	public static double CAMERA_START_ANGLE = 19.5;
	
	/**
	 * Actual height of the tapes.
	 */
	public static final double TOP_TAPE_HEIGHT = 4;
	public static final double BOTTOM_TAPE_HEIGHT = 2;
	/**
	 *  center coordinates on image so that we are lined up to shoot properly
	 */
	public static final double ALIGNED_CENTER_X=0;
	public static final double ALIGNED_CENTER_Y=0;	
	/**
	 * Elevation of the goal from the bottom of the reflective tape to the
	 * carpet.
	 */
	public static final double TARGET_HEIGHT_TOP = 85;

	public static final double TARGET_HEIGHT_BOTTOM = 70;

	/**
	 * Elevation of the camera from the carpet, at the point where it is
	 * attached to the robot.
	 */
	public static final double ROBOT_HEIGHT = 14.75;

	/**
	 * The distance between the center of the turret to the camera
	 */
	public static final double CAMERA_TURRET_DISTANCE = 5;

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
