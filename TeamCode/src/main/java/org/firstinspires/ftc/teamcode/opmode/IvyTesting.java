package org.firstinspires.ftc.teamcode.opmode;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.race;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Arm;
import org.firstinspires.ftc.teamcode.Claw;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.Poses;

@Autonomous
public class IvyTesting extends OpMode {

    private Follower follower;
    private Arm arm;
    private Claw claw;

    private final Path line1 = line(Poses.startLine1, Poses.endLine1)
            .linear(Poses.startLine1, Poses.endLine1);

    private final Path curve1 = curve(Poses.endLine1, Poses.curve1ControlPoint, Poses.curve1)
            .tangent();

    private final Path line2 = line(Poses.curve1, Poses.startLine1)
            .facingPoint(Poses.endLine1);

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(Poses.startLine1);
        follower.update();

        arm = new Arm();
        arm.init(hardwareMap);
        arm.setTarget(Arm.HIGH);

        claw = new Claw();
        claw.init(hardwareMap);
        claw.close();
    }

    @Override
    public void start() {
        schedule(autoRoutine1());
    }

    @Override
    public void loop() {
        follower.update();
        arm.update();
        Scheduler.execute();

        telemetry.addData("Pose", follower.pose());
        telemetry.addData("Arm", arm.getPosition());
    }

    private Command autoRoutine1() {

        return sequential(
                instant(() -> arm.setTarget(Arm.MIDH)),
                deadline(
                        follow(follower, line1),
                        sequential(
                                waitUntil(() -> follower.parametricCompletion() >= 0.5),
                                instant(() -> {
                                    arm.setTarget(Arm.DOWN);
                                    claw.open();
                                })
                        )
                ),

                instant(() -> arm.setTarget(Arm.HIGH)),
                follow(follower, curve1),
                race(waitUntil(arm::atTarget), waitMs(5000)),

                instant(() -> arm.setTarget(Arm.DOWN)),
                race(waitUntil(arm::atTarget), waitMs(5000)),
                follow(follower, line2),
                instant(() -> claw.close())
        );
    }

    private Command autoRoutine2() {

        return sequential(
                follow(follower, line1),
                follow(follower, curve1),
                follow(follower, line2)
        );
    }
}
