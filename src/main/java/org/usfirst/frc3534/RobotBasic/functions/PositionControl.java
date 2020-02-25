package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.systems.Elevator.ElevatorState;
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
        
        // System.out.println("PositionControl Cycle Start State: " + this.state);
                    
        if(!Buttons.PositionControl.getButton() && running && Robot.spinner.colorNoColor()){

            this.state = State.end.s;
            // System.out.println("PositionControl Changed to State: " + this.state);

        
        }
        
        if(this.state == State.ready.s){

            if(Buttons.PositionControl.getButton()){

                this.started();
                this.state = State.elevate.s;
                // System.out.println("PositionControl Changed to State: " + this.state);
                
            }

        }

        if(this.state == State.elevate.s){

            Robot.elevator.setElevatorState(ElevatorState.colorPosition);
            this.state = State.searchForColor.s;
            // System.out.println("PositionControl Changed to State: " + this.state);

        }

        if(this.state == State.searchForColor.s){

            /*if(colorsensor sees one of the colors){

            this.state = State.spin.s;
            // System.out.println("PositionControl Changed to State: " + this.state);

            }*/

        }

        if(this.state == State.spin.s){

            Robot.spinner.setSpinnerState(SpinnerState.spin);

            if(Robot.spinner.isColorCorrect()){

                Robot.spinner.setSpinnerState(SpinnerState.off);
                this.state = State.dead.s;
                // System.out.println("PositionControl Changed to State: " + this.state);

            } 

        }

        if(this.state == State.dead.s){


        }

        if(this.state == State.end.s){

            Robot.elevator.setElevatorState(ElevatorState.startPosition);
            completed();
            // System.out.println("PositionControl Changed to State: " + this.state);

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
