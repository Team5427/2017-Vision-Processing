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

    /**
     * Vars to determine if target is top or bottom of the retroreflective tape
     */
    public static int TOP = 0;
    public static int BOTTOM = 1;

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
     * Angle of elevation from the camera to the target. Default 0 degrees if the angle has not been calculated yet
     */
    private double angleOfElevation = 0;
    
    /**
     * Distance from the camera itself to the goal
     */
    private double cameraDistanceToGoal = 0;

    /**
     * Determines if angle of elevation has been calculated. If true, the boolean will be true and false otherwise
     */
    private boolean b_angleOfElevation = false;
    
    /**
     * Distance between camera and target. Value is -1 if distance has not been set
     */
    private double cameraDistanceToTarget = -1;
    
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
     * @param countour new contour for the target
     */
    public void setCountour(MyContour countour) {
        this.contour = countour;
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
        return Math.atan((GraphicsPanel.RESOLUTION.getHeight() / 2 - peak.getY())
                / GraphicsPanel.pixelsToGoal);
    }

    /**
     * Calculates the angle of elevation from the robot to the top of the target.
     * It utilizes the vertical FOV in order to determine the angle.
     *
     * @return the angle from the camera mounted on the robot, to the top of the
     *         target.
     */
    public double getAngleOfElevation() {
        /*
		 * System.out.println((GraphicsPanel.RESOLUTION.getHeight() / 2 -
		 * (leftLine.getTopPointY() + rightLine.getTopPointY()) / 2));
		 */

        if (b_angleOfElevation == false) {
            angleOfElevation = Math.atan((GraphicsPanel.RESOLUTION.getHeight() / 2
                - peak.getY() / GraphicsPanel.pixelsToGoal)
                + Math.toRadians(Config.CAMERA_START_ANGLE));

            b_angleOfElevation = true;
        }

        return angleOfElevation;

		/*
		 * return Math .atan(((leftLine.getMidpointY() +
		 * rightLine.getMidpointY()) / 2 - GraphicsPanel.RESOLUTION.getHeight() /
		 * 2) / GraphicsPanel.pixelsToGoal) +
		 * Math.toRadians(Config.CAMERA_START_ANGLE);
		 */
	}


    /**
     * The distance between the camera and the target
     *
     * @return distance between camera and target
     */
	public double getCameraDistance() {
        if (cameraDistanceToGoal == -1) {
            double height;
            if (type == TOP)
                height = Config.TARGET_HEIGHT_TOP;
            else
                height = Config.TARGET_HEIGHT_BOTTOM;

            cameraDistanceToGoal = (height - Config.ROBOT_HEIGHT) / Math.sin(getAngleOfElevation());
        }
        
        return cameraDistanceToGoal;
    }

    public double getCameraDistanceToGoal()
    {
    	return cameraDistanceToGoal;
    }
    
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
    	if(cameraDistanceToTarget == Double.MIN_VALUE)	{
    		cameraDistanceToTarget = Math.sqrt(Math.pow(,2)+Math.pow(,2));
    	}
    	return cameraDistanceToTarget;
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
