# FTC Two-Day Bootcamp — Java, the REV Control System, Mecanum, TeleOp and Pedro Pathing

**For:** Siddhant · **Starting point:** some R and Python, no Java · **Pace:** ~10 focused hours per day
**Hardware assumption:** no robot and no field. Everything below is doable with a laptop alone.
**Repo:** `~/Documents/GitHub/1002Memdev` — FTC SDK 11.1.0, Pedro Pathing 2.1.2, Panels, Sloth hot-reload.
**Season context:** the 2026–2027 game is **BIOBUZZ presented by RTX**, kickoff **Sat Sep 12, 2026, 12:00 pm ET**. These two days are the week before kickoff — the goal is that on kickoff day the software is not the thing slowing the team down.

---

## How to use this plan

Three rules that decide whether these two days work.

1. **Type every snippet. Never paste.** Reading Java produces the feeling of understanding without the substance. Typing produces compiler errors, and compiler errors are the curriculum.
2. **The build is the feedback loop.** With no robot, `./gradlew :TeamCode:assembleDebug` is the grader. If it compiles, the syntax is right. If it does not, the error message is a free tutor. Run it after every block.
3. **Everything new goes in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/learning/`.** A scratch package keeps the competition code clean, and `@Disabled` on every practice OpMode keeps the Driver Hub list short later.

Timings are targets, not obligations. Falling behind on Day 1 Block 3 matters; falling behind on Day 1 Block 1 means Block 1 was worth the time.

### The no-hardware toolkit

| Tool | What it replaces | How |
|---|---|---|
| `./gradlew :TeamCode:assembleDebug` | Deploying to the Control Hub | Terminal in the repo root. Compiles the whole app; ~1–3 min cold, seconds warm. |
| Plain-Java `main()` classes | Watching the robot move | Mecanum math and pose math are pure arithmetic. Print the numbers and check them by hand. |
| [Pedro Pathing visualizer](https://pedropathing.com/visualizer) | A field | Draw paths on a real field image, read out `Pose` coordinates, paste them into code. |
| [Pedro docs](https://pedropathing.com/docs) + [FTC SDK javadoc](https://javadoc.io/doc/org.firstinspires.ftc) | Asking a mentor | The javadoc is the source of truth for every SDK class name used below. |
| `Tuning.java` in the repo | Tuning on a field | Read it as a document. It describes, step by step, what will happen the first hour the robot is available. |

---

# DAY 1 — From Java syntax to a robot you could drive

The through-line: by the end of the day, be able to write, from a blank file, a field-centric mecanum TeleOp that compiles, and explain every line of it.

## Block 1 — Orientation (45 min)

**Goal:** know what every top-level folder is for, and how code physically gets onto a robot.

Walk the repo and answer these out loud:

- `FtcRobotController/` — FIRST's code. **Never edited.** It contains the Android app, the OpMode registration machinery, the web interface at `192.168.43.1:8080`.
- `TeamCode/` — the only module a team writes in. Everything is under `src/main/java/org/firstinspires/ftc/teamcode/`.
- `build.dependencies.gradle` — the shopping list. Note `org.firstinspires.ftc:*:11.1.0` (SDK version) and `com.pedropathing:ftc:2.1.2`. When someone online says "that method was added in 10.2", this file is how to check.
- `libs/`, `gradle/`, `gradlew` — the build system. Gradle downloads dependencies, compiles Java, packages an `.apk`, and (when a robot is attached) installs it.
- `.run/TeamCode.run.xml` — the Android Studio run configuration that does a full install.

**The deploy chain, once, so it is never mysterious:**

```
Android Studio (laptop)
   → Gradle compiles TeamCode + FtcRobotController into an .apk
   → USB or Wi-Fi installs that .apk onto the REV Control Hub
        (the Control Hub IS an Android device — an app is literally running on it)
   → Control Hub broadcasts a Wi-Fi Direct network
   → REV Driver Hub joins it and shows the OpMode list
   → Driver Hub sends INIT / START / STOP and gamepad state; Control Hub sends telemetry back
```

**Do now:**

```bash
cd ~/Documents/GitHub/1002Memdev
./gradlew :TeamCode:assembleDebug
```

Let it finish. `BUILD SUCCESSFUL` is the baseline everything else is measured against. (Android SDK is already at `~/Library/Android/sdk`, so this works with no robot connected.)

Then make the scratch package:

```bash
mkdir -p TeamCode/src/main/java/org/firstinspires/ftc/teamcode/learning
```

---

## Block 2 — Java for someone who knows Python (2 hr 15 min)

**Goal:** stop translating Python in your head. Java is not harder, it is stricter and louder about it.

### The five differences that cause 90% of early errors

1. **Static typing.** Every variable declares its type and cannot change it. `double power = 0.5;` not `power = 0.5`.
2. **Braces and semicolons, not indentation.** Indentation is for humans only. A missing `;` is the single most common first-week error.
3. **Everything lives inside a class.** There is no top-level script. A file named `Foo.java` must contain `public class Foo`.
4. **`int` division truncates.** `1 / 2` is `0`. `1.0 / 2` is `0.5`. This will silently ruin one piece of robot math; expect it.
5. **`==` on objects compares identity, not value.** For `String`, use `.equals()`. For `double`, never test exact equality — compare against a tolerance.

### Cheat sheet, Python → Java

| Python | Java |
|---|---|
| `x = 5` | `int x = 5;` |
| `x = 5.0` | `double x = 5.0;` |
| `s = "hi"` | `String s = "hi";` |
| `flag = True` | `boolean flag = true;` |
| `xs = [1,2,3]` | `int[] xs = {1, 2, 3};` |
| `for x in xs:` | `for (int x : xs) { }` |
| `for i in range(4):` | `for (int i = 0; i < 4; i++) { }` |
| `while cond:` | `while (cond) { }` |
| `def f(a, b): return a+b` | `public double f(double a, double b) { return a + b; }` |
| `if/elif/else` | `if () { } else if () { } else { }` |
| `abs, min, max` | `Math.abs`, `Math.min`, `Math.max` |
| `None` | `null` |
| `# comment` | `// comment` or `/* ... */` |
| f-string | `String.format("%.2f", x)` |

### Type it (30 min)

Create `learning/JavaBasics.java`. This is a plain Java class, not an OpMode — it exists to be read and reasoned about.

