package com.Team5427.VisionProcessing;

import com.Team5427.res.Config;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by Charlemagne Wong on 1/29/2017.
 */

//contains information about the different aspects about the tapee-goals marking the boilers
public class Target {

    /** Vars to determine if target is top or bottom of the retroreflective tape **/
    public static int UNDETERMINED = 0;
    public static int TOP = 1;
    public static int BOTTOM = 2;

    /**
     * List of lines that applies to the target
     */
    private ArrayList<Line> lineList;

    /**
     * Contour for the lines that apply to the target
     */
    private MyContour contour;

    /**
     * Peak point as a reference to find distance between the camera and the target
     */
    private Point2D.Double peak;

    /**
     * Type of the class, whether it is a top target or bottom target
     */
    private int type = -1;

    /**
     * The angle from the center of the camera without accounting camera angle
     * tip and the target. Use angleOfElevation for the angle between the camera
     * and the target while accounting camera angle tilt.
     */
    private double cameraAngleY;

    /**
     * Determines if cameraAngleY has been calculated. The boolean is true if
     * angle has been calculated and false if otherwise
     */
    private boolean b_cameraAngleY = false;

    /**
     * Angle of elevation from the camera to the target. Default 0 degrees if
     * the angle has not been calculated yet. The angle from the camera accounts
     * for the angle tilt of the camera. Use cameraAngleY for the angle between
     * the camera and the target without accounting camera angle tilt.
     */
    private double angleOfElevation;

    /**
     * Determines if angleOfElevation has been calculated. The boolean is true
     * if angle has been calculated and false if otherwise
     */
    private boolean b_angleOfElevation = false;
    
    /**
     * Distance between camera and target. Value is -1 if distance has not been calculated
     */
    private double targetDistance = -1;

    /**
     * Distance between camera and the tower. Value is -1 if distance has not been calculated
     */
    private double towerDistance = -1;

    /**
     * Initializes the target and sets attributes to their corresponding
     * values from the parameter.
     *
     * @param lineList list of lines for the target
     * @param contour contour box containing the target
     * @param peak peak y point of the target. The smaller the value of y,
     *             the higher it appears in the camera
     * @param type type of target whether it is the top tape or bottom tape
     */
    public Target(ArrayList<Line> lineList, MyContour contour, Point2D.Double peak, int type) {
        this.contour = contour;
        this.lineList = lineList;
        this.peak = peak;
        this.type = type;
    }

    /**
     * Returns ArrayList of lines for the target
     *
     * @return ArrayList of lines for the target
     */
    public ArrayList<Line> getLineList() {
        return lineList;
    }

    /**
     * Sets the lineList ArrayList
     *
     * @param lineList new lineList that replaces the current lineList ArrayList
     */
    public void setLineList(ArrayList<Line> lineList) {
        this.lineList = lineList;
    }

    /**
     * Returns the contour of the target
     *
     * @return contour of the target
     */
    public MyContour getCountour() {
        return contour;
    }

    /**
     * Sets the contour of the target
     *
     * @param contour new contour for the target
     */
    public void setCountour(MyContour contour) {
        this.contour = contour;
    }

    /**
     * Returns the peak of the target
     *
     * @return peak point of the target
     */
    public Point2D.Double getPeak() {
        return peak;
    }

    /**
     * Sets the peak of the contour
     *
     * @param peak new peak for the target
     */
    public void setPeak(Point2D.Double peak) {
        this.peak = peak;
    }

    /**
     * The type of the target
     *
     * @return type of the target
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the target
     *
     * @param type target type
     */
    public void setType(int type) {this.type = type;}

    /**
     * Angle between the peak of the target from and the center horizon (RESOLUTION
     * .getHeight() / 2)
     *
     * @return the angle between the center of the camera and the peak of the target
     */
    protected double getCameraAngleY() {
        if (!b_cameraAngleY) {
            cameraAngleY = Math.atan((GraphicsPanel.RESOLUTION.getHeight() / 2 - peak.getY())
                    / GraphicsPanel.pixelsToGoal);
            b_cameraAngleY = true;
        }

        return cameraAngleY;
    }

