package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous
public class ExampleAuto extends OpMode {

    private Follower follower;
    private int pathState = 0;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(72, 72, 90));
        buildPaths();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();
        telemetry.addData("Current Pose: ", follower.getPose());
    }

    private PathChain line1, curve1, line2;

    private void buildPaths() {
        line1 = follower.pathBuilder()
                .addPath(new BezierLine(new Pose(72, 72, Math.toRadians(90)), new Pose(72, 108, Math.toRadians(90))))
                .setConstantHeadingInterpolation(90)
                .build();

        curve1 = follower.pathBuilder()
                .addPath(new BezierCurve(new Pose(72, 108, 90), new Pose(108, 90), new Pose(108, 72, 180)))
                .setTangentHeadingInterpolation()
                .build();

        line2 = follower.pathBuilder()
                .addPath(new BezierLine(new Pose(108, 72, 180), new Pose(72, 72, 90)))
                .setLinearHeadingInterpolation(180, 90)
                .build();
    }

    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(line1);
                pathState++;
                break;

            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(curve1);
                    pathState++;
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(line2);
                    pathState++;
                }
                break;

            default:
                if (!follower.isBusy()) requestOpModeStop();
        }
    }

}
