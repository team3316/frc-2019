package com.team3316.robot.subsystems;

import java.util.Arrays;
import java.util.Optional;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team3316.kit.DBugLogger;
import com.team3316.kit.DBugSubsystem;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.kit.motors.DBugTalon;
import com.team3316.kit.motors.TalonType;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;
import com.team3316.robot.utils.InvalidStateException;
import com.team3316.robot.utils.Utils;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Elevator subsystem
 */
public class Elevator extends DBugSubsystem {
  /*
   * Actuators
   */
  private DBugTalon _masterMotor;
  private VictorSPX _slaveMotor;
  private double _distPerRevolution, _armStateThreshold;
  private int _upr;

  /*
   * Tolerances
   */
  private double _motorRunningTolerance, _motorCLETolerance;

  /*
   * Talon profile slots
   */
  public static final int kUpProfileSlot = 0;
  public static final int kDownProfileSlot = 1;

  /**
   * An enumeration of the elevator's different states
   */
  public enum ElevatorState {
    BOTTOM(0), // Elevator is bottomed
    BOTTOM_BP(1), // Elevator is at the bottom breakpoint
    PRE_LVL1_HP(2), LVL1_HP(3), // Level 1 HP install & acquire
    LVL1_CARGO(4), // Level 1 Cargo
    STARTING_CONF(5), // Starting Configuration
    PRE_LVL2_HP(6), LVL2_HP(7), // Level 2 HP install
    LVL2_CARGO(8), // Level 2 Cargo install
    PRE_LVL3_HP(9), LVL3_HP(10), // Level 3 HP install
    LVL3_CARGO(11), // Level 3 Cargo install
    TOP_BP(12), // Elevator is at the top breakpoint
    TOP(13), // Elevator is topped
    INTERMEDIATE(14); // Intermediate state

    private int _index;
    private double _height = -3316.0;
    private DigitalInput _hallEffect = null;

    ElevatorState(int index) {
      this._index = index;
    }

    /**
     * Sets the states' physical height in the elevator.
     *
     * @param height The height of the state, in centimeters. The value for
     *               heightless states (i.e. INTERMEDIATE) should be -3316.0 which
     *               is default.
     */
    public void setHeight(double height) {
      this._height = height;
    }

    /**
     * @return The state's height
     */
    public double getHeight() {
      return this._height;
    }

    /**
     * Adds a sensor to the state that activates when the state is active. This
     * usually will be a hall effect sensor (that's the reason for the name), but
     * can be any digital input. This method shouldn't be called on sensorless
     * states (i.e. INTERMEDIATE) since the default value of this property is null,
     * which is what the {@link ElevatorState#getSensorValue()} method requires for
     * reutrning an empty Optional.
     *
     * @param port The DIO port of the sensor.
     */
    public void addSensor(int port) {
      this._hallEffect = new DigitalInput(port);
    }

    /**
     * Returns an Optional instance with the sensor's value (if there is a sensor).
     *
     * @return If the sensor isn't null, then an Optional with its value. Else, an
     *         empty Optional.
     */
    public Optional<Boolean> getSensorValue() {
      return this._hallEffect != null ? Optional.of(!this._hallEffect.get()) : Optional.empty();
    }

    /**
     * @return A boolean indicating whether the state's position is above the
     *         ElevatorState.BOTTOM position using its index.
     */
    public boolean isAboveBottom() {
      return this._index > 0;
    }

    public ElevatorState getPreState() {
      switch (this) {
        case LVL1_HP:
          return ElevatorState.BOTTOM;
        case LVL2_HP:
          return ElevatorState.PRE_LVL2_HP;
        case LVL3_HP:
          return ElevatorState.PRE_LVL3_HP;
        default:
          return ElevatorState.INTERMEDIATE;
      }
    }

    public boolean isHPInstallState() {
      ElevatorState[] HPInstallStates = { ElevatorState.LVL1_HP, ElevatorState.LVL2_HP, ElevatorState.LVL3_HP, ElevatorState.LVL2_CARGO, ElevatorState.LVL3_CARGO };
      return (Arrays.stream(HPInstallStates).anyMatch(this::equals));
    }

    public boolean isCargoInstallState() {
      ElevatorState[] CargoInstallStates = { ElevatorState.LVL1_CARGO, ElevatorState.LVL2_CARGO,
          ElevatorState.LVL3_CARGO, ElevatorState.LVL2_HP, ElevatorState.LVL3_HP };
      return (Arrays.stream(CargoInstallStates).anyMatch(this::equals));
    }

