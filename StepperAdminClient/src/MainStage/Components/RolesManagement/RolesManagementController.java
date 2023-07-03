package MainStage.Components.RolesManagement;

import MainStage.Components.Main.MainStepperAdminClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

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
    @FXML
    private void initialize(){

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

}
