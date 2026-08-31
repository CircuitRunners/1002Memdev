package org.firstinspires.ftc.teamcode.Config.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.HeadingInterpolator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Routine: an organizer class for making cleaner autonomous classes.
 * No need for building pre-constructed paths.
 * Everything is extended from AutoBase.java, which contains the
 * actual loop and initialization.
 */
public class Routine {
    enum Heading { LINEAR, CONSTANT, TANGENT }

    static class Segment {
        final Pose end;
        final Pose control;

        Segment(Pose end, Pose control) {
            this.end = end;
            this.control = control;
        }
    }

    /**
     * One Leg indicates a single "phase" of the auto.
     * It can have multiple Segments that the robot follows (not recommended
     * when adding modifiers).
     * It should resemble a single PathChain and its properties.
     * Each Routine object has a starting point and
     * a list of Legs.
     */
    static class Leg {
        final List<Segment> segments = new ArrayList<>();
        Heading heading = Heading.LINEAR;
        //allow supplying a custom/piecewise HeadingInterpolator
        HeadingInterpolator headingInterpolator = null;
        // allow reversing the per-path heading interpolation (ie reversing tangent heading)
        boolean reverseHeading = false;

        boolean holdEnd = true;
        double pause = 0;
        Runnable onStart;
        Runnable onArrival;
        BooleanSupplier waitUntil;
        double waitTimeout = Double.POSITIVE_INFINITY;
        final Map<Double, Runnable> parametricTriggers = new LinkedHashMap<>();
        final Map<Double, Runnable> temporalTriggers = new LinkedHashMap<>();
        final Map<Pose, Runnable> poseTriggers = new LinkedHashMap<>();
        PathChain path;
    }

    private final Pose start;
    private final List<Leg> legs;

    private Routine(Pose start, List<Leg> legs) {
        this.start = start;
        this.legs = legs;
    }

    int size() {
        return legs.size();
    }

    Leg leg(int i) {
        return legs.get(i);
    }

    /**
     * The substitute for buildPaths();
     * instead reads the paths from each leg to build.
     * @param follower
     * @param mirrored
     */
    void materializePaths(Follower follower, boolean mirrored) {
        Pose cursor = mirrored ? start.mirror() : start;
        for (Leg leg : legs) {
            PathBuilder pb = follower.pathBuilder();
            for (Segment s : leg.segments) {
                Pose end = mirrored ? s.end.mirror() : s.end;
                Pose ctrl = s.control == null
                        ? null
                        : mirrored ? s.control.mirror() : s.control;

                pb.addPath(ctrl == null
                        ? new BezierLine(cursor, end)
                        : new BezierCurve(cursor, ctrl, end));

                // prefer an explicit custom/piecewise interpolator if provided
                if (leg.headingInterpolator != null) {
                    pb.setHeadingInterpolation(leg.headingInterpolator);
                } else {
                    switch (leg.heading) {
                        case LINEAR:
                            pb.setLinearHeadingInterpolation(cursor.getHeading(), end.getHeading());
                            break;
                        case CONSTANT:
                            pb.setConstantHeadingInterpolation(end.getHeading());
                            break;
                        case TANGENT:
                            pb.setTangentHeadingInterpolation();
                            break;
                    }
                }

                // if the leg requests reversing the per-path interpolation, apply it here
                if (leg.reverseHeading) {
                    pb.setReversed();
                }

                cursor = end;
            }

            leg.parametricTriggers.forEach(pb::addParametricCallback);
            leg.temporalTriggers.forEach(pb::addTemporalCallback);
            leg.poseTriggers.forEach((p, r) -> {
                pb.addPoseCallback(mirrored ? p.mirror() : p, r, 0.5);
            });

            leg.path = pb.build();
        }
    }

    /**
     * This is what creates the actual Routines.
     * It should function as a PathBuilder.
     * @param start
     * @return
     */
    public static Builder from(Pose start) {
        return new Builder(start);
    }

    public static class Builder {
        private final Pose start;
        private final List<Leg> legs = new ArrayList<>();
        private final List<Pose> pending = new ArrayList<>();

        private Builder(Pose start) {
            this.start = start;
        }

        public Builder through(Pose via) {
            pending.add(via);
            return this;
        }

        public Builder to(Pose end) {
            Leg leg = new Leg();
            for (Pose p : pending) {
                leg.segments.add(new Segment(p, null));
            }
            leg.segments.add(new Segment(end, null));
            pending.clear();
            legs.add(leg);
            return this;
        }

        public Builder curveTo(Pose end, Pose control) {
            Leg leg = new Leg();
            leg.segments.add(new Segment(end, control));
            legs.add(leg);
            return this;
        }

        public Builder interpolatedHeading() {
            last().heading = Heading.LINEAR;
            last().headingInterpolator = null;
            return this;
        }

        public Builder constantHeading() {
            last().heading = Heading.CONSTANT;
            last().headingInterpolator = null;
            return this;
        }

        public Builder tangentHeading() {
            last().heading = Heading.TANGENT;
            last().headingInterpolator = null;
            return this;
        }

        public Builder noHold() {
            last().holdEnd = false;
            return this;
        }

        public Builder pause(double seconds) {
            last().pause = seconds;
            return this;
        }

        public Builder onStart(Runnable r) {
            last().onStart = r;
            return this;
        }
        public Builder onArrival(Runnable r) {
            last().onArrival = r;
            return this;
        }

        public Builder waitUntil(BooleanSupplier condition) {
            last().waitUntil = condition;
            return this;
        }

        public Builder waitUntil(BooleanSupplier condition, double timeoutSeconds) {
            last().waitUntil = condition;
            last().waitTimeout = timeoutSeconds;
            return this;
        }

        /**
         * Set a custom or piecewise HeadingInterpolator for the last leg.
         * Example: customInterpolation(HeadingInterpolator.piecewise(...))
         */
        public Builder customInterpolation(HeadingInterpolator interpolator) {
            last().headingInterpolator = interpolator;
            return this;
        }

        /**
         * Mark the last leg's per-path heading interpolation as reversed.
         * Useful to reverse tangent heading (drive backwards while heading follows tangent +/- 180°).
         */
        public Builder reverseHeading() {
            last().reverseHeading = true;
            return this;
        }

        public Builder atTVal(double t, Runnable r) {
            if (last().parametricTriggers.containsKey(t)) {
                throw new IllegalStateException("Cannot run multiple Runnables for a singular t-value");
            }
            last().parametricTriggers.put(t, r);
            return this;
        }

        public Builder atTime(double ms, Runnable r) {
            if (last().temporalTriggers.containsKey(ms*1000)) {
                throw new IllegalStateException("Cannot run multiple Runnables for a singular time index");
            }
            last().temporalTriggers.put(ms*1000, r);
            return this;
        }

        public Builder atPose(Pose pose, Runnable r) {
            if (last().poseTriggers.containsKey(pose)) {
                throw new IllegalStateException("Cannot run multiple Runnables for a singular pose");
            }
            last().poseTriggers.put(pose, r);
            return this;
        }

        private Leg last() {
            if (legs.isEmpty()) {
                throw new IllegalStateException("modifier called before the first to()/curveTo()");
            }
            return legs.get(legs.size() - 1);
        }

        public Routine build() {
            if (!pending.isEmpty()) {
                throw new IllegalStateException("through() not followed by to()");
            }
            return new Routine(start, legs);
        }
    }
}