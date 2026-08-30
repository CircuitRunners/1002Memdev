package org.firstinspires.ftc.teamcode.pedroPathing;



import com.bylazar.configurables.annotations.Configurable;
import dev.frozenmilk.sinister.loading.Pinned;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
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

@Configurable
@Pinned
public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .headingPIDFCoefficients(new PIDFCoefficients(1.5, 0, 0.1, 0.01))
            .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(0.2,
                    0.0094287472645351,
                    0.002957665195206038))
            .centripetalScaling(0)
            .mass(4.54) ;//25 lbs no intake or climb;



    public static PathConstraints pathConstraints = new PathConstraints(0.97, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .setLocalizer(new OctoQuadLocalizer(
                        hardwareMap, octoConstants, OctoQuadLocalizer.InitMode.INITIALIZE_OCTOQUAD))
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
            .xVelocity(49.74409)
            .yVelocity(57.08661)
            .useBrakeModeInTeleOp(false)
            ;


    public static OctoQuadConstants octoConstants = new OctoQuadConstants()

            .name("octoquad") // change to your Robot Config name
            .deadwheelPortX(0) // strafe pod port
            .deadwheelPortY(7) // forward pod port


//            // OctoQuad uses mm for offsets:
//            // strafePodX = -5.27 in -> -133.86 mm
//            // forwardPodY = -2.05 in -> -52.07 mm
            .tcpOffsetXMM(-133.86f)
            .tcpOffsetYMM(-52.07f)


//            // Match your Pinpoint directions:
            .deadwheelXDir(OctoQuad.EncoderDirection.REVERSE) // forward
            .deadwheelYDir(OctoQuad.EncoderDirection.REVERSE) // strafe


//            // TODO: replace with your tuned/calculated values
            .deadwheelXTicksPerMM(19.89436789f)
            .deadwheelYTicksPerMM(19.89436789f)


//            .IMU_SCALAR(1.0f)
            .imuScalar(1.0323f)
            .velocityIntervalMs(25)
            .i2cRecoveryMode(OctoQuad.I2cRecoveryMode.MODE_1_PERIPH_RST_ON_FRAME_ERR)

            ;

}
