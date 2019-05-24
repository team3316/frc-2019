package com.team3316.robot.commands.cargoIntake;

 import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;
import com.team3316.robot.subsystems.CargoIntake.IntakeRollersState;

 /**
 * cargoIntakeSetRoller
 */
public class CargoIntakeSetRollers extends DBugCommand{
  private IntakeRollersState _state;

  public CargoIntakeSetRollers(IntakeRollersState state) {
    requires(Robot.cargoIntake);
    this._state = state;
  }

   @Override
  protected void init() {
    // Nothin'
  }

   @Override
  protected void execute() {
    Robot.cargoIntake.setRollersState(this._state);
  }

   @Override
  protected boolean isFinished() {
    switch (this._state) {
      case IN:
        return Robot.cargoEjector.hasCargo();
      default:
        return true;
    }
  }

   @Override
  protected void fin() {
    Robot.cargoIntake.setRollersState(IntakeRollersState.STOPPED);
  }

   @Override
  protected void interr() {
    this.fin();
  }

}
