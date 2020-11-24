package org.firstinspires.ftc.teamcode.Chassis;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Common.Assembly;
import org.firstinspires.ftc.teamcode.Common.Config;
import org.firstinspires.ftc.teamcode.Test.PowerShot;
import org.firstinspires.ftc.teamcode.Visual.Visual;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import java.util.concurrent.Callable;

import static org.firstinspires.ftc.teamcode.Common.Constants.encoderTicksPerInch;

public class SampleDrive extends Drive{

    //declare wheel motors
    private DcMotorEx motorFL;
    private DcMotorEx motorFR;
    private DcMotorEx motorBL;
    private DcMotorEx motorBR;
    private final double maxVel = 2500;
    private Visual visual;
    public double currentX = 0;
    public double currentY = 0;

    BNO055IMU imu;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        super.init(hardwareMap, telemetry);
        motorFL = (DcMotorEx)hardwareMap.dcMotor.get(Config.DRIVEFL);
        motorFR = (DcMotorEx)hardwareMap.dcMotor.get(Config.DRIVEFR);
        motorBL = (DcMotorEx)hardwareMap.dcMotor.get(Config.DRIVEBL);
        motorBR = (DcMotorEx)hardwareMap.dcMotor.get(Config.DRIVEBR);

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters IMUParameters = new BNO055IMU.Parameters();
        IMUParameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        IMUParameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        IMUParameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        IMUParameters.loggingEnabled      = true;
        IMUParameters.loggingTag          = "IMU";
        IMUParameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(IMUParameters);

