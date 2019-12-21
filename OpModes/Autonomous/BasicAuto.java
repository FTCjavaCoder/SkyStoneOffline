package Skystone_14999.OpModes.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import TestOpModesOffline.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
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
    public double stoneSideways = 0;
    public double sideGrabStone = 10;
    public double foundationPosChange = 0;// 26 for unmoved Foundation, 0 for moved Foundation
    public double insideOutside = 0;// 0 for Inside, 24 for Outside
    public double foundationInOut = 26;// 0 for Inside, 26 for Outside
    public double foundationPush = 8;
    public double sideColor = 1;// + for Blue, - for Red, KEEP BLUE

    public int stoneSelect = 0;//KS added 12/20/19 as variable to set in initialization to location stones

    //Define all double variables
    public double start = 0;//timer variable to use for setting waits in the code
    public float hsvValues[] = {0F, 0F, 0F};
    public double offset = 0;
    public String stonePos = "Unknown";

    public ElapsedTime runtime = new ElapsedTime(); //create a counter for elapsed time

//    public double testDistFwd = 24;
//    public double testAngleClock = 90;
//    public double testDistRight = 24;
//
//    public double sampleForward1 = 4;
//    public double sampleRotate1;
//    public double sampleForward2;
//    public double markerRotate1;
//    public double markerForward1;
//
//    public double dtc1Rotate;
//    public double dtc2Right;
//    public double dtc3Forward;
//
//    public double ctd1Back;
//    public double ctd2Rotate;
//    public double ctd3Forward;
//    public double ctd4Rotate;
//    public double ctd5Forward;
//    public double btc1Back; // -60
//
//    public double escapeFwd;
//    public double escapeSideWays;
//    public double parkRotate1;
//    public double parkForward1;
//
//    public String mineralGold = "Unknown";

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

    public void vuforiaStoneIdentifyExitNew() {

        if(sideColor == 1) {
            if(stoneSelect == 0) {
                stonePos = "Left";
                stoneSideways = -12;
            }
            if(stoneSelect == 1) {
                stonePos = "Center";
                stoneSideways = -4;
            }
            if(stoneSelect == 2) {
                stonePos = "Right";
                stoneSideways = 4;
            }
        }

        if(sideColor == -1) {
            if(stoneSelect == 0) {
                stonePos = "Right";
                stoneSideways = -12;
            }
            if(stoneSelect == 1) {
                stonePos = "Center";
                stoneSideways = -4;
            }
            if(stoneSelect == 2) {
                stonePos = "Left";
                stoneSideways = 4;
            }
        }

    }

    public void fwdToStone() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,22, cons.pHM.get("drivePowerLimit").value, "Forward 22 inches",this);

    }

    public void nextStone() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,8*sideColor,cons.pHM.get("drivePowerLimit").value / 2, "Right 8 inches",this);
        //KS: 12/20/19 Needed sideColor to change R/L

//        angleUnWrap();
//
//        drv.driveGeneral(DriveMethods.moveDirection.Rotate,( (0 - robotHeading) * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate to 0 degrees CCW",this);

    }

    public void grabSkyStone() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 8, cons.pHM.get("drivePowerLimit").value, "Forward 8 inches",this);
        // needs to be longer previously 12""
        if(sideColor == 1) {Billy.servoBlueStoneGrab.setPosition(1);}
        else if(sideColor == -1) {Billy.servoRedStoneGrab.setPosition(1);}
        angleUnWrap();
        updateIMU();
        //grab skystone with gripper
        if(sideColor == 1) {haveBlueSkyStone1 = true;}
        else if(sideColor == -1) {haveRedSkyStone1 = true;}

    }

    //KS: added method below 12/20/19 - needed means to identify 2nd stone as grabbed
    //Added 2nd method but could use 1 method with indicator
    public void grabSkyStone2() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 8, cons.pHM.get("drivePowerLimit").value, "Forward 8 inches",this);
        // needs to be longer previously 12""
        if(sideColor == 1) {Billy.servoBlueStoneGrab.setPosition(1);}
        else if(sideColor == -1) {Billy.servoRedStoneGrab.setPosition(1);}
        angleUnWrap();
        updateIMU();
        //grab skystone with gripper
        if(sideColor == 1) {haveBlueSkyStone2 = true;}
        else if(sideColor == -1) {haveRedSkyStone2 = true;}

    }

    public void moveAcrossBridge() {

        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 90 degrees CCW",this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,46 + extraFwd, cons.pHM.get("drivePowerLimit").value, "Forward 50 inches",this);

    }

    public void placeStoneOnFoundation() {

        //move jack to be above Foundation IS 3

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,(8 - (foundationPosChange/13)), cons.pHM.get("drivePowerLimit").value, "Forward 4 inches",this);

        //Place stone with gripper
        if(sideColor == 1) {haveBlueSkyStone1 = false;}
        else if(sideColor == -1) {haveRedSkyStone1 = false;}
    }

    public void bridgeCrossSkyStone() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,(-4 - insideOutside), cons.pHM.get("drivePowerLimit").value, "Back 4 inches",this);
        //previously -14"

        moveAcrossBridge();

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,((insideOutside + foundationPosChange) * sideColor), cons.pHM.get("drivePowerLimit").value / 2, "Right 8 inches",this);

