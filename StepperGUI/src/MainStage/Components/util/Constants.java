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
    public final static String FLOW_STATISTICS = FULL_SERVER_PATH + "/flowStatistics";
    public final static String STEP_STATISTICS=FULL_SERVER_PATH+"/stepStatistics";
    public final static String GET_ROLES = FULL_SERVER_PATH + "/roles";
    public final static String GET_USERS = FULL_SERVER_PATH + "/users";

    public final static String FLOW_HISTORIES=  FULL_SERVER_PATH + "/flows_histories";
    public final static String FLOWS_DESCRIPTORS= FULL_SERVER_PATH + "/flow";
    public final static String CREATE_FLOW= FULL_SERVER_PATH + "/create_flow";
    public final static String RUN_FLOW= FULL_SERVER_PATH + "/run_flow";
    public final static String FLOW_STATUS= FULL_SERVER_PATH + "/flow_status";
    public final static String CONTINUATION= FULL_SERVER_PATH + "/continuation";
    public final static String CONTINUATION_MAP= FULL_SERVER_PATH + "/continuation_map";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
