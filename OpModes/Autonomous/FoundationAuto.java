package Skystone_14999.OpModes.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

@Autonomous(name="Foundation Auto", group="Autonomous")

public class FoundationAuto extends BasicAuto {

    @Override
    public void runOpMode() {

        initialize();

        waitForStart();

        runtime.reset();

        grabFoundation();

        pullFoundation();

        aroundFoundation();

        pushFoundation();

        telemetry.addLine("OpMode Complete");
        sleep(2000);
    }
}
