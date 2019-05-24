package com.team3316.robot.subsystems;

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
import com.team3316.robot.commands.cargoIntake.CargoIntakeClose;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.commands.cargoIntake.CargoIntakeSetRollers;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.utils.InvalidStateException;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * CargoIntake
 */
public class CargoIntake extends DBugSubsystem {
  private boolean _isCargoSecured;
  private int _kPIDLoopIdx, _kTimeoutMs;
  private double _armPosTolerance;

  /**
   * _rollerMotor = motor thats on arm
   * _bottomRollerMotor = motor that's on rollers not on arm
   * _armMasterMotor = motor that controls the arm, it also has an encoder on it
   * _armHallDown = hall effect at arm's open position
   * _armHallUp = hall effect at arm's closed position
   */
  private DBugTalon _armMasterMotor;
  private VictorSPX _armSlaveMotor;
  private VictorSP _rollerMotor, _bottomRollerMotor;
  private DigitalInput _armHallDown, _armHallUp;

  public CargoIntake() throws ConfigException {
    _isCargoSecured = false;

    this._armHallDown = new DigitalInput((int) Config.getInstance().get("cargoIntake.arm.hallEffect.down"));
    this._armHallUp = new DigitalInput((int) Config.getInstance().get("cargoIntake.arm.hallEffect.up"));
    this._armMasterMotor = new DBugTalon((int) Config.getInstance().get("cargoIntake.armMasterMotor.port"),
        TalonType.CLOSED_LOOP_QUAD);
    // TODO - Remove when elec removes Victor from robot
    this._armSlaveMotor = new VictorSPX((int) Config.getInstance().get("cargoIntake.armSlaveMotor.port"));
    this._armSlaveMotor.follow(this._armMasterMotor);

    //To make it easy and intuitive to understand IN position will be 0 and OUT will be 90 because on the robot the arm has a 90° movement range
    IntakeArmState.IN.setPosition((double) Config.getInstance().get("cargoIntake.arm.pos.up"));
    IntakeArmState.OUT.setPosition((double) Config.getInstance().get("cargoIntake.arm.pos.down"));
    this._armPosTolerance = (double) Config.getInstance().get("cargoIntake.arm.pos.tolerance");

    // Config _armMasterMotor Closed Control Position loop
    this._kPIDLoopIdx = (int) Config.getInstance().get("cargoIntake.arm.kPIDLoopIdx");
    this._kTimeoutMs = (int) Config.getInstance().get("cargoIntake.arm.kTimeoutMs");
    this._armMasterMotor.setDistancePerRevolution((double) Config.getInstance().get("cargoIntake.arm.motor.distancePerPulse"), (int) Config.getInstance().get("cargoIntake.arm.motor.upr"));
    this._armMasterMotor.setSensorPhase((boolean) Config.getInstance().get("cargoIntake.arm.kSensorPhase"));
    this._armMasterMotor.setInverted((boolean) Config.getInstance().get("cargoIntake.arm.kMotorInvert"));

    // PID loop when going up
    this._armMasterMotor.configAllowableClosedloopError(0,
    (int) Config.getInstance().get("cargoIntake.arm.tolerance"), this._kTimeoutMs);
    this._armMasterMotor.config_kF(0, (double) Config.getInstance().get("cargoIntake.arm.kF"), this._kTimeoutMs);
    this._armMasterMotor.config_kP(0, (double) Config.getInstance().get("cargoIntake.arm.kP"), this._kTimeoutMs);
    this._armMasterMotor.config_kI(0, (double) Config.getInstance().get("cargoIntake.arm.kI"), this._kTimeoutMs);
    this._armMasterMotor.config_kD(0, (double) Config.getInstance().get("cargoIntake.arm.kD"), this._kTimeoutMs);

    // PID loop when going down
    this._armMasterMotor.configAllowableClosedloopError(1,
      (int) Config.getInstance().get("cargoIntake.arm.tolerance"), this._kTimeoutMs);
    this._armMasterMotor.config_kF(1, (double) Config.getInstance().get("cargoIntake.arm.DkF"), this._kTimeoutMs);
    this._armMasterMotor.config_kP(1, (double) Config.getInstance().get("cargoIntake.arm.DkP"), this._kTimeoutMs);
    this._armMasterMotor.config_kI(1, (double) Config.getInstance().get("cargoIntake.arm.DkI"), this._kTimeoutMs);
    this._armMasterMotor.config_kD(1, (double) Config.getInstance().get("cargoIntake.arm.DkD"), this._kTimeoutMs);
    this._rollerMotor = new VictorSP((int) Config.getInstance().get("cargoIntake.rollerMotor.port"));
    this._bottomRollerMotor = new VictorSP((int) Config.getInstance().get("cargoIntake.bottomRollerMotor.port"));

    // Set voltage for each roller state for both motors
    IntakeRollersState.IN.setRollerVoltage((double) Config.getInstance().get("cargoIntake.rollerMotor.voltage.in"));
    IntakeRollersState.IN
        .setBottomRollerVoltage((double) Config.getInstance().get("cargoIntake.bottomRollerMotor.voltage.in"));
    IntakeRollersState.OUT.setRollerVoltage((double) Config.getInstance().get("cargoIntake.rollerMotor.voltage.out"));
    IntakeRollersState.OUT
        .setBottomRollerVoltage((double) Config.getInstance().get("cargoIntake.bottomRollerMotor.voltage.out"));
    IntakeRollersState.STOPPED.setRollerVoltage(0);
    IntakeRollersState.STOPPED.setBottomRollerVoltage(0);
  }

