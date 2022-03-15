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
    private boolean initPressClaw = false;
    private boolean finalPressClaw = false;
    private boolean clamp = false;
    private boolean lsManualControl = false;
    private boolean armManualControl = false;
    private final int[][] tierPos = {{0, 0}, {0, 375}, {500, 375}, {3400, 375}};
    private int currentPos = 0;
    private boolean initRBPress = false;
    private boolean finalRBPress = false;
    private boolean initLBPress = false;
    private boolean finalLBPress = false;
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

        waitForStart();
        while (opModeIsActive()) {

            // drive
            if (gamepad1.x) {
                driveTrain.brake(0);
            } else {
                driveTrain.drive(gamepad1.right_trigger - gamepad1.left_trigger, gamepad1.left_stick_x);
            }

            // smart manipulation and positioning
            initLBPress = finalLBPress;
            initRBPress = finalRBPress;
            finalLBPress = gamepad2.left_bumper;
            finalRBPress = gamepad2.right_bumper;
            if (finalRBPress && !initRBPress) {
                armManualControl = false;
                lsManualControl = false;
                if (currentPos < tierPos.length - 1) {
                    currentPos += 1;
                }
            }
            if (finalLBPress && !initLBPress) {
                armManualControl = false;
                lsManualControl = false;
                if (currentPos > 0) {
                    currentPos -= 1;
                }
            }

            // linear slide
            if (!lsManualControl) {lsManualControl = (gamepad2.dpad_up || gamepad2.dpad_down);}
            if (lsManualControl) {
                if (gamepad2.dpad_up) {
                    linSlide.setPower(0.5);
                } else if (gamepad2.dpad_down) {
                    linSlide.setPower(-0.5);
                } else {
                    linSlide.setPower(0);
                    linSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            } else { //Code to take claw to preset heights. Team member wants it to go same speed as manual
                if (tierPos[currentPos][0] - linSlide.getCurrentPosition() > 20) {
                    linSlide.setPower(0.5);
                } else if (tierPos[currentPos][0] - linSlide.getCurrentPosition() < -20) {
                    linSlide.setPower(-0.5);
                } else {
                    linSlide.setPower(0);
                    linSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            }

            // arm controls
            if (!armManualControl) {armManualControl = Math.abs(gamepad2.right_stick_y) > 0.15;}
            if (armManualControl) {
                if (Math.abs(gamepad2.right_stick_y) > 0.15) {
                    arm.setPower(gamepad2.right_stick_y * 0.25);
                } else {
                    arm.setPower(0);
                    arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            } else {
                if (tierPos[currentPos][1] - arm.getCurrentPosition() > 20) {
                    arm.setPower(0.15);
                } else if (tierPos[currentPos][1] - arm.getCurrentPosition() < -20) {
                    arm.setPower(-0.15);
                } else {
                    arm.setPower(0);
                    arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            }

            // carousel spins
            if (gamepad2.b) {
                leftWheel.setPower(0.5);
                rightWheel.setPower(0.5);
            } else if (gamepad2.x) {
                leftWheel.setPower(-0.5);
                rightWheel.setPower(-0.5);
            } else {
                leftWheel.setPower(0);
                rightWheel.setPower(0);
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
                claw.setPosition(0.9);
            }

            if (getRuntime() >= T1 && getRuntime() < T1 + 1) {
                gamepad1.runRumbleEffect(rumbleEffect);
            }
            if (getRuntime() >= T2 && getRuntime() < T2 + 1) {
                gamepad1.runRumbleEffect(rumbleEffect);
            }

            // telemetry
            pos = driveTrain.getPos();
            telemetry.addData("pos x", pos[0]);
            telemetry.addData("pos y", pos[1]);
            telemetry.addData("left", driveTrain.getEncoderPos()[0]);
            telemetry.addData("right", driveTrain.getEncoderPos()[1]);
            telemetry.addData("counts", driveTrain.getCounts());
            telemetry.addData("arm", arm.getCurrentPosition());
            telemetry.addData("slide", linSlide.getCurrentPosition());
            telemetry.addData("current pos", currentPos);
            telemetry.update();
        }
    }
}
