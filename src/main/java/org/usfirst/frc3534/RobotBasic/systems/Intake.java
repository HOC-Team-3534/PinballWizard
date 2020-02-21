package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Intake extends SystemBase implements SystemInterface {

    private WPI_TalonSRX intakeArm = RobotMap.intakeArm;
    private WPI_TalonSRX intakeRoller = RobotMap.intakeRoller;

    long originalRollerTime;

    int ballsInPossession = 0;

    IntakeArmState intakeArmState = IntakeArmState.up;
    IntakeRollerState intakeRollerState = IntakeRollerState.off;

    public Intake(){

    }

    @Override
    public void process(){

        switch(intakeArmState){
            case up:
        
                if(intakeArm.getSupplyCurrent() > RobotMap.spikeCurrent){

                    System.out.println(intakeArm.getSupplyCurrent());   
                    setIntakeArmState(IntakeArmState.off);

                }
                setIntakeArmPower(intakeArmState.value);

                break;
            
            case down:

                if(intakeArm.getSupplyCurrent() > RobotMap.spikeCurrent){
                    setIntakeArmState(IntakeArmState.off);
                }
                setIntakeArmPower(intakeArmState.value); 

                break;

            case off:
                
                setIntakeArmPower(intakeArmState.value);  

                break;

        }

        //System.out.println(intakeArm.getSupplyCurrent());

        switch(intakeRollerState){
            case intake:

                if(intakeRoller.getSupplyCurrent() > RobotMap.rollerSpikeCurrent){

                    setIntakeRollerState(IntakeRollerState.burp);
                    originalRollerTime = System.currentTimeMillis();
                    

                }
    
                setIntakeRollerPower(intakeRollerState.value);
    
                break;

            case burp:

                if(System.currentTimeMillis() - originalRollerTime >= RobotMap.FunctionStateDelay.intakeRoller_burpDelay.time){

                    setIntakeRollerState(IntakeRollerState.intake);

                }

                setIntakeRollerPower(intakeRollerState.value);
    
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
        burp(RobotMap.PowerOutput.intake_intakeRoller_burp.power),
        off(0.0);

        double value;

        private IntakeRollerState(double value){

            this.value = value;

        }

    }

    public void setIntakeArmState(IntakeArmState state){

        intakeArmState = state;
        System.out.println("Intake arm State set at " + intakeArmState);

    }

    public IntakeArmState getIntakeArmState(){

        return intakeArmState;

    }

    private void setIntakeArmPower(double power){

        intakeArm.set(ControlMode.PercentOutput, power);

    }

    public void setIntakeRollerState(IntakeRollerState state){

        intakeRollerState = state;
        System.out.println("Intake Roller State set at " + intakeRollerState);

    }

    public IntakeRollerState getIntakeRollerState(){

        return intakeRollerState;

    }

    private void setIntakeRollerPower(double power){

        intakeRoller.set(ControlMode.PercentOutput, power);

    }

    public void shotBall(){

        ballsInPossession--;

    }

    public int getBallsInPossession(){
        
        return ballsInPossession;

    }
}