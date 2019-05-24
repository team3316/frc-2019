package com.team3316.robot.subsystems;

import java.io.IOException;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import com.team3316.kit.DBugSubsystem;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.kit.motors.DBugTalon;
import com.team3316.kit.motors.TalonType;
import com.team3316.kit.path.TalonTrajectoryFollower;
import com.team3316.kit.path.Trajectory;
import com.team3316.robot.commands.drivetrain.TankDrive;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI.Port;

/**
 * Drivetrain
 */
public class Drivetrain extends DBugSubsystem {
  /*
   * Actuators
   */
  private DBugTalon _rightMaster, _leftMaster, _rightSlave;
  private VictorSPX _leftSlave;

  /*
   * Sensors
   */
  private AHRS _navX;
  private double _yawOffset = 0.0;

  /**
   * Constructs a new Drivetrain instance.
   *
   * @throws ConfigException
   */
  public Drivetrain() throws ConfigException {
    // Actuators
    this._rightMaster = new DBugTalon((int) Config.getInstance().get("drivetrain.talonRF"), TalonType.CLOSED_LOOP_QUAD);
    this._rightSlave = new DBugTalon((int) Config.getInstance().get("drivetrain.talonRB"));
    this._leftMaster = new DBugTalon((int) Config.getInstance().get("drivetrain.talonLF"), TalonType.CLOSED_LOOP_QUAD);
    this._leftSlave = new VictorSPX((int) Config.getInstance().get("drivetrain.talonLB"));

    // Talon Configuration
    this._leftMaster.setInverted(false);
    this._leftSlave.setInverted(false);
    this._rightMaster.setInverted(true);
    this._rightSlave.setInverted(true);

    this._leftMaster.setSensorPhase(true);
    this._rightMaster.setSensorPhase(true);

    this._leftSlave.follow(this._leftMaster);
    this._rightSlave.follow(this._rightMaster);

    this._leftMaster.setDistancePerRevolution(4 * 2.54 * Math.PI / 100.0, 1024);
    this._rightMaster.setDistancePerRevolution(4 * 2.54 * Math.PI / 100.0, 1024);

    this.resetEncoders();

    // Sensors
    this._navX = new AHRS(Port.kMXP);

    this.resetAngle();
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new TankDrive());
  }

  /**
   * This method sets both of the motors.
   *
   * @param voltageL Recives a value between -1 and 1 and sets the value of the
   *                 left motors.
   * @param voltaeR  Recives a value between -1 and 1 and sets the value of the
   *                 right motors.
   */
  public void setMotors(double voltageL, double voltageR) {
    this._leftMaster.set(ControlMode.PercentOutput, voltageL);
    this._rightMaster.set(ControlMode.PercentOutput, voltageR);
  }

  /**
   * Sets the left motors to a given output percentage
   *
   * @param percentage The output percentage for the left motors, -1 <= percentage
   *                   <= 1
   */
  public void setLeftMotors(double percentage) {
    this._leftMaster.set(ControlMode.PercentOutput, percentage);
  }

  /**
   * Sets the right motors to a given output percentage
   *
   * @param percentage The output percentage for the right motors, -1 <=
   *                   percentage <= 1
   */
  public void setRightMotors(double percentage) {
    this._rightMaster.set(ControlMode.PercentOutput, percentage);
  }

  /**
   * Resets both of the drivetrain encoders.
   */
  public void resetEncoders() {
    this._leftMaster.setDistance(0);
    this._rightMaster.setDistance(0);
  }

  /**
   * Returns the distance travelled by the left of the robot.
   *
   * @return The left encoder value
   */
  public double getLeftDistance() {
    return this._leftMaster.getDistance();
  }

  /**
   * Returns the distance travelled by the right of the robot.
   *
   * @return The right encoder value
   */
  public double getRightDistance() {
    return this._rightMaster.getDistance();
  }

  /**
   * Returns the distance travelled by the center of the robot.
   *
   * @return The avrege of the left and right encoders.
   */
  public double getCenterDistance() {
    return (this.getLeftDistance() + this.getRightDistance()) / 2;
  }

  /**
   * This method resets the angle of the NavX.
   */
  public void resetAngle() {
    this._navX.reset();
  }

  /**
   * Returns the yaw angle of the robot.
   *
   * @return The yaw angle from the NavX
   */
  public double getAngle() {
    return this._navX.getYaw();
  }

  public void setBrake(boolean status) {
    NeutralMode mode = status ? NeutralMode.Brake : NeutralMode.Coast;
    this._rightMaster.setNeutralMode(mode);
    this._rightSlave.setNeutralMode(mode);
    this._leftMaster.setNeutralMode(mode);
    this._leftSlave.setNeutralMode(mode);
  }

  public TalonTrajectoryFollower getFollowerForPath(String pathname) throws IOException {
    Trajectory trajectory = Trajectory.fromFilename(pathname + ".csv");
    System.out.println("Trajectory size: " + trajectory.size());
    return new TalonTrajectoryFollower(this._leftMaster, this._rightMaster, this._navX, trajectory);
  }

  public PIDSource getAnglePIDSource() {
    return new PIDSource() {
      @Override
      public void setPIDSourceType(PIDSourceType pidSource) {
        // Nothin'
      }

      @Override
      public double pidGet() {
        return getAngle();
      }

      @Override
      public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
      }
    };
  }

  public PIDSource getLeftPIDSource() {
    return new PIDSource() {
      @Override
      public void setPIDSourceType(PIDSourceType pidSource) {
        // Nothin'
      }

      @Override
      public double pidGet() {
        return _leftMaster.getDistance();
      }

      @Override
      public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
      }
    };
  }

  public PIDSource getRightPIDSource() {
    return new PIDSource() {
      @Override
      public void setPIDSourceType(PIDSourceType pidSource) {
        // Nothin'
      }

      @Override
      public double pidGet() {
        return _rightMaster.getDistance();
      }

      @Override
      public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
      }
    };
  }

  
  public void displayTestData() {

  }

  
  public void displayMatchData() {

  }

  
  public void displayCommands() {

  }
}
