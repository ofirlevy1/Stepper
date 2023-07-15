package MainStage.Components.Main;

import MainStage.Components.ExecutionsHistory.ExecutionsHistoryController;
import MainStage.Components.FlowsDefinition.FlowsDefinitionController;
import MainStage.Components.FlowsExecution.FlowsExecutionController;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import Users.UserDescriptor;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class MainStepperController {
    @FXML
    private Label userNameLabel;
    @FXML
    private Label isManagerLabel;
    @FXML
    private Label rolesLabel;
    @FXML
    private TabPane selectionTabPane;
    @FXML
    private BorderPane flowsDefinition;
    @FXML
    private FlowsDefinitionController flowsDefinitionController;
    @FXML
    private BorderPane flowsExecution;
    @FXML
    private FlowsExecutionController flowsExecutionController;
    @FXML
    private BorderPane executionsHistory;
    @FXML
    private ExecutionsHistoryController executionsHistoryController;
    @FXML
    private TextField userNameTextField;
    @FXML
    private Button loginButton;
    @FXML
    private Label userNameNameLabel;
    @FXML
    private Label isManagerNameLabel;
    @FXML
    private Label rolesNameLabel;

    private Stage primaryStage;
    private Timer timer;
    private TimerTask rolesNamesRefresher;
    private BooleanProperty autoUpdate;


    public MainStepperController(){
    }

    @FXML
    private void initialize(){
        this.autoUpdate=new SimpleBooleanProperty(true);
        this.flowsDefinitionController.setMainStepperController(this);
        this.flowsExecutionController.setMainStepperController(this);
        this.executionsHistoryController.setMainStepperController(this);
        this.selectionTabPane.setDisable(true);
        isManagerNameLabel.setVisible(false);
        rolesNameLabel.setVisible(false);
        userNameNameLabel.setVisible(false);
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage=primaryStage;
    }



    @FXML
    void loginButtonAction(ActionEvent event){
        startLogin();
    }

    private void startLogin(){
        if(userNameTextField.getText().isEmpty()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("User Name Is Empty");
            errorAlert.show();
        }
        else{

            String finalUrl = HttpUrl
                    .parse(Constants.LOGIN_PAGE)
                    .newBuilder()
                    .addQueryParameter("username", userNameTextField.getText())
                    .build()
                    .toString();


            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Error");
                        errorAlert.setContentText("Something went wrong: " + e.getMessage());
                        errorAlert.show();
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() != 200) {
                        String responseBody = response.body().string();
                        Platform.runLater(() -> {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setHeaderText("Error");
                            errorAlert.setContentText("Something went wrong: " + responseBody);
                            errorAlert.show();
                        });
                    }
                    else {
                        Platform.runLater(() -> {
                            userNameLabel.textProperty().set(userNameTextField.getText());
                            isManagerNameLabel.setVisible(true);
                            rolesNameLabel.setVisible(true);
                            userNameNameLabel.setVisible(true);
                            loginButton.setVisible(false);
                            userNameTextField.setVisible(false);
                            selectionTabPane.setDisable(false);
                            startRefreshers();
                        });
                    }
                }
            });
        }

    }

    private void startRefreshers(){
        startUserRolesPresentationRefresherRefresher();
        this.executionsHistoryController.startFlowsHistoriesRefresher();
        this.flowsDefinitionController.startAvailableFlowsRefresher();
    }

    public void startUserRolesPresentationRefresherRefresher(){
        rolesNamesRefresher=new UserRolesPresentationRefresher(
                autoUpdate,
                this::updateUserRolesPresentation,
                userNameLabel.getText());
        timer=new Timer();
        timer.schedule(rolesNamesRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    private void updateUserRolesPresentation(UserDescriptor userDescriptor){
        Platform.runLater(()->{
            HashSet<String>rolesNames= userDescriptor.getRoles();
            StringBuilder rolesNamesListAsString= new StringBuilder();
            for(String roleName:rolesNames)
                rolesNamesListAsString.append(roleName).append(", ");
            rolesLabel.textProperty().set(rolesNamesListAsString.toString());
        });
    }

    public void switchTabs(Tabs tab,String flowName){
        selectionTabPane.getSelectionModel().select(tab.ordinal());
        flowsExecutionController.loadFlowsExecutionInputsHttpCall(flowName);
        flowsExecutionController.loadFlowsExecutionFlowDetails(flowName);
    }

    public void rerunFlow(String flowName, HashMap<String ,String> freeInputsMap){
        selectionTabPane.getSelectionModel().select(Tabs.FlowsExecutionTab.ordinal());
        flowsExecutionController.loadFlowsExecutionInputsRerun(flowName, freeInputsMap);
    }

//    public void restartUIElements(){
//        this.flowsDefinitionController.restartUIElements();
//        this.flowsExecutionController.restartUIElements();
//        this.executionsHistoryController.restartUIElements();
//    }

//    public ObservableList<Node> getFlowRunHistoryChildrenNodesFromFlowsExecution(){
//        return this.flowsExecutionController.getExecutionDetailsFlowPaneChildrenNodes();
//    }

    public enum Tabs{
        FlowsDefinitionTab,
        FlowsExecutionTab,
        ExecutionsHistoryTab,
        Statistics

    }
}
