package MainStage.Components.FlowsExecution;

import Flow.Flow;
import Flow.FreeInputDescriptor;
import MainStage.Components.FlowsExecution.SubComponents.InputGUI.InputGUIController;
import MainStage.Components.Main.MainStepperController;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import RunHistory.FlowRunHistory;
import RunHistory.FreeInputHistory;
import RunHistory.OutputHistory;
import RunHistory.StepHistory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static MainStage.Components.util.Constants.GSON_INSTANCE;

public class FlowsExecutionController {

    @FXML
    private FlowPane flowInputsFlowPane;
    @FXML
    private FlowPane flowDetailsFlowPane;
    @FXML
    private FlowPane executionDetailsFlowPane;
    @FXML
    private FlowPane continuationDataFlowPane;
    @FXML
    private Button startFlowExecutionButton;
    @FXML
    private Label flowProgressionLabel;


    private MainStepperController mainStepperController;
    private HashMap<String, InputGUIController> inputGUIControllers;
    private Thread statusThread;

    private SimpleBooleanProperty allMandatoryInputsFilled;
    private SimpleStringProperty selectedFlow;
    private SimpleStringProperty flowID;
    private SimpleStringProperty flowProgression;
    private SimpleStringProperty flowExecutionEndMessage;

    private Timer timer;
    private TimerTask flowExecutionStatusRefresher;
    private BooleanProperty autoUpdate;

