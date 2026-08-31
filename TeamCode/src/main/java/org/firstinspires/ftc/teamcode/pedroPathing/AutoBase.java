package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Objects;


public abstract class AutoBase extends OpMode {
    protected Follower follower;

    private Routine routine;
    private int index;
    private boolean arrived;
    private double arrivalTime;
    private Object lastKey;

    protected abstract Pose startPose();

    protected abstract Routine routine();

    protected void onInit() {}

    protected boolean mirrored() {
        return false;
    }

    protected void initLoop() {}

    protected Object selectionKey() {
        return null;
    }

    @Override
    public void init() {
        telemetry.addLine("Initializing...");
        follower = Constants.createFollower(hardwareMap);
        onInit();
        rebuild();
        telemetry.addLine("Done!");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        initLoop();
        if (!Objects.equals(lastKey, selectionKey())) {
            telemetry.clearAll();
            telemetry.addLine("Building...");
            rebuild();
            telemetry.addLine("Done!");
        }
    }

    private void rebuild() {
        lastKey = selectionKey();
        Pose start = mirrored() ? startPose().mirror() : startPose();
        follower.setStartingPose(start);
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

        if (!arrived && !follower.isBusy()) {
            arrived = true;
            arrivalTime = getRuntime();
            if (leg.onArrival != null) {
                leg.onArrival.run();
            }
        }

        if (arrived && (getRuntime() - arrivalTime >= leg.pause) && ready(leg)) {
            index++;
            beginLeg();
        }

        telemetry.addData("leg", index + "/" + routine.size());
        telemetry.addData("held", arrived && !ready(leg));
        telemetry.addData("t", follower.getCurrentTValue());
        telemetry.addData("pose", follower.getPose());
        telemetry.update();
    }
    private boolean ready(Routine.Leg leg) {
        if (leg.waitUntil == null) return true;
        if (getRuntime() - arrivalTime >= leg.waitTimeout) return true;
        return leg.waitUntil.getAsBoolean();
    }

    private void beginLeg() {
        arrived = false;
        if (index < routine.size()) {
            Routine.Leg leg = routine.leg(index);
            if (leg.onStart != null) {
                leg.onStart.run();
            }
            follower.followPath(leg.path, leg.holdEnd);
        }
    }
}
