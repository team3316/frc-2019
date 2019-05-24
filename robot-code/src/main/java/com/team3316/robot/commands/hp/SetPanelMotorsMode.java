package com.team3316.robot.commands.hp;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;

/**
 * SetPanelMotorsMode
 */
public class SetPanelMotorsMode extends DBugCommand{

  private NeutralMode _mode;

  public SetPanelMotorsMode(NeutralMode mode) {
    requires(Robot.panelMechanism);
    this._mode = mode;
  }

  @Override
  protected void init() {
    Robot.panelMechanism.setNeutralMode(this._mode);
  }

  @Override
  protected void execute() {}

  @Override
  protected boolean isFinished() {
    return true;
  }

  @Override
  protected void fin() {}

  @Override
  protected void interr() {}


}
