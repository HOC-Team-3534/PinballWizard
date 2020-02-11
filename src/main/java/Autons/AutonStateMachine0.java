package Autons;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

public class AutonStateMachine0 extends AutonStateMachineBase implements AutonStateMachineInterface {

	int state = 1;
	int stateCnt = 0;

	WPI_TalonFX frontRight = RobotMap.frontRightMotor;
	WPI_TalonFX frontLeft = RobotMap.frontLeftMotor;

	AutonCalculations part1;
	double part1Heading = Math.PI / 6;
	double part1Rotation = Math.PI;

	public AutonStateMachine0() {

	}

	@Override
	public void process() {

		int nextState = state;

		switch (state) {

		case 1:
		
			//any initialization code here
			nextState = 10;
			break;

		case 10:

			//calculate ramping and what not

			part1 = new AutonCalculations(10.0, RobotMap.maxVelocity, RobotMap.typicalAcceleration, 0.020);
			part1.calculate();

			nextState = 20;
			break;

		case 20:
			
			//drive
			double generalVelocity = part1.getVelocity();
			Robot.drive.drive(generalVelocity * Math.cos(part1Heading), generalVelocity * Math.sin(part1Heading), part1Rotation / part1.total_time, true);

			if(part1.isFinished()){
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
