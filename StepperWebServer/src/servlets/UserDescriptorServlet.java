package servlets;


import Stepper.StepperUIManager;
import Users.UserDescriptor;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "User Descriptor Servlet", urlPatterns = "/user")
public class UserDescriptorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        UserDescriptor userDescriptor = null;
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
            userDescriptor = stepperUIManager.getUserDescriptor(targetUser);
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(userDescriptor));
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }
    }
}