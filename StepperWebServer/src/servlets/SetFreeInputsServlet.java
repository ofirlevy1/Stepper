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
import java.util.HashMap;

@WebServlet(name = "Set Free Inputs Servlet", urlPatterns = "/set_free_inputs")
public class SetFreeInputsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        map = new Gson().fromJson(ServletUtils.getRequestBodyAsString(req), map.getClass());

        if(!map.containsKey("flow_id")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Requires key 'flow_id' was not found in the request.");
            return;
        }

        String flowID = map.get("flow_id");
        map.remove("flow_id");

        try {
            stepperUIManager.setFreeInputs(flowID, map);
        }

        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
        }
    }
}