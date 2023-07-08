package servlets;

import java.io.IOException;

import Stepper.StepperUIManager;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;


@WebServlet(name = "Login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String usernameFromSession = SessionUtils.getUsername(request);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());

        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("An attempt was made to login, when there's no active session, AND there's no 'username' query parameter in the request.");
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();

                synchronized (this) {
                    if (stepperUIManager.isUserExists(usernameFromParameter)) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        response.getWriter().println("Username " + usernameFromParameter + " already exists. Please enter a different username.");
                    }
                    else {
                        //add the new user to the users list
                        stepperUIManager.addUser(usernameFromParameter);

                        //set the username in a session so that it will be available on each request
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);

                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        } else {
            //user is already logged in
            response.setStatus(HttpServletResponse.SC_OK);
        }
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
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
