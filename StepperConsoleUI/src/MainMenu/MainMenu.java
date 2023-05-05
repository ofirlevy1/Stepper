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


import java.util.ArrayList;
import java.util.Scanner;

import Exceptions.PathDoesNotPointToXMLFileException;
import Flow.*;
import RunHistory.FlowRunHistory;
import Stepper.StepperUIManager;
import Steps.StepDescriptor;
import Steps.StepStatistics;

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
        System.out.println("Thank you for using Stepper :)");
    }

    private void executeUserChoice()
    {
        if(chosenOption != Options.LoadSystemFromXML && chosenOption != Options.Exit && !stepperUIManager.isLoaded()) {
            System.out.println("Stepper system is not loaded yet! Please load the system using 'Load System From XML' and try again");
            System.out.println("Enter anything to continue...");
            consoleScanner.nextLine();
            return;
        }

        if((chosenOption==Options.ShowStatistics||chosenOption==Options.ShowPastFlowExecutionDetails)&&!stepperUIManager.isFlowRan()){
            System.out.println("A flow has to be ran in order to show the information you need, Please run a flow with 'Run a Flow' and try again");
            System.out.println("Enter anything to continue...");
            consoleScanner.nextLine();
            return;
        }

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
        int userInput = getUserNumberChoiceWithinRange(1, 6);
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
        boolean loadedSuccessfully = false;
        while(!loadedSuccessfully) {
            System.out.println("Please enter the full path for the ST_Stepper XML file (example: C:\\Users\\me\\stepper.xml) :");
            System.out.println(("(Or enter 0 to return to Main Menu)"));
            String userInput = consoleScanner.nextLine();
            if(doesStringRepresentANumber(userInput, 0))
                return;
            try {
                stepperUIManager.LoadStepperFromXmlFile(userInput);
            }
            catch(PathDoesNotPointToXMLFileException e) {
                System.out.println("The given path is not an XML file! Please enter a valid path");
                continue;
            }
            catch (Exception e) {
                System.out.println("Failed to load system from XML file! ");
                System.out.println(e.getMessage());
                continue;
            }
            loadedSuccessfully = true;
            System.out.println("System was loaded successfully! enter anything to continue");
            consoleScanner.nextLine();
        }
    }
    private void showFlowDefinition() {
        String selectedFlowName = getUserFlowSelection();
        if(selectedFlowName == null)
            return;
        FlowDescriptor flowDescriptor = stepperUIManager.getFlowDescriptor(selectedFlowName);

        presentFlowDetails(flowDescriptor);
    }


    // returns null if the user entered 0 (back to main menu)
    private String getUserFlowSelection() {
        presentFlows();
        System.out.println("Please enter the number of the desired Flow(or 0 to return to main menu)");
        int userChoice = getUserNumberChoiceWithinRange(0, stepperUIManager.getFlowNames().size());
        if(userChoice == 0)
            return null;
        return stepperUIManager.getFlowNames().get(userChoice - 1);
    }

    int getUserNumberChoiceWithinRange(int min, int max) {
        boolean isChoiceValid = false;
        int userInputAsInteger = -1;

        while(!isChoiceValid) {
            System.out.println("Enter a choice between " + min + " and " + max + ":");
            try {
                userInputAsInteger = consoleScanner.nextInt();
                consoleScanner.nextLine(); // scanner.nextInt does not clear the newline(enter) from the buffer
            }
            catch(Exception e){
                System.out.println("Invalid input!");
                consoleScanner.nextLine(); // clearing the buffer manually since it's not cleared if the scanner fails.
                continue;
            }
            if(userInputAsInteger < min || userInputAsInteger > max) {
                System.out.println("Value is not withing range!");
                continue;
            }
            isChoiceValid = true;
        }
        return userInputAsInteger;
    }

    private void presentFlows() {
        for(int i = 0; i <= stepperUIManager.getFlowNames().size() - 1; i++) {
            System.out.println((i + 1) + ". " + stepperUIManager.getFlowNames().get(i));
        }
    }


    private void runFlow() {
        String selectedFlowName = getUserFlowSelection();
        if(selectedFlowName == null)
            return;
        runExecuteFlowMenu(selectedFlowName);
    }

    private void runExecuteFlowMenu(String flowName) {
        int userInput = 0;
        while(true) {
            presentFreeInputs(stepperUIManager.getFreeInputDescriptorsByFlow(flowName));
            System.out.println("1. Enter Free Input Value");
            if(stepperUIManager.areAllMandatoryFreeInputsSet(flowName))
                System.out.println("2. Run Flow");
            System.out.println("0. Back to Main Menu");
            userInput = getUserNumberChoiceWithinRange(0, stepperUIManager.areAllMandatoryFreeInputsSet(flowName) ? 2 : 1);
            if(userInput == 0)
                return;
            if(userInput == 1)
                fillFreeInputUI(flowName);
            if(userInput == 2) {
                try {
                    System.out.println("Executing flow...");
                    stepperUIManager.runFlow(flowName);
                }
                catch(Exception e) {
                    System.out.println("Flow execution failed: " + e.getMessage());
                }

                //presentFlowLastRunStatus()
                System.out.println("Flow execution done, enter anything to continue");
                consoleScanner.nextLine();
            }

        }
    }

    private void fillFreeInputUI(String flowName) {
        System.out.println("Choose the free input's number: ");
        int freeInputIndex = getUserNumberChoiceWithinRange(1, stepperUIManager.getFreeInputDescriptorsByFlow(flowName).size());
        String value = "";
        while(true) {
            System.out.println("Enter a value for the free input: ");
            value = consoleScanner.nextLine();
            try {
                stepperUIManager.setFreeInput(flowName, stepperUIManager.getFreeInputDescriptorsByFlow(flowName).get(freeInputIndex - 1).getInputEffectiveName(), value);
            }
            catch (Exception e) {
                System.out.println("Failed to assign value: " + e.getMessage());
                continue;
            }
            break;
        }
        System.out.println("free input value was inserted successfully! enter anything to continue");
        consoleScanner.nextLine();
    }


    private void showPastFlowExecutionDetails() {
        ArrayList<FlowRunHistory> flowsRunHistories = stepperUIManager.getFlowsRunHistories();
        for(int i = 0; i < flowsRunHistories.size(); i++) {
            System.out.println((i+1) + ". " + flowsRunHistories.get(i).showMinimalFlowHistory());
        }
        int userChoice = getUserNumberChoiceWithinRange(1, flowsRunHistories.size());
        System.out.println(flowsRunHistories.get(userChoice-1).showExtensiveFlowHistory());
        System.out.println("Enter anything to continue...");
        consoleScanner.nextLine();
    }
    private void showStatistics() {
        ArrayList<FlowStatistics> flowsStatistics = stepperUIManager.getFlowStatistics();
        ArrayList<StepStatistics> stepsStatistics = stepperUIManager.getStepsStatistics();

        for(FlowStatistics flowStatistics : flowsStatistics) {
            System.out.println(flowStatistics.getFlowStatisticsAsString());
        }
        for(StepStatistics stepStatistics : stepsStatistics) {
            System.out.println(stepStatistics.getStepstatisticsAsString());
        }
    }

    static boolean doesStringRepresentANumber(String str, int num) {
        int strAsNumber;
        try {
            strAsNumber = Integer.parseInt(str);
        }
        catch(NumberFormatException e) {
            return false;
        }
        if(strAsNumber != num)
            return false;
        return true;
    }


    private void presentFlowDetails(FlowDescriptor flowDescriptor) {
        System.out.println("Flow Name: " + flowDescriptor.getFlowName());
        System.out.println("Flow Description: " + flowDescriptor.getFlowDescription());
        System.out.println("Formal Outputs: " + flowDescriptor.getFormalOutputNames().toString());
        for(int i = 0; i < flowDescriptor.getStepDescriptors().size(); i++) {
            System.out.println("Step #" + (i + 1) + ":");
            presentStepDetails(flowDescriptor.getStepDescriptors().get(i));
        }

        presentFreeInputs(flowDescriptor.getFreeInputs());

        System.out.println("All Outputs:");
        for(StepOutputDescriptor stepOutputDescriptor : flowDescriptor.getOutputs()) {
            presentStepOutput(stepOutputDescriptor);
        }
    }

    private void presentFreeInputs(ArrayList<FreeInputDescriptor> freeInputDescriptors) {
        System.out.println("Free Inputs:");
        for(int i = 0; i < freeInputDescriptors.size(); i++) {
            System.out.println(i + 1);
            presentFreeInput(freeInputDescriptors.get(i));
        }
    }

    private void presentStepOutput(StepOutputDescriptor stepOutputDescriptor) {
        System.out.println("Effective Name: " + stepOutputDescriptor.getOutputEffectiveName());
        System.out.println("Type: " + stepOutputDescriptor.getOutputType());
        System.out.println("Produced by step: " + stepOutputDescriptor.getSourceStepName());
    }

    private void presentFreeInput(FreeInputDescriptor freeInputDescriptor) {
        System.out.println("Effective Name: " + freeInputDescriptor.getInputEffectiveName());
        System.out.println("Type: " + freeInputDescriptor.getInputType().toString());
        System.out.println("Associated steps: " + freeInputDescriptor.getAssociatedSteps());
        System.out.println((freeInputDescriptor.isMandatory() ? "mandatory" : "optional"));
    }

    private void presentStepDetails(StepDescriptor stepDescriptor) {
        System.out.println("Step Name: " + stepDescriptor.getStepName());
        if(stepDescriptor.isHasAlias())
            System.out.println("Step Alias: " + stepDescriptor.getStepAlias());
        System.out.println("The step is " + (stepDescriptor.isReadOnly() ? "readonly" : "NOT readonly"));
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
