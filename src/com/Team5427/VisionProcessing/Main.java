package com.Team5427.VisionProcessing;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.Team5427.Networking.Server;

import com.Team5427.res.Config;
import com.Team5427.res.Log;
import edu.wpi.first.wpilibj.networktables.*;

public class Main {

	/**
	 * Maximum ratio of contours to be selected
	 * Width:Height
	 */
	private static double MAX_CONTOUR_RATIO = 6;
	/**
	 * Minimum ratio of contours to be selected
	 * Width:Height
	 */
	private static double MIN_CONTOUR_RATIO = 2;

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

    /**
     * Temporary values to hold new data until data collection is completed
     */
	static double[] t_x1Values = new double[20];
	static double[] t_y1Values = new double[20];
	static double[] t_x2Values = new double[20];
	static double[] t_y2Values = new double[20];
	static double[] t_lengthValues = new double[20];
	static double[] t_centerXValues = new double[20];
	static double[] t_centerYValues = new double[20];
	static double[] t_widthValues = new double[20];
	static double[] t_heightValues = new double[20];
	static double FPS = 0;
	private static ArrayList<Line> t_lines = new ArrayList<>();
    private static ArrayList<MyContour> t_contours = new ArrayList<>();
    private static Target t_topTape, t_bottomTape;
	private static ArrayList<Target> t_targets = new ArrayList<>();
	private static ArrayList<TargetSet> t_targetSet = new ArrayList<>();

	// Values that can be called by GraphicsPanel
	static double[] x1Values = new double[20];
	static double[] y1Values = new double[20];
	static double[] x2Values = new double[20];
	static double[] y2Values = new double[20];
	static double[] lengthValues = new double[20];
	static double[] centerXValues = new double[20];
	static double[] centerYValues = new double[20];
	static double[] widthValues = new double[20];
	static double[] heightValues = new double[20];

    private static ArrayList<Line> lines = new ArrayList<>();
    private static ArrayList<MyContour> contours = new ArrayList<>();
    private static Target topTape, bottomTape;
    private static ArrayList<Target> targets = new ArrayList<>();
    private static ArrayList<TargetSet> targetSet = new ArrayList<>();



    private static boolean startPanelRepainting = false;
	private static boolean panelRepainting = false;

	/**
	 * The maximum distance that two t_lines can be from each other in order to be
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
	 * ArrayList full of t_lines, determining where the goals are out of all of
	 * those t_lines, and then removing the goals which are inside of others.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkTable.setClientMode();
		NetworkTable.setIPAddress("localhost");
		table = NetworkTable.getTable("GRIP");
		vf = new VisionFrame();


		setValues();

		Server.start();

		// Starts the ByteSender class. Set BYTE_SENDER_THREAD_ENABLE in com.Team5427.res.Config to enable/disable
		Thread byteSender = new Thread(new ByteSender());
		byteSender.start();

        runPaint();

		while (true) {
			long startTime = System.nanoTime();
			try {

				setValues();

				createLines();

				createContours();

				findTargets();

				filterContours();

				finalizeData();

				//sendData();

//				vf.getPanel().repaint();

//				do {
//					Thread.sleep(1);
//				} while (!vf.getPanel().isDonePainting());
//
//				vf.getPanel().setDonePainting(false);

				t_lines.clear();
				//goals.clear();

				// System.out.println((System.nanoTime() - startTime) / 1000000);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

    /**
     * Sets all temporary values to final for VisionPanel use
     */
	protected static void finalizeData() {
        x1Values = t_x1Values;
        y1Values = t_y1Values;
        x2Values = t_x2Values;
        y2Values = t_y2Values;
        lengthValues = t_lengthValues;
        centerXValues = t_centerXValues;
        centerYValues = t_centerYValues;
        widthValues = t_widthValues;
        heightValues = t_heightValues;

        lines = new ArrayList<>(t_lines);
        contours = new ArrayList<>(t_contours);
        topTape = t_topTape;
        bottomTape = t_bottomTape;
        targets = t_targets;
        targetSet = new ArrayList<>(t_targetSet);
    }

    /**
     * Returns ArrayList of t_lines
     * @return list of t_lines
     */
    public static synchronized ArrayList <Line> getLines() {
        return lines;
    }

    /**
     * Returns ArrayList of t_contours
     * @return list of conturs
     */
    public static synchronized ArrayList<MyContour> getContours() {
        return contours;
    }

    /**
     * Top tape identified by the program
     * @return top tape Target
     */
    public static synchronized Target getTopTape() {
        return topTape;
    }

    /**
     * Bottom tape identified by the program
     * @return bottom tape Target
     */
    public static synchronized Target getBottomTape() {
        return bottomTape;
    }

