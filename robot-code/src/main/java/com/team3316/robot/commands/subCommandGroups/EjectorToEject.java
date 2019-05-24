package com.team3316.robot.commands.subCommandGroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoIntake.CargoIntakeClose;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;

/**
 * EjectorToEject
 */
public class EjectorToEject extends DBugCommandGroup {
  public EjectorToEject() {
    // if (Robot.cargoEjector.getArmState() != EjectorArmState.EJECT) {
      addSequential(new ActivateTwoCommands(new CargoIntakeOpen(), new ElevatorSetState(ElevatorState.BOTTOM)));
      addSequential(new EjectorArmToEject());
      addParallel(new CargoIntakeClose());
    // }
  }
}
