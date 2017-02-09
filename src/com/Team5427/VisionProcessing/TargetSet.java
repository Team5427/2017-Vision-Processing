package com.Team5427.VisionProcessing;

/**
 * Created by Charlemagne Wong on 2/8/2017.
 */
public class TargetSet {

    private Target top = null;
    private Target bottom = null;

    public TargetSet() {

    }

    public TargetSet(Target top, Target bottom) {
        this.top = top;
        this.bottom = bottom;
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
        double distance = -1;

        if (top != null)
            distance = top.getTowerDistance();

        if (bottom != null) {
            if (top == null)
                distance = bottom.getTowerDistance();
            else
                distance += bottom.getTowerDistance();
        }

        return distance;
    }

}
