package MainStage.Components.Main;

import MainStage.Components.FlowsDefinition.FlowsDefinitionController;
import Stepper.StepperUIManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

public class MainStepperController {

    @FXML
    private Label filePathLabel;
    @FXML
    private Button loadFileButton;
    @FXML
    private TabPane selectionTabPane;
    @FXML
    private BorderPane flowsDefinition;
    @FXML
    private FlowsDefinitionController flowsDefinitionController;

    private SimpleStringProperty absoluteFilePath;
    private SimpleBooleanProperty fileLoaded;

    private Stage primaryStage;
    private StepperUIManager stepperUIManager;

    public MainStepperController(){
        absoluteFilePath=new SimpleStringProperty("File Not Loaded");
        fileLoaded=new SimpleBooleanProperty(false);
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage=primaryStage;
        stepperUIManager=new StepperUIManager();
    }

    @FXML
    private void initialize(){
        filePathLabel.textProperty().bind(absoluteFilePath);
        selectionTabPane.disableProperty().bind(fileLoaded.not());
        this.flowsDefinitionController.setMainStepperController(this);
    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        try {
            stepperUIManager.LoadStepperFromXmlFile(selectedFile.getAbsolutePath());
        }
        catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("File Invalid");
            errorAlert.setContentText(e.getMessage());
            errorAlert.show();
            return;
        }
        flowsDefinitionController.loadFlowsButtons(stepperUIManager); //need to obtain all the flow descriptors;
        String absolutPath=selectedFile.getAbsolutePath();
        absoluteFilePath.set(absolutPath);
        fileLoaded.set(true);
    }

}
