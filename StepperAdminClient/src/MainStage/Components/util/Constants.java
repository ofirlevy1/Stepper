package MainStage.Components.util;

import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 500;

    // fxml locations


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/StepperWebServer_Web";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String FLOW_STATISTICS = FULL_SERVER_PATH + "/flows_statistics";
    public final static String STEP_STATISTICS=FULL_SERVER_PATH+"/steps_statistics";
    public final static String GET_ROLES = FULL_SERVER_PATH + "/roles";
    public final static String GET_USERS = FULL_SERVER_PATH + "/users";
    public final static String GET_USER_DESCRIPTION = FULL_SERVER_PATH +"/user";
    public final static String GET_ROLE_DESCRIPTION = FULL_SERVER_PATH +"/role";
    public final static String CREATE_ROLE = FULL_SERVER_PATH +"/role";
    public final static String DELETE_ROLES = FULL_SERVER_PATH +"/delete_roles";
    public final static String PERMITTED_FLOWS = FULL_SERVER_PATH +"/role_permitted_flows";
    public final static String MANAGER =  FULL_SERVER_PATH + "/user/manager";
    public final static String GET_FLOWS = FULL_SERVER_PATH +"/flows";
    public final static String ASSIGN_ROLES = FULL_SERVER_PATH + "/set_user_assigned_roles";
    public final static String LOAD_XML_FILE = FULL_SERVER_PATH + "/upload_stepper_file";


    public final static String FLOW_HISTORIES=  FULL_SERVER_PATH + "/flows_runs_histories";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
