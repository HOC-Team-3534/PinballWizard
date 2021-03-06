// package org.usfirst.frc3534.RobotBasic.functions;

// import org.usfirst.frc3534.RobotBasic.Robot;
// import org.usfirst.frc3534.RobotBasic.RobotMap;
// import org.usfirst.frc3534.RobotBasic.OI.Buttons;
// import org.usfirst.frc3534.RobotBasic.systems.Shooter.HoodState;
// import org.usfirst.frc3534.RobotBasic.systems.Shooter.IndexWheelState;
// import org.usfirst.frc3534.RobotBasic.systems.Shooter.ShooterState;
// import org.usfirst.frc3534.RobotBasic.systems.Shooter.TopBeltState;

// public class ShootClose extends FunctionBase implements FunctionInterface{

//     long originalTime = 0;

//     public ShootClose(){

//         reset();
//         completed();

//     }

//     @Override
//     public void process(){

//         if(!running && Buttons.ShootClose.getButton()){

//             this.state = State.end.s;

//         }
        
//         // System.out.println("ShootClose Cycle Start State: " + this.state);
        
//         if(!Buttons.ShootFar.getButton() && running){

//             this.state = State.end.s;
//             // System.out.println("ShootClose Changed to State: " + this.state);

//         }
        
//         if(this.state == State.ready.s){

//             if(Buttons.ShootClose.getButton()){

//                 this.started();
//                 this.state = State.prepare.s;
//                 // System.out.println("ShootClose Changed to State: " + this.state);
                
//             }

//         }

//         if(this.state == State.prepare.s) {

//             Robot.shooter.setShooterState(ShooterState.shoot);
//             //Robot.shooter.setHoodState(HoodState.close);
//             // if(Robot.shooter.getShooterVelocity() == RobotMap.PowerOutput.shooter_shooter_shoot.power && Robot.shooter.isHoodGood()) {

//             //     this.state = State.shoot.s;
//             //     // System.out.println("ShootClose Changed to State: " + this.state);

//             // }

//         }

//         if(this.state == State.shoot.s){

//             Robot.shooter.setTopBeltState(TopBeltState.feed);
//             Robot.shooter.setIndexWheelState(IndexWheelState.feed);
//             this.state = State.dead.s;
//             // System.out.println("ShootClose Changed to State: " + this.state);

//         }

//         if(this.state == State.dead.s){
            
//         }

//         if(this.state == State.end.s){
            
//             Robot.shooter.setHoodState(HoodState.far);
//             Robot.shooter.setShooterState(ShooterState.off);
//             Robot.shooter.setTopBeltState(TopBeltState.off);
//             Robot.shooter.setIndexWheelState(IndexWheelState.off);
//             Robot.shooter.setLastDifference();
//             reset();
//             completed();
//             // System.out.println("ShootClose Changed to State: " + this.state);

//         }

//     }

//     private enum State{
//         dead(-1),
//         ready(0),
//         prepare(10),
//         shoot(20),
//         end(100);

//         int s;

//         private State(int s){
//             this.s = s;
//         }
//     }

// }
