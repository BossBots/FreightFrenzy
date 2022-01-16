package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.TwoWheel;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;

@TeleOp
public class DriverControlled extends LinearOpMode {

    private TwoWheel driveTrain;
    private DcMotor linSlide;
    private DcMotor leftWheel;
    private DcMotor rightWheel;
    private DcMotor arm;
    private Servo claw;
    private double[] pos;
    private boolean initPressDrive = false;
    private boolean finalPressDrive = false;
    private boolean mode = false;
    private boolean initPressClaw = false;
    private boolean finalPressClaw = false;
    private boolean clamp = false;
    private Gamepad.RumbleEffect rumbleEffect;
    private final double T1 = 80;
    private final double T2 = 110;


    @Override
    public void runOpMode() {

        driveTrain = new TwoWheel(
                hardwareMap.get(DcMotor.class, "left"),
                hardwareMap.get(DcMotor.class, "right"),
                hardwareMap.get(BNO055IMU.class, "imu")
        );
        linSlide = hardwareMap.get(DcMotor.class, "linSlide");
        linSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftWheel = hardwareMap.get(DcMotor.class, "leftWheel");
        leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheel = hardwareMap.get(DcMotor.class, "rightWheel");
        rightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm = hardwareMap.get(DcMotor.class, "arm");
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        claw = hardwareMap.get(Servo.class, "claw");
        rumbleEffect = new Gamepad.RumbleEffect.Builder()
                .addStep(0.5, 0.5, 1000)
                .build();

        int driveTrainMode = 0;
        waitForStart();
        while (opModeIsActive()) {
            //Special mode
            if (gamepad1.x && gamepad1.y && gamepad1.a && gamepad1.b) {
                if (gamepad1.dpad_up && driveTrainMode != 0)
                    driveTrainMode = 0;
                else if (gamepad1.dpad_left)
                    drivaTrainMode = 1;
                else if (gamepad1.dpad_right)
                    driveTrainMode = 2;
            }
            
            // drive
            if (gamepad1.x) {
                driveTrain.brake(0);
            } else if (driveTrainMode == 1) {
                driveTrain.left.setPower(max(-1, min(1, gamepad1.left_stick.y)));
                drivaTrain.right.setPower(max(-1, min(1, gamepad1.right_stick.y)));
            } else if (driveTrainMode == 2) {
                if (gamepad1.left_bumper)
                    driveTrain.left.setPower(max(-1, min((-1)*gamepad1.left_trigger, 1)));
                else
                    driveTrain.left.setPower(max(-1, min(gamepad1.left_trigger, 1)));
                if (gamepad1.right_bumper)
                    driveTrain.right.setPower(max(-1, min((-1)*gamepad1.right_trigger, 1)));
                else
                    driveTrain.right.setPower(max(-1, min(gamepad1.right_trigger, 1)));
            } else if (mode) {
                driveTrain.setMode(true);
                driveTrain.drive((gamepad1.right_trigger - gamepad1.left_trigger)/3, gamepad1.left_stick_x/3);
            } else {
                driveTrain.setMode(false);
                driveTrain.drive(gamepad1.right_trigger - gamepad1.left_trigger, gamepad1.left_stick_x);
            }
            finalPressDrive = gamepad1.a;
            if (!initPressDrive && finalPressDrive) {
                mode = !mode;
            }
            initPressDrive = finalPressDrive;

            // linear slide
            if (gamepad2.dpad_up) {
                linSlide.setPower(0.5);
            } else if (gamepad2.dpad_down) {
                linSlide.setPower(-0.5);
            } else {
                linSlide.setPower(0);
                linSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            // carousel spins
            if (gamepad2.x) {
                leftWheel.setPower(1);
                rightWheel.setPower(1);
            } else if (gamepad2.b) {
                leftWheel.setPower(-1);
                rightWheel.setPower(-1);
            } else {
                leftWheel.setPower(0);
                rightWheel.setPower(0);
            }

            // arm controls
            if (Math.abs(gamepad2.right_stick_y) > 0.15) {
                arm.setPower(gamepad2.right_stick_y * 0.25);
            } else {
                arm.setPower(0);
                arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            // claw controls
            finalPressClaw = gamepad2.a;
            if (!initPressClaw && finalPressClaw) {
                clamp = !clamp;
            }
            initPressClaw = finalPressClaw;
            if (clamp) {
                claw.setPosition(0);
            } else {
                claw.setPosition(0.75);
            }

            if (getRuntime() >= T1 && getRuntime() < T1+1) {gamepad1.runRumbleEffect(rumbleEffect);}
            if (getRuntime() >= T2 && getRuntime() < T2+1) {gamepad1.runRumbleEffect(rumbleEffect);}

            // telemetry
            pos = driveTrain.getPos();
            telemetry.addData("precision", mode);
            telemetry.addData("pos x", pos[0]);
            telemetry.addData("pos y", pos[1]);
            telemetry.addData("left", driveTrain.getEncoderPos()[0]);
            telemetry.addData("right", driveTrain.getEncoderPos()[1]);
            telemetry.addData("counts", driveTrain.getCounts());
            telemetry.addData("arm", arm.getCurrentPosition());
            telemetry.addData("slide", linSlide.getCurrentPosition());
            telemetry.update();
        }
    }
}
