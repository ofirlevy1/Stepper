package servlets;


import Stepper.StepperUIManager;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "Check Free Inputs Set Servlet", urlPatterns = "/are_all_mandatory_free_inputs_set")
public class CheckMandatoryFreeInputsSetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        boolean allMandatoryFreeInputsSet = false;

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        JsonObject requestBodyJson = ServletUtils.getRequestBodyAsJsonObject(req);

        if(!ServletUtils.VerifyRequestJsonBodyHasMember(requestBodyJson, "flow_id", resp))
            return;

        try {
            allMandatoryFreeInputsSet = stepperUIManager.areAllMandatoryFreeInputsSet(requestBodyJson.get("flow_id").getAsString());
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }

        resp.getWriter().println(allMandatoryFreeInputsSet);
    }
}