//        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-180 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 180 degrees CCW",this);

//        placeStoneOnFoundation();
    }

    public void grabAndRotateFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,6, cons.pHM.get("drivePowerLimit").value, "Backward 4 inches",this);

        // grab foundation with servos
        if(sideColor == 1) {

            haveBlueFoundation = true;
        }
        else if(sideColor == -1) {

            haveRedFoundation = true;
        }

        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-48 * sideColor), cons.pHM.get("drivePowerLimit").value, "Rotate 48 degrees CCW",this);

    }

    public void straightToCorner() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,20, cons.pHM.get("drivePowerLimit").value, "Back 25 inches",this);

        //Place stone with gripper
        if(sideColor == 1) {haveBlueSkyStone1 = false;}
        else if(sideColor == -1) {haveRedSkyStone1 = false;}
        if(sideColor == 1) {

            haveBlueFoundation = false;
        }
        else if(sideColor == -1) {

            haveRedFoundation = false;
        }

    }

    public void backSkyStoneAndFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-15, cons.pHM.get("drivePowerLimit").value, "Backwards 15 inches",this);//was -20

        //Stow gripper

        drv.driveGeneral(DriveMethods.moveDirection.Rotate, 48, cons.pHM.get("rotatePowerLimit").value, "Rotate 45 degrees CW", this);
    }

    // get SkyStone first then do move foundation then place block
    public void parkSkyStoneF() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,((-insideOutside - foundationPosChange) * sideColor), cons.pHM.get("drivePowerLimit").value / 2, "Sideways 0-50ish inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-13, cons.pHM.get("drivePowerLimit").value, "Backward 13 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-11, cons.pHM.get("drivePowerLimit").value, "Backward 11 inches",this);

    }

    public void parkSkyStone() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-(8 - (foundationPosChange/13)), cons.pHM.get("drivePowerLimit").value, "Backward  inches",this);

        // move jack down

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,((-(-4 + insideOutside + foundationPosChange)) * sideColor), cons.pHM.get("drivePowerLimit").value / 2, "Right 16 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-25, cons.pHM.get("drivePowerLimit").value, "Back 25 inches",this);

    }

    public void crossDropStonePark() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-4, cons.pHM.get("drivePowerLimit").value, "Back 4 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 90 degrees CCW",this);

        angleUnWrap();// think about commenting

//        telemetry.addData("robotHeading: (%.2f)", robotHeading);

//        if(sideColor == 1) {
//
//            drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 - robotHeading), cons.pHM.get("rotatePowerLimit").value, "Rotate to 90 degrees CCW",this);
//
//        }

//        drv.driveGeneral(DriveMethods.moveDirection.Rotate,( (-90 * sideColor) - robotHeading), cons.pHM.get("rotatePowerLimit").value, "Rotate to 90 degrees CCW",this);

        //pressAToContinue();

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,cons.pHM.get("dropStoneForward").value + extraFwd, cons.pHM.get("drivePowerLimit").value, "Forward 35+ inches",this);//was 48

        //Drop stone with gripper
        if(sideColor == 1) {haveBlueSkyStone1 = false;}
        else if(sideColor == -1) {haveRedSkyStone1 = false;}

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-6, cons.pHM.get("drivePowerLimit").value, "Back 6 inches",this);//was 4

    }

    public void crossDropStoneFor2Old() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-4, cons.pHM.get("drivePowerLimit").value, "Back 4 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 90 degrees CCW",this);

        angleUnWrap();// think about commenting

//        telemetry.addData("robotHeading: (%.2f)", robotHeading);

//        if(sideColor == 1) {
//
//            drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 - robotHeading), cons.pHM.get("rotatePowerLimit").value, "Rotate to 90 degrees CCW",this);
//
//        }