    private static Thread repaintPanel = new Thread(new Runnable() {
        @Override
        public void run() {

            long lastPaintTime = System.nanoTime();
            double timeGap = 10e9 / Config.MAX_FPS;

            while (true) {

//                vf.getPanel().repaint();

//                System.out.println("PRINT!");
//                System.out.println("A: " + timeGap + " B: " + (System.nanoTime() - lastPaintTime));
//
//                if (timeGap < System.nanoTime() - lastPaintTime) {
//                    System.out.println("Ya");
//                } else
//                    System.out.println("Nah");

                if (!panelRepainting) {
                    try {
                        Thread.currentThread().sleep(50);
                    }
                    catch (Exception e) {
                        Log.error(e.getMessage());
                    }

                    continue;
                }


                if (System.nanoTime() - lastPaintTime >= timeGap) {
                    vf.getPanel().repaint();
                    lastPaintTime = System.nanoTime();
//                    System.out.println("BOOM");
                }
                else {
                    long sleepGap = (long)(timeGap + 0.5) - System.nanoTime() - lastPaintTime;
                    if (sleepGap < 0) {
                        sleepGap = 0;
                    }

                    int nanoSleep = (int)(sleepGap % 10e9);
                    sleepGap = (long) (sleepGap % 10e9);

                    try {
                        Thread.currentThread().sleep(sleepGap, nanoSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    private static void runPaint() {
        if (!startPanelRepainting) {
            repaintPanel.start();
        }

        panelRepainting = true;
    }

    private static void pausePaint() {
        panelRepainting = false;
    }

	/**
	 * This method goes through the entire ArrayList of t_lines that is given to
	 * the program by GRIP, and is used to determine which, if any t_lines are
	 * touching the given line. If there are multiple, then it will only return
	 * the one that is first in the ArrayList.
	 *
	 *
	 * @param l
	 *            The line that you would like to compare to the rest of the
	 *            ArrayList
	 * @return The first line in the ArrayList of t_lines that is close to the
	 *         current line
	 */
	public static Line isClose(Line l) {
		int index = -1;
		double lowestValue = lowestAcceptableValue;

		for (int i = 0; i < t_lines.size(); i++) {

			if (l.isHorizontal() != t_lines.get(i).isHorizontal()) {

				double distance = l.compareTo(t_lines.get(i));
				if (distance <= lowestValue) {
					index = i;
					lowestValue = distance;
				}
			}
		}
		if (index >= 0)
			return t_lines.remove(index);
		else
			return null;
	}

	/**
	 *
	 * This method goes through the entire ArrayList of t_lines that is given to
	 * the program by GRIP, and is used to determine which, if any t_lines are
	 * touching the given t_lines. If there are multiple, then it will only return
	 * the one that is first in the ArrayList.
	 *
	 * @param l1
	 *            One of the line that you would like to compare to the rest of
	 *            the ArrayList
	 * @param l2
	 *            One of the line that you would like to compare to the rest of
	 *            the ArrayList
	 * @return An array of the two t_lines given, plus the third line that was
	 *         found
	 */
	@Deprecated
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

		for (int i = 0; i < t_lines.size(); i++) {
			if (needHorizontal == t_lines.get(i).isHorizontal()) {

				double d = returnLowestDouble(l1.compareTo(t_lines.get(i)), l2.compareTo(t_lines.get(i)));

				if (d < lowestValue) {
					index = i;
					lowestValue = d;
				}

			}

		}

		if (index >= 0)
			return new Line[] { l1, l2, t_lines.remove(index) };
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
			t_x1Values = table.getNumberArray("myLinesReport/x1", t_x1Values);
			t_y1Values = table.getNumberArray("myLinesReport/y1", t_y1Values);
			t_x2Values = table.getNumberArray("myLinesReport/x2", t_x2Values);
			t_y2Values = table.getNumberArray("myLinesReport/y2", t_y2Values);
			t_lengthValues = table.getNumberArray("myLinesReport/length", t_lengthValues);
		} while (!(t_x1Values.length == t_y1Values.length && t_y1Values.length == t_x2Values.length
				&& t_x2Values.length == t_y2Values.length && t_y2Values.length == t_lengthValues.length));

		// Sets contour values
		do	{
			t_centerXValues = table.getNumberArray("myContoursReport/centerX", t_centerXValues);
			t_centerYValues = table.getNumberArray("myContoursReport/centerY", t_centerYValues);
			t_widthValues = table.getNumberArray("myContoursReport/width", t_widthValues);
			t_heightValues = table.getNumberArray("myContoursReport/height", t_heightValues);
		} while (!(t_centerXValues.length == t_centerYValues.length && t_centerXValues.length == t_widthValues.length
                && t_centerXValues.length == t_heightValues.length));

		FPS = table.getNumber("fps", FPS);
	}

	/**
	 * Takes all of the data previously gotten from the NetworkTable, and turns
	 * it into an ArrayList of t_lines.
	 */
	private static void createLines() {
		t_lines.clear();

		for (int i = 0; i < t_lengthValues.length; i++) {
			if (t_lengthValues[i] != 0) {
				try {
					t_lines.add(new Line(t_x1Values[i], t_y1Values[i], t_x2Values[i], t_y2Values[i], t_lengthValues[i]));
				} catch (Exception e) {
					e.printStackTrace();
					Log.pl(t_x1Values.length + ":" + t_y1Values.length + ":" + t_x2Values.length + ":"
							+ t_y2Values.length + ":" + t_lengthValues.length);
				}
			}
		}
	}

    /**
     * Takes all of the recieved array of t_contours from the Network Table and
     * assign it to a contour class in the t_contours ArrayList
     */
    private static void createContours() {
		t_contours.clear();

		for (int i = 0; i < t_centerXValues.length; i++) {
            if (t_widthValues[i] != 0) {
                try {
                    t_contours.add(new MyContour(t_centerXValues[i], t_centerYValues[i], t_widthValues[i], t_heightValues[i]));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.error(t_centerXValues.length + ":" + t_centerYValues.length + ":" + t_widthValues.length + ":"
                            + t_heightValues.length);
                }
            }
        }
    }

    /**
     * Removes excess contours detected by grip
     */
    public static void filterContours() {

        for (int i = 0; i < t_contours.size(); i++) {

            MyContour contourBuffer = t_contours.get(i);
            double ratio = contourBuffer.getWidth() / contourBuffer.getHeight();

            if (ratio > MAX_CONTOUR_RATIO || ratio < MIN_CONTOUR_RATIO) {
                t_contours.remove(i--);
            }
        }
    }

	/**
	 * Iterates through the ArrayList of t_lines, and if two are found to have
	 * ends less than two pixels away from each other, then it will remove both
	 * of the t_lines from the ArrayList, and proceed to add both of them to a new
	 * Goal, leaving the third line of the Goal to be fixed later.
	 */
	private static void findTargets() {

		if (t_lines.size() == 0) {
			t_topTape = null;
			t_bottomTape = null;

			return;
		}

		ArrayList<Line>tempListFirstContour=new ArrayList<Line>();
		ArrayList<Line>tempListSecondContour=new ArrayList<Line>();
		ArrayList<Line>tempListAll=new ArrayList<Line>();

		Point2D.Double firstPeak, secondPeak;
		int firstType, secondType;

		for(int i = 0; i< t_lines.size(); i++)
		{
			if(t_contours.get(0).contains(t_lines.get(i)))
			{
				tempListFirstContour.add(t_lines.get(i));
				tempListAll.add(t_lines.get(i));
}
			else if(t_contours.get(1).contains(t_lines.get(i))) {
				tempListSecondContour.add(t_lines.get(i));
				tempListAll.add(t_lines.get(i));

			}
		}

//		orderLines(tempListFirstContour);
//		orderLines(tempListSecondContour);
		firstPeak=getPeak(t_contours.get(0));
		secondPeak=getPeak(t_contours.get(1));

		if(firstPeak.getY()>=secondPeak.getY())
		{
			t_topTape =new Target(tempListFirstContour, t_contours.get(0), firstPeak, Target.TOP);
			t_bottomTape =new Target(tempListSecondContour, t_contours.get(1), secondPeak, Target.BOTTOM);
		}

		else if(firstPeak.getY()<secondPeak.getY())
		{
			t_bottomTape =new Target(tempListFirstContour, t_contours.get(0), firstPeak, Target.TOP);
			t_topTape =new Target(tempListSecondContour, t_contours.get(1), secondPeak, Target.BOTTOM);
		}

		//TODO add new GRIP to github ~V

		t_lines.clear();
		t_lines =tempListAll;

		//sort t_lines into 2 al using t_contours
		//figute out peak vals using change of slope
		//create two targets(AL t_lines,Contour, Point peak, type--top or bottom tape)
	}

	public static Point2D.Double getPeak(MyContour c)
	{
		double x= (c.getCenterX());
		double y= (c.getCenterY()-c.getHeight()/2);
		return new Point2D.Double(x,y);
		
	}
	@Deprecated
	public static Point2D.Double getPeak(ArrayList<Line> list, MyContour c)
	{
		int x= (int)(c.getCenterX());
		int y=0;
		Point2D.Double peak=null;
		ArrayList<Point2D.Double>points=new ArrayList<Point2D.Double>();
		for(int i =0; i<list.size()-1;i++)
		{
				for(int a=(int)(list.get(i).getX1());a<list.get(i).getX2();a++)
				{
					peak=new Point2D.Double(x,a);
					Line2D.Double l =new Line2D.Double(list.get(i).getX1(),list.get(i).getY1(),list.get(i).getX2(),list.get(i).getY2());
					if(l.contains(peak))
						return peak;
				}
		}
		return peak;
		
	}
	
	
	/**
	 * finds the peak in a list of t_lines
	 * @param list the list of t_lines
	 * @return the highest peak in the t_lines
	 */
	@Deprecated
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
			if(points.get(i).getY()<peak.getY())
				peak=points.get(i);
		}
		if(points.get(points.size()-1).getY()<peak.getY())
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
	 *
	 * @param d1 The first distance.
	 * @param d2 The second distance.
	 * @return The lowest value given, either d1 or d2.
	 */
	private static double returnLowestDouble(double d1, double d2) {
		return d1 < d2 ? d1 : d2;
	}

	/**
	 * Finds the horizontal t_lines in the list of t_lines and returns it
	 *
	 * @return all horizontal t_lines in the list
	 */
	@Deprecated
	public ArrayList<Line> getHorizontalLines() {
		ArrayList<Line> horizontalLines = new ArrayList<>();

		for (Line l : t_lines) {
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
