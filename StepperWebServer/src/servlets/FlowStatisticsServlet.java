package servlets;


import Flow.FlowStatistics;
import Stepper.StepperUIManager;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "FlowStatisticsServlet", urlPatterns = "/flows_statistics")
public class FlowStatisticsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        ArrayList<FlowStatistics> flowsStatistics = null;

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        try {
            flowsStatistics = stepperUIManager.getFlowStatistics();
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(flowsStatistics));
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }
    }
}
