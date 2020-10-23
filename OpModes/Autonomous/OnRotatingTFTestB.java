package Skystone_14999.OpModes.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name="Rotating Early On TensorFlow Test Blue", group="TensorFlow")
//@Disabled
public class OnRotatingTFTestB extends BasicAuto {

    @Override
    public void runOpMode() {

        telemetry.addLine("NOT READY DON'T PRESS PLAY");
        telemetry.update();
//        setCamera();
//        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
//        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = cons.VUFORIA_KEY;
        parameters.cameraDirection   = cons.CAMERA_CHOICE;
        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
//        if (tfod != null) {
//            tfod.activate();
//        }

        foundationPosChange = 0;// 0 for moved, 26 for unmoved Foundation.
        insideOutside = 0;// 0 for Inside, 24 for Outside
        sideColor = 1;// + for Blue, - for Red, KEEP RED

//        initializeMiniBot();
        initialize();

        if (tfod != null) {
            tfod.activate();
        }

        while (!isStarted()) {

            List<Recognition> recognitionList = tfod.getRecognitions();
            if (recognitionList != null) {

                telemetry.addData("# Object Detected", recognitionList.size());

                int i = 0;
                for (Recognition recognition : recognitionList) {
                    telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                    telemetry.addData(String.format("  left,right (%d)", i), "%.03f , %.03f",
                            recognition.getLeft(), recognition.getRight());
                    telemetry.addData(String.format("  top,bottom (%d)", i), "%.03f , %.03f",
                            recognition.getTop(), recognition.getBottom());
                    telemetry.addData("Center Pixels", "recog(%d): %.3f", i,
                            (recognition.getRight() + recognition.getLeft()) / 2);
                }
            }
            else {

                telemetry.addLine("No Objects Detected");
            }

            telemetry.update();
        }

        waitForStart();

        runtime.reset();

        Billy.initIMU(this);

        fwdToTwoStone();

        tensorFlowStoneLIRotate();

        if (tfod != null) {
            tfod.shutdown();
        }

        telemetry.addData("stoneYLocation","(%.2f)", stoneYLocation);
        telemetry.addLine("OpMode Complete");
        telemetry.update();
        sleep(4000);
    }

//    public void setCamera() {
//        while (!gamepad1.x) {
//
//            if (gamepad1.a) {
//
//                cons.CAMERA_CHOICE = VuforiaLocalizer.CameraDirection.BACK;
//            }
//
//            if (gamepad1.b) {
//
//                cons.CAMERA_CHOICE = VuforiaLocalizer.CameraDirection.FRONT;
//            }
//
//
//            telemetry.addData("Current camera", cons.CAMERA_CHOICE);
//            telemetry.addLine("Press A to select BACK and B to select FRONT");
//            telemetry.addLine("Press X to exit");
//            telemetry.update();
//        }
//    }
    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    public void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = cons.tensorFlowMinimumConfidence;// was 0.8
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
        tfod.setClippingMargins(0,0,0,0);

    }
}