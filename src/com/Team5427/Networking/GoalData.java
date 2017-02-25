package com.Team5427.Networking;

import java.io.Serializable;
import java.nio.ByteBuffer;

//import com.Team5427.VisionProcessing.Goal;

import java.io.Serializable;

/**
 * The object that will be sent from the driver station and will be received by
 * the robot. This class only contains data that may be useful for the robot.
 */
public class GoalData implements Serializable {

	/**
	 * Distance between the robot and the goal. The value is
	 */
	private double distance;
	/**
	 * The angle of elevation from the robot to the goal
	 */
	private double angleOfElevation;
	/**
	 * The horizontal angle from the camera to the
	 */
	private double horizontalAngle;
	/**
	 * The value that the motor needs to be set at for the given distance
	 */
	private double motorValue;

	public GoalData(double distance, double angleOfElevation, double horiztonalAngle, double motorValue) {
		this.distance = distance;
		this.angleOfElevation = angleOfElevation;
		this.horizontalAngle = horiztonalAngle;
		this.motorValue = motorValue;
	}

	public GoalData(byte[] buff) {
		setByteBuffer(buff);
	}

	/**
	 * TODO: Change hardcode of index 1 to use with ByteDictionary
	 *
	 * Scans the received byte buffer. If the data can be used and is
	 * successfully set accordingly, then this method will return true. If the
	 * byte array is scanned to be an incorrect type as indicated in the
	 * ByteDictionary for a goal data, then the method will return false.
	 *
	 * The buffer is required to have a size of 17 (index 0 for type, 1-8 for
	 * speed, and 9-16 for the x angle).
	 *
	 * @param buff
	 *            array to be used for setting the data
	 * @return true if data is valid and set according. False if otherwise
	 */
	public boolean setByteBuffer(byte[] buff) {
		if (buff[0] == 1) {
			motorValue = ByteBuffer.wrap(buff, 1, 8).getDouble();
			horizontalAngle = ByteBuffer.wrap(buff, 9, 8).getDouble();
		}

		return false;
	}

	/**
	 * TODO, temp remove when working
	 * 
	 * @return
	 */
	public String printBuff(byte[] buff) {

		String s = "";

		for (byte b : buff) {
			s += b + ", ";
		}
		return s;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getAngleOfElevation() {
		return angleOfElevation;
	}

	public void setAngleOfElevation(double angleOfElevation) {
		this.angleOfElevation = angleOfElevation;
	}

	public double getHorizontalAngle() {
		return horizontalAngle;
	}

	public void setHorizontalAngle(double horizontalAngle) {
		this.horizontalAngle = horizontalAngle;
	}

	public double getMotorValue() {
		return motorValue;
	}

	/**
	 * The toString use by the network to identify the class type
	 *
	 * @return the class type for networking use
	 */
	public String toString() {
		return "Team 5427 - GoalData " + motorValue + "  " + Math.toDegrees(horizontalAngle);
	}
}