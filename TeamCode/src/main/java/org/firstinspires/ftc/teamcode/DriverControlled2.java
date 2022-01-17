package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class DriverControlled2 extends LinearOpMode {

    private DcMotor left;
    private DcMotor right;

    @Override
    public void runOpMode() throws InterruptedException {
        left = hardwareMap.get(DcMotor.class, "left");
        right = hardwareMap.get(DcMotor.class, "right");

        waitForStart();

        while (opModeIsActive()) {
            left.setPower(-gamepad1.left_stick_y);
            right.setPower(-gamepad1.right_stick_y);
        }
    }
}
