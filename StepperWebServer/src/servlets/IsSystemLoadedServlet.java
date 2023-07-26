package servlets;


import Stepper.StepperUIManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "IsSystemLoadedServlet Servlet", urlPatterns = "/is_system_loaded")
public class IsSystemLoadedServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());

        try {
            resp.getWriter().println(stepperUIManager.isLoaded());
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
        }
    }
}