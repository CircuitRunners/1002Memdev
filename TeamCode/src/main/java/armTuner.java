import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.controller.PIDFController;

@Configurable
@TeleOp
public class armTuner extends OpMode {
    public DcMotorEx qwerty;
    public PIDFController armPIDcontroller;

    public static double armP = 0, armI = 0, armD = 0, armF = 0;
    public static double armSetPoint = 0;
    private int armPosition;


     @Override
    public void init() {
        qwerty = hardwareMap.get(DcMotorEx.class, "fghj");
           armPIDcontroller = new PIDFController(armP,armI, armD, armF);
           qwerty.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
           qwerty.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           qwerty.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


    }

    @Override
    public void loop(){
        armPosition = qwerty.getCurrentPosition();
        armPIDcontroller.setPIDF(armP,armI, armD, armF);
        armPIDcontroller.setSetPoint(armSetPoint);
        qwerty.setPower(armPIDcontroller.calculate(armPosition,armSetPoint));
        telemetry.addData("arm position", armPosition);
        telemetry.addData("setPoint", armSetPoint);
        telemetry.update();
    }
}
