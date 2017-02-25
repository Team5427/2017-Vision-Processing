package com.Team5427.VisionProcessing;

/**
 * Created by Charlemagne Wong on 2/8/2017.
 */
public class TargetSet {

    /**
     * Top tape on the tower
     */
    private Target top = null;
    /**
     * Bottom tape on the tower
     */
    private Target bottom = null;

    /**
     * Average distance from the camera to the tower
     */
    private double distance = -1;
    /**
     * States if all values have been updated.
     */
    private boolean updated = false;

    public TargetSet() {

    }

    public TargetSet(Target top, Target bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public TargetSet(TargetSet set) {

    }

    public Target getTop() {
        return top;
    }

    public void setTop(Target top) {
        this.top = top;
    }

    public Target getBottom() {
        return bottom;
    }

    public void setBottom(Target bottom) {
        this.bottom = bottom;
    }

    public double getDistanceToTower() {

        if (distance < 0 || !updated) {

            if (top != null) {
                distance = top.getTowerDistance();
            }

            if (bottom != null) {
                if (top == null)
                    distance = bottom.getTowerDistance();
                else
                    distance += bottom.getTowerDistance();
            }

            updated = true;
        }

        return distance;
    }

}