        // Start the logging of measured acceleration
        imu.startAccelerationIntegration(new Position(), new Velocity(), 10);
    }

    //positive forwards value moves forwards, negative forwards value moves backwards
    //positive sideways value moves right, negative sideways value moves left
    //positive turn value moves clockwise, negative turn value moves counter-clockwise
    @Override
    public void drive(double forwards, double sideways, double turn) {
        motorFL.setVelocity((-forwards + sideways + turn) * maxVel);
        motorFR.setVelocity((forwards + sideways + turn) * maxVel);
        motorBL.setVelocity((-forwards - sideways + turn) * maxVel);
        motorBR.setVelocity((forwards - sideways + turn) * maxVel);
    }

    @Override
    public void move(double inchesX, double inchesY, double power) {
        //update the position of the bot according to the move, where it's moving from, and the direction it's moving
        double hypotenuse = Math.sqrt(Math.pow(inchesX, 2) + Math.pow(inchesY, 2));
        double movingDirection = (-Math.asin(inchesX/hypotenuse)) + imu.getAngularOrientation().firstAngle;
        currentX += Math.sin(-movingDirection) * hypotenuse;
        currentY += Math.cos(-movingDirection) * hypotenuse;

        //convert to encoder ticks for run to position
        inchesX *= encoderTicksPerInch;
        inchesY *= encoderTicksPerInch;

        //set where bot should end up according to encoders
        motorFL.setTargetPosition(motorFL.getCurrentPosition() + (int)inchesY + (int)inchesX);
        motorFR.setTargetPosition(motorFR.getCurrentPosition() - (int)inchesY + (int)inchesX);
        motorBL.setTargetPosition(motorBL.getCurrentPosition() + (int)inchesY - (int)inchesX);
        motorBR.setTargetPosition(motorBR.getCurrentPosition() - (int)inchesY - (int)inchesX);

        motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        boolean motorFLTolerance = false;
        boolean motorFRTolerance = false;
        boolean motorBLTolerance = false;
        boolean motorBRTolerance = false;

        while((!motorFLTolerance || !motorFRTolerance || !motorBLTolerance || !motorBRTolerance)) {
            try {
                if(isStopRequested.call())
                    return;
            }
            catch (NullPointerException exception){
                telemetry.addLine("You need to set isStopRequested when using move");
            }
            catch (Exception ignored) {}

            //add tolerance of 0.1 inches over and under in case bot is not exact
            motorFLTolerance = (motorFL.getCurrentPosition() >= motorFL.getTargetPosition() - (encoderTicksPerInch * 0.1) && motorFL.getCurrentPosition() <= motorFL.getTargetPosition() + (encoderTicksPerInch * 0.1));
            motorFRTolerance = (motorFR.getCurrentPosition() >= motorFR.getTargetPosition() - (encoderTicksPerInch * 0.1) && motorFR.getCurrentPosition() <= motorFR.getTargetPosition() + (encoderTicksPerInch * 0.1));
            motorBLTolerance = (motorBL.getCurrentPosition() >= motorBL.getTargetPosition() - (encoderTicksPerInch * 0.1) && motorBL.getCurrentPosition() <= motorBL.getTargetPosition() + (encoderTicksPerInch * 0.1));
            motorBRTolerance = (motorBR.getCurrentPosition() >= motorBR.getTargetPosition() - (encoderTicksPerInch * 0.1) && motorBR.getCurrentPosition() <= motorBR.getTargetPosition() + (encoderTicksPerInch * 0.1));

            //move different speeds depending on how far you're moving
            if(hypotenuse > 24) {
                motorFL.setPower(power);
                motorFR.setPower(power);
                motorBL.setPower(power);
                motorBR.setPower(power);
            }
            else if(hypotenuse <= 24) {
                motorFL.setPower(power/2);
                motorFR.setPower(power/2);
                motorBL.setPower(power/2);
                motorBR.setPower(power/2);
            }
            telemetry.update();
        }
        //stop everything
        motorFL.setPower(0);
        motorFR.setPower(0);
        motorBL.setPower(0);
        motorBR.setPower(0);
        motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    //just move without a power idk
    @Override
    public void move(double inchesX, double inchesY) {
        move(inchesX, inchesY, 1);
    }


    //imu only accounts for angles from -180 to 180
    //convert angles out of this range to usable angles in this range
    public double convertAngle(double degrees) {
        if(degrees > 180) {
            return degrees - 360;
        }
        else if(degrees < -180) {
            return degrees + 360;
        }
        else{
            return degrees;
        }
    }

    //positive degrees is counter clockwise and negative degrees is clockwise
    @Override
    public void turn(double degrees) {
        double targetAngle = convertAngle(imu.getAngularOrientation().firstAngle + degrees);
        double currentAngle;
        double clockwiseDistance = 0;
        double counterClockwiseDistance = 0;

        boolean angleTolerance = false;

        while(!angleTolerance)  {
            try {
                if(isStopRequested.call())
                    return;
            }
            catch (NullPointerException exception){
                telemetry.addLine("You need to set isStopRequested when using move");
            }
            catch (Exception ignored) {}

            telemetry.addData("imu", imu.getAngularOrientation().firstAngle);

            //add tolerance of 5 degrees over and under in case bot is not exact
            angleTolerance = (imu.getAngularOrientation().firstAngle >= convertAngle(targetAngle-5) && imu.getAngularOrientation().firstAngle <= convertAngle(targetAngle+5));

            //calculate clockwise distance and counter-clockwise distance
            currentAngle = imu.getAngularOrientation().firstAngle;
            while(currentAngle < targetAngle) {
                currentAngle = convertAngle(currentAngle + 1);
                counterClockwiseDistance = counterClockwiseDistance + 1;
            }
            telemetry.addData("counterClockwiseDistance", counterClockwiseDistance);
            currentAngle = imu.getAngularOrientation().firstAngle;
            while(currentAngle > targetAngle) {
                currentAngle = convertAngle(currentAngle - 1);
                clockwiseDistance = clockwiseDistance + 1;
            }
            telemetry.addData("clockwiseDistance", clockwiseDistance);

            //turn clockwise or counter-clockwise depending on which turn will be shorter
            //turn speed is also determined by how far away it is from its target angle
            if(clockwiseDistance > counterClockwiseDistance && counterClockwiseDistance > 90){
                drive(0,0,-1);
                telemetry.addLine("Turning Fast CounterClockwise");
            }
            else if(clockwiseDistance < counterClockwiseDistance && clockwiseDistance > 90){
                drive(0,0,1);
                telemetry.addLine("Turning Fast Clockwise");
            }
            else if(clockwiseDistance > counterClockwiseDistance){
                drive(0,0,-0.5);
                telemetry.addLine("Turning Slow CounterClockwise");
            }
            else if(clockwiseDistance < counterClockwiseDistance) {
                drive(0,0,0.5);
                telemetry.addLine("Turning Slow Clockwise");
            }
            telemetry.update();
        }
        //stop everything
        telemetry.addLine("turning done");
        drive(0,0,0);
        telemetry.update();
    }

    //turn until the bot is facing the front of the field
    //technically just turns the bot whatever angle it faced when the round started
    @Override
    public void alignForward() {
        turn(-imu.getAngularOrientation().firstAngle);
    }

    //move the bot to a position on the field using maths
    @Override
    public void moveToPosition(double x, double y) {
        //determine how faw away the bot is from where you want it to go
        double distanceFromX = Math.abs(x);
        double distanceFromY = Math.abs(y);

        if(currentX > 0) {
            distanceFromX = Math.abs(currentX - x);
        }
        else if(currentX < 0) {
            distanceFromX  = Math.abs(x - currentX);
        }
        if(currentY > 0) {
            distanceFromY = Math.abs(currentY -  y);
        }
        else if(currentY < 0) {
            distanceFromY = Math.abs(y - currentY);
        }

        //turn bot until facing field's positive y-axis
        alignForward();

        //drive to the target
        if(x > currentX && y > currentY) {
            move(distanceFromX, distanceFromY);
        }
        else if(x > currentX && y < currentY) {
            move(distanceFromX, -distanceFromY);
        }
        else if(x < currentX && y > currentY) {
            move(-distanceFromX, distanceFromY);
        }
        else if(x < currentX && y < currentY) {
            move(-distanceFromX, -distanceFromY);
        }

        //turn bot facing forwards again
        alignForward();

        telemetry.addData("x position", currentX);
        telemetry.addData("y position", currentY);
        telemetry.update();
    }

    //same thing as moveToPosition(double x, double y) but extracts target position from a vector
    @Override
    public void moveToPosition(VectorF target) {
        double targetX = target.get(0);
        double targetY = target.get(2);

        moveToPosition(targetX, targetY);
    }

    //move the bot to the shooting position in front of the tower
    @Override
    public void moveToTower() {
        //the shooting position is different depending on where the bot starts in the round
        //use distance sensors to determine where we are starting from to determine the shooting position
        if(1 == 1) {
            moveToPosition(-12.5, 55.5);
        }
        else if(0 == 1) {
            moveToPosition(12.5,55.5);
        }
    }

    //move the bot to the shooting position in front of the rightmost powershot target
    @Override
    public void moveToPower1() {
        //the shooting position is different depending on where the bot starts in the round
        //use distance sensors to determine where we are starting from to determine the shooting position
        if(1 == 1) {
            moveToPosition(-29.5,55.5);
        }
        else if(0 == 1) {
            moveToPosition(-4.5,55.5);
        }
    }

    //move the bot to the shooting position in front of the center powershot target
    @Override
    public void moveToPower2() {
        //the shooting position is different depending on where the bot starts in the round
        //use distance sensors to determine where we are starting from to determine the shooting position
        if(1 == 1) {
            moveToPosition(-37.5,55.5);
        }
        else if(0 == 1) {
            moveToPosition(-12.5,55.5);
        }
    }

    //move the bot to the shooting position in front of the leftmost powershot target
    @Override
    public void moveToPower3() {
        //the shooting position is different depending on where the bot starts in the round
        //use distance sensors to determine where we are starting from to determine the shooting position
        if(1 == 1) {
            moveToPosition(-45.5,55.5);
        }
        else if(0 == 1) {
            moveToPosition(-20.5,55.5);
        }
    }

    //stop everything
    @Override
    public void stop()
    {
        motorBR.setPower(0);
        motorFR.setPower(0);
        motorBL.setPower(0);
        motorFL.setPower(0);

    }

    //get the angle
    @Override
    public double getAngle() {
        return imu.getAngularOrientation().firstAngle;
    }


}
