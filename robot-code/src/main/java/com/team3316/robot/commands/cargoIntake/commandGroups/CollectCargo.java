package com.team3316.robot.commands.cargoIntake.commandGroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToCollect;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoEjector.EjectorCollectCargo;
import com.team3316.robot.commands.cargoIntake.CargoIntakeClose;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.commands.cargoIntake.IntakeCollectCargo;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.commands.hp.SetPanelCollectorState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;

/**
 * CollectCargo
 */
public class CollectCargo extends DBugCommandGroup {
  public CollectCargo() throws ConfigException {
    if (Robot.cargoEjector.hasCargo() || Robot.panelMechanism.hasPanel()) end();

    addParallel(new ElevatorSetState(ElevatorState.BOTTOM));
    addSequential(new CargoIntakeOpen());
    addSequential(new EjectorArmToCollect());
    addSequential(new SetPanelCollectorState(PanelMechanismState.CLOSED));
    addParallel(new IntakeCollectCargo());
    addSequential(new EjectorCollectCargo());
    addSequential(new EjectorArmToEject());
    addSequential(new CargoIntakeClose());
  }
}
