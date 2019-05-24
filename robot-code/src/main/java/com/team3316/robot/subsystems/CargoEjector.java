package com.team3316.robot.subsystems;

import com.team3316.robot.subsystems.CargoIntake.IntakeArmState;
import com.team3316.robot.utils.InvalidStateException;
import com.team3316.robot.subsystems.PanelMechanism;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team3316.kit.DBugLogger;
import com.team3316.kit.DBugSubsystem;
import com.team3316.kit.config.ConfigException;
import com.team3316.kit.motors.DBugTalon;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToCollect;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoEjector.EjectorCollectCargo;
import com.team3316.robot.commands.cargoEjector.EjectorEjectCargo;
import com.team3316.kit.config.Config;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.team3316.kit.motors.TalonType;
/**
 * CGEjector
 */
public class CargoEjector extends DBugSubsystem {
  private VictorSP _ejectorMotor;
  //private DigitalInput _LSwitch, _RSwitch;
  private DigitalInput _switch;
  private DBugTalon _armTalon;
  private DigitalInput _collectHallEffect, _ejectHallEffect;
  private int _armPIDLoopIndex, _armPIDTimeout;
  private double _kArmTolerance;

  public CargoEjector() throws ConfigException {
    this._armPIDTimeout = (int) Config.getInstance().get("cargoEjector.arm.talon.ktimeout");
    this._armPIDLoopIndex = (int) Config.getInstance().get("cargoEjector.arm.talon.PIDLoopIndex");

    this._ejectorMotor = new VictorSP((int) Config.getInstance().get("cargoEjector.rollerMotorPort"));

    this._kArmTolerance = (double) Config.getInstance().get("cargoEjector.kArmTolerance");

    /*
     * Switches
     */
    this._switch = new DigitalInput((int) Config.getInstance().get("cargoEjector.switchPort"));
    //this._LSwitch = new DigitalInput((int) Config.getInstance().get("cargoEjector.LSwitchPort"));
    //this._RSwitch = new DigitalInput((int) Config.getInstance().get("cargoEjector.RSwitchPort"));

    /*
     * Hall Effects
     */
    this._ejectHallEffect = new DigitalInput((int) Config.getInstance().get("cargoEjector.arm.ejectHallEffectPort"));
    this._collectHallEffect = new DigitalInput(
        (int) Config.getInstance().get("cargoEjector.arm.collectHallEffectPort"));

    /*
     * talon configuration
     */
    this._armTalon = new DBugTalon((int) Config.getInstance().get("cargoEjector.arm.talon.port"),
        TalonType.CLOSED_LOOP_QUAD);

    this._armTalon.config_kF(this._armPIDLoopIndex, 0, this._armPIDTimeout);
    this._armTalon.config_kP(this._armPIDLoopIndex, (double) Config.getInstance().get("cargoEjector.arm.talon.kP"),
        this._armPIDTimeout);
    this._armTalon.config_kI(this._armPIDLoopIndex, (double) Config.getInstance().get("cargoEjector.arm.talon.kI"),
        this._armPIDTimeout);
    this._armTalon.config_kD(this._armPIDLoopIndex, (double) Config.getInstance().get("cargoEjector.arm.talon.kD"),
        this._armPIDTimeout);

    this._armTalon.setDistancePerRevolution((double) Config.getInstance().get("cargoEjector.arm.talon.DPR"),
        (int) Config.getInstance().get("cargoEjector.arm.talon.UPR"));

    this._armTalon.setInverted(true);
    this._armTalon.setSensorPhase(true);

    EjectorArmState.COLLECT.setAngle((double) Config.getInstance().get("cargoEjector.ArmState.COLLECT.pos"));
    EjectorArmState.EJECT.setAngle((double) Config.getInstance().get("cargoEjector.ArmState.EJECT.pos"));

    EjectorRollerState.IN.setVoltage((double) Config.getInstance().get("cargoEjector.EjectorRollerState.IN.voltage"));
    EjectorRollerState.OUT.setVoltage((double) Config.getInstance().get("cargoEjector.EjectorRollerState.OUT.voltage"));
    EjectorRollerState.STOP.setVoltage(0.0);
    // TODO: move to config
    this._armTalon.configVoltageCompSaturation(4);
    this._armTalon.enableVoltageCompensation(true);
  }


