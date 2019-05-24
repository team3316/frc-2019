package com.team3316.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * TalonCommandBuilder
 */
public class TalonCommandBuilder {
  /*
   * The TalonSRX instance to use in the built command
   */
  private TalonSRX _controller;

  /*
   * The motion's setpoint and PID constants
   */
  private double _setpoint, _kP, _kI, _kD, _kF = 0.0;

  /*
   * The motion control mode. Currently supported - position, velocity
   */
  private ControlMode _mode;

  /*
   * The PID slot to use in the Talon. Default = 0
   */
  private int _pidSlot = 0;

  /*
   * The subsystem that will be required by the command
   */
  private Subsystem _subsystem;

  /*
   * TalonSRX configuration timeout
   */
  private static final int kTimeout = 10;

  public TalonCommandBuilder(Subsystem subsystem) {
    this._subsystem = subsystem;
  }

  /**
   * Sets the new command's TalonSRX controller object.
   *
   * @param mc The wanted talon to use for the command
   * @return The current builder instance
   */
  public TalonCommandBuilder setMotorController(TalonSRX mc) {
    this._controller = mc;
    return this;
  }

  /**
   * Sets the new command's setpoint.
   *
   * @param sp The wanted setpoint to use for the command
   * @return The current builder instance
   */
  public TalonCommandBuilder setSetpoint(double sp) {
    this._setpoint = sp;
    return this;
  }

  /**
   * Sets the new command's PID constants.
   *
   * @param kP The wanted kP to use for the command
   * @param kI The wanted kI to use for the command
   * @param kD The wanted kD to use for the command
   * @return The current builder instance
   */
  public TalonCommandBuilder setPIDConstants(double kP, double kI, double kD) {
    this._kP = kP;
    this._kI = kI;
    this._kD = kD;
    return this;
  }

  /**
   * Sets the new command's control mode.
   *
   * @param sp The wanted control mode to use for the command. Currently only
   *           position and velocity modes are supported.
   * @return The current builder instance
   */
  public TalonCommandBuilder setControlMode(ControlMode mode) throws Exception {
    if (mode != ControlMode.Position && mode != ControlMode.Velocity) {
      throw new Exception("Only position and velocity control is supported at the moment.");
    }

    this._mode = mode;
    return this;
  }

  public TalonCommandBuilder setPIDSlot(int slotIdx) {
    this._pidSlot = slotIdx;
    return this;
  }

  public Command build() {
    return new InstantCommand(this._subsystem, () -> {
      this._controller.config_kP(_pidSlot, _kP, TalonCommandBuilder.kTimeout);
      this._controller.config_kI(_pidSlot, _kI, TalonCommandBuilder.kTimeout);
      this._controller.config_kD(_pidSlot, _kD, TalonCommandBuilder.kTimeout);
      this._controller.config_kF(_pidSlot, _kF, TalonCommandBuilder.kTimeout);

      this._controller.set(_mode, _setpoint);
    });
  }
}
