package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.TwoWheel;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TeleTest extends LinearOpMode {

    private TwoWheel driveTrain;
    private DcMotor linSlide;
    private DcMotor leftWheel;
    private DcMotor rightWheel;
    private DcMotor arm;
    private Servo claw;
    private double[] pos;
    private boolean initPress = false;
    private boolean finalPress = false;
    private boolean clamp = false;

    @Override
    public void runOpMode() {

        driveTrain = new TwoWheel(hardwareMap.get(DcMotor.class, "left"), hardwareMap.get(DcMotor.class, "right"), hardwareMap.get(BNO055IMU.class, "imu"));
        linSlide = hardwareMap.get(DcMotor.class, "linSlide");
        linSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftWheel = hardwareMap.get(DcMotor.class, "leftWheel");
        leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheel = hardwareMap.get(DcMotor.class, "rightWheel");
        rightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm = hardwareMap.get(DcMotor.class, "arm");
        arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        claw = hardwareMap.get(Servo.class, "claw");

        waitForStart();
        while (opModeIsActive()) {

            /*if (gamepad1.x) {
                driveTrain.brake(0);
            } else {
                driveTrain.drive(gamepad1.right_trigger - gamepad1.left_trigger, gamepad1.left_stick_x);
            }*/
            driveTrain.drive(gamepad1.right_trigger - gamepad1.left_trigger, gamepad1.left_stick_x);

            if (gamepad1.dpad_up) {
                linSlide.setPower(0.25);
            } else if (gamepad1.dpad_down) {
                linSlide.setPower(-0.25);
            } else {
                linSlide.setPower(0);
                linSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            // carousel spins
            if (gamepad1.x) {
                leftWheel.setPower(1);
                rightWheel.setPower(1);
            } else if (gamepad1.b) {
                leftWheel.setPower(-1);
                rightWheel.setPower(-1);
            } else {
                leftWheel.setPower(0);
                rightWheel.setPower(0);
            }

            if (Math.abs(gamepad1.right_stick_y) > 0.15) {
                arm.setPower(gamepad1.right_stick_y * 0.25);
            } else {
                arm.setPower(0);
                arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }

            finalPress = gamepad1.a;
            if (!initPress && finalPress) {
                clamp = !clamp;
            }
            initPress = finalPress;
            if (clamp) {
                claw.setPosition(0);
            } else {
                claw.setPosition(1);
            }

            pos = driveTrain.getPos();
            telemetry.addData("pos x", pos[0]);
            telemetry.addData("pos y", pos[1]);
            telemetry.addData("left", driveTrain.getEncoderPos()[0]);
            telemetry.addData("right", driveTrain.getEncoderPos()[1]);
            telemetry.addData("counts", driveTrain.getCounts());
            telemetry.update();
        }
    }
}