package com.team3316.robot.commands.subCommandGroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;

/**
 * ElevateToLevel
 */
public class ElevateToLevel extends DBugCommandGroup{
  public ElevateToLevel(ElevatorState level) {
    addSequential(new EjectorToEject());
    addSequential(new ElevatorSetState(level));
  }
}
