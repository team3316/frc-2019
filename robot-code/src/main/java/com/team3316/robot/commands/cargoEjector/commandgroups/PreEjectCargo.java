package com.team3316.robot.commands.cargoEjector.commandgroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.commands.subCommandGroups.EjectorToEject;
import com.team3316.robot.commands.subCommandGroups.ElevateToLevel;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * PreEjectCargo
 */
public class PreEjectCargo extends DBugCommandGroup {

  public PreEjectCargo(ElevatorState target) throws InvalidStateException {
    if (!target.isCargoInstallState()) {
      throw new InvalidStateException("you must install cargo in a cargo install elevator state");
    }

    addSequential(new EjectorToEject());
    addSequential(new ElevateToLevel(target));
  }

}
