package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.bosch.BNO055IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;


@TeleOp
public class DriverControlled extends LinearOpMode {
    // mecanum drive train
    private Mecanum mecanum;

    // wobble goal manipulation system
    private DcMotor pivot;
    private Servo lock;

    // ring manipulation system
    private DcMotor launch;
    private DcMotor ramp;
    private CRServo intake;
    private CRServo ramp_rings1;
    private CRServo ramp_rings2;


    @Override
    public void runOpMode() {
        // mecanum initialization
        mecanum = new Mecanum(hardwareMap.get(BNO055IMU.class, "imu"), hardwareMap.get(DcMotor.class, "motor1"), hardwareMap.get(DcMotor.class, "motor2"), hardwareMap.get(DcMotor.class, "motor3"), hardwareMap.get(DcMotor.class, "motor4"));
        mecanum.constantPower();

        // wobble goal manipulation system initialization
        pivot = hardwareMap.get(DcMotor.class, "pivot");
        pivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lock = hardwareMap.get(Servo.class, "lock");

        // ring manipulation system initialization
        launch = hardwareMap.get(DcMotor.class, "launch");
        launch.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ramp = hardwareMap.get(DcMotor.class, "ramp");
        ramp.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake = hardwareMap.get(CRServo.class, "intake");
        ramp_rings1 = hardwareMap.get(CRServo.class, "ramp_rings1");
        ramp_rings2 = hardwareMap.get(CRServo.class, "ramp_rings2");



        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // mecanum drive
            mecanum.drive(gamepad1.right_trigger - gamepad1.left_trigger, gamepad1.left_stick_x, gamepad1.right_stick_x);
            
            
            // telemetry
            telemetry.addData("heading", mecanum.getHeading());
            telemetry.update();

            //pivot
            if (gamepad2.left_stick_y < -0.5 && pivot.getCurrentPosition() > 255) {
                pivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                pivot.setPower(-0.25);
            } else if (gamepad2.left_stick_y > 0.5) {
                pivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                pivot.setPower(0.25);
            } else {
                pivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                pivot.setPower(-0.1);
            }

            // lock
            if (gamepad2.a) {
                lock.setPosition(1);
            } else {
                lock.setPosition(0);
            }
            
            
            if (gamepad1.x) {
                mecanum.reset();
            }




            // launch motor
            if (gamepad2.x) {
                launch.setPower(0.95);
            } else if (gamepad2.y) {
                launch.setPower(0.65);
            } else {
                launch.setPower(0);
            }
            //launch.setPower(Math.abs(gamepad2.right_stick_y));

            // ramp control
            if (gamepad2.dpad_up) {
                ramp.setPower(0.6);
            } else if (gamepad2.dpad_down) {
                ramp.setPower(-0.25);
            } else {
                ramp.setPower(0.1);
            }

            // intake always remains on
            intake.setPower(-1);

            // ramp rotors
            if (gamepad2.left_bumper) {
                ramp_rings2.setPower(gamepad2.left_trigger);
            } else {
                ramp_rings2.setPower(-gamepad2.left_trigger);
            }
            if (gamepad2.right_bumper) {
                ramp_rings1.setPower(gamepad2.right_trigger);
            } else {
                ramp_rings1.setPower(-gamepad2.right_trigger);
            }

        }
    }
}