//        drv.driveGeneral(DriveMethods.moveDirection.Rotate,( (-90 * sideColor) - robotHeading), cons.pHM.get("rotatePowerLimit").value, "Rotate to 90 degrees CCW",this);

        //pressAToContinue();

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 35 + extraFwd, cons.pHM.get("drivePowerLimit").value, "Forward 35+ inches",this);//was 48
        if(sideColor == 1) {Billy.servoBlueStoneGrab.setPosition(0);}
        else if(sideColor == -1) {Billy.servoRedStoneGrab.setPosition(0);}
        angleUnWrap();
        updateIMU();
        //Drop stone with gripper
        if(sideColor == 1) {haveBlueSkyStone1 = false;}
        else if(sideColor == -1) {haveRedSkyStone1 = false;}

    }

    public void getSecondStoneOld() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, -35 - extraFwd - 24, cons.pHM.get("drivePowerLimit").value,"Backward to second stone",this);//was 48

        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(90 * sideColor), cons.pHM.get("rotatePowerLimit").value,"Rotate 90 degrees CW",this);

        grabSkyStone2(); //KS: added 12/20/19 - needed means to identify 2nd stone as grabbed

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-8, cons.pHM.get("drivePowerLimit").value, "Back 4 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 * sideColor), cons.pHM.get("rotatePowerLimit").value, "Rotate 90 degrees CCW",this);