  public enum IntakeArmState {
    IN, OUT, INTERMEDIATE;

    private double _position;

    public void setPosition(double pos) {
      this._position = pos;
    }

    public double getPosition() {
      return this._position;
    }

  }
  /**
   * @return Arm's current position, measured by the encoder.
   * Range:
   *   IntakeArmState.IN._position == fully closed
   *   IntakeArmState.OUT._position == fully open
   */


  /**
   * @return Current arm state
   */
  public IntakeArmState getArmState() {
    return this.degToArmState(this.getArmPos());
  }

  /**
   * @param state Move arm to a given state
   */
  public void setArmState(IntakeArmState state) throws InvalidStateException {
    // Return if wanted state is in and ejector isn't in eject
    if (state == IntakeArmState.IN && Robot.cargoEjector.getArmState() != EjectorArmState.EJECT)
      throw new InvalidStateException("Cannot set intake to IN if ejector isn't eject");

    //Select correct PID loop whether going up or down
    if (state == IntakeArmState.OUT) {
      this._armMasterMotor.selectProfileSlot(0, 0);
    } else if (state == IntakeArmState.IN) {
      this._armMasterMotor.selectProfileSlot(1, 0);
    } else return;

    this.setArmPos(state.getPosition());
  }

  /**
   * Take position for the encoder and return IntakeArmState
   */
  private IntakeArmState degToArmState(double deg) {
    if (deg >= IntakeArmState.IN.getPosition() - this._armPosTolerance) return IntakeArmState.IN;
    else if (deg <= IntakeArmState.OUT.getPosition() + this._armPosTolerance) return IntakeArmState.OUT;
    return IntakeArmState.INTERMEDIATE;
  }

  /**
   * @return Arm's current position, measured by the encoder.
   * Range:
   *   IntakeArmState.IN._position == fully closed
   *   IntakeArmState.OUT._position == fully open
   */
  private double getArmPos() {
    return this._armMasterMotor.getDistance();
  }

  /**
   * Set arm position by encoder value
   * @param pos value between IntakeArmState.IN._position and IntakeArmState.OUT._position in degrees
   */
  private void setArmPos(double pos) {
    this._armMasterMotor.set(ControlMode.Position, pos);
  }

  /**
   * Reset's arm encoder to a given value.
   */
  private void resetArmPos(IntakeArmState state) {
    this._armMasterMotor.setDistance(state.getPosition());
  }

  /**
   * @return Current value of the arm's bottom hall effect.
   */
  private boolean getArmHallOut() {
    return !this._armHallDown.get();
  }

