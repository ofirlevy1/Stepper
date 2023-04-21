package Stepper;

// This object acts as a Facade, offering the Stepper system UI functions
// and managing the stepper object itself

import Flow.Flow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class StepperUIManager {
    Stepper stepper;
    boolean isLoaded;
    HashSet<Flow> flows;

    public StepperUIManager() {
        isLoaded = false;
    }

    public void LoadStepperFromXmlFile(File xmlFile) {

        // don't forget to set isLoaded
    }

    public ArrayList<String> getFlowNames(){
        ArrayList<String> flowNames = new ArrayList<>();
        for(Flow flow : flows)
            flowNames.add(flow.getName());
        return flowNames;
    }
}
