package servlets;


import Stepper.StepperUIManager;
import Users.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashSet;

@WebServlet(name = "Users Servlet", urlPatterns = "/users")
public class UsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        HashSet<String> userNames = new HashSet<>();

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }


        try {
            userNames = stepperUIManager.getAllUsersNames();
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            resp.getWriter().println(e.getStackTrace());
        }

        Gson gson = new Gson();
        resp.setContentType("application/json");
        resp.getWriter().println(gson.toJson(userNames));
    }
}