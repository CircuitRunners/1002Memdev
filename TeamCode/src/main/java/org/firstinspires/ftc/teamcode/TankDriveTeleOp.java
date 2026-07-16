package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class TankDriveTeleOp extends OpMode {

    private TankDrive tankDrive;

    @Override
    public void init() {
        tankDrive = new TankDrive(hardwareMap);
    }

    @Override
    public void loop() {
        double forward = gamepad1.left_stick_y;
        double right = gamepad1.right_stick_x;

        tankDrive.drive(forward, right);

    }
}