```java
package org.firstinspires.ftc.teamcode.learning;

public class JavaBasics {

    // A field: belongs to the object, visible to every method in the class.
    private double speedMultiplier = 0.5;

    // A method: return type, name, typed parameters.
    public double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    // Overloading: same name, different parameter list. Java picks by argument types.
    public double clamp(double value) {
        return clamp(value, -1.0, 1.0);
    }

    // static = belongs to the class, not to any object. Called as JavaBasics.square(3).
    public static double square(double x) {
        return x * x;
    }

    // Arrays and loops.
    public double largestMagnitude(double[] values) {
        double best = 0.0;
        for (double v : values) {
            best = Math.max(best, Math.abs(v));
        }
        return best;
    }

    // The integer-division trap, made explicit.
    public static void divisionDemo() {
        int    wrong = 1 / 2;        // 0
        double right = 1.0 / 2.0;    // 0.5
        System.out.println(wrong + " vs " + right);
    }
}
```

**Prove each claim.** Add a `public static void main(String[] args)` to the class, call the methods, and run it from Android Studio (right-click → Run). Predict every printed value *before* running. Wrong predictions are the point.

### Classes and objects (45 min)

The single idea that makes FTC code readable: **a class is a blueprint, an object is one built thing.** `MecanumDrive` is a blueprint describing "a thing with four motors that can be told to drive"; `drive = new MecanumDrive();` builds one.

Open `MecanumDrive.java` and name the parts:

```java
public class MecanumDrive {
    public DcMotorEx frontLeftMotor;      // field  — state the object remembers
    private DcMotorEx[] motors;           // private — only this class may touch it

    public void init(HardwareMap hardwareMap) { ... }   // method — behavior
    public void setPowers(double fl, double fr, double bl, double br) { ... }
    public void drive(double forward, double right, double rotate) { ... }
}
```

- `public` — anyone can use it. `private` — only this class. Default to `private` and open it up only when needed.
- The four motor fields here are `public`. That is a deliberate shortcut so `MecanumDriveTeleOp` can print `drive.frontLeftMotor.getPower()`. The cleaner design is a `getFrontLeftPower()` method — worth doing later, and worth understanding *why* now: public fields let any code anywhere set a motor power, so when the robot misbehaves there is no single place to look.

**Exercise:** write `learning/Counter.java` with a private `int count`, methods `increment()`, `reset()`, `get()`. Then in `main`, create two `Counter` objects and confirm incrementing one does not change the other. This is the whole of object identity in ten lines.

### Inheritance, interfaces, `@Override` (30 min)

FTC uses inheritance in exactly one place, and it matters:

```java
public class MecanumDriveTeleOp extends OpMode {
    @Override public void init() { ... }
    @Override public void loop() { ... }
}
```

- `extends OpMode` — this class **is an** `OpMode`. It inherits `telemetry`, `gamepad1`, `gamepad2`, `hardwareMap` without declaring them. That is why those names appear from nowhere.
- `OpMode` is **abstract**: it declares `init()` and `loop()` without bodies and refuses to be instantiated. Subclasses must fill them in.
- `@Override` is an annotation that tells the compiler "this replaces a parent method." It is optional but always worth writing: misspell `loop()` as `Loop()` without it and the code compiles, does nothing, and the robot sits still with no error.
- **Annotations in general** (`@TeleOp`, `@Autonomous`, `@Disabled`, `@Configurable`) are metadata read at runtime. `@TeleOp(name = "Mecanum -Robot Centric")` is what puts that exact string on the Driver Hub list. Nothing else registers an OpMode — no list to add to, no file to edit.

**Exercise:** in `learning/`, write an abstract class `Shape` with `public abstract double area()`, then `Circle` and `Rect` extending it. Put three shapes in a `Shape[]` and total the areas in a loop. Polymorphism, in twenty lines, is the same mechanism that lets the SDK run any OpMode it finds.

### Radians, degrees, and `Math` (15 min)

Robot code lives in radians, humans live in degrees, and the conversion is the most expensive bug in FTC autonomous.

```java
Math.toRadians(90)   // 1.5707963...
Math.toDegrees(Math.PI)  // 180.0
Math.atan2(y, x)     // angle of a vector, -PI..PI — the correct tool, never atan(y/x)
Math.hypot(dx, dy)   // sqrt(dx*dx + dy*dy), no overflow
Math.sin, Math.cos   // ALWAYS take radians
```

Read the comment at the top of `ExampleAuto.java`: passing a bare `90` where radians are expected means 90 radians ≈ 5157°, which wraps to an arbitrary heading. Every heading in Pedro is radians. Every single one.

**Checkpoint:** `./gradlew :TeamCode:assembleDebug` → `BUILD SUCCESSFUL`.

---

## Block 3 — The REV control system, end to end (1 hr 30 min)

**Goal:** be able to draw the robot's electrical and software topology on a whiteboard from memory.

### The two hubs

**REV Control Hub** — the robot's brain. An Android device with no screen, running the Robot Controller app. It has:

- 4 motor ports (0–3), each with an **encoder** input on the same connector
- 6 servo ports
- 4 analog, 8 digital, 4 I2C buses
- a built-in **IMU** (inertial measurement unit — gives heading from gyro + accelerometer)
- 12V XT30 power in from the battery, through the main switch
- a USB-C port for laptop deployment, and RS485 to daisy-chain an **Expansion Hub** when 4 motors is not enough

**REV Driver Hub** — the driver station. An Android tablet running the Driver Station app, with USB ports for gamepads. It never runs team code. It only sends inputs and displays telemetry.

They talk over **Wi-Fi Direct**: the Control Hub is the access point, the Driver Hub connects to it. That is why the robot's Wi-Fi network name is the team number and why laptop deployment over Wi-Fi means joining that same network.

### The configuration file — the part beginners always skip

Names in code are strings that must match a config the Driver Hub stores:

```java
frontLeftMotor = hardwareMap.get(DcMotorEx.class, "fl");
```

`"fl"` is not magic. On the Driver Hub: **⋮ → Configure Robot → Control Hub → Motors → Port 0 → REV Robotics HD Hex Motor → name it `fl`**. If the config says `frontLeft` and the code says `fl`, the OpMode throws immediately on INIT with a message naming the missing device. That error is the single most common "the code is broken" report, and it is never the code.

The repo's config must therefore contain, at minimum: motors `fl`, `fr`, `bl`, `br` (see `MecanumDrive.init`), and an I2C device named `octoquad` (see `Constants.octoConstants.name("octoquad")`).

### Motors, encoders, servos

