package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.bosch.BNO055IMU;

@TeleOp
public class Odometry extends LinearOpMode {

    private TwoWheel driveTrain;

    @Override
    public void runOpMode() {

        driveTrain = new TwoWheel(
                hardwareMap.get(DcMotor.class, "left"),
                hardwareMap.get(DcMotor.class, "right"),
                hardwareMap.get(BNO055IMU.class, "imu")
        );
        driveTrain.setMode(false);

        waitForStart();

        while (opModeIsActive()) {

            /*if (gamepad1.a) {
                driveTrain.moveEncoder(0.5, 480);
            }

            if (gamepad1.b) {
                driveTrain.moveDist(0.5, 0.5);
            }

            if (gamepad1.x) {
                driveTrain.brake(0);
            }

            driveTrain.integrate();*/

            telemetry.addData("pos x", driveTrain.getPos()[0]);
            telemetry.addData("pos y", driveTrain.getPos()[1]);
            telemetry.addData("encoder L", driveTrain.getEncoderPos()[0]);
            telemetry.addData("encoder R", driveTrain.getEncoderPos()[1]);
            telemetry.update();

        }

    }
}
