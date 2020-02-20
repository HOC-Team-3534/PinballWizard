package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;
import org.usfirst.frc3534.RobotBasic.OI.Axes;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator extends SystemBase implements SystemInterface {

    private WPI_TalonSRX elevator = RobotMap.elevator;
    private WPI_TalonFX winch = RobotMap.winch;
    private WPI_TalonSRX translator = RobotMap.translator;

    private int initialElevatorPosition; 
    private int maxElevatorPosition;
    public double deadband = 0.25;
    int elevatorTargetPosition;
    int targetPosition;
    private boolean colorWheelHeightReached = false;      
    private int initialWinchPosition;
    private int maxWinchPosition;
    public double translateDeadband = 0.25;

    ElevatorState elevatorState = ElevatorState.off;
    WinchState winchState = WinchState.off;
    TranslatorState translatorState = TranslatorState.off;

    public Elevator(){

        initialElevatorPosition = elevator.getSelectedSensorPosition();
        maxElevatorPosition = initialElevatorPosition + RobotMap.elevator_maxHeight;
        initialWinchPosition = winch.getSelectedSensorPosition();
        maxWinchPosition = initialWinchPosition + RobotMap.winch_maxPosition;

    }

    @Override
    public void process(){

        System.out.println("Elevator State : " + elevatorState);

        switch(elevatorState){
        case up_down:

            boolean negative = false;
            double input = Axes.Elevate_UpAndDown.getAxis();
            if(input < 0){
                negative = true;
            }
            input = Math.abs(input);
            if(input >= deadband){
                input -= deadband;
                input *= (1 / (1 - deadband));
            }else{
                input = 0;
            }

            System.out.println("input number before negation" + input);

            if(negative){
                input = -input;
            }

            System.out.println("input number after negation" + input);

            System.out.println("amount to add or subtract from position" + (int)Math.floor(input * elevatorState.value));
            System.out.println("elevator position " + elevator.getSelectedSensorPosition());

            int amountPosition = (int)Math.floor(input * elevatorState.value);

            if(Math.abs(amountPosition) > 0){
                elevatorTargetPosition = elevator.getSelectedSensorPosition() + amountPosition;
            }
            if(elevatorTargetPosition < initialElevatorPosition){
                elevatorTargetPosition = initialElevatorPosition;
            }else if(elevatorTargetPosition > maxElevatorPosition){
                elevatorTargetPosition = maxElevatorPosition;
            }

            System.out.print("controller: " + Axes.Elevate_UpAndDown.getAxis());
            System.out.print(", target: " + elevatorTargetPosition);
            System.out.print(", power out: " + elevator.get());
            System.out.println(", initial: " + initialElevatorPosition);

            setElevatorPosition(elevatorTargetPosition);
            elevator.setNeutralMode(NeutralMode.Brake);

        case colorPosition:

            if(colorWheelHeightReached){

            }else{

                targetPosition = elevator.getSelectedSensorPosition() + (int)Math.floor(elevatorState.value * RobotMap.PowerOutput.elevator_elevator_maxupdown.power);
                if(targetPosition > maxElevatorPosition){
                    targetPosition = maxElevatorPosition;
                }
                setElevatorPosition(targetPosition); 
                elevator.setNeutralMode(NeutralMode.Brake);
        
            }

            break;

        case startPosition:

            setElevatorPosition(initialElevatorPosition);
            elevator.setNeutralMode(NeutralMode.Brake);
            
            break;

        case removeResistance:

            setElevatorPower(elevatorState.value);    
            elevator.setNeutralMode(NeutralMode.Coast); 

            break;

        case off:

            setElevatorPower(elevatorState.value);
            elevator.setNeutralMode(NeutralMode.Coast); 

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
    
                boolean negative = false;
                double input = Axes.Translate_FartherAndCloser.getAxis();
                if(input < 0){
                    negative = true;
                }
                input = Math.abs(input);
                input -= translateDeadband;
                input *= (1 / (1 - translateDeadband));
                if(negative){
                    input = -input;
                }
                double targetPower = Axes.Translate_FartherAndCloser.getAxis() * translatorState.value;
                
                if(Robot.drive.getAngle().getDegrees() >= 180 && Robot.drive.getAngle().getDegrees() < 360){

                    targetPower = -targetPower;

                }

                setTranslatorPower(targetPower);
    
                break;
    
            case  off:
    
                setTranslatorPower(translatorState.value);
    
                break;
    
        }
    }

    public enum ElevatorState{
        
        up_down(RobotMap.PowerOutput.elevator_elevator_maxupdown.power),
        colorPosition(RobotMap.PowerOutput.elevator_elevator_colorWheel.power),
        startPosition(RobotMap.PowerOutput.elevator_elevator_maxupdown.power),
        removeResistance(RobotMap.PowerOutput.elevator_elevator_removeResistance.power),
        off(0.0);

        double value;

        private ElevatorState(double value){

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

    public boolean isWinchClimbing(){

        return winch.getSelectedSensorPosition() - initialWinchPosition > RobotMap.winchLimit;

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