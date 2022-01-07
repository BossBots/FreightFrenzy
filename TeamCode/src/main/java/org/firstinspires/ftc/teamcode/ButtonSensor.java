package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp
public class ButtonSensor extends LinearOpMode {

    private DigitalChannel button;

    @Override
    public void runOpMode() {

        button = hardwareMap.get(DigitalChannel.class, "button");

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("pressed", button.getState());
            telemetry.update();
        }
    }
}
