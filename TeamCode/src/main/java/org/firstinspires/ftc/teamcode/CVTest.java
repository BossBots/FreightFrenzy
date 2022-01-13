package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class CVTest extends LinearOpMode {

    private ComputerVision cv;
    private int[][] rgb;

    @Override
    public void runOpMode() {
        cv = new ComputerVision(hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));
        waitForStart();

        while (opModeIsActive()) {
            rgb = cv.getRGB();
            telemetry.addData("index", cv.getAnalysis());
            telemetry.addData("red 1", rgb[0][0]);
            telemetry.addData("green 1", rgb[0][1]);
            telemetry.addData("blue 1", rgb[0][2]);
            telemetry.addData("red 2", rgb[1][0]);
            telemetry.addData("green 2", rgb[1][1]);
            telemetry.addData("blue 2", rgb[1][2]);
            telemetry.addData("red 3", rgb[2][0]);
            telemetry.addData("green 3", rgb[2][1]);
            telemetry.addData("blue 3", rgb[2][2]);
            telemetry.update();
        }
    }
}
