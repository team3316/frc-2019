package com.team3316.robot.commands.subCommandGroups;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToCollect;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;

/**
 * EjectorToCollect
 */
public class EjectorToCollect extends DBugCommandGroup {
  public EjectorToCollect() {
    // if (Robot.cargoEjector.getArmState() != EjectorArmState.COLLECT) {
      // addSequential(new ActivateTwoCommands(new CargoIntakeOpen(), new ElevateToLevel(ElevatorState.BOTTOM)));
      // addSequential(new EjectorWaitForState(EjectorArmState.COLLECT));
      addSequential(new CargoIntakeOpen());
      addSequential(new ElevateToLevel(ElevatorState.BOTTOM));
      addSequential(new EjectorArmToCollect());
    // }
  }
}
