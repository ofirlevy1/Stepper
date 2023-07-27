package MainStage.Components.FlowsExecution.SubComponents.InputGUI;

import MainStage.Components.FlowsExecution.FlowsExecutionController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class InputGuiFileLoaderController extends InputGUIController{

    @FXML
    private Label inputNameLabel;

    @FXML
    private TextField inputTextField;

    private FlowsExecutionController flowsExecutionController;

    private SimpleStringProperty inputName;
    private int inputIndex;
    private boolean isMandatory;

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        if(inputNameLabel.getText().contains("FOLDER")) {
            DirectoryChooser folderChooser = new DirectoryChooser();
            folderChooser.setTitle("Select Folder");
            File selectedFolder = folderChooser.showDialog(flowsExecutionController.getPrimaryStage());
            if(selectedFolder!=null)
                setInput(selectedFolder.getAbsolutePath());
        }
        else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(" files/folders", "*.*"));
            File path = fileChooser.showOpenDialog(flowsExecutionController.getPrimaryStage());
            if(path!=null)
                setInput(path.getAbsolutePath());
        }
    }

    @FXML
    public void initialize(){
        inputName=new SimpleStringProperty();
        inputNameLabel.textProperty().bind(inputName);
    }

    public void setFlowsExecutionController(FlowsExecutionController flowsExecutionController) {
        this.flowsExecutionController = flowsExecutionController;
    }

    public void setInputLabel(String name){
        inputName.set(name);
    }

    public void setPromptTextFieldText(String text){
        inputTextField.setPromptText(text);
    }

    public int getInputIndex() {
        return inputIndex;
    }

    public void setInputIndex(int inputIndex) {
        this.inputIndex = inputIndex;
    }

    public String getInput(){
        return inputTextField.getText();
    }

    public void setInput(String input){inputTextField.setText(input);}

    public String getInputName(){
        return inputName.get();
    }

    public TextField getInputTextField() {
        return inputTextField;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

}
