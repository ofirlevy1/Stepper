package Stepper;

// This object acts as a Facade, offering the Stepper system UI functions
// and managing the stepper object itself

import java.io.File;

public class StepperUIManager {
    Stepper stepper;
    boolean isLoaded;

    public StepperUIManager() {
        isLoaded = false;
    }

    public void LoadStepperFromXmlFile(File xmlFile) {

    }
}
