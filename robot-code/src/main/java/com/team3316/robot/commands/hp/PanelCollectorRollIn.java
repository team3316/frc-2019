package com.team3316.robot.commands.hp;

import com.team3316.kit.commands.DBugCommand;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.PanelMechanism.PanelRollerState;

/**
 * Collect
 */
public class PanelCollectorRollIn extends DBugCommand {
  private long _lastEmpty, _delay;

  public PanelCollectorRollIn() throws ConfigException {
    // requires(Robot.panelMechanism);
    this._delay = (long) Config.getInstance().get("panelCollect.delay");
  }

  @Override
  protected void init() {
    this._lastEmpty = System.currentTimeMillis();
  }

  @Override
  protected void execute() {
    Robot.panelMechanism.setRoller(PanelRollerState.IN);
  }

  @Override
  protected boolean isFinished() {
    if (!Robot.panelMechanism.hasPanel()) this._lastEmpty = System.currentTimeMillis();
    long timeDelta = System.currentTimeMillis() - this._lastEmpty;
    return timeDelta > this._delay;
  }

  @Override
  protected void fin() {
    Robot.panelMechanism.setRoller(PanelRollerState.STOP);
  }

  @Override
  protected void interr() {
    this.fin();
  }
}
