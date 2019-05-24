package com.team3316.robot.commands.cargoEjector;

import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.CargoEjector.EjectorRollerState;

/**
 * EjectorCollectCargo
 */
public class EjectorCollectCargo extends DBugCommand {
  public EjectorCollectCargo() {
    // requires(Robot.cargoEjector);
  }

  @Override
  protected void init() {

  }

  @Override
  protected void execute() {
    Robot.cargoEjector.setRollerState(EjectorRollerState.IN);
  }

  @Override
  protected boolean isFinished() {
    return Robot.cargoEjector.hasCargo();
  }

  @Override
  protected void fin() {
    Robot.cargoEjector.setRollerState(EjectorRollerState.STOP);
  }

  @Override
  protected void interr() {
    this.fin();
  }
}
