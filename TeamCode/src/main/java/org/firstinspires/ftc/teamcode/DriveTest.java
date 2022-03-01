package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class DriveTest extends LinearOpMode {

    private DcMotor left;
    private DcMotor right;
    private double leftPower;
    private double rightPower;

    @Override
    public void runOpMode() {

        left = hardwareMap.get(DcMotor.class, "left");
        right = hardwareMap.get(DcMotor.class, "right");
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();

        while (opModeIsActive()) {
            leftPower = (gamepad1.right_trigger - gamepad1.left_trigger) + gamepad1.left_stick_x;
            rightPower = -(gamepad1.right_trigger - gamepad1.left_trigger - gamepad1.left_stick_x);

            if (rightPower > 1) {
                rightPower = 1;
            } else if (rightPower < -1) {
                rightPower = -1;
            }

            if (leftPower > 1) {
                leftPower = 1;
            } else if (leftPower < -1) {
                leftPower = -1;
            }

            left.setPower(leftPower);
            right.setPower(rightPower);
        }
    }
}