    @FXML
    public  void  initialize(){
        flowExecutionEndMessage=new SimpleStringProperty("");
        autoUpdate=new SimpleBooleanProperty(true);
        allMandatoryInputsFilled=new SimpleBooleanProperty(false);
        startFlowExecutionButton.disableProperty().bind(allMandatoryInputsFilled.not());
        selectedFlow=new SimpleStringProperty("");
        flowID=new SimpleStringProperty("");
        flowProgression=new SimpleStringProperty("");
        flowProgressionLabel.textProperty().bind(flowProgression);
        inputGUIControllers =new HashMap<>();
    }

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    @FXML
    void startFlowExecutionAction(ActionEvent event) {
        String finalUrl = HttpUrl
               .parse(Constants.CREATE_FLOW)
               .newBuilder()
               .addQueryParameter("flow_name", selectedFlow.get())
               .build()
               .toString();
        RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), "empty");
        HttpClientUtil.runAsyncPost(finalUrl, emptyBody,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Input Invalid");
                    errorAlert.setContentText("Input: "+e.getMessage());
                    errorAlert.show();
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Error");
                        errorAlert.setContentText("Something went wrong: " + responseBody);
                        errorAlert.show();
                    });
                }
                else {
                    JsonObject jsonObject= JsonParser.parseString(response.body().string()).getAsJsonObject();
                    String flowIDFromJson=jsonObject.get("flow_id").getAsString();
                    flowID.set(flowIDFromJson);
                    Platform.runLater(()->assignInputsToFlow());
                }
            }
        });
    }

    public TimerTask getFlowExecutionStatusRefresher(){
        return flowExecutionStatusRefresher;
    }

    private void assignInputsToFlow(){
        Map<String, Object> httpBody = new HashMap<>();

        for (String inputGUIController : inputGUIControllers.keySet())
            httpBody.put(inputGUIControllers.get(inputGUIController).getInputName(), inputGUIControllers.get(inputGUIController).getInput());
        httpBody.put("flow_id",flowID.get());
        String json = GSON_INSTANCE.toJson(httpBody);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String finalUrl = HttpUrl
                .parse(Constants.SET_INPUTS)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("Input Invalid");
                    errorAlert.setContentText("Input: "+e.getMessage());
                    errorAlert.show();
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Error");
                        errorAlert.setContentText("Something went wrong: " + responseBody);
                        errorAlert.show();
                    });
                }
                else {
                    Platform.runLater(()->runFlow());
                }
            }
        });
    }

    private void runFlow(){
        Map<String,String> map=new HashMap<>();
        map.put("flow_id",flowID.get());
        String json = GSON_INSTANCE.toJson(map);
        startFlowExecutionButton.setText("Rerun Flow");
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        String finalUrl = HttpUrl
                .parse(Constants.RUN_FLOW)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                flowExecutionEndMessage.set("Something went wrong");
                autoUpdate.set(false);
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    flowExecutionEndMessage.set("The Flow has failed!");
                    autoUpdate.set(false);
                }
            }
        });
        autoUpdate.set(true);
        startFlowExecutionStatusRefresher();
    }

    public Stage getPrimaryStage(){
        return mainStepperController.getPrimaryStage();
    }

    public void startFlowExecutionStatusRefresher(){
        flowExecutionStatusRefresher=new FlowExecutionStatusRefresher(
                autoUpdate,
                flowExecutionEndMessage,
                this::checkOnFlow,
                flowID.get());
        timer=new Timer();
        timer.schedule(flowExecutionStatusRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    private void checkOnFlow(Flow.Status flowStatus){
        Platform.runLater(()->{
            if(!autoUpdate.get())
                return;
            if(flowStatus.equals(Flow.Status.FAILURE)) {
                flowExecutionStatusRefresher.cancel();
                autoUpdate.set(false);
                updateStatusLabel("Flow Execution Failed");
                getLatestFlowRunHistory();
                updateContinuationDataFlowPane();
            }
            else if (flowStatus.equals(Flow.Status.SUCCESS)||flowStatus.equals(Flow.Status.WARNING)) {
                flowExecutionStatusRefresher.cancel();
                autoUpdate.set(false);
                updateStatusLabel("Flow Execution Finished");
                getLatestFlowRunHistory();
                updateContinuationDataFlowPane();
            }
            else if(flowStatus.equals(Flow.Status.RUNNING))
                updateFlowExecutionProgression();
        });

    }

    private void updateFlowExecutionProgression(){
        Map<String,String> map=new HashMap<>();
        map.put("flow_id",flowID.get());
        String json = GSON_INSTANCE.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String finalUrl = HttpUrl
                .parse(Constants.COMPLETED_STEPS_COUNT)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JsonObject jsonObject= JsonParser.parseString(response.body().string()).getAsJsonObject();
                String flowProgress =jsonObject.get("completed_Steps").getAsString();
                updateStatusLabel("Number of completed steps is "+flowProgress);
            }
        });

    }
    private void updateStatusLabel(String  str){
        Platform.runLater(()->flowProgression.set(str));
    }
    private void getLatestFlowRunHistory(){
        Map<String,String> map=new HashMap<>();
        map.put("flow_id",flowID.get());
        String json = GSON_INSTANCE.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String finalUrl = HttpUrl
                .parse(Constants.FLOW_HISTORIES)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfFlowRunHistory = response.body().string();
                List<FlowRunHistory> flowRunHistories = Arrays.asList(GSON_INSTANCE.fromJson(jsonArrayOfFlowRunHistory, FlowRunHistory[].class));
                Platform.runLater(()->updateFlowDetailsFlowPane(flowRunHistories.get(flowRunHistories.size()-1)));
            }
        });
    }

    private void updateFlowDetailsFlowPane(FlowRunHistory flowRunHistory){
        flowDetailsFlowPane.getChildren().clear();
        flowDetailsFlowPane.getChildren().add(new Label(flowRunHistory.showGUIFlowHistory()));
        flowDetailsFlowPane.getChildren().add(new Label());
        flowDetailsFlowPane.getChildren().add(new Label("Steps:"));
        for(StepHistory stepHistory:flowRunHistory.getStepHistories()) {
            Hyperlink stepHyperLink=new Hyperlink(stepHistory.getName()+" Status: "+stepHistory.getStatus());
            stepHyperLink.setOnAction(event -> updateExecutionDetailsFlowPane(stepHistory));
            flowDetailsFlowPane.getChildren().add(stepHyperLink);
        }
    }

    private void updateExecutionDetailsFlowPane(StepHistory stepHistory){
        executionDetailsFlowPane.getChildren().clear();
        executionDetailsFlowPane.getChildren().add(new Label("Step name: "+stepHistory.getName()));
        executionDetailsFlowPane.getChildren().add(new Label("Status: "+stepHistory.getStatus()));
        executionDetailsFlowPane.getChildren().add(new Label("Run Time: "+stepHistory.getRunTimeInMs()));
        executionDetailsFlowPane.getChildren().add(new Label());
        if(!stepHistory.getInputs().isEmpty()) {
            executionDetailsFlowPane.getChildren().add(new Label("Inputs:"));
            for (FreeInputHistory input : stepHistory.getInputs())
                executionDetailsFlowPane.getChildren().add(new Label("Name: " + input.getName() + (input.getAlias() != null ? (" Alias: " + input.getAlias()) : "") + " User presentation:\n" + input.getPresentableString()));
        }

        if(!stepHistory.getOutputs().isEmpty()) {
            executionDetailsFlowPane.getChildren().add(new Label("Outputs:"));
            for (OutputHistory output : stepHistory.getOutputs())
                executionDetailsFlowPane.getChildren().add(new Label("Name: " + output.getName() + (output.getAlias() != null ? (" Alias: " + output.getAlias()) : "") + " User presentation:\n" + output.getPresentableString()));
        }
    }

    private void updateContinuationDataFlowPane(){
        Map<String,String> map=new HashMap<>();
        map.put("flow_id",flowID.get());
        String json = GSON_INSTANCE.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

        String finalUrl = HttpUrl
                .parse(Constants.CONTINUATION)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsyncPost(finalUrl, body,new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    String continuationOptionsJson=response.body().string();
                    List<String> continuationOptions=Arrays.asList(GSON_INSTANCE.fromJson(continuationOptionsJson,String[].class));
                    Platform.runLater(()-> {
                        continuationDataFlowPane.getChildren().clear();
                        continuationDataFlowPane.setPrefWrapLength(200);
                        for(String flow:continuationOptions) {
                            Button button = new Button(flow);
                            button.setOnAction(event -> loadFlowContinuation(flow));
                            continuationDataFlowPane.getChildren().add(button);
                            continuationDataFlowPane.setPrefWrapLength(continuationDataFlowPane.getPrefWrapLength() + 150);
                        }
                    });
                }
                else {
                    String body=response.body().string();
                    String body2=body;
                }
            }
        });
        }

    private void loadFlowContinuation(String flowName){

        Map<String,String> map=new HashMap<>();
        map.put("source_flow_id",flowID.get());
        map.put("target_flow_name",flowName);
        String json = GSON_INSTANCE.toJson(map);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        String finalUrl = HttpUrl
                .parse(Constants.CONTINUATION_MAP)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    String continuationMapJson=response.body().string();
                    Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                    HashMap<String,String> dataMap=GSON_INSTANCE.fromJson(continuationMapJson,type);
                    Platform.runLater(()-> loadFlowsExecutionInputsRerun(flowName,dataMap));
                }
            }
        });

    }

    public void loadFlowsExecutionFlowDetails(String flowName){
//        ArrayList<FlowRunHistory> flowRunHistories =mainStepperController.getStepperUIManager().getFlowsRunHistories();
//        List<FlowRunHistory> specifiedFlowRunHistories=flowRunHistories.stream().filter(flowRunHistory -> flowRunHistory.getFlowName().equals(flowName)).collect(Collectors.toList());
//        for(FlowRunHistory flowRunHistory:specifiedFlowRunHistories){
//            flowDetailsFlowPane.getChildren().add(new Label(flowRunHistory.showMinimalFlowHistory()));
//        }
    }

    public void loadFlowsExecutionInputsHttpCall(String flowName, HashMap<String, String> freeInputsMap){
        setUpFlowExecutionGui(flowName);
        if(flowName.isEmpty())
            return;
        flowInputsFlowPane.setPrefWrapLength(0);

        String finalUrl = HttpUrl
                .parse(Constants.FREE_INPUTS_DESCRIPTORS)
                .newBuilder()
                .addQueryParameter("flow_name", flowName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    String freeInputDescriptorsJson=response.body().string();
                    List<FreeInputDescriptor> freeInputDescriptors=Arrays.asList(GSON_INSTANCE.fromJson(freeInputDescriptorsJson,FreeInputDescriptor[].class));
                    Platform.runLater(()-> loadFlowsExecutionInputs(freeInputDescriptors,freeInputsMap));
                }
            }
        });
    }

    private void loadFlowsExecutionInputs(List<FreeInputDescriptor> freeInputDescriptors, HashMap<String, String> freeInputsMap){
        for(FreeInputDescriptor freeInputDescriptor:freeInputDescriptors){
            try {
                FXMLLoader loader = new FXMLLoader();
                if(freeInputDescriptor.getInputEffectiveName().contains("FOLDER")|| freeInputDescriptor.getInputEffectiveName().contains("FILE"))
                    loader.setLocation(getClass().getResource("/MainStage/Components/FlowsExecution/SubComponents/InputGUI/InputGuiFileLoader.fxml"));
                else
                    loader.setLocation(getClass().getResource("/MainStage/Components/FlowsExecution/SubComponents/InputGUI/InputGUI.fxml"));
                GridPane inputGUI = loader.load();

                InputGUIController inputGUIController=loader.getController();
                inputGUIController.setFlowsExecutionController(this);
                inputGUIController.setInputLabel(freeInputDescriptor.getInputEffectiveName());
                inputGUIController.setPromptTextFieldText(freeInputDescriptor.isMandatory()?"Mandatory":"Optional");
                inputGUIController.setMandatory(freeInputDescriptor.isMandatory());
                inputGUIController.getInputTextField().textProperty().addListener((observable, oldValue, newValue)->checkFieldsAreFilled());

                flowInputsFlowPane.setPrefWrapLength(flowInputsFlowPane.getPrefWrapLength()+inputGUI.getPrefWidth()+30.0);
                if(freeInputDescriptor.isMandatory())
                    flowInputsFlowPane.getChildren().add(0,inputGUI);
                else
                    flowInputsFlowPane.getChildren().add(inputGUI);
                inputGUIControllers.put(inputGUIController.getInputName(),inputGUIController);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(freeInputsMap==null)
            return;
        for(String freeInput:freeInputsMap.keySet())
            inputGUIControllers.get(freeInput).setInput(freeInputsMap.get(freeInput));

    }

    private void setUpFlowExecutionGui(String flowName){
        if(statusThread!=null)
            statusThread.interrupt();
        startFlowExecutionButton.setText("Start!");
        allMandatoryInputsFilled.set(false);
        selectedFlow.set(flowName);
        flowInputsFlowPane.getChildren().clear();
        flowDetailsFlowPane.getChildren().clear();
        executionDetailsFlowPane.getChildren().clear();
        continuationDataFlowPane.getChildren().clear();
        inputGUIControllers.clear();
        flowProgression.set("");
    }

    public void checkFieldsAreFilled(){
        allMandatoryInputsFilled.set(inputGUIControllers.keySet()
                .stream()
                .allMatch(inputGUIController -> !inputGUIControllers.get(inputGUIController).isMandatory() || !inputGUIControllers.get(inputGUIController).getInputTextField().getText().isEmpty()));
    }

    public void restartUIElements() {
        flowDetailsFlowPane.getChildren().clear();
        flowInputsFlowPane.getChildren().clear();
        executionDetailsFlowPane.getChildren().clear();
        continuationDataFlowPane.getChildren().clear();
        selectedFlow.set("");
        inputGUIControllers.clear();
        allMandatoryInputsFilled.set(false);
        if(statusThread!=null)
            statusThread.interrupt();
        try {
            if(statusThread!=null)
                statusThread.join(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        flowProgression.set("");
    }

    public void loadFlowsExecutionInputsRerun(String flowName, HashMap<String, String> freeInputsMap) {
        loadFlowsExecutionInputsHttpCall(flowName, freeInputsMap);
    }
}