    public static ElevatorState getCargoLevel(int level) {
      switch (level) {
        case 1:
          return ElevatorState.LVL1_CARGO;
        case 2:
          return ElevatorState.LVL2_CARGO;
        case 3:
          return ElevatorState.LVL3_CARGO;
        default:
          return ElevatorState.BOTTOM;
      }
    }

    public static ElevatorState getPanelsLevel(int level) {
      switch (level) {
        case 1:
          return ElevatorState.LVL1_HP;
        case 2:
          return ElevatorState.LVL2_HP;
        case 3:
          return ElevatorState.LVL3_HP;
        default:
          return ElevatorState.BOTTOM;
      }
    }
  }

  public Elevator() throws ConfigException {
    this._armStateThreshold = (double) Config.getInstance().get("elevator.armStateThreshold");
    // Actuators
    this._masterMotor = new DBugTalon((int) Config.getInstance().get("elevator.motors.1"), TalonType.CLOSED_LOOP_QUAD);
    this._slaveMotor = new VictorSPX((int) Config.getInstance().get("elevator.motors.2"));
    this._slaveMotor.follow(this._masterMotor); // Motor 1 is the master, motor 2 .losedLoopErrorTolerance");
    this._distPerRevolution = (double) Config.getInstance().get("elevator.encoder.distPerRevolution");
    this._upr = (int) Config.getInstance().get("elevator.encoder.upr");
    this._motorCLETolerance = (int) Config.getInstance().get("elevator.motors.motorCLETolerance");
    this._masterMotor.setDistancePerRevolution(this._distPerRevolution, this._upr);
    this._masterMotor.zeroEncoder();

    // Profile slots setups
    this._masterMotor.setupPIDF( // Up profile
        (double) Config.getInstance().get("elevator.profiles.up.kp"),
        (double) Config.getInstance().get("elevator.profiles.up.ki"),
        (double) Config.getInstance().get("elevator.profiles.up.kd"),
        (double) Config.getInstance().get("elevator.profiles.up.kf"), Elevator.kUpProfileSlot);

    this._masterMotor.setupPIDF( // Down profile
        (double) Config.getInstance().get("elevator.profiles.down.kp"),
        (double) Config.getInstance().get("elevator.profiles.down.ki"),
        (double) Config.getInstance().get("elevator.profiles.down.kd"),
        (double) Config.getInstance().get("elevator.profiles.down.kf"), Elevator.kDownProfileSlot);

    this._masterMotor.configAllowableClosedloopError(Elevator.kDownProfileSlot, (int) this._motorCLETolerance, 10);
    this._masterMotor.configAllowableClosedloopError(Elevator.kUpProfileSlot, (int) this._motorCLETolerance, 10);

    this._masterMotor.setInverted(false);
    this._slaveMotor.setInverted(false);
    this._masterMotor.setSensorPhase(true);

    this._masterMotor.enableCurrentLimit(true);
    this._masterMotor.configPeakCurrentLimit(100);
    this._masterMotor.configContinuousCurrentLimit(60);

    // Internal states initalization
    ElevatorState.BOTTOM.setHeight((double) Config.getInstance().get("elevator.heights.bottom"));
    ElevatorState.BOTTOM_BP.setHeight((double) Config.getInstance().get("elevator.heights.bottomBreakpoint"));
    ElevatorState.STARTING_CONF.setHeight((double) Config.getInstance().get("elevator.heights.stratingConfiguration"));
    ElevatorState.TOP_BP.setHeight((double) Config.getInstance().get("elevator.heights.topBreakpoint"));
    ElevatorState.TOP.setHeight((double) Config.getInstance().get("elevator.heights.top"));

    // Levels initialization
    ElevatorState.LVL1_HP.setHeight((double) Config.getInstance().get("elevator.heights.lvl1.hp"));
    ElevatorState.LVL1_CARGO.setHeight((double) Config.getInstance().get("elevator.heights.lvl1.cargo"));
    ElevatorState.PRE_LVL2_HP.setHeight((double) Config.getInstance().get("elevator.heights.lvl2.hp")
        - (double) Config.getInstance().get("elevator.heights.hp.offset"));
    ElevatorState.LVL2_HP.setHeight((double) Config.getInstance().get("elevator.heights.lvl2.hp"));
    ElevatorState.LVL2_CARGO.setHeight((double) Config.getInstance().get("elevator.heights.lvl2.cargo"));
    ElevatorState.PRE_LVL3_HP.setHeight((double) Config.getInstance().get("elevator.heights.lvl3.hp")
        - (double) Config.getInstance().get("elevator.heights.hp.offset"));
    ElevatorState.LVL3_HP.setHeight((double) Config.getInstance().get("elevator.heights.lvl3.hp"));
    ElevatorState.LVL3_CARGO.setHeight((double) Config.getInstance().get("elevator.heights.lvl3.cargo"));

    // Sensors
    ElevatorState.BOTTOM.addSensor((int) Config.getInstance().get("elevator.hallEffects.bottom"));
    ElevatorState.BOTTOM_BP.addSensor((int) Config.getInstance().get("elevator.hallEffects.bottomBreakpoint"));
    ElevatorState.STARTING_CONF.addSensor((int) Config.getInstance().get("elevator.hallEffects.startingConfiguration"));
    ElevatorState.TOP_BP.addSensor((int) Config.getInstance().get("elevator.hallEffects.topBreakpoint"));
    ElevatorState.TOP.addSensor((int) Config.getInstance().get("elevator.hallEffects.top"));
  }

