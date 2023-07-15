package MainStage.Components.RolesManagement;

import MainStage.Components.Main.MainStepperAdminClientController;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import Users.RoleDescriptor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static MainStage.Components.util.Constants.GSON_INSTANCE;

public class RolesManagementController {

    @FXML
    private FlowPane availableRolesFlowPane;
    @FXML
    private FlowPane roleDetailsFlowPane;
    @FXML
    private FlowPane rolesCreationFlowPane;
    @FXML
    private FlowPane rolesDeletionFlowPane;
    @FXML
    private Button updateRoleButton;
    @FXML
    private Button createRoleButton;
    @FXML
    private Button deleteRoleButton;
    @FXML
    private TextField roleCreationNameTextField;
    @FXML
    private TextField roleCreationDescriptionTextField;

    private SimpleStringProperty selectedRole;
    private List<String> roleNames;
    private List<String> flowNames;
    private List<CheckBox> flowsCreationCheckbox;
    private List<CheckBox> rolesDeletionCheckbox;
    private List<CheckBox> roleUpdateCheckbox;

    private MainStepperAdminClientController mainStepperAdminClientController;
    private Timer timer;
    private TimerTask rolesRefresher;
    private BooleanProperty autoUpdate;

    @FXML
    private void initialize(){
        this.autoUpdate=new SimpleBooleanProperty(true);
        this.selectedRole=new SimpleStringProperty("");
        this.roleNames=new ArrayList<>();
        this.flowNames=new ArrayList<>();
        this.flowsCreationCheckbox=new ArrayList<>();
        this.roleUpdateCheckbox=new ArrayList<>();
        this.rolesDeletionCheckbox=new ArrayList<>();
        this.roleNames.add("Read Only Flows");
        this.roleNames.add("All Flows");
    }

    public RolesManagementController(){
        autoUpdate=new SimpleBooleanProperty();
    }

