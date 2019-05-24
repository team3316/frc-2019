package com.team3316.robot.commands.hp.commandGroups;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;

/**
 * CollectHPFloor
 */
public class CollectHPFloor extends DBugCommandGroup {

  public CollectHPFloor() throws ConfigException {
    addSequential(new PrepareToCollectHP(true));
    addSequential(new CollectHP());
  }

}
