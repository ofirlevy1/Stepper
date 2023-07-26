package MainStage.Components.UsersManagement;

import MainStage.Components.Main.MainStepperAdminClientController;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import Users.UserDescriptor;
import com.google.gson.JsonObject;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.Body;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static MainStage.Components.util.Constants.GSON_INSTANCE;

public class UsersManagementController {

    @FXML
    private FlowPane usersFlowPane;

    @FXML
    private FlowPane selectedUserFlowPane;

    @FXML
    private FlowPane rolesAssignmentToUser;

    private SimpleStringProperty selectedUser;
    private List<CheckBox> rolesCheckboxes;

    private MainStepperAdminClientController mainStepperAdminClientController;
    private Timer timer;
    private TimerTask usersRefresher;
    private BooleanProperty autoUpdate;

    @FXML
    private void initialize(){
        this.autoUpdate=new SimpleBooleanProperty(true);
        this.selectedUser=new SimpleStringProperty("");
        rolesCheckboxes=new ArrayList<>();
    }

    @FXML
    void saveButtonAction(ActionEvent event) {
        List<String> rolesNames= new ArrayList<>();
        for(CheckBox roleCheckbox: rolesCheckboxes){
            if(roleCheckbox.isSelected())
                rolesNames.add(roleCheckbox.getText());
        }

        JsonObject jsonObject=new JsonObject();
        jsonObject.add("username", GSON_INSTANCE.toJsonTree(selectedUser.get()));
        jsonObject.add("roles_to_assign",GSON_INSTANCE.toJsonTree(rolesNames));

        String chosenRolesAsJson=GSON_INSTANCE.toJson(jsonObject);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), chosenRolesAsJson);

        String finalUrl = HttpUrl
                .parse(Constants.ASSIGN_ROLES)
                .newBuilder()
                .build()
                .toString();


        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
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
                if(response.code()!=200){
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
                        loadUserDetails(selectedUser.get());
                    });
                }
            }
        });

    }


    public void setMainStepperController(MainStepperAdminClientController mainStepperController){
        this.mainStepperAdminClientController=mainStepperController;
    }

    public void loadUserDetails(String userName){


        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_DESCRIPTION)
                .newBuilder()
                .addQueryParameter("target_user", userName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    String userDescriptionJson = response.body().string();
                    UserDescriptor userDescriptor = GSON_INSTANCE.fromJson(userDescriptionJson, UserDescriptor.class);
                    Platform.runLater(() -> {
                        selectedUser.set(userName);
                        loadUserDetailsFlowPane(userDescriptor);
                        loadRolesAssignment();
                    });
                }
                else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Error");
                        errorAlert.setContentText("Something went wrong: " + responseBody);
                        errorAlert.show();
                    });
                }
            }
        });

    }

    private void loadUserDetailsFlowPane(UserDescriptor userDescriptor){
        String str="| ";
        selectedUserFlowPane.getChildren().clear();
        selectedUserFlowPane.setPrefWrapLength(200);
        selectedUserFlowPane.getChildren().add(new Label("User Name: "+userDescriptor.getName()));
        for(String roleName:userDescriptor.getRoles())
            str+=roleName+" | ";
        selectedUserFlowPane.getChildren().add(new Label("Roles: "+str));
        str="";
        for(String flowName:userDescriptor.getPermittedFlowsNames())
            str+=flowName+" | ";
        selectedUserFlowPane.getChildren().add(new Label("Flows: "+str));
        selectedUserFlowPane.getChildren().add(new Label("Number of executions:"+userDescriptor.getNumberOfExecutedFlows()));
    }

    private void loadRolesAssignment(){
        rolesCheckboxes.clear();
        rolesAssignmentToUser.getChildren().clear();
        rolesAssignmentToUser.setPrefWrapLength(10);

        String finalUrl = HttpUrl
                .parse(Constants.GET_ROLES)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    String rolesJson = response.body().string();
                    List<String> roleNames= Arrays.asList(GSON_INSTANCE.fromJson(rolesJson, String[].class));
                    Platform.runLater(() -> {
                        for(String roleName:roleNames){
                            CheckBox roleCheckBox=new CheckBox(roleName);
                            rolesCheckboxes.add(roleCheckBox);
                            rolesAssignmentToUser.getChildren().add(roleCheckBox);
                            rolesAssignmentToUser.setPrefWrapLength(rolesAssignmentToUser.getPrefWrapLength()+20);
                        }
                    });
                }
            }
        });
    }

    private void updateUsersList(List<String> usersNames){
        Platform.runLater(()->{
            if(usersFlowPane.getChildren().size()!= usersNames.size()){
                usersFlowPane.getChildren().clear();
                usersFlowPane.setPrefWrapLength(200);
                for(String usersName : usersNames) {
                    Button button = new Button(usersName);
                    button.setOnAction(event -> loadUserDetails(usersName));
                    usersFlowPane.getChildren().add(button);
                    usersFlowPane.setPrefWrapLength(usersFlowPane.getPrefWrapLength()+150);
                }
            }

        });
    }

    @FXML
    void turnToManagerButtonAction(ActionEvent event) {
        setUserManager(true);
    }

    @FXML
    void turnToNonManagerButtonAction(ActionEvent event) {
        setUserManager(false);
    }

    private void setUserManager(boolean setManager){
        if (selectedUser.get().isEmpty())
            return;
        String finalUrl = HttpUrl
                .parse(Constants.SET_MANAGER)
                .newBuilder()
                .addQueryParameter("target_user", selectedUser.get())
                .addQueryParameter("set_manager", setManager?"true":"false")
                .build()
                .toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "chosenRolesAsJson");

        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()!=200){
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
                        loadUserDetails(selectedUser.get());
                    });
                }
            }
        });
    }

    public void startAvailableUsersRefresher(){
        usersRefresher=new AvailableUsersRefresher(
                autoUpdate,
                this::updateUsersList);
        timer=new Timer();
        timer.schedule(usersRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void restartUIElements(){

    }

}
