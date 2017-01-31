package com.Team5427.VisionProcessing;

import com.Team5427.Networking.Server;
import com.Team5427.res.Config;
import com.github.sarxos.webcam.Webcam;

import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GraphicsPanel extends JPanel implements KeyListener {

	public static String IP_CAMERA_URL = getIPFromText();
	public static final Dimension RESOLUTION = new Dimension(640, 480);

	// Game info
	public static final int DEFAULT = 0;
	public static final int AUTONOMOUS = 1;
	public static final int TELEOP = 2;

	public static int gameStatus = DEFAULT;
	/**
	 * The time set for countdown
	 */
	private static long gameTimerEnd = System.currentTimeMillis();

	public static double pixelsToGoal;

	private int width, height;
	private BufferedImage buffer;
	public Webcam webcam;

	private BufferedImage cameraImg;

	Scanner scanner;

	private static ArrayList<Color> colorList;
	private double previousFrameTime = 0; // Previous System nanotime for last

	// frame
	private BufferedImage panelImage;

	private boolean donePainting = false;

	public GraphicsPanel(int width, int height) {

		super();

		this.width = width;
		this.height = height;

		setSize(width, height);

		buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		scanner = new Scanner(System.in);

		addKeyListener(this);

		// Creates a new webcam
		try {
			initializeCamera();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// new Thread(this).start();
	}

	/**
	 * Initializes the camera for use
	 */
	static {
		Webcam.setDriver(new IpCamDriver());
		calculateVerticalFOV();

		// Color ArrayList
		colorList = new ArrayList<>();
		colorList.add(Color.BLUE);
		colorList.add(Color.GREEN);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.CYAN);
	}

	public static void taskCommand(String s) {
//			gameStatus = AUTONOMOUS;
//		} else if (s.contains(ByteDictionary.TELEOP_START)) {
//			gameTimerEnd = System.currentTimeMillis() + Config.TELEOP_TIME * 1000;
//			gameStatus = TELEOP;
//		}

		// TODO default mode was not implemented because t was the default for
		// the switch loop. I don't really know what you were trying to do there
		// Charlie.
	}

	public void initializeCamera() throws MalformedURLException {

		IpCamDeviceRegistry.register(new IpCamDevice("Robot Vision", IP_CAMERA_URL, IpCamMode.PUSH));

		try {
			webcam = Webcam.getWebcams().get(0);
			System.out.println("Command recieved from roborio...");
//
//		TODO uncomment this when it works with Bytes
//		if (s.contains(ByteDictionary.AUTO_START)) {
//			gameTimerEnd = System.currentTimeMillis() + Config.AUTO_TIME * 1000;
			webcam.setViewSize(RESOLUTION); // Sets the correct RESOLUTION
			webcam.open(); // I think this "opens" the camera. This line is
			// needed
		} catch (NoClassDefFoundError e) {
			System.err.println("Cannot find IP camera");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addNotify() {
		super.addNotify();
		requestFocus();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		char key = Character.toLowerCase(e.getKeyChar());

		if (key == 'c') {
			initializeCalibration();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char key = Character.toLowerCase(e.getKeyChar());
	}

	/**
	 * Initializes the calibration sequence
	 */
	@SuppressWarnings({ "unused", "resource" })
	public void initializeCalibration() {
		System.out.println("===FOV Calibration===");

		if (Config.ENABLE_FOV_CALIBRATION && Main.goals != null && Main.goals.size() == 1) {
			Goal g = Main.goals.get(0);

			System.out.print("Do you want to calibrate the camera? (y,n): ");
			char input;

			try {
				input = scanner.next().charAt(0);
			} catch (InputMismatchException e) {
				System.out.println("\nThe selection you entered is invalid.");
				input = 'n';
			}

			input = Character.toLowerCase(input);

			if (input == 'y') {
				System.out.print("Enter the distance from the camera to the goal, NOT the turret to the goal.");
				try {
					double distance = new Scanner(System.in).nextDouble();

					double angle = calibrateCameraAngle(g, distance);

					System.out.println("Calibration completed. The new camera angle is: " + angle);
				} catch (Exception e) {
					System.out.println("\n\tInvalid Input. Exiting calibration...");
				}
			} else
				System.out.println("\nExiting calibration.");

		} else {
			System.out.println("Unable to calibrate");

			if (Main.goals == null)
				System.out.println("Goals is null");
			else if (Main.goals.size() > 1)
				System.out.println("There are " + Main.goals.size()
						+ " goals. Only 1 must visible in the camera for calibration.");
			else if (Main.goals.size() == 0) {
				System.out.println("There are no goals found.");
			} else if (!Config.ENABLE_FOV_CALIBRATION)
				System.out.println("FOV Calibration has been disabled.");
			System.out.println("\nExiting calibration.");
		}
	}

	/**
	 * Calibrates the angle of the robot using angles
	 *
	 * @param goal
	 *            Goal used as a reference to calculate the camera's angle
	 * @param distance
	 *            the actual distance from the camera to the robot
	 * @return the new starting angle of the camera
	 */
	public static double calibrateCameraAngle(Goal goal, double distance) {
		double theta = Math.asin((Config.TARGET_HEIGHT_TOP - Config.ROBOT_HEIGHT) / distance);
		theta -= goal.getCameraAngleY();

		Config.CAMERA_START_ANGLE = Math.toDegrees(theta);

		return Config.CAMERA_START_ANGLE;
	}

	public static void calculateVerticalFOV() {
		pixelsToGoal = (RESOLUTION.getWidth() / 2) / Math.tan(Math.toRadians(Config.horizontalFOV / 2));

		Config.verticalFOV = Math.toDegrees(RESOLUTION.getHeight() / 2 / pixelsToGoal) * 2;
		System.out.println(Config.verticalFOV);

	}

	public boolean isDonePainting() {
		return donePainting;
	}

	public void setDonePainting(boolean donePainting) {
		this.donePainting = donePainting;
	}

	@Override
	public synchronized void paint(Graphics g) {
		Graphics bg = buffer.getGraphics();


		

		int xStart = getWidth() / 4;
		int yStart = (int) RESOLUTION.getHeight();

		double timeDifference = -1;

		bg.setColor(Color.BLACK);
		bg.fillRect(0, 0, getWidth(), getHeight());

		// Drawing the panel in the bottom
		if (panelImage == null) {
			panelImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics pg = panelImage.getGraphics();
			pg.setColor(Color.GRAY);
			pg.fillRect(0, (int) RESOLUTION.getHeight() + 1, getWidth(), getHeight() - (int) RESOLUTION.getHeight());

			pg.setColor(Color.BLACK);
			for (int i = 0; i < 3; i++) {
				int xPos = xStart * (i + 1);
				pg.drawLine(xPos, (int) RESOLUTION.getHeight(), xPos, getHeight());

				pg.setColor(Color.BLACK);
				pg.setFont(new Font("Arial", Font.BOLD, 16));

				xPos = xStart * i + 10;
				pg.fillRect(xPos, yStart + 10, 20, 20);
				pg.drawString("Goal " + (i + 1), xPos + 50, yStart + 27);
			}
		} else {
			bg.drawImage(panelImage, 0, 0, null);
		}

		// Gets image from camera and paints it to the buffer
		if (webcam != null) {
			cameraImg = webcam.getImage();
			timeDifference = System.nanoTime() - previousFrameTime;
			previousFrameTime = System.nanoTime();
			bg.drawImage(cameraImg, 0, 0, null);

		}

		int x1, y1, x2, y2;
		for (int i = 0; i < Main.goals.size(); i++) {

			int j = i;
			while (j > colorList.size())
				j = i % colorList.size();

			bg.setColor(colorList.get(j));
			// bg.setColor(Color.GREEN);
			bg.drawLine((int) Main.goals.get(i).getCenterLine().getX1(),
					(int) Main.goals.get(i).getCenterLine().getY1(), (int) Main.goals.get(i).getCenterLine().getX2(),
					(int) Main.goals.get(i).getCenterLine().getY2());

			// bg.setColor(Color.BLUE);
			bg.drawLine((int) Main.goals.get(i).getLeftLine().getX1(), (int) Main.goals.get(i).getLeftLine().getY1(),
					(int) Main.goals.get(i).getLeftLine().getX2(), (int) Main.goals.get(i).getLeftLine().getY2());

			// bg.setColor(Color.RED);
			bg.drawLine((int) Main.goals.get(i).getRightLine().getX1(), (int) Main.goals.get(i).getRightLine().getY1(),
					(int) Main.goals.get(i).getRightLine().getX2(), (int) Main.goals.get(i).getRightLine().getY2());

		}

		/* Draws distance of goal on the bottom left */
		// This can be later merged the for each loop that draws the lines. This
		// is temporary for readability
		bg.setFont(new Font("Arial", Font.BOLD, 12));

		// Printing data for each goal
		for (int i = 0; i < Main.goals.size(); i++) {

			// bg.setColor(new Color(255, 255, 255, 150));

			/*
			 * int x = (int) Main.goals.get(i).getCenterLine().getX1() - 8; int
			 * y = (int) Main.goals.get(i).getCenterLine().getY1() + 15;
			 */
			int x = xStart * i + 10;
			int y = yStart + 50;

			// bg.fillRect(x - 3, y - 10, 100, 48);

			bg.setColor(colorList.get(i));
			int xPos = xStart * i + 10;
			bg.fillRect(xPos, yStart + 10, 20, 20);

			bg.setColor(Color.BLACK);

			String distance = String.format("%.2f", Main.goals.get(i).getGoalDistanceCamera());
			String distanceToBase = String.format("%.2f", Main.goals.get(i).getGoalDistanceCamera());
			String angleDegrees = String.format("%.2f", Math.toDegrees(Main.goals.get(i).getAngleOfElevation()));
			String horizontalAngle = String.format("%.2f", Math.toDegrees(Main.goals.get(i).getTurretXAngle())); //used to be getCameraAngle, damn it Charlie

			System.out.println("Distance: " + distance + "in." + "    Elevation Angle: " + angleDegrees + "°"
					+ "     Horizontal Angle: " + horizontalAngle + "°");

			int interval = 15;
			bg.drawString("Distance: " + distance + "in.", x, y);
			bg.drawString("Elevation Angle: " + angleDegrees + "°", x, y += interval);
			bg.drawString("Horizontal Angle: " + horizontalAngle + "°", x, y += interval);
			bg.drawString("Horizontal Angle: " + horizontalAngle + "°", x, y += interval);
			bg.drawString("Angle Check: " + Main.goals.get(i).getAngleStatus() , x, y+=interval);
			bg.drawString("Distance Check: " + Main.goals.get(i).getDistanceStatus() , x, y+=interval);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		// Paints data from the roborio if connection is established
		bg.setFont(new Font("Arial", Font.BOLD, 12));
		if (Server.hasConnection()) {
			bg.setColor(Color.GREEN);
			bg.fillOval(489, 493, 10, 10);
			bg.drawString("Connected to Roborio", 510, 503);
		} else {
			bg.setColor(Color.RED);
			bg.fillOval(490, 493, 10, 10);
			bg.drawString("No Connection", 522, 503);
		}

		// Paints game status
		int statusX = 490;
		int statusY = 520;
		bg.setFont(new Font("Arial", Font.BOLD, 10));
		bg.setColor(Color.CYAN);
		bg.drawString("Game: ", statusX, statusY);
		statusX += 35;

		if (gameStatus == DEFAULT) {
			bg.setColor(Color.RED);
			bg.drawString("Default", statusX, statusY);
		} else if (gameStatus == AUTONOMOUS) {
			bg.setColor(Color.YELLOW);
			bg.drawString("Autonomous", statusX, statusY);
		} else if (gameStatus == TELEOP) {
			bg.setColor(Color.GREEN);
			bg.drawString("Teleop", statusX, statusY);
		}

		// Second possible timer
		int timerWidth = 60, timerHeight = 30;
		int startTimerX = (int) (RESOLUTION.getWidth() / 2 + .5) - timerWidth / 2;
		int startTimerY = (int) (RESOLUTION.getHeight() + 1) - timerHeight;

		bg.setColor(new Color(207, 255, 184, 122));
//		bg.fillRect(getWidth()/2-5, 0, 10,(int) RESOLUTION.getHeight());

		bg.fillRect(getWidth()/2-10, 0, 8,(int) RESOLUTION.getHeight());
		bg.fillRect(0, 16, (int) RESOLUTION.getWidth(), 10);

		bg.setColor(new Color(255, 0, 41, 150));
		bg.fillRect(startTimerX, startTimerY, timerWidth, timerHeight);

		// TODO: delete the small timer in the corner

		// Paints the timer
		int timeMinutes;
		int timeSecondsTens;
		int timeSecondsOnes;
		bg.setColor(Color.CYAN);
		if (gameTimerEnd > System.currentTimeMillis()) {
			timeMinutes = (int) ((gameTimerEnd - System.currentTimeMillis()) / 60000);
			timeSecondsOnes = (int) (((gameTimerEnd - System.currentTimeMillis()) / 1000. % 60) + 1);
			timeSecondsTens = timeSecondsOnes / 10;
			timeSecondsOnes %= 10;

			if (timeSecondsTens == 6) {
				timeMinutes++;
				timeSecondsTens = 0;
			}

			// bg.drawString("Time Remaining: " + timeMinutes + ":" +
			// timeSecondTens + (timeSecondsOnes % 10), 509, 600);

		} else {
			// bg.drawString("Time Remaining: 0:00" , 509, 600);

			timeMinutes = 0;
			timeSecondsTens = 0;
			timeSecondsOnes = 0;

			if (gameStatus != DEFAULT)
				gameStatus = DEFAULT;
		}

		bg.setFont(new Font("Arial", Font.BOLD, 20));
		bg.setColor(Color.BLACK);
		bg.drawString(timeMinutes + ":" + timeSecondsTens + timeSecondsOnes, 300, 475);

		// Draws frame rate
		bg.setColor(Color.GREEN);
		bg.setFont(new Font("Arial Narrow", Font.PLAIN, 14));
		/*
		 * double FPS = 1000000000 / timeDifference; String fpsOutput =
		 * String.format("%.2f", FPS);
		 */

		//testing



		bg.drawString("FPS: " + Main.FPS, 2, 14);


		////////////////////TEMP///////////////////
//		BufferedImage testImg = null;
//		try {
//			testImg = ImageIO.read(new File("C:/Users/Blackhawk/Desktop/newimg.png"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		bg.drawImage(testImg,-2, 0, null);
//
//		bg.setColor(new Color(255, 234, 0, 122));


		g.drawImage(buffer, 0, 0, null);


		donePainting = true;
	}

	public static String getIPFromText() {
		String address = "";
		Scanner scanner = null;
		
		try {
			scanner = new Scanner(new File("IPaddress.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		address = scanner.nextLine();

		System.out.println("IP Address: " + address);
		
		return address;
	}
}
