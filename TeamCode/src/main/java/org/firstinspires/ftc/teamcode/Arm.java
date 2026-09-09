package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.controller.PIDFController;

/**
 * Arm with closed-loop position control. Constants came out of armTuner.
 *
 * NOTE: the encoder is reset at init(), so whatever position the arm is
 * physically in when you press INIT becomes 0. Always start with the arm
 * at its resting/down position.
 */
@Configurable
public class Arm {
    // tuned in armTuner
    public static double armP = 0.004;
    public static double armI = 0;
    public static double armD = 0.0001;
    public static double armF = 0.00005;

    // soft limits, in encoder ticks -- widen once you know the arm's real travel
    public static int MIN_TICKS = 0;
    public static int MAX_TICKS = 1200;
    public static int TOLERANCE_TICKS = 50;

    // presets
    public static int DOWN = 0;
    public static int MID  = 400;
    public static int HIGH = 800;

    private DcMotorEx motor;
    private PIDFController controller;
    private double target = 0;

    public void init(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotorEx.class, "fghj");
        controller = new PIDFController(armP, armI, armD, armF);

        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        target = 0;
    }

    /** Call once per loop() iteration. */
    public void update() {
        controller.setPIDF(armP, armI, armD, armF);
        double power = controller.calculate(motor.getCurrentPosition(), target);
        motor.setPower(Range.clip(power, -1.0, 1.0));
    }

    public void setTarget(double ticks) {
        target = Range.clip(ticks, MIN_TICKS, MAX_TICKS);
    }

    /** Nudge the target, for trigger/stick jogging. */
    public void adjustTarget(double deltaTicks) {
        setTarget(target + deltaTicks);
    }

    public double getTarget()   { return target; }
    public int getPosition()    { return motor.getCurrentPosition(); }
    public double getPower()    { return motor.getPower(); }
    public boolean atTarget()   { return Math.abs(getTarget() - getPosition()) <= TOLERANCE_TICKS; }
}
