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

    private OpenCvCamera phoneCam;
    private final double[][] topLeft = {{1d/3d, 0d/3d}, {1d/3d, 1d/3d}, {1d/3d, 2d/3d}};
    private final double[][] botRight = {{2d/3d, 1d/3d}, {2d/3d, 2d/3d}, {2d/3d, 3d/3d}};
    private int[][] avgRGB = new int[3][3];

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

    public int getAnalysis() {
        int output = -1;
        for (int i = 0; i < 3; i++) {
            if (avgRGB[i][1] < 0.8 * avgRGB[i][0] && avgRGB[i][2] < 0.64 * avgRGB[i][0] && (output == -1 || avgRGB[output][0] <= avgRGB[i][0])) {
                output = i;
            }
        }
        return output;
    }

    public int[][] getRGB() {return avgRGB;}

    class Pipeline extends OpenCvPipeline {

        private boolean viewportPaused = false;
        private Mat YCrCb = new Mat();
        private Mat Cb = new Mat();
        private Mat[] regions = new Mat[3];

        private void inputToCb(Mat input) {
            Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
            Core.extractChannel(YCrCb, Cb, 2);
        }

        @Override
        public void init(Mat firstFrame) {
            inputToCb(firstFrame);
            for (int i = 0; i < 3; i++) {
                regions[i] = firstFrame.submat(new Rect(
                        new Point(firstFrame.cols() * topLeft[i][0], firstFrame.rows() * topLeft[i][1]),
                        new Point(firstFrame.cols() * botRight[i][0], firstFrame.rows() * botRight[i][1])
                ));
            }

        }

        @Override
        public Mat processFrame(Mat input) {
            for (int i = 0; i < 3; i++) {
                regions[i] = input.submat(new Rect(
                        new Point(input.cols() * topLeft[i][0], input.rows() * topLeft[i][1]),
                        new Point(input.cols() * botRight[i][0], input.rows() * botRight[i][1])
                ));
            }

            for (int i = 0; i < 3; i++) {
                double[] avg = Core.mean(regions[i]).val;
                for (int j = 0; j < 3; j++) {
                    avgRGB[i][j] = (int) avg[j];
                }
            }
            int rect = getAnalysis();
            if (rect != -1) {
                Imgproc.rectangle(
                        input,
                        new Point(
                                input.cols() * topLeft[rect][0],
                                input.rows() * topLeft[rect][1]),
                        new Point(
                                input.cols() * botRight[rect][0],
                                input.rows() * botRight[rect][1]),
                        new Scalar(0, 255, 0), 4);
            }
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