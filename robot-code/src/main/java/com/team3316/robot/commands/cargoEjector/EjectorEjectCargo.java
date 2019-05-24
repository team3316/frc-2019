package com.team3316.robot.commands.cargoEjector;

import com.team3316.kit.commands.DBugCommand;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.CargoEjector.EjectorRollerState;

/**
 * EjectorEjectCargo
 */
public class EjectorEjectCargo extends DBugCommand {
  private long _lastHadCargo, _delay;

  public EjectorEjectCargo() throws ConfigException {
    requires(Robot.cargoEjector);
    this._delay = (long) Config.getInstance().get("ejectCargo.delay");
  }

  @Override
  protected void init() {
    this._lastHadCargo = System.currentTimeMillis();
  }

  @Override
  protected void execute() {
    Robot.cargoEjector.setRollerState(EjectorRollerState.OUT);
  }

  @Override
  protected boolean isFinished() {
    if (Robot.cargoEjector.hasCargo()) this._lastHadCargo = System.currentTimeMillis();
    long timeDelta = System.currentTimeMillis() - this._lastHadCargo;
    return timeDelta > this._delay && !Robot.cargoEjector.hasCargo();
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
