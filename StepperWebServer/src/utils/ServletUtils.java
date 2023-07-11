package utils;

import Stepper.StepperUIManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

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

    public static JsonObject getRequestBodyAsJsonObject(HttpServletRequest request) {
        try {
            // reading the whole request body from the buffer into a string, then parsing it into a Json Object
            return JsonParser.parseString(request.getReader().lines().collect(Collectors.joining())).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean VerifyRequestJsonBodyHasMember(JsonObject body, String memberName, HttpServletResponse response) {
        if(!body.has(memberName)) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Request did not contain required json field '" + memberName + "'");
                return false;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
}
