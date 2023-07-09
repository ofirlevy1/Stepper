package MainStage.Components.RolesManagement;

import MainStage.Components.Main.MainStepperAdminClientController;
import MainStage.Components.util.Constants;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private MainStepperAdminClientController mainStepperAdminClientController;
    private Timer timer;
    private TimerTask rolesRefresher;
    private BooleanProperty autoUpdate;

    @FXML
    private void initialize(){
        this.autoUpdate=new SimpleBooleanProperty(true);
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
    }

    @FXML
    void deleteRoleButtonAction(ActionEvent event) {

    }

    @FXML
    void updateRoleButtonAction(ActionEvent event) {

    }

    public void setMainController(MainStepperAdminClientController mainStepperAdminClientController){
        this.mainStepperAdminClientController=mainStepperAdminClientController;
    }

    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    public void loadRoleDetails(String roleName){

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
