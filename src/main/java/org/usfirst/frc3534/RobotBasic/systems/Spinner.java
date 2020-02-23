package org.usfirst.frc3534.RobotBasic.systems;

import org.usfirst.frc3534.RobotBasic.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorSensorV3;

public class Spinner extends SystemBase implements SystemInterface {

    private WPI_TalonSRX spinner = RobotMap.spinner;
    private ColorSensorV3 colorSensor = RobotMap.colorSensor;

    private int colorCount = 0;
    private Color previousColor;
    private boolean isColorCorrect = false;
    private Color correctColor;
    private Color currentColor;

    ColorMatch matcher;
    private final Color kBlue = ColorMatch.makeColor(0.121, 0.430, 0.447);
    private final Color kGreen = ColorMatch.makeColor(0.165, 0.587, 0.249);
    private final Color kRed = ColorMatch.makeColor(0.520, 0.356, 0.125);
    private final Color kYellow = ColorMatch.makeColor(0.320, 0.563, 0.114);
    private final Color kNoColor = ColorMatch.makeColor(0.0, 0.0, 0.0);

    SpinnerState spinnerState = SpinnerState.off;

    public Spinner(){

        matcher = new ColorMatch();
        matcher.addColorMatch(kBlue);
        matcher.addColorMatch(kGreen);
        matcher.addColorMatch(kRed);
        matcher.addColorMatch(kYellow);
        matcher.addColorMatch(kNoColor);
        previousColor = matcher.matchClosestColor(colorSensor.getColor()).color;

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

        Color currentColor = matcher.matchClosestColor(colorSensor.getColor()).color;
        if(currentColor != previousColor){

            colorCount++;

        }
        previousColor = currentColor;
        isColorCorrect = (currentColor == correctColor);

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
       //System.out.println("Spinner State set at " + spinnerState);

    }

    public SpinnerState getSpinnerState(){

        return spinnerState;

    }

    private void setSpinnerPower(double power){

        spinner.set(ControlMode.PercentOutput, power);

    }

    public int getColorCount(){

        return colorCount;

    }

    public void resetColorCount(){

        colorCount = 0;

    }

    public boolean isColorCorrect(){

        return isColorCorrect;

    }

    public void setCorrectColor(String gameData){

        switch (gameData.charAt(0)){

            case 'B' :
              //Blue case code
              correctColor = kGreen;
              break;
            case 'G' :
              //Green case code
              correctColor = kBlue;
              break;
            case 'R' :
              //Red case code
              correctColor = kYellow;
              break;
            case 'Y' :
              //Yellow case code
              correctColor = kRed;
              break;
            default :
              //This is corrupt data
              break;

        }

    }

    public boolean colorNoColor(){

        return currentColor == kNoColor;

    }


}