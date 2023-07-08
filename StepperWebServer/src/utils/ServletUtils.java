package utils;

import Stepper.StepperUIManager;
import jakarta.servlet.ServletContext;

public class ServletUtils {
    private static final Object stepperUIManagerLock = new Object();
    public static StepperUIManager getStepperUIManager(ServletContext servletContext) {
        synchronized (stepperUIManagerLock) {
            if (servletContext.getAttribute("stepperUIManager") == null) {
                servletContext.setAttribute("stepperUIManager", new StepperUIManager());
            }
        }
        return (StepperUIManager)(servletContext.getAttribute("stepperUIManager"));
    }

}
