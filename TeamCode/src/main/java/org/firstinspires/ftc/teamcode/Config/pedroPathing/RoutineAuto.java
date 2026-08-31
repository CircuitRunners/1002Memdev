package org.firstinspires.ftc.teamcode.Config.pedroPathing;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Routine Auto")
public class RoutineAuto extends AutoBase {
    private enum RoutineType {
        QUARTER_CIRCLE,
        TWO_LINES,
        ZIG_ZAG,
        PIECEWISE_TEST
    }

    private RoutineType routineType = RoutineType.QUARTER_CIRCLE;
    private int routineIndex = 0;

    @Override
    protected Pose startPose() {
        return Poses.startLine1;
    }

    @Override
    protected boolean mirrored() {return false;}

    @Override
    protected void initLoop() {
        if (gamepad1.dpadUpWasPressed()) routineIndex++;
        if (gamepad1.dpadDownWasPressed()) routineIndex--;
        routineType = RoutineType.values()[Math.floorMod(routineIndex, RoutineType.values().length)];

        telemetry.addLine("=======================");
        telemetry.addLine("~~~ROUTINE SELECTION~~~");
        telemetry.addLine("=======================");
        telemetry.addLine("");
        telemetry.addLine("Press Dpad Up/Down to change routines");
        telemetry.addLine("");
        telemetry.addData("Current routine: ", routineType);
        telemetry.addLine("");
        telemetry.addLine("");
    }

    @Override
    protected Object selectionKey() {
        return routineType;
    }

    @Override
    protected Routine routine() {
        switch (routineType) {
            case QUARTER_CIRCLE:
                return Routine.from(startPose())
                        .to(Poses.endLine1)
                            .constantHeading()
                            .pause(0.5)
                        .curveTo(Poses.curve1, Poses.curve1ControlPoint)
                            .tangentHeading()
                            .pause(0.5)
                        .to(Poses.startLine1)
                            .interpolatedHeading()
                            .pause(0.5)
                        .build();

            case TWO_LINES:
                return Routine.from(startPose())
                        .to(Poses.endLine1)
                            .constantHeading()
                            .pause(0.5)
                        .to(Poses.startLine2)
                            .interpolatedHeading()
                            .pause(0.5)
                        .to(Poses.endLine2)
                            .constantHeading()
                            .pause(0.5)
                        .build();

            case ZIG_ZAG:
                return Routine.from(startPose())
                        .to(Poses.endLine1)
                            .constantHeading()
                            .pause(0.5)
                        .to(Poses.endLine2)
                            .tangentHeading()
                            .pause(0.5)
                        .to(Poses.startLine2)
                            .constantHeading()
                            .pause(0.5)
                        .build();

            case PIECEWISE_TEST:
                return Routine.from(startPose())
                        .to(Poses.startLine2)
                        .customInterpolation(HeadingInterpolator.piecewise(
                                new HeadingInterpolator.PiecewiseNode(0.0, 0.3, HeadingInterpolator.facingPoint(Poses.startLine1)),
                                new HeadingInterpolator.PiecewiseNode(0.3, 0.7, HeadingInterpolator.reversedLinear(Poses.startLine1.getHeading(), Math.toRadians(180))),
                                new HeadingInterpolator.PiecewiseNode(0.7, 1.0, HeadingInterpolator.tangent)
                        ))
                        .build();

            default:
                throw new IllegalStateException("New routine type has not been set");
        }
    }
}
