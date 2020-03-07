package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeArmState;
import org.usfirst.frc3534.RobotBasic.systems.Intake.IntakeRollerState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.IndexWheelState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.ShooterState;

public class ManualIndex extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public ManualIndex(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && (Buttons.ManualIndex.getButton() || Buttons.ReverseIndex.getButton())){

            this.reset();

        }

        // System.out.println("Intake Cycle Start State: " + this.state);

        if((!Buttons.ManualIndex.getButton() || !Buttons.ReverseIndex.getButton()) && running){

            this.state = State.end.s;
            // System.out.println("Intake Changed to State: " + this.state);

        }
        
        if(this.state == State.ready.s){

            if(Buttons.ReverseIndex.getButton()){

                this.started();
                this.state = State.reverseIndex.s;
               //  System.out.println("Intake Changed to State: " + this.state);
                
            }else if(Buttons.ManualIndex.getButton()){

                this.started();
                this.state = State.manualIndex.s;

            }

        }

        if(this.state == State.reverseIndex.s){

            Robot.shooter.setIndexWheelState(IndexWheelState.reverseIndex);
            this.state = State.dead.s;
            // System.out.println("Intake Changed to State: " + this.state);

        }

        if(this.state == State.manualIndex.s){

            Robot.shooter.setIndexWheelState(IndexWheelState.manualIndex);
            this.state = State.dead.s;
            // System.out.println("Intake Changed to State: " + this.state);

        }

        if(this.state == State.dead.s){

        }

        if(this.state == State.end.s){

            Robot.shooter.setIndexWheelState(IndexWheelState.off);
            reset();
            completed();
            // System.out.println("Intake Changed to State: " + this.state);

        }

    }

    private enum State{
        dead(-1),
        ready(0),
        reverseIndex(10),
        manualIndex(20),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}
