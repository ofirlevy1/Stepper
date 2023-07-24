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

@WebServlet(name = "FlowStatusServlet", urlPatterns = "/flow_status")

public class FlowStatusServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());


        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        JsonObject requestBodyJsonObject = ServletUtils.getRequestBodyAsJsonObject(req);
        ServletUtils.VerifyRequestJsonBodyHasMember(requestBodyJsonObject, "flow_id", resp);

        String flowID = requestBodyJsonObject.get("flow_id").getAsString();

        try {
            resp.getWriter().println(stepperUIManager.getFlowStatus(flowID));
            return;
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }
    }
}