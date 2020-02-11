package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.RobotMap;
import org.usfirst.frc3534.RobotBasic.OI.Axes;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator extends SystemBase implements SystemInterface {

    private WPI_TalonSRX elevator = RobotMap.elevator;
    private WPI_TalonFX winch = RobotMap.winch;
    private WPI_TalonSRX translator = RobotMap.translator;

    private final int initialElevatorPosition = elevator.getSelectedSensorPosition();
    private final int maxElevatorPosition = initialElevatorPosition + RobotMap.elevator_maxHeight;
    private int elevatorPosition = elevator.getSelectedSensorPosition();

    ElevatorState elevatorState = ElevatorState.off;
    WinchState winchState = WinchState.off;
    TranslatorState translatorState = TranslatorState.off;

    public Elevator(){

    }

    @Override
    public void process(){

        switch(elevatorState){
        case up_down:

            int targetPosition = elevator.getSelectedSensorPosition() + (int)Math.floor(Axes.Elevate_UpAndDown.getAxis() * elevatorState.value);
            if(targetPosition < initialElevatorPosition){
                targetPosition = initialElevatorPosition;
            }else if(targetPosition > maxElevatorPosition){
                targetPosition = maxElevatorPosition;
            }
            setElevatorPosition(targetPosition);

        case position:

            setElevatorPosition(initialElevatorPosition + elevatorState.value); 

            break;

        case off:

            setElevatorPower(elevatorState.value); 

            break;

        }

        switch(winchState){
            case winch:
    
                setWinchPower(winchState.value); 
    
                break;
    
            case off:
    
                setWinchPower(winchState.value); 
    
                break;
    
        }

        switch(translatorState){
            case farther_closer:
    
                double targetPower = Axes.Translate_FartherAndCloser.getAxis() * translatorState.value;
                //Need to add in logic for navx for field centric motion
                setTranslatorPower(targetPower);
    
                break;
    
            case  off:
    
                setTranslatorPower(translatorState.value);
    
                break;
    
        }
    }

    public enum ElevatorState{
        
        up_down((int)RobotMap.PowerOutput.elevator_elevator_maxupdown.power),
        position((int)RobotMap.PowerOutput.elevator_elevator_colorWheelPosition.power),
        off(0);

        int value;

        private ElevatorState(int value){

            this.value = value;

        }

    }

    public enum WinchState{
        
        winch(RobotMap.PowerOutput.elevator_winch_winch.power),
        off(0.0);

        double value;

        private WinchState(double value){

            this.value = value;

        }

    }

    public enum TranslatorState{
        
        farther_closer(RobotMap.PowerOutput.elevator_translator_maxOutput.power),
        off(0.0);

        double value;

        private TranslatorState(double value){

            this.value = value;

        }

    }

    public void setElevatorState(ElevatorState state){

        elevatorState = state;

    }

    public ElevatorState getElevatorState(){

        return elevatorState;

    }

    private void setElevatorPosition(int position){

        elevator.set(ControlMode.Position, position);

    }

    private void setElevatorPower(double power){

        elevator.set(ControlMode.PercentOutput, power);

    }

    public void setWinchState(WinchState state){

        winchState = state;

    }

    public WinchState getWinchState(){

        return winchState;

    }

    private void setWinchPower(double power){

        winch.set(ControlMode.PercentOutput, power);

    }

    public void setTranslatorState(TranslatorState state){

        translatorState = state;

    }

    public TranslatorState getTranslatorState(){

        return translatorState;

    }

    private void setTranslatorPower(double power){

        translator.set(ControlMode.PercentOutput, power);

    }
}