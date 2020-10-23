package Skystone_14999.OpModes.Autonomous.PurePursuit;

import android.graphics.Point;
import android.graphics.PointF;

import java.util.ArrayList;

import TestOpModesOffline.FieldLocation;
import Skystone_14999.OpModes.Autonomous.PurePursuit.PursuitMath;

public class PursuitPath {

    /**
     * PursuitPath holds an array of points that define the driving path for the robot
     * The methods within the class are used to define basic 2D geometries and append them to the
     * arraylist of points.  The robot hardware class HardwareBilly implements drivePursuit as the
     * means for the robot to follow the points.
     *
     *  --- Are these methods really needed since not invoked? ----
     *  Recommend that the ArrayList fieldPoints is defined in BasicAuto
     *  Recommend that the method to findPursuitPoint is added to RobotHardware
     */

    public ArrayList<PursuitLines> fieldLines = new ArrayList();
    public ArrayList<PursuitPoint> fieldPoints = new ArrayList();

    public ArrayList<PursuitLines> tempLocations = new ArrayList();
//    private final double TIME_STEP = 0.1;
//    private final int TOTAL_POINTS = (int) Math.round(30.0 / TIME_STEP);

    public PursuitPath() {
        //empty constructor to instantiate
    }

    public void appendPoints() {
        fieldLines.addAll(tempLocations);
        tempLocations.clear();
    }
    public void defineLine(double x1, double y1,double x2, double y2){
        PursuitLines L = new PursuitLines(x1, y1, x2, y2);
        fieldPoints.add(new PursuitPoint(x1,y1));
        fieldPoints.add(new PursuitPoint(x2,y2));

        fieldLines.add(L);

    }

    public void defineRectangle(double x1, double y1, double w, double h) {
        //Define rectangle from start x1, y1 to end at the same spot
        //w and l can be negative or positive
//        double ratioWidth = Math.abs(w)/(2*(Math.abs(w)+Math.abs(h)));
//        double ratioHeight = Math.abs(h)/(2*(Math.abs(w)+Math.abs(h)));
        PursuitLines L1;

        //define first X segment
        L1 = new PursuitLines(x1, y1, x1 + w, y1);
        fieldLines.add(L1);

        //define first Y segment
        L1 = new PursuitLines(x1 + w, y1, x1 + w, y1 + h);
        fieldLines.add(L1);

        //define second X segment
        L1 = new PursuitLines(x1 + w, y1 + h, x1, y1 + h);
        fieldLines.add(L1);

        //define second Y segment
        L1 = new PursuitLines(x1, y1 + h, x1, y1);
        fieldLines.add(L1);

    }

    public void defineArc(PursuitPoint center,double radius, double startAngleRad, double includedAngleRad,int points,pathDirection pd) {
        //Follow a circle of radius, offset by startAngleRad radians, for a total angle of includedAngleRad,
        //  divided into points
        double maxPoints = points;
        double scale = 1.0;
        if(pd.equals(pathDirection.NEGATIVE)){
            scale = -1.0;
        }
        for(int i =0; i<points;i++) {
            double angle = scale * (i/maxPoints) * includedAngleRad;
            double x = radius * Math.cos(angle - startAngleRad);
            double y = radius * Math.sin(angle - startAngleRad);
            fieldPoints.add(new PursuitPoint(x+center.x, y+center.y));
        }

    }

