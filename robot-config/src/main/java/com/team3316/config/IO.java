package com.team3316.config;

import java.util.HashMap;
import java.util.Map;

public class IO {
  public static Map<String, Integer> pwmA = new HashMap<>();
  public static Map<String, Integer> canA = new HashMap<>();

  public static Map<String, Integer> pwmB = new HashMap<>();
  public static Map<String, Integer> canB = new HashMap<>();

  public static Map<String, Integer> dioA = new HashMap<>();
  public static Map<String, Integer> dioB = new HashMap<>();

  public static Map<String, Integer> aioA = new HashMap<>();
  public static Map<String, Integer> aioB = new HashMap<>();

  public static Map<String, Integer> pdpA = new HashMap<>();
  public static Map<String, Integer> pdpB = new HashMap<>();

  public static Map<String, Integer> pcmA = new HashMap<>();
  public static Map<String, Integer> pcmB = new HashMap<>();

  /**
   * Finds the key that is mapped to a specified channel in the parameter map.
   *
   * @param in    The map we want to search in.
   * @param value The specified channel.
   * @return The key that is mapped to the requested channel. If none exists the
   *         method returns null.
   */
  private static Object findKey(Map<?, ?> in, Object value) {
    for (Object key : in.keySet()) {
      if (in.get(key).equals(value)) {
        return key;
      }
    }
    return null;
  }

  /**
   * Puts a mapping of a key to a channel in the requested map.
   *
   * @param in      The map to add the key.
   * @param name    The key.
   * @param channel The channel to map the key to.
   * @throws Exception If the channel already has a mapping, throws an exception.
   */
  private static void put(Map<String, Integer> in, String name, int channel) throws Exception {
    if (in.containsValue(channel)) {
      throw new Exception(
          "Channel " + channel + " for key " + name + " already exists and is: " + findKey(in, channel));
    }

    in.put(name, channel);
    Config.addToConstants(name, channel);
  }

  /**
   * Puts a mapping of a key to a channel in the requested map of ROBOT A.
   *
   * @param in      The map to add the key.
   * @param name    The key.
   * @param channel The channel to map the key to.
   * @throws Exception If the channel already has a mapping, throws an exception.
   */
  private static void putA(Map<String, Integer> in, String name, int channel) throws Exception {
    if (in.containsValue(channel)) {
      throw new Exception(
          "Channel " + channel + " for key " + name + " already exists and is: " + findKey(in, channel));
    }

    in.put(name, channel);
    Config.addToConstantsA(name, channel);
  }

  /**
   * Puts a mapping of a key to a channel in the requested map of ROBOT B.
   *
   * @param in      The map to add the key.
   * @param name    The key.
   * @param channel The channel to map the key to.
   * @throws Exception If the channel already has a mapping, throws an exception.
   */
  private static void putB(Map<String, Integer> in, String name, int channel) throws Exception {
    if (in.containsValue(channel)) {
      throw new Exception(
          "Channel " + channel + " for key " + name + " already exists and is: " + findKey(in, channel));
    }

    in.put(name, channel);
    Config.addToConstantsB(name, channel);
  }

  /**
   * Put method for pwm channels on robot A. Read the documentation of the put
   * method.
   */
  private static void putPWMA(String name, int channel) throws Exception {
    putA(pwmA, name, channel);
  }

  /**
   * Put method for can channels on robot A. Read the documentation of the put
   * method.
   */
  private static void putCANA(String name, int channel) throws Exception {
    putA(canA, name, channel);
  }

  /**
   * Put method for pwm channels on robot B. Read the documentation of the put
   * method.
   */
  private static void putPWMB(String name, int channel) throws Exception {
    putB(pwmB, name, channel);
  }

  /**
   * Put method for can channels on robot B. Read the documentation of the put
   * method.
   */
  private static void putCANB(String name, int channel) throws Exception {
    putB(canB, name, channel);
  }

  /**
   * Put method for dio channels. Read the documentation of the put method.
   */
  private static void putDIOA(String name, int channel) throws Exception {
    putA(dioA, name, channel);
  }

  /**
   * Put method for dio channels. Read the documentation of the put method.
   */
  private static void putDIOB(String name, int channel) throws Exception {
    putB(dioB, name, channel);
  }

  /**
   * Put method for aio channels. Read the documentation of the put method.
   */
  private static void putAIOA(String name, int channel) throws Exception {
    putA(aioA, name, channel);
  }

  /**
   * Put method for aio channels. Read the documentation of the put method.
   */
  private static void putAIOB(String name, int channel) throws Exception {
    putB(aioB, name, channel);
  }

  /**
   * Put method for pdp channels. Read the documentation of the put method.
   */
  private static void putPDPA(String name, int channel) throws Exception {
    putA(pdpA, name, channel);
  }

  /**
   * Put method for pdp channels. Read the documentation of the put method.
   */
  private static void putPDPB(String name, int channel) throws Exception {
    putB(pdpB, name, channel);
  }

  /**
   * Put method for pdp channels. Read the documentation of the put method.
   */
  private static void putPCMA(String name, int channel) throws Exception {
    putA(pcmA, name, channel);
  }

  /**
   * Put method for pdp channels. Read the documentation of the put method.
   */
  private static void putPCMB(String name, int channel) throws Exception {
    putB(pcmB, name, channel);
  }

