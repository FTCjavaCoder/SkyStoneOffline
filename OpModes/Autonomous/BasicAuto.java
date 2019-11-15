package Skystone_14999.OpModes.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import TestOpModesOffline.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import Skystone_14999.DriveMotion.DriveMethods;
import Skystone_14999.OpModes.BasicOpMode;

@Autonomous(name="BasicAuto", group="Autonomous")
@Disabled
public class BasicAuto extends BasicOpMode {

    public DriveMethods drv = new DriveMethods();// call with drv.(drive method: driveFwdRev etc.)

    // motor position must be integer number of counts
    public int forwardPosition = 0;//Target position in forward direction for drive motors
    public int rightPosition = 0;//Target position in right direction for drive motors
    public int clockwisePosition = 0;//Target position in clockwise direction for drive motors
    public int slideDistance = 0;

    public int flStart;
    public int frStart;
    public int blStart;
    public int brStart;

    public double extraFwd = 0;
    public double sideColor = 1;// + for Blue, - for Red

    //Define all double variables
    public double start = 0;//timer variable to use for setting waits in the code
    public float hsvValues[] = {0F, 0F, 0F};
    public double offset = 0;
    public String colorFound = "No";

    public ElapsedTime runtime = new ElapsedTime(); //create a counter for elapsed time

    public double testDistFwd = 24;
    public double testAngleClock = 90;
    public double testDistRight = 24;

    public double sampleForward1 = 4;
    public double sampleRotate1;
    public double sampleForward2;
    public double markerRotate1;
    public double markerForward1;

    public double dtc1Rotate;
    public double dtc2Right;
    public double dtc3Forward;

    public double ctd1Back;
    public double ctd2Rotate;
    public double ctd3Forward;
    public double ctd4Rotate;
    public double ctd5Forward;
    public double btc1Back; // -60

    public double escapeFwd;
    public double escapeSideWays;
    public double parkRotate1;
    public double parkForward1;

    public String mineralGold = "Unknown";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    public VuforiaLocalizer vuforia;
    public boolean targetVisible = false;

    public VuforiaTrackables targetsSkyStone = null;

    public List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();

    @Override
    //    public void runOpMode() throws InterruptedException {
    public void runOpMode() {


    }

    public void initialize() {

        Billy.init(hardwareMap);

        //Motor configuration, recommend Not Changing - Set all motors to forward direction, positive = clockwise when viewed from shaft side
        Billy.frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        Billy.frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        Billy.backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        Billy.backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        Billy.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Billy.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Billy.backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Billy.backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Reset all motor encoders
        Billy.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Billy.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Billy.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Billy.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        Billy.frontLeft.setTargetPosition(0);
        Billy.frontRight.setTargetPosition(0);
        Billy.backLeft.setTargetPosition(0);
        Billy.backRight.setTargetPosition(0);

        //Set all motors to position mode (assumes that all motors have encoders on them)
        Billy.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Billy.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Billy.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Billy.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        Billy.frontLeft.setPower(0);
        Billy.frontRight.setPower(0);
        Billy.backLeft.setPower(0);
        Billy.backRight.setPower(0);

        VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
        stoneTarget.setName("Stone Target");
        VuforiaTrackable blueRearBridge = targetsSkyStone.get(1);
        blueRearBridge.setName("Blue Rear Bridge");
        VuforiaTrackable redRearBridge = targetsSkyStone.get(2);
        redRearBridge.setName("Red Rear Bridge");
        VuforiaTrackable redFrontBridge = targetsSkyStone.get(3);
        redFrontBridge.setName("Red Front Bridge");
        VuforiaTrackable blueFrontBridge = targetsSkyStone.get(4);
        blueFrontBridge.setName("Blue Front Bridge");
        VuforiaTrackable red1 = targetsSkyStone.get(5);
        red1.setName("Red Perimeter 1");
        VuforiaTrackable red2 = targetsSkyStone.get(6);
        red2.setName("Red Perimeter 2");
        VuforiaTrackable front1 = targetsSkyStone.get(7);
        front1.setName("Front Perimeter 1");
        VuforiaTrackable front2 = targetsSkyStone.get(8);
        front2.setName("Front Perimeter 2");
        VuforiaTrackable blue1 = targetsSkyStone.get(9);
        blue1.setName("Blue Perimeter 1");
        VuforiaTrackable blue2 = targetsSkyStone.get(10);
        blue2.setName("Blue Perimeter 2");
        VuforiaTrackable rear1 = targetsSkyStone.get(11);
        rear1.setName("Rear Perimeter 1");
        VuforiaTrackable rear2 = targetsSkyStone.get(12);
        rear2.setName("Rear Perimeter 2");

        allTrackables.addAll(targetsSkyStone);

        readOrWriteHashMap();

        //Indicate initialization complete and provide telemetry
        telemetry.addData("Status: ", "Initialized");
        telemetry.addData("Drive Motors", "FL (%.2f), FR (%.2f), BL (%.2f), BR (%.2f)", Billy.frontLeft.getPower(), Billy.frontRight.getPower(), Billy.backLeft.getPower(), Billy.backRight.getPower());
        telemetry.addData("Target Positions", "Forward (%d), Right (%d), Rotate (%d)", forwardPosition, rightPosition, clockwisePosition);
        telemetry.update();//Update telemetry to update display

    }// sets: RUN_TO_POSITION, ZeroPowerBehaviour.FLOAT, and 0 power & targetPos

