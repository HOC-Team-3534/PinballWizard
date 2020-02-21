package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.systems.Elevator.ElevatorState;
import org.usfirst.frc3534.RobotBasic.systems.Spinner.SpinnerState;

public class RotationControl extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public RotationControl(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Buttons.RotationControl.getButton()){

            this.reset();

        }
        
        System.out.println("RotationControl Cycle Start State: " + this.state);
        
        if(!Buttons.RotationControl.getButton() && running && Robot.spinner.colorNoColor()){

            this.state = State.end.s;
            System.out.println("RotationControl Changed to State: " + this.state);

        }
        
        if(this.state == State.ready.s){

            if(Buttons.RotationControl.getButton()){

                this.started();
                this.state = State.elevate.s;
                System.out.println("RotationControl Changed to State: " + this.state);
                
            }

        }

        if(this.state == State.elevate.s){

            Robot.elevator.setElevatorState(ElevatorState.colorPosition);
            this.state = State.searchForColor.s;
            System.out.println("RotationControl Changed to State: " + this.state);

        }

        if(this.state == State.searchForColor.s){

            if(Robot.spinner.getColorCount() > 0){

                this.state = State.spin.s;
                System.out.println("RotationControl Changed to State: " + this.state);

            }

        }

        if(this.state == State.spin.s){

            Robot.spinner.setSpinnerState(SpinnerState.spin);

            if(Robot.spinner.getColorCount() >= 28){

                Robot.spinner.setSpinnerState(SpinnerState.off);
                Robot.spinner.resetColorCount();
                this.state = State.dead.s;
                System.out.println("RotationControl Changed to State: " + this.state);

            } 

        }

        if(this.state == State.dead.s){

        }

        if(this.state == State.end.s){

            Robot.elevator.setElevatorState(ElevatorState.startPosition);
            reset();
            completed();
            System.out.println("RotationControl Changed to State: " + this.state);

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
