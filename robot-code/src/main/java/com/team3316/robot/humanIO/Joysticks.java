/**
 * Class for joysticks and joystick buttons
 */
package com.team3316.robot.humanIO;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.commands.InstallGameObject;
import com.team3316.robot.commands.cargoIntake.commandGroups.CollectCargo;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.commands.elevator.SmartElevatorSequence;
import com.team3316.robot.commands.elevator.SmartElevatorState;
import com.team3316.robot.commands.hp.commandGroups.CollectHP;
import com.team3316.robot.commands.hp.commandGroups.CollectHPFloor;
import com.team3316.robot.commands.hp.commandGroups.PrepareToCollectHP;
import com.team3316.robot.subsystems.Elevator.ElevatorState;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class Joysticks {
  /*
   * Defines a button in a gamepad POV for an array of angles
   */
  private class POVButton extends Button {
    Joystick m_joystick;
    int m_deg;

    public POVButton(Joystick joystick, int deg) {
      m_joystick = joystick;
      m_deg = deg;
    }

    public boolean get() {
      if (m_joystick.getPOV() == m_deg) {
        return true;
      }
      return false;
    }
  }

  private Joystick _leftJoystick, _rightJoystick, _operatorJoystick;

  public Joysticks () throws ConfigException {
    this._leftJoystick = new Joystick((int) Config.getInstance().get("joysticks.left"));
    this._rightJoystick = new Joystick((int) Config.getInstance().get("joysticks.right"));
    this._operatorJoystick = new Joystick((int) Config.getInstance().get("joysticks.operator"));
  }

  public double getLeftY() {
    return this._leftJoystick.getY();
  }

  public double getRightY() {
    return this._rightJoystick.getY();
  }

  public void initButtons() throws ConfigException {
    JoystickButton collectCG = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.collectCG"));
    JoystickButton collectPanelFloor = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.collectPanelFloor"));
    JoystickButton preparePanelCollectLS = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.preparePanelCollectLS"));
    JoystickButton collectPanelLS = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.collectPanelLS"));
    JoystickButton install = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.install"));

    JoystickButton elevatorToBottom = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.elevatorToBottom"));
    JoystickButton elevatorToLvl_1 = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.elevatorToLvl_1"));
    JoystickButton elevatorToLvl_2 = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.elevatorToLvl_2"));
    JoystickButton elevatorToLvl_3 = new JoystickButton(this._operatorJoystick, (int) Config.getInstance().get("joysticks.operator.buttons.elevatorToLvl_3"));

    collectCG.whenPressed(new CollectCargo());
    collectPanelFloor.whenPressed(new CollectHPFloor());
    preparePanelCollectLS.whenPressed(new PrepareToCollectHP(false));
    collectPanelLS.whenPressed(new CollectHP());
    install.whenPressed(new InstallGameObject());

    elevatorToBottom.whenPressed(new InstantCommand(() -> {
      (new SmartElevatorSequence(0)).start();
    }));
    elevatorToLvl_1.whenPressed(new InstantCommand(() -> {
      (new SmartElevatorSequence(1)).start();
    }));
    elevatorToLvl_2.whenPressed(new InstantCommand(() -> {
      (new SmartElevatorSequence(2)).start();
    }));
    elevatorToLvl_3.whenPressed(new InstantCommand(() -> {
      (new SmartElevatorSequence(3)).start();
    }));
  }
}
