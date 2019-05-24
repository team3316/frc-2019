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
import com.team3316.robot.commands.hp.PanelCollectorRollIn;
import com.team3316.robot.commands.hp.PanelCollectorRollOut;
import com.team3316.robot.commands.hp.SetPanelCollectorState;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.utils.InvalidStateException;
import com.team3316.robot.utils.ResetPanels;
import com.team3316.robot.utils.Utils;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO: add gear box ratio, mechanisem state real distances and angels in the getState() and in isAtTop() function.

/**
 * A subsystem for the Hatch Panel collection mechanism
 */
public class PanelMechanism extends DBugSubsystem {
  private DigitalInput _switchRight, _MSDown, _MSUp;// , _switchLeft;
  private DBugTalon _talon;
  private VictorSPX _motor;
  private AnalogPotentiometer _analogPotentiometer;

  /**
   * Represents the internal state of the panel collector
   */
  public enum PanelMechanismState {
    STARTING_CONFIG, FLOORCOLLECT, LSCOLLECT, INSTALL, CLOSED, INTERMEDIATE_CLOSED, INTERMEDIATE_OPEN,
    INTERMEDIATE_BELOW_FLOOR;

    private double _angle, _tolerance;

    public double getAngle() {
      return this._angle;
    }

    public double getTolerance() {
      return this._tolerance;
    }

    public void setAngle(double angle) {
      this._angle = angle;
    }

    public void setTolerance(double tolerance) {
      this._tolerance = tolerance;
    }
  }

  public enum PanelRollerState {
    IN, OUT, GENTLE_OUT, STOP;

    private double _percentage;

    public double getPercentage() {
      return this._percentage;
    }

    public void setPercentage(double speed) {
      this._percentage = speed;
    }
  }

  /**
   * Constructs a new PanelMechanisem. This should only be used once in the Robot
   * class.
   *
   * @throws ConfigException
   */
  public PanelMechanism() throws ConfigException {
    // this._switchLeft = new DigitalInput((int)
    // Config.getInstance().get("panelMechanism._switchLeft"));
    this._switchRight = new DigitalInput((int) Config.getInstance().get("panelMechanism._switchRight"));

    this._MSDown = new DigitalInput((int) Config.getInstance().get("panelMechanism._MSDown"));
    this._MSUp = new DigitalInput((int) Config.getInstance().get("panelMechanism._MSUp"));

    this._analogPotentiometer = new AnalogPotentiometer(
        (int) Config.getInstance().get("panelMechanism.potentiometer.input"),
        (double) Config.getInstance().get("panelMechanism.potentiometer.fullRange"),
        (double) Config.getInstance().get("panelMechanism.potentiometer.offset"));

    this._motor = new VictorSPX((int) Config.getInstance().get("panelMechanism.victor"));

    // Talon Configuration
    this._talon = new DBugTalon((int) Config.getInstance().get("panelMechanism.talon"), TalonType.CLOSED_LOOP_QUAD);
    this._talon.setDistancePerRevolution((double) Config.getInstance().get("panelMechanism.distancePerPulse.dpr"),
        (int) Config.getInstance().get("panelMechanism.distancePerPulse.upr"));

    // Limit voltage for _talon
    this._talon.configVoltageCompSaturation((double) Config.getInstance().get("panelMechanism.maxVoltage"));
    this._talon.enableVoltageCompensation(true);

    // Motion Magic
    this._talon.config_kF(0, (double) Config.getInstance().get("panelMechanism.kF"));
    this._talon.config_kP(0, (double) Config.getInstance().get("panelMechanism.kP"));
    this._talon.config_kI(0, (double) Config.getInstance().get("panelMechanism.kI"));
    this._talon.config_kD(0, (double) Config.getInstance().get("panelMechanism.kD"));

    // Panel Mechansim State
    PanelMechanismState.STARTING_CONFIG
        .setAngle((double) Config.getInstance().get("PanelMechanismState.STARTING_CONFIG.angle"));
    PanelMechanismState.STARTING_CONFIG
        .setTolerance((double) Config.getInstance().get("PanelMechanismState.STARTING_CONFIG.tolerance"));

    PanelMechanismState.FLOORCOLLECT
        .setAngle((double) Config.getInstance().get("PanelMechanismState.FLOORCOLLECT.angle"));
    PanelMechanismState.FLOORCOLLECT
        .setTolerance((double) Config.getInstance().get("PanelMechanismState.FLOORCOLLECT.tolerance"));

    PanelMechanismState.LSCOLLECT.setAngle((double) Config.getInstance().get("PanelMechanismState.LSCOLLECT.angle"));
    PanelMechanismState.LSCOLLECT
        .setTolerance((double) Config.getInstance().get("PanelMechanismState.LSCOLLECT.tolerance"));

    PanelMechanismState.INSTALL.setAngle((double) Config.getInstance().get("PanelMechanismState.INSTALL.angle"));
    PanelMechanismState.INSTALL
        .setTolerance((double) Config.getInstance().get("PanelMechanismState.INSTALL.tolerance"));

    PanelMechanismState.CLOSED.setAngle((double) Config.getInstance().get("PanelMechanismState.CLOSED.angle"));
    PanelMechanismState.CLOSED.setTolerance((double) Config.getInstance().get("PanelMechanismState.CLOSED.tolerance"));

    // Panel Roller State
    PanelRollerState.IN.setPercentage((double) Config.getInstance().get("PanelRollerState.IN.speed"));
    PanelRollerState.OUT.setPercentage((double) Config.getInstance().get("PanelRollerState.OUT.speed"));
    PanelRollerState.GENTLE_OUT.setPercentage((double) Config.getInstance().get("PanelRollerState.GENTLE_OUT.speed"));
    PanelRollerState.STOP.setPercentage((double) Config.getInstance().get("PanelRollerState.STOP.speed"));
  }

