package servlets;

import java.io.IOException;

import Stepper.StepperUIManager;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.ServletUtils;
import utils.SessionUtils;


@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());

        if(!stepperUIManager.isLoaded()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("The Stepper system is not loaded yet! to start the system, an admin has to login first and load a stepper file.");
            return;
        }

        String usernameFromSession = SessionUtils.getUsername(request);

        if(usernameFromSession == null)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("A request to logout was made from a user that's not logged in (doesn't have an active session)");
            return;
        }

        request.getSession().invalidate();

        stepperUIManager.removeUser(usernameFromSession);


    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
