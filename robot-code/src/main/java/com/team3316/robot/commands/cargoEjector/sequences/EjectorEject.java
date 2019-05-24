package com.team3316.robot.commands.cargoEjector.sequences;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.kit.config.ConfigException;
import com.team3316.robot.commands.cargoEjector.EjectorArmToEject;
import com.team3316.robot.commands.cargoEjector.EjectorEjectCargo;

/**
 * EjectorEject
 */
public class EjectorEject extends DBugCommandGroup {
   public EjectorEject() throws ConfigException {
    addSequential(new EjectorArmToEject());
    addSequential(new EjectorEjectCargo());
  }
}
