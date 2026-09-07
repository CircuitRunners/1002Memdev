import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.MecanumDrive;
@TeleOp
public class MecanumSid extends OpMode {
    double speedMultiply = 1.0;
    MecanumDrive pratham;
    @Override
    public void init() {
        pratham = new MecanumDrive();
        pratham.init(hardwareMap);
        telemetry.addLine("Ready!");
        telemetry.update();
    }

    @Override
    public void loop() {
        double leftJoystickXAxis = -gamepad1.left_stick_x;
        double leftJoystickYaxis =
        gamepad1.left_stick_y;
        double rightJoystickXaxis = gamepad1.right_stick_x;
        pratham.drive(leftJoystickYaxis, leftJoystickXAxis,rightJoystickXaxis);
    }
}
