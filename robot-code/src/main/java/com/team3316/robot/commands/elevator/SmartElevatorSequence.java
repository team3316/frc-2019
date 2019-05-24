package com.team3316.robot.commands.elevator;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoIntake.CargoIntakeClose;
import com.team3316.robot.subsystems.Elevator.ElevatorState;

/**
 * SmartElevatorSequence
 */
public class SmartElevatorSequence extends DBugCommandGroup {
  public SmartElevatorSequence(int elevatorLevel) {
    ElevatorState wantedState = ElevatorState.BOTTOM;
    if (Robot.cargoEjector.hasCargo()) {
      wantedState = ElevatorState.getCargoLevel(elevatorLevel);
    } else if (Robot.panelMechanism.hasPanel()) {
      wantedState = ElevatorState.getPanelsLevel(elevatorLevel);
    }

    addSequential(new EjectorArmToEject());
    addSequential(new CargoIntakeClose());
    addSequential(new ElevatorSetState(wantedState));
  }
}
