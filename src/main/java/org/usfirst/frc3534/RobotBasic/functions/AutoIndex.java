package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Axes;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.IndexWheelState;

public class AutoIndex extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    int difference = 0;

    public AutoIndex(){

        reset();
        completed();
        started();
        this.state = State.dead.s;

    }

    @Override
    public void process(){
        
        // System.out.println("AutoIndex Cycle Start State: " + this.state);

        if((Robot.shooter.getLastDifference() < 2 && Robot.shooter.getDifference() < 2) && (Math.abs(Axes.Elevate_UpAndDown.getAxis()) >= .025)){

            if(Robot.shooter.getIndexWheelState() == IndexWheelState.off  && Robot.shooter.isBottomSensorBall()){

                difference = Robot.shooter.getDifference();
                this.state = State.index.s;
                // System.out.println("AutoIndex Changed to State: " + this.state);

            }

        }else{

            this.state = State.dead.s;
            // System.out.println("AutoIndex Changed to State: " + this.state);

        }

        if(this.state == State.index.s){

            if(Robot.shooter.getDifference() == difference) {

                Robot.shooter.setIndexWheelState(IndexWheelState.index);

            }else{

                Robot.shooter.setIndexWheelState(IndexWheelState.off);

            }

        }

        if(this.state == State.dead.s){

            Robot.shooter.setIndexWheelState(IndexWheelState.off);

        }

        if(Math.abs(Axes.Elevate_UpAndDown.getAxis()) >= .025){
            completed();
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
