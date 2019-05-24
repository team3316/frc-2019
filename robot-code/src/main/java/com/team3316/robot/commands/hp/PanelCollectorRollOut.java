package com.team3316.robot.commands.hp;

 import com.team3316.kit.commands.DBugCommand;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.PanelMechanism.PanelRollerState;

 /**
 * Collect
 */
public class PanelCollectorRollOut extends DBugCommand {
  private long _lastFull, _delay;

   public PanelCollectorRollOut() throws ConfigException {
    requires(Robot.panelMechanism);
    this._delay = (long) Config.getInstance().get("panelEject.delay");
  }

   @Override
  protected void init() {
    this._lastFull = System.currentTimeMillis();
  }

   @Override
  protected void execute() {
    Robot.panelMechanism.setRoller(PanelRollerState.OUT);
  }

   @Override
  protected boolean isFinished() {
    if (Robot.panelMechanism.hasPanel()) this._lastFull = System.currentTimeMillis();
    long timeDelta = System.currentTimeMillis() - this._lastFull;
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
