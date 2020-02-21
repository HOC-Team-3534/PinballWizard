package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.RobotMap;

import edu.wpi.first.wpilibj.util.Color;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorSensorV3;

public class Shooter extends SystemBase implements SystemInterface {

    private WPI_TalonFX shooter = RobotMap.shooter;
    private WPI_TalonSRX hood = RobotMap.hood;
    private WPI_TalonSRX topBelt = RobotMap.topBelt;
    private WPI_TalonSRX indexWheel = RobotMap.indexWheel;
    private ColorSensorV3 indexBottom = RobotMap.indexBottom;
    private ColorSensorV3 indexTop = RobotMap.indexTop;

    int shooterVelocity = 0;
    int prevShooterVelocity = 0;
    int initialHoodPosition;
    int indexWheelTargetPosition = 0;

    int ballsIndexed = 0;
    int ballsShot = 0;
    Color previousColor;
    int lastDifference = 0;

    ColorMatch matcher;
    Color kBall = ColorMatch.makeColor(0.0, 0.0, 0.0);
    Color kNoBall = ColorMatch.makeColor(0.0, 0.0, 0.0);

    ShooterState shooterState = ShooterState.off;
    HoodState hoodState = HoodState.close;
    TopBeltState topBeltState = TopBeltState.off;
    IndexWheelState indexWheelState = IndexWheelState.off;

    public Shooter(){

        initialHoodPosition = hood.getSelectedSensorPosition();

        matcher = new ColorMatch();
        matcher.addColorMatch(kBall);
        matcher.addColorMatch(kNoBall);
        previousColor = kBall;

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

        switch(hoodState){
            case far:
    
                setHoodPower(hoodState.value);
                if(hood.getSupplyCurrent() > RobotMap.spikeCurrent){
                    setHoodState(HoodState.off);
                } 
    
                break;

            case close:

                setHoodPower(hoodState.value);
                if(hood.getSupplyCurrent() > RobotMap.spikeCurrent){
                    setHoodState(HoodState.off);
                } 
    
                break;
    
            case off:
    
                setHoodPower(hoodState.value); 
    
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
    
            case off:
    
                setIndexWheelPower(indexWheelState.value); 
    
                break;
    
            }

        ballShot();
        ballIndexed();

    }

    public enum ShooterState{
        
        shoot(RobotMap.PowerOutput.shooter_shooter_shoot.power),
        off(0.0);

        double value;

        private ShooterState(double value){

            this.value = value;

        }
    }

    public enum HoodState{

        far(RobotMap.PowerOutput.shooter_hood_far.power),
        close(RobotMap.PowerOutput.shooter_hood_close.power),
        off(0.0);

        double value;

        private HoodState(double value){

            this.value = value;

        }

    }

    public int getHoodValue(){

        return initialHoodPosition + (int)hoodState.value;

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
        off(0.0);

        double value;

        private IndexWheelState(double value){

            this.value = value;

        }

    }
    public void setShooterState(ShooterState state){

        shooterState = state;
        System.out.println("Shooter State set at " + shooterState);

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

    public void setHoodState(HoodState state){

        hoodState = state;
        System.out.println("Hood State set at " + hoodState);

    }

    public HoodState getHoodState(){

        return hoodState;

    }

    private void setHoodPower(double power){

        hood.set(ControlMode.PercentOutput, power);

    }

    public boolean isHoodGood(){

        return hood.getSelectedSensorPosition() >= getHoodValue() - 30 && hood.getSelectedSensorPosition() <= getHoodValue() + 30;

    }

    public void setTopBeltState(TopBeltState state){

        topBeltState = state;
        System.out.println("Top Belt State set at " + topBeltState);

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
        System.out.println("Index Wheel State set at " + indexWheelState);

    }

    public IndexWheelState getIndexWheelState(){

        return indexWheelState;

    }

    private void setIndexWheelPower(double power){

        indexWheel.set(ControlMode.PercentOutput, power);

    }

    public boolean isBottomSensorBall(){

        return matcher.matchClosestColor(indexBottom.getColor()).color == kBall;

    }

    private void ballShot(){

        prevShooterVelocity = shooterVelocity;
        shooterVelocity = shooter.getSelectedSensorVelocity();
        int velocityDropLine = (int)shooterState.value - 500;
        ballsShot = (prevShooterVelocity > velocityDropLine && shooterVelocity <= velocityDropLine) ? ballsShot++ : ballsShot;

    }

    public void ballIndexed(){

        Color currentColor = matcher.matchClosestColor(indexTop.getColor()).color;
        ballsIndexed = (currentColor == kBall && previousColor == kNoBall) ? ballsIndexed++ : ballsIndexed;
        previousColor = currentColor;

    }

    public int getDifference(){

        return ballsIndexed - ballsShot;

    }

    //may need to use this value for something in the future if number of index balls changes
    public void setLastDifference(){

        lastDifference = getDifference();

    }

    public int getLastDifference(){

        return lastDifference;

    }

}