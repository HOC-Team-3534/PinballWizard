package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Intake extends SystemBase implements SystemInterface {

    private WPI_TalonSRX intakeArm = RobotMap.intakeArm;
    private WPI_TalonSRX intakeRoller = RobotMap.intakeRoller;

    IntakeArmState intakeArmState = IntakeArmState.off;
    IntakeRollerState intakeRollerState = IntakeRollerState.off;

    public Intake(){

    }

    @Override
    public void process(){

        switch(intakeArmState){
            case up:

                setIntakeArmPower(intakeArmState.value); 

                break;
            
            case down:

                setIntakeArmPower(intakeArmState.value); 

                break;

            case off:

                setIntakeArmPower(intakeArmState.value);  

                break;

        }

        switch(intakeRollerState){
            case intake:
    
                setIntakeRollerPower(intakeRollerState.value);
    
                break;
    
            case off:
    
                setIntakeRollerPower(intakeRollerState.value); 
    
                break;
    
            }

    }

    public enum IntakeArmState{
        
        up(RobotMap.PowerOutput.intake_intakeArm_armUp.power),
        down(RobotMap.PowerOutput.intake_intakeArm_armDown.power),
        off(0.0);

        double value;

        private IntakeArmState(double value){

            this.value = value;

        }

    }

    public enum IntakeRollerState{
        
        intake(RobotMap.PowerOutput.intake_intakeRoller_intake.power),
        off(0.0);

        double value;

        private IntakeRollerState(double value){

            this.value = value;

        }

    }

    public void setIntakeArmState(IntakeArmState state){

        intakeArmState = state;

    }

    public IntakeArmState getIntakeArmState(){

        return intakeArmState;

    }

    private void setIntakeArmPower(double power){

        intakeArm.set(ControlMode.PercentOutput, power);

    }

    public void setIntakeRollerState(IntakeRollerState state){

        intakeRollerState = state;

    }

    public IntakeRollerState getIntakeRollerState(){

        return intakeRollerState;

    }

    private void setIntakeRollerPower(double power){

        intakeRoller.set(ControlMode.PercentOutput, power);

    }

}