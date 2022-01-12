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
public class AutonRedC extends LinearOpMode {

    private TwoWheel driveTrain;
    private DcMotor linSlide;
    private DcMotor arm;
    private Servo claw;
    private int recognition;
    private final int[][] tierPos = {{0, 120}, {480, 120}, {960, 120}};
    private ComputerVision cv;
    private int[] avgRGB;

    @Override
    public void runOpMode() {

        driveTrain = new TwoWheel(
                hardwareMap.get(DcMotor.class, "left"),
                hardwareMap.get(DcMotor.class, "right"),
                hardwareMap.get(BNO055IMU.class, "imu")
        );
        driveTrain.setMode(true);
        linSlide = hardwareMap.get(DcMotor.class, "linSlide");
        linSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm = hardwareMap.get(DcMotor.class, "arm");
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        claw = hardwareMap.get(Servo.class, "claw");
        cv = new ComputerVision(hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName()));

        waitForStart();

        if (opModeIsActive()) {

            claw.setPosition(0);

            // get recognition
            recognition = cv.getAnalysis();

            // prepare to place freight
            while (linSlide.getCurrentPosition() < tierPos[recognition][0] && arm.getCurrentPosition() < tierPos[recognition][1]) {
                if (linSlide.getCurrentPosition() < tierPos[recognition][0]) {
                    linSlide.setPower(0.25);
                } else {
                    linSlide.setPower(0);
                    linSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }

                if (arm.getCurrentPosition() < tierPos[recognition][1]) {
                    arm.setPower(0.15);
                } else {
                    arm.setPower(0);
                    arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            }
            linSlide.setPower(0);
            linSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            arm.setPower(0);
            arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            // place freight
            driveTrain.rot(0.25, -45);
            driveTrain.fd(0.25, 0.5);
            claw.setPosition(0.6);
            driveTrain.fd(-0.25, -0.25);

            while (linSlide.getCurrentPosition() > 50) {
                linSlide.setPower(-0.25);
            }
            linSlide.setPower(0);

            // park
            driveTrain.rot(0.25, 180);
            driveTrain.fd(1, 2.);
        }
    }
}
