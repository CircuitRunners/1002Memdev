package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.MecanumDrive;
@TeleOp(name = "MecanumP")
public class MecanumTeleop extends OpMode {
    private MecanumDrive drive;
    private Claw claw;
    private Arm arm;
    private double speedMultiply = 0.8;

    /** ticks of target change per loop at full trigger */
    private static final double ARM_JOG_RATE = 8.0;

    @Override
    public void init() {
        drive = new MecanumDrive();
        drive.init(hardwareMap);
        claw = new Claw();
        claw.init(hardwareMap);
        arm = new Arm();
        arm.init(hardwareMap);
        telemetry.addLine("Pready!");
        telemetry.update();
    }
    @Override
    public void loop() {
        if (gamepad1.dpad_up) {
            speedMultiply = 0.25;
        } else if (gamepad1.dpad_left) {
            speedMultiply = 0.5;
        } else if (gamepad1.dpad_down) {
            speedMultiply = 0.75;
        } else if (gamepad1.dpad_right) {
            speedMultiply = 1.0;
        }

    double forward = -1*gamepad1.left_stick_y*speedMultiply;
    double strafe = gamepad1.left_stick_x*speedMultiply;
    double rotate = gamepad1.right_stick_x*speedMultiply;

    drive.drive(forward, strafe, rotate);
        // ----- claw: bumpers -----
        if (gamepad1.right_bumper)     claw.close();
        else if (gamepad1.left_bumper) claw.open();

        // ----- arm presets: Y = high, B = mid, A = down -----
        if (gamepad1.y)      arm.setTarget(Arm.HIGH);
        else if (gamepad1.b) arm.setTarget(Arm.MID);
        else if (gamepad1.a) arm.setTarget(Arm.DOWN);

        // ----- arm jog: triggers nudge the target up/down -----
        double jog = (gamepad1.right_trigger - gamepad1.left_trigger) * ARM_JOG_RATE;
        if (Math.abs(jog) > 0.1) arm.adjustTarget(jog);

        arm.update();

    telemetry.addData("speed", speedMultiply);
    telemetry.addData("claw", "%.3f", claw.getPosition());
    telemetry.addData("arm pos/target", "%d / %.0f", arm.getPosition(), arm.getTarget());
    telemetry.addData("arm power", "%.2f", arm.getPower());
    telemetry.update();
    }
}