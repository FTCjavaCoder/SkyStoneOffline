package Skystone_14999.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


/**
 *
 */

@TeleOp(name="FullDrive Scaled Stick Control", group="TeleOp")

public class FullDrive_Scaled_StickControl extends BasicTeleOp {

    @Override
    public void runOpMode() {

        telemetry.addLine("NOT READY DON'T PRESS PLAY");
        telemetry.update();

        initializeTeleOp();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        Billy.initIMU(this);

        Billy.armServoBlue.setPosition(0.6);
        Billy.armServoRed.setPosition(0.15);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Set Drive Motor Power
//            Billy.drivePowerAllLeftStickScaled(gamepad1, gamepad2);
            Billy.drivePowerAllLeftStickScaledSquared(gamepad1, gamepad2, this);

//            // use the left/right triggers on gamepad1 to rotate the robot counter/clockwise
//            Billy.rotatePowerRightStick(gamepad1, gamepad2);

            // use the left stick on gamepad2 to raise/lower the jack
//            Billy.jackPower(gamepad1, gamepad2);
            Billy.jackPowerEncoderStop(gamepad1, gamepad2, this);

            // use the right stick on gamepad2 to extend/retract the slide
            Billy.slidePower(gamepad1, gamepad2, this);

            //
            if (gamepad2.right_bumper) {

                servoStonePos = Billy.stoneServoRight.getPosition() + 0.005;// was 0.05 with 300 millisecond sleep
                Range.clip(servoStonePos, 0.15, 1);
                Billy.setServoPos(servoStonePos);
//                sleep(300);
            }

            if (gamepad2.left_bumper) {

                servoStonePos = Billy.stoneServoRight.getPosition() - 0.005;// was 0.05 with 300 millisecond sleep
                Range.clip(servoStonePos, 0.15, 1);
                Billy.setServoPos(servoStonePos);
//                sleep(300);
            }

            // sets the position of the servos to 8"
            if (gamepad2.a) {
                servoStonePos = 0.5;
                Billy.setServoPos(servoStonePos);
//                sleep(300);
            }

            // sets the position of the servos to 4"
            if (gamepad2.y) {
                servoStonePos = 1;
                Billy.setServoPos(servoStonePos);
//                sleep(300);
            }

            // sets the position of the servos to open
            if (gamepad2.b) {
                servoStonePos = 0.15;
                Billy.setServoPos(servoStonePos);
//                sleep(300);
            }


            // SERVOS FOUNDATION
            if(gamepad1.dpad_up) {

                Billy.servoFoundationL.setPosition(0.10);

                Billy.servoFoundationR.setPosition(0.90);
            }
            if(gamepad1.dpad_down) {

                Billy.servoFoundationL.setPosition(0.80);

                Billy.servoFoundationR.setPosition(0.20);
            }

            // SERVOS FOUNDATION
            if(gamepad1.dpad_left) {

                Billy.armServoBlue.setPosition(1);
                Billy.armServoRed.setPosition(0);
            }

            // Values Not determined
            if (Math.abs(gamepad2.left_trigger) > 0) {

                capstoneServoPosition -= 0.005;
                Billy.servoCapstoneRelease.setPosition(capstoneServoPosition);//
            }
            if (Math.abs(gamepad2.right_trigger) > 0) {

                capstoneServoPosition += 0.005;
                Billy.servoCapstoneRelease.setPosition(capstoneServoPosition);//
            }
            if (gamepad2.dpad_left) {

                capstoneServoPosition = 0.15;
                Billy.servoCapstoneRelease.setPosition(capstoneServoPosition);//
            }


            Billy.angleUnWrap();

            telemetry.addData("Status", "Run Time: ",runtime.toString());
            telemetry.addData("Robot Heading", "( %.2f )", Billy.robotHeading);
            telemetry.addData("Servos Gripper", "Servo Left (%.2f), Servo Right (%.2f)",
                    Billy.stoneServoLeft.getPosition(), Billy.stoneServoRight.getPosition());
            telemetry.addData("Drive Motor Pos", "FL (%d), FR (%d), BR (%d), BL (%d)",
                    Billy.frontLeft.getCurrentPosition(), Billy.frontRight.getCurrentPosition(),
                    Billy.backRight.getCurrentPosition(), Billy.backLeft.getCurrentPosition());
            telemetry.addData("Slide Pos", "Slide (%d)", Billy.slide.getCurrentPosition());
            telemetry.addData("Slide TargetPos", "Slide (%d)", Billy.slide.getTargetPosition());
            telemetry.addData("Slide Power", "Slide (%.2f)", Billy.slide.getPower());
            telemetry.addData("Servos Foundation", "F Servo Left (%.2f), F Servo Right (%.2f)",
                    Billy.servoFoundationL.getPosition(), Billy.servoFoundationR.getPosition());
            telemetry.addData("Commands Drive", "Forward (%.2f), Right (%.2f), Clockwise (%.2f)",
                    forwardDirection, rightDirection, clockwise);
            telemetry.addData("Drive Motors", "FL (%.2f), FR (%.2f), BL (%.2f), BR (%.2f)",
                    Billy.frontLeft.getPower(), Billy.frontRight.getPower(), Billy.backLeft.getPower(),
                    Billy.backRight.getPower());
            telemetry.addData("Jack Pos", "Center (%d)", Billy.jack.getCurrentPosition());
            telemetry.addData("Jack TargetPos", "Center (%d)", Billy.jack.getTargetPosition());
            telemetry.addData("Jack Power Cmd", "Vertical (%.2f)", verticalDirection);
            telemetry.addData("Jack Motors", "Jack Center (%.2f)", Billy.jack.getPower());

            telemetry.update();

            idle();
        }
        stopMotors();

    } // main opMode end methods go below

}