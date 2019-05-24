package com.team3316.robot.commands.hp.commandGroups;



import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.commands.subCommandGroups.ElevateToLevel;
import com.team3316.robot.commands.subCommandGroups.PanelMechanismToState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * PreInstallHP
 */
public class PreInstallHP extends DBugCommandGroup{

  public PreInstallHP (ElevatorState target) throws InvalidStateException {
    if (!target.isHPInstallState()) {
      throw new InvalidStateException("you must install HP in a HP install elevator state"); 
    }


    addSequential(new PanelMechanismToState(PanelMechanismState.INSTALL));
    addSequential(new ElevateToLevel(target.getPreState()));
  }
  
}