package MainStage;

import MainStage.Components.Main.MainStepperController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class MainStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader=new FXMLLoader();

        URL mainFXML=getClass().getResource("/MainStage/Components/Main/MainStepper.fxml");
        loader.setLocation(mainFXML);
        ScrollPane root=loader.load();

        MainStepperController mainStepperController=loader.getController();
        mainStepperController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Stepper");
        Scene scene=new Scene(root,1510, 950);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void run(String[] args){
        launch(args);
    }
}
