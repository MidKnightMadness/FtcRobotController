package org.firstinspires.ftc.teamcode.Test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
@Disabled
public class MotorTest extends OpMode {
    DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.dcMotor.get("motor1");
    }

    @Override
    public void loop() {
        motor.setPower(gamepad1.left_stick_y);
    }
}