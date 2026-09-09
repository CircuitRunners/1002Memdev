import com.bylazar.gamepad.PanelsGamepad;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Run this OFF the robot, or with nothing in the claw, to find the real
 * open/closed servo positions. Step in small increments -- if the servo
 * buzzes or gets warm you have hit the mechanical stop; back off immediately.
 *
 * Put the numbers you find into Claw.OPEN and Claw.CLOSED.
 */
@TeleOp(name = "Claw Tuner", group = "tuning")
public class ClawTuner extends OpMode {
    private Servo claw;
    private double pos = 0.5;
    private boolean lastUp, lastDown;

    @Override
    public void init() {
        claw = hardwareMap.get(Servo.class, "claw");
    }
    //0.1 extended, 0.67 retracted

    @Override
    public void loop() {
        // one press = one 0.01 step, so you creep up on the limits

        Gamepad g1 = PanelsGamepad.INSTANCE
                .getFirstManager()
                .asCombinedFTCGamepad(gamepad1);

        if (g1.right_bumper )   pos += 0.01;
        if (g1.left_bumper  ) pos -= 0.01;
//        lastUp   = g1.right_bumper;
//        lastDown = g1.left_bumper;

        pos = Range.clip(pos, 0.0, 1.0);
        claw.setPosition(pos);

        telemetry.addData("claw position", "%.3f", pos);
        telemetry.addLine("RB = +0.01, LB = -0.01");
        telemetry.update();
    }
}
