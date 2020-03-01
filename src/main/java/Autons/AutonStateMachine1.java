package Autons;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;
import Autons.AutonCalculations;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class AutonStateMachine1 extends AutonStateMachineBase implements AutonStateMachineInterface {

	int state = 1;
	int stateCnt = 0;

	double set_angle = Robot.drive.getAngle().getRadians();
	double last_angle_error = 0;

	WPI_TalonFX frontRight = RobotMap.frontRightMotor;
	WPI_TalonFX frontLeft = RobotMap.frontLeftMotor;

	AutonCalculations part1;
	//double part1Heading = Math.PI / 6;
	//double part1Rotation = Math.PI;

	public AutonStateMachine1() {

	}

	@Override
	public void process() {

		int nextState = state;

		switch (state) {

		case 1:
		
			//any initialization code here
			Robot.functionProcessor.autoIndex.process();
			if(Robot.shooter.getDifference() == 2){
				nextState = 30;
			}
			break;

		case 10:

			//calculate ramping and what not

			//magic number = 1.537

			part1 = new AutonCalculations(30, -200 , RobotMap.maxVelocity, 0.75, 0.020);
			part1.calculate();

			nextState = 20;
			break;

		case 20:
			
			//drive
			part1.calcGeneralVelocity();
			NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
			double tx = table.getEntry("tx").getDouble(0.0) / 180 * Math.PI;

			double rotationalVelocity = 0.0;

			if(tx < -10.0){

				rotationalVelocity = 0.2;

			}else if(tx > 10.0){

				rotationalVelocity = -0.2;

			}

			//this is where we want to change set angle if we want to rotate while driving
			// double angle_error = set_angle - Robot.drive.getAngle().getRadians();
			// double correctional_velocity = angle_error * 0.30 + (angle_error - last_angle_error) * 0;
			// last_angle_error = angle_error;
			Robot.drive.drive(part1.getXVelocity(false), part1.getYVelocity(false), rotationalVelocity, true);

			if(part1.isFinished()){
				nextState = 30;
			}
			break;

		case 30:

			Robot.autonomousFunctionsDead = false;

			Robot.functionProcessor.intake.process();

			nextState = 40;

			break;
		
		case 40:

			Robot.functionProcessor.intake.process();
			Robot.drive.drive(0.1 * RobotMap.maxVelocity, 0, 0, true);

			if(Robot.drive.getDistance() >= 225){
				Robot.drive.drive(0, 0, 0, true);
				nextState = 50;
			}

			break;
		
		case 50:

			Robot.autonomousFunctionsDead = false;
			
			Robot.functionProcessor.shootFar.process();
			
			if(Robot.shooter.getBallsShot() == 5){
				Robot.autonomousFunctionsDead = true;
				Robot.functionProcessor.intake.process();
				Robot.functionProcessor.shootFar.process();
				nextState = 100;
			}

			break;

		case 100:

			RobotMap.frontLeftMotor.set(ControlMode.Velocity, 0);
			RobotMap.frontRightMotor.set(ControlMode.Velocity, 0);
			RobotMap.backLeftMotor.set(ControlMode.Velocity, 0);
			RobotMap.backRightMotor.set(ControlMode.Velocity, 0);

			break;
		}

		if (nextState != state) {
			state = nextState;
			stateCnt = 0;
		} else {
			stateCnt++;
		}

	}

}