- **DcMotor / DcMotorEx** — `DcMotorEx` is the richer interface: velocity control, current draw, PIDF on the hub. `setPower(-1.0 .. 1.0)` is duty cycle, roughly "fraction of battery voltage", not speed.
- **Direction** — `setDirection(REVERSE)` flips a motor in software. On a mecanum chassis the left motors are physically mirrored, which is why `MecanumDrive.init` reverses `fl` and `bl`. Same reason `Constants.driveConstants` reverses the same two.
- **ZeroPowerBehavior** — `BRAKE` shorts the motor terminals so it resists motion at zero power; `FLOAT` lets it coast. `MecanumDrive` sets `BRAKE`. Note `Constants` sets `.useBrakeModeInTeleOp(false)` for Pedro's teleop drive — a real, findable inconsistency worth asking the team about.
- **Encoders** — a sensor on the motor shaft counting **ticks** per revolution. A goBILDA 5202 312 RPM motor is 537.7 ticks/rev at the output shaft. Ticks are the raw material of all odometry.
- **Servos** — `setPosition(0.0 .. 1.0)` maps to an angle range. Continuous-rotation servos (`CRServo`) take `setPower` instead.
- **I2C** — a two-wire bus for sensors. The **OctoQuad** (an encoder-reading coprocessor) sits on I2C and reads the odometry pods so the Control Hub does not have to.

### Battery and voltage — the invisible variable

A 12V FTC battery reads ~13.5V fresh and sags under load. `setPower(0.5)` produces meaningfully less speed at 11V than at 13V, which is why "the auto worked this morning" is a real phenomenon and why control loops with feedback (next section) beat open-loop timing.

**Exercise (no hardware needed):** sketch the full topology on paper — battery → switch → Control Hub → four motors with encoders, OctoQuad on I2C with two pods, Driver Hub over Wi-Fi Direct, laptop over USB. Label the four motor names. Keep the sketch; it is the debugging map.

---

## Block 4 — OpModes: the lifecycle everything hangs off (1 hr 30 min)

**Goal:** know exactly which method runs when, and why blocking code is fatal.

### `OpMode` vs `LinearOpMode`

**`OpMode`** (iterative) — the SDK calls your methods repeatedly. This repo uses it everywhere.

```
[OpMode selected on Driver Hub]
  init()        → runs once, when INIT is pressed
  init_loop()   → runs repeatedly between INIT and START
[START pressed]
  start()       → runs once
  loop()        → runs repeatedly, ~50–100× per second, until STOP
  stop()        → runs once
```

**`LinearOpMode`** — you write one `runOpMode()` method that reads top-to-bottom with `waitForStart()` and `while (opModeIsActive())`. Easier for simple sequences, worse for anything that must do several things at once. Pedro autos are almost always iterative `OpMode`s, because `follower.update()` must be called every cycle.

### The one rule

**`loop()` must return quickly. Always.** No `sleep()`, no `while` loop that waits for something, no long computation. Every cycle `loop()` does not return is a cycle where gamepad input is not read, telemetry is not sent, and — critically — `follower.update()` does not run, so the robot keeps executing the last motor command it was given.

This is why autonomous is written as a **state machine** instead of a sequence: instead of "drive forward, then wait until done", it is "if in state 1 and not busy, move to state 2" evaluated fresh every cycle. `ExampleAuto.autonomousPathUpdate()` is exactly this pattern.

### Telemetry

```java
telemetry.addLine("Ready!");
telemetry.addData("x (in)", "%.2f", pose.getX());   // format string + value
telemetry.update();                                  // nothing shows until this is called
```

The `%.2f` form matters more than it looks — raw doubles print as `72.00000000000001` and make a telemetry screen unreadable. In an `OpMode` (not Linear), `telemetry.update()` is called automatically after `loop()`, but calling it explicitly is a harmless habit.

This repo also has **Panels** (`com.bylazar.sloth:fullpanels`) — a browser dashboard at the Control Hub's address showing telemetry, a field view, and live-editable `@Configurable` values. `Constants` is `@Configurable`, which means PID coefficients can be tuned from a browser without rebuilding. It is also `@Pinned`, which means **Sloth cannot hot-reload changes to that file** — editing `Constants.java` requires a full install. The comment in the file says so; believe it.

### Gamepads

```java
gamepad1.left_stick_y      // float, -1.0 .. 1.0. UP IS NEGATIVE.
gamepad1.left_stick_x      // right is positive
gamepad1.right_stick_x
gamepad1.a, .b, .x, .y     // boolean, true while held
gamepad1.dpad_up           // boolean
gamepad1.left_trigger      // float 0.0 .. 1.0
gamepad1.left_bumper       // boolean
```

**Up is negative** on the sticks — a joystick convention inherited from screen coordinates. That is the `-1 *` in:

```java
double forward = -1 * gamepad1.left_stick_y * speedMultiply;
```

**Rising-edge detection.** `if (gamepad1.a)` is true for every cycle the button is held — at 60 Hz, a normal press fires ~15 times. To toggle something once per press:

```java
private boolean lastA = false;
private boolean clawOpen = false;

// in loop():
if (gamepad1.a && !lastA) {   // true only on the cycle the button goes down
    clawOpen = !clawOpen;
}
lastA = gamepad1.a;
```

Write this once and understand it; it is needed in every real TeleOp.

### Exercise (45 min): `learning/LifecycleDemo.java`

```java
package org.firstinspires.ftc.teamcode.learning;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp(name = "Learning - Lifecycle", group = "Learning")
public class LifecycleDemo extends OpMode {

    private final ElapsedTime timer = new ElapsedTime();
    private int initLoopCount = 0;
    private int loopCount = 0;
    private boolean lastA = false;
    private boolean toggled = false;

    @Override public void init() {
        timer.reset();
    }

    @Override public void init_loop() {
        initLoopCount++;
        telemetry.addData("init_loop calls", initLoopCount);
    }

    @Override public void start() {
        timer.reset();
        loopCount = 0;
    }

    @Override public void loop() {
        loopCount++;

        if (gamepad1.a && !lastA) toggled = !toggled;
        lastA = gamepad1.a;

        telemetry.addData("loop calls", loopCount);
        telemetry.addData("seconds", "%.1f", timer.seconds());
        telemetry.addData("loops/sec", "%.0f", loopCount / Math.max(timer.seconds(), 0.001));
        telemetry.addData("toggled by A", toggled);
        telemetry.addData("left_stick_y", "%.3f", gamepad1.left_stick_y);
    }
}
```

