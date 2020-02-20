package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;

public class FunctionProcessor{

    /**
     * Create a new variable of each of the functions
     */

    ShootFar shootFar;
    ShootClose shootClose;
    Intake intake;
    RotationControl rotationControl;
    PositionControl positionControl;
    Translate translate;
    Climb climb;
    Elevate elevate;
    AutoIndex autoIndex;

    public FunctionProcessor(){

       /**
        * Instantiate each of the functions
        */

        shootFar = new ShootFar();
        shootClose = new ShootClose();
        intake = new Intake();
        rotationControl = new RotationControl();
        positionControl = new PositionControl();
        translate = new Translate();
        climb = new Climb();
        elevate = new Elevate();
        autoIndex = new AutoIndex();

    }

    public void process(){

       /**
        * Call all of the process methods in each of the functions
        * Pay special attention to the order in which the function
        * methods are called
        */

        if(!shootClose.isRunning()){

            shootFar.process();

        }
        if(!shootFar.isRunning() && !Robot.elevator.isWinchClimbing() && false){

            shootClose.process();

        }
        if(!Robot.elevator.isWinchClimbing()){

            intake.process();

        }
        if(!elevate.isRunning() && !climb.isRunning() && !positionControl.isRunning() && !Robot.elevator.isWinchClimbing() && false){

            rotationControl.process();

        }
        if(!elevate.isRunning() && !climb.isRunning() && !rotationControl.isRunning() && !Robot.elevator.isWinchClimbing() && false){

            positionControl.process();

        }
        if(!climb.isRunning()){

            translate.process();

        }
        if(!elevate.isRunning() && !positionControl.isRunning() && !rotationControl.isRunning() && !translate.isRunning()){

            climb.process();

        }
        if(!climb.isRunning() && !positionControl.isRunning() && !rotationControl.isRunning() && !translate.isRunning() && !Robot.elevator.isWinchClimbing()){

            elevate.process();

        }
        if(!shootFar.isRunning() && !shootClose.isRunning() && false){

            autoIndex.process();

        }
    }
}