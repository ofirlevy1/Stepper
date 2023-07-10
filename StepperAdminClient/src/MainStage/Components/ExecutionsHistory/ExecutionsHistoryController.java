package MainStage.Components.ExecutionsHistory;

import Flow.Flow;
import MainStage.Components.Main.MainStepperAdminClientController;
import RunHistory.FlowRunHistory;
import RunHistory.FreeInputHistory;
import RunHistory.StepHistory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private RadioButton allRadioButton;

    @FXML
    private RadioButton successfullRadioButton;

    @FXML
    private RadioButton warningRadioButton;

    @FXML
    private RadioButton failureRadioButton;

    private MainStepperAdminClientController mainStepperController;
    private boolean filterCheckboxMarked;
    private FlowRunHistory currentlySelectedFlowRunHistory;
    private ObservableList<FlowRunHistory> flowRunHistoryObservableList;
    private ToggleGroup flowStatusToggleGroup;

    @FXML
    public void initialize(){
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        timeStampColumn.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        flowNameColumn.setCellFactory(column -> new TableCellWithHyperlink<>());
        currentlySelectedFlowRunHistory=null;
        flowRunHistoryObservableList=FXCollections.observableArrayList();
        setColumnsComparators();
        flowStatusToggleGroup=new ToggleGroup();
        allRadioButton.setToggleGroup(flowStatusToggleGroup);
        successfullRadioButton.setToggleGroup(flowStatusToggleGroup);
        warningRadioButton.setToggleGroup(flowStatusToggleGroup);
        failureRadioButton.setToggleGroup(flowStatusToggleGroup);
        flowStatusToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioButton rb = (RadioButton)flowStatusToggleGroup.getSelectedToggle();
                if (rb != null)
                    pastExecutionsFilter(rb.getText());
            }
        });

    }

    @FXML
    void rerunFlowButtonAction(ActionEvent event) {
        if(currentlySelectedFlowRunHistory!=null) {
            //  mainStepperController.rerunFlow(currentlySelectedFlowRunHistory.getFlowName(), currentlySelectedFlowRunHistory.getFreeInputsEnteredByUser());
        }
    }

    void pastExecutionsFilter(String str) {
        FilteredList<FlowRunHistory> flowRunHistories=new FilteredList<>(flowRunHistoryObservableList);
        switch (str) {
            case "all":
                flowRunHistories.setPredicate(flowRunHistory -> true);
                pastExecutionsTable.setItems(flowRunHistories);
                break;
            case "successful":
                flowRunHistories.setPredicate(flowRunHistory -> flowRunHistory.getStatus() == Flow.Status.SUCCESS);
                pastExecutionsTable.setItems(flowRunHistories);
                break;
            case "warning":
                flowRunHistories.setPredicate(flowRunHistory -> flowRunHistory.getStatus() == Flow.Status.WARNING);
                pastExecutionsTable.setItems(flowRunHistories);
                break;
            case "failure":
                flowRunHistories.setPredicate(flowRunHistory -> flowRunHistory.getStatus() == Flow.Status.FAILURE);
                pastExecutionsTable.setItems(flowRunHistories);
                break;
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

    public void setMainStepperController(MainStepperAdminClientController mainStepperController) {
        this.mainStepperController = mainStepperController;
    }

    public void updateExecutionsTable(){

//        if(flowRunHistoryObservableList.size()==mainStepperController.getStepperUIManager().getFlowsRunHistories().size())
//            return;
//        flowRunHistoryObservableList.clear();
//        flowRunHistoryObservableList.addAll(mainStepperController.getStepperUIManager().getFlowsRunHistories());
//        pastExecutionsTable.setItems(flowRunHistoryObservableList);
//        filterCheckboxMarked=false;
//        flowStatusToggleGroup.selectToggle(allRadioButton);
    }

    private void updateFlowsDetails(FlowRunHistory flowRunHistory){
        currentlySelectedFlowRunHistory=flowRunHistory;
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
        currentlySelectedFlowRunHistory=null;
        this.flowRunHistoryObservableList.clear();
        this.flowDetailsFlowPane.getChildren().clear();
        this.executionElementsFlowPane.getChildren().clear();
        this.flowStatusToggleGroup.selectToggle(allRadioButton);
    }

    private class TableCellWithHyperlink<T> extends TableCell<T, String> {

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
