package Skystone_14999.OpModes.Autonomous.PurePursuit;


import java.util.ArrayList;

import TestOpModesOffline.FieldLocation;

/**
 * Should these methods all more to RobotHardware class?
 * Angle, distance, intersection are all things acting on the robot or path
 * Being called in the RobotHardware drivePursuit so should be local to that
*/

public class PursuitMath {
    public PursuitMath(){
        //empty constructor
    }
    public static double wrap(double angle) {
        if (angle > 180) {//This is IF/THEN for the wrap routine
            angle -= 360;//Decrease angle for negative direction //rotation
        } else if (angle < -180) {
            angle += 360;//increase angle for positive direction //rotation 
        }

        return angle;
    }
    public static double findDistance(PursuitPoint point, PursuitPoint robot){
        return Math.sqrt(Math.pow(point.x - robot.x,2) + Math.pow(point.y - robot.y,2));//distance equation
    }
    public static double findAbsAngle(PursuitPoint point, FieldLocation robot){
        double angle = Math.atan2((point.y - robot.y) , (point.x - robot.x))*180/Math.PI;//angle in radians converted to degrees
        angle = Math.abs(wrap(angle) - wrap(-robot.theta));// wrap to +/-180 subtract robot heading and take absolute value
        return angle;
    }
    public static ArrayList<PursuitPoint> findIntersection(PursuitLines pl, double radius,PursuitPoint robot){
        ArrayList<PursuitPoint> PointList = new ArrayList<>();
        //avoid infinite or zero slope lines
        if(Math.abs(pl.x2 - pl.x1)<0.01){
            pl.x2 = pl.x1+0.01;
        }
        if(Math.abs(pl.y2 - pl.y1)<0.01){
            pl.y2 = pl.y1+0.01;
        }
        double minX = pl.x1 < pl.x2 ? pl.x1 : pl.x2;
        double maxX = pl.x1 > pl.x2 ? pl.x1 : pl.x2;
        //Determine quadratic formula constants
        double constb =2.0*pl.b*pl.slope;
        double const4ac =4.0*((1+Math.pow(pl.slope,2))*(Math.pow(pl.b,2)-Math.pow(radius,2)));
        double const2a=2.0*(1+Math.pow(pl.slope,2));

        //to compute quadratic - because sqrt, could be NaN
        try {

            double Xpos = ( -constb + Math.sqrt(Math.pow(constb, 2) - const4ac) )/ const2a;
            double Ypos = Xpos * pl.slope + pl.b;

            //validate that intersection is within line endpoints
            if ((Xpos >= pl.x1) && (Xpos <= pl.x2)) {
                PointList.add(new PursuitPoint(Xpos, Ypos));//include if in boundaries
            }

            double Xneg = ( -constb - Math.sqrt(Math.pow(constb, 2) - const4ac) )/ const2a;
            double Yneg = Xneg * pl.slope + pl.b;
            //validate that intersection is within line endpoints
            if ((Xneg >= minX) && (Xneg <= maxX)) {
                PointList.add(new PursuitPoint(Xneg, Yneg));//include if in boundaries
            }

        }catch(Exception e){

        }

        return PointList;

    }
}