  @Override
  public void periodic() {
    if (!this._collectHallEffect.get())
      this.resetEncoder(EjectorArmState.COLLECT);
    else if (!this._ejectHallEffect.get())
      this.resetEncoder(EjectorArmState.EJECT);
  }

  public enum EjectorArmState {
    EJECT, COLLECT, INTERMEDIATE;

    private double _angle;

    public void setAngle(double angle) {
      this._angle = angle;
    }

    public double getAngle() {
      return this._angle;
    }
  }

  public enum EjectorRollerState {
    IN, OUT, STOP;

    private double _voltage;

    public void setVoltage(double voltage) {
      this._voltage = voltage;
    }

    public double getVoltage() {
      return this._voltage;
    }
  }

  public void setRollerState(EjectorRollerState state) {
    this._ejectorMotor.set(state.getVoltage());
  }

  public void setArmState(EjectorArmState state) throws InvalidStateException {
    if (state == EjectorArmState.INTERMEDIATE)
      throw new InvalidStateException("Cannot move ejector arm to intermediate");
    if (Robot.elevator.getState().isAboveBottom())
      throw new InvalidStateException("Cannot move ejector arm while elevator isn't down");
    if (state == EjectorArmState.COLLECT && Robot.cargoIntake.getArmState() == IntakeArmState.IN)
      throw new InvalidStateException("Cannot move arm to COLLECT if cargointake is IN");
    if (state == EjectorArmState.EJECT
        && Robot.panelMechanism.getState() == PanelMechanism.PanelMechanismState.INTERMEDIATE_CLOSED)
      throw new InvalidStateException("Cannot move arm to EJECT if panel mechainsm is between CLOSED and INSTALL");

    EjectorArmState currentState = this.getArmState();
    DBugLogger.getInstance().info("Changing Ejector arm state: " + currentState.toString() + " -> " + state.toString());
    this._armTalon.set(ControlMode.Position, state.getAngle());
  }

  @Override
  public void initDefaultCommand() {

  }

  public EjectorArmState getArmState() {
    if (this.getArmPos() >= EjectorArmState.EJECT.getAngle() - this._kArmTolerance)
      return EjectorArmState.EJECT;
    else if (this.getArmPos() <= EjectorArmState.COLLECT.getAngle() + this._kArmTolerance)
      return EjectorArmState.COLLECT;
    return EjectorArmState.INTERMEDIATE;
  }

  public double getArmPos() {
    return this._armTalon.getDistance();
  }

  public boolean hasCargo() {
    return !this._switch.get(); //this._LSwitch.get() || this._RSwitch.get();
  }

  private void resetEncoder(EjectorArmState state) {
    this._armTalon.setDistance(state.getAngle());
  }

  public void setBrake(boolean status) {
    NeutralMode mode = status ? NeutralMode.Brake : NeutralMode.Coast;
    this._armTalon.setNeutralMode(mode);
  }

  public void initRoutine() {
    this.setBrake(false);
  }

  public void disabledRoutine() {
    this.setBrake(true);
    this._armTalon.set(ControlMode.PercentOutput, 0.0);
  }

  
  public void displayTestData() {
    SmartDashboard.putNumber("Ejector arm angle", this.getArmPos());
    SmartDashboard.putString("Ejector arm state", this.getArmState().toString());
    SmartDashboard.putBoolean("Ejector collect HE", !this._collectHallEffect.get());
    SmartDashboard.putBoolean("Ejector eject HE", !this._ejectHallEffect.get());
    SmartDashboard.putBoolean("Ejector has cargo", this.hasCargo());
  }

  
  public void displayMatchData() {
    // TODO - Implement
  }

  
  public void displayCommands() {
    try {
      SmartDashboard.putData("Ejector roll in", new EjectorCollectCargo());
      SmartDashboard.putData("Ejector roll out", new EjectorEjectCargo());
      SmartDashboard.putData("Ejector arm to collect", new EjectorArmToCollect());
      SmartDashboard.putData("Ejector arm to eject", new EjectorArmToEject());
    } catch (ConfigException e) {
      DBugLogger.getInstance().severe(e);
    }
  }
}
