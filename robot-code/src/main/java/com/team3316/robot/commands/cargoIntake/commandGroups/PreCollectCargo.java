package com.team3316.robot.commands.cargoIntake.commandGroups;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.commands.cargoIntake.CargoIntakeClose;
import com.team3316.robot.commands.subCommandGroups.EjectorToEject;
import com.team3316.robot.commands.subCommandGroups.PanelMechanismToState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * PreCollectCargo
 */
public class PreCollectCargo extends DBugCommandGroup {
  public PreCollectCargo() {
    addSequential(new PanelMechanismToState(PanelMechanismState.CLOSED));
    addSequential(new EjectorToEject());

    addSequential(new CargoIntakeClose());
  }
}
