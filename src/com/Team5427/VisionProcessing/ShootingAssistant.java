package com.Team5427.VisionProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ShootingAssistant {

	private static HashMap<Double, Double> distancePower;

	static {
		distancePower = new HashMap<Double, Double>();

		try {
			Scanner scan = new Scanner(new File("src/com/Team5427/VisionProcessing/DistanceToPower.dat"));
			Scanner currentLine;
			String s;
			while (scan.hasNextLine()) {
				s = scan.nextLine();
				if (!s.contains("*") && s.length() > 4) {
					currentLine = new Scanner(s).useDelimiter("=");

					// System.out.println(Double.parseDouble(currentLine.next())+"
					// "+Double.parseDouble(currentLine.next()));
					// double d1 = Double.parseDouble(currentLine.next()), d2 =
					// Double.parseDouble(currentLine.next());

					// System.out.println(d1+", "+d2);

					distancePower.put(Double.parseDouble(currentLine.next()), Double.parseDouble(currentLine.next()));

				}
			}
			System.out.println(distancePower.toString());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 
	 * 
	 * @param distance
	 *            the distance from the goal to the robot
	 * @return
	 */
	public static double getShootingPower(double distance) {
		double remainder = ((distance % 2.0))*.5;
		int roundedDistance = (int)(distance-distance % 2.0);
		System.out.println("distance"+ roundedDistance);
				
		return (distancePower.get(roundedDistance*1.0) * (remainder))
				+ (distancePower.get(roundedDistance*1.0 + 2) * (1-remainder));
	}

}
