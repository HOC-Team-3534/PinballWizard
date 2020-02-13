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

public class Climb extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public Climb(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Buttons.Climb.{

            this.reset();

        }
        
        if(this.state == State.ready.s){

            if(Math.abs(Axes.Elevate_UpAndDown.getAxis()) >= 0.25){

                this.started();
                this.state = State.elevate.s;
                
            }

        }

        if(this.state == State.elevate.s){

            Robot.elevator.setElevatorState(ElevatorState.up_down);
            this.state = State.dead.s;

        }

        if(this.state == State.dead.s){

        }

        if(Math.abs(Axes.Elevate_UpAndDown.getAxis()) < 0.25){

            this.state = State.end.s;

        }

        if(this.state == State.end.s){

            completed();

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