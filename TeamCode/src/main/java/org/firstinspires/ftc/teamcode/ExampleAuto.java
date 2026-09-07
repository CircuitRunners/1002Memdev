package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/**
 * Three-segment shakedown path: straight line, curve, straight line back to start.
 *
 * EVERY heading in Pedro is in RADIANS -- Pose headings, both heading interpolators, and
 * PathConstraints.headingConstraint. Passing a bare 90 means 90 radians (~5157 degrees),
 * which wraps to an essentially arbitrary angle. Always wrap in Math.toRadians().
 */
@Autonomous(name = "Example Auto", group = "Pedro Pathing")
public class ExampleAuto extends OpMode {

    /** Start pose. Field is 144 x 144 in, so (72, 72) is dead center. */
    private static final Pose START = new Pose(72, 72, Math.toRadians(90));

    /** Hard ceiling on the whole routine. Nothing here should take close to this long. */
    private static final double OPMODE_TIMEOUT_S = 20.0;

    /** Abort if the localizer reports the robot has not moved for this long mid-path. */
    private static final double STALL_WINDOW_S = 2.0;
    private static final double STALL_MIN_MOVE_IN = 0.5;
    private static final double STALL_MIN_TURN_RAD = 0.05;

    private Follower follower;
    private int pathState = 0;

    private final ElapsedTime opTimer = new ElapsedTime();
    private final ElapsedTime stallTimer = new ElapsedTime();
    private Pose lastPose;
    private boolean aborted = false;
    private String abortReason = "";

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(START);
        buildPaths();

        telemetry.addData("Localizer",
                Constants.useDriveEncoderLocalizer ? "DRIVE ENCODERS" : "OCTOQUAD dead wheels");
        telemetry.addLine("Push the robot by hand and watch the pose before pressing START.");
    }

    @Override
    public void init_loop() {
        // updatePose() reads the localizer WITHOUT touching the drivetrain. Do not call
        // follower.update() here -- it is the method that can command motor power.
        follower.updatePose();
        Pose p = follower.getPose();
        telemetry.addData("Localizer",
                Constants.useDriveEncoderLocalizer ? "DRIVE ENCODERS" : "OCTOQUAD dead wheels");
        telemetry.addData("x (in)", "%.2f", p.getX());
        telemetry.addData("y (in)", "%.2f", p.getY());
        telemetry.addData("heading (deg)", "%.1f", Math.toDegrees(p.getHeading()));
        telemetry.addData("localizer NaN", follower.isLocalizationNAN());
        telemetry.addLine("These numbers MUST change when you push the robot. If they don't,");
        telemetry.addLine("the localizer is dead and this OpMode will not run safely.");
    }

    @Override
    public void start() {
        opTimer.reset();
        stallTimer.reset();
        lastPose = follower.getPose();
    }

    @Override
    public void loop() {
        follower.update();

        if (aborted) {
            telemetry.addLine("ABORTED: " + abortReason);
            return;
        }
        if (safetyTripped()) {
            return;
        }

        autonomousPathUpdate();

        Pose p = follower.getPose();
        telemetry.addData("path state", pathState);
        telemetry.addData("busy", follower.isBusy());
        telemetry.addData("t (s)", "%.1f", opTimer.seconds());
        telemetry.addData("x (in)", "%.2f", p.getX());
        telemetry.addData("y (in)", "%.2f", p.getY());
        telemetry.addData("heading (deg)", "%.1f", Math.toDegrees(p.getHeading()));
    }

    /**
     * Two guards against the single worst failure mode: a localizer that reports a frozen
     * pose. The follower then believes it is never approaching the target, holds full
     * corrective power, and isBusy() never goes false -- so the robot drives into the wall
     * and stays there. Pedro's own stuck detection does not cover this, because it only
     * arms between t-values 0.1 and 0.8 and a frozen pose never leaves t ~ 0.
     *
     * @return true if the routine was aborted this cycle
     */
    private boolean safetyTripped() {
        if (opTimer.seconds() > OPMODE_TIMEOUT_S) {
            abort(String.format("overall timeout at %.0f s", OPMODE_TIMEOUT_S));
            return true;
        }

        Pose now = follower.getPose();
        double moved = Math.hypot(now.getX() - lastPose.getX(), now.getY() - lastPose.getY());
        double turned = Math.abs(wrap(now.getHeading() - lastPose.getHeading()));

        if (moved > STALL_MIN_MOVE_IN || turned > STALL_MIN_TURN_RAD) {
            lastPose = now;
            stallTimer.reset();
        } else if (follower.isBusy() && stallTimer.seconds() > STALL_WINDOW_S) {
            abort(String.format("localizer reported no motion for %.0f s - check odometry",
                    STALL_WINDOW_S));
            return true;
        }
        return false;
    }

    private void abort(String reason) {
        aborted = true;
        abortReason = reason;
        // Same stop pattern Tuning.stopRobot() uses: hand control back to teleop drive
        // and command zero, so follower.update() keeps holding the motors at 0.
        follower.startTeleopDrive(true);
        follower.setTeleOpDrive(0, 0, 0, true);
        telemetry.addLine("ABORTED: " + reason);
        requestOpModeStop();
    }

    private static double wrap(double radians) {
        while (radians > Math.PI) radians -= 2 * Math.PI;
        while (radians < -Math.PI) radians += 2 * Math.PI;
        return radians;
    }

    private PathChain line1, curve1, line2;

    private void buildPaths() {
        line1 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(72, 72, Math.toRadians(90)),
                        new Pose(72, 108, Math.toRadians(90))))
                .setConstantHeadingInterpolation(Math.toRadians(90))
                .build();

        curve1 = follower.pathBuilder()
                .addPath(new BezierCurve(
                        new Pose(72, 108, Math.toRadians(90)),
                        new Pose(108, 90),
                        new Pose(108, 72, Math.toRadians(180))))
                .setTangentHeadingInterpolation()
                .build();

        line2 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(108, 72, Math.toRadians(180)),
                        new Pose(72, 72, Math.toRadians(90))))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(90))
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
