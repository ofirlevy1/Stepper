package servlets;

import Stepper.StepperUIManager;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import java.util.Base64.Decoder;
import java.io.IOException;

@WebServlet(name = "Load New Stepper File Servlet", urlPatterns = "/load_new_stepper")
public class LoadNewStepperServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String filePathBase64 = req.getParameter("file_path_base64");

        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());

        if (username == null || filePathBase64 == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("either 'username' or 'file_path' parameters are missing in the request!");
            return;
        }


        if (!stepperUIManager.isUserAllowedToLoadNewStepperFile(username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("User '" + username + "' is not authorized to load new files into the Stepper system!");
            return;
        }

        try {
            stepperUIManager.LoadStepperFromXmlFile(Base64.base64Decode(filePathBase64), username);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
