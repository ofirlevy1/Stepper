package servlets;


import Stepper.StepperUIManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "Role's Permitted Flows Servlet", urlPatterns = "/roles_permitted_flows")
public class RolesPermittedFlowsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        Gson gson = new Gson();

        JsonObject requestBodyJsonObject = ServletUtils.getRequestBodyAsJsonObject(req);

        String roleName = requestBodyJsonObject.get("role_name").getAsString();
        JsonElement permittedFlowsJsonString = requestBodyJsonObject.get("permitted_flows");

        String[] permittedFlows = gson.fromJson(permittedFlowsJsonString, String[].class);



        try {
            stepperUIManager.setPermittedFlowsForRole(roleName, permittedFlows);
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
        }
    }
}