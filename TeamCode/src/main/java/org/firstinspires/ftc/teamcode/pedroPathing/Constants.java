package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.OctoQuadConstants;
import com.pedropathing.ftc.localization.localizers.OctoQuadLocalizer;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(4.54);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .setLocalizer(new OctoQuadLocalizer(hardwareMap, octoConstants, OctoQuadLocalizer.InitMode.INITIALIZE_OCTOQUAD))
                .mecanumDrivetrain(driveConstants)
                .build();
    }

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("fr")
            .rightRearMotorName("br")
            .leftRearMotorName("bl")
            .leftFrontMotorName("fl")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .useBrakeModeInTeleOp(false)
            ;
    public static OctoQuadConstants octoConstants = new OctoQuadConstants()

            .name("octoquad") // change to your Robot Config name
            .deadwheelPortX(0)
            .deadwheelPortY(7)


//            // OctoQuad uses mm for offsets:
//            // strafePodX = -3.6485 in -> -92.6719 mm
//            // forwardPodY = -0.2458 in -> -6.2433 mm
            .tcpOffsetXMM(95.25f)
            .tcpOffsetYMM(-47.625f)


//            // Match your Pinpoint directions:
            .deadwheelXDir(OctoQuad.EncoderDirection.REVERSE) // strafe
            .deadwheelYDir(OctoQuad.EncoderDirection.FORWARD) // forward


//            // TODO: replace with your tuned/calculated values
            .deadwheelXTicksPerMM(19.89436789f)
            .deadwheelYTicksPerMM(19.89436789f)


//            .IMU_SCALAR(1.0f)
            .imuScalar(1.0135f)
            .velocityIntervalMs(25)
            .i2cRecoveryMode(OctoQuad.I2cRecoveryMode.MODE_1_PERIPH_RST_ON_FRAME_ERR)

            ;
}
