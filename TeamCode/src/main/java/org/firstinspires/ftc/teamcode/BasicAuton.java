package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous
public class BasicAuton extends LinearOpMode {

    private TwoWheel driveTrain;
    private DcMotor rightWheel;
    private DcMotor leftWheel;
    private Servo claw;

    @Override
    public void runOpMode() {
        driveTrain = new TwoWheel(
                hardwareMap.get(DcMotor.class, "left"),
                hardwareMap.get(DcMotor.class, "right"),
                hardwareMap.get(BNO055IMU .class, "imu")
        );
        rightWheel = hardwareMap.get(DcMotor.class, "rightWheel");
        rightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftWheel = hardwareMap.get(DcMotor.class, "leftWheel");
        leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        claw = hardwareMap.get(Servo.class, "claw");
        claw.setPosition(0);


        waitForStart();

        if (opModeIsActive()) {
            driveTrain.fd(-0.3, 0.25);
            rightWheel.setPower(-0.5);
            sleep(5000);
            rightWheel.setPower(0);
            driveTrain.fd(0.75, 1.75);
            sleep(2000);
        }
    }
}
