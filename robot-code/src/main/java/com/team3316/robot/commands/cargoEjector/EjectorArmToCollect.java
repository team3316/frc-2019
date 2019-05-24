package com.team3316.robot.commands.cargoEjector;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * EjectorArmToCollect
 */
public class EjectorArmToCollect extends DBugCommand {
  public EjectorArmToCollect() {
    requires(Robot.cargoEjector);
  }

  @Override
  protected void init() {
    try {
      Robot.cargoEjector.setArmState(EjectorArmState.COLLECT);
    } catch (InvalidStateException e) {
      DBugLogger.getInstance().severe(e);
      end();
	  }
  }

  @Override
  protected void execute() {

  }

  @Override
  protected boolean isFinished() {
    return Robot.cargoEjector.getArmState() == EjectorArmState.COLLECT;
  }

  @Override
  protected void fin() {

  }

  @Override
  protected void interr() {

  }
}
