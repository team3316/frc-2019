package com.team3316.robot.commands.drivetrain;

import com.team3316.robot.Robot;
import com.team3316.kit.commands.DBugCommand;

/**
 * TankDrive Command - used to move the robot with joysticks
 */
public class TankDrive extends DBugCommand {
  public TankDrive() {
    requires(Robot.drivetrain);
  }

  @Override
  protected void init() {

  }

  /**
   * Sets motors value to Joysticks' Y axis
   */
  @Override
  protected void execute() {
    double leftY = Robot.joysticks.getLeftY();
    double rightY = Robot.joysticks.getRightY();
    Robot.drivetrain.setMotors(leftY, rightY);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void fin() {
    Robot.drivetrain.setMotors(0, 0);
  }

  @Override
  protected void interr() {
    this.fin();
  }
}
