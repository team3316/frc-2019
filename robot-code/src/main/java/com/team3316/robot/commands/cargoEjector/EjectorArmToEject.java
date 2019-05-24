package com.team3316.robot.commands.cargoEjector;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * EjectorArmToEject
 */
public class EjectorArmToEject extends DBugCommand {
  public EjectorArmToEject() {
    requires(Robot.cargoEjector);
  }

  @Override
  protected void init() {
    try {
      Robot.cargoEjector.setArmState(EjectorArmState.EJECT);
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
    return Robot.cargoEjector.getArmState() == EjectorArmState.EJECT;
  }

  @Override
  protected void fin() {

  }

  @Override
  protected void interr() {

  }
}