  @Override
  public void periodic() {
    if (!this._MSDown.get())
      resetEncoder(PanelMechanismState.STARTING_CONFIG);
    else if (!this._MSUp.get())
      resetEncoder(PanelMechanismState.CLOSED);
  }

  public void resetEncoder(PanelMechanismState state) {
    this._talon.setDistance(state.getAngle());
  }

  private void resetEncoder(double angle) {
    this._talon.setDistance(angle);
  }

  /**
   * get the state of the mechanism.
   *
   * @return the state of the mechanisem.
   */
  public PanelMechanismState getState() {
    if (!this._MSDown.get() || Utils.isInNeighborhood(PanelMechanismState.STARTING_CONFIG.getAngle(), this.getArmPos(),
        PanelMechanismState.STARTING_CONFIG.getTolerance()))
      return PanelMechanismState.STARTING_CONFIG;

    else if (Utils.isInNeighborhood(PanelMechanismState.FLOORCOLLECT.getAngle(), this.getArmPos(),
        PanelMechanismState.FLOORCOLLECT.getTolerance()))
      return PanelMechanismState.FLOORCOLLECT;

    else if (Utils.isInNeighborhood(PanelMechanismState.LSCOLLECT.getAngle(), this.getArmPos(),
        PanelMechanismState.LSCOLLECT.getTolerance()))
      return PanelMechanismState.LSCOLLECT;

    else if (Utils.isInNeighborhood(PanelMechanismState.INSTALL.getAngle(), this.getArmPos(),
        PanelMechanismState.INSTALL.getTolerance()))
      return PanelMechanismState.INSTALL;

    else if (!this._MSUp.get() || Utils.isInNeighborhood(PanelMechanismState.CLOSED.getAngle(), this.getArmPos(),
        PanelMechanismState.CLOSED.getTolerance()))
      return PanelMechanismState.CLOSED;

    if (this.getArmPos() > PanelMechanismState.INSTALL.getAngle())
      return PanelMechanismState.INTERMEDIATE_CLOSED;

    else if (this.getArmPos() > PanelMechanismState.FLOORCOLLECT.getAngle())
      return PanelMechanismState.INTERMEDIATE_OPEN;

    return PanelMechanismState.INTERMEDIATE_BELOW_FLOOR;
  }

