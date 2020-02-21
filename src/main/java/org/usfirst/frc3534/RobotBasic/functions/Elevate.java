package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Axes;
import org.usfirst.frc3534.RobotBasic.systems.Elevator.ElevatorState;

public class Elevate extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public Elevate(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Math.abs(Axes.Elevate_UpAndDown.getAxis()) >= Robot.elevator.deadband){

            this.reset();

        }
        
        System.out.println("Elevate Cycle Start State: " + this.state);
        
        if(this.state == State.ready.s){

            if(Math.abs(Axes.Elevate_UpAndDown.getAxis()) >= Robot.elevator.deadband){

                this.started();
                this.state = State.elevate.s;
                System.out.println("Elevate Changed to State: " + this.state);
                
            }

        }

        if(this.state == State.elevate.s){

            Robot.elevator.setElevatorState(ElevatorState.up_down);
            this.state = State.dead.s;
            System.out.println("Elevate Changed to State: " + this.state);

        }

        if(this.state == State.dead.s){

            if(Math.abs(Axes.Elevate_UpAndDown.getAxis()) < Robot.elevator.deadband){

                this.state = State.end.s;
                System.out.println("Elevate Changed to State: " + this.state);
    
            }

        }

        if(this.state == State.end.s){

            //Robot.elevator.setElevatorState(ElevatorState.stop);
            reset();
            completed();
            System.out.println("Elevate Changed to State: " + this.state);

        }

    }

    private enum State{
        dead(-1),
        ready(0),
        elevate(10),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}
