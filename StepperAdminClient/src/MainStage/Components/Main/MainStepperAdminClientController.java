package MainStage.Components.Main;

import MainStage.Components.ExecutionsHistory.ExecutionsHistoryController;
import MainStage.Components.RolesManagement.RolesManagementController;
import MainStage.Components.UsersManagement.UsersManagementController;
import MainStage.Components.Statistics.StatisticsController;
import MainStage.Components.util.Constants;
import MainStage.Components.util.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

public class MainStepperAdminClientController {

    @FXML
    private Label adminNameLabel;
    @FXML
    private TextField userNameTextField;
    @FXML
    private Label selectedFileLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button loadFileButton;
    @FXML
    private TabPane selectionTabPane;
    @FXML
    private BorderPane executionsHistory;
    @FXML
    private ExecutionsHistoryController executionsHistoryController;
    @FXML
    private BorderPane statistics;
    @FXML
    private StatisticsController statisticsController;
    @FXML
    private BorderPane usersManagement;
    @FXML
    private UsersManagementController usersManagementController;
    @FXML
    private  BorderPane rolesManagement;
    @FXML
    private RolesManagementController rolesManagementController;

    private Stage primaryStage;
    private SimpleStringProperty absoluteFilePath;
    private SimpleBooleanProperty fileLoaded;
    private SimpleStringProperty userName;
    private String errorMessage;

    public MainStepperAdminClientController(){
        absoluteFilePath=new SimpleStringProperty("File Not Loaded");
        fileLoaded=new SimpleBooleanProperty(false);
        userName=new SimpleStringProperty("");
        errorMessage="";
    }

    @FXML
    void loginButtonAction(ActionEvent event){
        startLogin();
    }

    @FXML
    void loadFileButtonAction(ActionEvent event) {
        FileChooser fileChooser=new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null)
            return;

        loadXmlFile(selectedFile.getAbsolutePath());
    }

    private void loadXmlFile(String path){
        File f = new File(path);
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("file1", f.getName(), RequestBody.create(f, MediaType.parse("application/xml")))
                        .build();
        HttpClientUtil.runAsyncPost(Constants.LOAD_XML_FILE, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                errorMessage="something went wrong";
                Platform.runLater(()->{
                    fileLoaded.set(false);
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setHeaderText("File Invalid");
                    errorAlert.setContentText(errorMessage);
                    absoluteFilePath.set("File Not Loaded");
                    errorAlert.show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(()->{
                        fileLoaded.set(true);
                        restartUIElements();
                        absoluteFilePath.set(path);
                    });
            }

                else {
                    errorMessage="Error"+response.body().string();
                    Platform.runLater(()->{
                        fileLoaded.set(false);
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Something Went Wrong");
                        errorAlert.setContentText(errorMessage);
                        absoluteFilePath.set("File Not Loaded");
                        errorAlert.show();
                    });

                }
            }
        });
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize(){
        selectedFileLabel.textProperty().bind(absoluteFilePath);
        selectionTabPane.disableProperty().bind(fileLoaded.not());
        adminNameLabel.textProperty().bind(userName);
        loadFileButton.setVisible(false);
        selectedFileLabel.setVisible(false);
       this.executionsHistoryController.setMainStepperController(this);
       this.statisticsController.setMainStepperController(this);
       this.usersManagementController.setMainStepperController(this);
       this.rolesManagementController.setMainController(this);
    }

    private  void startLogin(){
        if(userNameTextField.getText().isEmpty()){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText("User Name Is Empty");
            errorAlert.show();
        }
        else{

            String finalUrl = HttpUrl
                    .parse(Constants.LOGIN_PAGE)
                    .newBuilder()
                    .addQueryParameter("username", userNameTextField.getText())
                    .build()
                    .toString();


            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Error");
                        errorAlert.setContentText("Something went wrong: " + e.getMessage());
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
                        Platform.runLater(() -> {
                            userName.set(userNameTextField.getText());
                            loadFileButton.setVisible(true);
                            selectedFileLabel.setVisible(true);
                            loginButton.setVisible(false);
                            userNameTextField.setVisible(false);
                        });
                    }
                }
            });
        }
    }

    public void restartUIElements(){
//        this.executionsHistoryController.restartUIElements();
        this.statisticsController.restartUIElements();
        this.usersManagementController.restartUIElements();
    }


    public enum Tabs{
        UsersManagement,
        RolesManagement,
        ExecutionsHistoryTab,
        Statistics
    }
}
