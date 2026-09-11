package org.firstinspires.ftc.teamcode.Config.pedroPathing;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.OctoQuadLocalizer;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.Config.pedroPathing.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.procedures.OctoQuadTuner;
import org.firstinspires.ftc.teamcode.Config.pedroPathing.procedures.Tests;

public class Tuning {
    @Tuner
    public Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    @Tuner
    public Procedure octoquadTuner() {
        return new OctoQuadTuner();
    }

    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner((hardwareMap) -> new OctoQuadLocalizer(hardwareMap, Constants.localizerConfig), (hardwareMap) -> new Mecanum(hardwareMap, Constants.drivetrainConfig));
    }

    @Tuner
    public static Procedure tests() {
        return new Tests(hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig), (hardwareMap -> new OctoQuadLocalizer(hardwareMap, Constants.localizerConfig)), () -> new Foresight(Constants.foresightConfig));
    }
}