package com.Team5427.VisionProcessing;

import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.Rectangle;

/**
 * 
 * A simple class that stores all of the data from a contour, including the center
 * point, width, and height. 
 *
 */
public class MyContour {
	
	private double centerX, centerY;
	private double width, height;
	private Rectangle contourRect;
	private Line top, bottom, left, right;

	public MyContour(double centerX, double centerY, double width, double height)	{
		this.centerX = centerX;
		this.centerY = centerY;
		this.width = width;
		this.height = height;
		contourRect = new Rectangle((int)(centerX-width/2), (int)(centerY-height/2), (int)width, (int)height);
		
		/**
		 * Used for contains(), to see if the line intersects with any of the 4 sides
		 */
		top = new Line(centerX-width/2,centerY-height/2,centerX+width/2,centerY-height/2,width);
		bottom = new Line(centerX-width/2,centerY+height/2,centerX+width/2,centerY+height/2,width);
		left = new Line(centerX-width/2,centerY-height/2,centerX-width/2,centerY+height/2,height);
		right = new Line(centerX+width/2,centerY-height/2,centerX+width/2,centerY+height/2,height);
	}

	public boolean contains(Line line)	{
		if(top.intersectsLine((line)) || bottom.intersectsLine(line) || left.intersectsLine(line) || right.intersectsLine(line))
			return true;
		return false;
	}
	
	public Rectangle getContourRect() {
		return contourRect;
	}

	public String toString() {
		return "Contour [centerX=" + centerX + ", centerY=" + centerY + ", width=" + width + ", height=" + height + "]";
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}