//        angleUnWrap();// think about commenting
//
//        telemetry.addData("robotHeading: (%.2f)", robotHeading);
//
//        if(sideColor == 1) {
//
//            drv.driveGeneral(DriveMethods.moveDirection.Rotate,(-90 - robotHeading), cons.pHM.get("rotatePowerLimit").value, "Rotate to 90 degrees CCW",this);
//
//        }
//
//        drv.driveGeneral(DriveMethods.moveDirection.Rotate,( (-90 * sideColor) - robotHeading), cons.pHM.get("rotatePowerLimit").value, "Rotate to 90 degrees CCW",this);

        //pressAToContinue();

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,35 + extraFwd + 24, cons.pHM.get("drivePowerLimit").value, "Forward with second stone",this);//was 48
        if(sideColor == 1) {Billy.servoBlueStoneGrab.setPosition(0);}
        else if(sideColor == -1) {Billy.servoRedStoneGrab.setPosition(0);}
        angleUnWrap();
        updateIMU();
        //Drop stone with gripper
        if(sideColor == 1) {haveBlueSkyStone2 = false;}
        else if(sideColor == -1) {haveRedSkyStone2 = false;}
        drv.driveGeneral(DriveMethods.moveDirection.FwdBack,-6, cons.pHM.get("drivePowerLimit").value, "Back 6 inches",this);//was 4

    }

    public void goToStone() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (stoneSideways * sideColor), cons.pHM.get("drivePowerLimit").value, "Sideways to face stone",this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate, (-90 * sideColor), cons.pHM.get("drivePowerLimit").value, "Rotate 90 degrees CCW",this);

    }

    public void takeStone1() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, sideGrabStone * sideColor, cons.pHM.get("drivePowerLimit").value, "Right 8 inches",this);

        if(sideColor == 1) {Billy.servoBlueStoneGrab.setPosition(1);}
        else if(sideColor == -1) {Billy.servoRedStoneGrab.setPosition(1);}
        //grab skystone with gripper
        if(sideColor == 1) {haveBlueSkyStone1 = true;}
        else if(sideColor == -1) {haveRedSkyStone1 = true;}

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (-sideGrabStone / 2) * sideColor, cons.pHM.get("drivePowerLimit").value, "Left 8 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate, (-90 * sideColor), cons.pHM.get("drivePowerLimit").value, "Rotate 90 degrees CCW",this);

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (45 + stoneSideways) * sideColor, cons.pHM.get("drivePowerLimit").value, "Right 45 inches",this);

        //release skystone with gripper
        if(sideColor == 1) {haveBlueSkyStone1 = false;}
        else if(sideColor == -1) {haveRedSkyStone1 = false;}

    }

    public void getStone2() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft,  ( -45 - stoneSideways - 24 ) * sideColor, cons.pHM.get("drivePowerLimit").value, "Left 45+ inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate, (90 * sideColor), cons.pHM.get("drivePowerLimit").value, "Rotate 90 degrees CW",this);

    }

    public void takeStone2() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, sideGrabStone/2 * sideColor, cons.pHM.get("drivePowerLimit").value, "Right 8 inches",this);

        if(sideColor == 1) {Billy.servoBlueStoneGrab.setPosition(1);}
        else if(sideColor == -1) {Billy.servoRedStoneGrab.setPosition(1);}
        //grab skystone with gripper
        if(sideColor == 1) {haveBlueSkyStone2 = true;}
        else if(sideColor == -1) {haveRedSkyStone2 = true;}

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, -sideGrabStone/2 * sideColor, cons.pHM.get("drivePowerLimit").value, "Left 8 inches",this);

        drv.driveGeneral(DriveMethods.moveDirection.Rotate, (-90 * sideColor), cons.pHM.get("drivePowerLimit").value, "Rotate 90 degrees CCW",this);

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (45 + stoneSideways + 24) * sideColor, cons.pHM.get("drivePowerLimit").value, "Right with stone 2",this);

        //release skystone with gripper
        if(sideColor == 1) {haveBlueSkyStone2 = false;}
        else if(sideColor == -1) {haveRedSkyStone2 = false;}

    }

    public void twoStonePark() {

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, -10 * sideColor, cons.pHM.get("drivePowerLimit").value, "Left 10 inches",this);

    }

    public void findSkyStone() {
        boolean skystoneFound = false;
        int looped = 0;

        while(looped < 2 && activeOpMode) {
            skystoneFound = vuforiaStoneIdentifyExit(looped, stoneSelect);//KS Added 12/120/19 variable to set stone location and map to field

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

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, -32, cons.pHM.get("drivePowerLimit").value, "Backward 32 inches to Foundation", this);
        //KS 12/20/19 Does this assume that grippers are on back of robot - needs to be updated to change sign

        // grab foundation with servos
        if(sideColor == 1) {

            haveBlueFoundation = true;
        }
        else if(sideColor == -1) {

            haveRedFoundation = true;
        }

    }

    public void pullFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 30, cons.pHM.get("drivePowerLimit").value, "Forward 30 inches with Foundation", this);

        // release foundation from gripper
        if(sideColor == 1) {

            haveBlueFoundation = false;
        }
        else if(sideColor == -1) {

            haveRedFoundation = false;
        }

    }

    public void aroundFoundation() {

//        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 2, cons.pHM.get("drivePowerLimit").value, "Forward 2 inches away from Foundation", this);

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (-26 * sideColor), cons.pHM.get("drivePowerLimit").value / 2, "Left 26 inches around Foundation", this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, -18, cons.pHM.get("drivePowerLimit").value, "Backwards 18 inches around Foundation", this);

    }

    public void pushFoundation() {
        // touching foundation to push it
        if(sideColor == 1) {

            haveBlueFoundation = true;
        }
        else if(sideColor == -1) {

            haveRedFoundation = true;
        }

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (foundationPush * sideColor), cons.pHM.get("drivePowerLimit").value / 2, "Right 8 inches pushing Foundation", this);

        if(sideColor == 1) {

            haveBlueFoundation = false;
        }
        else if(sideColor == -1) {

            haveRedFoundation = false;
        }

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (-foundationPush * sideColor), cons.pHM.get("drivePowerLimit").value / 2, "Left 8 inches away from Foundation", this);

    }

    public void awayFromFoundation() {

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, (foundationInOut), cons.pHM.get("drivePowerLimit").value, "Back 6 inches towards center of Bridge", this);

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, -16 * sideColor, cons.pHM.get("drivePowerLimit").value / 2, "Left 16 inches to park", this);

    }

    public void coachFoundation(){
        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 32, cons.pHM.get("drivePowerLimit").value, "Forward 32 inches to Foundation", this);

        // grab foundation with servos
        if(sideColor == 1) {haveBlueFoundation = true;}
        else if(sideColor == -1) {haveRedFoundation = true;}

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, 10*sideColor, cons.pHM.get("drivePowerLimit").value, "Right/Left 10 inches to rotate Foundation", this);
        drv.driveArc(10*sideColor, -92*sideColor, cons.pHM.get("drivePowerLimit").value, "move Foundation in arc", this);
        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, 15, cons.pHM.get("drivePowerLimit").value, "Forward 15 inches to Push Foundation", this);

        // release foundation from servos
        if(sideColor == 1) {haveBlueFoundation = false;}
        else if(sideColor == -1) {haveRedFoundation = false;}

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, -15, cons.pHM.get("drivePowerLimit").value, "Backwards 15 inches away from Foundation", this);

        drv.driveGeneral(DriveMethods.moveDirection.RightLeft, (11 -foundationInOut) * sideColor, cons.pHM.get("drivePowerLimit").value / 2, "Align to Bridge Side", this);

        drv.driveGeneral(DriveMethods.moveDirection.FwdBack, -26, cons.pHM.get("drivePowerLimit").value, "Backwards 18 inches to Bridge", this);

    }

}