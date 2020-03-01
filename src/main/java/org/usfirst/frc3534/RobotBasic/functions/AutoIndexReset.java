package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;

public class AutoIndexReset extends FunctionBase implements FunctionInterface{

    long startTime = 0;

    public AutoIndexReset(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Buttons.IndexReset.getButton()){

            this.reset();

        }
        
        // System.out.println("Climb Cycle Start State: " + this.state);
        
        if(!Buttons.IndexReset.getButton() && running){


            this.state = State.end.s;
            // System.out.println("Climb Changed to State: " + this.state);

        }
        
        if(this.state == State.ready.s){

            if(Buttons.IndexReset.getButton()){

                startTime = System.currentTimeMillis();

                this.state = State.wait.s;
                this.started();
                
            }

        }

        if(this.state == State.wait.s){
            if(System.currentTimeMillis() - startTime > 2000){
                Robot.shooter.ballsShot = 0;
                Robot.shooter.ballsIndexed = 0;
                this.state = State.dead.s;
            }

        }

        if(this.state == State.dead.s){


        }

        if(this.state == State.end.s){

            reset();
            completed();
            // System.out.println("Climb Changed to State: " + this.state);

        }

    }

    private enum State{
        
        dead(-1),
        reset(20),
        ready(0),
        wait(10),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}
