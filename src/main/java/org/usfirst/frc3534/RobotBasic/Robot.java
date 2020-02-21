package org.usfirst.frc3534.RobotBasic;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier; 

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.usfirst.frc3534.RobotBasic.functions.FunctionProcessor;
import org.usfirst.frc3534.RobotBasic.systems.*;

import Autons.AutonStateMachine0;
import Autons.AutonStateMachineInterface;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static int AUTON_PERIODIC = 1;
	public static OI oi;
	public static Drive drive;
	public static Shooter shooter;
	public static Intake intake;
	public static Elevator elevator;
	public static Spinner spinner;
	public static FunctionProcessor functionProcessor;

	private int loopPeriod = 0;
	private int loopCnt = 0;
	private int logCounter = 0;

	public static double designatedLoopPeriod = 20; // in milliseconds. milliseconds = seconds/1000. seconds to
													// milliseconds . seconds * 1000 = milliseconds

	Notifier m_follower_notifier;

	public static boolean autonomous;
	public static boolean teleop;
	public static boolean enabled;

	private AutonStateMachineInterface autonStateMachine;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {

		RobotMap.init();

		drive = new Drive();
		shooter = new Shooter();
		intake = new Intake();
		elevator = new Elevator();
		spinner = new Spinner();

		// OI must be constructed after subsystems. If the OI creates Commands
		// (which it very likely will), subsystems are not guaranteed to be
		// constructed yet. Thus, their requires() statements may grab null
		// pointers. Bad news. Don't move it.
		oi = new OI();

		functionProcessor = new FunctionProcessor();

		SmartDashboard.putNumber("kf", .05);
		SmartDashboard.putNumber("kp", 0.125);
		SmartDashboard.putNumber("ki", 0.000001);
		SmartDashboard.putNumber("kd", 3.0);
	}

	/**
	 * This function is called when the disabled button is hit. You can use it to
	 * reset subsystems before shutting down.
	 */
	@Override
	public void disabledInit() {

		RobotMap.frontLeftMotor.set(ControlMode.Velocity, 0);
		RobotMap.frontRightMotor.set(ControlMode.Velocity, 0);
		RobotMap.backLeftMotor.set(ControlMode.Velocity, 0);
		RobotMap.backRightMotor.set(ControlMode.Velocity, 0);

	}

	@Override
	public void disabledPeriodic() {

	}

	@Override
	public void autonomousInit() {

		int desiredAutonMode = 0;

		try {

			desiredAutonMode = (int) SmartDashboard.getNumber("autonMode", 0);

		} catch (Exception ex) {
		}

		System.out.println("Running Auton " + desiredAutonMode);

		switch (desiredAutonMode) {

		case 0:

			autonStateMachine = new AutonStateMachine0();
			break;

		case 1:

			break;

		case 2:

			break;

		case 3:

			break;

		}

		SmartDashboard.putNumber("aMode", desiredAutonMode);

	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {

		long prevLoopTime = 0;

		while (this.isAutonomous()) {

			RobotState("autonomous enabled");

			long currentTime = System.currentTimeMillis();

			if (currentTime - prevLoopTime >= designatedLoopPeriod) {

				log();

				loopPeriod = (int) (currentTime - prevLoopTime);
				prevLoopTime = currentTime;
				loopCnt++;

				// run processes
				autonStateMachine.process();
				drive.process();
				shooter.process();
				intake.process();
				elevator.process();
				spinner.process();

			}

			Timer.delay(0.001);

		}

		RobotState("autonomous disabled");

	}

	@Override
	public void teleopInit() {

		RobotMap.shooter.config_kF(0, SmartDashboard.getNumber("kf", 0.0), 0);
		RobotMap.shooter.config_kP(0, SmartDashboard.getNumber("kp", 0.0), 0);
		RobotMap.shooter.config_kI(0, SmartDashboard.getNumber("ki", 0.0), 0);
		RobotMap.shooter.config_kD(0, SmartDashboard.getNumber("kd", 0.0), 0);

	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

		log();

		long prevLoopTime = 0;

		while (this.isOperatorControl() && this.isEnabled()) {

			log();

			RobotState("teleop enabled");

			long currentTime = System.currentTimeMillis();

			if (currentTime - prevLoopTime >= designatedLoopPeriod) {

				loopPeriod = (int) (currentTime - prevLoopTime);
				prevLoopTime = currentTime;
				loopCnt++;

				String gameData = DriverStation.getInstance().getGameSpecificMessage();
				if(gameData.length() > 0){
					spinner.setCorrectColor(gameData);
				}
				
				functionProcessor.process();
				// run processes
				drive.process();
				shooter.process();
				intake.process();
				elevator.process();
				spinner.process();
				/** Run subsystem process methods here */

			}

			Timer.delay(0.001);

		}

		RobotState("teleop disabled");

	}

	public void log() {

		logCounter++;

		if (logCounter >= 5) {

			// SmartDashboard Numbers
			SmartDashboard.putNumber("Loop Period", loopPeriod);
			SmartDashboard.putNumber("Loop Count", loopCnt);
			SmartDashboard.putNumber("autonMode", 0);

			SmartDashboard.putNumber("Red", RobotMap.colorSensor.getColor().red);
			SmartDashboard.putNumber("Green", RobotMap.colorSensor.getColor().green);
			SmartDashboard.putNumber("Blue", RobotMap.colorSensor.getColor().blue);
			SmartDashboard.putNumber("Wheel Shooter Speed", RobotMap.shooter.getSelectedSensorVelocity());
			SmartDashboard.putNumber("Left Front Encoder Position", RobotMap.frontLeftMotor.getSelectedSensorVelocity());
			SmartDashboard.putNumber("Left Rear Encoder Position", RobotMap.backLeftMotor.getSelectedSensorVelocity());
			SmartDashboard.putNumber("Right Front Encoder Position", RobotMap.frontRightMotor.getSelectedSensorVelocity());
			SmartDashboard.putNumber("Right Rear Encoder Position", RobotMap.backRightMotor.getSelectedSensorVelocity());

			SmartDashboard.putBoolean("Beam Break Sensor", RobotMap.beamBreakSensor.get());

			logCounter = 0;

		}
	}

	public void RobotState(String state) {

		switch (state) {

		case "teleop enabled":

			autonomous = false;
			teleop = true;
			enabled = true;
			break;

		case "teleop disabled":

			autonomous = false;
			teleop = true;
			enabled = false;
			break;

		case "autonomous enabled":

			autonomous = true;
			teleop = false;
			enabled = true;
			break;

		case "autonomous disabled":

			autonomous = true;
			teleop = false;
			enabled = false;
			break;

		}

	}
}