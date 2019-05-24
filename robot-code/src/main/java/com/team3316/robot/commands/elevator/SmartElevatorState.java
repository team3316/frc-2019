package com.team3316.robot.commands.elevator;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.commands.DBugCommand;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoIntake.CargoIntakeClose;
import com.team3316.robot.commands.hp.SetPanelCollectorState;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.subsystems.CargoIntake.IntakeArmState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;
import com.team3316.robot.utils.InvalidStateException;

/**
 * SmartElevatorState
 */
public class SmartElevatorState extends DBugCommand {
  private int _elevatorLevel = 0;
  private ElevatorState _wantedState;

  public SmartElevatorState(int elevatorLevel) {
    this._elevatorLevel = elevatorLevel;
  }

  @Override
  protected void init() {
    if (Robot.cargoEjector.hasCargo()) {
      this._wantedState = ElevatorState.getCargoLevel(this._elevatorLevel);
    } else if (Robot.panelMechanism.hasPanel()) {
      this._wantedState = ElevatorState.getPanelsLevel(this._elevatorLevel);
    } else {
      this._wantedState = ElevatorState.BOTTOM;
    }

    try {
      Robot.elevator.setState(this._wantedState);
    } catch (InvalidStateException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  protected void execute() {
    // Obligatory ugly code warning
    // TODO - CLEANUP!!!!!!!!
    try {
      // if (Robot.cargoEjector.hasCargo() && Robot.panelMechanism.getState() != PanelMechanismState.CLOSED) {
      //   Robot.panelMechanism.setArmState(PanelMechanismState.CLOSED);
      // }

      // if (Robot.cargoEjector.getArmState() != EjectorArmState.EJECT) {
      //   Robot.cargoEjector.setArmState(EjectorArmState.EJECT);
      // } else {
      //   if (Robot.cargoIntake.getArmState() != IntakeArmState.IN || this._wantedState == ElevatorState.BOTTOM) {
      //     Robot.cargoIntake.setArmState(IntakeArmState.IN);
      //   }

      //   boolean shouldSetElevator = (Robot.cargoEjector.hasCargo() && Robot.panelMechanism.getState() == PanelMechanismState.CLOSED)
      //     || Robot.panelMechanism.hasPanel()
      //     || this._wantedState == ElevatorState.BOTTOM;

      //   if (shouldSetElevator) {
          // Robot.elevator.setState(this._wantedState);
      //   }
      // }
    } catch (Exception e) {
      DBugLogger.getInstance().severe(e);
      end();
    }
  }

  @Override
  protected boolean isFinished() {
    return Robot.elevator.getState() == this._wantedState;
  }

  @Override
  protected void fin() {

  }

  @Override
  protected void interr() {

  }


}
