package com.team3316.robot.commands.hp.commandGroups;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.commands.hp.PanelCollectorGentleRollOut;
import com.team3316.robot.commands.hp.SetPanelCollectorState;
import com.team3316.robot.commands.hp.SetPanelMotorsMode;
import com.team3316.robot.commands.subCommandGroups.ElevateToLevel;
import com.team3316.robot.commands.subCommandGroups.PanelMechanismToState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * InstallHP
 */
public class InstallHP extends DBugCommandGroup {
  public InstallHP(ElevatorState target) throws InvalidStateException, ConfigException {
    if (!target.isHPInstallState()) {
      throw new InvalidStateException("you must install HP in a HP install elevator state");
    }

    if (target != Robot.elevator.getState()) {
      addParallel(new ElevateToLevel(target));
    }

    addParallel(new PanelCollectorGentleRollOut());
    addSequential(new ElevatorSetState(target.getPreState()));
  }
}
