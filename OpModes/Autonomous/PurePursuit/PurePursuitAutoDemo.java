package Skystone_14999.OpModes.Autonomous.PurePursuit;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.ArrayList;

import Skystone_14999.OpModes.Autonomous.BasicAuto;

@Autonomous(name="Pure Pursuit Demo", group="Autonomous")

 public class PurePursuitAutoDemo extends BasicAuto {


	@Override
	public void runOpMode() {

		initialize();

		waitForStart();

		runCode();

	}

	@Override
	public void initialize() {

		foundationPosChange = 0.0;// 0 for moved, 26 for unmoved Foundation
		insideOutside = 0.0;// 0 for Inside, 24 for Outside
		sideColor = 1.0;// + for Blue, - for Red

		initializeMiniBot();

	}

	@Override
	public void runCode() {

		runtime.reset();

		Billy.robotX = -50;
		Billy.robotY = -30;
		Billy.robotHeading = 0;
		Billy.priorAngle = 0;

		ArrayList<PursuitPoint> pathPoints = new ArrayList<>();
		pathPoints.add(new PursuitPoint(-30,-30));
		pathPoints.add(new PursuitPoint(0,0));
		pathPoints.add(new PursuitPoint(50,25));


//		path.defineRectangle(-50,-30,100,60);

		Billy.drivePursuit( pathPoints, this, "Drive 100 x 60 rectangle");


	}

}
