package MainStage.Components.Statistics;

import Flow.FlowStatistics;
import MainStage.Components.Main.MainStepperController;
import Stepper.StepperUIManager;
import Steps.StepStatistics;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StatisticsController {

    @FXML
    private TableView<FlowStatistics> flowTableView;
    @FXML
    private TableColumn<FlowStatistics, String> flowNameColumn;
    @FXML
    private TableColumn<FlowStatistics, String> flowExecutionCounterColumn;
    @FXML
    private TableColumn<FlowStatistics, String> flowAverageRunTimeColumn;
    @FXML
    private TableView<StepStatistics> stepTableView;
    @FXML
    private TableColumn<StepStatistics, String> stepNameColumn;
    @FXML
    private TableColumn<StepStatistics, String> stepExecutionCounterColumn;
    @FXML
    private TableColumn<StepStatistics, String> stepAverageRunTimeColumn;

    private MainStepperController mainStepperController;
    private ObservableList<FlowStatistics> flowStatisticsObservableList;
    private ObservableList<StepStatistics> stepStatisticsObservableList;

    @FXML
    public void initialize(){
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        flowAverageRunTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startUpCount"));
        flowExecutionCounterColumn.setCellValueFactory(new PropertyValueFactory<>("startUpCount"));
        stepNameColumn.setCellValueFactory(new PropertyValueFactory<>("stepName"));
        stepExecutionCounterColumn.setCellValueFactory(new PropertyValueFactory<>("startUpCount"));
        stepAverageRunTimeColumn.setCellValueFactory(new PropertyValueFactory<>("avgDuration"));
        flowStatisticsObservableList= FXCollections.observableArrayList();
        stepStatisticsObservableList= FXCollections.observableArrayList();
    }

    public void setMainStepperController(MainStepperController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    public void restartUIElements(){
        flowStatisticsObservableList.clear();
        stepStatisticsObservableList.clear();
    }

    public void updateStatisticsTables(){
        flowStatisticsObservableList.clear();
        stepStatisticsObservableList.clear();
        StepperUIManager stepperUIManager=mainStepperController.getStepperUIManager();
        flowStatisticsObservableList.addAll(stepperUIManager.getFlowStatistics());
        stepStatisticsObservableList.addAll(stepperUIManager.getStepsStatistics());
        flowTableView.setItems(flowStatisticsObservableList);
        stepTableView.setItems(stepStatisticsObservableList);
    }

}
