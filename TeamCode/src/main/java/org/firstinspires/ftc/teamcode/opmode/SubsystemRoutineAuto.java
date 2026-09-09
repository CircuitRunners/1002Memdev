package org.firstinspires.ftc.teamcode.opmode;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;

import org.firstinspires.ftc.teamcode.Arm;
import org.firstinspires.ftc.teamcode.Claw;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.AutoBase;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.Poses;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.Routine;

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
                    .onStart(() -> arm.setTarget(Arm.MID))
                    .atTVal(0.5, () -> {
                            arm.setTarget(Arm.DOWN);
                            claw.open();
                    })
                .curveTo(Poses.curve1, Poses.curve1ControlPoint)
                    .tangentHeading()
                    .onStart(() -> arm.setTarget(Arm.HIGH))
                    .waitUntil(arm::atTarget, 5)
                .to(Poses.startLine1)
                    .customInterpolation(HeadingInterpolator.facingPoint(Poses.endLine1))
                    .onStart(() -> arm.setTarget(Arm.DOWN))
                    .blockFromUntil(0, arm::atTarget, 5)
                    .onArrival(() -> claw.close())
                .build();
    }

}
