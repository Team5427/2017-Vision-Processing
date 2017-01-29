//<<<<<<< HEAD
////package com.Team5427.VisionProcessing;
////
////import com.Team5427.res.Config;
////
////
////@SuppressWarnings("rawtypes")
////public class Goal implements Comparable {
////
////	//stores the center coordinates and height of a goal that we see in GRIP
////	private double centerX, centerY, height;
////	private double cameraDistanceToBottomOfTape=Double.MIN_VALUE;
////	private double robotHorizontalDistanceToGoal=Double.MIN_VALUE;
////	private double angleOfTapeTriangle=Double.MIN_VALUE;
////
////	public static final int MOVE_LEFT=-1, SPOT_ON=0, MOVE_RIGHT=1;
////	private int rangeStatus=Integer.MIN_VALUE;
////	public static final int MIN_DISTANCE = 50, MAX_DISTANCE = 100;  // not actual range, temp
////	public static final int MOVE_BACK=-1, MOVE_FORWARD=1;
////	private int distanceStatus = Integer.MIN_VALUE;
////	private boolean goalCompleted = false;
////
////	/**
////	 * Todo write comment
////	 */
////	public Goal(double centerX, double centerY, double height) {
////
////		this.centerX=centerX;
////		this.centerY=centerY;
////		//TODO either change the true goal height to pixels or convert GRIP height to inches
////		this.height=height;
////
////		setAngleOfTapeTriangle();
////
////		updateAngleStatus();
////		updateDistanceStatus();
////	}
////
////	public void setAngleOfTapeTriangle()
////	{
////		angleOfTapeTriangle=(height/Config.TRUE_GOAL_HEIGHT)*180;
////	}
////
////	/**
////	 * NOTE: This should not be used outside of the goal class
////	 *
////	 * Calculates the angle of the goal from the robot to the top of the goal.
////	 * This does not take in account the starting angle of the camera.
////	 *
////	 * @return Angle from the robot to the top of the camera as viewed by the
////	 *         camera in radians
////	 */
////	public void updateAngleStatus()
////	{
////		if(centerLine.getSlope()<Config.SHOOT_MIN_SLOPE)
////			rangeStatus= MOVE_LEFT;
////		else if(centerLine.getSlope()>Config.SHOOT_MAX_SLOPE)
////			rangeStatus= MOVE_RIGHT;
////		else if(centerLine.getSlope()<Config.SHOOT_MAX_SLOPE&&centerLine.getSlope()>Config.SHOOT_MIN_SLOPE)
////			rangeStatus=SPOT_ON;
////		else
////			rangeStatus=Integer.MIN_VALUE;
////	}
////
////	public int getAngleStatusInt() {
////		return rangeStatus;
////	}
////
////	public String getAngleStatus()
////	{
////		if(1==rangeStatus)
////			return "Move Right!";
////		else if(-1==rangeStatus)
////			return "Move Left!";
////		else if(0==rangeStatus)
////			return "Spot On!";
////		else
////			return "Scrub";
////	}
////
////
////	protected double getCameraAngleY() {
////		return Math.atan(
////				(GraphicsPanel.RESOLUTION.getHeight() / 2 - (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2)
////						/ GraphicsPanel.pixelsToGoal);
////	}
////
////	/**
////	 * Calculates the angle of elevation from the robot to the top of the goal.
////	 * It utilizes the vertical FOV in order to determine the angle.
////	 *
////	 * @return the angle from the camera mounted on the robot, to the top of the
////	 *         goal.
////	 */
////	public double getAngleOfElevation() {
////		/*
////		 * System.out.println((GraphicsPanel.RESOLUTION.getHeight() / 2 -
////		 * (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2));
////		 */
////
////		if (angleOfElevation == Double.MIN_VALUE)
////			angleOfElevation = Math
////					.atan((GraphicsPanel.RESOLUTION.getHeight() / 2
////							- (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2) / GraphicsPanel.pixelsToGoal)
////					+ Math.toRadians(Config.CAMERA_START_ANGLE);
////
////		return angleOfElevation;
////
////		/*
////		 * return Math .atan(((leftLine.getMidpointY() +
////		 * rightLine.getMidpointY()) / 2 - GraphicsPanel.RESOLUTION.getHeight() /
////		 * 2) / GraphicsPanel.pixelsToGoal) +
////		 * Math.toRadians(Config.CAMERA_START_ANGLE);
////		 */
////	}
////
////	/**
////	 *
////	 * Determines whether or not the current goal is inside of another goal
////	 * based on whether the left and right line of the current goal are within
////	 * the bounds of the left and right lines of the goal that was given to it
////	 * when called.
////	 *
////	 * @param g
////	 *            The goal which is potentially outside of the current goal.
////	 *
////	 * @return Whether or not the current goal is inside of the goal passed
////	 *         through the parameters.
////	 */
////	public boolean isInsideGoal(Goal g) {
////		if (g.leftLine.getSmallestX() > leftLine.getSmallestX()
////				&& g.rightLine.getLargestX() < rightLine.getLargestX()) {
////
////			return true;
////
////		}
////		return false;
////	}
////
////	/**
////	 * Returns the length between the pixel length of the top side of the goal
////	 *
////	 * @return pixel length of the the top side of the goal
////	 */
////	public double getTopLength() {
////		double x1 = leftLine.getTopPointX();
////		double y1 = leftLine.getTopPointY();
////		double x2 = rightLine.getTopPointX();
////		double y2 = rightLine.getTopPointY();
////
////		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
////	}
////
////	/**
////	 * Note: This should not be used outside of the goal class
////	 *
////	 * Gets the angle the robot has to aim in the x axis in radians
////	 *
////	 * @return x angle in radians from the center of the robot to the goal. A
////	 *         negative angle represents that the goal is to the left from the
////	 *         center of the robot. A positive angle represents that the goal is
////	 *         to the right from the center of the robot.
////	 */
////	protected double getCameraXAngle() {
////		if (cameraXAngle == Double.MIN_VALUE)
////			cameraXAngle = Math.atan((centerLine.getMidpointX() - GraphicsPanel.RESOLUTION.getWidth() / 2) / GraphicsPanel.pixelsToGoal);
////
////
////		return cameraXAngle;
////	}
////
////	/**
////	 * Returns the angle from the center of the turret to the goal in radians in
////	 * the x axis
////	 *
////	 * @return Returns the angle from the center of the turret to the goal in
////	 *         radians in the x axis
////	 */
////	public double getTurretXAngle() {
////		if (Config.CAMERA_TURRET_DISTANCE == 0) {
////			return getCameraXAngle();
////		} else if (turretXAngle == Double.MIN_VALUE) {
////			double B = Math.acos((Math.pow(getGoalDistanceCamera(), 2) - Math.pow(Config.CAMERA_TURRET_DISTANCE, 2)
////					- Math.pow(getGoalDistanceTurret(), 2))
////					/ (-2 * Config.CAMERA_TURRET_DISTANCE * getGoalDistanceTurret()));
////
////			turretXAngle = Math.PI / 2 - B;
////		}
////
////		return turretXAngle;
////	}
////
////	public boolean isComplete() {
////		return goalCompleted;
////	}
////
////	/**
////	 * Returns if the robot has to move in order to be in range of the goal
////	 *
////	 * @return if the robot has to move in order to be a in range of the goal
////	 */
////	public void updateDistanceStatus()	{
////		if(getTowerDistanceTurret() < MIN_DISTANCE)
////			distanceStatus = MOVE_BACK;
////		else if(getTowerDistanceTurret() > MAX_DISTANCE)
////			distanceStatus = MOVE_FORWARD;
////		else if(getTowerDistanceTurret() > MIN_DISTANCE && getTowerDistanceTurret() < MAX_DISTANCE)
////			distanceStatus = SPOT_ON;
////		else
////			distanceStatus = Integer.MIN_VALUE;
////	}
////
////	public int getDistanceStatusInt()	{
////		return distanceStatus;
////	}
////
////	public String getDistanceStatus()	{
////		if(MOVE_BACK==distanceStatus)
////			return "Back";
////		else if(MOVE_FORWARD==distanceStatus)
////			return "Foward";
////		else if(SPOT_ON==distanceStatus)
////			return "Spot On";
////		else
////			return "";
////	}
////	/**
////	 * Returns the time the robot has to move at ? speed
////	 * in order to be in the correct distance range to shoot
////	 *
////	 * @return Returns the time the robot has to move at ? speed
////	 *  in order to be in the correct distance range to shoot
////	 */
////	// we weren't sure what the speed should be
/////*	public double getDistanceStatusTime()	{
////		double distanceToTravel = 0;
////		double speed;
////
////		if(getDistanceStatusInt() == MOVE_FORWARD)	{
////			distanceToTravel = getTowerDistanceTurret() - MAX_DISTANCE;
////		} else if(getDistanceStatusInt() == MOVE_BACK)	{
////			distanceToTravel = MIN_DISTANCE - getTowerDistanceTurret();
////		}
////
////		return distanceToTravel / speed;
////
////	}*/
////	/**
////	 * Returns the distance between the turret and tower in inches
////	 *
////	 * @return the distance between the turret and tower in inches
////	 */
////	public double getTowerDistanceTurret()	{
////
////		if(turretDistanceToTower == Double.MIN_VALUE)	{
////			turretDistanceToTower = getGoalDistanceTurret() * Math.cos(getAngleOfElevation());
////		}
////
////		return turretDistanceToTower;
////	}
////
////	/**
////	 * Returns the distance between the turret to the center of the goal in
////	 * inches
////	 *
////	 * @return Returns the distance between the turret to the center of the goal
////	 *         in inches
////	 */
////	public double getGoalDistanceTurret() {
////		if (Config.CAMERA_TURRET_DISTANCE == 0) {
////			return getGoalDistanceCamera();
////		} else if (turretDistanceToGoal == Double.MIN_VALUE) {
////			turretDistanceToGoal = Math.sqrt(Math.pow(Config.CAMERA_TURRET_DISTANCE, 2)
////					+ Math.pow(getGoalDistanceCamera(), 2) - 2 * Config.CAMERA_TURRET_DISTANCE * getGoalDistanceCamera()
////							* Math.cos(getCameraXAngle() + Math.PI / 2));
////		}
////
////		return turretDistanceToGoal;
////	}
////
////	/**
////	 * NOTE: This should not be used outside of the goal class
////	 *
////	 * Returns the distance between the camera to the center of the goal in
////	 * inches
////	 *
////	 * @return Returns the camera between the robot to the center of the goal in
////	 *         inches
////	 */
////	protected double getGoalDistanceCamera() {
////
////		if (cameraDistanceToGoal == Double.MIN_VALUE)
////			cameraDistanceToGoal = (Config.TRUE_GOAL_HEIGHT + Config.TOWER_HEIGHT - Config.ROBOT_HEIGHT)
////					/ Math.sin(getAngleOfElevation());
////
////		return cameraDistanceToGoal;
////	}
////
////	// TODO make this print out values to make a new goalData.
////	public String toString() {
////		return null;
////
////	}
////
////	/**
////	 * used to compare the area of two goals to each other.
////	 *
////	 * @param o
////	 *            a goal to be compared to the current goal.
////	 *
////	 * @return 1 if the current goal is larger than the one given, 0 if it is
////	 *         not, and -1 if the object given is n ot an instance of a goal.
////	 */
////	@Override
////	public int compareTo(Object o) {
////		if (o instanceof Goal) {
////			if (area > ((Goal) o).getArea())
////				return 1;
////			else
////				return 0;
////		}
////		return -1;
////	}
////
////}
//=======
//package com.Team5427.VisionProcessing;
//
//import com.Team5427.res.Config;
//
//@SuppressWarnings("rawtypes")
//public class Goal implements Comparable {
//
//	//stores the center coordinates and height of a goal that we see in GRIP
//	private Line leftLine,rightLine;
//	private double centerX, centerY, height;
//	private double cameraDistanceToBottomOfTape=Double.MIN_VALUE;
//	private double robotHorizontalDistanceToGoal=Double.MIN_VALUE;
//	private double angleOfTapeTriangle=Double.MIN_VALUE;
//
//	public static final int MOVE_LEFT=-1, SPOT_ON=0, MOVE_RIGHT=1;
//	private int rangeStatus=Integer.MIN_VALUE;
//	public static final int MOVE_BACK=-1, MOVE_FORWARD=1;
//	private int distanceStatus = Integer.MIN_VALUE;
//	private boolean goalCompleted = false;
//
//	/**
//	 * Receives an Array of three lines, then determines which of the three
//	 * lines is horizontal line, sets it as the horizontal line, and then
//	 * proceeds to remove it from the Array. The remaining two lines then have
//	 * their X values compared in order to determine which of the remaining
//	 * lines is the left and which is the right. By the end of this constructor,
//	 * there is no longer an Array of lines, but instead the left, right, and
//	 * center lines are all set, in addition to the approximate area being
//	 * calculated.
//	 *
//	 * @param lines
//	 *            An array of three lines that will comprise the goal.
//	 */
//	public Goal(double centerX, double centerY, double height) {
//
//		this.centerX=centerX;
//		this.centerY=centerY;
//		//TODO either change the true goal height to pixels or convert GRIP height to inches
//		this.height=height;
//
//		setAngleOfTapeTriangle();
//
//		updateAngleStatus();
//		updateDistanceStatus();
//	}
//
//	public void setAngleOfTapeTriangle()
//	{
//		angleOfTapeTriangle=(height/Config.TRUE_GOAL_HEIGHT)*180;
//	}
//
//	/**
//	 * NOTE: This should not be used outside of the goal class
//	 *
//	 * Calculates the angle of the goal from the robot to the top of the goal.
//	 * This does not take in account the starting angle of the camera.
//	 *
//	 * @return Angle from the robot to the top of the camera as viewed by the
//	 *         camera in radians
//	 */
//	public void updateAngleStatus()
//	{
//		if(centerLine.getSlope()<Config.SHOOT_MIN_SLOPE)
//			rangeStatus= MOVE_LEFT;
//		else if(centerLine.getSlope()>Config.SHOOT_MAX_SLOPE)
//			rangeStatus= MOVE_RIGHT;
//		else if(centerLine.getSlope()<Config.SHOOT_MAX_SLOPE&&centerLine.getSlope()>Config.SHOOT_MIN_SLOPE)
//			rangeStatus=SPOT_ON;
//		else
//			rangeStatus=Integer.MIN_VALUE;
//	}
//
//	public int getAngleStatusInt() {
//		return rangeStatus;
//	}
//
//	public String getAngleStatus()
//	{
//		if(1==rangeStatus)
//			return "Move Right!";
//		else if(-1==rangeStatus)
//			return "Move Left!";
//		else if(0==rangeStatus)
//			return "Spot On!";
//		else
//			return "Scrub";
//	}
//
//	protected double getCameraAngleY() {
//		return Math.atan(
//				(GraphicsPanel.RESOLUTION.getHeight() / 2 - (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2)
//						/ GraphicsPanel.pixelsToGoal);
//	}
//
//	/**
//	 * Calculates the angle of elevation from the robot to the top of the goal.
//	 * It utilizes the vertical FOV in order to determine the angle.
//	 *
//	 * @return the angle from the camera mounted on the robot, to the top of the
//	 *         goal.
//	 */
//	public double getAngleOfElevation() {
//		/*
//		 * System.out.println((GraphicsPanel.RESOLUTION.getHeight() / 2 -
//		 * (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2));
//		 */
//
//		if (angleOfElevation == Double.MIN_VALUE)
//			angleOfElevation = Math
//					.atan((GraphicsPanel.RESOLUTION.getHeight() / 2
//							- (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2) / GraphicsPanel.pixelsToGoal)
//					+ Math.toRadians(Config.CAMERA_START_ANGLE);
//
//		return angleOfElevation;
//
//		/*
//		 * return Math .atan(((leftLine.getMidpointY() +
//		 * rightLine.getMidpointY()) / 2 - GraphicsPanel.RESOLUTION.getHeight() /
//		 * 2) / GraphicsPanel.pixelsToGoal) +
//		 * Math.toRadians(Config.CAMERA_START_ANGLE);
//		 */
//	}
//
//	/**
//	 *
//	 * Determines whether or not the current goal is inside of another goal
//	 * based on whether the left and right line of the current goal are within
//	 * the bounds of the left and right lines of the goal that was given to it
//	 * when called.
//	 *
//	 * @param g
//	 *            The goal which is potentially outside of the current goal.
//	 *
//	 * @return Whether or not the current goal is inside of the goal passed
//	 *         through the parameters.
//	 */
//	public boolean isInsideGoal(Goal g) {
//		if (g.leftLine.getSmallestX() > leftLine.getSmallestX()
//				&& g.rightLine.getLargestX() < rightLine.getLargestX()) {
//
//			return true;
//
//		}
//		return false;
//	}
//
//	/**
//	 * Returns the length between the pixel length of the top side of the goal
//	 *
//	 * @return pixel length of the the top side of the goal
//	 */
//	public double getTopLength() {
//		double x1 = leftLine.getTopPointX();
//		double y1 = leftLine.getTopPointY();
//		double x2 = rightLine.getTopPointX();
//		double y2 = rightLine.getTopPointY();
//
//		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
//	}
//
//	/**
//	 * Note: This should not be used outside of the goal class
//	 *
//	 * Gets the angle the robot has to aim in the x axis in radians
//	 *
//	 * @return x angle in radians from the center of the robot to the goal. A
//	 *         negative angle represents that the goal is to the left from the
//	 *         center of the robot. A positive angle represents that the goal is
//	 *         to the right from the center of the robot.
//	 */
//	protected double getCameraXAngle() {
//		if (cameraXAngle == Double.MIN_VALUE)
//			cameraXAngle = Math.atan((centerLine.getMidpointX() - GraphicsPanel.RESOLUTION.getWidth() / 2) / GraphicsPanel.pixelsToGoal);
//
//
//		return cameraXAngle;
//	}
//
//	/**
//	 * Returns the angle from the center of the turret to the goal in radians in
//	 * the x axis
//	 *
//	 * @return Returns the angle from the center of the turret to the goal in
//	 *         radians in the x axis
//	 */
//	public double getTurretXAngle() {
//		if (Config.CAMERA_TURRET_DISTANCE == 0) {
//			return getCameraXAngle();
//		} else if (turretXAngle == Double.MIN_VALUE) {
//			double B = Math.acos((Math.pow(getGoalDistanceCamera(), 2) - Math.pow(Config.CAMERA_TURRET_DISTANCE, 2)
//					- Math.pow(getGoalDistanceTurret(), 2))
//					/ (-2 * Config.CAMERA_TURRET_DISTANCE * getGoalDistanceTurret()));
//
//			turretXAngle = Math.PI / 2 - B;
//		}
//
//		return turretXAngle;
//	}
//
//	public boolean isComplete() {
//		return goalCompleted;
//	}
//
//	/**
//	 * Returns if the robot has to move in order to be in range of the goal
//	 *
//	 * @return if the robot has to move in order to be a in range of the goal
//	 */
//	public void updateDistanceStatus()	{
//		if(getTowerDistanceTurret() < Config.SHOOT_MIN_DISTANCE)
//			distanceStatus = MOVE_BACK;
//		else if(getTowerDistanceTurret() > Config.SHOOT_MAX_DISTANCE)
//			distanceStatus = MOVE_FORWARD;
//		else if(getTowerDistanceTurret() > Config.SHOOT_MIN_DISTANCE && getTowerDistanceTurret() < Config.SHOOT_MAX_DISTANCE)
//			distanceStatus = SPOT_ON;
//		else
//			distanceStatus = Integer.MIN_VALUE;
//	}
//
//	public int getDistanceStatusInt()	{
//		return distanceStatus;
//	}
//
//	public String getDistanceStatus()	{
//		if(MOVE_BACK==distanceStatus)
//			return "Back";
//		else if(MOVE_FORWARD==distanceStatus)
//			return "Foward";
//		else if(SPOT_ON==distanceStatus)
//			return "Spot On";
//		else
//			return "";
//	}
//	/**
//	 * Returns the time the robot has to move at ? speed
//	 * in order to be in the correct distance range to shoot
//	 *
//	 * @return Returns the time the robot has to move at ? speed
//	 *  in order to be in the correct distance range to shoot
//	 */
//	// we weren't sure what the speed should be
//	public double getDistanceStatusTime()	{
//		double distanceToTravel = 0;
//		double speed = Config.AUTO_DRIVE_SPEED;
//
//		if(getDistanceStatusInt() == MOVE_FORWARD)	{
//			distanceToTravel = getTowerDistanceTurret() - Config.SHOOT_MAX_DISTANCE;
//		} else if(getDistanceStatusInt() == MOVE_BACK)	{
//			distanceToTravel = Config.SHOOT_MIN_DISTANCE - getTowerDistanceTurret();
//		}
//
//		return distanceToTravel / speed;
//	}
//	/**
//	 * Returns the distance between the turret and tower in inches
//	 * 
//	 * @return the distance between the turret and tower in inches
//	 */
//	public double getTowerDistanceTurret()	{
//
//		if(turretDistanceToTower == Double.MIN_VALUE)	{
//			turretDistanceToTower = getGoalDistanceTurret() * Math.cos(getAngleOfElevation());
//		}
//
//		return turretDistanceToTower;
//	}
//
//	/**
//	 * Returns the distance between the turret to the center of the goal in
//	 * inches
//	 *
//	 * @return Returns the distance between the turret to the center of the goal
//	 *         in inches
//	 */
//	public double getGoalDistanceTurret() {
//		if (Config.CAMERA_TURRET_DISTANCE == 0) {
//			return getGoalDistanceCamera();
//		} else if (turretDistanceToGoal == Double.MIN_VALUE) {
//			turretDistanceToGoal = Math.sqrt(Math.pow(Config.CAMERA_TURRET_DISTANCE, 2)
//					+ Math.pow(getGoalDistanceCamera(), 2) - 2 * Config.CAMERA_TURRET_DISTANCE * getGoalDistanceCamera()
//							* Math.cos(getCameraXAngle() + Math.PI / 2));
//		}
//
//		return turretDistanceToGoal;
//	}
//
//	/**
//	 * NOTE: This should not be used outside of the goal class
//	 *
//	 * Returns the distance between the camera to the center of the goal in
//	 * inches
//	 *
//	 * @return Returns the camera between the robot to the center of the goal in
//	 *         inches
//	 */
//	protected double getGoalDistanceCamera() {
//
//		if (cameraDistanceToGoal == Double.MIN_VALUE)
//			cameraDistanceToGoal = (Config.TRUE_GOAL_HEIGHT + Config.TOWER_HEIGHT - Config.ROBOT_HEIGHT)
//					/ Math.sin(getAngleOfElevation());
//
//		return cameraDistanceToGoal;
//	}
//
//	// TODO make this print out values to make a new goalData.
//	public String toString() {
//		return null;
//
//	}
//
//	/**
//	 * used to compare the area of two goals to each other.
//	 *
//	 * @param o
//	 *            a goal to be compared to the current goal.
//	 *
//	 * @return 1 if the current goal is larger than the one given, 0 if it is
//	 *         not, and -1 if the object given is n ot an instance of a goal.
//	 */
//	@Override
//	public int compareTo(Object o) {
//		if (o instanceof Goal) {
//			if (area > ((Goal) o).getArea())
//				return 1;
//			else
//				return 0;
//		}
//		return -1;
//	}
//
//}
//>>>>>>> 054c86d52019088b571f11eaab329fd0735b5b49
