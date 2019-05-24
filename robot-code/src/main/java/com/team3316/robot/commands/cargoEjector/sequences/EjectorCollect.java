package com.team3316.robot.commands.cargoEjector.sequences;

import com.team3316.kit.commands.DBugCommandGroup;
import com.team3316.robot.commands.cargoEjector.EjectorArmToCollect;
import com.team3316.robot.commands.cargoEjector.EjectorCollectCargo;

/**
 * EjectorCollect
 */
public class EjectorCollect extends DBugCommandGroup {
   public EjectorCollect() {
    addSequential(new EjectorArmToCollect());
    addSequential(new EjectorCollectCargo());
  }
}
