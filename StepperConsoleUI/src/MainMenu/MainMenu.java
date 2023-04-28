package MainMenu;

/*
*  - General Console UI System Requirements:
*  do NOT clear the console screen between commands
*  - EVERY command should have an output, that needs to
*  be presented to the user after it is executed.
*  - after every command choice by the user - execute,
*  present output and present menu again
*  - some commands cannot be executed before other ones.
* if the users tries - present appropriate error
*  message ot him (but the program should keep running!)
* - when ever there are multiple choices - ask the user to
*  their NUMBER (and NOT a string), starting from 1
* - in command 2,3 (show & run flows), when showing the flow
* names selection, there should also be a "0" option
* to get back to the main menu (without selecting any flow)
* - also in the flow,
*
*
*
* */


import java.util.InputMismatchException;
import java.util.Scanner;
import Stepper.StepperUIManager;

public class MainMenu {
    private Options chosenOption;
    private Scanner consoleScanner;
    private StepperUIManager stepperUIManager;


    public MainMenu() {
        consoleScanner = new Scanner(System.in);
        stepperUIManager = new StepperUIManager();
    }

    public void run() {
        displayWelcomeLine();
        runMainMenu();
    }


    private void runMainMenu() {
        showMenuAndGetUserChoice();
        while (chosenOption != Options.Exit) {
            executeUserChoice();
            showMenuAndGetUserChoice();
        }
    }

    private void executeUserChoice()
    {
        switch(chosenOption) {
            case LoadSystemFromXML:
                loadSystemFromXML();
                break;
            case ShowFlowDefinition:
                showFlowDefinition();
                break;
            case RunFlow:
                runFlow();
                break;
            case ShowPastFlowExecutionDetails:
                showPastFlowExecutionDetails();
                break;
            case ShowStatistics:
                showStatistics();
                break;
        }
    }

    private void showMenuAndGetUserChoice() {
        showMainMenu();
        getUserChoice();
    }

    private void displayWelcomeLine() {
        System.out.println("Welcome to the Stepper system! :)");
    }

    private void showMainMenu() {
        System.out.println("1. Load Stepper System From XML File");
        System.out.println("2. Show Flow Definition");
        System.out.println("3. Run a Flow");
        System.out.println("4. Show Past Flow Execution Details");
        System.out.println("5. Show Statistics");
        System.out.println("6. Exit");
    }

    private void getUserChoice() {
        System.out.println("Please enter the desired choice number(1-6): ");
        boolean isInputValid = false;
        int userInput = 0;
        while(!isInputValid)
        {
            try {
                userInput = consoleScanner.nextInt();
            }
            catch (InputMismatchException e) {
                System.out.println("You have entered an invalid value! Please enter a number from 1 to 6: ");
                consoleScanner.nextLine(); //clearing the buffer, since it's not cleared automatically in this case.
                continue;
            }
            if(userInput < 1 || userInput > 6) {
                System.out.println("The number must be from 1 to 6! please try again:");
                continue;
            }
            isInputValid = true;
        }
        switch(userInput) {
            case 1:
                chosenOption = Options.LoadSystemFromXML;
                break;
            case 2:
                chosenOption = Options.ShowFlowDefinition;
                break;
            case 3:
                chosenOption = Options.RunFlow;
                break;
            case 4:
                chosenOption = Options.ShowPastFlowExecutionDetails;
                break;
            case 5:
                chosenOption = Options.ShowStatistics;
                break;
            case 6:
                chosenOption = Options.Exit;
                break;
        }
    }




    private void loadSystemFromXML() {
    }
    private void showFlowDefinition() {

    }
    private void runFlow() {

    }
    private void showPastFlowExecutionDetails() {

    }
    private void showStatistics() {

    }






    public enum Options {
        LoadSystemFromXML,
        ShowFlowDefinition,
        RunFlow,
        ShowPastFlowExecutionDetails,
        ShowStatistics,
        Exit
    }

}
