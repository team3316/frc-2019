package com.team3316.robot.commands.cargoEjector.commandgroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoEjector.EjectorEjectCargo;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * EjectCargo
 */
public class EjectCargo extends DBugCommandGroup {
  public EjectCargo(ElevatorState target) throws ConfigException, InvalidStateException {
    if (!target.isCargoInstallState()) {
      throw new InvalidStateException("you must install cargo in a cargo install elevator state");
    }

    if (!Robot.cargoEjector.hasCargo()) end();

    if (Robot.cargoEjector.getArmState() != EjectorArmState.EJECT) {
      addParallel(new CargoIntakeOpen());
      addSequential(new ElevatorSetState(ElevatorState.BOTTOM));
      addSequential(new EjectorArmToEject());
    }

    if (Robot.elevator.getState() != target) {
      addSequential(new ElevatorSetState(target));
    }

    addSequential(new EjectorEjectCargo());
  }
}
