package com.team3316.robot.commands.hp.commandGroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.commands.hp.PanelCollectorRollIn;
import com.team3316.robot.commands.subCommandGroups.PanelMechanismToState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;

/**
 * CollectHP
 */
public class PrepareToCollectHP extends DBugCommandGroup {
  /**
   *
   * @param isFloor Choose if you want to collect from the floor or from the ls.
   *                True for floor, false for LS.
   *
   * @throws ConfigException
   */
  public PrepareToCollectHP(boolean isFloor) throws ConfigException {
    if (Robot.panelMechanism.hasPanel() || Robot.cargoEjector.hasCargo())
      end();

    addSequential(new ElevatorSetState(ElevatorState.BOTTOM));

    if (isFloor) {
      addParallel(new PanelMechanismToState(PanelMechanismState.FLOORCOLLECT));
    } else {
      addSequential(new PanelMechanismToState(PanelMechanismState.LSCOLLECT));
    }
    addSequential(new EjectorArmToEject());
  }
}
