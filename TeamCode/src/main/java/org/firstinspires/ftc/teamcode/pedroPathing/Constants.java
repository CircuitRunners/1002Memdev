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
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
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

    /**
     * Which localizer the follower uses.
     *
     * true  = DRIVE ENCODERS. Position comes from the four drivetrain motor encoders.
     *         No dead wheels needed. Wheel slip shows up directly as position error, so it
     *         is noticeably less accurate -- fine for developing and testing paths, not
     *         what you want for a competition auto.
     * false = OCTOQUAD dead wheels. Use this the moment the pods are back on the robot.
     *
     * NOTE: this class is @Pinned, so Sloth cannot hot-reload a change to this file.
     *       Run the TeamCode configuration for a full install after editing it.
     */
    public static boolean useDriveEncoderLocalizer = true;

    public static Follower createFollower(HardwareMap hardwareMap) {
        FollowerBuilder builder = new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants);

        if (useDriveEncoderLocalizer) {
            builder = builder.driveEncoderLocalizer(driveEncoderConstants);
        } else {
            builder = builder.setLocalizer(new OctoQuadLocalizer(
                    hardwareMap, octoConstants, OctoQuadLocalizer.InitMode.INITIALIZE_OCTOQUAD));
        }

        return builder.build();
    }

    /**
     * Drive-encoder localizer setup, used when useDriveEncoderLocalizer is true.
     *
     * The three ticksToInches values start at 1 ON PURPOSE. Each Pedro tuner reports a
     * MULTIPLIER, and you paste that multiplier straight into the matching field here.
     *
     * Tune in this order, from the "Tuning" OpMode -> Localization folder:
     *   1. Localization Test - push the robot by hand, confirm X, Y and heading all move
     *                          in the right direction before tuning anything.
     *   2. Forward Tuner     - push forward 48 in       -> forwardTicksToInches
     *   3. Lateral Tuner     - push left 48 in          -> strafeTicksToInches
     *   4. Turn Tuner        - rotate 1 full turn CCW   -> turnTicksToInches
     */
    public static DriveEncoderConstants driveEncoderConstants = new DriveEncoderConstants()
            .leftFrontMotorName("fl")
            .rightFrontMotorName("fr")
            .leftRearMotorName("bl")
            .rightRearMotorName("br")

            .forwardTicksToInches(1)
            .strafeTicksToInches(1)
            .turnTicksToInches(1)

            // Only the SUM of these matters -- the Turn Tuner fits itself around whatever
            // you set -- but put the real numbers in so heading stays sane if you retune.
            .robotWidth(12)   // TODO: measure your robot, in inches
            .robotLength(12)  // TODO: measure your robot, in inches

            // These are INDEPENDENT of the drivetrain motor directions set above.
            // If Localization Test shows an axis counting backwards, flip that one here.
            .leftFrontEncoderDirection(Encoder.REVERSE)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.REVERSE)
            .rightRearEncoderDirection(Encoder.FORWARD);



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
//            // strafePodX = -3.6485 in -> -92.6719 mm
//            // forwardPodY = -0.2458 in -> -6.2433 mm
            .tcpOffsetXMM(79.375f)
            .tcpOffsetYMM(-31.75f)


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
