package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.bosch.BNO055IMU;

@TeleOp
public class ArmPositionTest extends LinearOpMode {

    private final int[][] tierPos = {{0, 0}, {500, 375}, {1850, 375}, {3400, 375}};
    private int currentPos = 0;
    private DcMotor linSlide;
    private DcMotor arm;
    private TwoWheel driveTrain;

    @Override
    public void runOpMode() {
        driveTrain = new TwoWheel(hardwareMap.get(DcMotor.class, "left"),
                hardwareMap.get(DcMotor.class, "right"),
                hardwareMap.get(BNO055IMU.class, "imu"));
        linSlide = hardwareMap.get(DcMotor.class, "linSlide");
        linSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm = hardwareMap.get(DcMotor.class, "arm");
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {

            driveTrain.drive(gamepad1.right_trigger - gamepad1.left_trigger, gamepad1.left_stick_x);

            if (Math.abs(linSlide.getCurrentPosition() - tierPos[currentPos][0]) > 30) {
                if (linSlide.getCurrentPosition() > tierPos[currentPos][0]) {
                    linSlide.setPower(-0.25);
                } else {
                    linSlide.setPower(0.25);
                }
            } else {
                linSlide.setPower(0);
            }

            if (Math.abs(arm.getCurrentPosition() - tierPos[currentPos][1]) > 30) {
                if (arm.getCurrentPosition() > tierPos[currentPos][1]) {
                    arm.setPower(-0.05);
                } else {
                    arm.setPower(0.05);
                }
            } else {
                arm.setPower(0);
            }

            if (gamepad1.a) {
                currentPos = 0;
            } else if (gamepad1.b) {
                currentPos = 1;
            } else if (gamepad1.x) {
                currentPos = 2;
            } else if (gamepad1.y) {
                currentPos = 3;
            }



            telemetry.addData("tier", currentPos);
        }
    }
}
