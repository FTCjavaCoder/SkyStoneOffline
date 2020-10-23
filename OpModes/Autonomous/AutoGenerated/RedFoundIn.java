package Skystone_14999.OpModes.Autonomous.AutoGenerated;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import Skystone_14999.OpModes.Autonomous.BasicAuto;

@Autonomous(name="RedFoundIn", group="Autonomous")

 public class RedFoundIn extends BasicAuto {

	@Override
	public void runOpMode() {

		 int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

		parameters.vuforiaLicenseKey = cons.VUFORIA_KEY;
		parameters.cameraDirection   = cons.CAMERA_CHOICE;

		//  Instantiate the Vuforia engine
		vuforia = ClassFactory.getInstance().createVuforia(parameters);

		targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");
		//all above lines need to be all autonomous OpMode's runOpMode before initialization

		initialize();

		waitForStart();

		runCode();

	}

	@Override
	public void initialize() {

		foundationPosChange = 26.0;// 0 for moved, 26 for unmoved Foundation
		insideOutside = 0.0;// 0 for Inside, 24 for Outside
		sideColor = -1.0;// + for Blue, - for Red

		initializeMiniBot();

	}

	@Override
	public void runCode() {

		runtime.reset();

		Billy.initIMU(this);

		grabFoundation();

		grabAndRotateFoundation();

		telemetry.addLine("OpMode Complete");
		telemetry.update();
		sleep(500);
	}

}
