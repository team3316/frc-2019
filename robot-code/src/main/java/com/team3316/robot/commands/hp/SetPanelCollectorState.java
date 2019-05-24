package com.team3316.robot.commands.hp;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * ArmUp
 */
public class SetPanelCollectorState extends DBugCommand {
  private PanelMechanismState _state;

  public SetPanelCollectorState(PanelMechanismState state) {
    requires(Robot.panelMechanism);
    this._state = state;
  }

  @Override
  protected void init() {
    try {
      Robot.panelMechanism.setArmState(this._state);
    } catch (InvalidStateException e) {
      DBugLogger.getInstance().severe(e);
      end();
    }
  }

  @Override
  protected void execute() {}

  @Override
  protected boolean isFinished() {
    return Robot.panelMechanism.getState() == this._state;
  }

  @Override
  protected void fin() {

  }

  @Override
  protected void interr() {

  }
}
