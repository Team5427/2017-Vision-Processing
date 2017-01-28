package com.Team5427.VisionProcessing;

/**
 * 
 * A simple class that is used to store all of the data from a line, including
 * the points of both of the ends, as well as the length of the line. It also
 * includes a built in method to determine whether or not two lines are close to
 * each other.
 *
 */
public class Line {

	private double x1, y1, x2, y2;
	private double length;
	private boolean horizontal, vertical;

	/**
	 * Creates a Line using the x and y values of the endpoints of a line in
	 * addition to the length of the aforementioned line.
	 * 
	 * @param x1
	 *            The X-value of the first point.
	 * @param y1
	 *            The Y-value of the first point.
	 * @param x2
	 *            The X-value of the second point.
	 * @param y2
	 *            The Y-Value of the second point.
	 * @param length
	 *            The length of the line.
	 */
	public Line(double x1, double y1, double x2, double y2, double length) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.length = length;

		if (getSlope() > -.3 && getSlope() < .3)
			horizontal = true;
		else
			vertical = true;

	}

	/**
	 * Calculates the shortest distance between the ends of two lines.
	 * 
	 * @param l
	 *            The line that it will be compared with
	 * @return The shortest distance between the endpoints of the two lines.
	 */
	public double compareTo(Line l) {
		double[] d = new double[4];
		double lowestDistance = 500;

		d[0] = Math.sqrt((x1 - l.getX1()) * (x1 - l.getX1()) + (y1 - l.getY1()) * (y1 - l.getY1()));

		d[1] = Math.sqrt((x2 - l.getX2()) * (x2 - l.getX2()) + (y2 - l.getY2()) * (y2 - l.getY2()));

		d[2] = Math.sqrt((x2 - l.getX1()) * (x2 - l.getX1()) + (y2 - l.getY1()) * (y2 - l.getY1()));

		d[3] = Math.sqrt((x1 - l.getX2()) * (x1 - l.getX2()) + (y1 - l.getY2()) * (y1 - l.getY2()));

		for (double distance : d) {
			if (distance < lowestDistance)
				lowestDistance = distance;
		}
		// System.out.println(lowestDistance);
		return lowestDistance;

	}

	public String toString() {
		return "Point 1:  (" + x1 + " , " + y1 + ")  Point 2:  (" + x2 + " , " + y2 + ")  Length:  " + length;

	}

	public double getX1() {
		return x1;
	}

	public double getY1() {
		return y1;
	}

	public double getX2() {
		return x2;
	}

	public double getY2() {
		return y2;
	}

	public double getMidpointY() {
		return (y1 + y2) / 2;
	}

	public double getMidpointX() {
		return (x1 + x2) / 2;
	}

	public double getLength() {
		return length;
	}

	/**
	 * Returns the angle of the line in radians
	 * 
	 * @return angle of line in radians
	 */
	public double getAngle() {
		double x1 = this.x1;
		double y1 = this.y1;
		double x2 = this.x2;
		double y2 = this.y2;

		if (this.x1 > this.x2) {
			x1 = this.x2;
			y1 = this.y2;
			x2 = this.x1;
			y2 = this.y1;
		}

		double angle = Math.acos(length / (x2 - x1));

		if (y1 > y2)
			angle *= -1;

		return angle;
	}

	public double getSmallestX() {
		if (x1 < x2)
			return x1;

		return x2;
	}

	public double getLargestX() {
		if (x1 > x2)
			return x1;

		return x2;
	}

	public double getSmallestY() {
		if (y1 < y2)
			return y1;

		return y2;
	}

	public double getLargestY() {
		if (y1 > y2)
			return y1;

		return y2;
	}

	/**
	 * Returns the x value of the top point
	 *
	 * @return the x value of the top point
	 */
	public double getTopPointX() {
		if (y1 < y2)
			return x1;

		return x2;
	}

	/**
	 * Returns the y value of the top point
	 *
	 * @return the y value of the top point
	 */
	public double getTopPointY() {
		if (y1 < y2)
			return y1;

		return y2;
	}

	public double getSlope() {

		if (x1 == x2)
			return 1 / Double.MAX_VALUE;

		return (y2 - y1) / (x2 - x1);
	}

	public double getXWidth() {
		return x2 - x1;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public boolean isVertical() {
		return vertical;
	}
}