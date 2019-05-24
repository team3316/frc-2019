package com.team3316.config;

import java.util.Hashtable;

public class Config {
  public static Hashtable<String, Object> variablesB;
  public static Hashtable<String, Object> constantsB;

  public static Hashtable<String, Object> variablesA;
  public static Hashtable<String, Object> constantsA;

  static {
    variablesB = new Hashtable<String, Object>();
    constantsB = new Hashtable<String, Object>();

    variablesA = new Hashtable<String, Object>();
    constantsA = new Hashtable<String, Object>();

    initConfig();
    IO.initIO();
  }

  public static void addToConstantsA (String key, Object value) {
    System.out.println("Trying to add to constants A: key " + key + " value " + value);

    if (constantsA.containsKey(key)) {
      constantsA.replace(key, value);
    } else {
      constantsA.put(key, value);
    }
  }

  public static void addToVariablesA (String key, Object value) {
    System.out.println("Trying to add to variables A: key " + key + " value " + value);

    if (variablesA.containsKey(key)) {
      variablesA.replace(key, value);
    } else {
      variablesA.put(key, value);
    }
  }

  public static void addToConstantsB (String key, Object value) {
    System.out.println("Trying to add to constants B: key " + key + " value " + value);

    if (constantsB.containsKey(key)) {
      constantsB.replace(key, value);
    } else {
      constantsB.put(key, value);
    }
  }

  public static void addToVariablesB (String key, Object value) {
    System.out.println("Trying to add to variables B: key " + key + " value " + value);

    if (variablesB.containsKey(key)) {
      variablesB.replace(key, value);
    } else {
      variablesB.put(key, value);
    }
  }

  public static void addToConstants (String key, Object value) {
    addToConstantsA(key, value);
    addToConstantsB(key, value);
  }

  public static void addToVariables (String key, Object value) {
    addToVariablesA(key, value);
    addToVariablesB(key, value);
  }

