package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;



@TeleOp
public class MecanumDriveTeleOp extends OpMode {

    private MecanumDrive mecanumDrive;

    @Override
    public void init() {
        mecanumDrive = new MecanumDrive();
    }

    @Override
    public void loop() {
        double forward = gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        mecanumDrive.drive(forward, right, rotate);
    }
}
