import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumDrive {
    private DcMotor fr;
    private DcMotor fl;
    private DcMotor br;
    private DcMotor bl;

    public MecanumDrive(HardwareMap hardwareMap) {
        fr = hardwareMap.get(DcMotor.class, "fr");
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fl = hardwareMap.get(DcMotor.class, "fl");
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        br = hardwareMap.get(DcMotor.class, "br");
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        bl = hardwareMap.get(DcMotor.class, "bl");
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void setPowers(double frPower, double flPower, double brPower, double blPower) {
        double maxPower = 1.0;
        maxPower = Math.max(maxPower, frPower);
        maxPower = Math.max(maxPower, flPower);
        maxPower = Math.max(maxPower, brPower);
        maxPower = Math.max(maxPower, blPower);
        frPower /= maxPower;
        flPower /= maxPower;
        brPower /= maxPower;
        blPower /= maxPower;

        fr.setPower(frPower);
        fl.setPower(flPower);
        br.setPower(brPower);
        bl.setPower(blPower);
    }

    public void drive(double forward, double right, double rotate) {
        double frPower = forward - right - rotate;
        double flPower = forward + right + rotate;
        double brPower = forward + right - rotate;
        double blPower = forward - right + rotate;

        setPowers(frPower, flPower, brPower, blPower);
    }
}
