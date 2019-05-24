/**
 * Class for managing the SmartDashboard data
 */
package com.team3316.robot.humanIO;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimerTask;

import com.team3316.kit.DBugLogger;
import com.team3316.kit.config.Config;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.Robot;
import com.team3316.robot.commands.InstallGameObject;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoEjector.EjectorEjectCargo;
import com.team3316.robot.commands.cargoEjector.commandgroups.EjectCargo;
import com.team3316.robot.commands.cargoIntake.commandGroups.CollectCargo;
import com.team3316.robot.commands.cargoIntake.commandGroups.PreCollectCargo;
import com.team3316.robot.commands.elevator.ElevatorSetState;
import com.team3316.robot.commands.elevator.SmartElevatorSequence;
import com.team3316.robot.commands.elevator.SmartElevatorState;
import com.team3316.robot.commands.hp.commandGroups.CollectHP;
import com.team3316.robot.commands.hp.commandGroups.InstallHP;
import com.team3316.robot.commands.hp.commandGroups.PrepareToCollectHP;
import com.team3316.robot.subsystems.CargoEjector.EjectorArmState;
import com.team3316.robot.subsystems.CargoIntake.IntakeArmState;
import com.team3316.robot.subsystems.Elevator.ElevatorState;
import com.team3316.robot.subsystems.PanelMechanism.PanelMechanismState;

public class SDB {
  /*
   * Runnable that periodically updates values from the robot into the
   * SmartDashboard This is the place where all of the robot data should be
   * displayed from
   */
  private class UpdateSDBTask extends TimerTask {
    public UpdateSDBTask() {
      DBugLogger.getInstance().info("Created UpdateSDBTask");
    }

    public void run() {
      /*
       * Insert put methods here
       */

      put("Robot State", this.getRobotState());

      // Robot.elevator.displayTestData();
      // Robot.panelMechanism.displayTestData();
      // Robot.cargoEjector.displayTestData();
      // Robot.cargoIntake.displayTestData();
    }

    private String getRobotState() {
      // Ugly code ahead! use for testing purposes only

      // Collect Cargo
      if (checkRobotState(IntakeArmState.OUT, EjectorArmState.COLLECT, true, false))
        return "Collect Cargo";
      // Eject Cargo
      else if (checkRobotState(IntakeArmState.IN, EjectorArmState.EJECT, true, true))
        return "Eject Cargo";
      // Pre-Collect Cargo
      else if (checkRobotState(IntakeArmState.IN, EjectorArmState.EJECT, true, false))
        return "Pre-Collect Cargo";
      // Install HP
      else if (checkRobotState(IntakeArmState.IN, EjectorArmState.EJECT, false, false))
        return "Install HP";
      // Collect HP
      else if (checkRobotState(IntakeArmState.IN, EjectorArmState.EJECT, false, false))
        return "Collect HP";
      // Other
      else
        return "Other";
    }

    private Boolean checkRobotState(IntakeArmState intake, EjectorArmState ejector, boolean panelClosed,
        boolean elevatorAboveBottom) {
      return Robot.cargoIntake.getArmState() == intake && Robot.cargoEjector.getArmState() == ejector
          && panelClosed == (Robot.panelMechanism.getState() == PanelMechanismState.CLOSED
              || Robot.panelMechanism.getState() == PanelMechanismState.INTERMEDIATE_CLOSED)
          && Robot.elevator.getState().isAboveBottom() == elevatorAboveBottom;
    }

    private void put(String name, double d) {
      SmartDashboard.putNumber(name, d);
    }

    private void put(String name, int i) {
      SmartDashboard.putNumber(name, i);
    }

    private void put(String name, boolean b) {
      SmartDashboard.putBoolean(name, b);
    }

    private void put(String name, String s) {
      SmartDashboard.putString(name, s);
    }
  }

  private UpdateSDBTask updateSDBTask;

  private Hashtable<String, Class<?>> variablesInSDB;

  public SDB() throws Exception {
    variablesInSDB = new Hashtable<String, Class<?>>();

    initSDB();
    timerInit();
  }

  public void timerInit() {
    updateSDBTask = new UpdateSDBTask();
    Robot.timer.schedule(updateSDBTask, 0, 100);
  }

  /**
   * Adds a certain key in the config to the SmartDashboard
   *
   * @param key the key required
   * @return whether the value was put in the SmartDashboard
   */
  public boolean putConfigVariableInSDB(String key) {
    try {
      Object value = Config.getInstance().get(key);
      Class<?> type = value.getClass();

      boolean constant = Character.isUpperCase(key.codePointAt(0));

      if (type == Double.class) {
        SmartDashboard.putNumber(key, (double) value);
      } else if (type == Integer.class) {
        SmartDashboard.putNumber(key, (int) value);
      } else if (type == Boolean.class) {
        SmartDashboard.putBoolean(key, (boolean) value);
      }

      if (!constant) {
        variablesInSDB.put(key, type);
        DBugLogger.getInstance().info("Added to SDB " + key + " of type " + type + "and allows for its modification");
      } else {
        DBugLogger.getInstance()
            .info("Added to SDB " + key + " of type " + type + "BUT DOES NOT ALLOW for its modification");
      }

      return true;
    } catch (ConfigException e) {
      DBugLogger.getInstance().severe(e);
    }
    return false;
  }

  public Set<Entry<String, Class<?>>> getVariablesInSDB() {
    return variablesInSDB.entrySet();
  }

  private void initSDB() throws Exception {
    SmartDashboard.putData(new UpdateVariablesInConfig()); // NEVER REMOVE THIS COMMAND

    SmartDashboard.putData("Collect Cargo", new CollectCargo());
    SmartDashboard.putData("[PRE] CHP-LS", new PrepareToCollectHP(false));
    SmartDashboard.putData("CHP-FLR", new PrepareToCollectHP(true));
    SmartDashboard.putData("CHP-LS", new CollectHP());
    SmartDashboard.putData("ELV-BOTTOM", new InstantCommand(() -> {
      (new SmartElevatorSequence(0)).start();
    }));
    SmartDashboard.putData("SMART-ELV-1", new InstantCommand(() -> {
      (new SmartElevatorSequence(1)).start();
    }));
    SmartDashboard.putData("SMART-ELV-2", new InstantCommand(() -> {
      (new SmartElevatorSequence(2)).start();
    }));
    SmartDashboard.putData("SMART-ELV-3", new InstantCommand(() -> {
      (new SmartElevatorSequence(3)).start();
    }));
    SmartDashboard.putData("Install Game Object", new InstallGameObject());

    // SmartDashboard.putData("Go to level 1", new
    // ElevatorSetState(ElevatorState.LVL1_HP));
    // SmartDashboard.putData("Ejector Rollout", new EjectorEjectCargo());
    // SmartDashboard.putData("Panel to LSCOLLECT", new
    // PanelMechanismToState(PanelMechanismState.LSCOLLECT));
    // SmartDashboard.putData("Panel to INSTALL", new
    // PanelMechanismToState(PanelMechanismState.INSTALL));

    // Robot.elevator.displayCommands();
    // Robot.panelMechanism.displayCommands();
    // Robot.cargoEjector.displayCommands();
    // Robot.cargoIntake.displayCommands();

    DBugLogger.getInstance().info("Finished initSDB()");
  }
}
