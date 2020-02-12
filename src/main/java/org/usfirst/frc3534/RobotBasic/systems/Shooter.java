package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Shooter extends SystemBase implements SystemInterface {

    private WPI_TalonFX shooter = RobotMap.shooter;
    private WPI_TalonSRX topBelt = RobotMap.topBelt;
    private WPI_TalonSRX indexWheel = RobotMap.indexWheel;

    int shooterVelocity = 0;
    int prevShooterVelocity = 0;
    int indexWheelTargetPosition = 0;

    ShooterState shooterState = ShooterState.off;
    TopBeltState topBeltState = TopBeltState.off;
    IndexWheelState indexWheelState = IndexWheelState.off;

    public Shooter(){

    }

    @Override
    public void process(){

        switch(shooterState){
        case shoot:

            setShooterPower(shooterState.value); 

            break;

        case off:

            setShooterPower(shooterState.value); 

            break;

        }

        switch(topBeltState){
            case feed:
    
                setTopBeltPower(topBeltState.value); 
    
                break;
    
            case off:
    
                setTopBeltPower(topBeltState.value); 
    
                break;
    
            }

        switch(indexWheelState){
            case feed:
    
                setIndexWheelPower(indexWheelState.value); 
    
                break;

            case index:

                setIndexWheelPosition(indexWheelTargetPosition);
    
            case off:
    
                setIndexWheelPower(indexWheelState.value); 
    
                break;
    
            }

            prevShooterVelocity = shooterVelocity;
            shooterVelocity = shooter.getSelectedSensorVelocity();

    }

    public enum ShooterState{
        
        shoot(RobotMap.PowerOutput.shooter_shooter_shoot.power),
        off(0.0);

        double value;

        private ShooterState(double value){

            this.value = value;

        }
    }

    public double getShooterValue(){

        return shooterState.value;

    }

    public enum TopBeltState{
        
        feed(RobotMap.PowerOutput.shooter_topBelt_feed.power),
        off(0.0);

        double value;

        private TopBeltState(double value){

            this.value = value;

        }

    }

    public enum IndexWheelState{
        
        feed(RobotMap.PowerOutput.shooter_indexWheel_feed.power),
        index(RobotMap.PowerOutput.shooter_indexWheel_index.power),
        off(0.0);

        double value;

        private IndexWheelState(double value){

            this.value = value;

        }

    }
    public void setShooterState(ShooterState state){

        shooterState = state;

    }

    public ShooterState getShooterState(){

        return shooterState;

    }

    private void setShooterPower(double power){

        shooter.set(ControlMode.Velocity, power);

    }

    public int getShooterVelocity(){

        return shooterVelocity;

    }

    public int getPrevShooterVelocity(){

        return prevShooterVelocity;

    }

    public void setTopBeltState(TopBeltState state){

        topBeltState = state;

    }

    public TopBeltState getTopBeltState(){

        return topBeltState;

    }

    private void setTopBeltPower(double power){

        topBelt.set(ControlMode.PercentOutput, power);

    }

    public void setIndexWheelState(IndexWheelState state){

        indexWheelState = state;
        indexWheelTargetPosition = indexWheel.getSelectedSensorPosition() + (int)indexWheelState.value;

    }

    private void setIndexWheelPosition(int position){

        indexWheel.set(ControlMode.Position, position);

    }

    private void setIndexWheelPower(double power){

        indexWheel.set(ControlMode.PercentOutput, power);

    }
}