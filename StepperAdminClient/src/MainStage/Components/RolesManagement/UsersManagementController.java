package MainStage.Components.RolesManagement;

import MainStage.Components.Main.MainStepperAdminClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

public class UsersManagementController {

    @FXML
    private FlowPane usersFlowPane;

    @FXML
    private FlowPane selectedUserFlowPane;

    @FXML
    private FlowPane rolesAssignmentToUser;

    private MainStepperAdminClientController mainStepperAdminClientController;

    @FXML
    void saveButtonAction(ActionEvent event) {

    }


    public void setMainStepperController(MainStepperAdminClientController mainStepperController){
        this.mainStepperAdminClientController=mainStepperController;
    }

    public void restartUIElements(){

    }

}
