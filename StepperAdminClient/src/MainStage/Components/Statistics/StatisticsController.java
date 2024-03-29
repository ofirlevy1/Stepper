package MainStage.Components.Statistics;

import Flow.FlowStatistics;
import MainStage.Components.Main.MainStepperAdminClientController;
import MainStage.Components.util.Constants;
import Steps.StepStatistics;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private MainStepperAdminClientController mainStepperController;
    private ObservableList<FlowStatistics> flowStatisticsObservableList;
    private ObservableList<StepStatistics> stepStatisticsObservableList;

    private Timer timer;
    private TimerTask statisticsRefresher;
    private BooleanProperty autoUpdate;

    @FXML
    public void initialize(){
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        flowAverageRunTimeColumn.setCellValueFactory(new PropertyValueFactory<>("AvgDuration"));
        flowExecutionCounterColumn.setCellValueFactory(new PropertyValueFactory<>("startUpCount"));
        stepNameColumn.setCellValueFactory(new PropertyValueFactory<>("stepName"));
        stepExecutionCounterColumn.setCellValueFactory(new PropertyValueFactory<>("startUpCount"));
        stepAverageRunTimeColumn.setCellValueFactory(new PropertyValueFactory<>("AvgDuration"));
        autoUpdate=new SimpleBooleanProperty();
        flowStatisticsObservableList= FXCollections.observableArrayList();
        stepStatisticsObservableList= FXCollections.observableArrayList();
        this.autoUpdate=new SimpleBooleanProperty(true);
    }

    public void setMainStepperController(MainStepperAdminClientController mainStepperController){
        this.mainStepperController=mainStepperController;
    }

    public void restartUIElements(){
        flowStatisticsObservableList.clear();
        stepStatisticsObservableList.clear();
    }

    private void updateStepStatisticsTable(List<StepStatistics> stepStatistics){
        Platform.runLater(()->{
            stepStatisticsObservableList.clear();
            stepStatisticsObservableList.addAll(stepStatistics);
            stepTableView.setItems(stepStatisticsObservableList);
        });
    }

    private void updateFlowStatisticsTable(List<FlowStatistics> flowStatistics){
        Platform.runLater(()->{
            flowStatisticsObservableList.clear();
            flowStatisticsObservableList.addAll(flowStatistics);
            flowTableView.setItems(flowStatisticsObservableList);
        });
    }

    public void startStatisticsRefresher(){
        statisticsRefresher=new StatisticsTableRefresher(
                autoUpdate,
                this::updateFlowStatisticsTable,
                this::updateStepStatisticsTable);
        timer=new Timer();
        timer.schedule(statisticsRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

}
