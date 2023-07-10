package MainStage;

import MainStage.Components.Main.MainStepperAdminClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.net.URL;

public class MainStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader=new FXMLLoader();

        URL mainFXML=getClass().getResource("/MainStage/Components/Main/MainStepperAdminClient.fxml");
        loader.setLocation(mainFXML);
        ScrollPane root=loader.load();

        MainStepperAdminClientController mainStepperController=loader.getController();
        mainStepperController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Stepper - Admin Client");
        Scene scene=new Scene(root,1510, 950);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void run(String[] args){
        launch(args);
    }
}
