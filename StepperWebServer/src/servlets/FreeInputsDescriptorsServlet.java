package servlets;


import Flow.FreeInputDescriptor;
import Stepper.StepperUIManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.SessionUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

@WebServlet(name = "Free Inputs Servlet", urlPatterns = "/flow/free_inputs_descriptors")
public class FreeInputsDescriptorsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());
        ArrayList<FreeInputDescriptor> freeInputsDescriptors = new ArrayList<>();
        String flowName = req.getParameter("flow_name");

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("The client is not logged in! please login first (/login)");
            return;
        }

        if (flowName == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Required 'role_name' query parameter was not found in the request.");
            return;
        }


        try {
            freeInputsDescriptors = stepperUIManager.getFreeInputDescriptorsByFlow(flowName);
        }
        catch(Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            resp.getWriter().println(e.getStackTrace());
        }

        Gson gson = new Gson();
        resp.setContentType("application/json");
        resp.getWriter().println(gson.toJson(freeInputsDescriptors));
    }
}