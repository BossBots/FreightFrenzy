package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import com.vuforia.Frame;
import android.graphics.Bitmap;
import org.tensorflow.lite.Interpreter;
import com.vuforia.CameraDevice;


@TeleOp
public class VuforiaTest extends LinearOpMode {

    private VuforiaLocalizer vuforia;
    private final String VUFORIA_KEY = "ASXuYar/////AAABmXFF9U0Sqkf4nhGpOxAwL4hjOruKN+pzxoY06iPyjRCJzC2VJLEcTeWGlru0xqBeYD1/4Q/Re8WeD61/uoVn4xHD2U4ZJcxUOgBIJ9tpv181fPxonQEECTo6DAbx6VaADyWN5deZ5fkOVeQD7He5kCgL7DA5VbYDsXCNoqF4Ifnj+pyVUlYZeuiIvpHUDGXfa6E5a3jJk4p6ksFK0BCObGsRtKfFsCKZvjz8shWT0ifp4majfLUu3J8xLNmEM6pLy9bsDWgANt+Ao+zrFDJ1jUGG92oI1nllBYQCW/PC3to0UeGPIeUWpTSFBZFp8GSAf633CgKcxSftde0rgBxMlrB2YIWeM6bPXPwbqSpvS/yT";
    private Continuation<? extends Consumer<Bitmap>> continuation;

    @Override
    public void runOpMode() {
        initVuforia();
        waitForStart();
        while (opModeIsActive()) {
            vuforia.getFrameBitmap((Continuation<? extends org.firstinspires.ftc.robotcore.external.function.Consumer<Bitmap>>) continuation);
            continuation.getTarget();
        }
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }
}