    @FXML
    void createRoleButtonAction(ActionEvent event) {
        if(roleCreationDescriptionTextField.getText().isEmpty()&&roleCreationNameTextField.getText().isEmpty()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("Text Fields Are Not Filled");
            errorAlert.show();
        }

        String finalUrl = HttpUrl
                .parse(Constants.CREATE_ROLE)
                .newBuilder()
                .build()
                .toString();
        Map<String,String> map=new HashMap<>();
        map.put("role_name",roleCreationNameTextField.getText());
        map.put("role_description",roleCreationDescriptionTextField.getText());
        String mapAsJson=GSON_INSTANCE.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), mapAsJson);

        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    Platform.runLater(() -> {
                        addFlowsToNewlyCreatedRole(roleCreationNameTextField.getText());

                    });
                }
                else{
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Error");
                    errorAlert.setContentText(response.body().string());
                    errorAlert.show();

                }
            }
        });

    }

    private void addFlowsToNewlyCreatedRole(String roleName){
        List<String> chosenFlows =new ArrayList<>();
        for(CheckBox flowCheckbox : flowsCreationCheckbox){
            if(flowCheckbox.isSelected())
                chosenFlows.add(flowCheckbox.getText());
        }

        JsonObject jsonObject=new JsonObject();
        jsonObject.add("role_name", GSON_INSTANCE.toJsonTree(roleName));
        jsonObject.add("permitted_flows",GSON_INSTANCE.toJsonTree(chosenFlows));

        String chosenFlowsAsJson=GSON_INSTANCE.toJson(jsonObject);

        String finalUrl = HttpUrl
                .parse(Constants.PERMITTED_FLOWS)
                .newBuilder()
                .build()
                .toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), chosenFlowsAsJson);

        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    Platform.runLater(() -> {
                        roleNames.add(roleCreationNameTextField.getText());
                        updateRoleDeletionFlowPane();
                    });
                }
                else{
                    String errorText=response.body().string();
                    Platform.runLater(()->{
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Error");
                        errorAlert.setContentText(errorText);
                        errorAlert.show();
                    });
                }
            }
        });
    }

    @FXML
    void deleteRoleButtonAction(ActionEvent event) {
        List<String> chosenRoles=new ArrayList<>();
        for(CheckBox roleCheckbox:rolesDeletionCheckbox)
            chosenRoles.add(roleCheckbox.getText());
        String chosenRolesAsJson=GSON_INSTANCE.toJson(chosenRoles);
        String finalUrl = HttpUrl
                .parse(Constants.DELETE_ROLES)
                .newBuilder()
                .build()
                .toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), chosenRolesAsJson);

        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    Platform.runLater(() -> {
                        for(String role:chosenRoles)
                            roleNames.remove(role);
                        updateRoleDeletionFlowPane();
                    });
                }
                else{
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Error");
                    errorAlert.setContentText(response.body().string());
                    errorAlert.show();
                }
            }
        });
    }

    private void updateRoleDeletionFlowPane(){
        rolesDeletionFlowPane.getChildren().clear();
        rolesDeletionFlowPane.setPrefWrapLength(100);
        rolesDeletionCheckbox.clear();
        for(String roleName:roleNames){
            CheckBox checkBox=new CheckBox(roleName);
            rolesDeletionCheckbox.add(checkBox);
            rolesDeletionFlowPane.getChildren().add(checkBox);
            rolesDeletionFlowPane.setPrefWrapLength(rolesDeletionFlowPane.getPrefWrapLength()+20);
        }
    }

    @FXML
    void updateRoleButtonAction(ActionEvent event) {
        List<String> chosenFlows =new ArrayList<>();
        for(CheckBox flowCheckbox : roleUpdateCheckbox){
            if(flowCheckbox.isSelected())
                chosenFlows.add(flowCheckbox.getText());
        }

        JsonObject jsonObject=new JsonObject();
        jsonObject.add("role_name", GSON_INSTANCE.toJsonTree(selectedRole.get()));
        jsonObject.add("permitted_flows",GSON_INSTANCE.toJsonTree(chosenFlows));

        String chosenFlowsAsJson=GSON_INSTANCE.toJson(jsonObject);

        String finalUrl = HttpUrl
                .parse(Constants.PERMITTED_FLOWS)
                .newBuilder()
                .build()
                .toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), chosenFlowsAsJson);
        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    Platform.runLater(() -> updateRoleDeletionFlowPane());
                }
                else{
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Error");
                    errorAlert.setContentText(response.body().string());
                    errorAlert.show();

                }
            }
        });
    }

    public void updateFlows(){
        String finalUrl = HttpUrl
                .parse(Constants.GET_FLOWS)
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
                    String flowsNamesJson = response.body().string();
                    flowNames= Arrays.asList(GSON_INSTANCE.fromJson(flowsNamesJson,String[].class));
                    Platform.runLater(() -> {
                        updateRoleCreationFlowPane(flowNames);
                        updateRoleDeletionFlowPane();
                    });
                }
            }
        });
    }

    private void updateRoleCreationFlowPane(List<String> flowNames){
        rolesCreationFlowPane.getChildren().clear();
        rolesCreationFlowPane.setPrefWrapLength(100);
        flowsCreationCheckbox.clear();
        for(String flowName: flowNames){
            CheckBox flowNameCheckbox= new CheckBox(flowName);
            flowsCreationCheckbox.add(flowNameCheckbox);
            rolesCreationFlowPane.getChildren().add(flowNameCheckbox);
            rolesCreationFlowPane.setPrefWrapLength(rolesCreationFlowPane.getPrefWrapLength()+10);
        }
    }

    public void setMainController(MainStepperAdminClientController mainStepperAdminClientController){
        this.mainStepperAdminClientController=mainStepperAdminClientController;
    }

    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    public void loadRoleDetails(String roleName){
        String finalUrl = HttpUrl
                .parse(Constants.GET_ROLE_DESCRIPTION)
                .newBuilder()
                .addQueryParameter("role_name", roleName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code()==200){
                    String roleDescriptionJson = response.body().string();
                    RoleDescriptor roleDescriptor = GSON_INSTANCE.fromJson(roleDescriptionJson, RoleDescriptor.class);
                    Platform.runLater(() -> {
                        selectedRole.set(roleName);
                        loadRoleDetailsFlowPane(roleDescriptor);
                    });
                }
            }
        });
    }

    private void loadRoleDetailsFlowPane(RoleDescriptor roleDescriptor){
        roleDetailsFlowPane.getChildren().clear();
        roleDetailsFlowPane.setPrefWrapLength(100);
        roleDetailsFlowPane.getChildren().add(new Label("Name: "+roleDescriptor.getName()));
        roleDetailsFlowPane.getChildren().add(new Label("Description: "+roleDescriptor.getDescription()));
        roleDetailsFlowPane.getChildren().add(new Label("Flows: "+roleDescriptor.getPermittedFlowsNames()));
        roleDetailsFlowPane.getChildren().add(new Label("Users: "+roleDescriptor.getUsersWithThisRole()));
        roleUpdateCheckbox.clear();
        for(String flowName:flowNames){
            CheckBox flowNameCheckbox=new CheckBox(flowName);
            roleUpdateCheckbox.add(flowNameCheckbox);
            roleDetailsFlowPane.getChildren().add(flowNameCheckbox);
            roleDetailsFlowPane.setPrefWrapLength(roleDetailsFlowPane.getPrefWrapLength()+20);
        }
    }

    private void updateRolesList(List<String> rolesNames){
        Platform.runLater(()->{
            if(availableRolesFlowPane.getChildren().size()!=rolesNames.size()){
                availableRolesFlowPane.getChildren().clear();
                availableRolesFlowPane.setPrefWrapLength(200);
                for(String roleName:rolesNames) {
                    Button button = new Button(roleName);
                    button.setOnAction(event -> loadRoleDetails(roleName));
                    availableRolesFlowPane.getChildren().add(button);
                    availableRolesFlowPane.setPrefWrapLength(availableRolesFlowPane.getPrefWrapLength()+150);
                }
            }
        });
    }

    public void startAvailableRolesRefresher(){
        rolesRefresher=new AvailableRolesRefresher(
                autoUpdate,
                this::updateRolesList);
        timer=new Timer();
        timer.schedule(rolesRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

}
