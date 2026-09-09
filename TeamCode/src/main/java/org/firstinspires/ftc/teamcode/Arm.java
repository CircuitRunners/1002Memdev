package org.firstinspires.ftc.teamcode;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.controller.PIDFController;
@Configurable
public class Arm {
    public static double armP = 0.004;
    public static double armI = 0;
    public static double armD = 0.0001;
    public static double armF = 0.00005;

    public static int MIN_TICKS = 0;
    public static int MAX_TICKS = 1200;

    public static int DOWN = 0;
    public static int MIDD  = 266;
    public static int MIDH = 532;
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

    public void update() {
        controller.setPIDF(armP, armI, armD, armF);
        double power = controller.calculate(motor.getCurrentPosition(), target);
        motor.setPower(Range.clip(power, -1.0, 1.0));
    }

    public void setTarget(double ticks) {
        target = Range.clip(ticks, MIN_TICKS, MAX_TICKS);
    }

    public void adjustTarget(double deltaTicks) {
        setTarget(target + deltaTicks);
    }

    public double getTarget()   { return target; }
    public int getPosition()    { return motor.getCurrentPosition(); }
    public double getPower()    { return motor.getPower(); }
}