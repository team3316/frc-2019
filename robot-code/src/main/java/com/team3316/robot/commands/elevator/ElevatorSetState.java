package com.team3316.robot.commands.elevator;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * ElevatorSetState
 */
public class ElevatorSetState extends DBugCommand {
  private ElevatorState _wantedState;

  public ElevatorSetState(ElevatorState wanted) {
    requires(Robot.elevator);
    this._wantedState = wanted;
  }

  @Override
  protected void init() {
    try {
      Robot.elevator.setState(this._wantedState);
    } catch (InvalidStateException e) {
      DBugLogger.getInstance().severe(e);
      end();
    }
  }

  @Override
  protected void execute() {
    // Nothin'
  }

  @Override
  protected boolean isFinished() {
    return Robot.elevator.getState() == this._wantedState;
  }

  @Override
  protected void fin() {
    // Nothin'
  }

  @Override
  protected void interr() {
    this.fin();
  }
}
