package com.team3316.robot.humanIO;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.team3316.kit.config.Config;
import com.team3316.robot.Robot;

/**
 * Updates all the variables that are in the SmartDashboard
 */
public class UpdateVariablesInConfig extends Command {
  public UpdateVariablesInConfig () {
    setRunWhenDisabled(true);
  }

  protected void initialize () {
    /*
     * Iterates over all of the entries in the map and replaces their value in the config from the SDB
     */
    Set<Entry<String, Class<?>>> variables = Robot.sdb.getVariablesInSDB();

    for (Map.Entry<String, Class<?>> entry : variables) {
      String key = entry.getKey();
      if (entry.getValue() == Double.class) {
        Config.getInstance().add(key, SmartDashboard.getNumber(key, 3316));
      } else if (entry.getValue() == Integer.class) {
        Config.getInstance().add(entry.getKey(), (int) SmartDashboard.getNumber(key, 3316));
      } else if (entry.getValue() == Boolean.class) {
        Config.getInstance().add(entry.getKey(), SmartDashboard.getBoolean(entry.getKey(), false));
      }
    }
  }

  protected void execute () {
  }

  protected boolean isFinished () {
    return true;
  }

  protected void end () {
  }

  protected void interrupted () {
  }
}