  private double getArmPos() {
    return this._talon.getDistance();
  }

  /**
   * Determines whether an HP is inside the collector.
   *
   * @return The value of the limit switch inside the collector.
   */
  public boolean hasPanel() {
    return !this._switchRight.get(); // || !this._switchLeft.get();
  }

  /**
   * Sets where you want the arm to move.
   *
   * @param state what state you want the arm to be in. STARTING_CONFIG or
   *              FLOORCOLLECT or INSTALL or CLOSED.
   */
  public void setArmState(PanelMechanismState state) throws InvalidStateException {
    if (state == PanelMechanismState.STARTING_CONFIG || state == PanelMechanismState.INTERMEDIATE_OPEN
        || state == PanelMechanismState.INTERMEDIATE_CLOSED || state == PanelMechanismState.INTERMEDIATE_BELOW_FLOOR)
      throw new InvalidStateException("Cannot set arm to invalid state");
    if (this.hasPanel() && state == PanelMechanismState.CLOSED)
      throw new InvalidStateException("Cannot close arm when panel is in");

    if (Robot.cargoEjector.getArmState() != EjectorArmState.COLLECT && !(((this.getState() == PanelMechanismState.CLOSED
        || this.getState() == PanelMechanismState.INTERMEDIATE_CLOSED) && state == PanelMechanismState.CLOSED)
        || ((this.getState() != PanelMechanismState.CLOSED
            && this.getState() != PanelMechanismState.INTERMEDIATE_CLOSED) && state != PanelMechanismState.CLOSED)))
      throw new InvalidStateException("Cannot exit safe range when Shooter isn't in eject");

    this._talon.set(ControlMode.Position, state.getAngle());
  }

  /**
   * Sets the roller speed.
   *
   * @param state what state you want the roller to be in. IN or OUT or STOP.
   */
  public void setRoller(PanelRollerState state) {
    this._motor.set(ControlMode.PercentOutput, state.getPercentage());
  }

  /**
   *
   * @param mode The mode you want to set the rollers in. Can be either
   *             NeutralMode.Brake or NeutralMode.Coast or
   *             NeutralMode.EEPROMSetting.
   */
  public void setNeutralMode(NeutralMode mode) {
    this._motor.setNeutralMode(mode);
  }

  @Override
  public void initDefaultCommand() {

  }

  public void setBrake(boolean status) {
    NeutralMode mode = status ? NeutralMode.Brake : NeutralMode.Coast;
    this._talon.setNeutralMode(mode);
  }

  public void initRoutine() {
    this.setBrake(false);
  }

  public void disabledRoutine() {
    this.setBrake(true);
    this._talon.set(ControlMode.PercentOutput, 0.0);
  }

  public void displayTestData() {
    SmartDashboard.putNumber("Panels arm angle", this.getArmPos());
    SmartDashboard.putString("Panels arm state", this.getState().toString());
    SmartDashboard.putBoolean("Panels SC HE", !this._MSDown.get());
    SmartDashboard.putBoolean("Panels Closed HE", !this._MSUp.get());
    SmartDashboard.putBoolean("Panels has panel", this.hasPanel());
  }

  public void displayMatchData() {
    // TODO - Implement
  }

  public void displayCommands() {
    try {
      SmartDashboard.putData("Reset panels", new ResetPanels());
      SmartDashboard.putData("Panels roll in", new PanelCollectorRollIn());
      SmartDashboard.putData("Panels roll out", new PanelCollectorRollOut());
      SmartDashboard.putData("Panels arm to CLOSED", new SetPanelCollectorState(PanelMechanismState.CLOSED));
      SmartDashboard.putData("Panels arm to LS", new SetPanelCollectorState(PanelMechanismState.LSCOLLECT));
      SmartDashboard.putData("Panels arm to FLOOR", new SetPanelCollectorState(PanelMechanismState.FLOORCOLLECT));
      SmartDashboard.putData("Panels arm to INSTALL", new SetPanelCollectorState(PanelMechanismState.INSTALL));
    } catch (ConfigException e) {
      DBugLogger.getInstance().severe(e);
    }
  }
}
