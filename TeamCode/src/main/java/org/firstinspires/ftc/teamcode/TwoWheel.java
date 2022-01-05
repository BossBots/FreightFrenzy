package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.bosch.BNO055IMU;

import org.checkerframework.checker.units.qual.C;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class TwoWheel {

    // motor variables
    private double leftPower = 0;
    private double rightPower = 0;
    private DcMotor left;
    private DcMotor right;

    // kinematics variables
    private int counts;
    private int[] initEncoderPos;
    private int[] finalEncoderPos;
    private double displacement;
    private double[] pos = new double[2];
    private final double CONVERSION = 2. * Math.PI * 0.0254 / 480.; // meters per count

    // rotation variables
    private Orientation angles;

    // IMU
    private BNO055IMU imu;
    private BNO055IMU.Parameters params;


    public TwoWheel(DcMotor initLeft, DcMotor initRight, BNO055IMU initIMU) {
        left = initLeft;
        right = initRight;
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        imu = initIMU;
        params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.calibrationDataFile = "IMUCal.java";
        imu.initialize(params);
        counts = 0;

        setMode(false);
    }

    public double[] getPower() {return new double[]{leftPower, rightPower};}
    public double[] getPos() {return pos;}
    public int getCounts() {return counts;}
    public double getAngle() {return (double) angles.thirdAngle;}
    public int[] getEncoderPos() {return finalEncoderPos;}

    public void brake(long dur) {
        leftPower = 0;
        rightPower = 0;
        left.setPower(leftPower);
        right.setPower(rightPower);
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < dur) {
            drive(0, 0);
        }
    }

    public void fd(double pwr, long dur) {
        int leftPos = left.getCurrentPosition();
        int rightPos = right.getCurrentPosition();
        double angle = getAngle();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < dur) {
            if (Math.abs(getAngle() - angle) < 5) {
                drive(pwr, 0);
            } else if ((getAngle() - angle) > 5) {
                drive(pwr, pwr/2);
            } else {
                drive(pwr, -pwr/2);
            }
        }
        brake(125);
    }

    public void rot(double pwr, double targetAngle) {
        if (targetAngle != 180) {
            while (Math.abs(angles.thirdAngle - targetAngle) > 3) {
                drive(0, pwr);
            }
        } else {
            while (Math.abs(targetAngle - Math.abs(angles.thirdAngle)) > 3) {
                drive(0, pwr);
            }
        }
        brake(125);
    }

    public void moveEncoder(double power, int dist) {
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setTargetPosition(finalEncoderPos[0] + dist);
        right.setTargetPosition(finalEncoderPos[1] + dist);
        left.setPower(power);
        right.setPower(power);
    }

    public void moveDist(double power, double dist) {
        int countDist = (int) (dist / CONVERSION);
        left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        left.setTargetPosition(finalEncoderPos[0] + countDist);
        right.setTargetPosition(finalEncoderPos[1] + countDist);
        left.setPower(power);
        right.setPower(power);
    }

    public void setMode(boolean useEncoder) {
        if (useEncoder) {
            left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public void integrate() {
        finalEncoderPos = new int[] {left.getCurrentPosition(), right.getCurrentPosition()};
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
        displacement = ((double) (finalEncoderPos[0] - initEncoderPos[0]) + (double) (finalEncoderPos[1] - initEncoderPos[1])) * CONVERSION / 2.;
        pos[0] += displacement * Math.sin(Math.PI * angles.thirdAngle / 180.);
        pos[1] += displacement * Math.cos(Math.PI * angles.thirdAngle / 180.);
        counts += 1;
        initEncoderPos[0] = finalEncoderPos[0];
        initEncoderPos[1] = finalEncoderPos[1];
    }

    public void drive(double move, double yaw) {

        if (counts == 0) {
            initEncoderPos = new int[] {left.getCurrentPosition(), right.getCurrentPosition()};
        }

        leftPower = move + yaw;
        rightPower = move - yaw;

        if (leftPower > 1) {
            leftPower = 1;
        } else if (leftPower < -1) {
            leftPower = -1;
        }
        if (rightPower > 1) {
            rightPower = 1;
        } else if (rightPower < -1) {
            rightPower = -1;
        }

        left.setPower(leftPower);
        right.setPower(rightPower);

        integrate();
    }
}