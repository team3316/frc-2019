package com.team3316.robot.commands.subCommandGroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToCollect;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoIntake.CargoIntakeClose;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.commands.hp.SetPanelCollectorState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;

/**
 * PanelMechanismToInstall
 */
public class PanelMechanismToState extends DBugCommandGroup {
  public PanelMechanismToState(PanelMechanismState state) {
    if (state == Robot.panelMechanism.getState())
      end();

    if (Robot.elevator.getState().isAboveBottom()) {
      addSequential(new ElevatorSetState(ElevatorState.BOTTOM));
    }

    addSequential(new CargoIntakeOpen());
    addSequential(new EjectorArmToCollect());
    addSequential(new SetPanelCollectorState(state));
    // addParallel(new CargoIntakeOpen());
    // addSequential(new ElevatorSetState(ElevatorState.BOTTOM));
    // addSequential(new EjectorArmToEject());
    // addSequential(new CargoIntakeClose());
  }
}
