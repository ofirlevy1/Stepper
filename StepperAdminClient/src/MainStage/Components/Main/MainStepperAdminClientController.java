package MainStage.Components.Main;

import MainStage.Components.Statistics.StatisticsController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainStepperAdminClientController {

    @FXML
    private Label adminNameLabel;

    @FXML
    private Label selectedFileLabel;

    @FXML
    private TabPane selectionTabPane;
    @FXML
    private BorderPane executionsHistory;
 //   @FXML
 //   private ExecutionsHistoryController executionsHistoryController;
    @FXML
    private BorderPane statistics;
    @FXML
    private StatisticsController statisticsController;

    private Stage primaryStage;
    private SimpleStringProperty absoluteFilePath;
    private SimpleBooleanProperty fileLoaded;

    public MainStepperAdminClientController(){
        absoluteFilePath=new SimpleStringProperty("File Not Loaded");
        fileLoaded=new SimpleBooleanProperty(false);
    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null)
            return;
        try {
           // stepperUIManager.LoadStepperFromXmlFile(selectedFile.getAbsolutePath());     servlet command
        }
        catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("File Invalid");
            errorAlert.setContentText(e.getMessage());
            absoluteFilePath.set("File Not Loaded");
            fileLoaded.set(false);
            errorAlert.show();
            return;
        }
        restartUIElements();
        String absolutPath=selectedFile.getAbsolutePath();
        absoluteFilePath.set(absolutPath);
        fileLoaded.set(true);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize(){
        selectedFileLabel.textProperty().bind(absoluteFilePath);
        selectionTabPane.disableProperty().bind(fileLoaded.not());
//        this.executionsHistoryController.setMainStepperController(this);
       this.statisticsController.setMainStepperController(this);
    }

    public void restartUIElements(){
//        this.executionsHistoryController.restartUIElements();
        this.statisticsController.restartUIElements();
    }


    public enum Tabs{
        UsersManagement,
        RolesManagement,
        ExecutionsHistoryTab,
        Statistics
    }
}
