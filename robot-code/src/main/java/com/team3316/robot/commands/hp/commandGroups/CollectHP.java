package com.team3316.robot.commands.hp.commandGroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.hp.PanelCollectorRollIn;
import com.team3316.robot.commands.subCommandGroups.PanelMechanismToState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;

/**
 * PreCollectHP
 */
public class CollectHP extends DBugCommandGroup {
  public CollectHP() throws ConfigException {
    // addSequential(new ElevateToLevel(ElevatorState.BOTTOM));
    // addSequential(new PanelMechanismToState(PanelMechanismState.LSCOLLECT));
    // addSequential(new CargoIntakeClose());

    PanelMechanismState currentState = Robot.panelMechanism.getState();
    if (currentState == PanelMechanismState.LSCOLLECT) {
      addParallel(new PanelMechanismToState(PanelMechanismState.INSTALL));
      addSequential(new PanelCollectorRollIn());
    } else if (currentState == PanelMechanismState.FLOORCOLLECT) {
      addSequential(new PanelCollectorRollIn());
      addSequential(new PanelMechanismToState(PanelMechanismState.INSTALL));
    }
  }
}
