package org.firstinspires.ftc.teamcode.opmode;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Arm;
import org.firstinspires.ftc.teamcode.Claw;
import org.firstinspires.ftc.teamcode.Config.pedroPathingv2.AutoBase;
import org.firstinspires.ftc.teamcode.Config.pedroPathingv2.Poses;
import org.firstinspires.ftc.teamcode.Config.pedroPathingv2.Routine;

@TeleOp(name = "SubsystemRoutineAuto")
public class SubsystemRoutineAuto extends AutoBase {

    private Arm arm;
    private Claw claw;
    @Override
    protected void onInit() {
        arm = new Arm();
        arm.init(hardwareMap);
        arm.setTarget(Arm.HIGH);

        claw = new Claw();
        claw.init(hardwareMap);
        claw.close();
    }

    @Override
    protected void onLoop() {
        arm.update();
    }

    @Override
    protected Pose startPose() {
        return Poses.startLine1;
    }

    @Override
    protected Routine routine() {
        return Routine.from(startPose())
                .to(Poses.endLine1)
                    .onStart(() -> arm.setTarget(Arm.MIDD))
                    .atTVal(0.5, () -> {
                            arm.setTarget(Arm.DOWN);
                            claw.open();
                    })
                .curveTo(Poses.curve1, Poses.curve1ControlPoint)
                    .tangentHeading()
                    .onStart(() -> arm.setTarget(Arm.HIGH))
                    .waitUntil(arm::atTarget, 5)
                .to(Poses.startLine1)
                    .facingPoint(Poses.endLine1)
                    .onStart(() -> arm.setTarget(Arm.DOWN))
                    .onArrival(() -> claw.close())
                .build();
    }

}
