package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class ArmPositionTest extends LinearOpMode {

    private final int[][] tierPos = {{0, 0}, {0, 120}, {480, 120}, {960, 120}};
    private int currentPos = 0;
    private DcMotor linSlide;
    private DcMotor arm;

    @Override
    public void runOpMode() {
        linSlide = hardwareMap.get(DcMotor.class, "linSlide");
        linSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm = hardwareMap.get(DcMotor.class, "arm");
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            if (Math.abs(linSlide.getCurrentPosition() - tierPos[currentPos][0]) > 20) {
                if (linSlide.getCurrentPosition() > tierPos[currentPos][0]) {
                    linSlide.setPower(-0.25);
                } else {
                    linSlide.setPower(0.25);
                }
            }

            if (Math.abs(arm.getCurrentPosition() - tierPos[currentPos][1]) > 20) {
                if (arm.getCurrentPosition() > tierPos[currentPos][1]) {
                    arm.setPower(-0.25);
                } else {
                    arm.setPower(0.25);
                }
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
        }
    }
}