    public void vuforiaStoneIdentifyLoop() {

        targetsSkyStone.activate();
        while (!isStopRequested()) {

            // check all the trackable targets to see which one (if any) is visible.
            targetVisible = false;
            for (VuforiaTrackable trackable : allTrackables) {
                if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                    telemetry.addData("Visible Target", trackable.getName());
                    targetVisible = true;
                    if (trackable.getName() == "Stone Target") {

                        telemetry.addLine("\n If statement successful for Stone Target \n");
                    }

                    break;
                }

            }
            if(!targetVisible) {
                telemetry.addData("Visible Target", "none");
            }
            telemetry.update();
        }
        targetsSkyStone.deactivate();
    }

//    public boolean vuforiaStoneIdentifyExit() {
//        boolean skystone = false;
//
//        targetsSkyStone.activate();
//        double start = runtime.time();
//        while (!isStopRequested() && !skystone && ( (runtime.time() - start) < 2) ) {
//
//            // check all the trackable targets to see which one (if any) is visible.
//            targetVisible = false;
//            for (VuforiaTrackable trackable : allTrackables) {
//                if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
//                    telemetry.addData("Visible Target", trackable.getName());
//                    targetVisible = true;
//
//                    if (trackable.getName() == "Stone Target") {
//
//                        skystone = true;
//                        telemetry.addLine("\n If statement successful for Stone Target \n");
//                    }
//
//                    break;
//                }
//            }
//
//            if(!targetVisible) {
//                telemetry.addData("Visible Target", "none");
//            }
//            //telemetry.update();
//        }
//        targetsSkyStone.deactivate();
//
//        return skystone;
//    }
    public boolean vuforiaStoneIdentifyExit(int loop, int desired) {
        boolean skystone = false;
        if(loop == desired){
            skystone = true;
        }

        return skystone;
    }
    // made for blue side only || sign multiplier for red/blue to be added
    public void fwdToStone() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,22, cons.pHM.get("drivePowerLimit").value, "Forward 22 inches",this);
    }

    public void nextStone() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,(8 * sideColor), cons.pHM.get("drivePowerLimit").value, "Right 8 inches",this);
    }

    public void grabSkyStone() {
        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 12, cons.pHM.get("drivePowerLimit").value, "Forward 8 inches",this);
        // needs to be longer previously 8""

        //grab skystone with gripper
        haveSkyStone = true;

    }

    public void moveAcrossBridge() {

        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 90 degrees CCW",this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,50 + extraFwd, cons.pHM.get("drivePowerLimit").value, "Forward 50 inches",this);
        //(5") add variable for additional forward distances depending on position of stone grabbed

    }

    public void placeStoneOnFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,4, cons.pHM.get("drivePowerLimit").value, "Forward 4 inches",this);

        //Place stone with gripper
        haveSkyStone = false;
    }

    public void brideCrossOutside() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-4, cons.pHM.get("drivePowerLimit").value, "Back 4 inches",this);
        //previously -14"

        moveAcrossBridge();

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,(-8 * sideColor), cons.pHM.get("drivePowerLimit").value, "Left 8 inches",this);

        placeStoneOnFoundation();
    }

    public void parkOutside() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-4, cons.pHM.get("drivePowerLimit").value, "Forward 4 inches",this);


        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,(8 * sideColor), cons.pHM.get("drivePowerLimit").value, "Right 16 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-25, cons.pHM.get("drivePowerLimit").value, "Back 25 inches",this);

    }

    public void brideCrossInside() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-30, cons.pHM.get("drivePowerLimit").value, "Back 32 inches",this);
        //previously -14"

        moveAcrossBridge();

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,(16 * sideColor), cons.pHM.get("drivePowerLimit").value, "Right 16 inches",this);

        placeStoneOnFoundation();

    }

    public void parkInside() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-4, cons.pHM.get("drivePowerLimit").value, "Forward 4 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,(-16 * sideColor), cons.pHM.get("drivePowerLimit").value, "Right 16 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-25, cons.pHM.get("drivePowerLimit").value, "Back 25 inches",this);

    }

    public void findSkyStone() {
        boolean skystoneFound = false;
        int looped = 0;

        while(looped < 2 && activeOpMode) {
            skystoneFound = vuforiaStoneIdentifyExit(looped, 1);

            if(skystoneFound) {

                telemetry.addLine("SkyStone Found");
                telemetry.update();

                if(looped == 1){

                    extraFwd = 8;
                }

                grabSkyStone();

                looped = 100;
            }
            else {

                telemetry.addLine("Next Stone");
                telemetry.update();

                nextStone();
                looped +=1;
            }
        }
        if(!skystoneFound) {

            telemetry.addLine("Third Stone");
            telemetry.update();

            extraFwd = 16;

            grabSkyStone();

        }

    }

    public void grabFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 30, cons.pHM.get("drivePowerLimit").value, "Forward 30 inches to Foundation", this);

        // grab foundation with gripper
        if (sideColor == 1) {
            haveBlueFoundation = true;
        }
        if (sideColor == -1) {
            haveRedFoundation = true;
        }
    }

    public void pullFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, -26, cons.pHM.get("drivePowerLimit").value, "Backward 26 inches with Foundation", this);

        // release foundation from gripper
        if (sideColor == 1) {
            haveBlueFoundation = false;
        }
        if (sideColor == -1) {
            haveRedFoundation = false;
        }
    }

    public void aroundFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (26 * sideColor), cons.pHM.get("drivePowerLimit").value, "Right 26 inches around Foundation", this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 18, cons.pHM.get("drivePowerLimit").value, "Forward 18 inches around Foundation", this);

    }

    public void pushFoundation() {
        // touching foundation to push it
        if (sideColor == 1) {
            haveBlueFoundation = true;
        }
        if (sideColor == -1) {
            haveRedFoundation = true;
        }

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (-4 * sideColor), cons.pHM.get("drivePowerLimit").value, "Left 4 inches pushing Foundation", this);

        if (sideColor == 1) {
            haveBlueFoundation = false;
        }
        if (sideColor == -1) {
            haveRedFoundation = false;
        }
    }

    public void awayFromFoundationInside() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, -16, cons.pHM.get("drivePowerLimit").value, "Left 4 inches pushing Foundation", this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate, (90 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 90 Degrees", this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 25, cons.pHM.get("drivePowerLimit").value, "Forward 25 inches to park", this);

    }

    public void awayFromFoundationOutside() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 8, cons.pHM.get("drivePowerLimit").value, "Left 4 inches pushing Foundation", this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate, (90 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 90 degrees", this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 25, cons.pHM.get("drivePowerLimit").value, "Forward 25 inches to park", this);

    }
}