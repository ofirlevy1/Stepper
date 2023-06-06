package MainStage.Components.ExecutionsHistory;

import Flow.Flow;
import MainStage.Components.Main.MainStepperController;
import RunHistory.FlowRunHistory;
import RunHistory.FreeInputHistory;
import RunHistory.StepHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;

import java.util.Comparator;
import java.util.Objects;

public class ExecutionsHistoryController {

    @FXML
    private TableView<FlowRunHistory> pastExecutionsTable;
    @FXML
    private TableColumn<FlowRunHistory, String> flowNameColumn;
    @FXML
    private TableColumn<FlowRunHistory, String> timeStampColumn;
    @FXML
    private TableColumn<FlowRunHistory, Flow.Status> statusColumn;
    @FXML
    private FlowPane flowDetailsFlowPane;
    @FXML
    private FlowPane executionElementsFlowPane;
    @FXML
    private Button rerunFlowButton;
    @FXML
    private CheckBox successfulExecutionsFilterCheckBox;

    private MainStepperController mainStepperController;
    private boolean filterCheckboxMarked;
    private ObservableList<FlowRunHistory> flowRunHistoryObservableList;

    @FXML
    public void initialize(){
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        timeStampColumn.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        flowNameColumn.setCellFactory(column -> new TableCellWithHyperlink<>());
        filterCheckboxMarked=false;
        flowRunHistoryObservableList=FXCollections.observableArrayList();
        setColumnsComparators();
    }

    @FXML
    void rerunFlowButtonAction(ActionEvent event) {

    }

    @FXML
    void successfulPastExecutionsFilter(ActionEvent event) {
        FilteredList<FlowRunHistory> flowRunHistories=new FilteredList<>(flowRunHistoryObservableList);
        if(filterCheckboxMarked){
            filterCheckboxMarked=false;
            flowRunHistories.setPredicate(flowRunHistory -> true);
            pastExecutionsTable.setItems(flowRunHistories);
        }
        else{
            filterCheckboxMarked=true;
            flowRunHistories.setPredicate(flowRunHistory -> flowRunHistory.getStatus()== Flow.Status.SUCCESS);
            pastExecutionsTable.setItems(flowRunHistories);
        }
    }

    private void setColumnsComparators(){
        Comparator<String> stringColumnComparator = Comparator.comparing(String::toLowerCase);
        Comparator<Flow.Status> statusColumnComparator=
                (Flow.Status st1, Flow.Status st2)->{
            if(st1.equals(st2)) return 0;
            if(Objects.equals(st1, Flow.Status.SUCCESS))
                return 1;
            else if(Objects.equals(st1, Flow.Status.WARNING)){
                if(st2.equals(Flow.Status.SUCCESS))
                    return -1;
                else
                    return 1;
            }
            else
                return -1;
                };
        flowNameColumn.setComparator(stringColumnComparator);
        timeStampColumn.setComparator(stringColumnComparator);
        statusColumn.setComparator(statusColumnComparator);
    }

    public void setMainStepperController(MainStepperController mainStepperController) {
        this.mainStepperController = mainStepperController;
    }

    public void updateExecutionsTable(){
        FlowRunHistory flowRunHistory = mainStepperController.getStepperUIManager().getFlowsRunHistories().get(mainStepperController.getStepperUIManager().getFlowsRunHistories().size()-1);
        //pastExecutionsTable.getItems().add(flowRunHistory);
        flowRunHistoryObservableList.add(flowRunHistory);
        pastExecutionsTable.setItems(flowRunHistoryObservableList);
        filterCheckboxMarked=false;
        successfulExecutionsFilterCheckBox.setSelected(false);
    }

    private void updateFlowsDetails(FlowRunHistory flowRunHistory){
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

    private void updateExecutionDetailsFlowPane(StepHistory stepHistory) {
        executionElementsFlowPane.getChildren().clear();
        executionElementsFlowPane.getChildren().add(new Label("Step name: " + stepHistory.getName()));
        executionElementsFlowPane.getChildren().add(new Label("Status: " + stepHistory.getStatus()));
        executionElementsFlowPane.getChildren().add(new Label("Run Time: " + stepHistory.getRunTimeInMs()));
        executionElementsFlowPane.getChildren().add(new Label());
        if (!stepHistory.getInputs().isEmpty()) {
            executionElementsFlowPane.getChildren().add(new Label("Inputs:"));
            for (FreeInputHistory input : stepHistory.getInputs())
                executionElementsFlowPane.getChildren().add(new Label("Name: " + input.getName() + (input.getAlias() != null ? (" Alias: " + input.getAlias()) : "") + " User presentation:\n" + input.getPresentableString()));
        }
    }

    public void restartUIElements() {
        this.filterCheckboxMarked=false;
        this.flowRunHistoryObservableList.clear();
        this.flowDetailsFlowPane.getChildren().clear();
        this.executionElementsFlowPane.getChildren().clear();
        this.successfulExecutionsFilterCheckBox.setSelected(false);
    }

    private class TableCellWithHyperlink<T> extends javafx.scene.control.TableCell<T, String> {

        private final Hyperlink hyperlink;

        public TableCellWithHyperlink() {
            this.hyperlink = new Hyperlink();
            this.hyperlink.setOnAction(event -> updateFlowsDetails((FlowRunHistory)getTableView().getItems().get(getIndex())));
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                hyperlink.setText(item);
                setGraphic(hyperlink);
            }
        }
    }
}
