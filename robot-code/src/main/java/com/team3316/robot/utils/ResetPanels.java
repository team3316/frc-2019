package com.team3316.robot.utils;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToCollect;
import com.team3316.robot.commands.cargoIntake.CargoIntakeOpen;
import com.team3316.robot.commands.hp.SetPanelCollectorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * ResetPanels
 */
public class ResetPanels extends DBugCommandGroup {
  public ResetPanels() {
    addSequential(new CargoIntakeOpen());
    addSequential(new EjectorArmToCollect());
    addSequential(new InstantCommand(() -> {
      Robot.panelMechanism.resetEncoder(PanelMechanismState.STARTING_CONFIG);
    }));
    addSequential(new SetPanelCollectorState(PanelMechanismState.CLOSED));
  }
}