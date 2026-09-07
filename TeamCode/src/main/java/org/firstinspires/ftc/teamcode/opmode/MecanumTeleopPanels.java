package org.firstinspires.ftc.teamcode.opmode;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Config.MecanumDrive;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.Constants;


//@Disabled
@TeleOp(name = "Mecanum -Robot Centric - PANELS")
public class MecanumTeleopPanels extends OpMode {
    private MecanumDrive drive;

    private double speedMultiply = 0.5;

    private Follower follower;

    private static final FieldManager panelsField =
            PanelsField.INSTANCE.getField();

    private TelemetryManager panelsTelemetry =
            PanelsTelemetry.INSTANCE.getTelemetry();
    private static final Style robotStyle =
            new Style("", "#3F51B5", 0.75);

    @Override
    public void init(){
        telemetry.addLine("Initializing...");
        telemetry.update();



        drive = new MecanumDrive();
        drive.init(hardwareMap);

        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
        follower = Constants.createFollower(hardwareMap);

        follower.setStartingPose(new Pose(72, 72, Math.toRadians(90)));

        follower.update();



        telemetry.addLine("Ready!");
        telemetry.update();
    }

    @Override
    public void loop() {


        Gamepad g1 = PanelsGamepad.INSTANCE
                .getFirstManager()
                .asCombinedFTCGamepad(gamepad1);


        follower.update();

        drawRobot(follower.getPose());

        panelsField.update();

        if (g1.square) {
            follower.setPose(new Pose(72, 72, Math.toRadians(90)));
        }

        if (g1.dpad_up) {
            speedMultiply = 0.25;
        } else if (g1.dpad_left) {
            speedMultiply = 0.5;
        } else if (g1.dpad_down) {
            speedMultiply = 0.75;
        } else if (g1.dpad_right)  {
            speedMultiply = 1.0;
        }

        double forward = -1*g1.left_stick_y * speedMultiply;
        double strafe  =  g1.left_stick_x * speedMultiply;
        double rotate  =  g1.right_stick_x * speedMultiply;

        /** Send inputs to drive class using method created in Mecanum Drive Class */
        drive.drive(forward, strafe, rotate);

        panelsTelemetry.addData(
                "FL power",
                drive.frontLeftMotor.getPower()
        );

        panelsTelemetry.addData(
                "FR power",
                drive.frontRightMotor.getPower()
        );

        panelsTelemetry.addData(
                "BL power",
                drive.backLeftMotor.getPower()
        );

        panelsTelemetry.addData(
                "BR power",
                drive.backRightMotor.getPower()
        );
        panelsTelemetry.update();
        telemetry.addData("fl power", drive.frontLeftMotor.getPower());
        telemetry.addData("fr Power", drive.frontRightMotor.getPower());
        telemetry.addData("rl Power", drive.backLeftMotor.getPower());
        telemetry.addData("rr Power", drive.backRightMotor.getPower());

        telemetry.addLine("sloth");



        telemetry.update();
    }


    private void drawRobot(Pose pose) {
        panelsField.setStyle(robotStyle);

        panelsField.moveCursor(
                pose.getX(),
                pose.getY()
        );

        panelsField.circle(9);

        double heading = pose.getHeading();

        double x2 = pose.getX() + Math.cos(heading) * 9;
        double y2 = pose.getY() + Math.sin(heading) * 9;

        panelsField.moveCursor(
                pose.getX(),
                pose.getY()
        );

        panelsField.line(x2, y2);
    }
}

