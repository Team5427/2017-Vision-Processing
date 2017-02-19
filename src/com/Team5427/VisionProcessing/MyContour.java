package com.Team5427.VisionProcessing;

import com.sun.javafx.geom.Line2D;
import java.awt.*;
import java.util.ArrayList;

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

	/**
	 * Creates contour class from values
	 *
	 * @param centerX center x value
	 * @param centerY center y value
	 * @param width width of contour
	 * @param height height of contour
	 */
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

		Rectangle rectCpy = new Rectangle(contourRect);
		rectCpy.grow(20,20);
		Point p1 = new Point((int)line.getX1(),(int)line.getY1());
		Point p2 = new Point((int)line.getX2(),(int)line.getY2());
		if (rectCpy.contains(p1) && rectCpy.contains(p2))
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

	/**
	 * Draws the contour to graphics
	 *
	 * @param g target graphics to paint
	 */
	public void paint(Graphics g) {
		g.drawRect(contourRect.x, contourRect.y, contourRect.width, contourRect.height);
		System.out.print("painting In Contur");
	}
}
