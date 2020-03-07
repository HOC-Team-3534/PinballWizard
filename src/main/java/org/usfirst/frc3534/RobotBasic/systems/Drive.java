package org.usfirst.frc3534.RobotBasic.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;
import org.usfirst.frc3534.RobotBasic.OI.Axes;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.MecanumDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.MecanumDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.MecanumDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Drive extends SystemBase implements SystemInterface {

	private WPI_TalonFX frontLeft = RobotMap.frontLeftMotor, frontRight = RobotMap.frontRightMotor, backLeft = RobotMap.backLeftMotor, backRight = RobotMap.backRightMotor;

	private final Translation2d frontLeftLocation = new Translation2d(0.279, 0.327);
	private final Translation2d frontRightLocation = new Translation2d(0.279, -0.327);
	private final Translation2d backLeftLocation = new Translation2d(-0.279, 0.327);
	private final Translation2d backRightLocation = new Translation2d(-0.279, -0.327);

	private final MecanumDriveKinematics kinematics = new MecanumDriveKinematics(frontLeftLocation, frontRightLocation, backLeftLocation, backRightLocation);
	private final MecanumDriveOdometry odometry = new MecanumDriveOdometry(kinematics, getAngle());

	private double deadband = 0.15;				//the deadband on the controller (forward/backward)
	private double sideDeadband = 0.15;
	private double turningDeadband = 0.10;		//the deadband on the controller (left/right)
	private boolean negative = false;

	private double longitudinal_input, latitudinal_input, rotational_input;			//used to store the direct input value of the respective axis on the controller
	private double longitudinal_output, latitudinal_output, rotational_output;		//used to save the percentage output calculated from the initial inputs

	private double last_error, overall_error;	
	
	private double last_rotational_angle = 0.0;

	public double setLFVelocity, setLRVelocity, setRFVelocity, setRRVelocity;

	private double KpAim = 0.045;
	private double KiAim = 0.000425;				
	private double KdAim = 22.5; 

	private double kMaxError = 4.0;
	private double last_steering_adjust = 0.0;
	private double kOffset = 2.25;

	private boolean dtmEnabled = false;
	private boolean dtmCorrected = false;
	private boolean dtmLockedOn = false;

	private long prevTime = System.currentTimeMillis();

	private double heightOfLimeLight = 23.25;
	private double heightToTop = 8.1875 * 12;
	private double angleOfLimelight = 13.2;//10.29 was close for close
	private double limelightX = 0;
	private double limelightY = 0;
	private double kLimelightToCenterAngle = 140.9 / 180 * Math.PI;
	private double kLimelightToCenterDist = 10.3;
	private double centerOfRobotY = 0.0;
	private double centerOfRobotX = 0.0;
	private double distance;

	private Direction rotDirection;
	private Direction posDirection;

	public Drive() {
	}

	@Override
	public void process() {

		updateOdometry();
		// SmartDashboard.putNumber("Odometry X Displacement", odometry.getPoseMeters().getTranslation().getX() / 1.537);
		// SmartDashboard.putNumber("Odometry Y Displacement", odometry.getPoseMeters().getTranslation().getY() / 1.537);

		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		double regTx = table.getEntry("tx").getDouble(0.0);
		double tx = table.getEntry("tx").getDouble(0.0) / 180 * Math.PI;
		double height = table.getEntry("tvert").getDouble(0.0);
		double width = table.getEntry("thor").getDouble(0.0);
		double skew = table.getEntry("ts").getDouble(0.0);

		double pixelAngle = table.getEntry("ty").getDouble(0.0);

		distance = (heightToTop - heightOfLimeLight) * Math.tan(Math.PI / 180 * (90 - (angleOfLimelight + pixelAngle)));
		distance = distance / Math.cos(Math.abs(tx) / 180 * Math.PI);

		double navxAngle = getRawAngle();
			navxAngle = (navxAngle > Math.PI) ? Math.PI * 2 - navxAngle:navxAngle;
			posDirection = (skew > -45) ? Direction.right: Direction.left;
			rotDirection = (getRawAngle() > Math.PI) ? Direction.right: Direction.left;
			navxAngle = (posDirection == rotDirection) ? -navxAngle: navxAngle;
			tx = (posDirection == Direction.right) ? -tx: tx;
			double totalAngle = navxAngle + tx;
			// SmartDashboard.putNumber("TotalAngle", totalAngle);
			limelightX = distance * Math.sin(totalAngle);
			limelightX = (posDirection == Direction.left) ? -limelightX: limelightX;
			limelightY = distance * Math.cos(totalAngle);

			double targetAngle = Math.atan(Math.abs(limelightX) / limelightY);
			targetAngle = (limelightX > 0) ? -targetAngle: targetAngle;
			navxAngle = (navxAngle > Math.PI) ? Math.PI * 2 - navxAngle:navxAngle;
			double aimError = navxAngle - targetAngle;

			double limelightToCenter;
			boolean subtract = false;
			navxAngle = getRawAngle();
			if(navxAngle > Math.PI){

				navxAngle = Math.PI * 2 - navxAngle;
				limelightToCenter = navxAngle + kLimelightToCenterAngle;

			} else {

				limelightToCenter = -navxAngle + kLimelightToCenterAngle;

			}

			if(limelightToCenter > Math.PI){

				subtract = true;
				limelightToCenter -= Math.PI;

			} else if(limelightToCenter < 0){
				limelightToCenter = -limelightToCenter;
			}

			double distance1 = Math.sqrt(Math.pow(limelightY, 2) + Math.pow(kLimelightToCenterDist, 2) - 2 * limelightY * kLimelightToCenterDist * Math.cos(limelightToCenter));
			double angle2 = Math.asin(Math.sin(limelightToCenter)/distance1 * kLimelightToCenterDist);
			double angle3 = Math.PI / 2 - angle2;
			centerOfRobotY = distance1 * Math.sin(angle3);
			centerOfRobotX = (subtract) ? limelightX - distance1 * Math.cos(angle3):limelightX + distance1 * Math.cos(angle3);

		if (Robot.teleop && Robot.enabled) {

			//Network 
			//Attempt at calling the Network Tables for Limelight and setting it 

			
			

			// SmartDashboard.putNumber("tx", tx);
			// SmartDashboard.putNumber("navx angle", getAngle().getDegrees());
			// SmartDashboard.putNumber("skew", skew);
			// SmartDashboard.putNumber("limelight y", limelightY);
			// SmartDashboard.putNumber("limelight x", limelightX);
			// SmartDashboard.putNumber("center of bot y", centerOfRobotY);
			// SmartDashboard.putNumber("center of bot x", centerOfRobotX);
			// SmartDashboard.putNumber("ty value", pixelAngle);
			// SmartDashboard.putNumber("distance", distance);

//-----------------------------------------------------------------------------------------------------

			RobotMap.blinkin.set(0.55); //color waves of team colors

			negative = false;
			longitudinal_input = Axes.Drive_ForwardBackward.getAxis();
			// SmartDashboard.putNumber("longitudinal input", longitudinal_input);
			if(longitudinal_input < 0) negative = true;

			longitudinal_output = Math.abs(longitudinal_input);

			if(longitudinal_output > deadband){

				longitudinal_output -= deadband;
				longitudinal_output *= (1 / (1 - deadband));
				longitudinal_output = Math.pow(longitudinal_output, 2);
				if(negative) longitudinal_output = -longitudinal_output;

			}else{

				longitudinal_output = 0;

			}

			negative = false;
			latitudinal_input = Axes.Drive_LeftRight.getAxis();
			if(latitudinal_input < 0) negative = true;
			
			latitudinal_output = Math.abs(latitudinal_input);
			if(latitudinal_output > sideDeadband){

				latitudinal_output -= sideDeadband;
				latitudinal_output *= (1 / (1 - sideDeadband));
				latitudinal_output = Math.pow(latitudinal_output, 2);
				if(negative) latitudinal_output = -latitudinal_output;

			}else{

				latitudinal_output = 0;

			}
			
			if(!dtmEnabled){

				overall_error = 0.0;
				negative = false;
				rotational_input = Axes.Drive_Rotation.getAxis();
				if(rotational_input < 0) negative = true;
				
				rotational_output = Math.abs(rotational_input);
				if(rotational_output > turningDeadband){

					rotational_output -= turningDeadband;
					rotational_output *= (1 / (1 - turningDeadband));
					rotational_output = Math.pow(rotational_output, 2);
					if(negative) rotational_output = -rotational_output;
					last_rotational_angle = getRawAngle();

				}else{

					rotational_output = 0.0;
					
				}

				dtmCorrected = false;
				overall_error = 0.0;

				double total_output = longitudinal_output * latitudinal_output;
				rotational_output = (2 * Math.pow(total_output, 2) - 2 * total_output + 1) * rotational_output;

			}else if(dtmEnabled){

				/// System.out.println("im getting here");
				aimError = regTx + kOffset;
				double steering_adjust = 0.0;
				double errorChange = 0;
				errorChange = aimError - last_error;

				/*if(Robot.drive.posDirection == Direction.left){
					aimError += kOffset;
				}*/

				if(Math.abs(aimError) < 2){
					overall_error += aimError;
				} else {
					overall_error = 0;
				}
				

				last_error = aimError;

				// System.out.println("...and the aimError is: " + aimError);

				if (aimError > kMaxError){
					aimError = kMaxError;
				}else if (aimError < -kMaxError){
					aimError = -kMaxError;
				}

				steering_adjust = KpAim * aimError + KiAim * overall_error + (errorChange) * KdAim;

				if (Math.abs(steering_adjust) <= .05 && Math.abs(errorChange) <= .05){

					dtmCorrected = true;

				} else {

					dtmCorrected = false;

				}

				double steering_adjust_change = steering_adjust - last_steering_adjust;
				last_steering_adjust = steering_adjust;

				/*if(steering_adjust_change > 0.005 && steering_adjust_change < .01){
					dtmLockedOn = true;
				}

				if (dtmLockedOn){

					steering_adjust = 0;
					dtmCorrected = true;

				}*/

				rotational_output = steering_adjust;

				drive(0, 0, rotational_output * RobotMap.maxAngularVelocity * 0.5, true);


			}
			
			
			// System.out.println("******************Rotational OutPut: " + rotational_output);

			if(Robot.elevator.isCreepModeEnabled()){
				
				drive(longitudinal_output * 0.4 * RobotMap.maxVelocity, latitudinal_output * 0.4 * RobotMap.maxVelocity, rotational_output * RobotMap.maxAngularVelocity * 0.5, true);

			}else{

				drive(longitudinal_output * RobotMap.maxVelocity, latitudinal_output * RobotMap.maxVelocity, rotational_output * RobotMap.maxAngularVelocity * 0.5, true);

			}

			if((Math.abs(latitudinal_input) < sideDeadband && Math.abs(longitudinal_output) < deadband) && Math.abs(rotational_output) < turningDeadband){

				drive(0, 0, 0, true);

			}

		} else if (Robot.autonomous) {

			if(dtmEnabled){

				/// System.out.println("im getting here");
				aimError = regTx + kOffset;
				double steering_adjust = 0.0;
				double errorChange = 0;
				errorChange = aimError - last_error;

				/*if(Robot.drive.posDirection == Direction.left){
					aimError += kOffset;
				}*/

				if(Math.abs(aimError) < 2){
					overall_error += aimError;
				} else {
					overall_error = 0;
				}
				

				last_error = aimError;

				// System.out.println("...and the aimError is: " + aimError);

				if (aimError > kMaxError){
					aimError = kMaxError;
				}else if (aimError < -kMaxError){
					aimError = -kMaxError;
				}

				steering_adjust = KpAim * aimError + KiAim * overall_error + (errorChange) * KdAim;

				if (Math.abs(steering_adjust) <= .05 && Math.abs(errorChange) <= .05){

					dtmCorrected = true;

				} else {

					dtmCorrected = false;

				}

				double steering_adjust_change = steering_adjust - last_steering_adjust;
				last_steering_adjust = steering_adjust;

				/*if(steering_adjust_change > 0.005 && steering_adjust_change < .01){
					dtmLockedOn = true;
				}

				if (dtmLockedOn){

					steering_adjust = 0;
					dtmCorrected = true;

				}*/

				rotational_output = steering_adjust;
				
				drive(0, 0, rotational_output * RobotMap.maxAngularVelocity * 0.5, true);

			
			}
		}
		// uncomment the following code to test for max velocity
		/*
		 * double velocity;
		 * 
		 * if(RobotMap.frontLeftMotor.getSensorCollection().getQuadratureVelocity() >
		 * RobotMap.frontRightMotor.getSensorCollection().getQuadratureVelocity()) {
		 * 
		 * velocity =
		 * RobotMap.frontLeftMotor.getSensorCollection().getQuadratureVelocity();
		 * 
		 * }else{
		 * 
		 * velocity =
		 * RobotMap.frontRightMotor.getSensorCollection().getQuadratureVelocity();
		 * 
		 * }
		 * 
		 * SmartDashboard.putNumber("Velocity", velocity);
		 */

	}

	/**
   	* Method to drive the robot using joystick info.
   	*
   	* @param xSpeed        Speed of the robot in the x direction (forward).
   	* @param ySpeed        Speed of the robot in the y direction (sideways).
   	* @param rot           Angular rate of the robot.
   	* @param fieldRelative Whether the provided x and y speeds are relative to the field.
   	*/
  	@SuppressWarnings("ParameterName")
  	public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {

    	var mecanumDriveWheelSpeeds = kinematics.toWheelSpeeds(
        	fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
            xSpeed, ySpeed, rot, getAngle()
        	) : new ChassisSpeeds(xSpeed, ySpeed, rot)
		);
    	mecanumDriveWheelSpeeds.normalize(RobotMap.maxVelocity);
		setSpeeds(mecanumDriveWheelSpeeds);
		
  	}

	public MecanumDriveWheelSpeeds getCurrentState() {

		return new MecanumDriveWheelSpeeds(
			frontLeft.getSelectedSensorVelocity() / RobotMap.encoderVelocityToWheelVelocity,
			frontRight.getSelectedSensorVelocity() / RobotMap.encoderVelocityToWheelVelocity,
			backLeft.getSelectedSensorVelocity() / RobotMap.encoderVelocityToWheelVelocity,
			backRight.getSelectedSensorVelocity() / RobotMap.encoderVelocityToWheelVelocity
		);

	}

	public void setSpeeds(MecanumDriveWheelSpeeds speeds) {

		frontLeft.selectProfileSlot(0, 0);
		frontRight.selectProfileSlot(0, 0);
		backLeft.selectProfileSlot(0, 0);
		backRight.selectProfileSlot(0, 0);
		setLFVelocity = speeds.frontLeftMetersPerSecond * RobotMap.encoderVelocityToWheelVelocity;
		setRFVelocity = speeds.frontRightMetersPerSecond * RobotMap.encoderVelocityToWheelVelocity;
		setLRVelocity = speeds.rearLeftMetersPerSecond * RobotMap.encoderVelocityToWheelVelocity;
		setRRVelocity = speeds.rearRightMetersPerSecond * RobotMap.encoderVelocityToWheelVelocity;
		frontLeft.set(ControlMode.Velocity, setLFVelocity);
		frontRight.set(ControlMode.Velocity, setRFVelocity);
		backLeft.set(ControlMode.Velocity, setLRVelocity);
		backRight.set(ControlMode.Velocity, setRRVelocity);

	}

	public void updateOdometry() {

		odometry.update(getAngle(), getCurrentState());

	}

	public Rotation2d getAngle(){

		//// System.out.println(-RobotMap.navx.getAngle());
		return Rotation2d.fromDegrees(-RobotMap.navx.getAngle());

	}

	public void setDtmEnabled(boolean enabled) {

		dtmEnabled = enabled;

	}

	public boolean getDtmCorrected(){

		return dtmCorrected;
		
	}
	public double getRawAngle(){

		double rawAngle = (Rotation2d.fromDegrees(-RobotMap.navx.getAngle()).getRadians() % (2 * Math.PI));
		return (rawAngle < 0) ? 2 * Math.PI + rawAngle:rawAngle;

	}

	private enum Direction{

		left,right

	}
	
	public double getDistance(){
		return distance;
	}

	public double getCurrentX(){
		return centerOfRobotX;
	}

	public double getCurrentY(){
		return centerOfRobotY;
	}
}