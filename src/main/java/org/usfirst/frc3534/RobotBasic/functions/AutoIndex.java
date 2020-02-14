package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.IndexWheelState;

public class AutoIndex extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public AutoIndex(){

        reset();
        completed();
        started();
        this.state = State.index.s;

    }

    @Override
    public void process(){

        if(Robot.shooter.getLastDifference() < 2 && Robot.shooter.getDifference() < 2){

            this.state = State.index.s;

        }else{

            this.state = State.dead.s;

        }

        if(this.state == State.index.s){

            Robot.shooter.setIndexWheelState(IndexWheelState.feed);

        }

        if(this.state == State.dead.s){

            Robot.shooter.setIndexWheelState(IndexWheelState.off);

        }

    }

    private enum State{
        dead(-1),
        index(10);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}