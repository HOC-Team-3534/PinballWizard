package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;
import org.usfirst.frc3534.RobotBasic.OI.Axes;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.RobotMap.FunctionStateDelay;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeArmState;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeRollerState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.IndexWheelState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.ShooterState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.TopBeltState;

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