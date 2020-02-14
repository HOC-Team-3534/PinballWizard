package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.OI.Axes;
import org.usfirst.frc3534.RobotBasic.systems.Elevator.TranslatorState;

public class Translate extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public Translate(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Math.abs(Axes.Translate_FartherAndCloser.getAxis()) >= Robot.elevator.translateDeadband){

            this.reset();

        }
        
        if(this.state == State.ready.s){

            if(Math.abs(Axes.Translate_FartherAndCloser.getAxis()) >= Robot.elevator.translateDeadband){

                this.started();
                this.state = State.translate.s;
                
            }  
        
        }

        if(this.state == State.translate.s){
        
            Robot.elevator.setTranslatorState(TranslatorState.farther_closer);
            this.state = State.dead.s;
        
        }

        if(this.state == State.dead.s){

        }

        if(Math.abs(Axes.Translate_FartherAndCloser.getAxis()) < Robot.elevator.translateDeadband){
        
            this.state = State.end.s;

        }

        if(this.state == State.end.s){
            
            completed();

        }

    }

    private enum State{
        dead(-1),
        ready(0),
        translate(10),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}