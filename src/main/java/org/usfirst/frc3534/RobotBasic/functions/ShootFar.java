package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;
import org.usfirst.frc3534.RobotBasic.OI.Buttons;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.IndexWheelState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.ShooterState;
import org.usfirst.frc3534.RobotBasic.systems.Shooter.TopBeltState;

public class ShootFar extends FunctionBase implements FunctionInterface{

    long originalTime = 0;

    public ShootFar(){

        reset();
        completed();

    }

    @Override
    public void process(){

        if(!running && Buttons.ShootFar.getButton()){

            this.reset();

        }
        
        if(this.state == State.ready.s){

            if(Buttons.ShootFar.getButton()){

                this.started();
                this.state = State.prepare.s;
                
            }

        }

        if(this.state == State.prepare.s) {

            Robot.shooter.setShooterState(ShooterState.shoot);
            Robot.drive.setDtmEnabled(true);
            if(Robot.shooter.getShooterVelocity() == RobotMap.PowerOutput.shooter_shooter_shoot.power && Robot.drive.getDtmCorrected()) {

                this.state = State.shoot.s;

            }

        }

        if(this.state == State.shoot.s){

            Robot.shooter.setTopBeltState(TopBeltState.feed);
            Robot.shooter.setIndexWheelState(IndexWheelState.feed);
            this.state = State.dead.s;

        }

        if(this.state == State.dead.s){
            
        }

        if(!Buttons.ShootFar.getButton()){

            this.state = State.end.s;

        }

        if(this.state == State.end.s){
            
            Robot.drive.setDtmEnabled(false);
            Robot.shooter.setShooterState(ShooterState.off);
            Robot.shooter.setTopBeltState(TopBeltState.off);
            Robot.shooter.setIndexWheelState(IndexWheelState.off);
            Robot.shooter.setLastDifference();
            completed();

        }

    }

    private enum State{
        dead(-1),
        ready(0),
        prepare(10),
        shoot(20),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

}