package org.firstinspires.ftc.teamcode.pedroPathing;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
@Configurable
public class Poses {
    public static final Pose startLine1 = new Pose(72, 72, Math.toRadians(90));
    public static final Pose endLine1 = new Pose(72, 108, Math.toRadians(90));
    public static final Pose curve1 = new Pose(108, 72, Math.toRadians(180));
    public static final Pose curve1ControlPoint = new Pose(108, 108);
    public static final Pose startLine2 = new Pose(108, 108, Math.toRadians(-90));
    public static final Pose endLine2 = new Pose(108, 72, Math.toRadians(-90));
}