  @Override
  public void initDefaultCommand() {

  }

  /**
   * Elevator periodic zeroing function
   */
  @Override
  public void periodic() {
    if (ElevatorState.BOTTOM.getSensorValue().orElse(false)) {
      this._masterMotor.setDistance(ElevatorState.BOTTOM.getHeight());
    }

    else if (ElevatorState.BOTTOM_BP.getSensorValue().orElse(false)) {
      this._masterMotor.setDistance(ElevatorState.BOTTOM_BP.getHeight());
    }

    else if (ElevatorState.STARTING_CONF.getSensorValue().orElse(false)) {
      this._masterMotor.setDistance(ElevatorState.STARTING_CONF.getHeight());
    }

    else if (ElevatorState.TOP_BP.getSensorValue().orElse(false)) {
      this._masterMotor.setDistance(ElevatorState.TOP_BP.getHeight());
    }

    else if (ElevatorState.TOP.getSensorValue().orElse(false)) {
      this._masterMotor.setDistance(ElevatorState.TOP.getHeight());
    }

  }

  /**
   * Sets the elevator motors' percentage output. The method will check whether
   * the motion is physically possible (meaning that when the elevator is down we
   * can't go more down and when it's up we can't go more up, so do nothing in
   * these cases).
   *
   * @param percentage The voltage percentage to set to the motors. Value ranges
   *                   from -1 to 1.
   */
  public void setPercentage(double percentage) {
    double direction = Math.signum(percentage);
    boolean willHurtElevator = (this.getState() == ElevatorState.BOTTOM && direction == -1) // Trying to go down in bottom
        || (this.getState() == ElevatorState.TOP && direction == 1); // Trying to go up in top
    if (willHurtElevator)
      return;

    this._masterMotor.set(ControlMode.PercentOutput, percentage);
  }

  /**
   * Sets the elevator's position using a PID loop. This method is unsafe since it
   * doesn't check for system safety which is why it's private. In order to set
   * the position safely, use {@link Elevator#setPosition(double)}
   *
   * @param position The wanted elevator position. This should be between 0 and
   *                 the top state's height.
   */
  private void setPositionUnsafe(double position) {
    if (this._masterMotor.getDistance() < position) {
      this._masterMotor.selectProfileSlot(Elevator.kUpProfileSlot, 0);
    } else {
      this._masterMotor.selectProfileSlot(Elevator.kDownProfileSlot, 0);
    }
    this._masterMotor.set(ControlMode.Position, position);
  }

  /**
   * Returns the holder's postion inside the elevator.
   */
  public double getPosition() {
    return this._masterMotor.getDistance();
  }

  /**
   * @return The closed-loop error of the master TalonSRX.
   */
  public double getError() {
    return this._masterMotor.getError();
  }

  /**
   * Returns the elevator's current state - a value of the {@link ElevatorState}
   * enum.
   *
   * @return If any of the elevator's hall effect sensors is activated, then its
   *         ajdacent state. Otherwise, the intermediate state.
   */
  public ElevatorState getState() {
    for (ElevatorState possibleState : ElevatorState.values()) { // Iterate over all of the possible states
      if (possibleState == ElevatorState.INTERMEDIATE) {
        continue;
      }

      if (Utils.isInNeighborhood(this.getPosition(), possibleState.getHeight(), this._motorCLETolerance)) {
        return possibleState;
      }
    }
    return ElevatorState.INTERMEDIATE;
  }

