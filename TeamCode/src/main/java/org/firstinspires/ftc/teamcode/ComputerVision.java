package org.firstinspires.ftc.teamcode;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

public class ComputerVision {

    private int avg_r;
    private int avg_g;
    private int avg_b;
    private OpenCvCamera phoneCam;

    public ComputerVision(int camId) {
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, camId);
        phoneCam.setPipeline(new ComputerVision.Pipeline());
        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }
            @Override
            public void onError(int errorCode) {
            }
        });
    }

    public int[] getRGB() {return new int[] {avg_r, avg_g, avg_b};}

    class Pipeline extends OpenCvPipeline {

        private boolean viewportPaused = false;
        private final Scalar RED = new Scalar(255, 0, 0);
        private final Scalar GREEN = new Scalar(0, 255, 0);
        private final Scalar BLUE = new Scalar(0, 0, 255);
        private Mat YCrCb = new Mat();
        private Mat Cb = new Mat();
        private Point pointTopLeft;
        private Point pointBotRight;
        private Mat region;

        private void inputToCb(Mat input) {
            Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
            Core.extractChannel(YCrCb, Cb, 2);
        }

        @Override
        public void init(Mat firstFrame) {
            pointTopLeft = new Point(firstFrame.cols() / 3, firstFrame.rows() / 3);
            pointBotRight = new Point(firstFrame.cols() * (2f / 3f), firstFrame.rows() * (2f / 3f));
            inputToCb(firstFrame);
            region = Cb.submat(new Rect(pointTopLeft, pointBotRight));
        }

        @Override
        public Mat processFrame(Mat input) {
            pointTopLeft = new Point(input.cols() / 3, input.rows() / 3);
            pointBotRight = new Point(input.cols() * (2f / 3f), input.rows() * (2f / 3f));
            region = input.submat(new Rect(pointTopLeft, pointBotRight));

            avg_r = (int) Core.mean(region).val[0];
            avg_g = (int) Core.mean(region).val[1];
            avg_b = (int) Core.mean(region).val[2];

            Imgproc.rectangle(
                    input,
                    new Point(
                            input.cols() / 3,
                            input.rows() / 3),
                    new Point(
                            input.cols() * (2f / 3f),
                            input.rows() * (2f / 3f)),
                    new Scalar(0, 255, 0), 4);
            return input;
        }

        @Override
        public void onViewportTapped() {
            viewportPaused = !viewportPaused;
            if (viewportPaused) {
                phoneCam.pauseViewport();
            } else {
                phoneCam.resumeViewport();
            }
        }
    }
}