  /*
   * NOTE: constants and variables that are common to both robot A and robot B
   * should be added with addToConstants() or addToVariables()
   *
   * Use different constants and variables for the two robots only if there is a
   * difference. TestModeStuff
   */
  private static void initConfig () {
    /*
     * Human IO
     */
    {
      /*
       * Constants
       */
      {
        /*
         * Joysticks
         */
        {
          addToConstants("joysticks.left", 0);
          addToConstants("joysticks.right", 1);
          addToConstants("joysticks.operator", 2);

          addToConstants("joysticks.operator.buttons.collectCG", 7);
          addToConstants("joysticks.operator.buttons.collectPanelFloor", 9);
          addToConstants("joysticks.operator.buttons.preparePanelCollectLS", 5);
          addToConstants("joysticks.operator.buttons.collectPanelLS", 6);
          addToConstants("joysticks.operator.buttons.install", 8);

          addToConstants("joysticks.operator.buttons.elevatorToBottom", 1);
          addToConstants("joysticks.operator.buttons.elevatorToLvl_1", 2);
          addToConstants("joysticks.operator.buttons.elevatorToLvl_2", 3);
          addToConstants("joysticks.operator.buttons.elevatorToLvl_3", 4);
        }

        /*
         * Buttons and axis
         */
        {
        }
      }
    }

    /*
     * RobotIO
     */
    {
      /*
       * Constants
       */
      {
        addToConstants("talonConfig.closedLoop.quad.neutralDeadband", 0.0);
        addToConstants("talonConfig.regular.neutralDeadband", 0.0);
      }

      /*
       * Variables
       */
      {
      }
    }

    /*
     * Chassis
     */
    {
      /*
       * Constants
       */
      {
        addToConstants("drivetrain.encoderR.distancePerPulse", 0.00124224); // Units: m
        addToConstants("drivetrain.encoderL.distancePerPulse", 0.00124224); // Units: m
      }

      /*
       * Variables
       */
      {
      }
    }

    /*
     * CargoIntake
     */
    {
      /*
       * Constants
       */
      {
        addToConstants("cargoIntake.rollerMotor.voltage.in", -0.5);
        addToConstants("cargoIntake.rollerMotor.voltage.out", 0.5);
        addToConstants("cargoIntake.bottomRollerMotor.voltage.in", -0.5);
        addToConstants("cargoIntake.bottomRollerMotor.voltage.out", 0.5);

        addToConstants("cargoIntake.arm.motor.distancePerPulse", 360.0);
        addToConstants("cargoIntake.arm.motor.upr", 1024);
        addToConstants("cargoIntake.arm.pos.up", 90.0);
        addToConstants("cargoIntake.arm.pos.down", 0.0);
        addToConstants("cargoIntake.arm.pos.tolerance", 5.0);

        addToConstants("cargoIntake.arm.kPIDLoopIdx", 0);
        addToConstants("cargoIntake.arm.kTimeoutMs", 30);
        addToConstants("cargoIntake.arm.kSensorPhase", false);
        addToConstants("cargoIntake.arm.kMotorInvert", false);
        addToConstants("cargoIntake.arm.tolerance", 5);
        addToConstants("cargoIntake.arm.kP", 1.0);
        addToConstants("cargoIntake.arm.kI", 0.0);
        addToConstants("cargoIntake.arm.kD", 1.0);
        addToConstants("cargoIntake.arm.kF", 0.0);
        addToConstants("cargoIntake.arm.DkP", 1.25);
        addToConstants("cargoIntake.arm.DkI", 0.001);
        addToConstants("cargoIntake.arm.DkD", 1.0);
        addToConstants("cargoIntake.arm.DkF", 0.0);
        addToConstants("cargoIntake.arm.kIzone", 0);

        addToConstants("cargoIntake.hold.voltage", -0.2);
      }

      /*
       * Variables
       */
      {
      }
    }

    /*
     * Elevator
     */
    {
      /*
       * Constants
       */
      {
        addToConstants("elevator.encoder.upr", 1024);
        addToConstants("elevator.encoder.distPerRevolution", 100 * Math.PI * 32.5 / 428); // Units: cm/encoderTicks
        //Arm Stay Fresh, ~hold~
        addToConstants("elevator.heights.hp.offset", 15.0);
        addToConstants("elevator.armStateThreshold", 2.0);
        addToConstants("elevator.motors.runningTolerance", 3);
        addToConstants("elevator.motors.motorCLETolerance", 2);
        addToConstants("elevator.profiles.up.kp", 3.35);
        addToConstants("elevator.profiles.up.ki", 0.0);
        addToConstants("elevator.profiles.up.kd", 20.0);
        addToConstants("elevator.profiles.up.kf", 0.0);
        addToConstants("elevator.profiles.down.kp", 0.5);
        addToConstants("elevator.profiles.down.ki", 0.0);
        addToConstants("elevator.profiles.down.kd", 5.0);
        addToConstants("elevator.profiles.down.kf", 0.0);
        addToConstants("elevator.heights.bottom", 0.0); // Absolute
        addToConstants("elevator.heights.bottomBreakpoint", 7.9); // Absolute
        addToConstants("elevator.heights.stratingConfiguration", 58.25); // Absolute
        addToConstants("elevator.heights.topBreakpoint", 142.5); // Absolute
        addToConstants("elevator.heights.top", 159.4); // Absolute
        addToConstants("elevator.heights.lvl1.hp", 18.0); // Absolute
        addToConstants("elevator.heights.lvl1.cargo", 13.0); // TODO - Change to real value
        addToConstants("elevator.heights.lvl2.hp", 87.0); // Absolute
        addToConstants("elevator.heights.lvl2.cargo", 90.0); // TODO - Change to real value
        addToConstants("elevator.heights.lvl3.hp", 159.4); // Absolute
        addToConstants("elevator.heights.lvl3.cargo", 159.4); // TODO - Change to real value
      }

      /*
       * Variables
       */
      {
      }
    }

    /*
     * cargoEjector
     */
    {
      /*
       * Constants
       */
      {
        addToConstants("cargoEjector.arm.talon.ktimeout", 10);
        addToConstants("cargoEjector.arm.talon.PIDLoopIndex", 0);
        addToConstants("cargoEjector.arm.talon.kP", 0.7);
        addToConstants("cargoEjector.arm.talon.kI", 0.0);//1.0 / 5000.0);
        addToConstants("cargoEjector.arm.talon.kD", 0.0);
        addToConstants("cargoEjector.arm.talon.DPR", 360.0 * 0.5);
        addToConstants("cargoEjector.arm.talon.UPR", 4096);
        addToConstants("cargoEjector.ArmState.COLLECT.pos", 0.0);
        addToConstants("cargoEjector.ArmState.EJECT.pos", 180.0);
        addToConstants("cargoEjector.RollerState.IN.voltage", -2.0);
        addToConstants("cargoEjector.RollerState.OUT.voltage", 2.0);
        addToConstants("ejectCargo.delay", 1000L);
        addToConstants("cargoEjector.kArmTolerance", 2.0);
        addToConstants("cargoEjector.EjectorRollerState.IN.voltage", -1.0);
        addToConstants("cargoEjector.EjectorRollerState.OUT.voltage", 1.0);
        addToConstants("cargoEjector.EjectorRollerState.OUT.delay", 700L);
      }

      /*
       * Variables
       */
      {
      }
    }

    /*
     * PanelMechanism
     */
    {
      /*
       * Constants
       */
      {
        addToConstants("panelEject.delay", 700l);
        addToConstants("panelCollect.delay", 1200l);
        addToConstants("panelGentleEject.delay", 3000l);
        addToConstants("panelMechanism.maxVoltage", 4.0);

        addToConstants("panelMechanism.potentiometer.fullRange", 360.0 * 10);
        addToConstants("panelMechanism.potentiometer.offset", 0.0);
        addToConstants("panelMechanism.distancePerPulse.dpr", 360.0 / 2.0);
        addToConstants("panelMechanism.distancePerPulse.upr", 4096);
        addToConstants("panelMechanism.kF", 0.0);
        addToConstants("panelMechanism.kP", 8.0);
        addToConstants("panelMechanism.kI", 0.0);
        addToConstants("panelMechanism.kD", 80.0);
        addToConstants("PanelMechanismState.STARTING_CONFIG.angle", 0.0);
        addToConstants("PanelMechanismState.STARTING_CONFIG.tolerance", 2.0);
        addToConstants("PanelMechanismState.FLOORCOLLECT.angle", 72.0);
        addToConstants("PanelMechanismState.FLOORCOLLECT.tolerance", 4.0);
        addToConstants("PanelMechanismState.LSCOLLECT.angle", 126.0);
        addToConstants("PanelMechanismState.LSCOLLECT.tolerance", 2.0);
        addToConstants("PanelMechanismState.INSTALL.angle", 155.0);
        addToConstants("PanelMechanismState.INSTALL.tolerance", 2.0);
        addToConstants("PanelMechanismState.CLOSED.angle", 270.0);
        addToConstants("PanelMechanismState.CLOSED.tolerance", 2.0);
        addToConstants("PanelRollerState.IN.speed", 0.6);
        addToConstants("PanelRollerState.OUT.speed", -0.6);
        addToConstants("PanelRollerState.GENTLE_OUT.speed", -0.4);
        addToConstants("PanelRollerState.STOP.speed", 0.0);
      }

      /*
       * Variables
       */
      {
      }
    }

  }
}
