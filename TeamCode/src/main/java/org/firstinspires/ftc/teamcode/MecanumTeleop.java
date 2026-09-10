package org.firstinspires.ftc.teamcode;
import com.bylazar.gamepad.PanelsGamepad;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.opmode.MecanumDrive;
@TeleOp(name = "MecanumSH")
public class MecanumTeleop extends OpMode {
    private MecanumDrive drive;
    private Claw claw;
    private Arm arm;
    private double speedMultiply = 1.0;
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
        Gamepad g1 = PanelsGamepad.INSTANCE
                .getFirstManager()
                .asCombinedFTCGamepad(gamepad1);


        if (g1.dpadDownWasPressed()) {
            speedMultiply = 0.25;
        } else if (g1.dpadLeftWasPressed()) {
            speedMultiply = 0.5;
        } else if (g1.dpadRightWasPressed()) {
            speedMultiply = 0.75;
        } else if (g1.dpadUpWasPressed()) {
            speedMultiply = 1.0;
        }
    double forward = -1*(g1.left_stick_y*speedMultiply);
    double strafe = g1.left_stick_x*speedMultiply;
    double rotate = g1.right_stick_x*speedMultiply;

    drive.drive(forward, strafe, rotate);

    if (g1.rightBumperWasPressed()) {
        claw.close();
    }
    else if (g1.leftBumperWasPressed()){
        claw.open();}
    if (g1.yWasPressed()){
        arm.setTarget(Arm.HIGH);
    }
    else if (g1.bWasPressed()){
        arm.setTarget(Arm.MIDD);
    }
    else if (g1.xWasPressed()) {
        arm.setTarget(Arm.MIDH);
    }
    else if (g1.aWasPressed()) {
        arm.setTarget(Arm.DOWN);
    }
    double jog = (g1.right_trigger - g1.left_trigger) * ARM_JOG_RATE;

    if (Math.abs(jog) > 0.1) arm.adjustTarget(jog);

    arm.update();

    telemetry.addData("speed", speedMultiply);
    telemetry.addData("claw", "%.3f", claw.getPosition());
    telemetry.addData("arm pos/target", "%d / %.0f", arm.getPosition(), arm.getTarget());
    telemetry.addData("arm power", "%.2f", arm.getPower());
    telemetry.update();
    }
}