package org.usfirst.frc3534.RobotBasic.functions;

import org.usfirst.frc3534.RobotBasic.OI;
import org.usfirst.frc3534.RobotBasic.Robot;
import org.usfirst.frc3534.RobotBasic.RobotMap;

public class FunctionProcessor{

    /**
     * Create a new variable of each of the functions
     */

    public ShootFar shootFar;
    //ShootClose shootClose;
    public Intake intake;
    ReverseIntake reverseIntake;
    RotationControl rotationControl;
    PositionControl positionControl;
    Translate translate;
    Climb climb;
    Elevate elevate;
    AutoIndexReset autoIndexReset;
    ManualIndex manualIndex;
    public AutoIndex autoIndex;

    public FunctionProcessor(){

       /**
        * Instantiate each of the functions
        */

        shootFar = new ShootFar();
      //  shootClose = new ShootClose();
        manualIndex = new ManualIndex();
        intake = new Intake();
        reverseIntake = new ReverseIntake();
        rotationControl = new RotationControl();
        positionControl = new PositionControl();
        translate = new Translate();
        climb = new Climb();
        elevate = new Elevate();
        autoIndex = new AutoIndex();
        autoIndexReset = new AutoIndexReset();

    }

    public void process(){

       /**
        * Call all of the process methods in each of the functions
        * Pay special attention to the order in which the function
        * methods are called
        */
        if(true/*!climb.isRunning() && /*!positionControl.isRunning() && !rotationControl.isRunning() &&*/ /*!translate.isRunning() && !Robot.elevator.isWinchClimbing()*/){

            // System.out.println("Elevate Processing...");
            elevate.process();

        }
        if(true){

            // System.out.println("Shoot Far Processing...");
            shootFar.process();

        }
        if(true){

            manualIndex.process();

        }
        if(!shootFar.isRunning()){

            autoIndexReset.process();
        
        }
        // if(!shootFar.isRunning() && !Robot.elevator.isWinchClimbing() && false){

        //     // System.out.println("Shoot Close Processing...");
        //     shootClose.process();

        // }
        if(!Robot.elevator.isWinchClimbing() && !elevate.isRunning() /*&& OI.Buttons.IntakeOverride.getButton()*/){

            // System.out.println("Intake Processing...");
            intake.process();

        }
        if(!intake.isRunning()){
            reverseIntake.process();
        }
        if(!elevate.isRunning() && !climb.isRunning() && !positionControl.isRunning() && !Robot.elevator.isWinchClimbing() && false){

            // System.out.println("Rotation Control Processing...");
            rotationControl.process();

        }
        if(!elevate.isRunning() && !climb.isRunning() && !rotationControl.isRunning() && !Robot.elevator.isWinchClimbing() && false){

            // System.out.println("Position Control Processing...");
            positionControl.process();

        }
        if(!climb.isRunning()){

            // System.out.println("Translate Processing...");
            translate.process();

        }
        if(!elevate.isRunning() && !positionControl.isRunning() && !rotationControl.isRunning() && !translate.isRunning()){

            // System.out.println("Climb Processing...");
            climb.process();

        }
        if(!shootFar.isRunning() && !manualIndex.isRunning() && !elevate.isRunning()/* && !shootClose.isRunning()*/){

            // System.out.println("AutoIndex Processing...");
            autoIndex.process();

        }
    }
}