    /**
     * Calculates the angle of elevation in radians from the robot to the top of the
     * target. It utilizes the vertical FOV in order to determine the angle.
     *
     * @return the angle in radians from the camera mounted on the robot, to the top
     * of the target.
     */
    public double getAngleOfElevation() {
        if (!b_angleOfElevation) {
            angleOfElevation = Math.atan(getCameraAngleY()+ Math.toRadians(Config.CAMERA_START_ANGLE));

            b_angleOfElevation = true;
        }

        return angleOfElevation;
	}

    /**
     * Returns the angle of elevation calculate by getAngleOfElevation() in degrees
     *
     * @return angle of elevation in degrees
     */
	public double getAngleOfElevation_degrees() {
        return Math.toDegrees(getAngleOfElevation());
    }

    /**
     * The distance between the camera and the target
     *
     * @return distance between camera and target
     */
	public double getTargetDistance() {
        if (targetDistance == -1) {
            double height;
            if (type == TOP)
                height = Config.TARGET_HEIGHT_TOP;
            else
                height = Config.TARGET_HEIGHT_BOTTOM;

            targetDistance = (height - Config.ROBOT_HEIGHT) / Math.sin(getAngleOfElevation());
        }
        
        return targetDistance;
    }

    /**
     * Gets the distance from camera to the tower
     *
     * @return distance between the camera and the tower
     */
    public double getTowerDistance() {
	    if (towerDistance == -1) {
	        towerDistance = getTargetDistance() * Math.cos(getAngleOfElevation());
        }

        return towerDistance;
    }

/*    *//**
     * Updates if the robot is within the distance range required in order to shoot
     *//*
    public void updateDistanceStatus()	{
        if(getTowerDistance() < Config.MIN_DISTANCE)
            distanceStatus = MOVE_BACK;
        else if(getTowerDistance() > Config.MAX_DISTANCE)
            distanceStatus = MOVE_FORWARD;
        else if(getTowerDistance() > Config.MIN_DISTANCE && getTowerDistance() < Config.MAX_DISTANCE)
            distanceStatus = SPOT_ON;
        else
            distanceStatus = Integer.MIN_VALUE;
    }

    *//**
     * Gets the status of the distance range as an int (If the robot needs to move forward or backwards)
     *
     * @return the status of the distance range as an int (If the robot needs to move forward or backwards)
     *//*
    public int getDistanceStatusInt()	{
        return distanceStatus;
    }

    *//**
     * Gets the status of the distance range as a string (If the robot needs to move forward or backwards)
     *
     * @return the status of the distance range as a string (If the robot needs to move forward or backwards)
     *//*
    public String getDistanceStatus()	{
        if(MOVE_BACK==distanceStatus)
            return "Back";
        else if(MOVE_FORWARD==distanceStatus)
            return "Forward";
        else if(SPOT_ON==distanceStatus)
            return "Spot On";
        else
            return "";
    }*/

    /**
     * Calls on all methods that requires calculations. This ensures that all
     * necessary variables are calculated so that calls to their methods are
     * fast.
     */
    public void calculate() {
        getTargetDistance();                           // This method calls on other methods, which call other methods
    }

/*
    public double getCameraDistanceToGoal()
    {
    	return cameraDistanceToGoal;
    }
*/

//    public void setCameraDistanceToGoal()
//    {
//    	Double distance=null;
//    	
//    	distance=
//    	
//    	cameraDistanceToGoal=distance;
//    }

    /**
     * Returns the distance from the camera to the tower (horizontal distance)
     * 
     * @return Returns the distance from the camera to the tower (horizontal distance)
     */
/*    public double getCameraDistanceToTower()	{
    	if(targetDistance == Double.MIN_VALUE)	{
    		targetDistance = Math.sqrt(Math.pow(,2)+Math.pow(,2));
    	}
    	return targetDistance;
    }
*/

    /**
     * Draws the target to graphics
     *
     * @param g target graphics to paint
     */
    public void paint(Graphics g) {
        contour.paint(g);
    }
}
