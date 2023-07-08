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

@WebServlet(name = "Managers Servlet", urlPatterns = "/user/manager")
public class ManagersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        boolean isUserManager = false;
        String targetUser = req.getParameter("target_user");

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        if (targetUser == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Required 'target_user' query parameter was not found in the request.");
            return;
        }


        try {
            isUserManager = stepperUIManager.isUserManager(targetUser);
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            resp.getWriter().println(e.getStackTrace());
        }

        resp.getWriter().println(isUserManager);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        boolean isUserManager = false;
        String targetUser = req.getParameter("target_user");
        String setManager = req.getParameter("set_manager");

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        if (targetUser == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Required 'target_user' query parameter was not found in the request.");
            return;
        }

        if (setManager == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Required 'set_manager' (boolean) query parameter was not found in the request.");
            return;
        }


        try {
            stepperUIManager.setManager(targetUser, Boolean.parseBoolean(setManager));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            resp.getWriter().println(e.getStackTrace());
        }
    }
}