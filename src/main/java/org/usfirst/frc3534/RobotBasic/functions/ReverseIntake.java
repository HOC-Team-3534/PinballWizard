package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeArmState;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeRollerState;

public class ReverseIntake extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public ReverseIntake(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Buttons.ReverseIntake.getButton()){

            this.reset();

        }

        // System.out.println("Intake Cycle Start State: " + this.state);

        if(!Buttons.ReverseIntake.getButton() && running){

            this.state = State.end.s;
            // System.out.println("Intake Changed to State: " + this.state);

        }
        
        if(this.state == State.ready.s){

            if(Buttons.ReverseIntake.getButton()){

                this.started();
                this.state = State.revereseIntake.s;
               //  System.out.println("Intake Changed to State: " + this.state);
                
            }

        }

        if(this.state == State.revereseIntake.s){

            Robot.intake.setIntakeRollerState(IntakeRollerState.reverseIntake);
            this.state = State.dead.s;
            // System.out.println("Intake Changed to State: " + this.state);

        }

        if(this.state == State.dead.s){

        }

        if(this.state == State.end.s){

            Robot.intake.setIntakeRollerState(IntakeRollerState.off);
            reset();
            completed();
            // System.out.println("Intake Changed to State: " + this.state);

        }

    }

    private enum State{
        dead(-1),
        ready(0),
        revereseIntake(10),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}
