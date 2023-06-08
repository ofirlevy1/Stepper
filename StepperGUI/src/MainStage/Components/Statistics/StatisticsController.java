package MainStage.Components.Statistics;

import MainStage.Components.Main.MainStepperController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StatisticsController {

    @FXML
    private TableView<?> flowTableView;
    @FXML
    private TableColumn<?, ?> flowNameColumn;
    @FXML
    private TableColumn<?, ?> flowExecutionCounterColumn;
    @FXML
    private TableColumn<?, ?> flowAverageRunTimeColumn;
    @FXML
    private TableView<?> stepTableView;
    @FXML
    private TableColumn<?, ?> stepNameColumn;
    @FXML
    private TableColumn<?, ?> stepExecutionCounterColumn;
    @FXML
    private TableColumn<?, ?> stepAverageRunTimeColumn;

    private MainStepperController mainStepperController;

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

}
