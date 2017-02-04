package com.Team5427.VisionProcessing;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.Team5427.Networking.Server;

import com.Team5427.res.Log;
import edu.wpi.first.wpilibj.networktables.*;

public class Main {

	/**
	 * The FOV of the attached webcam. It is used in calculating the distance to
	 * the goals.
	 */
	// public static final double CAMERA_FOV = 59;

	/**
	 * The object from WPILib that allows the program to retrieve all of the
	 * information that is created by GRIP.
	 */
	public static NetworkTable table;

	static double[] x1Values = new double[20];
	static double[] y1Values = new double[20];
	static double[] x2Values = new double[20];
	static double[] y2Values = new double[20];
	static double[] lengthValues = new double[20];
	static double[] centerXValues = new double[20];
	static double[] centerYValues = new double[20];
	static double[] widthValues = new double[20];
	static double[] heightValues = new double[20];
	static double FPS = -1;
	static ArrayList<Line> lines = new ArrayList<Line>();
	static ArrayList<MyContour> contours = new ArrayList<MyContour>();
	static Target topTape,bottomTape;
	//static ArrayList<Goal> goals = new ArrayList<Goal>();

	/**
	 * The maximum distance that two lines can be from each other in order to be
	 * considered as part of the same goal.
	 */
	private final static int lowestAcceptableValue = 9;

	/**
	 * A frame created just to hold a GraphicsPanel.
	 */
	static VisionFrame vf;

	/**
	 * After initializing several variables, the main method then proceeds to go
	 * into a while loop which cycles through getting the values from the
	 * network table, turning all of the arrays full of doubles into a single
	 * ArrayList full of lines, determining where the goals are out of all of
	 * those lines, and then removing the goals which are inside of others.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress("localhost");
		table = NetworkTable.getTable("GRIP");
		vf = new VisionFrame();

		// System.out.println("POWER "+ShootingAssistant.getShootingPower(99));

		setValues();

		Server.start();

		// Starts the ByteSender class. Set BYTE_SENDER_THREAD_ENABLE in com.Team5427.res.Config to enable/disable
		Thread byteSender = new Thread(new ByteSender());
		byteSender.start();


		while (true) {
			long startTime = System.nanoTime();
			try {

				setValues();

				createLines();

				createContours();

				findTargets();

				filterGoals();

				//sendData();

				vf.getPanel().repaint();

				do {
					Thread.sleep(1);
				} while (!vf.getPanel().isDonePainting());

				vf.getPanel().setDonePainting(false);

				lines.clear();
				//goals.clear();

				// System.out.println((System.nanoTime() - startTime) / 1000000);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * This method goes through the entire ArrayList of lines that is given to
	 * the program by GRIP, and is used to determine which, if any lines are
	 * touching the given line. If there are multiple, then it will only return
	 * the one that is first in the ArrayList.
	 *
	 *
	 * @param l
	 *            The line that you would like to compare to the rest of the
	 *            ArrayList
	 * @return The first line in the ArrayList of lines that is close to the
	 *         current line
	 */
	public static Line isClose(Line l) {
		int index = -1;
		double lowestValue = lowestAcceptableValue;

		for (int i = 0; i < lines.size(); i++) {

			if (l.isHorizontal() != lines.get(i).isHorizontal()) {

				double distance = l.compareTo(lines.get(i));
				if (distance <= lowestValue) {
					index = i;
					lowestValue = distance;
				}
			}
		}
		if (index >= 0)
			return lines.remove(index);
		else
			return null;
	}

	/**
	 *
	 * This method goes through the entire ArrayList of lines that is given to
	 * the program by GRIP, and is used to determine which, if any lines are
	 * touching the given lines. If there are multiple, then it will only return
	 * the one that is first in the ArrayList.
	 *
	 * @param l1
	 *            One of the line that you would like to compare to the rest of
	 *            the ArrayList
	 * @param l2
	 *            One of the line that you would like to compare to the rest of
	 *            the ArrayList
	 * @return An array of the two lines given, plus the third line that was
	 *         found
	 */
	public static Line[] isClose(Line l1, Line l2) {

		int index = -1;
		double lowestValue = lowestAcceptableValue;
		boolean needHorizontal;

		// determines whether a vertical or horizontal line is needed in order
		// to complete the goal
		if (l1.isHorizontal() || l2.isHorizontal())
			needHorizontal = false;
		else
			needHorizontal = true;

		for (int i = 0; i < lines.size(); i++) {
			if (needHorizontal == lines.get(i).isHorizontal()) {

				double d = returnLowestDouble(l1.compareTo(lines.get(i)), l2.compareTo(lines.get(i)));

				if (d < lowestValue) {
					index = i;
					lowestValue = d;
				}

			}

		}

		if (index >= 0)
			return new Line[] { l1, l2, lines.remove(index) };
		else
			return null;

	}

