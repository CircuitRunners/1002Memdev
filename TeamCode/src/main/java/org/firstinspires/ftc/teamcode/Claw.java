package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Claw servo wrapper. Same pattern as MecanumDrive: plain class, no OpMode annotation.
 *
 * OPEN / CLOSED are placeholders -- run the "Claw Tuner" OpMode, find the real
 * numbers, then replace them here.
 */
public class Claw {
    public static double OPEN   = 0.55;
    public static double CLOSED = 0.25;

    private Servo claw;

    public void init(HardwareMap hardwareMap) {
        claw = hardwareMap.get(Servo.class, "claw");
        claw.setDirection(Servo.Direction.FORWARD); // flip if open/close are backwards
        open();  // start open -- safer than clamping onto whatever is in there at init
    }

    public void open() {
        claw.setPosition(OPEN);
    }

    public void close() {
        claw.setPosition(CLOSED);
    }

    public void setPosition(double p) {
        claw.setPosition(Range.clip(p, 0.0, 1.0));
    }

    public double getPosition() {
        return claw.getPosition();
    }
}
