package com.team3316.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * RobotEntry
 */
public class RobotEntry {
  private RobotEntry() {}

  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}
