package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class TankDrive {

    private DcMotor fr;
    private DcMotor fl;
    private DcMotor br;
    private DcMotor bl;

    public TankDrive(HardwareMap hardwareMap) {
        fr = hardwareMap.get(DcMotor.class, "fr");
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fl = hardwareMap.get(DcMotor.class, "fl");
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        br = hardwareMap.get(DcMotor.class, "br");
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        bl = hardwareMap.get(DcMotor.class, "bl");
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);


    }

    private void setPowers(double leftPower, double rightPower) {
        double maxPower = 1.0;
        maxPower = Math.max(leftPower, rightPower);

        if (maxPower > 1.0) {
            leftPower /= maxPower;
            rightPower /= maxPower;
        }

        fl.setPower(leftPower);
        bl.setPower(leftPower);
        fr.setPower(rightPower);
        br.setPower(rightPower);
    }

    public void drive(double forward, double right) {
        setPowers(forward - right, forward + right);
    }


}
