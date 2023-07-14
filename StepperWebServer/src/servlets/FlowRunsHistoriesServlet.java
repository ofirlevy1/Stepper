package servlets;


import RunHistory.FlowRunHistory;
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
import java.util.Vector;

@WebServlet(name = "FlowRunsHistoriesServlet", urlPatterns = "/flows_runs_histories")
public class FlowRunsHistoriesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        Vector<FlowRunHistory> flowsRunsHistories = null;

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }


        try {
            flowsRunsHistories = stepperUIManager.getFlowsRunHistories(username);
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(flowsRunsHistories));
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }
    }
}