  /**
  * Every port above 20 is a place holder
  * TODO - Add real ports
  */
  public static void initIO () {
    try {
      /*
       * PWM and CAN initialization
       */
      {
        /*
         * Robot A
         */
        {
          //CargoIntake
          putPWMA("cargoIntake.rollerMotor.port", 0);
          putPWMA("cargoIntake.bottomRollerMotor.port", 1);
          putCANA("cargoIntake.armMasterMotor.port", 10);
          putCANA("cargoIntake.armSlaveMotor.port", 11);

          //CargoEjector
          putCANA("cargoEjector.arm.talon.port", 8);
          putPWMA("cargoEjector.rollerMotorPort", 2);

          //PanelMechanism
          putCANA("panelMechanism.victor", 4);
          putCANA("panelMechanism.talon", 9);

          // Drivetrain
          putCANA("drivetrain.talonRF", 12); // Right front
          putCANA("drivetrain.talonRB", 13); // Right back
          putCANA("drivetrain.talonLF", 14); // Left front
          putCANA("drivetrain.talonLB", 15); // Left back

          // Elevator motors
          putCANA("elevator.motors.1", 1); // First motor
          putCANA("elevator.motors.2", 2); // Second motor
        }

        /*
         * Robot B (Currently Mars, will be changed to actual 2nd robot)
         */
        {
          //CargoIntake
          putPWMB("cargoIntake.rollerMotor.port", 0);
          putPWMB("cargoIntake.bottomRollerMotor.port", 1);
          putCANB("cargoIntake.armMasterMotor.port", 10);
          putCANB("cargoIntake.armSlaveMotor.port", 11);

          //CargoEjector
          putCANB("cargoEjector.arm.talon.port", 8);
          putPWMB("cargoEjector.rollerMotorPort", 2);

          //PanelMechanism
          putCANB("panelMechanism.victor", 4);
          putCANB("panelMechanism.talon", 9);
  
          // Drivetrain
          putCANB("drivetrain.talonRF", 12); // Right front
          putCANB("drivetrain.talonRB", 13); // Right back
          putCANB("drivetrain.talonLF", 14); // Left front
          putCANB("drivetrain.talonLB", 15); // Left back
  
            // Elevator motors
          putCANB("elevator.motors.1", 1); // First motor
          putCANB("elevator.motors.2", 2); // Second motor
          }
      }
      // My name is Jeff
      /*
       * DIO initialization
       */
      {
        /*
         * Robot A
         */
        {
          //CargoIntake
          putDIOA("cargoIntake.arm.hallEffect.down", 6);
          putDIOA("cargoIntake.arm.hallEffect.up", 5);

          //CargoEjector
          putDIOA("cargoEjector.switchPort", 12);
          putDIOA("cargoEjector.arm.ejectHallEffectPort", 7);
          putDIOA("cargoEjector.arm.collectHallEffectPort", 8);

          //PanelMechanism
          //putDIOA("panelMechanism._switchLeft", 26);
          putDIOA("panelMechanism._switchRight", 9);
          putDIOA("panelMechanism._MSDown", 10);
          putDIOA("panelMechanism._MSUp", 11);

          //elevator
          putDIOA("elevator.hallEffects.bottom", 4);
          putDIOA("elevator.hallEffects.bottomBreakpoint", 3);
          putDIOA("elevator.hallEffects.startingConfiguration", 1);
          putDIOA("elevator.hallEffects.topBreakpoint", 2);
          putDIOA("elevator.hallEffects.top", 0);
        }

        /*
         * Robot B (Currently Mars, will be changed to actual 2nd robot)
         */
        {
          //CargoIntake
          putDIOB("cargoIntake.arm.hallEffect.down", 6);
          putDIOB("cargoIntake.arm.hallEffect.up", 5);

          //CargoEjector
          putDIOB("cargoEjector.switchPort", 12);
          putDIOB("cargoEjector.arm.ejectHallEffectPort", 7);
          putDIOB("cargoEjector.arm.collectHallEffectPort", 8);

          //PanelMechanism
          //putDIOA("panelMechanism._switchLeft", 26);
          putDIOB("panelMechanism._switchRight", 9);
          putDIOB("panelMechanism._MSDown", 11);
          putDIOB("panelMechanism._MSUp", 10);

          //elevator
          putDIOB("elevator.hallEffects.bottom", 4);
          putDIOB("elevator.hallEffects.bottomBreakpoint", 3);
          putDIOB("elevator.hallEffects.startingConfiguration", 0);
          putDIOB("elevator.hallEffects.topBreakpoint", 2);
          putDIOB("elevator.hallEffects.top", 1);
        }
      }

      /*
       * AIO initialization
       */
      {
        /*
         * Robot A
         */
        {
          putAIOA("panelMechanism.potentiometer.input", 0);
        }

        /*
         * Robot B
         */
        {
          putAIOB("panelMechanism.potentiometer.input", 0);
        }
      }

      /*
       * PDP initialization
       */
      {
        /*
         * Robot A
         */
        {
        }

        /*
         * Robot B
         */
        {
        }
      }

      /*
       * PCM initialization
       */
      {
        /*
         * Robot A
         */
        {

        }

        {
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
