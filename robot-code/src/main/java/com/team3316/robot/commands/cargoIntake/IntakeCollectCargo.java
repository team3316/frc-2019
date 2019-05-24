
package com.team3316.robot.commands.cargoIntake;

import com.team3316.kit.commands.DBugCommand;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.CargoIntake.IntakeRollersState;

/**
 * IntakeCollectCargo
 */
public class IntakeCollectCargo extends DBugCommand {
  private double _precentage;

  public IntakeCollectCargo() throws ConfigException {
    requires(Robot.cargoIntake);
    this._precentage = (double) Config.getInstance().get("cargoIntake.hold.voltage");
  }

  @Override
  protected void init() {
    // Nothin'
  }

  @Override
  protected void execute() {
    Robot.cargoIntake.setRollersState(IntakeRollersState.IN);
    Robot.cargoIntake.setPrecntage(this._precentage);
  }

  @Override
  protected boolean isFinished() {
    return Robot.cargoEjector.hasCargo();

  }

  @Override
  protected void fin() {
    Robot.cargoIntake.setRollersState(IntakeRollersState.STOPPED);
    Robot.cargoIntake.setPrecntage(0.0);
  }

  @Override
  protected void interr() {
    this.fin();
  }
}
