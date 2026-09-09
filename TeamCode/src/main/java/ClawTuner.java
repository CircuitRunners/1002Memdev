import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Claw Tuner", group = "tuning")
public class ClawTuner extends OpMode {
    private Servo claw;
    private double pos = 0.5;
    private boolean lastUp, lastDown;
    @Override
    public void init() {
        claw = hardwareMap.get(Servo.class, "claw");
    }
    @Override
    public void loop() {
        // one press = one 0.01 step, so you creep up on the limits
        if (gamepad1.right_bumper && !lastUp)   pos += 0.01;
        if (gamepad1.left_bumper  && !lastDown) pos -= 0.01;
        lastUp   = gamepad1.right_bumper;
        lastDown = gamepad1.left_bumper;

        pos = Range.clip(pos, 0.0, 1.0);
        claw.setPosition(pos);

        telemetry.addData("claw position", "%.3f", pos);
        telemetry.addLine("RB = +0.01, LB = -0.01");
        telemetry.update();
    }
}
