package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.teamcode.pedroPathing.Poses;

import java.util.ArrayList;
import java.util.List;

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

    static class Leg {
        final List<Segment> segments = new ArrayList<>();
        Heading heading = Heading.LINEAR;
        boolean holdEnd = true;
        double pause = 0;
        Runnable onStart;
        Runnable onArrival;
        final List<Double> triggerAt = new ArrayList<>();
        final List<Runnable> triggers = new ArrayList<>();
        boolean[] fired;
        PathChain path;

        void reset() {
            fired = new boolean[triggers.size()];
        }

        void fire(double t) {
            for (int i = 0; i < triggers.size(); i++) {
                if (!fired[i] && t >= triggerAt.get(i)) {
                    fired[i] = true;
                    triggers.get(i).run();
                }
            }
        }
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

    void materialize(Follower follower, boolean mirrored) {
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
                cursor = end;
            }
            leg.path = pb.build();
        }
    }

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
            return this;
        }

        public Builder constantHeading() {
            last().heading = Heading.CONSTANT;
            return this;
        }

        public Builder tangentHeading() {
            last().heading = Heading.TANGENT;
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

        public Builder at(double t, Runnable r) {
            last().triggerAt.add(t);
            last().triggers.add(r);
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
