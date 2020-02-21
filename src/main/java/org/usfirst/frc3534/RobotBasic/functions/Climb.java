package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.systems.Elevator.ElevatorState;
import org.usfirst.frc3534.RobotBasic.systems.Elevator.WinchState;

public class Climb extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public Climb(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Buttons.Climb.getButton()){

            this.reset();

        }
        
        System.out.println("Climb Cycle Start State: " + this.state);
        
        if(this.state == State.ready.s){

            if(Buttons.Climb.getButton()){

                this.started();
                this.state = State.climb.s;
                System.out.println("Climb Changed to State: " + this.state);
                
            }

        }

        if(this.state == State.climb.s){

            Robot.elevator.setWinchState(WinchState.winch);
            Robot.elevator.setElevatorState(ElevatorState.removeResistance);
            this.state = State.dead.s;
            System.out.println("Climb Changed to State: " + this.state);

        }

        if(this.state == State.dead.s){

            if(!Buttons.Climb.getButton()){

                this.state = State.end.s;
                System.out.println("Climb Changed to State: " + this.state);
    
            }

        }

        if(this.state == State.end.s){

            Robot.elevator.setWinchState(WinchState.off);
            Robot.elevator.setElevatorState(ElevatorState.off);
            reset();
            completed();
            System.out.println("Climb Changed to State: " + this.state);

        }

    }

    private enum State{
        
        dead(-1),
        ready(0),
        climb(10),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}