It cannot run without a robot — that is fine. It must **compile**, and the trace of which method runs when must be explainable without looking. Write down, on paper, the expected value of `loop calls` after 10 seconds at 60 Hz before moving on.

**Checkpoint:** build succeeds.

---

## Block 5 — Mecanum drive, from rollers to code (2 hr 30 min)

**Goal:** derive the mecanum equations rather than memorize them, then extend the repo's TeleOp to field-centric.

### Why a mecanum wheel strafes

Each mecanum wheel has free-spinning rollers set at **45°** to the wheel axis. Spinning the wheel pushes the floor along the roller axis, so the force each wheel produces points diagonally, at 45° to straight ahead. The wheels are mounted so the rollers form an **X pattern** when viewed from above (front-left and back-right rollers point one way; front-right and back-left the other).

Sum four diagonal force vectors and the sideways components cancel or reinforce depending on the sign pattern:

| Motion | FL | FR | BL | BR |
|---|---|---|---|---|
| Forward | + | + | + | + |
| Strafe right | + | − | − | + |
| Rotate clockwise | + | − | + | − |

Superpose all three, and each wheel power is a signed sum. That is the entire derivation, and it is exactly what `MecanumDrive.drive` does:

```java
double frontLeftPower  = forward + right + rotate;
double frontRightPower = forward - right - rotate;
double backLeftPower   = forward - right + rotate;
double backRightPower  = forward + right - rotate;
```

**Check the table against the code, term by term, before continuing.** If a robot strafes when told to go forward, or spins when told to strafe, one sign in these four lines is wrong — and knowing the table means fixing it in thirty seconds instead of an hour of guessing.

