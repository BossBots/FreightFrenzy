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
            telemetry.addData("index", cv.getRecognition());
            telemetry.addData("x", cv.getX());
            telemetry.update();
        }
    }
}
