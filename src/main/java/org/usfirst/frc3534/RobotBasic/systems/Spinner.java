package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorSensorV3;

public class Spinner extends SystemBase implements SystemInterface {

    private WPI_TalonSRX spinner = RobotMap.spinner;

    private ColorSensorV3 colorSensor = RobotMap.colorSensor;

    SpinnerState spinnerState = SpinnerState.off;

    public Spinner(){

    }

    @Override
    public void process(){

        switch(spinnerState){
        case spin:

            setSpinnerPower(spinnerState.value); 

            break;

        case off:

            setSpinnerPower(spinnerState.value); 

            break;

        }

    }

    public enum SpinnerState{
        
        spin(RobotMap.PowerOutput.spinner_spinner_spin.power),
        off(0.0);

        double value;

        private SpinnerState(double value){

            this.value = value;

        }

    }

    public void setSpinnerState(SpinnerState state){

        spinnerState = state;

    }

    public SpinnerState getSpinnerState(){

        return spinnerState;

    }

    private void setSpinnerPower(double power){

        spinner.set(ControlMode.PercentOutput, power);

    }

}