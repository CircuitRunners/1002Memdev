package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Poses;
import org.firstinspires.ftc.teamcode.pedroPathing.Routine;


public abstract class AutoBase extends OpMode {
    protected Follower follower;

    private Routine routine;
    private int index;
    private boolean arrived;
    private double arrivalTime;

    protected abstract Pose startPose();

    protected abstract Routine routine();

    protected void onInit() {}

    protected boolean mirrored() {
        return false;
    }

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        Pose start = mirrored() ? startPose().mirror() : startPose();
        follower.setStartingPose(start);

        onInit();

        routine = routine();
        routine.materialize(follower, mirrored());
    }

    @Override
    public void start() {
        index = 0;
        beginLeg();
    }

    @Override
    public void loop() {
        follower.update();

        if (index >= routine.size()) {
            telemetry.addData("state", "done");
            telemetry.update();
            requestOpModeStop();
            return;
        }

        Routine.Leg leg = routine.leg(index);
        leg.fire(follower.getCurrentTValue());

        if (!arrived && !follower.isBusy()) {
            arrived = true;
            arrivalTime = getRuntime();
            if (leg.onArrival != null) {
                leg.onArrival.run();
            }
        }

        if (arrived && getRuntime() - arrivalTime >= leg.pause) {
            index++;
            beginLeg();
        }

        telemetry.addData("leg", index + "/" + routine.size());
        telemetry.addData("t", follower.getCurrentTValue());
        telemetry.addData("pose", follower.getPose());
        telemetry.update();
    }

    private void beginLeg() {
        arrived = false;
        if (index < routine.size()) {
            Routine.Leg leg = routine.leg(index);
            leg.reset();
            if (leg.onStart != null ) {
                leg.onStart.run();
            }
            follower.followPath(leg.path, leg.holdEnd);
        }
    }
}
