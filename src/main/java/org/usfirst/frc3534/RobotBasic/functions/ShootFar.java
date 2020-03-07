package org.usfirst.frc3534.RobotBasic.functions;

import javax.sound.midi.SysexMessage;

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

        if(!running && (Buttons.ShootFar.getButton() || Buttons.ShootFarBackUp.getButton())){

            this.reset();

        }
        
        // System.out.println("ShootFar Cycle Start State: " + this.state);
        
        if(((!Buttons.ShootFar.getButton() && !Buttons.ShootFarBackUp.getButton()) && !Robot.isAutonomous) && running){

            this.state = State.end.s;
            // System.out.println("ShootFar Changed to State: " + this.state);

        }

        if((Robot.isAutonomous && Robot.autonomousFunctionsDead) && running){

            this.state = State.end.s;

        }
        
        if(this.state == State.ready.s){

            if(Buttons.ShootFar.getButton() || (Robot.isAutonomous && !Robot.autonomousFunctionsDead)){

                this.started();
                this.state = State.prepare.s;
                // System.out.println("ShootFar Changed to State: " + this.state);
                
            } else if (Buttons.ShootFarBackUp.getButton()){

                this.started();
                this.state = State.prepareBackUp.s;
                // System.out.println("ShootFar Changed to State: " + this.state);

            }

        }

        if(this.state == State.prepare.s) {

            Robot.shooter.setShooterState(ShooterState.shootInner);
            Robot.drive.setDtmEnabled(true);
            //// System.out.println(Robot.shooter.getShooterVelocity());
            if(Robot.shooter.getShooterVelocity() >= (Math.floor((0.1076 * Math.pow((Robot.drive.getDistance()), 2) - (42.0584 * Robot.drive.getDistance()) + 19325))) - 250 && Robot.drive.getDtmCorrected()) {

                this.state = State.shoot.s;
                // System.out.println("ShootFar Changed to State: " + this.state);

            }

        }

        if(this.state == State.prepareBackUp.s) {

            Robot.shooter.setShooterState(ShooterState.shootConstant);
            //// System.out.println(Robot.shooter.getShooterVelocity());
            if(Robot.shooter.getShooterVelocity() >= RobotMap.PowerOutput.shooter_shooter_shootConstant.power - 250) {

                this.state = State.shoot.s;
                // System.out.println("ShootFar Changed to State: " + this.state);

            }

        }

        if(this.state == State.shoot.s){

            Robot.shooter.setTopBeltState(TopBeltState.feed);
            Robot.shooter.setIndexWheelState(IndexWheelState.feed);
            this.state = State.dead.s;
            // System.out.println("ShootFar Changed to State: " + this.state);

        }

        if(this.state == State.dead.s){
            
        }

        if(this.state == State.end.s){
            
            Robot.drive.setDtmEnabled(false);
            Robot.shooter.setShooterState(ShooterState.off);
            Robot.shooter.setTopBeltState(TopBeltState.off);
            Robot.shooter.setIndexWheelState(IndexWheelState.off);
            Robot.shooter.setLastDifferenceOverrride(0);
            reset();
            completed();
            // System.out.println("ShootFar Changed to State: " + this.state);

        }

    }

    public enum State{
        dead(-1),
        ready(0),
        prepare(10),
        prepareBackUp(15),
        shoot(20),
        end(100);

        int s;

        private State(int s){
            this.s = s;
        }
    }

    public void setState(State selectedState){
        this.state = selectedState.s;
    }

}
