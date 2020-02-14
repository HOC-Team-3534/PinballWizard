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
        
        if(this.state == State.ready.s){

            if(Buttons.Climb.getButton()){

                this.started();
                this.state = State.climb.s;
                
            }

        }

        if(this.state == State.climb.s){

            Robot.elevator.setWinchState(WinchState.winch);
            Robot.elevator.setElevatorState(ElevatorState.removeResistance);
        }

        if(this.state == State.dead.s){

        }

        if(!Buttons.Climb.getButton()){

            this.state = State.end.s;

        }

        if(this.state == State.end.s){

            Robot.elevator.setWinchState(WinchState.off);
            Robot.elevator.setElevatorState(ElevatorState.off);
            completed();

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