	/**
	 * Retrieves all of the data from the network table in the form of arrays
	 */
	private static void setValues() {
		// Sets line values
	    do {
			// FPS = table.getNumber("FPS");
			x1Values = table.getNumberArray("myLinesReport/x1", x1Values);
			y1Values = table.getNumberArray("myLinesReport/y1", y1Values);
			x2Values = table.getNumberArray("myLinesReport/x2", x2Values);
			y2Values = table.getNumberArray("myLinesReport/y2", y2Values);
			lengthValues = table.getNumberArray("myLinesReport/length", lengthValues);
		} while (!(x1Values.length == y1Values.length && y1Values.length == x2Values.length
				&& x2Values.length == y2Values.length && y2Values.length == lengthValues.length));

		// Sets contour values
		do	{
			centerXValues = table.getNumberArray("myContoursReport/centerX", centerXValues);
			centerYValues = table.getNumberArray("myContoursReport/centerY", centerYValues);
			widthValues = table.getNumberArray("myContoursReport/width", widthValues);
			heightValues = table.getNumberArray("myContoursReport/height", heightValues);
		} while (!(centerXValues.length == centerYValues.length && centerXValues.length == widthValues.length
                && centerXValues.length == heightValues.length));

	}

	/**
	 * Takes all of the data previously gotten from the NetworkTable, and turns
	 * it into an ArrayList of lines.
	 */
	private static void createLines() {
		lines.clear();

		for (int i = 0; i < lengthValues.length; i++) {
			if (lengthValues[i] != 0) {
				try {
					lines.add(new Line(x1Values[i], y1Values[i], x2Values[i], y2Values[i], lengthValues[i]));
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(x1Values.length + ":" + y1Values.length + ":" + x2Values.length + ":"
							+ y2Values.length + ":" + lengthValues.length);
				}
			}
		}
	}

    /**
     * Takes all of the recieved array of contours from the Network Table and
     * assign it to a contour class in the contours ArrayList
     */
    private static void createContours() {
		contours.clear();

		for (int i = 0; i < centerXValues.length; i++) {
            if (widthValues[i] != 0) {
                try {
                    contours.add(new MyContour(centerXValues[i], centerYValues[i], widthValues[i], heightValues[i]));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.error(centerXValues.length + ":" + centerYValues.length + ":" + widthValues.length + ":"
                            + heightValues.length);
                }
            }
        }
    }

	/**
	 * Iterates through the ArrayList of lines, and if two are found to have
	 * ends less than two pixels away from each other, then it will remove both
	 * of the lines from the ArrayList, and proceed to add both of them to a new
	 * Goal, leaving the third line of the Goal to be fixed later.
	 */
	private static void findTargets() {
		System.out.println("Size: " + lines.size());

		if (lines.size() == 0) {
			topTape = null;
			bottomTape = null;

			return;
		}

		ArrayList<Line>tempListFirstContour=new ArrayList<Line>();
		ArrayList<Line>tempListSecondContour=new ArrayList<Line>();
		Point2D.Double firstPeak, secondPeak;
		int firstType, secondType;

		for(int i =0;i<lines.size();i++)
		{
			if(contours.get(0).contains(lines.get(i)))
			{
				tempListFirstContour.add(lines.get(i));
			}
			else if(contours.get(1).contains(lines.get(i)))
				tempListSecondContour.add(lines.get(i));
		}

		orderLines(tempListFirstContour);
		orderLines(tempListSecondContour);
		firstPeak=getPeak(tempListFirstContour);
		secondPeak=getPeak(tempListSecondContour);

		if(firstPeak.getY()>=secondPeak.getY())
		{
			topTape=new Target(tempListFirstContour, contours.get(0), firstPeak, Target.TOP);
			bottomTape=new Target(tempListSecondContour, contours.get(1), secondPeak, Target.BOTTOM);
		}

		else if(firstPeak.getY()<secondPeak.getY())
		{
			bottomTape=new Target(tempListFirstContour, contours.get(0), firstPeak, Target.TOP);
			topTape=new Target(tempListSecondContour, contours.get(1), secondPeak, Target.BOTTOM);
		}
		//sort lines into 2 al using contours
		//figute out peak vals using change of slope
		//create two targets(AL lines,Contour, Point peak, type--top or bottom tape)
	}
	
