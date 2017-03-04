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

	/**----------VARIABLES----------*/
	//Variables this class uses
    /** Vars to determine if target is top or bottom of the retroreflective tape **/
    public static int UNDETERMINED = 0;
    public static int TOP = 1;
    public static int BOTTOM = 2;
    /**List of lines that applies to the target*/
    private ArrayList<Line> lineList;
    /**Contour for the lines that apply to the target*/
    private MyContour contour;
    /**Peak point as a reference to find distance between the camera and the target*/
    private Point2D.Double peak;
    /**Type of the class, whether it is a top target or bottom target*/
    private int type = -1;
    /** The angle from the center of the camera without accounting camera angle tip and the target.
     *  Use angleOfElevation for the angle between the camera and the target while accounting camera angle tilt.
     */
    private double cameraAngleY;
    /** Determines if cameraAngleY has been calculated. The boolean is true if
     * angle has been calculated and false if otherwise*/
    private boolean b_cameraAngleY = false;
    
    /*----------CONSTRUCTOR----------*/
    //Constructor used to make an instance of this class
     /**Initializes the target and sets attributes to their corresponding
     * values from the parameter.
     * @param lineList list of lines for the target
     * @param contour contour box containing the target
     * @param peak peak y point of the target. The smaller the value of y, the higher it appears in the camera
     * @param type type of target whether it is the top tape or bottom tape
     */
    public Target(ArrayList<Line> lineList, MyContour contour, Point2D.Double peak, int type) {
        this.contour = contour;
        this.lineList = lineList;
        this.peak = peak;
        this.type = type;
    }
    
    /**----------ACCESSORS AND MUTATORS------------*/
    //General accessors and mutators
    /** @return ArrayList of lines for the target*/
    public ArrayList<Line> getLineList() {
        return lineList;
    }
    /**Sets the lineList ArrayList
     * @param lineList new lineList that replaces the current lineList ArrayList*/
    public void setLineList(ArrayList<Line> lineList) {
        this.lineList = lineList;
    }
    /**@return contour of the target*/
    public MyContour getContour() {
        return contour;
    }
    /**Sets the contour of the target
     * @param contour new contour for the target*/
    public void setContour(MyContour contour) {
        this.contour = contour;
    }
    /** @return peak point of the target*/
    public Point2D.Double getPeak() {
        return peak;
    }
    /**Sets the peak of the contour
     *@param peak new peak for the target*/
    public void setPeak(Point2D.Double peak) {
        this.peak = peak;
    }
    /**@return type of the target*/
    public int getType() {
        return type;
    }
    /**Sets the type of the target
     *@param type target type*/
    public void setType(int type) {this.type = type;}
    
    /**----------DISTANCE FINDING----------*/
    //methods for finding distances to goal
    /**Calls on all methods that requires calculations. This ensures that all necessary variables 
     * are calculated so that calls to their methods are fast.*/
    public void calculate() {
      getTowerDistance();           // This method calls on other methods, which call other methods
    }
    /** @return distance parallel to the ground between the camera and the tower*/
    public double getTowerDistance() {
    	double height=0;
	    if (type == TOP)
	         height = Config.TARGET_HEIGHT_TOP;
	    else if (type==BOTTOM)
	        height = Config.TARGET_HEIGHT_BOTTOM;
	    double inches=Math.abs(height-Config.ROBOT_HEIGHT);
	    return inches/Math.tan(getAngleInRadians());
    }
    /**@return angle from horizontal to top of target in radians*/
    protected double getAngleInRadians()
	{return Math.toRadians(getAngleInDegrees());}
    /**
     * calculates the angle from horizontal to the top of the target in degrees using similar triangles.
     * Finds the pixel height from the center of the camera image, and correlates the fraction
     * of that height with a fraction of the top part of the camera's vertical field of view, and finally
     * adding that to the tilt up of the camera
     * @return
     */
	protected double getAngleInDegrees()
	{
		double pixelsForHeight=Math.abs(peak.getY()-GraphicsPanel.RESOLUTION.getHeight()/2);
		double ratio=pixelsForHeight/(GraphicsPanel.RESOLUTION.getHeight()/2);
		double degreesToAdd=ratio*Config.VERTICAL_FOV/2;
		double angle=Config.CAMERA_START_ANGLE+degreesToAdd;
		return angle;
	}
    
    /**----------PAINTING----------*/
    //methods used to paint this class
    /**Draws the target to graphics
     * @param g target graphics to paint*/
    public void paint(Graphics g) 
    {contour.paint(g);}
    
    /**---------MOTION FEEDBACK----------*/
    //Methods that give feedback on how we need to move
    /**returns whether this target says that we need to move left*/
    public boolean needToMoveLeft()
    {
    	if(peak.getX()>Config.ALIGNED_RIGHT_X)
    		return true;
    	return false;
    }
    /**returns whether this target says that we need to move right*/
    public boolean needToMoveRight()
    {
    	if(peak.getX()<Config.ALIGNED_LEFT_X)
    		return true;
    	return false;
    }
    /**returns whether this target says that we need to move forward*/
    public boolean needToMoveForward()
    {
    	if(getTowerDistance()>Config.MAX_SHOOTING_DISTANCE)
    		return true;
    	return false;
    }
    /**returns whether this target says that we need to move backward*/
    public boolean needToMoveBackward()
    {
    	if(getTowerDistance()<Config.MIN_SHOOTING_DISTANCE)
    		return true;
    	return false;
    }
    /** Gets the status of the distance range as a string (If the robot needs to move forward or backwards)
     * @return the status of the distance range as a string (If the robot needs to move forward or backwards)*/
    public String getDistanceStatus()	{
        if(needToMoveBackward())
            return "Back. ";
        else if(needToMoveForward())
            return "Forward. ";
        return "";
    }
    /** Gets the status of the angular alignment as a string
     * @return If the robot needs to move left or right*/
    public String getAlignmentStatus()	{
        if(needToMoveRight())
            return "Right. ";
        else if(needToMoveLeft())
            return "Left. ";
        return "";
    }
    
    /**----------OTHER ANGLE AND DISTANVE METHODS----------*/
	//We don't really use these, except for with possibly calibrating the Vertical FOV
    /** Angle between the peak of the target from and the center horizon (RESOLUTION.getHeight() / 2)
     * @return the angle between the center of the camera and the peak of the target*/
    protected double getCameraAngleY() {
        if (!b_cameraAngleY) {
            if(GraphicsPanel.RESOLUTION.getHeight() / 2 - peak.getY()<0)
            	cameraAngleY=-cameraAngleY;
            b_cameraAngleY = true;
        }
        return cameraAngleY;
    }
     /**The distance between the camera and the target (hypotenuse)
     * @return distance between camera and target*/
	public double getTargetDistance() {
		double height=0;
	    if (type == TOP)
	         height = Config.TARGET_HEIGHT_TOP;
	    else if (type==BOTTOM)
	        height = Config.TARGET_HEIGHT_BOTTOM;
	    double inches=Math.abs(height-Config.ROBOT_HEIGHT);	
	    return inches/Math.sin(getAngleInRadians());
	}     
}