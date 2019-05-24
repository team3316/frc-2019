package com.team3316.robot.commands.subCommandGroups;

import com.team3316.kit.commands.DBugCommand;

import edu.wpi.first.wpilibj.command.Command;

/**
 * ActivateTwoCommands
 */
public class ActivateTwoCommands extends DBugCommand {
  private Command _cmd1, _cmd2;

  public ActivateTwoCommands(Command cmd1, Command cmd2) {
    this._cmd1 = cmd1;
    this._cmd2 = cmd2;
  }

  @Override
  protected void init() {
    this._cmd1.start();
    this._cmd2.start();
  }

  @Override
  protected void execute() {
    // Nothin'
  }

  @Override
  protected boolean isFinished() {
    return this._cmd1.isCompleted() && this._cmd2.isCompleted();
  }

  @Override
  protected void fin() {
    // Nothin'
  }

  @Override
  protected void interr() {
    this._cmd1.cancel();
    this._cmd2.cancel();
  }
}
