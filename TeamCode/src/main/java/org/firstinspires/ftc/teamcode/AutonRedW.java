package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.TwoWheel;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.bosch.BNO055IMU;


/** Pre-Match Setup
 * Freight loaded in claw
 * Within 18x18x18
 * Robot faces toward warehouse
 * Custom element on bar code
 * Phone on right side
 */

@Autonomous
public class AutonRedW extends LinearOpMode {

    private TwoWheel driveTrain;
    private DcMotor leftWheel;
    private DcMotor rightWheel;
    private DcMotor linSlide;
    private DcMotor arm;
    private Servo claw;
    private int recognition = 0;
    private final int[][] tierPos = {{3400, 375}, {1850, 375}, {500, 375}};
    private ComputerVision cv;
    private int[] avgRGB;

    @Override
    public void runOpMode() {

        driveTrain = new TwoWheel(
                hardwareMap.get(DcMotor.class, "left"),
                hardwareMap.get(DcMotor.class, "right"),
                hardwareMap.get(BNO055IMU.class, "imu")
        );
        driveTrain.setMode(false);
        leftWheel = hardwareMap.get(DcMotor.class, "leftWheel");
        leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheel = hardwareMap.get(DcMotor.class, "rightWheel");
        rightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linSlide = hardwareMap.get(DcMotor.class, "linSlide");
        linSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm = hardwareMap.get(DcMotor.class, "arm");
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        claw = hardwareMap.get(Servo.class, "claw");
        cv = new ComputerVision(hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));

        waitForStart();

        if (opModeIsActive()) {

            claw.setPosition(0);
            // get recognition
            recognition = 2 - cv.getRecognition();
            telemetry.log().add(String.valueOf(recognition));
            telemetry.update();
            if (recognition == -1) {recognition = 2;}

            // prepare to place freight
            driveTrain.fd(-0.5, 0.05, 1000);
            driveTrain.rot(-0.75, 90);
            driveTrain.fd(-0.8, 0.45, 5000);
            driveTrain.rot(-0.75, 180);
            elevate();

            // place freight
            driveTrain.fd(0.3, 0.22, 4000);
            claw.setPosition(0.6);
            sleep(1000);
            driveTrain.fd(-0.6, 0.22, 3000);
            driveTrain.rot(0.75, 90);
            lower();

            //park
            driveTrain.fd(1, 1, 10000);

            // duck
            /*driveTrain.fd(-0.75, 0.55, 5000);
            driveTrain.fd(-0.3, 0.15, 4000);
            leftWheel.setPower(0.5);
            rightWheel.setPower(0.5);
            sleep(3500);
            leftWheel.setPower(0);
            rightWheel.setPower(0);
            driveTrain.fd(0.8, 0.05, 3000);
            driveTrain.rot(-0.75, 180);

            // park
            //while (opModeIsActive() && (linSlide.isBusy() || arm.isBusy())) {
            //    idle();
            //}

            driveTrain.fd(0.5, 0.22, 4000);
            driveTrain.rot(-0.75, -90);
            driveTrain.fd(0.5, 0.05, 4000);*/

            // duck
            /*driveTrain.fd(-0.3, 0.25, 4000);
            leftWheel.setPower(0.5);
            rightWheel.setPower(0.5);
            sleep(5000);
            leftWheel.setPower(0);
            rightWheel.setPower(0);
            driveTrain.fd(0.5, 0.75, 3000);
            // driveTrain.rot(-0.75, 90);

            // prepare to place freight
            elevate();

            // place freight
            driveTrain.fd(0.3, 0.25, 4000);
            claw.setPosition(0.6);
            sleep(1000);
            driveTrain.fd(-0.5, 0.25, 3000);

            // park
            driveTrain.rot(0.75, 0);
            driveTrain.fd(1, 1.1, 10000);*/
        }
    }

    public void elevate() {
        linSlide.setTargetPosition(tierPos[recognition][0]);
        arm.setTargetPosition(tierPos[recognition][1]);

        linSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        linSlide.setPower(0.4);
        arm.setPower(0.25);

        while (opModeIsActive() && (linSlide.isBusy() || arm.isBusy())) {
            idle();
        }

        linSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void lower() {
        linSlide.setTargetPosition(0);
        arm.setTargetPosition(0);

        linSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        linSlide.setPower(-0.1);
        arm.setPower(-0.1);
    }
}
