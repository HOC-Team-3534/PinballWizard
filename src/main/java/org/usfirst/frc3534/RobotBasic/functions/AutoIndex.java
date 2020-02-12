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

    }

    @Override
    public void process(){

        if(!running && Axes.Intake.getAxis() >= 0.5){

            this.reset();

        }
        
        if(this.state == State.ready.s){

            if(Axes.Intake.getAxis() >= 0.5){

                this.started();
                this.state = State.intake.s;
                
            }

        }

        if(this.state == State.intake.s){

            Robot.intake.setIntakeArmState(IntakeArmState.down);
            Robot.intake.setIntakeRollerState(IntakeRollerState.intake);
            this.state = State.dead.s;

        }

        if(this.state == State.dead.s){

        }

        if(Axes.Intake.getAxis() < 0.5){

            this.state = State.end.s;

        }

        if(this.state == State.end.s){

            Robot.intake.setIntakeArmState(IntakeArmState.up);
            Robot.intake.setIntakeRollerState(IntakeRollerState.off);
            completed();

        }

    }

    private enum State{
        dead(-1),
        ready(0),
        intake(10),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}