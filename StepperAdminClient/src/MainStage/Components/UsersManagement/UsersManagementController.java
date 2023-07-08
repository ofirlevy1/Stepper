package MainStage.Components.UsersManagement;

import MainStage.Components.Main.MainStepperAdminClientController;
import MainStage.Components.util.Constants;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UsersManagementController {

    @FXML
    private FlowPane usersFlowPane;

    @FXML
    private FlowPane selectedUserFlowPane;

    @FXML
    private FlowPane rolesAssignmentToUser;

    private MainStepperAdminClientController mainStepperAdminClientController;
    private Timer timer;
    private TimerTask usersRefresher;
    private BooleanProperty autoUpdate;

    @FXML
    void saveButtonAction(ActionEvent event) {

    }


    public void setMainStepperController(MainStepperAdminClientController mainStepperController){
        this.mainStepperAdminClientController=mainStepperController;
    }

    public void loadUserDetails(String userName){

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

    public void startAvailableRolesRefresher(){
        usersRefresher=new AvailableUsersRefresher(
                autoUpdate,
                this::updateUsersList);
        timer=new Timer();
        timer.schedule(usersRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void restartUIElements(){

    }

}
