package servlets;

import Stepper.StepperUIManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "ContinuationMapServlet", urlPatterns = "/continuation_map")

public class ContinuationMapServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());


        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        JsonObject requestBodyJsonObject = ServletUtils.getRequestBodyAsJsonObject(req);
        ServletUtils.VerifyRequestJsonBodyHasMember(requestBodyJsonObject, "source_flow_id", resp);
        ServletUtils.VerifyRequestJsonBodyHasMember(requestBodyJsonObject, "target_flow_name", resp);

        String flowID = requestBodyJsonObject.get("source_flow_id").getAsString();
        String targetFlowName = requestBodyJsonObject.get("target_flow_name").getAsString();

        try {
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(stepperUIManager.getFlowContinuationMap(flowID, targetFlowName)));
            return;
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }
    }
}