  /**
   * @return Current value of the arm's top hall effect.
   */
  private boolean getArmHallIn() {
    return !this._armHallUp.get();
  }

  /**
   * IntakeRollersState is built so that you can easily access the needed voltage in
   * each state for each roller For example IntakeRollersState.IN._bottomRollerVoltage
   * will return the voltage for the bottom roller motor when rolling a cargo in
   */
  public enum IntakeRollersState {
    IN, OUT, STOPPED;

    private double _rollerVoltage, _bottomRollerVoltage;

    public void setRollerVoltage(double voltage) {
      this._rollerVoltage = voltage;
    }

    public void setBottomRollerVoltage(double voltage) {
      this._bottomRollerVoltage = voltage;
    }

    public double getRollerVoltage() {
      return this._rollerVoltage;
    }

    public double getBottomRollerVoltage() {
      return this._bottomRollerVoltage;
    }
  }

  public void setRollersState(IntakeRollersState state) {
    setBothRollers(state.getRollerVoltage(), state.getBottomRollerVoltage());
  }

  public IntakeRollersState getRollerState() {
    if (this._rollerMotor.get() == IntakeRollersState.IN.getRollerVoltage())
      return IntakeRollersState.IN;
    else if (this._rollerMotor.get() == IntakeRollersState.OUT.getBottomRollerVoltage())
      return IntakeRollersState.OUT;
    // We haven't checked if the motors are actually stopped but the logic behind it
    // doesn't wllow it to be any other state.
    return IntakeRollersState.STOPPED;
  }

  /**
   * Set both rollers to a given value
   */
  private void setBothRollers(double rollerVoltage, double bottomRollerVoltage) {
    // Set _rollerMotor and _bottomRollerMotor's voltage
    this._rollerMotor.set(rollerVoltage);
    this._bottomRollerMotor.set(bottomRollerVoltage);
  }

  /**
   * @return Is there currently cargo inside CargoIntake
   */
  public boolean isCargoSecured() {
    return this._isCargoSecured;
  }

  @Override
  public void initDefaultCommand() {

  }

  public void setBrake(boolean status) {
    NeutralMode mode = status ? NeutralMode.Brake : NeutralMode.Coast;
    this._armMasterMotor.setNeutralMode(mode);
    this._armSlaveMotor.setNeutralMode(mode);
  }

  public void initRoutine() {
    this.setBrake(false);
  }

  public void setPrecntage(double precentage) {
    this._armMasterMotor.set(ControlMode.PercentOutput, precentage);
  }

  public void disabledRoutine() {
    this.setBrake(true);
    this._armMasterMotor.set(ControlMode.PercentOutput, 0.0);
  }

  @Override
  public void periodic() {
    // Reset arm when a hall effect is active
    // if (this.isArmOut()) this.resetArmPos(IntakeArmState.OUT._position);
    if (this.getArmHallOut()) this.resetArmPos(IntakeArmState.OUT);
    else if (this.getArmHallIn()) this.resetArmPos(IntakeArmState.IN);
  }

  
  public void displayTestData() {
    SmartDashboard.putNumber("Intake arm angle", this.getArmPos());
    SmartDashboard.putString("Intake arm state", this.getArmState().toString());
    SmartDashboard.putBoolean("Intake out HE", this.getArmHallOut());
    SmartDashboard.putBoolean("Intake in HE", this.getArmHallIn());
  }

  
  public void displayMatchData() {
    // TODO - Implement
  }

  
  public void displayCommands() {
    SmartDashboard.putData("Intake roll in", new CargoIntakeSetRollers(IntakeRollersState.IN));
    SmartDashboard.putData("Intake roll out", new CargoIntakeSetRollers(IntakeRollersState.OUT));
    SmartDashboard.putData("Intake stop rollers", new CargoIntakeSetRollers(IntakeRollersState.STOPPED));
    SmartDashboard.putData("Intake arm out", new CargoIntakeOpen());
    SmartDashboard.putData("Intake arm in", new CargoIntakeClose());
  }
}
