package servlets;

import Stepper.StepperUIManager;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Base64.Decoder;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

@WebServlet(name = "Load New Stepper File Servlet", urlPatterns = "/upload_stepper_file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadNewStepperServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = SessionUtils.getUsername(req);
        StepperUIManager stepperUIManager = ServletUtils.getStepperUIManager(getServletContext());

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("'username'(or active session) is missing in the request!");
            return;
        }


        if (!stepperUIManager.isUserAllowedToLoadNewStepperFile(username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("User '" + username + "' is not authorized to load new files into the Stepper system!");
            return;
        }

        Collection<Part> parts = req.getParts();
        StringBuilder fileContent = new StringBuilder();

        for(Part part : parts) {
            fileContent.append(readFromInputStream(part.getInputStream()));
        }
        System.out.println(fileContent.toString());


        try {
            stepperUIManager.LoadStepperFromXmlString(fileContent.toString(), username);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(e.getMessage());
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

}
