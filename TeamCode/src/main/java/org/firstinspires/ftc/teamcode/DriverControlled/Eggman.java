package org.firstinspires.ftc.teamcode.DriverControlled;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Chassis.Drive;
import org.firstinspires.ftc.teamcode.Chassis.SampleDrive;
import org.firstinspires.ftc.teamcode.Common.Config;
import org.firstinspires.ftc.teamcode.Intake.Intake;
import org.firstinspires.ftc.teamcode.Intake.SampleIntake;
import org.firstinspires.ftc.teamcode.Outtake.Outtake;
import org.firstinspires.ftc.teamcode.Outtake.SampleOuttake;
import org.firstinspires.ftc.teamcode.WobbleGoal.WobbleGoal;
import org.firstinspires.ftc.teamcode.WobbleGoal.SampleWobbleGoal;

@TeleOp
public class Eggman extends OpMode {

    private Drive drive = new SampleDrive();
    private Intake intake = new SampleIntake();
    private Outtake outtake = new SampleOuttake();
    private WobbleGoal wobbleGoal = new SampleWobbleGoal();
    private int intToggle = 0;
    private boolean lastLeftBumper = false;
    private int outToggle = 0;
    private boolean lastRightBumper = false;
    private int openWobToggle = 0;
    private boolean lastAButton = false;

    @Override
    public void init() {
        drive.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        outtake.init(hardwareMap, telemetry);
        wobbleGoal.init(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        //drive controlled by left stick and right stick
        drive.drive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        //intake is a toggle controlled by left bumper
        if(!lastLeftBumper && gamepad1.left_bumper) {
            if(intToggle == 1) {
                intToggle = 0;
            }
            else if(intToggle == 0) {
                intToggle = 1;
            }
        }
        if(intToggle == 0) {
            outtake.stop();
        }
        if(intToggle == 1) {
            outtake.start();
        }
        lastLeftBumper = gamepad1.left_bumper;

        //outtake is a toggle controlled by right bumper
        if(!lastRightBumper && gamepad1.right_bumper) {
            if(outToggle == 1) {
                outToggle = 0;
            }
            else if(outToggle == 0) {
                outToggle = 1;
            }
        }
        if(outToggle == 0) {
            intake.stop();
        }
        if(outToggle == 1) {
            intake.start();
        }
        lastRightBumper = gamepad1.right_bumper;

        //wobble goal toggles between open and close using a button
        if(!lastAButton && gamepad1.a) {
            if(openWobToggle == 1) {
                openWobToggle = 0;
            }
            else if(openWobToggle == 0) {
                openWobToggle = 1;
            }
        }
        if(openWobToggle == 0) {
            wobbleGoal.close();
        }
        if(openWobToggle == 1) {
            wobbleGoal.open();
        }
        lastAButton = gamepad1.a;
    }

}