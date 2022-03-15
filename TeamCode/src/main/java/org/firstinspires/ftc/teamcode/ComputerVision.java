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
    private final int fractions = 12;
    private final double[][] topLeft = new double[fractions][2];
    private final double[][] botRight = new double[fractions][2];
    private int[][] avgRGB = new int[fractions][3];
    private final int[] RED = {0, 200};
    private final int[] GREEN = {50, 255};
    private final int[] BLUE = {0, 200};
    private int x;
    private int[] longestSeq = new int[2];

    public ComputerVision(int camId) {
        for (int i = 0; i < fractions; i++) {
            topLeft[i][0] = 0d / 3d;
            topLeft[i][1] = ((double) i) / ((double) fractions);
            botRight[i][0] = 1d / 3d;
            botRight[i][1] = ((double) (i + 1)) / ((double) fractions);
        }
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, camId);
        phoneCam.setPipeline(new ComputerVision.Pipeline());
        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                phoneCam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
            }
            @Override
            public void onError(int errorCode) {
            }
        });
    }

    public int getX() {return x;}

    public int[] getAnalysis() {
        /*int output = -1;
        int max_red = 0;
        for (int i = 0; i < fractions; i++) {
            if (avgRGB[i][1] < MAX_GREEN * avgRGB[i][0] && avgRGB[i][1] > MIN_GREEN * avgRGB[i][0] && avgRGB[i][2] < MAX_BLUE * avgRGB[i][0] && (output == -1 || avgRGB[output][0] <= avgRGB[i][0])) {
                output = i;
            }
            if (avgRGB[i][0] > max_red) {
                output = i;
                max_red = avgRGB[i][0];
            }
        }
        return output;
        */
        x+=1;
        longestSeq = new int[2];
        boolean[] seq = new boolean[fractions];
        for (int i = 0; i < fractions; i++) {
            seq[i] = (
                    (RED[0] < avgRGB[i][0] && avgRGB[i][0] < RED[1]) &&
                            (GREEN[0] < avgRGB[i][1] && avgRGB[i][1] < GREEN[1]) &&
                            (BLUE[0] < avgRGB[i][2] && avgRGB[i][2] < BLUE[1]) &&
                            (avgRGB[i][1] * 0.9 > avgRGB[i][0] && avgRGB[i][1] * 0.9 > avgRGB[i][2])
            );
        }
        int[] currentSeq = new int[2];
        boolean prevTrue = false;
        for (int i = 0; i < fractions; i++) {
            if (seq[i]) {
                if (prevTrue) {
                    currentSeq[1] = i;
                } else {
                    currentSeq[0] = i;
                }
                prevTrue = true;
            } else {
                if (prevTrue) {
                    prevTrue = false;
                    currentSeq[1] = i;
                }
                if (currentSeq[1] - currentSeq[0] > longestSeq[1] - longestSeq[0]) {
                    longestSeq[0] = currentSeq[0];
                    longestSeq[1] = currentSeq[1];
                }
            }
        }
        if (prevTrue) {
            currentSeq[1] = fractions;
        }
        if (currentSeq[1] - currentSeq[0] > longestSeq[1] - longestSeq[0]) {
            longestSeq[0] = currentSeq[0];
            longestSeq[1] = currentSeq[1];
        }
        return longestSeq;
    }

    public int getRecognition() {
        return (int) (Math.round((double) (longestSeq[1] + longestSeq[0]) / 2.)) / (fractions / 3);
    }

    public int[][] getRGB() {return avgRGB;}

    class Pipeline extends OpenCvPipeline {

        private boolean viewportPaused = false;
        private Mat YCrCb = new Mat();
        private Mat Cb = new Mat();
        private Mat[] regions = new Mat[fractions];

        private void inputToCb(Mat input) {
            Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
            Core.extractChannel(YCrCb, Cb, 2);
        }

        @Override
        public void init(Mat firstFrame) {
            inputToCb(firstFrame);
            for (int i = 0; i < fractions; i++) {
                regions[i] = firstFrame.submat(new Rect(
                        new Point(firstFrame.cols() * topLeft[i][0], firstFrame.rows() * topLeft[i][1]),
                        new Point(firstFrame.cols() * botRight[i][0], firstFrame.rows() * botRight[i][1])
                ));
            }

        }

        @Override
        public Mat processFrame(Mat input) {
            for (int i = 0; i < fractions; i++) {
                regions[i] = input.submat(new Rect(
                        new Point(input.cols() * topLeft[i][0], input.rows() * topLeft[i][1]),
                        new Point(input.cols() * botRight[i][0], input.rows() * botRight[i][1])
                ));
            }
            double[] avg;
            for (int i = 0; i < fractions; i++) {
                avg = Core.mean(regions[i]).val;
                for (int j = 0; j < 3; j++) {
                    avgRGB[i][j] = (int) avg[j];
                }
            }
            getAnalysis();
            if (longestSeq[0] != longestSeq[1]) {
                Imgproc.rectangle(
                        input,
                        new Point(
                                input.cols() * topLeft[longestSeq[0]][0],
                                input.rows() * topLeft[longestSeq[0]][1]),
                        new Point(
                                input.cols() * botRight[longestSeq[1] - 1][0],
                                input.rows() * botRight[longestSeq[1] - 1][1]),
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