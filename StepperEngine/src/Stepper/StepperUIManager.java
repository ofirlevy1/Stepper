package Stepper;

// This object acts as a Facade, offering the Stepper system UI functions
// and managing the stepper object itself

import Flow.Flow;
import Flow.FlowDescriptor;
import Generated.STStepper;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class StepperUIManager {
    Stepper stepper;
    boolean isLoaded;

    public StepperUIManager() {
        isLoaded = false;
    }

    public void LoadStepperFromXmlFile(String xmlFilePath) throws FileNotFoundException, JAXBException {
        stepper = new Stepper(xmlFilePath);
        isLoaded = true;
    }

    public ArrayList<String> getFlowNames(){
        return stepper.getFlowNames();
    }

    public FlowDescriptor getFlowDescriptor(String flowName) {
        return stepper.getFlowDescriptor(flowName);
    }


}