  /**
   * Sets the elevator state to a given wanted state
   *
   * @param wantedState The wanted state of the elevator, as a member of the
   *                    {@link ElevatorState} enum.
   * @throws InvalidStateException
   */
  public void setState(ElevatorState wantedState) throws InvalidStateException {
    if (this.getState() == wantedState)
      return;
    switch (wantedState) {
    case INTERMEDIATE:
    case STARTING_CONF:
      throw new InvalidStateException("Cannot move elevator states without heights.");
    default:
      if (Robot.panelMechanism.getState() == PanelMechanismState.INTERMEDIATE_BELOW_FLOOR
          && wantedState.getHeight() <= this._armStateThreshold) {
        throw new InvalidStateException("Cannot get elevator down when the panel is in INTERMEDIATE_BELOW_FLOOR");
      }
      if (wantedState != ElevatorState.BOTTOM && Robot.cargoEjector.getArmState() != EjectorArmState.EJECT)
        throw new InvalidStateException("Cannot lift elevator if ejector is not in eject");
      DBugLogger.getInstance() // Log the state change
          .info("Chainging Elevator state: " + this.getState().toString() + " -> " + wantedState.toString());
      this.setPositionUnsafe(wantedState.getHeight());
    }
  }

  public void setBrake(boolean status) {
    NeutralMode mode = status ? NeutralMode.Brake : NeutralMode.Coast;
    this._masterMotor.setNeutralMode(mode);
    this._slaveMotor.setNeutralMode(mode);
  }

  public void initRoutine() {
    this.setBrake(false);
  }

  public void disabledRoutine() {
    this.setBrake(true);
    this.setPercentage(0.0);
  }

  
  public void displayTestData() {
    SmartDashboard.putNumber("Elevator Height", this._masterMotor.getDistance());
    SmartDashboard.putString("Elevator State", this.getState().toString());
    SmartDashboard.putBoolean("Elevator Bottom HE", ElevatorState.BOTTOM.getSensorValue().get());
    SmartDashboard.putBoolean("Elevator Bottom BP HE", ElevatorState.BOTTOM_BP.getSensorValue().get());
    SmartDashboard.putBoolean("Elevator SC HE", ElevatorState.STARTING_CONF.getSensorValue().get());
    SmartDashboard.putBoolean("Elevator Top BP HE", ElevatorState.TOP_BP.getSensorValue().get());
    SmartDashboard.putBoolean("Elevator Top HE", ElevatorState.TOP.getSensorValue().get());
    SmartDashboard.putNumber("Elevator Current", this._masterMotor.getOutputCurrent());
  }

  
  public void displayMatchData() {
    // TODO - Implement
  }

  
  public void displayCommands() {
    SmartDashboard.putData("Elevator to TOP", new ElevatorSetState(ElevatorState.BOTTOM));
    SmartDashboard.putData("Elevator to Cargo LVL1", new ElevatorSetState(ElevatorState.LVL1_CARGO));
    SmartDashboard.putData("Elevator to HP LVL1", new ElevatorSetState(ElevatorState.LVL1_HP));
    SmartDashboard.putData("Elevator to Pre-HP LVL1", new ElevatorSetState(ElevatorState.PRE_LVL1_HP));
    SmartDashboard.putData("Elevator to Cargo LVL2", new ElevatorSetState(ElevatorState.LVL2_CARGO));
    SmartDashboard.putData("Elevator to HP LVL2", new ElevatorSetState(ElevatorState.LVL2_HP));
    SmartDashboard.putData("Elevator to Pre-HP LVL2", new ElevatorSetState(ElevatorState.PRE_LVL2_HP));
    SmartDashboard.putData("Elevator to Cargo LVL3", new ElevatorSetState(ElevatorState.LVL3_CARGO));
    SmartDashboard.putData("Elevator to HP LVL3", new ElevatorSetState(ElevatorState.LVL3_HP));
    SmartDashboard.putData("Elevator to Pre-HP LVL3", new ElevatorSetState(ElevatorState.PRE_LVL2_HP));
    SmartDashboard.putData("Elevator to TOP", new ElevatorSetState(ElevatorState.TOP));
  }
}
