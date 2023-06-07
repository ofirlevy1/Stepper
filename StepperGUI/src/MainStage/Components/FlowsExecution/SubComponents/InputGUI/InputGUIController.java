package MainStage.Components.FlowsExecution.SubComponents.InputGUI;

import MainStage.Components.FlowsExecution.FlowsExecutionController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Method;

public class InputGUIController {

    @FXML
    private TextField inputTextField;
    @FXML
    private Label inputNameLabel;

    private FlowsExecutionController flowsExecutionController;

    private SimpleStringProperty inputName;
    private int inputIndex;
    private boolean isMandatory;

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
