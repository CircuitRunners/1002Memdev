package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Routine Auto")
public class RoutineAuto extends AutoBase {

    @Override
    protected Pose startPose() {
        return Poses.startLine1;
    }

    @Override
    protected boolean mirrored() {return false;}

    @Override
    protected Routine routine() {
        return Routine.from(Poses.startLine1)
                .to(Poses.endLine1)
                    .constantHeading()
                    .pause(0.5)
                .curveTo(Poses.curve, Poses.curveControlPoint)
                    .tangentHeading()
                    .pause(0.5)
                .to(Poses.startLine1)
                    .interpolatedHeading()
                    .pause(0.5)
                .build();
    }
}
