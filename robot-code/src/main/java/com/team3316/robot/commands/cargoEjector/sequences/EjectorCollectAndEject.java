package com.team3316.robot.commands.cargoEjector.sequences;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;

/**
 * EjectorCollectAndEject
 */
public class EjectorCollectAndEject extends DBugCommandGroup {
   public EjectorCollectAndEject() throws ConfigException {
    addSequential(new EjectorCollect());
    addSequential(new EjectorEject());
  }
}