    /**
     * Relocate this findPursuitPoint to the RobotHardware class for supporting driving
     * Acts on path points, current point, and robot parameters (radius)
     */
    public PursuitPoint findPursuitPoint(ArrayList<PursuitPoint> inputPoints, FieldLocation robot, double radius) {

        PursuitPoint currentPoint = inputPoints.get(0);
        //set line starting point as default for starting to follow
//        PursuitPoint robotOffsetPoint = new PursuitPoint(robot.x,robot.y);
//        FieldLocation robotOffset = new FieldLocation(0,0,robot.theta);//create field location to pass robot angle as offset at (0,0)

        ArrayList<PursuitPoint> targetPoints = new ArrayList<>();

        double currentAngle = 1000;

// Search all lines for the valid intersections with the robot radius and that line withi its endpoints
//        robotOffsetPoint.offset(robot.x, robot.y);//offset point for robot to be (0,0)
//        currentPoint.offset(robot.x,robot.y);
        targetPoints.add(currentPoint);//Ensure a minimum of 1 point to search
//        ArrayList<PursuitPoint> PointList = new ArrayList<>();

        for(int i = 0; i<inputPoints.size()-1;i++) {

//            pl.offset(robot.x, robot.y);//offset points for robot to be (0,0) for circle math
            PursuitPoint first = new PursuitPoint(inputPoints.get(i).x,inputPoints.get(i).y);
            PursuitPoint second = new PursuitPoint(inputPoints.get(i+1).x,inputPoints.get(i+1).y);

            //offset lines for robot being moved to (0,0);
            first.offset(robot.x,robot.y);
            second.offset(robot.x,robot.y);

            PursuitLines pl = new PursuitLines(first,second);

            //avoid infinite or zero slope lines
            if(Math.abs(pl.x2 - pl.x1)<0.01){
                pl.x2 = pl.x1+0.01;
            }
            if(Math.abs(pl.y2 - pl.y1)<0.01){
                pl.y2 = pl.y1+0.01;
            }

            pl.calcSlope();
            //Determine quadratic formula constants
            double constb =2.0*pl.b*pl.slope;
            double const4ac =4.0 * ( (1+Math.pow(pl.slope,2)) * (Math.pow(pl.b,2) - Math.pow(radius,2)) );
            double const2a=2.0 * (1+Math.pow(pl.slope,2));

            double Xpos;double Xneg;double Ypos;double Yneg;
            //to compute quadratic - because sqrt, could be imaginary, so if statement puts imaginary values off the line
            if(const4ac > Math.pow(constb, 2)) {
                Xpos = -1000;
                Xneg = -1000;
            }
            else {

                Xpos = ( -constb + Math.sqrt(Math.pow(constb, 2) - const4ac) ) / const2a;
                Xneg = ( -constb - Math.sqrt(Math.pow(constb, 2) - const4ac) ) / const2a;

            }
            Ypos = Xpos * pl.slope + pl.b;
            Yneg = Xneg * pl.slope + pl.b;

            PursuitPoint PosPoint = new PursuitPoint(Xpos, Ypos);
            PursuitPoint NegPoint = new PursuitPoint(Xneg, Yneg);



            //Offset back into the actual coordinates - robot != (0,0)
            PosPoint.offset(-robot.x,-robot.y);
            NegPoint.offset(-robot.x,-robot.y);

//            Xpos -= robot.x;
//            Xneg -= robot.x;
//
//            Ypos -= robot.y;
//            Yneg -= robot.y;

            pl.offset(-robot.x,-robot.y);
            double minX;// = pl.x1 < pl.x2 ? pl.x1 : pl.x2;
            double maxX;// = pl.x1 > pl.x2 ? pl.x1 : pl.x2;
            if(pl.x1 < pl.x2){minX = pl.x1;}
            else{ minX = pl.x2;}
            if(pl.x1 > pl.x2){maxX = pl.x1;}
            else{ maxX = pl.x2;}
//            double minX = -30.0;
//            double maxX = 0.0;

            //validate that intersection is within line endpoints
            if ((PosPoint.x >= minX) && (PosPoint.x <= maxX)) {
                targetPoints.add(PosPoint);//include if in boundaries
            }


            //validate that intersection is within line endpoints
            if ((NegPoint.x >= minX) && (NegPoint.x <= maxX)) {
                targetPoints.add(NegPoint);//include if in boundaries
            }

        }
        //Search list of intersection points  for closest angle to robot
        for(PursuitPoint testPoint:targetPoints){
            double pointAngle = PursuitMath.findAbsAngle(testPoint,robot);
            if(pointAngle <= currentAngle){
                currentPoint = testPoint;
                currentAngle = pointAngle;
            }
        }

        return currentPoint;
    }

    public enum pathDirection {POSITIVE,NEGATIVE};
}