	/**
	 * finds the peak in a list of lines
	 * @param list the list of lines
	 * @return the highest peak in the lines
	 */
	public static Point2D.Double getPeak(ArrayList<Line> list)
	{
		ArrayList<Point2D.Double>points=new ArrayList<Point2D.Double>();
		for(int i =0; i<list.size()-1;i++)
		{
			if(!compareSlopeSigns(list.get(i).getSlope(),list.get(i+1).getSlope()))
					points.add(new Point2D.Double(list.get(i).getX2(),list.get(i).getY2()));
		}
		if(!compareSlopeSigns(list.get(list.size()-1).getSlope(),list.get(0).getSlope()))
			points.add(new Point2D.Double(list.get(list.size()-1).getX2(),list.get(list.size()-1).getY2()));
		Point2D.Double peak=points.get(0);
		for(int i=1; i<points.size();i++)
		{
			if(points.get(i).getY()>peak.getY())
				peak=points.get(i);
		}
		if(points.get(points.size()-1).getY()>peak.getY())
			peak=points.get(points.size()-1);
		return peak;
	}
	
	/**
	 * compares the signs of two doubles 
	 * @param a one double value
	 * @param b the other double value
	 * @return true if same signs, false if different signs
	 */
	public static boolean compareSlopeSigns(double a, double b)
	{
		if(Math.abs(a-b)>Math.abs(a+b))
			return false;
		return true;
	}
	
	public static void orderLines(ArrayList<Line> list)
	{
		ArrayList<Line>tempLines=new ArrayList<Line>();
		Line line=list.get(0);
				
		for(int i=1; i<list.size();i++)
		{
			if(line.getX2()==list.get(i).getX1()&&line.getY2()==list.get(i).getY1())
			{
				tempLines.add(line);
				line=list.get(i);
			}
		}
		list.clear();
		list.addAll(tempLines);
	}

	/**
	 * Removes any goals from the ArrayList which are inside of another goal by
	 * calling the isInsideGoal method within the Goal class.
	 */
	private synchronized static void filterGoals() {

//		for (int index = 0; index < goals.size(); index++) {
//			if (goals.get(index).isComplete()) {
//				Goal g = goals.get(index);
//				for (int i = 0; i < goals.size(); i++) {
//					if (g.isInsideGoal(goals.get(i))) {
//						goals.remove(i);
//						i--;
//					}
//				}
//			} else
//				goals.remove(index);
//		}

	}

	//TODO fix this method
	/**
	 * method that will be used in determining which goal to send to the robot
	 * 
	 * @return the goal with the largest area.
	 */
//	public static Goal getBestGoal() {
//		if (goals.size() > 0) {
//			int index = 0;
//			for (int i = 1; i < goals.size() - 1; i++) {
//				if (goals.get(index).compareTo(goals.get(i)) == 0) {
//					index = i;
//				}
//			}
//			return goals.get(index);
//		} else
//			return null;
//	}

	/**
	 *
	 * @param d1
	 *            The first distance.
	 * @param d2
	 *            The second distance.
	 * @return The lowest value given, either d1 or d2.
	 */
	private static double returnLowestDouble(double d1, double d2) {
		if (d1 < d2)
			return d1;
		else
			return d2;

	}

	/**
	 * Finds the horizontal lines in the list of lines and returns it
	 *
	 * @return all horizontal lines in the list
	 */
	public ArrayList<Line> getHorizontalLines() {
		ArrayList<Line> horizontalLines = new ArrayList<>();

		for (Line l : lines) {
			if (l.isHorizontal())
				horizontalLines.add(l);
		}

		return horizontalLines;
	}

	/**
	 * Sends the appropriate goal data to the roborio
	 */
	//TODO fix this method
//	public static void sendData() {
//
//		if (Server.hasConnection() && goals.size() > 0) {
//			Goal g = getBestGoal();
//
//			if (g != null) {
//				// TODO verify that the getGoalDistanceTurret is working
//				Server.send(StringDictionary.TASK + StringDictionary.GOAL_ATTACHED + g.getGoalDistanceTurret() + " "
//						+ g.getAngleOfElevation() + " " + g.getTurretXAngle() + " "
//						+ ShootingAssistant.getShootingPower(g.getGoalDistanceTurret()));
//
//			}
//		}
//	}

}
