package org.firstinspires.ftc.teamcode.Autonomous.Main;/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.Chassis.Drivechain;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;


/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Freight Frenzy game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@Autonomous(name = "AutonomousPos1", group = "Concept")
public class AutonomousPos1 extends LinearOpMode {
    /* Note: This sample uses the all-objects Tensor Flow model (FreightFrenzy_BCDM.tflite), which contains
     * the following 4 detectable objects
     *  0: Ball,
     *  1: Cube,
     *  2: Duck,
     *  3: Marker (duck location tape marker)
     *
     *  Two additional model assets are available which only contain a subset of the objects:
     *  FreightFrenzy_BC.tflite  0: Ball,  1: Cube
     *  FreightFrenzy_DM.tflite  0: Duck,  1: Marker
     */
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "ARHVqML/////AAABmdwV3COoyUo6te5Z9nV9Xbs58R8vE55rTErE0ztbuXhfaoos0oD/3ZcFBeJ+b0gLISGqWdDOBM9m4cv6rMlzbJ2qLTB9KX5EpbWfKO2fo9LUIYHLWbre2dui4BfhgLuvKxP8nT/yBsEjAUVz61Bzf3gIEFPTaF8jAnVLwUmYO2Y7/8bXyCNTCoYnC74qHS9D0mqbg+LlGVelz4Zg3zpFfIgwYi56uvaTpdVAxYmPao5JQ0h9FJYLuvfPs9znZEU6QNkS83GVoRm5/cd4S52lWr1jcoeFWg2Haqn7wxKfFGgS7fB41O9wxOe/FHO5Yz4RV0jfYp7M97PxUOvE+c6tOipsSIJnL0aZwYHPRBbX48jA ";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

//    DcMotor motoryay;
//    DcMotor topLeftMotor;
//    DcMotor topRightMotor;
//    DcMotor bottomLeftMotor;
//    DcMotor bottomRightMotor;
//    DcMotor motor1;


    @Override


    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();
        Drivechain AutonomousDC = new Drivechain(hardwareMap);
//        motoryay = hardwareMap.get(DcMotor.class, "motor1");
//        topLeftMotor = hardwareMap.get(DcMotor.class, "tLMotor");
//        topRightMotor = hardwareMap.get(DcMotor.class, "tRMotor");
//        bottomLeftMotor = hardwareMap.get(DcMotor.class, "bLMotor");
//        bottomRightMotor = hardwareMap.get(DcMotor.class, "bRMotor");
//        motor1 = hardwareMap.get(DcMotor.class, "motor1");

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.5, 16.0 / 9.0);
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();
        telemetry.addData(">", "You never felt the touch of a woman");
        telemetry.update();
        if (opModeIsActive()) {
            AutonomousDC.switchToPower();
            AutonomousDC.runRobot(0.15, -0.15, 0.15, -0.15);


            while (opModeIsActive()) {

                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.

                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    // if (updatedRecognitions == null){

                    // }
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.

                        int i = 0;
//                        if (updatedRecognitions.size() == 0){
//                            motoryay.setPower(0.0);
//                        }

                        boolean recog = false;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            telemetry.addData(String.format(" confidence: ", i), recognition.getConfidence());
                            i++;
                            detectPosition results = getTeamElementPosition(recognition);
                            if (results == detectPosition.left) {
                                telemetry.addData("Block position: ", "right");
                                AutonomousDC.moveAutonomousRobotPOS1("var1", "right");
                                break;

                            } else if (results == detectPosition.center) {
                                telemetry.addData("Block position: ", "center");
                            } else if (results == detectPosition.right) {
                                telemetry.addData("Block position: ", "left");

                            }

//                            if (recognition.getLabel().equals("Ball")){
//                                motor1.setPower(1.0);
//                                telemetry.addData("Motor power: ", motoryay.getPower());
////                                telemetry.update();
//                            }
//                            else if  (recognition.getLabel().equals("Cube")){
//                                motor1.setPower(1.0);
//                                telemetry.addData("Motor power: ", motoryay.getPower());
////                                telemetry.update();
//                            }

                            boolean isDetectingTeamElement = false;
                            if (((recognition.getConfidence() > 0.35) && (recognition.getConfidence() < 0.50))) {
                                recog = true;
                                isDetectingTeamElement = true;
//                                telemetry.update();
                            }
                            if (isDetectingTeamElement) {
                                telemetry.addData(">", "detected team element");
                            }


                            // else if (recognition.getLabel().equals("Cube")){
                            //     AutonomousDC.stoprobot();
                            //     telemetry.addLine("I have no friends");
                            // }
//                            else{
//                                motor1.setPower(0.0);
//                                telemetry.addData("Motor power: ", motoryay.getPower());
////                                telemetry.update();
//                            }
                        }
                        if (recog == true) {
                            AutonomousDC.stoprobot();
                            telemetry.addLine("detected thing");
                        } else if (recog == false) {
                            AutonomousDC.switchToPower();
                            AutonomousDC.runRobot(0.15, -0.15, 0.15, -0.15);
                            telemetry.addLine("Paul George runs the NBA");
                        }
                        telemetry.update();
                    }
                }
            }
        }
    }

    /**
     * If it recognizes block, start this function
     */
    public void autonomousBlock() {
//        topLeftMotor.setPower(10);

    }

    public void autonomousBall() {

    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.26f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 330;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }

    private enum detectPosition {
        left, right, center;
    }

    public detectPosition getTeamElementPosition(Recognition recognition) {
        double avgWidth = (Math.abs(recognition.getRight() - recognition.getLeft()) / 2);
        double imgWidth = recognition.getImageWidth();
        if (avgWidth < (recognition.getImageWidth() / 3)) {
            return detectPosition.left;
        } else if ((avgWidth > (recognition.getImageWidth() / 3)) && (avgWidth < (int) (recognition.getImageWidth() * (2 / 3)))) {
            return detectPosition.center;
        } else if (avgWidth > (int) (recognition.getImageWidth() * (2 / 3))) {
            return detectPosition.right;
        } else {
            return null;
        }
    }

}