Physical caveats worth knowing: mecanum wheels **strafe slower than they drive forward** (roughly 15–20% loss — visible in this repo's `xVelocity(49.74)` vs `yVelocity(57.09)`), they **slip more** than traction wheels, and roller direction must alternate correctly or the robot will do something confidently wrong.

### Normalization — why `setPowers` divides

Full forward + full strafe + full rotate gives `1 + 1 + 1 = 3` on one wheel. `setPower(3.0)` clips to `1.0`, and now the wheel ratios are wrong, so the robot travels the wrong direction entirely. The fix is to scale all four down by the largest magnitude — but only if that magnitude exceeds 1:

```java
double maxSpeed = 1.0;                                  // starts at 1.0, not 0.0 — on purpose
maxSpeed = Math.max(maxSpeed, Math.abs(frontLeftPower));
// ... other three ...
frontLeftPower /= maxSpeed;                             // no-op when everything is already ≤ 1
```

Starting `maxSpeed` at `1.0` means small inputs are left alone; starting at `0.0` would normalize gentle inputs up to full power. Trace both versions with inputs `(0.2, 0.1, 0.0)` and confirm.

**Exercise (30 min):** `learning/MecanumMath.java`, a plain class with a `main`:

```java
package org.firstinspires.ftc.teamcode.learning;

public class MecanumMath {

    public static double[] drive(double forward, double right, double rotate) {
        double fl = forward + right + rotate;
        double fr = forward - right - rotate;
        double bl = forward - right + rotate;
        double br = forward + right - rotate;

        double max = 1.0;
        max = Math.max(max, Math.abs(fl));
        max = Math.max(max, Math.abs(fr));
        max = Math.max(max, Math.abs(bl));
        max = Math.max(max, Math.abs(br));

        return new double[] { fl / max, fr / max, bl / max, br / max };
    }

    private static void show(String label, double f, double r, double t) {
        double[] p = drive(f, r, t);
        System.out.printf("%-22s fl=%+.2f fr=%+.2f bl=%+.2f br=%+.2f%n",
                label, p[0], p[1], p[2], p[3]);
    }

    public static void main(String[] args) {
        show("full forward",   1, 0, 0);
        show("full strafe R",  0, 1, 0);
        show("full rotate CW", 0, 0, 1);
        show("fwd + strafe",   1, 1, 0);
        show("everything",     1, 1, 1);
        show("gentle",       0.2, 0.1, 0);
    }
}
```

Predict all six rows on paper first. Then run it. Every mismatch is a real gap.

### Robot-centric vs field-centric (1 hr)

The repo's TeleOp is `@TeleOp(name = "Mecanum -Robot Centric")`. **Robot-centric**: pushing the stick forward drives out of the robot's front, whatever direction that is pointing. Intuitive when the robot faces away, disorienting the instant it turns around — and in a match it turns around constantly.

**Field-centric**: pushing the stick away from you always moves the robot away from you, regardless of robot heading. It requires knowing the heading, then rotating the requested field vector into the robot's frame:

$$\begin{bmatrix} x_r \\ y_r \end{bmatrix} = \begin{bmatrix} \cos(-\theta) & -\sin(-\theta) \\ \sin(-\theta) & \cos(-\theta) \end{bmatrix} \begin{bmatrix} x_f \\ y_f \end{bmatrix}$$

The negative angle is the whole trick: rotating the *vector* by $-\theta$ is the same as expressing it in a frame rotated by $+\theta$. Getting this sign backwards produces a robot that drifts in a curve when asked to go straight — a classic and very confusing symptom.

**Exercise — `learning/MecanumFieldCentricTeleOp.java`.** Write it from scratch; use the repo's `MecanumDriveTeleOp` only as a shape reference.

```java
package org.firstinspires.ftc.teamcode.learning;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;

@Disabled
@TeleOp(name = "Learning - Field Centric", group = "Learning")
public class MecanumFieldCentricTeleOp extends OpMode {

    private MecanumDrive drive;
    private IMU imu;
    private double speedMultiplier = 0.5;

    @Override public void init() {
        drive = new MecanumDrive();
        drive.init(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        // These two enums must match how the Control Hub is physically bolted to the robot.
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));

        telemetry.addLine("Field centric ready. Press OPTIONS to re-zero heading.");
    }

    @Override public void loop() {
        if (gamepad1.dpad_up)         speedMultiplier = 0.25;
        else if (gamepad1.dpad_left)  speedMultiplier = 0.50;
        else if (gamepad1.dpad_down)  speedMultiplier = 0.75;
        else if (gamepad1.dpad_right) speedMultiplier = 1.00;

        if (gamepad1.options) imu.resetYaw();   // driver re-zero: face the robot away, press

        double yF = -gamepad1.left_stick_y;   // field-frame forward
        double xF =  gamepad1.left_stick_x;   // field-frame right
        double rotate = gamepad1.right_stick_x;

        // Deadband: sticks rarely rest at exactly 0.
        if (Math.hypot(xF, yF) < 0.05) { xF = 0; yF = 0; }
        if (Math.abs(rotate) < 0.05)   { rotate = 0; }

        double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the field vector by -heading into the robot frame.
        double cos = Math.cos(-heading);
        double sin = Math.sin(-heading);
        double forwardR = yF * cos - xF * sin;
        double rightR   = yF * sin + xF * cos;

        drive.drive(forwardR * speedMultiplier,
                    rightR   * speedMultiplier,
                    rotate   * speedMultiplier);

        telemetry.addData("heading (deg)", "%.1f", Math.toDegrees(heading));
        telemetry.addData("speed mult", speedMultiplier);
        telemetry.update();
    }
}
```

Two things to be able to explain afterwards: why `imu.resetYaw()` exists (the IMU zeroes wherever the robot happened to be at INIT, which is rarely the direction the driver is facing), and what happens if the two `RevHubOrientationOnRobot` enums do not match reality (heading is measured about the wrong axis; field-centric becomes nonsense).

**Optional polish (20 min):** input shaping. `Math.pow(x, 3)` or `x * Math.abs(x)` gives fine control near center and full power at the extremes. Add it, keep the sign, verify the build.

**Checkpoint:** build succeeds; both new OpModes compile.

---

## Block 6 — Day 1 consolidation (1 hr)

Close the laptop for the written part.

**Answer from memory, in writing:**

1. What runs between pressing INIT and pressing START, and how many times?
2. `hardwareMap.get(DcMotorEx.class, "fl")` throws on INIT. Name three distinct causes.
3. Why does `setPowers` divide by `maxSpeed` and why does `maxSpeed` start at `1.0`?
4. Robot heading is 90°. The driver pushes the stick straight away. In field-centric, which way does the robot move, and which motors turn which way?
5. What does `@Override` actually do, and what silently breaks without it?
6. Why can `loop()` never contain `sleep(1000)`?
7. Why does `MecanumDrive.init` reverse `fl` and `bl` but not `fr` and `br`?

**Then, at the laptop (30 min):** delete `MecanumFieldCentricTeleOp.java` and rewrite it from a blank file without looking. It will not be perfect. The compiler will say what is missing, and that round trip is the actual learning. Finish with a green build.

---

# DAY 2 — Localization, path following, and a real autonomous

The through-line: by the end of the day, be able to write a new multi-segment Pedro auto from scratch, explain every constant in `Constants.java`, and say what to do first the next time the robot is on a field.

## Block 1 — Localization: how a robot knows where it is (1 hr 30 min)

**Goal:** understand pose, ticks, and why this repo has two localizers.

### Pose

A robot's position on the field is a **pose**: `(x, y, heading)`. The FTC field is **144 × 144 inches** (12 ft square), so `(72, 72)` is dead center — the `START` constant in `ExampleAuto`. Heading is an angle in radians, counter-clockwise positive.

Pedro uses its own coordinate convention and provides conversion helpers to FIRST's official one. When copying coordinates from a teammate's Road Runner code or a random tutorial, coordinates are the first thing to check.

### Dead reckoning from ticks

An encoder reports **ticks**. Convert to inches:

$$\text{inches} = \frac{\text{ticks}}{\text{ticks per revolution}} \times \pi \times \text{wheel diameter}$$

In practice nobody computes this from the datasheet, because gear slop, wheel wear and tread compression make the theoretical number wrong by a few percent. Instead: push the robot a **measured 48 inches**, see how many inches the code reports, and the ratio of the two is the multiplier you paste in. That is precisely what Pedro's Forward / Lateral / Turn tuners do, and why `Constants.driveEncoderConstants` has `forwardTicksToInches(1)` with a comment saying the `1` is a placeholder.

### The two localizers in this repo

`Constants.useDriveEncoderLocalizer` picks between them:

**`true` — drive encoders.** Position comes from the four drivetrain motors. No extra hardware. The flaw is direct: a drive wheel that slips still turns, so the robot believes it moved when it did not. Under acceleration, on a defended push, or when strafing, error accumulates and never corrects. Fine for developing paths; not a competition auto.

**`false` — OctoQuad dead wheels.** Two (or three) unpowered omni wheels on light spring pressure, whose only job is to measure. They cannot slip under torque because no torque is applied to them. The **OctoQuad** is an I2C coprocessor that reads up to eight encoders and hands positions to the Control Hub, saving bus time.

The odometry pods are **off the robot as of late August 2026**, so `useDriveEncoderLocalizer = true` is correct today and must be flipped the moment the pods go back on. Note the `@Pinned` warning: Sloth cannot hot-reload `Constants.java`, so that flip requires a full install.

### Reading the OctoQuad block

Go through `Constants.octoConstants` line by line and say what each does:

- `.name("octoquad")` — must match the I2C device name in the Driver Hub config
- `.deadwheelPortX(0)` / `.deadwheelPortY(7)` — which OctoQuad ports the two pods are plugged into
- `.tcpOffsetXMM(79.375f)` / `.tcpOffsetYMM(-31.75f)` — where each pod sits relative to the tracking center, in **millimetres**. These matter enormously for heading: a pod offset error makes the robot's believed position rotate wrongly as it turns.
- `.deadwheelXTicksPerMM(19.894f)` — the calibration constant, from `DeadwheelCalibrator`
- `.imuScalar(1.0323f)` — a correction factor for IMU heading drift, from `HeadingScalarCalibrator` (both of those OpModes are in `OctoQuadTesting/`)

**Exercise (20 min):** `learning/OdometryMath.java`. Given `ticksPerRev = 2000`, `wheelDiameterMm = 48`, compute inches per tick; then, given left and right pod deltas and a track width, compute Δheading. Print the numbers. The point is that "odometry" is arithmetic, not magic.

---

## Block 2 — Control theory, the useful half (1 hr 15 min)

**Goal:** be able to look at `PIDFCoefficients(1.5, 0, 0.1, 0.01)` and predict what changing each number does.

### Open loop vs closed loop

Open loop: "run the motors at 0.5 for 2 seconds." Fast to write, wrong on a low battery, wrong on carpet vs tile, wrong when a robot bumps you. Closed loop: measure where you are, compare to where you want to be, and correct. Every reliable autonomous is closed loop.

### PIDF

Let **error** = target − measured.

- **P — proportional.** Output ∝ error. Far away, push hard; close, push gently. Too low: never arrives (steady-state error). Too high: overshoots and oscillates.
- **I — integral.** Accumulated error over time. Kills small persistent offsets — friction the P term is too weak to overcome. Dangerous: it "winds up" while the robot is blocked and then dumps that stored energy. Note this repo uses `I = 0` for heading. That is common and deliberate.
- **D — derivative.** Responds to the *rate* of error change. It is the brake: it damps the overshoot P causes. Amplifies sensor noise, so it is usually the smallest term.
- **F — feedforward.** A guess added before any feedback, from a model of the system — "a motor needs 0.1 power just to overcome friction." Feedforward does the bulk of the work; PID cleans up the difference.

So `headingPIDFCoefficients(new PIDFCoefficients(1.5, 0, 0.1, 0.01))` reads as: strong proportional correction on heading, no integral, a tenth of that in damping, and a small constant nudge.

**Tuning order that actually works:** P until it oscillates slightly → add D until the oscillation damps → add I only if a steady offset remains → F last, or first if a good model exists.

### Pedro's two followers

- **PIDF following** — three PID controllers (translational, heading, drive) plus a **centripetal force correction** that pushes outward-to-inward on curves so the robot does not drift wide. This repo sets `.centripetalScaling(0)`, meaning it is currently off — a real tuning item.
- **Predictive braking** — estimates stopping distance and starts decelerating early, so the robot arrives at a point without overshoot-and-return. This repo sets it: `PredictiveBrakingCoefficients(0.2, 0.00943, 0.00296)`. Mostly auto-tuned; one manual tuner.

`pathConstraints = new PathConstraints(0.97, 100, 1, 1)` sets the four end conditions, in order: **tValue** (how far along the path, 0–1, counts as done), **velocity** (in/s below which the robot is considered stopped), **translational** (inches of position tolerance), **heading** (radians of heading tolerance). Together they decide when `isBusy()` goes false.

Look hard at those last three. Velocity 100 in/s is faster than the robot can move, so that condition is always satisfied; translational 1 means "within an inch"; heading **1 radian** is 57°, which is an enormous heading tolerance. Pedro's own documented example uses `0.1`, `0.1`, `0.009`. These are loose enough to be worth asking about — they may be deliberate for a fast shakedown path, or they may be placeholders nobody tightened.

**Exercise (30 min):** `learning/PidSim.java`. Simulate a 1-D system: `position += velocity * dt; velocity += (power * k - drag * velocity) * dt;` with a P controller driving position to 100. Print position every step. Then try P = 0.01, 0.1, 0.5, 2.0 and watch it go from sluggish to oscillating. Add D and watch it settle. Twenty minutes here is worth more than any amount of reading about PID.

---

## Block 3 — Pedro Pathing vs Road Runner (45 min)

**Goal:** be able to explain the team's choice in two sentences to a judge.

| | **Pedro Pathing** | **Road Runner** |
|---|---|---|
| Core idea | Follow a **path** (a shape in space); three PID controllers correct toward it continuously | Follow a **trajectory** (a shape in space *plus* a schedule in time), via motion profiling with feedforward + feedback |
| Optimizes for | Speed and precision, and recovery from disturbance | Time consistency and predictability |
| Pushed off course | Corrects back to the path and continues | Fights to catch up with a time schedule it can no longer meet |
| Drivetrains | Mecanum, coaxial swerve, custom | Mecanum, tank |
| Coordinates | Custom system, with converters to FIRST's | FIRST's official system directly |
| Tuning | ~6 automatic + ~4 manual steps for PIDF; predictive braking is nearly all automatic | ~4 automatic + ~2 manual |
| Path design | No-code **web visualizer** | Code-based visualizer |
| Logging | Telemetry only; third-party for more | Built-in file logging, AdvantageScope support |
| Speed cap | Aims for full speed | Defaults to ~80% max, does not model strafe speed loss |

**Path vs trajectory is the whole difference.** A path says "go through these points." A trajectory says "be at this point at t = 1.4 s." When a defensive robot hits you, a path-follower resumes; a time-follower is now behind schedule and behaves badly.

**Why this team is on Pedro:** mecanum, aggressive auto speeds, a defended game, and a visualizer that makes path design fast. That is a defensible answer, and "we use Pedro because our old code used it" is not.

**Do:** open the [visualizer](https://pedropathing.com/visualizer), draw a three-segment path on the field image, and compare the coordinates it produces with the ones hardcoded in `ExampleAuto.buildPaths()`.

---

## Block 4 — The Pedro API (1 hr 30 min)

**Goal:** write paths from memory.

### The pieces

```java
Pose p = new Pose(72, 108, Math.toRadians(90));  // x in, y in, heading in RADIANS
```

**`BezierLine(start, end)`** — a straight segment between two poses.

**`BezierCurve(start, control..., end)`** — a smooth curve. The control points are **magnets**, not waypoints: the curve bends toward them but does not pass through them. More control points, more bend. In `ExampleAuto`:

```java
new BezierCurve(new Pose(72, 108, Math.toRadians(90)),   // start — on the curve
                new Pose(108, 90),                        // control — pulls the curve, NOT visited
                new Pose(108, 72, Math.toRadians(180)))   // end — on the curve
```

**Heading interpolation** — position and heading are independent. Three choices:

- `.setConstantHeadingInterpolation(rad)` — hold one heading for the whole segment. Used for `line1`.
- `.setLinearHeadingInterpolation(startRad, endRad)` — rotate smoothly from one to the other across the segment. Used for `line2`.
- `.setTangentHeadingInterpolation()` — always face along the direction of travel, like a car. Used for `curve1`.

**`PathChain`** — one or more segments built with `follower.pathBuilder()...build()`, followed as a unit.

### The follower

```java
follower = Constants.createFollower(hardwareMap);  // reads Constants, picks the localizer
follower.setStartingPose(START);                    // tell it where it starts — it cannot know
follower.update();                                  // EVERY loop cycle. Reads sensors, commands motors.
follower.updatePose();                              // reads sensors only — safe before START
follower.followPath(chain);                         // start following
follower.isBusy();                                  // true until the chain is finished
follower.getPose();                                 // current believed pose
follower.isLocalizationNAN();                       // localizer has gone numerically insane
follower.startTeleopDrive(true);                    // hand control to manual
follower.setTeleOpDrive(f, s, r, true);             // manual power under the follower
```

The `init_loop()` / `update()` distinction in `ExampleAuto` is a genuine safety design: `updatePose()` reads the localizer without touching the drivetrain, so the robot can be pushed by hand during INIT to verify the pose numbers move correctly — before anything is allowed to command a motor.

### Radians. Every time.

Repeating it because it costs teams whole matches: `Pose` headings, both heading interpolators, and `PathConstraints.headingConstraint` are all radians. `Math.toRadians()` around every human-written angle, with no exceptions.

### Exercise (45 min): `learning/SquarePathAuto.java`

Write a Pedro auto from scratch, without copying `ExampleAuto`, that drives a 48-inch square starting at `(72, 72)` facing 90°, holding a constant heading the whole way (so it strafes two of the four sides), then returns to start. Requirements:

- Four `PathChain`s built in a `buildPaths()` method
- A `switch (pathState)` state machine advancing on `!follower.isBusy()`
- `follower.update()` first thing in `loop()`
- Telemetry showing pose, state, and elapsed time
- `@Disabled` and `group = "Learning"`
- It compiles

Then a harder variant: same square, but `setTangentHeadingInterpolation()` so the robot turns each corner like a car. Predict on paper how the motion differs before writing it.

---

## Block 5 — Autonomous structure and safety (1 hr)

**Goal:** understand why `ExampleAuto` is written the way it is — it is a genuinely good model.

### The state machine

```java
switch (pathState) {
    case 0:
        follower.followPath(line1);
        pathState++;
        break;
    case 1:
        if (!follower.isBusy()) { follower.followPath(curve1); pathState++; }
        break;
    ...
}
```

Every case is a fast, non-blocking check. `loop()` returns immediately in all cases, so `follower.update()` keeps running at full rate. To add "raise the arm, then drive", it becomes a new state that checks `armMotor.isBusy()` or an `ElapsedTime` — never a `sleep()`.

### The two safety guards

Read `safetyTripped()` and understand the failure it prevents, because it is subtle and it is the worst one in FTC:

If the localizer **freezes** — an unplugged pod, a dead I2C bus — the follower sees a pose that never approaches the target. It concludes it is not there yet and holds full corrective power. `isBusy()` never becomes false. The robot drives into the wall and keeps pushing until the 30-second auto period ends. Motors burn, and nothing in the code notices.

Two guards:

1. **Overall timeout** — `OPMODE_TIMEOUT_S = 20.0`. Nothing in the routine should approach this, so hitting it means something is wrong.
2. **Stall detection** — if the pose has moved less than 0.5 in and turned less than 0.05 rad for 2 continuous seconds while `isBusy()`, abort. The comment notes Pedro's own stuck detection does not cover this case, because it only arms between t-values 0.1 and 0.8 and a frozen pose never leaves t ≈ 0.

And the abort itself is careful:

```java
follower.startTeleopDrive(true);
follower.setTeleOpDrive(0, 0, 0, true);
requestOpModeStop();
```

It hands control to teleop drive and commands zero, so `follower.update()` actively holds the motors at 0 rather than leaving the last command standing. Copy this pattern into every auto ever written.

**Exercise (25 min):** add both guards to `SquarePathAuto`, and add a third — abort if `follower.isLocalizationNAN()` returns true, with a distinct message. Build.

---

## Block 6 — Tuning, as a document to read now and execute later (1 hr)

**Goal:** when the robot is next available, no time is lost figuring out what to do.

`Tuning.java` is 1,813 lines and registers as a single `@TeleOp(name = "Tuning", group = "Pedro Pathing")` with a menu. Read the menu structure and write the checklist below onto one page to bring to the field.

### The order, and it is an order

**Stage 0 — Localization sanity.**
`Localization Test`. Push the robot by hand. X, Y and heading must all move in the correct direction and the correct sign. Nothing downstream means anything until this is right. When an axis counts backwards, flip that one encoder direction in `Constants.driveEncoderConstants` — those directions are independent of the drivetrain motor directions, which is a trap worth remembering.

**Stage 1 — Distance calibration.**

| Tuner | Action | Field in `Constants` |
|---|---|---|
| Forward Tuner | Push forward exactly 48 in | `forwardTicksToInches` |
| Lateral Tuner | Push left exactly 48 in | `strafeTicksToInches` |
| Turn Tuner | Rotate exactly 1 full turn CCW | `turnTicksToInches` |

Each tuner reports a **multiplier**; paste it into the matching field. The values are `1` today because they are placeholders, not because someone tuned them to 1.

Also fill in `robotWidth` and `robotLength` — they are `12`/`12` with a `TODO` and need real measurements.

**Stage 2 — Velocity.** `Forward Velocity` and `Lateral Velocity` tuners produce `xVelocity` and `yVelocity` in `MecanumConstants` (currently 49.74 and 57.09 in/s). Pedro uses them to plan how fast a path can be followed. Note `yVelocity > xVelocity` — sanity-check which axis is which for this drivetrain, because mecanum strafing is normally the *slower* direction.

**Stage 3 — Following PIDs.** Translational, then heading, then drive. P until slight oscillation, D to damp, I rarely. These are `@Configurable`, so they can be edited live in Panels — but `Constants` is `@Pinned`, so a code edit needs a full install.

**Stage 4 — Centripetal scaling.** Currently `0`. Raise it until curves stop drifting wide.

**Stage 5 — Mass.** `.mass(4.54)` kg (~10 lb) with a comment saying "25 lbs no intake or climb." 25 lb is 11.34 kg. Those two numbers disagree by a factor of 2.5, and mass feeds the predictive braking model. **Flag this to the team.** It may be deliberate and it may be a units bug, but it should not be a mystery.

**Exercise (20 min):** produce a one-page field checklist — every stage, the exact OpMode menu item, the exact `Constants` field to update, and a blank for the measured value. That page is the deliverable of this block.

---

## Block 7 — Working without a robot, well (45 min)

**Goal:** build habits that make robot time productive rather than exploratory.

- **Compile constantly.** `./gradlew :TeamCode:assembleDebug` after every meaningful edit. A green build before the robot arrives means the first hour is spent testing, not typing.
- **Extract the math.** Any calculation that does not touch `hardwareMap` can live in a plain class with a `main()` and be checked by printing numbers. Mecanum mixing, pose math, unit conversion, input curves — all testable on a laptop.
- **Read the SDK.** In Android Studio, ctrl-click into `OpMode`, `DcMotorEx`, `Follower`. Reading the actual source of a library is the fastest way to stop guessing what it does.
- **Telemetry is a debugger.** Adding a value to telemetry is the closest thing FTC has to a breakpoint. Add generously; delete before competition, since telemetry costs loop time.
- **Commit small and often.** `git add -p`, real commit messages. When a working auto stops working, `git diff` answers "what changed" in seconds.
- **Write the questions down.** Keep a running list of things only the hardware can answer: which axis is strafe, is `yVelocity` really larger, is the mass in kg or lb. Bring the list, not a vague memory.

**Do now:**

```bash
cd ~/Documents/GitHub/1002Memdev
git checkout -b learning-week
git add TeamCode/src/main/java/org/firstinspires/ftc/teamcode/learning
git commit -m "Add learning-week practice OpModes and math scratch classes"
```

---

## Block 8 — Capstone (2 hr)

No references beyond the javadoc. Both files must compile.

**Part A — `learning/CapstoneTeleOp.java`**

- Field-centric mecanum, IMU-based, with `options` to re-zero heading
- Four D-pad speed presets
- Cubic input shaping on the drive sticks, linear on rotate
- A rising-edge toggle on `a` that flips a boolean and shows it in telemetry
- Telemetry: heading in degrees, speed multiplier, all four motor powers, loop frequency
- `@Disabled`, `group = "Learning"`

**Part B — `learning/CapstoneAuto.java`**

- Starts at `(9, 60, 0°)` — a plausible left-wall start pose
- Four segments: a straight line out, a Bézier curve to a second position, a straight strafe, and a curve back
- A different heading interpolation on at least three of the four segments, each chosen for a stated reason written in a comment
- A `switch` state machine, with one state that waits 1.5 s on an `ElapsedTime` between paths (no `sleep()`)
- All three safety guards: overall timeout, stall detection, localization-NaN
- `init_loop()` telemetry that proves the localizer is alive before START
- `@Disabled`, `group = "Learning"`

**Part C — self-review (30 min)**

Read both files as if reviewing someone else's work and answer in writing:

1. Is there a `sleep()` or a blocking `while` anywhere? (There must not be.)
2. Is every angle wrapped in `Math.toRadians()`?
3. If the localizer dies at t = 3 s, what happens, in sequence?
4. Which hardware config names must exist for these to run, exactly as spelled?
5. Which numbers are guesses that only field time can settle?

---

# Reference

## Glossary

| Term | Meaning |
|---|---|
| **OpMode** | A program the Driver Hub can run. Registered by `@TeleOp` / `@Autonomous`. |
| **TeleOp** | The driver-controlled period of a match. |
| **Auto** | The first 30 s, robot-controlled, no driver input. |
| **Control Hub** | REV's Android-based robot controller. Runs your code. |
| **Driver Hub** | REV's Android tablet driver station. Runs FIRST's app, sends inputs. |
| **hardwareMap** | Runtime lookup from config-file names to hardware objects. |
| **Telemetry** | Text sent from Control Hub to Driver Hub screen. |
| **Encoder** | Shaft sensor counting ticks of rotation. |
| **Odometry** | Estimating position by integrating wheel motion. |
| **Dead wheel** | An unpowered omni wheel used only for measuring, so it cannot slip. |
| **OctoQuad** | I2C coprocessor reading up to 8 encoders for the Control Hub. |
| **IMU** | Inertial measurement unit; supplies heading. Built into the Control Hub. |
| **Pose** | `(x, y, heading)` — a position and facing on the field. |
| **PIDF** | Proportional / Integral / Derivative / Feedforward control. |
| **Path** | A shape in space to follow (Pedro). |
| **Trajectory** | A path plus a time schedule (Road Runner). |
| **Bézier curve** | Smooth curve defined by endpoints and control points that pull without being visited. |
| **PathChain** | One or more Pedro segments followed as a unit. |
| **Follower** | Pedro's object that reads the localizer and drives the robot along a path. |
| **Panels** | Browser dashboard for telemetry and live-editing `@Configurable` values. |
| **Sloth** | Hot-reload for TeamCode. Cannot reload `@Pinned` classes. |

## Errors and what they actually mean

| Symptom | Real cause |
|---|---|
| INIT throws naming a device | Config name ≠ string in `hardwareMap.get`. Fix the config, not the code. |
| OpMode missing from the list | `@Disabled` still present, or the install did not actually run. |
| Robot spins when told to go forward | A motor direction is wrong, or a sign in the four mecanum lines is wrong. |
| Robot drifts in a curve when told straight | Field-centric rotation sign flipped, or the IMU orientation enums do not match the physical hub mounting. |
| Auto goes to a wildly wrong angle | A heading passed in degrees where radians were expected. |
| Auto drives into a wall and stays | Frozen localizer. This is what the stall guard exists for. |
| Everything works, then does not after a code change | `Constants` is `@Pinned` — Sloth hot-reload did not apply it. Full install. |
| Works in the morning, not in the afternoon | Battery voltage sag. Argues for closed-loop control over timed open-loop. |

## Links

- Pedro Pathing docs — https://pedropathing.com/docs
- Pedro path visualizer — https://pedropathing.com/visualizer
- Pedro vs Road Runner (Pedro's own writeup) — https://pedropathing.com/docs/pathing/pedro-v-roadrunner
- Pedro vs Road Runner (Dairy Foundation cookbook, neutral) — https://cookbook.dairy.foundation/misc/pedro_vs_roadrunner.html
- FTC game and season (BIOBUZZ, kickoff Sep 12 2026) — https://www.firstinspires.org/programs/ftc/game-and-season
- FTC SDK javadoc — https://javadoc.io/doc/org.firstinspires.ftc
- FTC Docs (official programming guide) — https://ftc-docs.firstinspires.org
- Game Manual 0 (community reference) — https://gm0.org

## Open questions for the team

Bring these; they are real and this repo raises them.

1. `.mass(4.54)` kg vs the comment "25 lbs" (= 11.34 kg). Which is right? It feeds predictive braking.
2. `xVelocity(49.74)` < `yVelocity(57.09)`, but mecanum normally strafes *slower* than it drives. Which axis is which here?
3. `MecanumDrive` sets `BRAKE`, `Constants` sets `.useBrakeModeInTeleOp(false)`. Intentional?
4. `.centripetalScaling(0)` — is centripetal correction switched off deliberately, or never tuned?
5. `robotWidth(12)` / `robotLength(12)` still carry `TODO: measure your robot`.
6. `PathConstraints(0.97, 100, 1, 1)` — a 1-radian (57°) heading tolerance and a 100 in/s velocity tolerance are far looser than Pedro's documented example (`0.1, 0.1, 0.009`). Deliberate, or never tightened?
7. When do the odometry pods go back on? That flips `useDriveEncoderLocalizer` and changes what "accurate" means.
