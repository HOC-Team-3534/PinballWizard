package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;
import org.usfirst.frc3534.RobotBasic.OI.Axes;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.RobotMap.FunctionStateDelay;
import org.usfirst.frc3534.RobotBasic.systems.Elevator.ElevatorState;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeArmState;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeRollerState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.IndexWheelState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.ShooterState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.TopBeltState;
import org.usfirst.frc3534.RobotBasic.systems.Spinner.SpinnerState;

public class PositionControl extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public PositionControl(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Buttons.PositionControl.getButton()){

            this.reset();

        }
        
        if(this.state == State.ready.s){

            if(Buttons.PositionControl.getButton()){

                this.started();
                this.state = State.elevate.s;
                
            }

        }

        if(this.state == State.elevate.s){

            Robot.elevator.setElevatorState(ElevatorState.colorPosition);
            this.state = State.searchForColor.s;

        }

        if(this.state == State.searchForColor.s){

            /*if(colorsensor sees one of the colors){

            this.state = State.spin.s;

            }*/

        }

        if(this.state == State.spin.s){

            Robot.spinner.setSpinnerState(SpinnerState.spin);

            if(Robot.spinner.isColorCorrect()){

                Robot.spinner.setSpinnerState(SpinnerState.off);
                this.state = State.dead.s;

            } 

        }

        if(this.state == State.dead.s){

        }

        if(!Buttons.PositionControl.getButton()){

            if(Robot.spinner.getColorCount() > 0){

                this.state = State.end.s;

            }
            
        }

        if(this.state == State.end.s){

            Robot.elevator.setElevatorState(ElevatorState.startPosition);
            completed();

        }

    }

    private enum State{
        dead(-1),
        ready(0),
        elevate(10),
        searchForColor(20),
        spin(30),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}