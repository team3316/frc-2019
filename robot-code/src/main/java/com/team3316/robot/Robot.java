package com.team3316.robot;

import java.util.Timer;

import com.team3316.kit.DBugLogger;
import com.team3316.robot.humanIO.Joysticks;
import com.team3316.robot.humanIO.SDB;
import com.team3316.robot.subsystems.CargoEjector;
import com.team3316.robot.subsystems.CargoIntake;
import com.team3316.robot.subsystems.Drivetrain;
import com.team3316.robot.subsystems.Elevator;
import com.team3316.robot.subsystems.PanelMechanism;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  public static Timer timer;

  /*
   * Human IO
   */
  public static Joysticks joysticks;
  public static SDB sdb;

  /*
   * Subsystems
   */
  public static Drivetrain drivetrain;
  public static CargoIntake cargoIntake;
  public static Elevator elevator;
  public static PanelMechanism panelMechanism;
  public static CargoEjector cargoEjector;

  Command autonomousCommand;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  public void robotInit() {
    try {
      /*
       * Above all else
       */
      timer = new Timer();

      /*
       * Human IO (that does not require subsystems)
       */
      joysticks = new Joysticks();

      /*
       * Subsystems
       */
      elevator = new Elevator();
      drivetrain = new Drivetrain();
      cargoIntake = new CargoIntake();
      panelMechanism = new PanelMechanism();
      cargoEjector = new CargoEjector();

      /*
       * Human IO (that requires subsystems)
       */
      joysticks.initButtons();
      sdb = new SDB();

    } catch (Exception e) {
      DBugLogger.getInstance().severe(e);
    }
  }

  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  public void autonomousInit() {
    Robot.elevator.initRoutine();

    if (autonomousCommand != null)
      autonomousCommand.start();
  }

  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  public void teleopInit() {
    Robot.elevator.initRoutine();

    if (autonomousCommand != null)
      autonomousCommand.cancel();
  }

  public void disabledInit() {
    Robot.elevator.disabledRoutine();
  }

  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  public void testPeriodic() {
    LiveWindow.run();
  }

  private void printTheTruth() {
    System.out.println("Vita is the Melech!!!");
  }
}
